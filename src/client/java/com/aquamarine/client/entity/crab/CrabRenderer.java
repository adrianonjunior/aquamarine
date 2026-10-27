package com.aquamarine.client.entity.crab;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.CrabEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public final class CrabRenderer extends MobRenderer<CrabEntity, CrabRenderState, CrabModel> {
    public CrabRenderer(EntityRendererProvider.Context context) {
        super(context, new CrabModel(context.bakeLayer(CrabModel.LAYER)), .35F);
    }
    @Override public CrabRenderState createRenderState() { return new CrabRenderState(); }
    @Override public Identifier getTextureLocation(CrabRenderState state) { return Aquamarine.id("textures/entity/crab/crab.png"); }
    @Override public void extractRenderState(CrabEntity entity, CrabRenderState state, float partial) {
        super.extractRenderState(entity, state, partial);
        state.idle_eyes.copyFrom(entity.animation("idle_eyes"));
        state.walk_side.copyFrom(entity.animation("walk_side"));
        state.flee.copyFrom(entity.animation("flee"));
        state.claw_wave.copyFrom(entity.animation("claw_wave"));
        state.burrow.copyFrom(entity.animation("burrow"));
        state.emerge.copyFrom(entity.animation("emerge"));
        state.pinch.copyFrom(entity.animation("pinch"));
        state.sleep.copyFrom(entity.animation("sleep"));
    }
}
