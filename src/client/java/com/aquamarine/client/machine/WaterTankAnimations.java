package com.aquamarine.client.machine;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** Clipes de Tanque d'Água. GERADO por tools/models/machines/water_tank/build_model.py: não edite à mão. */
public final class WaterTankAnimations {
	private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
	private static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;
	private static final AnimationChannel.Target SCALE = AnimationChannel.Targets.SCALE;
	private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
	private static final AnimationChannel.Interpolation LINEAR = AnimationChannel.Interpolations.LINEAR;

	/** Clipes pelo nome do contrato (idle, start, run, stop, overload...). */
	public static final Map<String, AnimationDefinition> CLIPS = clips();

	private static Map<String, AnimationDefinition> clips() {
		Map<String, AnimationDefinition> clips = new LinkedHashMap<>();
		clips.put("idle", idle());
		clips.put("run", run());
		clips.put("start", start());
		clips.put("stop", stop());
		clips.put("full", full());
		clips.put("fill", fill());
		clips.put("empty", empty());
		return clips;
	}

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.3333F, rot(0F, 0F, 0.25F), LINEAR),
				k(0.6667F, rot(0F, 0F, 0.433F), LINEAR),
				k(1F, rot(0F, 0F, 0.5F), LINEAR),
				k(1.3333F, rot(0F, 0F, 0.433F), LINEAR),
				k(1.6667F, rot(0F, 0F, 0.25F), LINEAR),
				k(2F, rot(0F, 0F, 0F), LINEAR),
				k(2.3333F, rot(0F, 0F, -0.25F), LINEAR),
				k(2.6667F, rot(0F, 0F, -0.433F), LINEAR),
				k(3F, rot(0F, 0F, -0.5F), LINEAR),
				k(3.3333F, rot(0F, 0F, -0.433F), LINEAR),
				k(3.6667F, rot(0F, 0F, -0.25F), LINEAR),
				k(4F, rot(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition run() {
		return AnimationDefinition.Builder.withLength(3F).looping()
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 80F), LINEAR),
				k(0.25F, rot(0F, 0F, 81F), LINEAR),
				k(0.5F, rot(0F, 0F, 81.7321F), LINEAR),
				k(0.75F, rot(0F, 0F, 82F), LINEAR),
				k(1F, rot(0F, 0F, 81.7321F), LINEAR),
				k(1.25F, rot(0F, 0F, 81F), LINEAR),
				k(1.5F, rot(0F, 0F, 80F), LINEAR),
				k(1.75F, rot(0F, 0F, 79F), LINEAR),
				k(2F, rot(0F, 0F, 78.2679F), LINEAR),
				k(2.25F, rot(0F, 0F, 78F), LINEAR),
				k(2.5F, rot(0F, 0F, 78.2679F), LINEAR),
				k(2.75F, rot(0F, 0F, 79F), LINEAR),
				k(3F, rot(0F, 0F, 80F), LINEAR)))
			.build();
	}

	private static AnimationDefinition start() {
		return AnimationDefinition.Builder.withLength(1.5F)
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.8F, rot(0F, 0F, 88F), SMOOTH),
				k(1.5F, rot(0F, 0F, 80F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition stop() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 80F), SMOOTH),
				k(0.7F, rot(0F, 0F, -3F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition full() {
		return AnimationDefinition.Builder.withLength(3F).looping()
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 165F), LINEAR),
				k(0.25F, rot(0F, 0F, 165.15F), LINEAR),
				k(0.5F, rot(0F, 0F, 165.2598F), LINEAR),
				k(0.75F, rot(0F, 0F, 165.3F), LINEAR),
				k(1F, rot(0F, 0F, 165.2598F), LINEAR),
				k(1.25F, rot(0F, 0F, 165.15F), LINEAR),
				k(1.5F, rot(0F, 0F, 165F), LINEAR),
				k(1.75F, rot(0F, 0F, 164.85F), LINEAR),
				k(2F, rot(0F, 0F, 164.7402F), LINEAR),
				k(2.25F, rot(0F, 0F, 164.7F), LINEAR),
				k(2.5F, rot(0F, 0F, 164.7402F), LINEAR),
				k(2.75F, rot(0F, 0F, 164.85F), LINEAR),
				k(3F, rot(0F, 0F, 165F), LINEAR)))
			.build();
	}

	private static AnimationDefinition fill() {
		return AnimationDefinition.Builder.withLength(3F).looping()
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 80F), LINEAR),
				k(0.25F, rot(0F, 0F, 81F), LINEAR),
				k(0.5F, rot(0F, 0F, 81.7321F), LINEAR),
				k(0.75F, rot(0F, 0F, 82F), LINEAR),
				k(1F, rot(0F, 0F, 81.7321F), LINEAR),
				k(1.25F, rot(0F, 0F, 81F), LINEAR),
				k(1.5F, rot(0F, 0F, 80F), LINEAR),
				k(1.75F, rot(0F, 0F, 79F), LINEAR),
				k(2F, rot(0F, 0F, 78.2679F), LINEAR),
				k(2.25F, rot(0F, 0F, 78F), LINEAR),
				k(2.5F, rot(0F, 0F, 78.2679F), LINEAR),
				k(2.75F, rot(0F, 0F, 79F), LINEAR),
				k(3F, rot(0F, 0F, 80F), LINEAR)))
			.build();
	}

	private static AnimationDefinition empty() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 80F), SMOOTH),
				k(0.7F, rot(0F, 0F, -3F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static Keyframe k(float time, Vector3f vector, AnimationChannel.Interpolation interpolation) {
		return new Keyframe(time, vector, interpolation);
	}

	private static Vector3f rot(float x, float y, float z) {
		return KeyframeAnimations.degreeVec(x, y, z);
	}

	private static Vector3f pos(float x, float y, float z) {
		return KeyframeAnimations.posVec(x, y, z);
	}

	private static Vector3f scale(float x, float y, float z) {
		return KeyframeAnimations.scaleVec(x, y, z);
	}

	private WaterTankAnimations() {}
}
