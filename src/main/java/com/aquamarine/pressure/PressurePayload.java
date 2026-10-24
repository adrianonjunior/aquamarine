package com.aquamarine.pressure;

import com.aquamarine.Aquamarine;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Nível de Pressão do próprio jogador (0 a 4), enviado a cada mudança para a vinheta do cliente. */
public record PressurePayload(int level) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PressurePayload> TYPE = new CustomPacketPayload.Type<>(Aquamarine.id("pressure"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PressurePayload> CODEC =
		StreamCodec.composite(ByteBufCodecs.VAR_INT, PressurePayload::level, PressurePayload::new);

	@Override
	public CustomPacketPayload.Type<PressurePayload> type() {
		return TYPE;
	}
}
