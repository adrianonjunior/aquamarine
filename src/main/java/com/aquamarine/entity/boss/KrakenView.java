package com.aquamarine.entity.boss;

import net.minecraft.world.entity.AnimationState;

/** Client-facing animation and phase state shared by the Kraken entity and renderer. */
public interface KrakenView {
    AnimationState idleAnimation();

    AnimationState swimAnimation();

    AnimationState emergeAnimation();

    AnimationState stunnedAnimation();

    AnimationState phaseChangeAnimation();

    AnimationState deathAnimation();

    AnimationState slamAnimation();

    AnimationState grabAnimation();

    AnimationState inkAnimation();

    AnimationState whirlpoolAnimation();

    AnimationState spawnYoungAnimation();

    AnimationState waterJetAnimation();

    AnimationState roarAnimation();

    AnimationState boltsAnimation();

    AnimationState ambushAnimation();

    AnimationState doubleGrabAnimation();

    int phase();

    float shieldFraction();
}
