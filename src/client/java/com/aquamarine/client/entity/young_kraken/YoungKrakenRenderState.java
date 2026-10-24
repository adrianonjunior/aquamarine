package com.aquamarine.client.entity.young_kraken;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public final class YoungKrakenRenderState extends LivingEntityRenderState {
    public final AnimationState idle = new AnimationState();
    public final AnimationState swim = new AnimationState();
    public final AnimationState stunned = new AnimationState();
    public final AnimationState hatch = new AnimationState();
    public final AnimationState attack = new AnimationState();
    public final AnimationState death = new AnimationState();
}
