package com.aquamarine.client.machine;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** Clipes de Corda de Cultivo. GERADO por tools/models/machines/cultivation_rope/build_model.py: não edite à mão. */
public final class CultivationRopeAnimations {
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
		return clips;
	}

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("wave_0", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.0552F, 0F), SMOOTH),
				k(1F, pos(0F, 0.078F, 0F), SMOOTH),
				k(1.5F, pos(0F, 0.0552F, 0F), SMOOTH),
				k(2F, pos(0F, 0F, 0F), SMOOTH),
				k(2.5F, pos(0F, -0.0552F, 0F), SMOOTH),
				k(3F, pos(0F, -0.078F, 0F), SMOOTH),
				k(3.5F, pos(0F, -0.0552F, 0F), SMOOTH),
				k(4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("wave_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0F, 0F, 0.2069F), SMOOTH),
				k(1F, rot(0F, 0F, 0.2926F), SMOOTH),
				k(1.5F, rot(0F, 0F, 0.2069F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH),
				k(2.5F, rot(0F, 0F, -0.2069F), SMOOTH),
				k(3F, rot(0F, 0F, -0.2926F), SMOOTH),
				k(3.5F, rot(0F, 0F, -0.2069F), SMOOTH),
				k(4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("wave_1", new AnimationChannel(POS,
				k(0F, pos(0F, 0.085F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.2053F, 0F), SMOOTH),
				k(1F, pos(0F, 0.2053F, 0F), SMOOTH),
				k(1.5F, pos(0F, 0.085F, 0F), SMOOTH),
				k(2F, pos(0F, -0.085F, 0F), SMOOTH),
				k(2.5F, pos(0F, -0.2053F, 0F), SMOOTH),
				k(3F, pos(0F, -0.2053F, 0F), SMOOTH),
				k(3.5F, pos(0F, -0.085F, 0F), SMOOTH),
				k(4F, pos(0F, 0.085F, 0F), SMOOTH)))
			.addAnimation("wave_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0.3189F), SMOOTH),
				k(0.5F, rot(0F, 0F, 0.7699F), SMOOTH),
				k(1F, rot(0F, 0F, 0.7699F), SMOOTH),
				k(1.5F, rot(0F, 0F, 0.3189F), SMOOTH),
				k(2F, rot(0F, 0F, -0.3189F), SMOOTH),
				k(2.5F, rot(0F, 0F, -0.7699F), SMOOTH),
				k(3F, rot(0F, 0F, -0.7699F), SMOOTH),
				k(3.5F, rot(0F, 0F, -0.3189F), SMOOTH),
				k(4F, rot(0F, 0F, 0.3189F), SMOOTH)))
			.addAnimation("wave_2", new AnimationChannel(POS,
				k(0F, pos(0F, 0.2352F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.3326F, 0F), SMOOTH),
				k(1F, pos(0F, 0.2352F, 0F), SMOOTH),
				k(1.5F, pos(0F, 0F, 0F), SMOOTH),
				k(2F, pos(0F, -0.2352F, 0F), SMOOTH),
				k(2.5F, pos(0F, -0.3326F, 0F), SMOOTH),
				k(3F, pos(0F, -0.2352F, 0F), SMOOTH),
				k(3.5F, pos(0F, 0F, 0F), SMOOTH),
				k(4F, pos(0F, 0.2352F, 0F), SMOOTH)))
			.addAnimation("wave_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0.8819F), SMOOTH),
				k(0.5F, rot(0F, 0F, 1.2472F), SMOOTH),
				k(1F, rot(0F, 0F, 0.8819F), SMOOTH),
				k(1.5F, rot(0F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 0F, -0.8819F), SMOOTH),
				k(2.5F, rot(0F, 0F, -1.2472F), SMOOTH),
				k(3F, rot(0F, 0F, -0.8819F), SMOOTH),
				k(3.5F, rot(0F, 0F, 0F), SMOOTH),
				k(4F, rot(0F, 0F, 0.8819F), SMOOTH)))
			.addAnimation("wave_3", new AnimationChannel(POS,
				k(0F, pos(0F, 0.3625F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.3625F, 0F), SMOOTH),
				k(1F, pos(0F, 0.1501F, 0F), SMOOTH),
				k(1.5F, pos(0F, -0.1501F, 0F), SMOOTH),
				k(2F, pos(0F, -0.3625F, 0F), SMOOTH),
				k(2.5F, pos(0F, -0.3625F, 0F), SMOOTH),
				k(3F, pos(0F, -0.1501F, 0F), SMOOTH),
				k(3.5F, pos(0F, 0.1501F, 0F), SMOOTH),
				k(4F, pos(0F, 0.3625F, 0F), SMOOTH)))
			.addAnimation("wave_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 1.3592F), SMOOTH),
				k(0.5F, rot(0F, 0F, 1.3592F), SMOOTH),
				k(1F, rot(0F, 0F, 0.563F), SMOOTH),
				k(1.5F, rot(0F, 0F, -0.563F), SMOOTH),
				k(2F, rot(0F, 0F, -1.3592F), SMOOTH),
				k(2.5F, rot(0F, 0F, -1.3592F), SMOOTH),
				k(3F, rot(0F, 0F, -0.563F), SMOOTH),
				k(3.5F, rot(0F, 0F, 0.563F), SMOOTH),
				k(4F, rot(0F, 0F, 1.3592F), SMOOTH)))
			.addAnimation("wave_4", new AnimationChannel(POS,
				k(0F, pos(0F, 0.3923F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.2774F, 0F), SMOOTH),
				k(1F, pos(0F, 0F, 0F), SMOOTH),
				k(1.5F, pos(0F, -0.2774F, 0F), SMOOTH),
				k(2F, pos(0F, -0.3923F, 0F), SMOOTH),
				k(2.5F, pos(0F, -0.2774F, 0F), SMOOTH),
				k(3F, pos(0F, 0F, 0F), SMOOTH),
				k(3.5F, pos(0F, 0.2774F, 0F), SMOOTH),
				k(4F, pos(0F, 0.3923F, 0F), SMOOTH)))
			.addAnimation("wave_4", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 1.4712F), SMOOTH),
				k(0.5F, rot(0F, 0F, 1.0403F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.5F, rot(0F, 0F, -1.0403F), SMOOTH),
				k(2F, rot(0F, 0F, -1.4712F), SMOOTH),
				k(2.5F, rot(0F, 0F, -1.0403F), SMOOTH),
				k(3F, rot(0F, 0F, 0F), SMOOTH),
				k(3.5F, rot(0F, 0F, 1.0403F), SMOOTH),
				k(4F, rot(0F, 0F, 1.4712F), SMOOTH)))
			.addAnimation("wave_5", new AnimationChannel(POS,
				k(0F, pos(0F, 0.3073F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.1273F, 0F), SMOOTH),
				k(1F, pos(0F, -0.1273F, 0F), SMOOTH),
				k(1.5F, pos(0F, -0.3073F, 0F), SMOOTH),
				k(2F, pos(0F, -0.3073F, 0F), SMOOTH),
				k(2.5F, pos(0F, -0.1273F, 0F), SMOOTH),
				k(3F, pos(0F, 0.1273F, 0F), SMOOTH),
				k(3.5F, pos(0F, 0.3073F, 0F), SMOOTH),
				k(4F, pos(0F, 0.3073F, 0F), SMOOTH)))
			.addAnimation("wave_5", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 1.1523F), SMOOTH),
				k(0.5F, rot(0F, 0F, 0.4773F), SMOOTH),
				k(1F, rot(0F, 0F, -0.4773F), SMOOTH),
				k(1.5F, rot(0F, 0F, -1.1523F), SMOOTH),
				k(2F, rot(0F, 0F, -1.1523F), SMOOTH),
				k(2.5F, rot(0F, 0F, -0.4773F), SMOOTH),
				k(3F, rot(0F, 0F, 0.4773F), SMOOTH),
				k(3.5F, rot(0F, 0F, 1.1523F), SMOOTH),
				k(4F, rot(0F, 0F, 1.1523F), SMOOTH)))
			.addAnimation("wave_6", new AnimationChannel(POS,
				k(0F, pos(0F, 0.1571F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0F, 0F), SMOOTH),
				k(1F, pos(0F, -0.1571F, 0F), SMOOTH),
				k(1.5F, pos(0F, -0.2222F, 0F), SMOOTH),
				k(2F, pos(0F, -0.1571F, 0F), SMOOTH),
				k(2.5F, pos(0F, 0F, 0F), SMOOTH),
				k(3F, pos(0F, 0.1571F, 0F), SMOOTH),
				k(3.5F, pos(0F, 0.2222F, 0F), SMOOTH),
				k(4F, pos(0F, 0.1571F, 0F), SMOOTH)))
			.addAnimation("wave_6", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0.5893F), SMOOTH),
				k(0.5F, rot(0F, 0F, 0F), SMOOTH),
				k(1F, rot(0F, 0F, -0.5893F), SMOOTH),
				k(1.5F, rot(0F, 0F, -0.8334F), SMOOTH),
				k(2F, rot(0F, 0F, -0.5893F), SMOOTH),
				k(2.5F, rot(0F, 0F, 0F), SMOOTH),
				k(3F, rot(0F, 0F, 0.5893F), SMOOTH),
				k(3.5F, rot(0F, 0F, 0.8334F), SMOOTH),
				k(4F, rot(0F, 0F, 0.5893F), SMOOTH)))
			.addAnimation("wave_7", new AnimationChannel(POS,
				k(0F, pos(0F, 0.0299F, 0F), SMOOTH),
				k(0.5F, pos(0F, -0.0299F, 0F), SMOOTH),
				k(1F, pos(0F, -0.0721F, 0F), SMOOTH),
				k(1.5F, pos(0F, -0.0721F, 0F), SMOOTH),
				k(2F, pos(0F, -0.0299F, 0F), SMOOTH),
				k(2.5F, pos(0F, 0.0299F, 0F), SMOOTH),
				k(3F, pos(0F, 0.0721F, 0F), SMOOTH),
				k(3.5F, pos(0F, 0.0721F, 0F), SMOOTH),
				k(4F, pos(0F, 0.0299F, 0F), SMOOTH)))
			.addAnimation("wave_7", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0.112F), SMOOTH),
				k(0.5F, rot(0F, 0F, -0.112F), SMOOTH),
				k(1F, rot(0F, 0F, -0.2704F), SMOOTH),
				k(1.5F, rot(0F, 0F, -0.2704F), SMOOTH),
				k(2F, rot(0F, 0F, -0.112F), SMOOTH),
				k(2.5F, rot(0F, 0F, 0.112F), SMOOTH),
				k(3F, rot(0F, 0F, 0.2704F), SMOOTH),
				k(3.5F, rot(0F, 0F, 0.2704F), SMOOTH),
				k(4F, rot(0F, 0F, 0.112F), SMOOTH)))
			.addAnimation("float", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.5F, pos(0F, -0.2263F, 0F), SMOOTH),
				k(1F, pos(0F, -0.32F, 0F), SMOOTH),
				k(1.5F, pos(0F, -0.2263F, 0F), SMOOTH),
				k(2F, pos(0F, 0F, 0F), SMOOTH),
				k(2.5F, pos(0F, 0.2263F, 0F), SMOOTH),
				k(3F, pos(0F, 0.32F, 0F), SMOOTH),
				k(3.5F, pos(0F, 0.2263F, 0F), SMOOTH),
				k(4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("float", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0F, 0F, -1.5556F), SMOOTH),
				k(1F, rot(0F, 0F, -2.2F), SMOOTH),
				k(1.5F, rot(0F, 0F, -1.5556F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH),
				k(2.5F, rot(0F, 0F, 1.5556F), SMOOTH),
				k(3F, rot(0F, 0F, 2.2F), SMOOTH),
				k(3.5F, rot(0F, 0F, 1.5556F), SMOOTH),
				k(4F, rot(0F, 0F, 0F), SMOOTH)))
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

	private CultivationRopeAnimations() {}
}
