package com.aquamarine.entity.boss;

import java.util.List;

import net.minecraft.world.entity.AnimationState;

/**
 * Estados de animação de uma criatura do Kraken, na ordem de uma lista de nomes do contrato. O
 * servidor sorteia o índice em dados sincronizados; o cliente chama {@link #start} ao receber.
 */
public final class AnimationSync {
	private final List<String> names;
	private final AnimationState[] states;

	public AnimationSync(List<String> names) {
		this.names = names;
		this.states = new AnimationState[names.size()];
		for (int i = 0; i < states.length; i++) {
			states[i] = new AnimationState();
		}
	}

	public AnimationState state(String name) {
		return states[names.indexOf(name)];
	}

	public int indexOf(String name) {
		return names.indexOf(name);
	}

	public List<String> names() {
		return names;
	}

	/** Cliente: para as outras animações pontuais ({@code keepRunning} fica de fora, ex.: idle) e inicia a escolhida. */
	public void start(int index, int tick, int keepRunning) {
		for (int i = 0; i < states.length; i++) {
			if (i != index && i != keepRunning) {
				states[i].stop();
			}
		}
		states[index].start(tick);
	}
}
