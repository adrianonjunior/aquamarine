package com.aquamarine.network;

import net.minecraft.core.Direction;

/**
 * Marca as máquinas que trocam gás pelo Tubo de Ar (0.6.1, 7C e 7F: Compressor de Ar, Pulmão de Ferro e os geradores
 * regulados). Só nelas o tubo mostra o anel de ligação e entrega ou puxa Ar Comprimido. O Cano não liga nas máquinas só
 * de gás; nas que também recebem líquido (a Fenda no modo fluido) ele liga pelos lados de {@link #liquidSide}.
 */
public interface GasPort {
	/** Por este lado (da máquina) troca gás: o Tubo de Ar liga. */
	default boolean gasSide(Direction side) {
		return true;
	}

	/** Por este lado (da máquina) também recebe líquido: o Cano liga. */
	default boolean liquidSide(Direction side) {
		return false;
	}
}
