package com.aquamarine.entity;

import net.minecraft.world.entity.AnimationState;

/** Animation state consumed by the Cachalote renderer. */
public interface SpermWhaleView {
    AnimationState idleAnimation();
    AnimationState swimAnimation();
    AnimationState sonicAnimation();
    AnimationState deathAnimation();
}
