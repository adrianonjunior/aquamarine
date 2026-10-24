package com.aquamarine.client.entity.kraken_egg;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

public final class KrakenEggRenderState extends EntityRenderState {
    public final AnimationState pulse = new AnimationState();
    public final AnimationState hatch = new AnimationState();
}
