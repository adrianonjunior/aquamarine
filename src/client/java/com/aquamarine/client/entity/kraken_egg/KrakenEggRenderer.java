package com.aquamarine.client.entity.kraken_egg;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.KrakenEggView;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;

public final class KrakenEggRenderer<T extends Entity> extends EntityRenderer<T, KrakenEggRenderState> {
    private static final Identifier TEXTURE = Aquamarine.id("textures/entity/kraken_egg/kraken_egg.png");
    private final KrakenEggModel model;
    public KrakenEggRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new KrakenEggModel(context.bakeLayer(KrakenEggModel.LAYER));
    }
    @Override public KrakenEggRenderState createRenderState() { return new KrakenEggRenderState(); }
    @Override public void extractRenderState(T entity, KrakenEggRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (entity instanceof KrakenEggView view) {
            copy(view.pulseAnimation(), state.pulse); copy(view.hatchAnimation(), state.hatch);
        }
    }
    @Override public void submit(KrakenEggRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
        CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        model.setupAnim(state);
        // Modelo no padrão de entidade do Blockbench (Y para baixo): inverte como o LivingEntityRenderer faz.
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        collector.submitModel(model, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
    private static void copy(AnimationState from, AnimationState to) { to.copyFrom(from); }
}
