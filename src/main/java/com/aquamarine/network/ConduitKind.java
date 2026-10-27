package com.aquamarine.network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;

/**
 * As três redes da indústria (0.6.1, 7C; docs/12-industria.md): energia (Cabo, anel azul), fluido (Cano, anel verde-água;
 * água e lava) e gás (Tubo de Ar, anel cinza-claro; Ar Comprimido). Cada rede só leva o seu tipo de conteúdo.
 */
public enum ConduitKind {
	ENERGY,
	FLUID,
	GAS;

	/** O Cano leva líquidos; o Tubo de Ar, só gás (mais leve que o ar). */
	public boolean accepts(FluidVariant variant) {
		return switch (this) {
			case FLUID -> !variant.isBlank() && !FluidVariantAttributes.isLighterThanAir(variant);
			case GAS -> !variant.isBlank() && FluidVariantAttributes.isLighterThanAir(variant);
			case ENERGY -> false;
		};
	}
}
