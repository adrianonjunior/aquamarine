package com.aquamarine.gametest;

import com.aquamarine.client.music.KrakenMusic;
import com.aquamarine.client.music.KrakenSky;
import com.aquamarine.client.pressure.PressureHud;
import com.aquamarine.command.KrakenCommand;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.WhaleEntity;
import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.item.ModComponents;
import com.aquamarine.item.ModItems;
import com.aquamarine.item.TideThrusterItem;
import java.util.Locale;
import java.util.Properties;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Multiplayer de verdade (release 0.4.0): sobe um **servidor dedicado** e conecta o cliente pela rede, para pegar o que
 * o singleplayer esconde (o servidor integrado divide a mesma JVM e o mesmo tempo). Confere a entrada com os registros
 * sincronizados, a luta do Kraken no cliente (música por fase, céu, chuva), os pacotes nos dois sentidos (Pressão do
 * servidor para o cliente; Propulsor do cliente para o servidor) e a baleia andando com o piloto. Sem screenshots.
 */
final class MultiplayerGameTest {
	static void run(ClientGameTestContext context) {
		Properties properties = new Properties();
		properties.setProperty("difficulty", "normal");
		properties.setProperty("allow-flight", "true");
		try (TestDedicatedServerContext server = context.worldBuilder().createServer(properties);
			TestDedicatedServerConnection connection = server.connect()) {
			context.waitTicks(40);
			check(context.computeOnClient(mc -> mc.player != null && mc.getConnection() != null && !mc.isLocalServer()),
				"Multiplayer: o cliente devia estar num servidor dedicado");
			check(context.computeOnClient(mc -> mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENTITY_TYPE)
				.containsKey(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(ModEntities.KRAKEN))),
				"Multiplayer: os registros do mod não sincronizaram");
			// Convidado num servidor: o JEI só vê as receitas do mod porque o servidor as manda ao entrar.
			boolean forge = false;
			for (int i = 0; i < 30 && !forge; i++) {
				context.waitTicks(10);
				forge = context.computeOnClient(mc -> com.aquamarine.client.compat.jei.AquamarineJeiPlugin.runtime()
					.map(runtime -> runtime.getRecipeManager().createRecipeLookup(com.aquamarine.client.compat.jei.AquamarineJeiPlugin.ABYSSAL_FORGING)
						.get().findAny().isPresent()).orElse(false));
			}
			check(forge, "Multiplayer: o JEI do convidado devia ver as receitas da Forja");
			String name = context.computeOnClient(mc -> mc.player.getGameProfile().name());
			server.runCommand("op " + name);
			server.runCommand("gamemode survival " + name);

			BlockPos origin = server.computeOnServer(s -> player(s).blockPosition().offset(150, 0, 0));
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				level.getChunkAt(origin);
				KrakenCommand.buildArena(level, origin);
				KrakenEntity kraken = KrakenSummoning.spawnAt(level, origin.offset(0, 25, 0), origin);
				check(kraken != null, "Multiplayer: o Kraken não surgiu");
				kraken.attacks().setEnabled(false);
				ServerPlayer p = player(s);
				p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20000));
				p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20000, 4));
			});
			tp(server, name, origin.getX() + 0.5, origin.getY() + 28, origin.getZ() + 14.5, 180, 0);
			context.waitTicks(80);

			// Luta no cliente: música da fase 1 tocando de verdade, céu tingido e chuva no servidor.
			check(context.computeOnClient(mc -> KrakenMusic.currentPhase() == 1 && KrakenMusic.framesMixed() > 0),
				"Multiplayer: a música da Tormenta devia tocar no cliente");
			check(context.computeOnClient(mc -> KrakenSky.intensity() > 0.3F), "Multiplayer: o céu devia tingir perto do Kraken");
			check(server.computeOnServer(s -> s.getWeatherData().isRaining()), "Multiplayer: a luta devia trazer chuva");

			// Pacote do servidor para o cliente: a Pressão sobe perto do Kraken e chega na HUD.
			context.waitFor(mc -> PressureHud.level() >= 1, 400);
			check(context.computeOnClient(mc -> PressureHud.level()) >= 1, "Multiplayer: a Pressão não chegou ao cliente");

			// Troca de fase no servidor; a Fúria entra no cliente depois do golpe.
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				KrakenEntity k = kraken(s);
				k.hurtServer(level, level.damageSources().playerAttack(player(s)), k.shield() * 4.0F + 1.0F);
				k.hurtServer(level, level.damageSources().playerAttack(player(s)), k.realMaxHealth());
			});
			context.waitTicks(90);
			check(server.computeOnServer(s -> kraken(s).phase() == 2), "Multiplayer: o Kraken devia estar na fase 2");
			check(context.computeOnClient(mc -> KrakenMusic.currentPhase() == 2), "Multiplayer: a Fúria devia tocar no cliente");

			// Pacote do cliente para o servidor: o Propulsor dispara pelo botão e o servidor empurra o jogador.
			server.runOnServer(s -> {
				ItemStack thruster = new ItemStack(ModItems.TIDE_THRUSTER);
				thruster.set(ModComponents.THRUSTER_CHARGES, 3);
				player(s).setItemInHand(InteractionHand.OFF_HAND, thruster);
			});
			context.waitTicks(5);
			context.runOnClient(mc -> ClientPlayNetworking.send(new TideThrusterItem.FirePayload()));
			connection.waitForServerboundPackets();
			context.waitTicks(2);
			check(server.computeOnServer(s -> TideThrusterItem.isBoosting(player(s))), "Multiplayer: o Propulsor não disparou no servidor");
			check(context.computeOnClient(mc -> mc.player.getDeltaMovement().length() > 0.3), "Multiplayer: o impulso não chegou ao cliente");

			// Vitória: a música da luta para e o tempo limpa quando o Kraken some.
			server.runCommand("kill @e[type=aquamarine:kraken]");
			context.waitTicks(120);
			check(context.computeOnClient(mc -> !KrakenMusic.playing()), "Multiplayer: a música da luta devia parar na vitória");

			// Baleia: domada e selada, o piloto anda com ela (a montaria anda no cliente do piloto).
			int whaleId = server.computeOnServer(s -> {
				ServerLevel level = s.overworld();
				WhaleEntity whale = ModEntities.WHALE.create(level, EntitySpawnReason.COMMAND);
				whale.snapTo(origin.getX() + 0.5, origin.getY() + 20, origin.getZ() + 0.5, 180.0F, 0.0F);
				whale.setPersistenceRequired();
				level.addFreshEntity(whale);
				whale.tame(player(s));
				whale.setSaddled(true);
				player(s).startRiding(whale, true, true);
				return whale.getId();
			});
			context.waitTicks(20);
			check(context.computeOnClient(mc -> mc.level.getEntity(whaleId) instanceof WhaleEntity w && w.getControllingPassenger() == mc.player),
				"Multiplayer: no cliente, o dono devia pilotar a baleia");
			Vec3 before = server.computeOnServer(s -> s.overworld().getEntity(whaleId).position());
			context.getInput().holdKey(options -> options.keyUp);
			context.waitTicks(50);
			context.getInput().releaseKey(options -> options.keyUp);
			connection.waitForServerboundPackets();
			double moved = server.computeOnServer(s -> s.overworld().getEntity(whaleId).position().distanceTo(before));
			check(moved > 3.0, String.format(Locale.ROOT, "Multiplayer: a baleia montada andou só %.2f blocos", moved));
			server.runOnServer(s -> {
				player(s).stopRiding();
				s.overworld().getEntity(whaleId).discard();
			});
			context.waitTicks(5);
		}
	}

	private static ServerPlayer player(MinecraftServer s) {
		return s.getPlayerList().getPlayers().getFirst();
	}

	private static KrakenEntity kraken(MinecraftServer s) {
		return s.overworld().getEntities(ModEntities.KRAKEN, e -> e.isAlive()).getFirst();
	}

	private static void tp(TestDedicatedServerContext server, String name, double x, double y, double z, float yaw, float pitch) {
		server.runCommand(String.format(Locale.ROOT, "tp %s %.2f %.2f %.2f %.1f %.1f", name, x, y, z, yaw, pitch));
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private MultiplayerGameTest() {}
}
