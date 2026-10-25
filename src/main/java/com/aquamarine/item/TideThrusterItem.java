package com.aquamarine.item;

import com.aquamarine.Aquamarine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

/**
 * Propulsor de Maré (docs/08-fase3.md, seção 4): na mão secundária, a tecla V (configurável) pede
 * um impulso de ~16 blocos em 1,2 s na direção do olhar. O servidor valida tudo (item, água, recarga
 * de 3 s, cargas) e empurra o jogador; cada Bobina Voltaica do inventário vira 6 cargas (+2 por nível
 * de Sobrecarga) quando as cargas acabam.
 */
public class TideThrusterItem extends Item {
	public static final int CHARGES_PER_COIL = 6;
	public static final int OVERCHARGE_PER_LEVEL = 2;
	public static final int BOOST_TICKS = 24;
	/** 16 blocos em 24 ticks. */
	public static final double BOOST_SPEED = 16.0 / BOOST_TICKS;
	public static final int COOLDOWN_TICKS = 3 * 20;
	public static final ResourceKey<Enchantment> OVERCHARGE = ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("overcharge"));

	public enum Result { OK, NO_THRUSTER, DRY, COOLDOWN, NO_CHARGE }

	/** Ticks de impulso restantes por jogador (só servidor). */
	private static final Map<UUID, Integer> BOOSTING = new HashMap<>();

	/** Pedido do cliente ao soltar a tecla; sem dados: o servidor usa o olhar atual do jogador. */
	public record FirePayload() implements CustomPacketPayload {
		public static final Type<FirePayload> TYPE = new Type<>(Aquamarine.id("thruster_fire"));
		public static final StreamCodec<RegistryFriendlyByteBuf, FirePayload> CODEC = StreamCodec.unit(new FirePayload());

		@Override
		public Type<FirePayload> type() {
			return TYPE;
		}
	}

	public TideThrusterItem(Properties properties) {
		super(properties.stacksTo(1).rarity(Rarity.RARE).enchantable(10).component(ModComponents.THRUSTER_CHARGES, 0));
	}

	public static void register() {
		PayloadTypeRegistry.serverboundPlay().register(FirePayload.TYPE, FirePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(FirePayload.TYPE, (payload, context) -> {
			Result result = fire(context.player());
			if (result != Result.OK && result != Result.COOLDOWN) {
				context.player().sendOverlayMessage(Component.translatable("message.aquamarine.thruster." + result.name().toLowerCase(java.util.Locale.ROOT)));
			}
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (BOOSTING.isEmpty()) {
				return;
			}
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tickBoost(player);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> BOOSTING.remove(handler.getPlayer().getUUID()));
	}

	public static int charges(ItemStack stack) {
		return stack.getOrDefault(ModComponents.THRUSTER_CHARGES, 0);
	}

	/** Cargas que uma Bobina dá neste Propulsor (6 + 2 por nível de Sobrecarga). */
	public static int chargesPerCoil(ServerLevel level, ItemStack stack) {
		int overcharge = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(OVERCHARGE)
			.map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, stack)).orElse(0);
		return CHARGES_PER_COIL + OVERCHARGE_PER_LEVEL * overcharge;
	}

	public static boolean isBoosting(ServerPlayer player) {
		return BOOSTING.containsKey(player.getUUID());
	}

	/** Valida e dispara o impulso (servidor). Só funciona na água ou na superfície. */
	public static Result fire(ServerPlayer player) {
		ItemStack stack = player.getOffhandItem();
		if (!(stack.getItem() instanceof TideThrusterItem)) {
			return Result.NO_THRUSTER;
		}
		if (!player.isInWater()) {
			return Result.DRY;
		}
		if (player.getCooldowns().isOnCooldown(stack) || isBoosting(player)) {
			return Result.COOLDOWN;
		}
		ServerLevel level = player.level();
		int charges = charges(stack);
		if (charges <= 0) {
			if (!consumeCoil(player.getInventory()) && !player.getAbilities().instabuild) {
				return Result.NO_CHARGE;
			}
			charges = chargesPerCoil(level, stack);
		}
		stack.set(ModComponents.THRUSTER_CHARGES, charges - 1);
		player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
		BOOSTING.put(player.getUUID(), BOOST_TICKS);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundSource.PLAYERS, 1.0F, 0.7F);
		return Result.OK;
	}

	private static boolean consumeCoil(Inventory inventory) {
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.is(ModItems.VOLTAIC_COIL)) {
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}

	/** Empurra na direção do olhar enquanto o impulso dura, com rastro de bolhas e luz verde-água. */
	private static void tickBoost(ServerPlayer player) {
		Integer left = BOOSTING.get(player.getUUID());
		if (left == null) {
			return;
		}
		if (left <= 0 || !player.isAlive()) {
			BOOSTING.remove(player.getUUID());
			return;
		}
		BOOSTING.put(player.getUUID(), left - 1);
		Vec3 look = player.getLookAngle();
		player.setDeltaMovement(look.scale(BOOST_SPEED));
		player.connection.send(new ClientboundSetEntityMotionPacket(player));
		Vec3 back = player.position().add(0, 0.6, 0).subtract(look.scale(0.8));
		player.level().sendParticles(ParticleTypes.BUBBLE, back.x, back.y, back.z, 6, 0.2, 0.2, 0.2, 0.05);
		player.level().sendParticles(ParticleTypes.GLOW, back.x, back.y, back.z, 2, 0.15, 0.15, 0.15, 0.0);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		builder.accept(Component.translatable("tooltip.aquamarine.tide_thruster.charges", charges(stack)).withStyle(ChatFormatting.AQUA));
	}
}
