package com.aquamarine.client.machine;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** Clipes de Pulmão de Ferro. GERADO por tools/models/machines/iron_lung/build_model.py: não edite à mão. */
public final class IronLungAnimations {
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
		clips.put("level", level());
		return clips;
	}

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.3333F, rot(0F, 0F, 0.125F), LINEAR),
				k(0.6667F, rot(0F, 0F, 0.2165F), LINEAR),
				k(1F, rot(0F, 0F, 0.25F), LINEAR),
				k(1.3333F, rot(0F, 0F, 0.2165F), LINEAR),
				k(1.6667F, rot(0F, 0F, 0.125F), LINEAR),
				k(2F, rot(0F, 0F, 0F), LINEAR),
				k(2.3333F, rot(0F, 0F, -0.125F), LINEAR),
				k(2.6667F, rot(0F, 0F, -0.2165F), LINEAR),
				k(3F, rot(0F, 0F, -0.25F), LINEAR),
				k(3.3333F, rot(0F, 0F, -0.2165F), LINEAR),
				k(3.6667F, rot(0F, 0F, -0.125F), LINEAR),
				k(4F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("bellows", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), LINEAR),
				k(0.3333F, scale(1.01F, 1F, 1F), LINEAR),
				k(0.6667F, scale(1.0173F, 1F, 1F), LINEAR),
				k(1F, scale(1.02F, 1F, 1F), LINEAR),
				k(1.3333F, scale(1.0173F, 1F, 1F), LINEAR),
				k(1.6667F, scale(1.01F, 1F, 1F), LINEAR),
				k(2F, scale(1F, 1F, 1F), LINEAR),
				k(2.3333F, scale(0.99F, 1F, 1F), LINEAR),
				k(2.6667F, scale(0.9827F, 1F, 1F), LINEAR),
				k(3F, scale(0.98F, 1F, 1F), LINEAR),
				k(3.3333F, scale(0.9827F, 1F, 1F), LINEAR),
				k(3.6667F, scale(0.99F, 1F, 1F), LINEAR),
				k(4F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("bellows_plate", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(0.3333F, pos(0.04F, 0F, 0F), LINEAR),
				k(0.6667F, pos(0.0693F, 0F, 0F), LINEAR),
				k(1F, pos(0.08F, 0F, 0F), LINEAR),
				k(1.3333F, pos(0.0693F, 0F, 0F), LINEAR),
				k(1.6667F, pos(0.04F, 0F, 0F), LINEAR),
				k(2F, pos(0F, 0F, 0F), LINEAR),
				k(2.3333F, pos(-0.04F, 0F, 0F), LINEAR),
				k(2.6667F, pos(-0.0693F, 0F, 0F), LINEAR),
				k(3F, pos(-0.08F, 0F, 0F), LINEAR),
				k(3.3333F, pos(-0.0693F, 0F, 0F), LINEAR),
				k(3.6667F, pos(-0.04F, 0F, 0F), LINEAR),
				k(4F, pos(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition run() {
		return AnimationDefinition.Builder.withLength(2.4F).looping()
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 95F), LINEAR),
				k(0.2F, rot(0F, 0F, 97F), LINEAR),
				k(0.4F, rot(0F, 0F, 98.4641F), LINEAR),
				k(0.6F, rot(0F, 0F, 99F), LINEAR),
				k(0.8F, rot(0F, 0F, 98.4641F), LINEAR),
				k(1F, rot(0F, 0F, 97F), LINEAR),
				k(1.2F, rot(0F, 0F, 95F), LINEAR),
				k(1.4F, rot(0F, 0F, 93F), LINEAR),
				k(1.6F, rot(0F, 0F, 91.5359F), LINEAR),
				k(1.8F, rot(0F, 0F, 91F), LINEAR),
				k(2F, rot(0F, 0F, 91.5359F), LINEAR),
				k(2.2F, rot(0F, 0F, 93F), LINEAR),
				k(2.4F, rot(0F, 0F, 95F), LINEAR)))
			.addAnimation("bellows", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), LINEAR),
				k(0.2F, scale(1.04F, 1F, 1F), LINEAR),
				k(0.4F, scale(1.0693F, 1F, 1F), LINEAR),
				k(0.6F, scale(1.08F, 1F, 1F), LINEAR),
				k(0.8F, scale(1.0693F, 1F, 1F), LINEAR),
				k(1F, scale(1.04F, 1F, 1F), LINEAR),
				k(1.2F, scale(1F, 1F, 1F), LINEAR),
				k(1.4F, scale(0.96F, 1F, 1F), LINEAR),
				k(1.6F, scale(0.9307F, 1F, 1F), LINEAR),
				k(1.8F, scale(0.92F, 1F, 1F), LINEAR),
				k(2F, scale(0.9307F, 1F, 1F), LINEAR),
				k(2.2F, scale(0.96F, 1F, 1F), LINEAR),
				k(2.4F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("bellows_plate", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(0.2F, pos(0.16F, 0F, 0F), LINEAR),
				k(0.4F, pos(0.2771F, 0F, 0F), LINEAR),
				k(0.6F, pos(0.32F, 0F, 0F), LINEAR),
				k(0.8F, pos(0.2771F, 0F, 0F), LINEAR),
				k(1F, pos(0.16F, 0F, 0F), LINEAR),
				k(1.2F, pos(0F, 0F, 0F), LINEAR),
				k(1.4F, pos(-0.16F, 0F, 0F), LINEAR),
				k(1.6F, pos(-0.2771F, 0F, 0F), LINEAR),
				k(1.8F, pos(-0.32F, 0F, 0F), LINEAR),
				k(2F, pos(-0.2771F, 0F, 0F), LINEAR),
				k(2.2F, pos(-0.16F, 0F, 0F), LINEAR),
				k(2.4F, pos(0F, 0F, 0F), LINEAR)))
			.addAnimation("float", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(0.2F, pos(0F, 0.075F, 0F), LINEAR),
				k(0.4F, pos(0F, 0.1299F, 0F), LINEAR),
				k(0.6F, pos(0F, 0.15F, 0F), LINEAR),
				k(0.8F, pos(0F, 0.1299F, 0F), LINEAR),
				k(1F, pos(0F, 0.075F, 0F), LINEAR),
				k(1.2F, pos(0F, 0F, 0F), LINEAR),
				k(1.4F, pos(0F, -0.075F, 0F), LINEAR),
				k(1.6F, pos(0F, -0.1299F, 0F), LINEAR),
				k(1.8F, pos(0F, -0.15F, 0F), LINEAR),
				k(2F, pos(0F, -0.1299F, 0F), LINEAR),
				k(2.2F, pos(0F, -0.075F, 0F), LINEAR),
				k(2.4F, pos(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition start() {
		return AnimationDefinition.Builder.withLength(1.6F)
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.8F, rot(0F, 0F, 40F), SMOOTH),
				k(1.6F, rot(0F, 0F, 95F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition stop() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 95F), SMOOTH),
				k(0.6F, rot(0F, 0F, 10F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition full() {
		return AnimationDefinition.Builder.withLength(1.2F).looping()
			.addAnimation("relief_cap", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(0.1F, pos(0F, 0.6F, 0F), LINEAR),
				k(0.25F, pos(0F, 0.35F, 0F), LINEAR),
				k(0.4F, pos(0F, 0.7F, 0F), LINEAR),
				k(0.6F, pos(0F, 0.4F, 0F), LINEAR),
				k(0.8F, pos(0F, 0.65F, 0F), LINEAR),
				k(1.2F, pos(0F, 0F, 0F), LINEAR)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 172F), LINEAR),
				k(0.1F, rot(0F, 0F, 172.75F), LINEAR),
				k(0.2F, rot(0F, 0F, 173.299F), LINEAR),
				k(0.3F, rot(0F, 0F, 173.5F), LINEAR),
				k(0.4F, rot(0F, 0F, 173.299F), LINEAR),
				k(0.5F, rot(0F, 0F, 172.75F), LINEAR),
				k(0.6F, rot(0F, 0F, 172F), LINEAR),
				k(0.7F, rot(0F, 0F, 171.25F), LINEAR),
				k(0.8F, rot(0F, 0F, 170.701F), LINEAR),
				k(0.9F, rot(0F, 0F, 170.5F), LINEAR),
				k(1F, rot(0F, 0F, 170.701F), LINEAR),
				k(1.1F, rot(0F, 0F, 171.25F), LINEAR),
				k(1.2F, rot(0F, 0F, 172F), LINEAR)))
			.addAnimation("bellows", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), LINEAR),
				k(0.1F, scale(1.0075F, 1F, 1F), LINEAR),
				k(0.2F, scale(1.013F, 1F, 1F), LINEAR),
				k(0.3F, scale(1.015F, 1F, 1F), LINEAR),
				k(0.4F, scale(1.013F, 1F, 1F), LINEAR),
				k(0.5F, scale(1.0075F, 1F, 1F), LINEAR),
				k(0.6F, scale(1F, 1F, 1F), LINEAR),
				k(0.7F, scale(0.9925F, 1F, 1F), LINEAR),
				k(0.8F, scale(0.987F, 1F, 1F), LINEAR),
				k(0.9F, scale(0.985F, 1F, 1F), LINEAR),
				k(1F, scale(0.987F, 1F, 1F), LINEAR),
				k(1.1F, scale(0.9925F, 1F, 1F), LINEAR),
				k(1.2F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("bellows_plate", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(0.1F, pos(0.03F, 0F, 0F), LINEAR),
				k(0.2F, pos(0.052F, 0F, 0F), LINEAR),
				k(0.3F, pos(0.06F, 0F, 0F), LINEAR),
				k(0.4F, pos(0.052F, 0F, 0F), LINEAR),
				k(0.5F, pos(0.03F, 0F, 0F), LINEAR),
				k(0.6F, pos(0F, 0F, 0F), LINEAR),
				k(0.7F, pos(-0.03F, 0F, 0F), LINEAR),
				k(0.8F, pos(-0.052F, 0F, 0F), LINEAR),
				k(0.9F, pos(-0.06F, 0F, 0F), LINEAR),
				k(1F, pos(-0.052F, 0F, 0F), LINEAR),
				k(1.1F, pos(-0.03F, 0F, 0F), LINEAR),
				k(1.2F, pos(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition level() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("bellows", new AnimationChannel(SCALE,
				k(0F, scale(0.5F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("bellows_plate", new AnimationChannel(POS,
				k(0F, pos(-2F, 0F, 0F), LINEAR),
				k(1F, pos(0F, 0F, 0F), LINEAR)))
			.addAnimation("float", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, 4.5F, 0F), LINEAR)))
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

	private IronLungAnimations() {}
}
