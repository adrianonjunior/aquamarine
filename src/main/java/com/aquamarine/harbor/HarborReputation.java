package com.aquamarine.harbor;

import com.aquamarine.Aquamarine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Reputação de cada jogador no Porto dos Náufragos (docs/08-fase3.md, seção 8): sobe ao negociar com os
 * Mergulhadores e ao defender a Vila no Alerta de Moréia, e abre os níveis de comércio. Vale para todos os
 * Portos do mundo. Também guarda o dia do último Alerta. Fica no overworld.
 */
public class HarborReputation extends SavedData {
	/** Pontos mínimos de cada nível de comércio (1 a 5): os mesmos da XP dos aldeões vanilla. */
	public static final int[] TIER_POINTS = {0, 10, 70, 150, 250};
	public static final int MAX_TIER = TIER_POINTS.length;

	private static final Codec<HarborReputation> CODEC = RecordCodecBuilder.create(i -> i.group(
		Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).optionalFieldOf("points", Map.of()).forGetter(d -> Map.copyOf(d.points)),
		Codec.LONG.optionalFieldOf("lastAlertDay", HarborAlert.NEVER).forGetter(d -> d.lastAlertDay)
	).apply(i, HarborReputation::new));
	public static final SavedDataType<HarborReputation> TYPE = new SavedDataType<>(Aquamarine.id("harbor"), HarborReputation::new, CODEC, DataFixTypes.LEVEL);

	private final Map<UUID, Integer> points = new HashMap<>();
	private long lastAlertDay = HarborAlert.NEVER;

	public HarborReputation() {}

	private HarborReputation(Map<UUID, Integer> points, long lastAlertDay) {
		this.points.putAll(points);
		this.lastAlertDay = lastAlertDay;
	}

	public static HarborReputation get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(TYPE);
	}

	/** Nível de comércio (1 a {@link #MAX_TIER}) que uma reputação abre. */
	public static int tier(int points) {
		int tier = 1;
		for (int level = 1; level < TIER_POINTS.length; level++) {
			if (points >= TIER_POINTS[level]) {
				tier = level + 1;
			}
		}
		return tier;
	}

	public int points(UUID player) {
		return points.getOrDefault(player, 0);
	}

	public int tier(UUID player) {
		return tier(points(player));
	}

	public void add(UUID player, int amount) {
		if (amount != 0) {
			set(player, points(player) + amount);
		}
	}

	public void set(UUID player, int amount) {
		points.put(player, Math.max(0, amount));
		setDirty();
	}

	public long lastAlertDay() {
		return lastAlertDay;
	}

	public void setLastAlertDay(long day) {
		lastAlertDay = day;
		setDirty();
	}
}
