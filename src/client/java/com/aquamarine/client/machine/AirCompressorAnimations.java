package com.aquamarine.client.machine;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** Clipes de Compressor de Ar. GERADO por tools/models/machines/air_compressor/build_model.py: não edite à mão. */
public final class AirCompressorAnimations {
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
		return clips;
	}

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(3F).looping()
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.25F, rot(0F, 0F, 0.125F), LINEAR),
				k(0.5F, rot(0F, 0F, 0.2165F), LINEAR),
				k(0.75F, rot(0F, 0F, 0.25F), LINEAR),
				k(1F, rot(0F, 0F, 0.2165F), LINEAR),
				k(1.25F, rot(0F, 0F, 0.125F), LINEAR),
				k(1.5F, rot(0F, 0F, 0F), LINEAR),
				k(1.75F, rot(0F, 0F, -0.125F), LINEAR),
				k(2F, rot(0F, 0F, -0.2165F), LINEAR),
				k(2.25F, rot(0F, 0F, -0.25F), LINEAR),
				k(2.5F, rot(0F, 0F, -0.2165F), LINEAR),
				k(2.75F, rot(0F, 0F, -0.125F), LINEAR),
				k(3F, rot(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition run() {
		return AnimationDefinition.Builder.withLength(0.8F).looping()
			.addAnimation("piston", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(0.0667F, pos(0F, 0.375F, 0F), LINEAR),
				k(0.1333F, pos(0F, 0.6495F, 0F), LINEAR),
				k(0.2F, pos(0F, 0.75F, 0F), LINEAR),
				k(0.2667F, pos(0F, 0.6495F, 0F), LINEAR),
				k(0.3333F, pos(0F, 0.375F, 0F), LINEAR),
				k(0.4F, pos(0F, 0F, 0F), LINEAR),
				k(0.4667F, pos(0F, -0.375F, 0F), LINEAR),
				k(0.5333F, pos(0F, -0.6495F, 0F), LINEAR),
				k(0.6F, pos(0F, -0.75F, 0F), LINEAR),
				k(0.6667F, pos(0F, -0.6495F, 0F), LINEAR),
				k(0.7333F, pos(0F, -0.375F, 0F), LINEAR),
				k(0.8F, pos(0F, 0F, 0F), LINEAR)))
			.addAnimation("flywheel", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.8F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 105F), LINEAR),
				k(0.0667F, rot(0F, 0F, 106.5F), LINEAR),
				k(0.1333F, rot(0F, 0F, 107.5981F), LINEAR),
				k(0.2F, rot(0F, 0F, 108F), LINEAR),
				k(0.2667F, rot(0F, 0F, 107.5981F), LINEAR),
				k(0.3333F, rot(0F, 0F, 106.5F), LINEAR),
				k(0.4F, rot(0F, 0F, 105F), LINEAR),
				k(0.4667F, rot(0F, 0F, 103.5F), LINEAR),
				k(0.5333F, rot(0F, 0F, 102.4019F), LINEAR),
				k(0.6F, rot(0F, 0F, 102F), LINEAR),
				k(0.6667F, rot(0F, 0F, 102.4019F), LINEAR),
				k(0.7333F, rot(0F, 0F, 103.5F), LINEAR),
				k(0.8F, rot(0F, 0F, 105F), LINEAR)))
			.addAnimation("rod_link", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.0667F, rot(0F, 0F, 5F), LINEAR),
				k(0.1333F, rot(0F, 0F, 8.6603F), LINEAR),
				k(0.2F, rot(0F, 0F, 10F), LINEAR),
				k(0.2667F, rot(0F, 0F, 8.6603F), LINEAR),
				k(0.3333F, rot(0F, 0F, 5F), LINEAR),
				k(0.4F, rot(0F, 0F, 0F), LINEAR),
				k(0.4667F, rot(0F, 0F, -5F), LINEAR),
				k(0.5333F, rot(0F, 0F, -8.6603F), LINEAR),
				k(0.6F, rot(0F, 0F, -10F), LINEAR),
				k(0.6667F, rot(0F, 0F, -8.6603F), LINEAR),
				k(0.7333F, rot(0F, 0F, -5F), LINEAR),
				k(0.8F, rot(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition start() {
		return AnimationDefinition.Builder.withLength(1.6F)
			.addAnimation("flywheel", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0F, 0F, 15F), SMOOTH),
				k(1F, rot(0F, 0F, 130F), SMOOTH),
				k(1.6F, rot(0F, 0F, 360F), SMOOTH)))
			.addAnimation("piston", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0F, 0F), SMOOTH),
				k(1F, pos(0F, -0.5F, 0F), SMOOTH),
				k(1.35F, pos(0F, 0.4F, 0F), SMOOTH),
				k(1.6F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.8F, rot(0F, 0F, 20F), SMOOTH),
				k(1.3F, rot(0F, 0F, 112F), SMOOTH),
				k(1.6F, rot(0F, 0F, 105F), SMOOTH)))
			.addAnimation("rod_link", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.1333F, rot(0F, 0F, 5F), LINEAR),
				k(0.2667F, rot(0F, 0F, 8.6603F), LINEAR),
				k(0.4F, rot(0F, 0F, 10F), LINEAR),
				k(0.5333F, rot(0F, 0F, 8.6603F), LINEAR),
				k(0.6667F, rot(0F, 0F, 5F), LINEAR),
				k(0.8F, rot(0F, 0F, 0F), LINEAR),
				k(0.9333F, rot(0F, 0F, -5F), LINEAR),
				k(1.0667F, rot(0F, 0F, -8.6603F), LINEAR),
				k(1.2F, rot(0F, 0F, -10F), LINEAR),
				k(1.3333F, rot(0F, 0F, -8.6603F), LINEAR),
				k(1.4667F, rot(0F, 0F, -5F), LINEAR),
				k(1.6F, rot(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition stop() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("flywheel", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.25F, rot(0F, 0F, 75F), SMOOTH),
				k(0.6F, rot(0F, 0F, 140F), SMOOTH),
				k(1F, rot(0F, 0F, 180F), SMOOTH)))
			.addAnimation("piston", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.3F, pos(0F, 0.3F, 0F), SMOOTH),
				k(0.7F, pos(0F, -0.1F, 0F), SMOOTH),
				k(1F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 105F), SMOOTH),
				k(0.4F, rot(0F, 0F, 65F), SMOOTH),
				k(0.85F, rot(0F, 0F, -4F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("rod_link", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.0833F, rot(0F, 0F, 5F), LINEAR),
				k(0.1667F, rot(0F, 0F, 8.6603F), LINEAR),
				k(0.25F, rot(0F, 0F, 10F), LINEAR),
				k(0.3333F, rot(0F, 0F, 8.6603F), LINEAR),
				k(0.4167F, rot(0F, 0F, 5F), LINEAR),
				k(0.5F, rot(0F, 0F, 0F), LINEAR),
				k(0.5833F, rot(0F, 0F, -5F), LINEAR),
				k(0.6667F, rot(0F, 0F, -8.6603F), LINEAR),
				k(0.75F, rot(0F, 0F, -10F), LINEAR),
				k(0.8333F, rot(0F, 0F, -8.6603F), LINEAR),
				k(0.9167F, rot(0F, 0F, -5F), LINEAR),
				k(1F, rot(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition full() {
		return AnimationDefinition.Builder.withLength(3F).looping()
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 170F), LINEAR),
				k(0.25F, rot(0F, 0F, 170.5F), LINEAR),
				k(0.5F, rot(0F, 0F, 170.866F), LINEAR),
				k(0.75F, rot(0F, 0F, 171F), LINEAR),
				k(1F, rot(0F, 0F, 170.866F), LINEAR),
				k(1.25F, rot(0F, 0F, 170.5F), LINEAR),
				k(1.5F, rot(0F, 0F, 170F), LINEAR),
				k(1.75F, rot(0F, 0F, 169.5F), LINEAR),
				k(2F, rot(0F, 0F, 169.134F), LINEAR),
				k(2.25F, rot(0F, 0F, 169F), LINEAR),
				k(2.5F, rot(0F, 0F, 169.134F), LINEAR),
				k(2.75F, rot(0F, 0F, 169.5F), LINEAR),
				k(3F, rot(0F, 0F, 170F), LINEAR)))
			.addAnimation("relief_cap", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(0.2F, pos(0F, 0.5F, 0F), LINEAR),
				k(0.35F, pos(0F, 0.3F, 0F), LINEAR),
				k(0.6F, pos(0F, 0.55F, 0F), LINEAR),
				k(1.2F, pos(0F, 0F, 0F), LINEAR),
				k(3F, pos(0F, 0F, 0F), LINEAR)))
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

	private AirCompressorAnimations() {}
}
