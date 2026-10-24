package com.aquamarine.entity;

import net.minecraft.world.entity.AnimationState;

/** Animation contract implemented by the Kraken's independent tentacle entities. */
public interface TentacleView {
    AnimationState idleAnimation();
    AnimationState riseAnimation();
    AnimationState slamAnimation();
    AnimationState grabAnimation();
    AnimationState releaseAnimation();
}
