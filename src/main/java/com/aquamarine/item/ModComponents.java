package com.aquamarine.item;

import com.aquamarine.Aquamarine;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

/** Componentes de item do mod (dados guardados no próprio item, sincronizados com o cliente). */
public final class ModComponents {
	/** Pontas guardadas na Aljava de Arpões. */
	public static final DataComponentType<QuiverContents> QUIVER_CONTENTS = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		Aquamarine.id("quiver_contents"), DataComponentType.<QuiverContents>builder()
			.persistent(QuiverContents.CODEC).networkSynchronized(QuiverContents.STREAM_CODEC).build());

	/** Radar Aquático (Fase 6, 6G2): até que tick fica ligado e o Porto mais perto achado ao ligar. */
	public static final DataComponentType<Long> RADAR_UNTIL = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		Aquamarine.id("radar_until"), DataComponentType.<Long>builder()
			.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());
	public static final DataComponentType<net.minecraft.core.BlockPos> RADAR_TARGET = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		Aquamarine.id("radar_target"), DataComponentType.<net.minecraft.core.BlockPos>builder()
			.persistent(net.minecraft.core.BlockPos.CODEC).networkSynchronized(net.minecraft.core.BlockPos.STREAM_CODEC).build());

	/** Cargas guardadas no Propulsor de Maré. */
	public static final DataComponentType<Integer> THRUSTER_CHARGES = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		Aquamarine.id("thruster_charges"), DataComponentType.<Integer>builder()
			.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModComponents() {}
}
