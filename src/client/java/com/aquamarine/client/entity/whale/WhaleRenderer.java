package com.aquamarine.client.entity.whale;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.WhaleView;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

public final class WhaleRenderer<T extends Mob> extends MobRenderer<T, WhaleRenderState, WhaleModel> {
    private static final Identifier TEXTURE = Aquamarine.id("textures/entity/whale/whale.png");
    public WhaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhaleModel(context.bakeLayer(WhaleModel.LAYER)), .88F);
    }
    @Override public WhaleRenderState createRenderState() { return new WhaleRenderState(); }
    @Override public void extractRenderState(T entity, WhaleRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (entity instanceof WhaleView view) {
            copy(view.idleAnimation(), state.idle); copy(view.swimAnimation(), state.swim);
            copy(view.singAnimation(), state.sing); copy(view.surfaceAnimation(), state.surface);
            copy(view.deathAnimation(), state.death); state.saddled = view.saddled();
        }
    }
    @Override public Identifier getTextureLocation(WhaleRenderState state) { return TEXTURE; }
    private static void copy(AnimationState from, AnimationState to) { to.copyFrom(from); }
}
