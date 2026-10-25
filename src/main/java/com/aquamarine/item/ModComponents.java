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

	/** Cargas guardadas no Propulsor de Maré. */
	public static final DataComponentType<Integer> THRUSTER_CHARGES = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		Aquamarine.id("thruster_charges"), DataComponentType.<Integer>builder()
			.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModComponents() {}
}
