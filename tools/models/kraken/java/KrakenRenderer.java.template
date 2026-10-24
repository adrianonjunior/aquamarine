package com.aquamarine.client.entity.kraken;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.boss.KrakenView;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

/** Renderer is generic so it compiles before the parallel 2A-C entity reaches main. */
public final class KrakenRenderer<T extends Mob> extends MobRenderer<T, KrakenRenderState, KrakenModel> {
	private static final Identifier TEXTURE = Aquamarine.id("textures/entity/kraken/kraken.png");

	public KrakenRenderer(EntityRendererProvider.Context context) {
		super(context, new KrakenModel(context.bakeLayer(KrakenModel.LAYER)), 2.0F);
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
		}
	}

	@Override
	public Identifier getTextureLocation(KrakenRenderState state) {
		return TEXTURE;
	}

	private static void copy(AnimationState from, AnimationState to) {
		to.copyFrom(from);
	}
}
