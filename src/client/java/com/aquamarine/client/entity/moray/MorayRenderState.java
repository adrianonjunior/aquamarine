package com.aquamarine.client.entity.moray;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public final class MorayRenderState extends LivingEntityRenderState {
	public final AnimationState idle = new AnimationState();
	public final AnimationState swim = new AnimationState();
	public final AnimationState strike = new AnimationState();
	public final AnimationState chain = new AnimationState();
	public final AnimationState coil = new AnimationState();
	public final AnimationState stunned = new AnimationState();
	public final AnimationState death = new AnimationState();
	/** 0 a 1: carga da Descarga ou o brilho do aviso do Enrolar. */
	public float chargeFraction;
}
