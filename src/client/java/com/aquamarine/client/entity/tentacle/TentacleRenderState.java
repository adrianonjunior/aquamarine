package com.aquamarine.client.entity.tentacle;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public final class TentacleRenderState extends LivingEntityRenderState {
    public final AnimationState idle = new AnimationState();
    public final AnimationState rise = new AnimationState();
    public final AnimationState slam = new AnimationState();
    public final AnimationState grab = new AnimationState();
    public final AnimationState release = new AnimationState();
}
