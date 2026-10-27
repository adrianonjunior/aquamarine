package com.aquamarine.machine;

/**
 * Ponte entre o block entity de uma máquina animada e o renderizador do cliente (0.6.1, 7B; contrato em
 * docs/06-tecnico.md, "Máquinas animadas"): o block entity guarda o relógio, o renderizador só lê.
 */
public interface MachineView {
	MachineMotion motion();
}
