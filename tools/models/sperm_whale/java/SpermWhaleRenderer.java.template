package com.aquamarine.client.entity.sperm_whale;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.SpermWhaleView;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

public final class SpermWhaleRenderer<T extends Mob> extends MobRenderer<T, SpermWhaleRenderState, SpermWhaleModel> {
    private static final Identifier TEXTURE = Aquamarine.id("textures/entity/sperm_whale/sperm_whale.png");
    private static final float MODEL_DROP = 1.5F;
    private static final int MIN_LIGHT = 6;
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
    /** O modelo foi desenhado 1,5 bloco acima dos pés; desce para a barriga encostar na caixa. */
    @Override protected void scale(SpermWhaleRenderState state, PoseStack poseStack) { poseStack.translate(0.0F, MODEL_DROP, 0.0F); }
    /** A caixa cobre só o tronco; nadadeiras e cauda passam dela, então o corte usa uma caixa maior. */
    @Override protected AABB getBoundingBoxForCulling(T entity, float partialTick) { return entity.getBoundingBox().inflate(8.0); }
    /** No fundo do mar a luz some: um mínimo de luz própria para a silhueta não virar mancha preta. */
    @Override protected int getBlockLightLevel(T entity, BlockPos pos) { return Math.max(MIN_LIGHT, super.getBlockLightLevel(entity, pos)); }
    @Override public Identifier getTextureLocation(SpermWhaleRenderState state) { return TEXTURE; }
    private static void copy(AnimationState from,AnimationState to) { to.copyFrom(from); }
}
