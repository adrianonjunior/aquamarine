package com.aquamarine.client.entity.whale;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public final class WhaleRenderState extends LivingEntityRenderState {
    public final AnimationState idle = new AnimationState();
    public final AnimationState swim = new AnimationState();
    public final AnimationState sing = new AnimationState();
    public final AnimationState surface = new AnimationState();
    public final AnimationState death = new AnimationState();
    public boolean saddled;
}
