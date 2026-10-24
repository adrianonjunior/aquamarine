package com.aquamarine.gametest;

import com.aquamarine.block.AnchorBuoyBlock;
import com.aquamarine.block.ModBlocks;
import com.aquamarine.client.pressure.PressureHud;
import com.aquamarine.effect.ModEffects;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.item.ModItems;
import com.aquamarine.pressure.PressureManager;

import java.util.Locale;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * Teste da faixa 2B (Pressão): níveis 0 a 4 por tempo perto do Kraken, efeitos de cada nível e
 * contramedidas (armadura de Oricálcio, Tônico, Boia de Âncora). Chamado por
 * {@link AquamarineClientGameTest}; 2 screenshots.
 */
final class PressureGameTest {
	private static final int SIZE = 21;

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(400, 0, 0);
		int top = o.getY() + SIZE - 2; // y da superfície da água
		server.runCommand("gamemode survival @a");
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 16, o.getZ() - 16, o.getX() + 16, o.getZ() + 16));
		context.waitTicks(20);
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow", o.getX() - 11, o.getY() - 1, o.getZ() - 11,
			o.getX() + 11, o.getY() + SIZE, o.getZ() + 11));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", o.getX() - 10, o.getY(), o.getZ() - 10,
			o.getX() + 10, top, o.getZ() + 10));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:air", o.getX() - 10, top + 1, o.getZ() - 10,
			o.getX() + 10, o.getY() + SIZE, o.getZ() + 10));
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			KrakenEntity kraken = KrakenSummoning.spawnAt(level, o.offset(8, 8, 8), o);
			check(kraken != null, "Pressão: o Kraken não surgiu");
			kraken.attacks().setEnabled(false);
			ServerPlayer player = player(s);
			player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20000));
			clearArmor(player);
		});
		tp(server, o.getX() + 0.5, o.getY() + 8, o.getZ() + 0.5, 270, 0);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		check(level(server) == 0, "Pressão: deve começar em 0");

		// Níveis por tempo (10 s cada) e efeitos de cada nível.
		context.waitTicks(215);
		check(level(server) == 1, "Pressão: nível 1 após ~10 s, era " + level(server) + " " + server.computeOnServer(s -> player(s).position() + " water=" + player(s).isUnderWater() + " krakens=" + s.overworld().getEntities(com.aquamarine.entity.ModEntities.KRAKEN, e -> true).stream().map(k -> k.position() + "/" + k.isAlive()).toList() + " hp=" + player(s).getHealth()));
		context.waitTicks(200);
		check(level(server) == 2, "Pressão: nível 2 após ~20 s, era " + level(server));
		check(server.computeOnServer(s -> player(s).getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY) < 0.1), "Pressão 2: nado lento");
		context.waitTicks(200);
		check(level(server) == 3, "Pressão: nível 3 após ~30 s, era " + level(server));
		check(server.computeOnServer(s -> player(s).getAttributeValue(Attributes.ATTACK_SPEED) < 3.0), "Pressão 3: velocidade de ataque menor");
		context.waitTicks(200);
		check(level(server) == 4, "Pressão: nível 4 após ~40 s, era " + level(server));
		check(context.computeOnClient(mc -> PressureHud.level()) == 4, "Pressão: o cliente não recebeu o nível 4");
		server.runOnServer(s -> player(s).setHealth(20.0F));
		context.waitTicks(100);
		check(server.computeOnServer(s -> player(s).getHealth() < 20.0F), "Pressão 4: devia causar dano");
		shot(context, "16_pressao_nivel4");

		// Boia de Âncora: plataforma 3x3, zera a Pressão, recarga e consumo.
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.ANCHOR_BUOY, 2));
			player.snapTo(o.getX() + 0.5, top + 2.0, o.getZ() + 0.5);
			player.setXRot(90.0F);
			var result = player.gameMode.useItem(player, s.overworld(), player.getMainHandItem(), InteractionHand.MAIN_HAND);
			var eye = player.getEyePosition();
			var clip = s.overworld().clip(new net.minecraft.world.level.ClipContext(eye, eye.add(0, -4.5, 0),
				net.minecraft.world.level.ClipContext.Block.OUTLINE, net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY, player));
			check(result.consumesAction(), "Boia: não formou a plataforma (" + result + ", " + clip.getType() + " " + clip.getBlockPos()
				+ ", y=" + player.getY() + ", pitch=" + player.getXRot() + ", topY=" + top + ")");
			check(player.getCooldowns().isOnCooldown(new ItemStack(ModItems.ANCHOR_BUOY)), "Boia: recarga de 30 s não começou");
			check(player.getMainHandItem().getCount() == 1, "Boia: o item não foi consumido");
			ServerLevel level = s.overworld();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					check(level.getBlockState(new BlockPos(o.getX() + dx, top, o.getZ() + dz)).is(ModBlocks.ANCHOR_BUOY), "Boia: bloco " + dx + "," + dz);
				}
			}
			player.snapTo(o.getX() + 0.5, top + 1.0, o.getZ() + 0.5);
		});
		context.waitTicks(40);
		check(level(server) == 0, "Boia: pisar devia zerar a Pressão, era " + level(server));
		check(context.computeOnClient(mc -> PressureHud.level()) == 0, "Boia: o cliente devia receber o nível 0");
		shot(context, "17_pressao_boia");
		// o Kraken destrói a plataforma
		server.runOnServer(s -> AnchorBuoyBlock.collapse(s.overworld(), new BlockPos(o.getX(), top, o.getZ())));
		check(server.computeOnServer(s -> s.overworld().getBlockState(new BlockPos(o.getX(), top, o.getZ())).is(Blocks.WATER)),
			"Boia: collapse devia devolver a água");

		// Armadura completa: ganho 60% menor (200 ticks viram 500).
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.ORICHALCUM_HELMET));
			player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.ORICHALCUM_CHESTPLATE));
			player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.ORICHALCUM_LEGGINGS));
			player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.ORICHALCUM_BOOTS));
			player.snapTo(o.getX() + 0.5, o.getY() + 8, o.getZ() + 0.5);
			PressureManager.reset(player);
		});
		context.waitTicks(300);
		check(level(server) == 0, "Armadura: após 15 s a Pressão devia seguir em 0, era " + level(server));
		context.waitTicks(250);
		check(level(server) == 1, "Armadura: após ~27 s devia chegar ao nível 1, era " + level(server));

		// Tônico: beber dá o efeito, devolve o frasco e limita o máximo em 2.
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			clearArmor(player);
			ItemStack left = new ItemStack(ModItems.PRESSURE_TONIC).finishUsingItem(s.overworld(), player);
			check(ModEffects.hasTonic(player), "Tônico: beber devia dar o efeito");
			check(left.is(Items.GLASS_BOTTLE), "Tônico: devia devolver o frasco");
			PressureManager.reset(player);
		});
		context.waitTicks(900);
		check(level(server) == 2, "Tônico: o máximo devia ser 2, era " + level(server));
		server.runCommand("kill @e[type=aquamarine:kraken]");
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.removeAllEffects();
			PressureManager.reset(player);
		});
		context.waitTicks(5);
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
	}

	private static void clearArmor(ServerPlayer player) {
		for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
			player.setItemSlot(slot, ItemStack.EMPTY);
		}
	}

	private static int level(TestServerContext server) {
		return server.computeOnServer(s -> PressureManager.level(player(s)));
	}

	private static ServerPlayer player(MinecraftServer server) {
		return server.getPlayerList().getPlayers().getFirst();
	}

	private static void shot(ClientGameTestContext context, String name) {
		context.runOnClient(mc -> mc.gui.toastManager().clear());
		context.waitTick();
		context.takeScreenshot(name);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void tp(TestServerContext server, double x, double y, double z, float yaw, float pitch) {
		server.runCommand(String.format(Locale.ROOT, "tp @p %.2f %.2f %.2f %.1f %.1f", x, y, z, yaw, pitch));
	}

	private PressureGameTest() {}
}
