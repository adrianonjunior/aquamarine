package com.aquamarine.client.machine;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** Clipes de Garrafão de Cultivo. GERADO por tools/models/machines/cultivation_jar/build_model.py: não edite à mão. */
public final class CultivationJarAnimations {
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
		clips.put("ready", ready());
		clips.put("harvest", harvest());
		return clips;
	}

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-8F, 0F, 0F), SMOOTH),
				k(2F, rot(-11F, 0F, 0F), SMOOTH),
				k(4F, rot(-8F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0.1F, 0.1F, 0.1F), SMOOTH),
				k(4F, scale(0.1F, 0.1F, 0.1F), SMOOTH)))
			.addAnimation("bubble_0", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_1", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_2", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition run() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-20F, 0F, 0F), SMOOTH),
				k(2F, rot(-25F, 0F, 0F), SMOOTH),
				k(4F, rot(-20F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0.15F, 0.15F, 0.15F), SMOOTH),
				k(2F, scale(0.2F, 0.2F, 0.2F), SMOOTH),
				k(4F, scale(0.15F, 0.15F, 0.15F), SMOOTH)))
			.addAnimation("bubble_0", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(1F, pos(0F, 2F, 0F), SMOOTH),
				k(2.5F, pos(0F, 4F, 0F), SMOOTH),
				k(4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_0", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(1F, scale(1F, 1F, 1F), SMOOTH),
				k(2.7F, scale(1F, 1F, 1F), SMOOTH),
				k(3.1F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_1", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(1.3F, pos(0F, 2F, 0F), SMOOTH),
				k(2.8F, pos(0F, 4F, 0F), SMOOTH),
				k(4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_1", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(1F, scale(1F, 1F, 1F), SMOOTH),
				k(2.7F, scale(1F, 1F, 1F), SMOOTH),
				k(3.1F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_2", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(1.6F, pos(0F, 2F, 0F), SMOOTH),
				k(3.1F, pos(0F, 4F, 0F), SMOOTH),
				k(4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_2", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(1F, scale(1F, 1F, 1F), SMOOTH),
				k(2.7F, scale(1F, 1F, 1F), SMOOTH),
				k(3.1F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition start() {
		return AnimationDefinition.Builder.withLength(1.5F)
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-8F, 0F, 0F), SMOOTH),
				k(0.6F, rot(-12F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-20F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0.1F, 0.1F, 0.1F), SMOOTH),
				k(1.5F, scale(0.15F, 0.15F, 0.15F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition stop() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-20F, 0F, 0F), SMOOTH),
				k(0.7F, rot(-7F, 0F, 0F), SMOOTH),
				k(1F, rot(-8F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0.15F, 0.15F, 0.15F), SMOOTH),
				k(1F, scale(0.1F, 0.1F, 0.1F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition full() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-30F, 0F, 0F), SMOOTH),
				k(2F, rot(-33F, 0F, 0F), SMOOTH),
				k(4F, rot(-30F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(2F, scale(1.08F, 1.08F, 1.08F), SMOOTH),
				k(4F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 15F, 0F), SMOOTH),
				k(4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_0", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_1", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_2", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition ready() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-30F, 0F, 0F), SMOOTH),
				k(2F, rot(-33F, 0F, 0F), SMOOTH),
				k(4F, rot(-30F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(2F, scale(1.08F, 1.08F, 1.08F), SMOOTH),
				k(4F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 15F, 0F), SMOOTH),
				k(4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_0", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_1", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("bubble_2", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition harvest() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.4F, scale(1.15F, 1.15F, 1.15F), SMOOTH),
				k(0.65F, scale(0.1F, 0.1F, 0.1F), SMOOTH),
				k(1F, scale(0.1F, 0.1F, 0.1F), SMOOTH)))
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-30F, 0F, 0F), SMOOTH),
				k(1F, rot(-20F, 0F, 0F), SMOOTH)))
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

	private CultivationJarAnimations() {}
}
