package com.aquamarine.client.entity.harpoon;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.HarpoonStyleHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;

/**
 * Renderer do tridente em voo (Fase 6, 6D3): o do jogo continua desenhando o tridente comum; o Arpão Abissal, cada
 * ponta e o Cavitador de Neutrônio usam o {@link HarpoonModel} com textura própria.
 */
public final class HarpoonRenderer extends EntityRenderer<ThrownTrident, HarpoonRenderState> {
	private static final String[] STYLES = {"trident", "abyssal", "poison", "fire", "wither", "shock", "cavitator"};
	private static final RenderType[] TYPES = new RenderType[HarpoonStyleHolder.COUNT];

	static {
		for (int i = 1; i < STYLES.length; i++) {
			TYPES[i] = RenderTypes.entityCutout(Aquamarine.id("textures/entity/harpoon/" + STYLES[i] + ".png"));
		}
	}

	private final ThrownTridentRenderer vanilla;
	private final HarpoonModel model;

	public HarpoonRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.vanilla = new ThrownTridentRenderer(context);
		this.model = new HarpoonModel(context.bakeLayer(HarpoonModel.LAYER));
	}

	@Override
	public HarpoonRenderState createRenderState() {
		return new HarpoonRenderState();
	}

	@Override
	public void extractRenderState(ThrownTrident trident, HarpoonRenderState state, float partialTick) {
		vanilla.extractRenderState(trident, state, partialTick);
		state.style = trident instanceof HarpoonStyleHolder holder ? holder.aquamarine$harpoonStyle() : HarpoonStyleHolder.TRIDENT;
	}

	@Override
	public void submit(HarpoonRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
		if (state.style <= HarpoonStyleHolder.TRIDENT || state.style >= TYPES.length) {
			vanilla.submit(state, poseStack, collector, cameraState);
			return;
		}
		poseStack.pushPose();
		poseStack.rotateDegrees(Axis.YP, state.yRot - 90.0F);
		poseStack.rotateDegrees(Axis.ZP, state.xRot + 90.0F);
		collector.submitModel(model, state, poseStack, TYPES[state.style], state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
		super.submit(state, poseStack, collector, cameraState);
	}
}
