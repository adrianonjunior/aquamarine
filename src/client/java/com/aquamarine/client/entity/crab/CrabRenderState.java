package com.aquamarine.client.entity.crab;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
public final class CrabRenderState extends LivingEntityRenderState {
    public final AnimationState idle_eyes = new AnimationState();
    public final AnimationState walk_side = new AnimationState();
    public final AnimationState flee = new AnimationState();
    public final AnimationState claw_wave = new AnimationState();
    public final AnimationState burrow = new AnimationState();
    public final AnimationState emerge = new AnimationState();
    public final AnimationState pinch = new AnimationState();
    public final AnimationState sleep = new AnimationState();
}
