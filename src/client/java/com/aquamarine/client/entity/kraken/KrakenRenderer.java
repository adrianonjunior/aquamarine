package com.aquamarine.client.entity.kraken;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.boss.KrakenStats;
import com.aquamarine.entity.boss.KrakenView;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.function.Function;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
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
import net.minecraft.world.phys.AABB;

/**
 * Renderer dos Krakens. Desde a Fase 6 o desenho vem de uma {@link Variant}: a v3a (Lula-colosso) é o
 * {@code aquamarine:kraken}; a v3b (Vampiro-abissal) e a v3c (Amonita-colosso) são {@code kraken_vampire} e
 * {@code kraken_ammonite}, com a mesma luta. A v2 continua aqui para referência, sem ID no jogo.
 */
public final class KrakenRenderer<T extends Mob> extends MobRenderer<T, KrakenRenderState, EntityModel<KrakenRenderState>> {
	/** Modelo, textura (e a camada que brilha, com luz própria), descida até os pés da entidade e se o modelo olha para +z. */
	public enum Variant {
		V2("kraken", KrakenModel.LAYER, KrakenModel::new, KrakenStats.MODEL_DROP, true),
		V3A("v3a", KrakenV3aModel.LAYER, KrakenV3aModel::new, 1.48F, false),
		V3B("v3b", KrakenV3bModel.LAYER, KrakenV3bModel::new, -4.34F, false),
		V3C("v3c", KrakenV3cModel.LAYER, KrakenV3cModel::new, -3.78F, false);

		final Identifier texture;
		final Identifier glow;
		final ModelLayerLocation layer;
		final Function<ModelPart, ? extends EntityModel<KrakenRenderState>> factory;
		/** Descida do modelo (gerada por tools/models/kraken_v3/build_game.py: a ponta mais baixa fica nos pés). */
		final float drop;
		final boolean facesPlusZ;

		Variant(String name, ModelLayerLocation layer, Function<ModelPart, ? extends EntityModel<KrakenRenderState>> factory, float drop,
			boolean facesPlusZ) {
			this.texture = Aquamarine.id("textures/entity/kraken/" + name + ".png");
			this.glow = Aquamarine.id("textures/entity/kraken/" + name + "_glow.png");
			this.layer = layer;
			this.factory = factory;
			this.drop = drop;
			this.facesPlusZ = facesPlusZ;
		}
	}

	private final Variant variant;
	/**
	 * Escala do modelo sobre a escala da entidade ({@link KrakenStats#MODEL_SCALE}); as partículas do Kraken
	 * (KrakenEffects) e os Nós de Maré usam o mesmo valor e acompanham sozinhos.
	 */
	public static final float MODEL_SCALE = KrakenStats.MODEL_SCALE;
	/** Folga da caixa de corte: braços erguidos passam bem acima da hitbox e não podem sumir da tela. */
	private static final double CULLING_MARGIN = 14.0;
	private static final int FULL_BRIGHT = 0xF000F0;
	/** Luz mínima de bloco no corpo (0 a 15). */
	private static final int MIN_LIGHT = 9;

	public KrakenRenderer(EntityRendererProvider.Context context, Variant variant) {
		super(context, variant.factory.apply(context.bakeLayer(variant.layer)), 2.0F);
		this.variant = variant;
		this.addLayer(new GlowLayer(this, RenderTypes.eyes(variant.glow)));
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
		// O eixo y aqui já está invertido: descer é y positivo. Ponta dos braços nos pés da entidade.
		poseStack.translate(0.0F, variant.drop, 0.0F);
		poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
		// A v2 tem os olhos e o bico em +z; no jogo a frente da entidade é -z. Gira para encarar o alvo.
		if (variant.facesPlusZ) {
			poseStack.rotateDegrees(Axis.YP, 180.0F);
		}
	}

	/** Bioluminescência fraca: no fundo da Cratera a luz do céu some, e o corpo não pode virar uma mancha preta. */
	@Override
	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return Math.max(MIN_LIGHT, super.getBlockLightLevel(entity, pos));
	}

	@Override
	protected AABB getBoundingBoxForCulling(T entity, float partialTicks) {
		return super.getBoundingBoxForCulling(entity, partialTicks).inflate(CULLING_MARGIN);
	}

	@Override
	public Identifier getTextureLocation(KrakenRenderState state) {
		return variant.texture;
	}

	private static void copy(AnimationState from, AnimationState to) {
		to.copyFrom(from);
	}

	/**
	 * Camada que brilha no escuro da água: rachaduras (só as visíveis pelo escudo), olhos e ventosas.
	 * Ciano-gelo nas fases 1 e 2; na fase 3 pulsa em roxo bioluminescente (docs/02-kraken.md).
	 */
	private static final class GlowLayer extends RenderLayer<KrakenRenderState, EntityModel<KrakenRenderState>> {
		private final RenderType glow;

		GlowLayer(RenderLayerParent<KrakenRenderState, EntityModel<KrakenRenderState>> parent, RenderType glow) {
			super(parent);
			this.glow = glow;
		}

		@Override
		public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, KrakenRenderState state,
			float yRot, float xRot) {
			collector.order(1).submitModel(this.getParentModel(), state, poseStack, glow, FULL_BRIGHT,
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
