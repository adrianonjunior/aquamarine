package com.aquamarine.entity.boss;

import com.aquamarine.Aquamarine;

import java.util.UUID;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Estado da barra de vida customizada de um boss, enviado do servidor para o cliente.
 * {@code barId} é o id do {@code ServerBossEvent}: o cliente troca a barra vanilla desse
 * evento pela barra própria.
 */
public record BossBarPayload(UUID barId, float health, float maxHealth, float shield, float maxShield, int phase,
	boolean shieldRegenerating) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<BossBarPayload> TYPE = new CustomPacketPayload.Type<>(Aquamarine.id("boss_bar"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BossBarPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, BossBarPayload::barId,
		ByteBufCodecs.FLOAT, BossBarPayload::health,
		ByteBufCodecs.FLOAT, BossBarPayload::maxHealth,
		ByteBufCodecs.FLOAT, BossBarPayload::shield,
		ByteBufCodecs.FLOAT, BossBarPayload::maxShield,
		ByteBufCodecs.VAR_INT, BossBarPayload::phase,
		ByteBufCodecs.BOOL, BossBarPayload::shieldRegenerating,
		BossBarPayload::new);

	@Override
	public CustomPacketPayload.Type<BossBarPayload> type() {
		return TYPE;
	}
}
