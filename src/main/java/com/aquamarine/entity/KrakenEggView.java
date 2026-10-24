package com.aquamarine.entity;

import net.minecraft.world.entity.AnimationState;

/** Animation contract implemented by the Kraken's egg entity. */
public interface KrakenEggView {
    AnimationState pulseAnimation();
    AnimationState hatchAnimation();
}
