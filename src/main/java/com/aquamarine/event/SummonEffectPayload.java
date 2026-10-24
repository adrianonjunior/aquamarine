package com.aquamarine.event;

import com.aquamarine.Aquamarine;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Efeito de tela da invocação, por {@code ticks} ticks, mandado pelo servidor aos jogadores
 * na área: tremor (aos 5 s), silêncio do oceano (aos 0 s) e céu escurecendo (fim do aviso falso).
 */
public record SummonEffectPayload(int effect, int ticks) implements CustomPacketPayload {
	public static final int SHAKE = 0;
	public static final int SILENCE = 1;
	public static final int DARKEN = 2;

	public static final CustomPacketPayload.Type<SummonEffectPayload> TYPE = new CustomPacketPayload.Type<>(Aquamarine.id("summon_effect"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SummonEffectPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, SummonEffectPayload::effect,
		ByteBufCodecs.VAR_INT, SummonEffectPayload::ticks,
		SummonEffectPayload::new);

	public static void register() {
		PayloadTypeRegistry.clientboundPlay().register(TYPE, CODEC);
	}

	@Override
	public CustomPacketPayload.Type<SummonEffectPayload> type() {
		return TYPE;
	}
}
