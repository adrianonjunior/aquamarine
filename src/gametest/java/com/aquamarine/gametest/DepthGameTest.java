package com.aquamarine.gametest;

import com.aquamarine.client.pressure.PressureHud;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.item.DiveGear;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Teste da faixa 3A (Pressão por profundidade): a fonte ambiental sobe com a profundidade, vale o
 * maior nível entre as fontes (não a soma), o Traje e o Regulador a reduzem e a Reserva de Ar segura
 * o afogamento. Chamado por {@link AquamarineClientGameTest}; 1 screenshot.
 */
final class DepthGameTest {
	private static final int SIZE = 21;

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(0, 0, 300);
		int top = o.getY() + SIZE - 2;
		// O mundo plano não tem mar: o teste põe o nível do mar 75 blocos acima de onde o jogador fica.
		PressureManager.setTestSeaLevel(o.getY() + 8 + 75);
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
			ServerPlayer player = player(s);
			player.removeAllEffects();
			clearGear(player);
			player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.DEPTH_GAUGE));
			PressureManager.reset(player);
		});
		tp(server, o.getX() + 0.5, o.getY() + 8, o.getZ() + 0.5, 270, 0);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		// Fundo do oceano: bem mais de 70 blocos abaixo do nível do mar, então o teto ambiental é 3.
		check(server.computeOnServer(s -> PressureManager.depth(player(s)) >= 70.0), "Profundidade: a sala de teste devia estar a 70 m ou mais");
		check(level(server) == 0, "Profundidade: deve começar em 0");

		// Níveis por tempo (10 s cada), sem Kraken por perto; nunca passa de 3.
		server.runOnServer(s -> player(s).addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WATER_BREATHING, 20000)));
		context.waitTicks(215);
		check(level(server) == 1, "Profundidade: nível 1 após ~10 s, era " + level(server));
		context.waitTicks(400);
		check(level(server) == 3, "Profundidade: nível 3 após ~30 s, era " + level(server));
		context.waitTicks(300);
		check(level(server) == 3, "Profundidade: o ambiente sozinho não passa de 3, era " + level(server));
		check(context.computeOnClient(mc -> PressureHud.level()) == 3, "Profundidade: o cliente não recebeu o nível 3");
		shot(context, "27_medidor_profundidade_nivel3");

		// Fontes: com o Kraken perto, vale o maior (4), nunca a soma.
		server.runOnServer(s -> {
			KrakenEntity kraken = KrakenSummoning.spawnAt(s.overworld(), o.offset(8, 8, 8), o);
			check(kraken != null, "Profundidade: o Kraken não surgiu");
			kraken.attacks().setEnabled(false);
		});
		context.waitTicks(260);
		check(level(server) == 3, "Fontes: ambiental 3 e Kraken em 1 vale 3, era " + level(server));
		context.waitTicks(700);
		check(level(server) == 4, "Fontes: o Kraken leva ao 4 (max, não soma), era " + level(server));
		server.runCommand("kill @e[type=aquamarine:kraken]");
		server.runOnServer(s -> player(s).setHealth(20.0F));
		context.waitTicks(260);
		check(level(server) == 3, "Fontes: sem o Kraken volta ao ambiental 3, era " + level(server));

		// Traje completo: a Pressão ambiental cai 2 níveis (alvo 1).
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.DEEP_DIVING_HELMET));
			player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.DEEP_DIVING_CHESTPLATE));
			player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.DEEP_DIVING_LEGGINGS));
			player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.DEEP_DIVING_BOOTS));
			check(DiveGear.ambientReduction(player) == 2, "Traje: conjunto completo devia tirar 2 níveis");
			PressureManager.reset(player);
		});
		context.waitTicks(500);
		check(level(server) == 1, "Traje: com o conjunto o ambiente fica em 1, era " + level(server));

		// Duas peças: -1 nível (alvo 2).
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
			player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
			check(DiveGear.ambientReduction(player) == 1, "Traje: 2 peças devia tirar 1 nível");
		});
		context.waitTicks(500);
		check(level(server) == 2, "Traje: com 2 peças o ambiente fica em 2, era " + level(server));

		// Reserva de Ar: sem fôlego, a Reserva segura o afogamento.
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			clearGear(player);
			player.removeAllEffects();
			player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.DEEP_DIVING_HELMET));
			DiveGear.setAirReserve(player, DiveGear.AIR_PER_PIECE);
			player.setAirSupply(-5);
			player.setHealth(20.0F);
		});
		context.waitTicks(100);
		check(server.computeOnServer(s -> DiveGear.airReserve(player(s)) < DiveGear.AIR_PER_PIECE - 50), "Reserva de Ar: devia ter gastado ~100 ticks");
		check(server.computeOnServer(s -> player(s).getHealth() == 20.0F), "Reserva de Ar: não devia haver dano de afogamento");

		// Regulador na mão secundária (sem o Traje): ambiente 3 vira 1 e gasta durabilidade.
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			clearGear(player);
			player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WATER_BREATHING, 20000));
			player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ModItems.PRESSURE_REGULATOR));
			PressureManager.reset(player);
		});
		context.waitTicks(500);
		check(level(server) == 1, "Regulador: o ambiente fica em 1, era " + level(server));
		check(server.computeOnServer(s -> player(s).getOffhandItem().getDamageValue() >= 1), "Regulador: devia gastar durabilidade");

		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			clearGear(player);
			player.removeAllEffects();
			PressureManager.reset(player);
		});
		PressureManager.setTestSeaLevel(null);
		context.waitTicks(5);
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
	}

	private static void clearGear(ServerPlayer player) {
		for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
			player.setItemSlot(slot, ItemStack.EMPTY);
		}
		player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
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

	private DepthGameTest() {}
}
