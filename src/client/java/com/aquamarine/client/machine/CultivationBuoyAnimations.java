package com.aquamarine.client.machine;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** Clipes de Boia de Cultivo. GERADO por tools/models/machines/cultivation_buoy/build_model.py: não edite à mão. */
public final class CultivationBuoyAnimations {
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
		clips.put("start", start());
		clips.put("run", run());
		clips.put("full", full());
		clips.put("stop", stop());
		clips.put("harvest", harvest());
		return clips;
	}

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("amber", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("flag", new AnimationChannel(POS,
				k(0F, pos(0F, -1.5F, 0F), SMOOTH),
				k(4F, pos(0F, -1.5F, 0F), SMOOTH)))
			.addAnimation("root", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.4243F, 0F), SMOOTH),
				k(1F, pos(0F, 0.6F, 0F), SMOOTH),
				k(1.5F, pos(0F, 0.4243F, 0F), SMOOTH),
				k(2F, pos(0F, 0F, 0F), SMOOTH),
				k(2.5F, pos(0F, -0.4243F, 0F), SMOOTH),
				k(3F, pos(0F, -0.6F, 0F), SMOOTH),
				k(3.5F, pos(0F, -0.4243F, 0F), SMOOTH),
				k(4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("root", new AnimationChannel(ROT,
				k(0F, rot(0.2044F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0.4865F, 0F, 1.0607F), SMOOTH),
				k(1F, rot(0.4836F, 0F, 1.5F), SMOOTH),
				k(1.5F, rot(0.1974F, 0F, 1.0607F), SMOOTH),
				k(2F, rot(-0.2044F, 0F, 0F), SMOOTH),
				k(2.5F, rot(-0.4865F, 0F, -1.0607F), SMOOTH),
				k(3F, rot(-0.4836F, 0F, -1.5F), SMOOTH),
				k(3.5F, rot(-0.1974F, 0F, -1.0607F), SMOOTH),
				k(4F, rot(0.2044F, 0F, 0F), SMOOTH)))
			.addAnimation("flag", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, -5F), SMOOTH),
				k(1F, rot(0F, 0F, 3F), SMOOTH),
				k(2F, rot(0F, 0F, -5F), SMOOTH),
				k(3F, rot(0F, 0F, -11F), SMOOTH),
				k(4F, rot(0F, 0F, -5F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition start() {
		return AnimationDefinition.Builder.withLength(1.2F)
			.addAnimation("flag", new AnimationChannel(POS,
				k(0F, pos(0F, -1.5F, 0F), SMOOTH),
				k(0.35F, pos(0F, -1.2F, 0F), SMOOTH),
				k(1F, pos(0F, 0.1F, 0F), SMOOTH),
				k(1.2F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("amber", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(0.45F, scale(0F, 0F, 0F), SMOOTH),
				k(0.9F, scale(1.12F, 1.12F, 1.12F), SMOOTH),
				k(1.2F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(1.2F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("root", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.45F, pos(0F, 0.25F, 0F), SMOOTH),
				k(0.8F, pos(0F, -0.12F, 0F), SMOOTH),
				k(1.2F, pos(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition run() {
		return AnimationDefinition.Builder.withLength(2F).looping()
			.addAnimation("amber", new AnimationChannel(SCALE,
				k(0F, scale(0.96F, 0.96F, 0.96F), SMOOTH),
				k(0.5F, scale(1.05F, 1.05F, 1.05F), SMOOTH),
				k(1F, scale(0.96F, 0.96F, 0.96F), SMOOTH),
				k(1.5F, scale(0.88F, 0.88F, 0.88F), SMOOTH),
				k(2F, scale(0.96F, 0.96F, 0.96F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(2F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("flag", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(2F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("root", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.25F, pos(0F, 0.3182F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.45F, 0F), SMOOTH),
				k(0.75F, pos(0F, 0.3182F, 0F), SMOOTH),
				k(1F, pos(0F, 0F, 0F), SMOOTH),
				k(1.25F, pos(0F, -0.3182F, 0F), SMOOTH),
				k(1.5F, pos(0F, -0.45F, 0F), SMOOTH),
				k(1.75F, pos(0F, -0.3182F, 0F), SMOOTH),
				k(2F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("root", new AnimationChannel(ROT,
				k(0F, rot(0.1499F, 0F, 0F), SMOOTH),
				k(0.25F, rot(0.3568F, 0F, 0.7778F), SMOOTH),
				k(0.5F, rot(0.3546F, 0F, 1.1F), SMOOTH),
				k(0.75F, rot(0.1447F, 0F, 0.7778F), SMOOTH),
				k(1F, rot(-0.1499F, 0F, 0F), SMOOTH),
				k(1.25F, rot(-0.3568F, 0F, -0.7778F), SMOOTH),
				k(1.5F, rot(-0.3546F, 0F, -1.1F), SMOOTH),
				k(1.75F, rot(-0.1447F, 0F, -0.7778F), SMOOTH),
				k(2F, rot(0.1499F, 0F, 0F), SMOOTH)))
			.addAnimation("flag", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, -5F), SMOOTH),
				k(0.5F, rot(0F, 0F, 4F), SMOOTH),
				k(1F, rot(0F, 0F, -5F), SMOOTH),
				k(1.5F, rot(0F, 0F, -12F), SMOOTH),
				k(2F, rot(0F, 0F, -5F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition full() {
		return AnimationDefinition.Builder.withLength(2F).looping()
			.addAnimation("amber", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(2F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0.45F, 0.45F, 0.45F), SMOOTH),
				k(0.25F, scale(1.15F, 1.15F, 1.15F), SMOOTH),
				k(0.5F, scale(0.7F, 0.7F, 0.7F), SMOOTH),
				k(0.75F, scale(1F, 1F, 1F), SMOOTH),
				k(1F, scale(0.35F, 0.35F, 0.35F), SMOOTH),
				k(1.25F, scale(1.2F, 1.2F, 1.2F), SMOOTH),
				k(1.5F, scale(0.65F, 0.65F, 0.65F), SMOOTH),
				k(1.75F, scale(1.05F, 1.05F, 1.05F), SMOOTH),
				k(2F, scale(0.45F, 0.45F, 0.45F), SMOOTH)))
			.addAnimation("flag", new AnimationChannel(POS,
				k(0F, pos(0F, 1.5F, 0F), SMOOTH),
				k(2F, pos(0F, 1.5F, 0F), SMOOTH)))
			.addAnimation("root", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.25F, pos(0F, 0.5657F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.8F, 0F), SMOOTH),
				k(0.75F, pos(0F, 0.5657F, 0F), SMOOTH),
				k(1F, pos(0F, 0F, 0F), SMOOTH),
				k(1.25F, pos(0F, -0.5657F, 0F), SMOOTH),
				k(1.5F, pos(0F, -0.8F, 0F), SMOOTH),
				k(1.75F, pos(0F, -0.5657F, 0F), SMOOTH),
				k(2F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("root", new AnimationChannel(ROT,
				k(0F, rot(0.2999F, 0F, 0F), SMOOTH),
				k(0.25F, rot(0.7135F, 0F, 1.5556F), SMOOTH),
				k(0.5F, rot(0.7092F, 0F, 2.2F), SMOOTH),
				k(0.75F, rot(0.2895F, 0F, 1.5556F), SMOOTH),
				k(1F, rot(-0.2999F, 0F, 0F), SMOOTH),
				k(1.25F, rot(-0.7135F, 0F, -1.5556F), SMOOTH),
				k(1.5F, rot(-0.7092F, 0F, -2.2F), SMOOTH),
				k(1.75F, rot(-0.2895F, 0F, -1.5556F), SMOOTH),
				k(2F, rot(0.2999F, 0F, 0F), SMOOTH)))
			.addAnimation("flag", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, -8F), SMOOTH),
				k(0.25F, rot(0F, 0F, 8F), SMOOTH),
				k(0.5F, rot(0F, 0F, -12F), SMOOTH),
				k(0.75F, rot(0F, 0F, 10F), SMOOTH),
				k(1F, rot(0F, 0F, -8F), SMOOTH),
				k(1.25F, rot(0F, 0F, 8F), SMOOTH),
				k(1.5F, rot(0F, 0F, -12F), SMOOTH),
				k(1.75F, rot(0F, 0F, 10F), SMOOTH),
				k(2F, rot(0F, 0F, -8F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition stop() {
		return AnimationDefinition.Builder.withLength(1.2F)
			.addAnimation("flag", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.55F, pos(0F, -1.7F, 0F), SMOOTH),
				k(0.8F, pos(0F, -1.35F, 0F), SMOOTH),
				k(1.2F, pos(0F, -1.5F, 0F), SMOOTH)))
			.addAnimation("amber", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.5F, scale(0.75F, 0.75F, 0.75F), SMOOTH),
				k(0.9F, scale(0F, 0F, 0F), SMOOTH),
				k(1.2F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(1.2F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("root", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.45F, pos(0F, -0.15F, 0F), SMOOTH),
				k(1.2F, pos(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition harvest() {
		return AnimationDefinition.Builder.withLength(0.8F)
			.addAnimation("root", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.18F, pos(0F, -1.8F, 0F), SMOOTH),
				k(0.42F, pos(0F, -2.1F, 0F), SMOOTH),
				k(0.62F, pos(0F, 0.35F, 0F), SMOOTH),
				k(0.8F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("flag", new AnimationChannel(POS,
				k(0F, pos(0F, 1.5F, 0F), SMOOTH),
				k(0.22F, pos(0F, -1.4F, 0F), SMOOTH),
				k(0.52F, pos(0F, 0.55F, 0F), SMOOTH),
				k(0.8F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("flag", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, -8F), SMOOTH),
				k(0.22F, rot(0F, 0F, -28F), SMOOTH),
				k(0.52F, rot(0F, 0F, 12F), SMOOTH),
				k(0.8F, rot(0F, 0F, -5F), SMOOTH)))
			.addAnimation("amber", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(0.8F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(0.4F, 0.4F, 0.4F), SMOOTH),
				k(0.16F, scale(1.45F, 1.45F, 1.45F), SMOOTH),
				k(0.38F, scale(0.15F, 0.15F, 0.15F), SMOOTH),
				k(0.8F, scale(0F, 0F, 0F), SMOOTH)))
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

	private CultivationBuoyAnimations() {}
}
