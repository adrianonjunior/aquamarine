package com.aquamarine.event;

import com.aquamarine.config.AquamarineConfig;

import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;

/**
 * Gatilho do aviso falso (docs/03-invocacao-e-chat.md, "Aviso falso"): em noite de tempestade,
 * com jogador em oceano profundo, 8% de chance por noite e no máximo uma vez a cada 3 dias de
 * jogo. As contas são puras ({@link #decide}) para o JUnit; {@link #check} liga ao mundo.
 */
public final class FalseWarning {
	public static final double CHANCE = 0.08;
	public static final int MIN_DAYS_BETWEEN = 3;
	public static final long NEVER = -1000;
	private static final long DAY_TICKS = 24000;
	private static final long NIGHT_OFFSET = 12000;
	private static final int CHECK_INTERVAL = 100;

	public enum Outcome {
		/** Sem condições (desligado, sem tempestade, ninguém em oceano profundo) ou noite já sorteada. */
		NOT_ELIGIBLE,
		/** Noite sorteada e perdeu. */
		MISSED,
		/** Sorteou, mas o último aviso falso foi há menos de 3 dias. */
		COOLDOWN,
		TRIGGERED
	}

	public static long dayIndex(long clockTicks) {
		return Math.floorDiv(clockTicks, DAY_TICKS);
	}

	/** Cada noite (a partir das 12000 do relógio, até o amanhecer) tem um número; só uma sorte por noite. */
	public static long nightIndex(long clockTicks) {
		return Math.floorDiv(clockTicks - NIGHT_OFFSET, DAY_TICKS);
	}

	public static Outcome decide(boolean enabled, boolean stormyNight, boolean playerInDeepOcean, long clockTicks,
		long lastCheckedNight, long lastWarningDay, double roll) {
		if (!enabled || !stormyNight || !playerInDeepOcean || nightIndex(clockTicks) == lastCheckedNight) {
			return Outcome.NOT_ELIGIBLE;
		}
		if (roll >= CHANCE) {
			return Outcome.MISSED;
		}
		return dayIndex(clockTicks) - lastWarningDay >= MIN_DAYS_BETWEEN ? Outcome.TRIGGERED : Outcome.COOLDOWN;
	}

	/** Jogador na água de um oceano profundo (bioma da tag {@code #minecraft:is_deep_ocean}). */
	public static boolean inDeepOcean(ServerPlayer player) {
		return !player.isSpectator() && player.isAlive() && player.isEyeInFluid(FluidTags.WATER)
			&& player.level().getBiome(player.blockPosition()).is(BiomeTags.IS_DEEP_OCEAN);
	}

	public static void tick(MinecraftServer server) {
		if (server.getTickCount() % CHECK_INTERVAL == 0) {
			ServerLevel level = server.overworld();
			check(level, FalseWarning::inDeepOcean, () -> level.getRandom().nextDouble());
		}
	}

	/** Uma checagem: sorteia a noite e, se der, começa o aviso falso sobre o primeiro jogador elegível. */
	public static Outcome check(ServerLevel level, Predicate<ServerPlayer> eligible, DoubleSupplier roll) {
		AquamarineConfig config = AquamarineConfig.get();
		boolean stormy = level.isThundering() && level.isDarkOutside();
		if (!config.falseWarning || !stormy) {
			return Outcome.NOT_ELIGIBLE;
		}
		ServerPlayer target = level.players().stream()
			.filter(p -> eligible.test(p) && !KrakenSummoning.isActiveNear(level, p.blockPosition()))
			.findFirst().orElse(null);
		long clock = level.getOverworldClockTime();
		KrakenWorldData data = KrakenWorldData.get(level.getServer());
		Outcome outcome = decide(config.falseWarning, stormy, target != null, clock, data.lastCheckedNight(),
			data.lastFalseWarningDay(), target == null ? 1.0 : roll.getAsDouble());
		if (outcome != Outcome.NOT_ELIGIBLE) {
			data.setLastCheckedNight(nightIndex(clock));
		}
		if (outcome == Outcome.TRIGGERED) {
			data.setLastFalseWarningDay(dayIndex(clock));
			KrakenSummoning.start(level, target.blockPosition(), true);
		}
		return outcome;
	}

	private FalseWarning() {}
}
