package com.aquamarine.entity;

import net.minecraft.world.entity.AnimationState;

/** Estado de animação da Moréia Tempestuosa lido pelo renderer (contrato da Fase 3 em docs/06-tecnico.md). */
public interface MorayView {
	AnimationState idleAnimation();
	AnimationState swimAnimation();
	/** Bote: recua e arqueia 1 s, depois avança. */
	AnimationState strikeAnimation();
	/** Descarga em Cadeia: treme 1,5 s e descarrega. */
	AnimationState chainAnimation();
	/** Enrolar: a cauda brilha 1 s e enrola o alvo. */
	AnimationState coilAnimation();
	AnimationState stunnedAnimation();
	AnimationState deathAnimation();
	/** 0 a 1: carga das faíscas (Descarga) e do brilho da cauda (Enrolar). */
	float chargeFraction();
}
