package com.aquamarine.item;

import com.aquamarine.Aquamarine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * Equipamento de mergulho da Fase 3A (docs/08-fase3.md, seções 1 e 2): Regulador de Pressão,
 * Medidor de Profundidade e Traje de Mergulho Profundo com a Reserva de Ar. Tudo no servidor.
 */
public final class DiveGear {
	public static final int REGULATOR_DURABILITY = 600;
	/** Níveis ambientais que o Regulador tira. */
	public static final int REGULATOR_LEVELS = 2;
	/** Ticks ativo para gastar 1 de durabilidade do Regulador (10 s). */
	public static final int REGULATOR_WEAR_TICKS = 200;
	/** Fôlego extra por peça do Traje: 45 s (180 s com o conjunto). */
	public static final int AIR_PER_PIECE = 45 * 20;
	/** Ticks de Reserva de Ar recuperados por tick fora da água. */
	private static final int REFILL_PER_TICK = 6;

	public static final ResourceKey<Enchantment> ACCLIMATION = ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("acclimation"));

	private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	private static final Map<UUID, Integer> RESERVE = new HashMap<>();
	private static final Map<UUID, Integer> REGULATOR_WEAR = new HashMap<>();

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tickAirReserve(player);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			RESERVE.remove(handler.getPlayer().getUUID());
			REGULATOR_WEAR.remove(handler.getPlayer().getUUID());
		});
	}

	/** Peças do Traje de Mergulho Profundo vestidas (0 a 4). */
	public static int suitPieces(LivingEntity entity) {
		int pieces = 0;
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			Item item = entity.getItemBySlot(slot).getItem();
			if (item == ModItems.DEEP_DIVING_HELMET || item == ModItems.DEEP_DIVING_CHESTPLATE
				|| item == ModItems.DEEP_DIVING_LEGGINGS || item == ModItems.DEEP_DIVING_BOOTS) {
				pieces++;
			}
		}
		return pieces;
	}

	/** Níveis ambientais que o Traje tira: 1 com 2 peças ou mais, 2 com o conjunto. */
	public static int suitPressureLevels(int pieces) {
		return pieces >= 4 ? 2 : pieces >= 2 ? 1 : 0;
	}

	public static boolean hasRegulator(LivingEntity entity) {
		return entity.getOffhandItem().is(ModItems.PRESSURE_REGULATOR);
	}

	/** Níveis ambientais a menos pelas contramedidas deste equipamento (Regulador e Traje). */
	public static int ambientReduction(LivingEntity entity) {
		return (hasRegulator(entity) ? REGULATOR_LEVELS : 0) + suitPressureLevels(suitPieces(entity)) + acclimationLevels(entity);
	}

	/**
	 * Aclimatação: −1 nível de Pressão ambiental por nível, somando as peças de armadura. Vale só para
	 * a profundidade, para não esvaziar a Pressão da luta (docs/00-decisoes.md).
	 */
	public static int acclimationLevels(LivingEntity entity) {
		return entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(ACCLIMATION).map(holder -> {
			int levels = 0;
			for (EquipmentSlot slot : ARMOR_SLOTS) {
				levels += EnchantmentHelper.getItemEnchantmentLevel(holder, entity.getItemBySlot(slot));
			}
			return levels;
		}).orElse(0);
	}

	/** Gasta a durabilidade do Regulador enquanto ele reduz uma Pressão ambiental real. */
	public static void wearRegulator(ServerPlayer player, boolean working) {
		if (!working || !hasRegulator(player)) {
			REGULATOR_WEAR.remove(player.getUUID());
			return;
		}
		int ticks = REGULATOR_WEAR.merge(player.getUUID(), 1, Integer::sum);
		if (ticks >= REGULATOR_WEAR_TICKS) {
			REGULATOR_WEAR.put(player.getUUID(), 0);
			ItemStack stack = player.getOffhandItem();
			stack.hurtAndBreak(1, player, InteractionHand.OFF_HAND);
		}
	}

	public static int maxAirReserve(LivingEntity entity) {
		return suitPieces(entity) * AIR_PER_PIECE;
	}

	/** Reserva de Ar restante, em ticks (0 sem o Traje). */
	public static int airReserve(Player player) {
		int max = maxAirReserve(player);
		return Math.min(max, RESERVE.getOrDefault(player.getUUID(), max));
	}

	/** Define a Reserva de Ar (só para testes). */
	public static void setAirReserve(Player player, int ticks) {
		RESERVE.put(player.getUUID(), ticks);
	}

	/**
	 * Ar extra consumido depois do ar normal: quando o fôlego acaba, a Reserva segura o jogador em
	 * 0 em vez de deixá-lo se afogar; fora da água ela se recarrega.
	 */
	private static void tickAirReserve(ServerPlayer player) {
		int max = maxAirReserve(player);
		if (max == 0) {
			RESERVE.remove(player.getUUID());
			return;
		}
		int current = Math.min(max, RESERVE.getOrDefault(player.getUUID(), max));
		if (player.isUnderWater()) {
			if (player.getAirSupply() < 0 && current > 0) {
				player.setAirSupply(0);
				current--;
			}
		} else if (player.getAirSupply() >= player.getMaxAirSupply()) {
			current = Math.min(max, current + REFILL_PER_TICK);
		}
		RESERVE.put(player.getUUID(), current);
	}

	private DiveGear() {}
}
