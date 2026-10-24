package com.aquamarine.event;

import com.aquamarine.Aquamarine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Estado do mundo ligado ao Kraken (docs/03-invocacao-e-chat.md, "Implementação"): quem já
 * recebeu o Coração do Kraken e o controle do aviso falso (última noite sorteada e último dia
 * em que ele aconteceu). Guardado no overworld.
 */
public class KrakenWorldData extends SavedData {
	private static final Codec<KrakenWorldData> CODEC = RecordCodecBuilder.create(i -> i.group(
		UUIDUtil.CODEC.listOf().optionalFieldOf("heartReceived", List.of()).forGetter(d -> List.copyOf(d.heartReceived)),
		Codec.LONG.optionalFieldOf("lastCheckedNight", -1L).forGetter(d -> d.lastCheckedNight),
		Codec.LONG.optionalFieldOf("lastFalseWarningDay", FalseWarning.NEVER).forGetter(d -> d.lastFalseWarningDay)
	).apply(i, KrakenWorldData::new));
	public static final SavedDataType<KrakenWorldData> TYPE = new SavedDataType<>(Aquamarine.id("kraken"), KrakenWorldData::new, CODEC, DataFixTypes.LEVEL);

	private final Set<UUID> heartReceived = new HashSet<>();
	private long lastCheckedNight = -1;
	private long lastFalseWarningDay = FalseWarning.NEVER;

	public KrakenWorldData() {}

	private KrakenWorldData(List<UUID> heartReceived, long lastCheckedNight, long lastFalseWarningDay) {
		this.heartReceived.addAll(heartReceived);
		this.lastCheckedNight = lastCheckedNight;
		this.lastFalseWarningDay = lastFalseWarningDay;
	}

	public static KrakenWorldData get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(TYPE);
	}

	public boolean hasReceivedHeart(UUID player) {
		return heartReceived.contains(player);
	}

	/** Marca o jogador como já tendo o Coração; devolve true se era a primeira vez. */
	public boolean markHeartReceived(UUID player) {
		boolean added = heartReceived.add(player);
		if (added) {
			setDirty();
		}
		return added;
	}

	public long lastCheckedNight() {
		return lastCheckedNight;
	}

	public void setLastCheckedNight(long night) {
		lastCheckedNight = night;
		setDirty();
	}

	public long lastFalseWarningDay() {
		return lastFalseWarningDay;
	}

	public void setLastFalseWarningDay(long day) {
		lastFalseWarningDay = day;
		setDirty();
	}
}
