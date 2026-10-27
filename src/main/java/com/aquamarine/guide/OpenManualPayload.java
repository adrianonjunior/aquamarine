package com.aquamarine.guide;

import com.aquamarine.Aquamarine;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** O servidor pede ao cliente que abra o Manual ({@code /aquamarine help}). */
public record OpenManualPayload() implements CustomPacketPayload {
	public static final OpenManualPayload INSTANCE = new OpenManualPayload();
	public static final CustomPacketPayload.Type<OpenManualPayload> TYPE = new CustomPacketPayload.Type<>(Aquamarine.id("open_manual"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenManualPayload> CODEC = StreamCodec.unit(INSTANCE);

	public static void register() {
		PayloadTypeRegistry.clientboundPlay().register(TYPE, CODEC);
	}

	@Override
	public CustomPacketPayload.Type<OpenManualPayload> type() {
		return TYPE;
	}
}
