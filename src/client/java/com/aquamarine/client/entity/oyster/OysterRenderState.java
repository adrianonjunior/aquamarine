package com.aquamarine.client.entity.oyster;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
public final class OysterRenderState extends LivingEntityRenderState {
    public final AnimationState idle=new AnimationState(), close=new AnimationState(), open=new AnimationState(), pearl_glow=new AnimationState();
    public float openness;
    public boolean pearl;
}
