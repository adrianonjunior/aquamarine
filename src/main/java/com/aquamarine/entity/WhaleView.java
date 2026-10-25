package com.aquamarine.entity;

import net.minecraft.world.entity.AnimationState;

/** Animation and tack state consumed by the Baleia-de-Nereia renderer. */
public interface WhaleView {
    AnimationState idleAnimation();
    AnimationState swimAnimation();
    AnimationState singAnimation();
    AnimationState surfaceAnimation();
    AnimationState deathAnimation();
    boolean saddled();
}
