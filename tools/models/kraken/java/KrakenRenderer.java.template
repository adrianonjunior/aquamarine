package com.aquamarine.client.entity.kraken;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.boss.KrakenView;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

/** Renderer is generic so it compiles before the parallel 2A-C entity reaches main. */
public final class KrakenRenderer<T extends Mob> extends MobRenderer<T, KrakenRenderState, KrakenModel> {
	private static final Identifier TEXTURE = Aquamarine.id("textures/entity/kraken/kraken.png");
	/** Só as partes que brilham (rachaduras, olhos, ventosas), desenhadas com luz própria por cima. */
	private static final Identifier GLOW_TEXTURE = Aquamarine.id("textures/entity/kraken/kraken_glow.png");
	/**
	 * Escala do modelo sobre a escala da entidade. O polimento de tamanho (24 x 16 blocos) muda só isto;
	 * as partículas do Kraken (KrakenEffects) usam o mesmo valor e acompanham sozinhas.
	 */
	public static final float MODEL_SCALE = 4.0F;
	private static final int FULL_BRIGHT = 0xF000F0;
	/** Luz mínima de bloco no corpo (0 a 15). */
	private static final int MIN_LIGHT = 9;

	public KrakenRenderer(EntityRendererProvider.Context context) {
		super(context, new KrakenModel(context.bakeLayer(KrakenModel.LAYER)), 2.0F);
		this.addLayer(new GlowLayer(this));
	}

	@Override
	public KrakenRenderState createRenderState() {
		return new KrakenRenderState();
	}

	@Override
	public void extractRenderState(T entity, KrakenRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		if (entity instanceof KrakenView view) {
			copy(view.idleAnimation(), state.idle);
			copy(view.swimAnimation(), state.swim);
			copy(view.emergeAnimation(), state.emerge);
			copy(view.stunnedAnimation(), state.stunned);
			copy(view.phaseChangeAnimation(), state.phaseChange);
			copy(view.deathAnimation(), state.death);
			copy(view.slamAnimation(), state.slam);
			copy(view.grabAnimation(), state.grab);
			copy(view.inkAnimation(), state.ink);
			copy(view.whirlpoolAnimation(), state.whirlpool);
			copy(view.spawnYoungAnimation(), state.spawnYoung);
			copy(view.waterJetAnimation(), state.waterJet);
			copy(view.roarAnimation(), state.roar);
			copy(view.boltsAnimation(), state.bolts);
			copy(view.ambushAnimation(), state.ambush);
			copy(view.doubleGrabAnimation(), state.doubleGrab);
			state.phase = view.phase();
			state.shieldFraction = view.shieldFraction();
			state.speed = (float) Math.sqrt(Mth.square(entity.getX() - entity.xo) + Mth.square(entity.getZ() - entity.zo));
		}
	}

	@Override
	protected void scale(KrakenRenderState state, PoseStack poseStack) {
		poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
		// O modelo tem os olhos e o bico em +z; no jogo a frente da entidade é -z. Gira para encarar o alvo.
		poseStack.rotateDegrees(Axis.YP, 180.0F);
	}

	/** Bioluminescência fraca: no fundo da Cratera a luz do céu some, e o corpo não pode virar uma mancha preta. */
	@Override
	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return Math.max(MIN_LIGHT, super.getBlockLightLevel(entity, pos));
	}

	@Override
	public Identifier getTextureLocation(KrakenRenderState state) {
		return TEXTURE;
	}

	private static void copy(AnimationState from, AnimationState to) {
		to.copyFrom(from);
	}

	/**
	 * Camada que brilha no escuro da água: rachaduras (só as visíveis pelo escudo), olhos e ventosas.
	 * Ciano-gelo nas fases 1 e 2; na fase 3 pulsa em roxo bioluminescente (docs/02-kraken.md).
	 */
	private static final class GlowLayer extends RenderLayer<KrakenRenderState, KrakenModel> {
		private static final RenderType GLOW = RenderTypes.eyes(GLOW_TEXTURE);

		GlowLayer(RenderLayerParent<KrakenRenderState, KrakenModel> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, KrakenRenderState state,
			float yRot, float xRot) {
			collector.order(1).submitModel(this.getParentModel(), state, poseStack, GLOW, FULL_BRIGHT,
				OverlayTexture.NO_OVERLAY, color(state));
		}

		private static int color(KrakenRenderState state) {
			float pulse = 0.5F + 0.5F * Mth.sin(state.ageInTicks * (state.phase >= 3 ? 0.22F : 0.08F));
			if (state.phase >= 3) {
				int r = (int) Mth.lerp(pulse, 150, 230);
				int g = (int) Mth.lerp(pulse, 90, 170);
				return 0xFF000000 | r << 16 | g << 8 | 255;
			}
			int v = (int) Mth.lerp(pulse, 200, 255);
			return 0xFF000000 | v << 16 | 255 << 8 | 255;
		}
	}
}
