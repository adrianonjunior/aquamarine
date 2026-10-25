package com.aquamarine.client.entity.moray;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.MorayView;

import com.mojang.blaze3d.vertex.PoseStack;

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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

/** Moréia Tempestuosa: corpo de 14 blocos, listras e olhos que brilham e crescem com a carga elétrica. */
public final class MorayRenderer<T extends Mob> extends MobRenderer<T, MorayRenderState, MorayModel> {
	private static final Identifier TEXTURE = Aquamarine.id("textures/entity/moray/moray.png");
	private static final Identifier GLOW_TEXTURE = Aquamarine.id("textures/entity/moray/moray_glow.png");
	private static final int FULL_BRIGHT = 0xF000F0;
	/** Luz mínima de bloco no corpo: nas fossas ela não pode virar uma mancha preta. */
	private static final int MIN_LIGHT = 7;

	public MorayRenderer(EntityRendererProvider.Context context) {
		super(context, new MorayModel(context.bakeLayer(MorayModel.LAYER)), 0.9F);
		this.addLayer(new GlowLayer(this));
	}

	@Override
	public MorayRenderState createRenderState() {
		return new MorayRenderState();
	}

	@Override
	public void extractRenderState(T entity, MorayRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		if (entity instanceof MorayView view) {
			state.idle.copyFrom(view.idleAnimation());
			state.swim.copyFrom(view.swimAnimation());
			state.strike.copyFrom(view.strikeAnimation());
			state.chain.copyFrom(view.chainAnimation());
			state.coil.copyFrom(view.coilAnimation());
			state.stunned.copyFrom(view.stunnedAnimation());
			state.death.copyFrom(view.deathAnimation());
			state.chargeFraction = view.chargeFraction();
		}
	}

	/** A entidade é só a cabeça; o corpo segue 14 blocos atrás, então a caixa de corte cobre tudo. */
	@Override
	protected AABB getBoundingBoxForCulling(T entity, float partialTick) {
		return entity.getBoundingBox().inflate(14.0);
	}

	@Override
	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return Math.max(MIN_LIGHT, super.getBlockLightLevel(entity, pos));
	}

	/** Sem a inclinação de morte do jogo: a animação death já vira a Moréia de barriga para cima. */
	@Override
	protected float getFlipDegrees() {
		return 0.0F;
	}

	@Override
	public Identifier getTextureLocation(MorayRenderState state) {
		return TEXTURE;
	}

	/** Listras, olhos e cristas em ciano elétrico; a carga deixa tudo mais forte e pisca. */
	private static final class GlowLayer extends RenderLayer<MorayRenderState, MorayModel> {
		private static final RenderType GLOW = RenderTypes.eyes(GLOW_TEXTURE);

		GlowLayer(RenderLayerParent<MorayRenderState, MorayModel> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, MorayRenderState state,
			float yRot, float xRot) {
			collector.order(1).submitModel(this.getParentModel(), state, poseStack, GLOW, FULL_BRIGHT,
				OverlayTexture.NO_OVERLAY, color(state));
		}

		private static int color(MorayRenderState state) {
			float charge = Mth.clamp(state.chargeFraction, 0.0F, 1.0F);
			float flicker = charge > 0.0F ? 0.75F + 0.25F * Mth.sin(state.ageInTicks * (1.2F + 2.0F * charge)) : 1.0F;
			float base = Mth.lerp(charge, 0.55F, 1.0F) * flicker;
			int r = (int) (Mth.lerp(charge, 120, 235) * base);
			int g = (int) (235 * base);
			int b = (int) (255 * base);
			return 0xFF000000 | r << 16 | g << 8 | b;
		}
	}
}
