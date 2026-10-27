package com.aquamarine.client.machine;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

/**
 * O que uma máquina animada precisa para ser desenhada num quadro (0.6.1, 7B): até três clipes misturados (o atual e o
 * que está saindo), a direção da frente e as cores do corpo e do brilho (a sobrecarga avermelha e pulsa).
 */
public class MachineRenderState extends BlockEntityRenderState {
	public static final int LAYERS = 4;

	public MachineRenderer.Machine machine;
	public float yRot;
	public final String[] clip = new String[LAYERS];
	/** Segundos dentro de cada clipe (já com o módulo do laço). */
	public final float[] time = new float[LAYERS];
	public final float[] weight = new float[LAYERS];
	public int layers;
	public int bodyColor = -1;
	public int glowColor = -1;

	void add(String name, float seconds, float w) {
		if (layers < LAYERS && w > 0.001F) {
			clip[layers] = name;
			time[layers] = seconds;
			weight[layers] = w;
			layers++;
		}
	}
}
