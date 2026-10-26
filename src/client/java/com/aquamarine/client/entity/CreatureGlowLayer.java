package com.aquamarine.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * Camada de brilho das criaturas do Kraken (ventosas, olhos, pintas bioluminescentes): desenha a textura
 * {@code <bicho>_glow.png} com luz própria por cima do corpo, como os olhos da aranha. As texturas saem de
 * tools/models/spawnlings/build_model.py.
 */
public final class CreatureGlowLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
	private static final int FULL_BRIGHT = 0xF000F0;
	private final RenderType type;

	public CreatureGlowLayer(RenderLayerParent<S, M> parent, Identifier texture) {
		super(parent);
		this.type = RenderTypes.eyes(texture);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, S state, float yRot, float xRot) {
		collector.order(1).submitModel(this.getParentModel(), state, poseStack, this.type, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1, null, 0);
	}
}
