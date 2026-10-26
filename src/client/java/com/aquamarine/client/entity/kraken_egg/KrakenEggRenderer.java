package com.aquamarine.client.entity.kraken_egg;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.KrakenEggView;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;

public final class KrakenEggRenderer<T extends Entity> extends EntityRenderer<T, KrakenEggRenderState> {
    private static final Identifier TEXTURE = Aquamarine.id("textures/entity/kraken_egg/kraken_egg.png");
    private static final Identifier GLOW = Aquamarine.id("textures/entity/kraken_egg/kraken_egg_glow.png");
    /**
     * Três passadas, nesta ordem: raízes e filhote opacos, o brilho (olhos do filhote e veias) e por último a membrana
     * translúcida. Com a membrana antes, a profundidade dela escondia o filhote.
     */
    private static final RenderType INNER = RenderTypes.entityCutout(TEXTURE);
    private static final RenderType SHINE = RenderTypes.eyes(GLOW);
    private static final RenderType MEMBRANE = RenderTypes.entityTranslucentCull(TEXTURE);
    private final KrakenEggModel inner;
    private final KrakenEggModel shine;
    private final KrakenEggModel membrane;
    public KrakenEggRenderer(EntityRendererProvider.Context context) {
        super(context);
        inner = new KrakenEggModel(context.bakeLayer(KrakenEggModel.LAYER));
        shine = new KrakenEggModel(context.bakeLayer(KrakenEggModel.LAYER));
        membrane = new KrakenEggModel(context.bakeLayer(KrakenEggModel.LAYER));
        inner.root().getChild("anchor").getChild("sac").visible = false;
        ModelPart anchor = membrane.root().getChild("anchor");
        anchor.getChild("embryo").visible = false;
        for (int k = 0; k < 5; k++) anchor.getChild("root_" + k).visible = false;
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
        inner.setupAnim(state); shine.setupAnim(state); membrane.setupAnim(state);
        // Modelo no padrão de entidade do Blockbench (Y para baixo): inverte como o LivingEntityRenderer faz.
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        collector.submitModel(inner, state, poseStack, INNER, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        collector.order(1).submitModel(shine, state, poseStack, SHINE, 0xF000F0, OverlayTexture.NO_OVERLAY, -1, null, 0);
        collector.order(2).submitModel(membrane, state, poseStack, MEMBRANE, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
    private static void copy(AnimationState from, AnimationState to) { to.copyFrom(from); }
}
