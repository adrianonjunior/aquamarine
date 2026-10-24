package com.aquamarine.entity;

import net.minecraft.world.entity.AnimationState;

/** Animation contract implemented by young Kraken entities. */
public interface YoungKrakenView {
    AnimationState idleAnimation();
    AnimationState swimAnimation();
    AnimationState stunnedAnimation();
    AnimationState hatchAnimation();
    AnimationState attackAnimation();
    AnimationState deathAnimation();
}
