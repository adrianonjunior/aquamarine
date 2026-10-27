package com.aquamarine.machine;

/**
 * Ponte entre o block entity de uma máquina animada e o renderizador do cliente (0.6.1, 7B; contrato em
 * docs/06-tecnico.md, "Máquinas animadas"): o block entity guarda o relógio, o renderizador só lê.
 */
public interface MachineView {
	MachineMotion motion();

	/** Progresso visual contínuo, de 0 a 1; máquinas sem medidor mantêm o padrão zero. */
	default float visualProgress() { return 0.0F; }

	/** Estoque visível, de 0 a 1; usado por bandejas e reservatórios modelados. */
	default float visualStock() { return 0.0F; }

	/** Acende o diagnóstico vermelho quando o ambiente da máquina não serve. */
	default boolean visualInvalid() { return false; }
}
