package com.aquamarine.gametest;

import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.SpermWhaleEntity;
import com.aquamarine.entity.WhaleEntity;
import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.item.ModItems;

import com.mojang.authlib.GameProfile;

import java.util.Locale;
import java.util.UUID;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.entity.FakePlayer;

import net.minecraft.client.CameraType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Teste da faixa 3B-W (baleias): domar com peixe, selar, montar com 3 (só o dono pilota, quarto
 * passageiro recusado), Respiração Aquática para quem monta, fuga do Kraken e a onda sonora do
 * Cachalote quando atacado. Chamado por {@link AquamarineClientGameTest}; 2 screenshots.
 */
final class WhaleGameTest {
	private static final int HALF = 22;
	private static final int HEIGHT = 22;

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(0, 0, -300);
		int top = o.getY() + HEIGHT - 3;
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 32, o.getZ() - 32, o.getX() + 32, o.getZ() + 32));
		context.waitTicks(20);
		// O /fill aceita no máximo 32.768 blocos: a arena grande vai em camadas.
		fill(server, o.getX() - HALF - 1, o.getY() - 1, o.getZ() - HALF - 1, o.getX() + HALF + 1, o.getY() + HEIGHT, o.getZ() + HALF + 1, "glass");
		fill(server, o.getX() - HALF, o.getY(), o.getZ() - HALF, o.getX() + HALF, top, o.getZ() + HALF, "water");
		fill(server, o.getX() - HALF, top + 1, o.getZ() - HALF, o.getX() + HALF, o.getY() + HEIGHT - 1, o.getZ() + HALF, "air");
		server.runCommand("gamemode survival @a");
		server.runCommand("effect give @a minecraft:night_vision 600 0 true");

		server.runOnServer(s -> {
			WhaleEntity whale = ModEntities.WHALE.create(s.overworld(), EntitySpawnReason.COMMAND);
			whale.snapTo(o.getX() + 0.5, o.getY() + 6, o.getZ() + 0.5, 0.0F, 0.0F);
			whale.setPersistenceRequired();
			s.overworld().addFreshEntity(whale);
		});
		tp(server, o.getX() + 0.5, o.getY() + 8, o.getZ() - 6.5, 0, 10);
		context.waitTicks(10);

		// Domar: 4 a 6 peixes crus de quem alimenta.
		server.runOnServer(s -> {
			ServerPlayer p = player(s);
			WhaleEntity whale = whale(s);
			p.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COD, 8));
			for (int i = 0; i < WhaleEntity.MAX_FOOD && !whale.isTamed(); i++) {
				p.interactOn(whale, InteractionHand.MAIN_HAND, whale.position());
			}
			check(whale.isTamed(), "Baleia: devia estar domada com até 6 peixes");
			check(whale.fed() >= WhaleEntity.MIN_FOOD, "Baleia: domou com menos de 4 peixes (" + whale.fed() + ")");
			check(p.getUUID().equals(whale.owner()), "Baleia: o dono devia ser quem alimentou");
			// Sem sela não monta; com a sela, monta e pilota.
			p.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
			p.interactOn(whale, InteractionHand.MAIN_HAND, whale.position());
			check(!p.isPassenger(), "Baleia: sem sela não devia montar");
			p.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.WHALE_SADDLE));
			p.interactOn(whale, InteractionHand.MAIN_HAND, whale.position());
			check(whale.saddled() && p.getMainHandItem().isEmpty(), "Baleia: a Sela devia ficar na baleia");
			p.interactOn(whale, InteractionHand.MAIN_HAND, whale.position());
			check(p.getVehicle() == whale, "Baleia: o dono devia montar");
			check(whale.getControllingPassenger() == p, "Baleia: o dono devia pilotar");
		});

		// Passageiros: mais 2 cabem e o quarto é recusado (porcos no lugar de jogadores: o FakePlayer da
		// Fabric não monta); um jogador que não é o dono não pilota.
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			WhaleEntity whale = whale(s);
			Pig[] riders = new Pig[3];
			for (int i = 0; i < riders.length; i++) {
				riders[i] = EntityTypes.PIG.create(level, EntitySpawnReason.COMMAND);
				riders[i].snapTo(whale.getX(), whale.getY() + 2, whale.getZ(), 0, 0);
				level.addFreshEntity(riders[i]);
			}
			check(riders[0].startRiding(whale) && riders[1].startRiding(whale), "Baleia: devia levar 2 passageiros além do piloto");
			check(!riders[2].startRiding(whale), "Baleia: o quarto passageiro não devia caber");
			check(whale.getPassengers().size() == WhaleEntity.MAX_RIDERS, "Baleia: devia levar 3");
			check(whale.getControllingPassenger() == player(s), "Baleia: com passageiros, o piloto segue sendo o dono");
			check(!whale.canPilot(fake(level, "Passageiro2")), "Baleia: quem não é dono não devia pilotar");
			riders[2].discard();
		});
		context.waitTicks(25);
		check(server.computeOnServer(s -> player(s).hasEffect(MobEffects.WATER_BREATHING)), "Baleia: quem monta devia respirar debaixo d'água");

		context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK));
		world.getConnection().waitForChunksRender();
		context.waitTicks(10);
		shot(context, "32_baleia_montada_3_jogadores");
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.FIRST_PERSON));

		server.runOnServer(s -> {
			WhaleEntity whale = whale(s);
			player(s).stopRiding();
			check(whale.getControllingPassenger() == null, "Baleia: sem o dono, ninguém pilota");
			whale.getPassengers().forEach(p -> p.discard());
			whale.ejectPassengers();
		});

		// Fuga do Kraken: a baleia solta se afasta.
		double before = server.computeOnServer(s -> {
			KrakenEntity kraken = KrakenSummoning.spawnAt(s.overworld(), o.offset(-14, 2, 0), o);
			check(kraken != null, "Baleia: o Kraken de teste não surgiu");
			kraken.attacks().setEnabled(false);
			return whale(s).distanceTo(kraken);
		});
		context.waitTicks(60);
		double after = server.computeOnServer(s -> whale(s).distanceTo(s.overworld().getEntities(ModEntities.KRAKEN, k -> true).getFirst()));
		check(after > before + 1.0, String.format(Locale.ROOT, "Baleia: devia fugir do Kraken (%.1f -> %.1f)", before, after));
		server.runCommand("kill @e[type=aquamarine:kraken]");
		server.runCommand("kill @e[type=aquamarine:whale]");

		// Cachalote: neutro; atacado, vira e solta a onda sonora (6 de dano no cone de 8 blocos).
		server.runOnServer(s -> {
			SpermWhaleEntity sperm = ModEntities.SPERM_WHALE.create(s.overworld(), EntitySpawnReason.COMMAND);
			sperm.snapTo(o.getX() + 0.5, o.getY() + 6, o.getZ() + 0.5, 180.0F, 0.0F);
			sperm.setPersistenceRequired();
			s.overworld().addFreshEntity(sperm);
		});
		tp(server, o.getX() + 12.5, o.getY() + 8, o.getZ() + 0.5, 90, 10);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		shot(context, "33_cachalote");
		check(server.computeOnServer(s -> sperm(s).angryAt() == null), "Cachalote: não devia reagir sem ser atacado");
		tp(server, o.getX() + 0.5, o.getY() + 7, o.getZ() - 6.5, 0, 0);
		server.runOnServer(s -> {
			player(s).setHealth(20.0F);
			SpermWhaleEntity sperm = sperm(s);
			sperm.setInvulnerableTime(0);
			boolean hurt = sperm.hurtServer(s.overworld(), s.overworld().damageSources().playerAttack(player(s)), 2.0F);
			check(sperm.angryAt() == player(s), "Cachalote: devia ficar bravo com quem atacou (golpe " + hurt + ", vida "
				+ sperm.getHealth() + ")");
		});
		// Confere no tick da onda: com a fome cheia o jogador se cura rápido logo depois.
		for (int i = 0; i < SpermWhaleEntity.SONIC_WARN + 60 && server.computeOnServer(s -> sperm(s).sonicCooldown()) == 0; i++) {
			context.waitTick();
		}
		check(server.computeOnServer(s -> player(s).getHealth()) <= 15.0F, "Cachalote: a onda sonora devia dar 6 de dano; "
			+ server.computeOnServer(s -> String.format(Locale.ROOT, "vida %.1f, recarga %d, bravo %s, distância %.1f, yaw %.0f",
				player(s).getHealth(), sperm(s).sonicCooldown(), sperm(s).angryAt() != null, sperm(s).distanceTo(player(s)), sperm(s).getYRot())));

		server.runCommand("kill @e[type=aquamarine:sperm_whale]");
		server.runCommand("kill @e[type=item]");
		server.runCommand("effect clear @a");
		server.runOnServer(s -> player(s).setHealth(20.0F));
		server.runCommand("gamemode creative @a");
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
		context.waitTicks(5);
	}

	private static void fill(TestServerContext server, int x1, int y1, int z1, int x2, int y2, int z2, String block) {
		int perLayer = (x2 - x1 + 1) * (z2 - z1 + 1);
		int layers = Math.max(1, 32768 / perLayer);
		for (int y = y1; y <= y2; y += layers) {
			server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:%s", x1, y, z1, x2, Math.min(y2, y + layers - 1), z2, block));
		}
	}

	private static FakePlayer fake(ServerLevel level, String name) {
		return FakePlayer.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
	}

	private static WhaleEntity whale(MinecraftServer server) {
		return server.overworld().getEntities(ModEntities.WHALE, e -> e.isAlive()).stream().findFirst()
			.orElseThrow(() -> new AssertionError("Baleia: sumiu do mundo"));
	}

	private static SpermWhaleEntity sperm(MinecraftServer server) {
		return server.overworld().getEntities(ModEntities.SPERM_WHALE, e -> e.isAlive()).stream().findFirst()
			.orElseThrow(() -> new AssertionError("Cachalote: sumiu do mundo"));
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

	private WhaleGameTest() {}
}
