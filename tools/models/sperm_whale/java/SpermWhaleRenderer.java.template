package com.aquamarine.client.entity.sperm_whale;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.SpermWhaleView;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

public final class SpermWhaleRenderer<T extends Mob> extends MobRenderer<T, SpermWhaleRenderState, SpermWhaleModel> {
    private static final Identifier TEXTURE = Aquamarine.id("textures/entity/sperm_whale/sperm_whale.png");
    public SpermWhaleRenderer(EntityRendererProvider.Context context) {
        super(context, new SpermWhaleModel(context.bakeLayer(SpermWhaleModel.LAYER)), .82F);
    }
    @Override public SpermWhaleRenderState createRenderState() { return new SpermWhaleRenderState(); }
    @Override public void extractRenderState(T entity, SpermWhaleRenderState state, float partialTick) {
        super.extractRenderState(entity,state,partialTick);
        if (entity instanceof SpermWhaleView view) {
            copy(view.idleAnimation(),state.idle); copy(view.swimAnimation(),state.swim);
            copy(view.sonicAnimation(),state.sonic); copy(view.deathAnimation(),state.death);
        }
    }
    @Override public Identifier getTextureLocation(SpermWhaleRenderState state) { return TEXTURE; }
    private static void copy(AnimationState from,AnimationState to) { to.copyFrom(from); }
}
