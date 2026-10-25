package com.aquamarine.client.entity.whale;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.WhaleView;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

public final class WhaleRenderer<T extends Mob> extends MobRenderer<T, WhaleRenderState, WhaleModel> {
    private static final Identifier TEXTURE = Aquamarine.id("textures/entity/whale/whale.png");
    private static final float MODEL_DROP = 1.5F;
    private static final int MIN_LIGHT = 6;
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
    /** O modelo foi desenhado 1,5 bloco acima dos pés; desce para a barriga encostar na caixa. */
    @Override protected void scale(WhaleRenderState state, PoseStack poseStack) { poseStack.translate(0.0F, MODEL_DROP, 0.0F); }
    /** A caixa cobre só o tronco; nadadeiras e cauda passam dela, então o corte usa uma caixa maior. */
    @Override protected AABB getBoundingBoxForCulling(T entity, float partialTick) { return entity.getBoundingBox().inflate(8.0); }
    /** No fundo do mar a luz some: um mínimo de luz própria para a silhueta não virar mancha preta. */
    @Override protected int getBlockLightLevel(T entity, BlockPos pos) { return Math.max(MIN_LIGHT, super.getBlockLightLevel(entity, pos)); }
    @Override public Identifier getTextureLocation(WhaleRenderState state) { return TEXTURE; }
    private static void copy(AnimationState from, AnimationState to) { to.copyFrom(from); }
}
