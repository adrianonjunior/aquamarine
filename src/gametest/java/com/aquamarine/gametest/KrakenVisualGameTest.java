package com.aquamarine.gametest;

import com.aquamarine.client.KrakenEffects;
import com.aquamarine.command.KrakenCommand;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.boss.KrakenMovement;
import com.aquamarine.event.KrakenSummoning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

/**
 * Animações, partículas e rachaduras do Kraken (faixa de animação do Opus). Três Krakens lado a lado,
 * cada um numa animação, disparadas em tempos diferentes para a foto pegar todos no meio do aviso.
 * Confere no cliente que cada animação de ataque e de fase soltou partículas (KrakenEffects).
 * Chamado por {@link AquamarineClientGameTest}; 8 screenshots.
 */
final class KrakenVisualGameTest {
	/** Distância entre os Krakens da fileira (blocos). */
	private static final int GAP = 36;
	/** Os Krakens na ordem em que surgiram (esquerda para a direita); eles se movem, então a ordem fica fixa aqui. */
	private static final List<UUID> ROW = new ArrayList<>();

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		server.runCommand("kill @e[type=aquamarine:kraken]");
		// O teste anterior pode ter deixado o jogador morto ou sob efeitos: começa vivo, em criativo e curado.
		if (server.computeOnServer(s -> !player(s).isAlive())) {
			for (int i = 0; i < 5 && server.computeOnServer(s -> !player(s).isAlive()); i++) {
				context.runOnClient(mc -> {
					if (mc.player != null) {
						mc.player.respawn();
					}
				});
				context.waitTicks(30);
			}
		}
		server.runCommand("gamemode creative @a");
		server.runOnServer(s -> {
			player(s).removeAllEffects();
			player(s).setHealth(player(s).getMaxHealth());
		});
		BlockPos o = base.offset(-600, 0, 0);
		server.runCommand(String.format(Locale.ROOT, "forceload add %d %d %d %d", o.getX() - 80, o.getZ() - 80, o.getX() + 80, o.getZ() + 80));
		context.waitTicks(20);
		ROW.clear();
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			KrakenCommand.buildArena(level, o, 64, 64);
			for (int i = -1; i <= 1; i++) {
				// Sem a IA de ataques (2D-C): cada Kraken só faz a animação que o teste manda.
				KrakenEntity kraken = KrakenSummoning.spawnAt(level, o.offset(i * GAP, 24, 0), o);
				kraken.attacks().setEnabled(false);
				ROW.add(kraken.getUUID());
			}
			ServerPlayer player = player(s);
			player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20000));
			player.getAbilities().flying = true;
			player.onUpdateAbilities();
		});
		aim(context, server, o);
		world.getConnection().waitForChunksRender();
		context.waitTicks(40);
		check(server.computeOnServer(s -> row(s).size() == 3), "Kraken visual: os 3 Krakens não surgiram");

		// Cada trio pega a foto com o aviso perto do fim (tempo em segundos dentro do clipe).
		pose(context, server, o, "20_kraken_slam_grab_duplo", "slam", 1.25F, "grab", 1.3F, "doubleGrab", 1.3F);
		pose(context, server, o, "21_kraken_tinta_redemoinho_crias", "ink", 1.75F, "whirlpool", 1.0F, "spawnYoung", 1.7F);
		pose(context, server, o, "22_kraken_jato_rugido_raios", "waterJet", 1.0F, "roar", 1.25F, "bolts", 1.7F);
		pose(context, server, o, "22b_kraken_jato_rugido_raios_impacto", "waterJet", 1.45F, "roar", 1.75F, "bolts", 2.3F);
		pose(context, server, o, "23_kraken_emboscada_emerge_atordoado", "ambush", 1.7F, "emerge", 1.1F, "stunned", 0.4F);
		pose(context, server, o, "24_kraken_fase_morte_nado", "phaseChange", 1.0F, "death", 1.0F, "swim", 0.5F);
		pose(context, server, o, "25_kraken_golpes", "slam", 1.65F, "roar", 1.62F, "ink", 2.15F);

		for (String name : KrakenEntity.ANIMATION_NAMES) {
			if (!name.equals("idle") && !name.equals("swim")) {
				int emitted = context.computeOnClient(mc -> KrakenEffects.emitted(name));
				check(emitted > 0, "Kraken visual: a animação " + name + " não soltou partículas");
			}
		}

		// Rachaduras nas 3 fases: escudo abaixo de 25% em cada Kraken (fases 1, 2 e 3 da esquerda para a direita).
		server.runOnServer(s -> row(s).forEach(k -> k.playAnimation("idle")));
		hitShield(server, 0, 0.85F);
		breakToNextPhase(context, server, 1);
		context.waitTicks(70);
		hitShield(server, 1, 0.85F);
		breakToNextPhase(context, server, 2);
		context.waitTicks(70);
		hitShield(server, 2, 0.85F);
		context.waitTicks(10);
		check(server.computeOnServer(s -> {
			List<KrakenEntity> row = row(s);
			return row.get(0).phase() == 1 && row.get(1).phase() == 2 && row.get(2).phase() == 3
				&& row.stream().allMatch(k -> k.shieldFraction() > 0.0F && k.shieldFraction() <= 0.25F);
		}), "Kraken visual: as fases 1, 2 e 3 deviam estar com o escudo abaixo de 25%");
		check(context.computeOnClient(mc -> KrakenEffects.emitted("shield")) > 0, "Kraken visual: as rachaduras não soltaram estilhaços");
		aim(context, server, o);
		shot(context, "26_kraken_rachaduras_fases_1_2_3");

		// Locomoção: com a IA ligada o Kraken do meio nada pela arena, preso ao centro, e solta rastro.
		Vec3 start = server.computeOnServer(s -> row(s).get(1).position());
		server.runOnServer(s -> row(s).get(1).attacks().setEnabled(true));
		context.waitTicks(200);
		Vec3 now = server.computeOnServer(s -> row(s).get(1).position());
		double moved = Math.sqrt(Mth.square(now.x - start.x) + Mth.square(now.z - start.z));
		check(moved > 1.5, "Kraken visual: com a IA ligada o Kraken devia nadar, andou " + moved);
		Vec3 home = Vec3.atBottomCenterOf(o.offset(0, 24, 0));
		double fromHome = Math.sqrt(Mth.square(now.x - home.x) + Mth.square(now.z - home.z));
		check(fromHome <= KrakenMovement.LEASH + 1.0, "Kraken visual: saiu do limite da arena, " + fromHome + " blocos do centro");
		check(context.computeOnClient(mc -> KrakenEffects.emitted("trail")) > 0, "Kraken visual: nadando sem rastro");
		aim(context, server, o);
		shot(context, "27_kraken_nadando");
		server.runOnServer(s -> row(s).get(1).attacks().setEnabled(false));

		server.runCommand("kill @e[type=aquamarine:kraken]");
		server.runCommand(String.format(Locale.ROOT, "forceload remove %d %d %d %d", o.getX() - 24, o.getZ() - 24, o.getX() + 24, o.getZ() + 24));
		context.waitTicks(20);
	}

	/** Dispara as três animações escalonadas para estarem nos tempos pedidos no mesmo instante e fotografa. */
	private static void pose(ClientGameTestContext context, TestServerContext server, BlockPos o, String shot,
		String a, float ta, String b, float tb, String c, float tc) {
		String[] names = {a, b, c};
		float[] at = {ta, tb, tc};
		int longest = (int) Math.ceil(Math.max(ta, Math.max(tb, tc)) * 20.0F);
		aim(context, server, o);
		int elapsed = 0;
		List<Integer> order = new ArrayList<>(List.of(0, 1, 2));
		order.sort(Comparator.comparingDouble(i -> -at[i]));
		for (int i : order) {
			int startTick = longest - Math.round(at[i] * 20.0F);
			if (startTick > elapsed) {
				context.waitTicks(startTick - elapsed);
				elapsed = startTick;
			}
			String name = names[i];
			int slot = i;
			server.runOnServer(s -> check(row(s).get(slot).playAnimation(name), "Kraken visual: animação desconhecida " + name));
		}
		context.waitTicks(longest - elapsed + 1);
		check(server.computeOnServer(s -> player(s).isAlive()), "Kraken visual: o jogador morreu antes da foto " + shot);
		shot(context, shot);
	}

	/** Os Krakens da arena, na ordem em que surgiram (da esquerda para a direita). */
	private static List<KrakenEntity> row(MinecraftServer s) {
		List<KrakenEntity> row = new ArrayList<>();
		for (UUID id : ROW) {
			if (s.overworld().getEntity(id) instanceof KrakenEntity kraken) {
				row.add(kraken);
			}
		}
		return row;
	}

	/** Leva o escudo do Kraken {@code index} a {@code 1 - fraction} (mão vazia: escudo recebe 25%). */
	private static void hitShield(TestServerContext server, int index, float fraction) {
		server.runOnServer(s -> {
			KrakenEntity k = row(s).get(index);
			ServerLevel level = s.overworld();
			k.hurtServer(level, level.damageSources().playerAttack(player(s)), k.maxShield() * fraction * 4.0F);
		});
	}

	/** Quebra o escudo e tira vida até a fronteira: os Krakens de {@code from} em diante passam de fase. */
	private static void breakToNextPhase(ClientGameTestContext context, TestServerContext server, int from) {
		hitRow(server, from, true);
		context.waitTicks(12);
		hitRow(server, from, false);
		context.waitTicks(2);
	}

	private static void hitRow(TestServerContext server, int from, boolean shield) {
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			List<KrakenEntity> row = row(s);
			for (int i = from; i < row.size(); i++) {
				KrakenEntity k = row.get(i);
				if (k.isTransitioning()) {
					continue;
				}
				k.hurtServer(level, level.damageSources().playerAttack(player(s)), shield ? k.shield() * 4.0F + 1.0F : 700.0F);
			}
		});
	}

	/** Câmera na água, à frente da fileira e um pouco acima, olhando para os três. */
	private static void aim(ClientGameTestContext context, TestServerContext server, BlockPos o) {
		server.runCommand(String.format(Locale.ROOT, "tp @p %.2f %.2f %.2f 180 -22", o.getX() + 0.5, o.getY() + 26.0, o.getZ() + 34.5));
		context.waitTicks(3);
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

	private KrakenVisualGameTest() {}
}
