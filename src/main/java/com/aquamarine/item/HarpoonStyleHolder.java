package com.aquamarine.item;

/**
 * Desenho do arpão em voo (Fase 6, 6D3), guardado nos dados sincronizados do tridente pelo {@code ThrownTridentMixin}:
 * 0 é o tridente do jogo; o Arpão Abissal sem ponta, cada ponta e o Cavitador de Neutrônio têm modelo e rastro próprios.
 * O arremesso continua o do tridente (Lealdade, Fisgada e Perfurante iguais).
 */
public interface HarpoonStyleHolder {
	int TRIDENT = 0;
	int PLAIN = 1;
	/** 2 a 5: {@code 2 + HarpoonTip.ordinal()}. */
	int CAVITATOR = 6;
	int COUNT = 7;

	int aquamarine$harpoonStyle();

	void aquamarine$setHarpoonStyle(int style);

	static int of(HarpoonTip tip) {
		return tip == null ? PLAIN : 2 + tip.ordinal();
	}
}
