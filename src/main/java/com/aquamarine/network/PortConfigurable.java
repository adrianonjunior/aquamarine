package com.aquamarine.network;

import net.minecraft.core.Direction;

/**
 * Máquina com lados configuráveis (0.6.1, 7C e 7J): a Chave de Maré num lado cicla sai, entra e desligado, e a tela
 * mostra os seis lados. Quem implementa guarda o modo de cada lado e avisa o cliente quando muda.
 */
public interface PortConfigurable {
	PortMode port(Direction side);

	void cyclePort(Direction side);
}
