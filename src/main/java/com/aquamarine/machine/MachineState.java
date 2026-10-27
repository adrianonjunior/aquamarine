package com.aquamarine.machine;

/**
 * Estado de uma máquina animada (0.6.1, 7B; docs/12-industria.md): o servidor decide e manda ao cliente só quando muda.
 * "Ligando" e "desligando" não são estados: o cliente toca os clipes {@code start} e {@code stop} na troca.
 */
public enum MachineState {
	/** Parada: sem produzir. */
	IDLE,
	/** Funcionando. */
	RUNNING,
	/** Sobrecarga: produz, mas não tem para onde mandar (a carga ficou cheia). */
	OVERLOAD,
	/** Cheia (tanques e coletores). */
	FULL,
	/** Pulso curto de uma máquina que acabou de produzir uma unidade. */
	COLLECTING;

	public static MachineState byId(int id) {
		MachineState[] all = values();
		return id >= 0 && id < all.length ? all[id] : IDLE;
	}
}
