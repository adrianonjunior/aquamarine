package com.aquamarine.client.entity.kraken_egg;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** GERADO por tools/models/spawnlings/build_model.py: não edite à mão. */
public final class KrakenEggAnimations {
	private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
	private static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;
	private static final AnimationChannel.Target SCALE = AnimationChannel.Targets.SCALE;
	private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
	private static final AnimationChannel.Interpolation LINEAR = AnimationChannel.Interpolations.LINEAR;

	public static final AnimationDefinition PULSE = pulse();
	public static final AnimationDefinition HATCH = hatch();

	private static AnimationDefinition pulse() {
		return AnimationDefinition.Builder.withLength(2F).looping()
			.addAnimation("sac", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.18F, scale(1.06F, 1.04F, 1.06F), SMOOTH),
				k(0.3F, scale(0.99F, 1F, 0.99F), SMOOTH),
				k(0.45F, scale(1.04F, 1.03F, 1.04F), SMOOTH),
				k(0.7F, scale(1F, 1F, 1F), SMOOTH),
				k(2F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("embryo", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.25F, rot(0F, 9.8995F, 4F), SMOOTH),
				k(0.5F, rot(0F, 14F, 0F), SMOOTH),
				k(0.75F, rot(0F, 9.8995F, -4F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, -9.8995F, 4F), SMOOTH),
				k(1.5F, rot(0F, -14F, 0F), SMOOTH),
				k(1.75F, rot(0F, -9.8995F, -4F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("embryo", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.2F, scale(1.08F, 1.08F, 1.08F), SMOOTH),
				k(0.5F, scale(1F, 1F, 1F), SMOOTH),
				k(2F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("embryo_arm_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.25F, rot(7.0711F, 0F, 0F), SMOOTH),
				k(0.5F, rot(10F, 0F, 0F), SMOOTH),
				k(0.75F, rot(7.0711F, 0F, 0F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.25F, rot(-7.0711F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.75F, rot(-7.0711F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("embryo_arm_1", new AnimationChannel(ROT,
				k(0F, rot(9.9957F, 0F, 0F), SMOOTH),
				k(0.25F, rot(6.8616F, 0F, 0F), SMOOTH),
				k(0.5F, rot(-0.292F, 0F, 0F), SMOOTH),
				k(0.75F, rot(-7.2745F, 0F, 0F), SMOOTH),
				k(1F, rot(-9.9957F, 0F, 0F), SMOOTH),
				k(1.25F, rot(-6.8616F, 0F, 0F), SMOOTH),
				k(1.5F, rot(0.292F, 0F, 0F), SMOOTH),
				k(1.75F, rot(7.2745F, 0F, 0F), SMOOTH),
				k(2F, rot(9.9957F, 0F, 0F), SMOOTH)))
			.addAnimation("embryo_arm_2", new AnimationChannel(ROT,
				k(0F, rot(-0.5837F, 0F, 0F), SMOOTH),
				k(0.25F, rot(-7.4718F, 0F, 0F), SMOOTH),
				k(0.5F, rot(-9.9829F, 0F, 0F), SMOOTH),
				k(0.75F, rot(-6.6462F, 0F, 0F), SMOOTH),
				k(1F, rot(0.5837F, 0F, 0F), SMOOTH),
				k(1.25F, rot(7.4718F, 0F, 0F), SMOOTH),
				k(1.5F, rot(9.9829F, 0F, 0F), SMOOTH),
				k(1.75F, rot(6.6462F, 0F, 0F), SMOOTH),
				k(2F, rot(-0.5837F, 0F, 0F), SMOOTH)))
			.addAnimation("embryo_arm_3", new AnimationChannel(ROT,
				k(0F, rot(-9.9616F, 0F, 0F), SMOOTH),
				k(0.25F, rot(-6.4252F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0.875F, 0F, 0F), SMOOTH),
				k(0.75F, rot(7.6627F, 0F, 0F), SMOOTH),
				k(1F, rot(9.9616F, 0F, 0F), SMOOTH),
				k(1.25F, rot(6.4252F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-0.875F, 0F, 0F), SMOOTH),
				k(1.75F, rot(-7.6627F, 0F, 0F), SMOOTH),
				k(2F, rot(-9.9616F, 0F, 0F), SMOOTH)))
			.addAnimation("petal_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.2F, rot(-3F, 0F, 3F), SMOOTH),
				k(0.6F, rot(0F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("petal_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.2F, rot(3F, 0F, 3F), SMOOTH),
				k(0.6F, rot(0F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("petal_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.2F, rot(3F, 0F, -3F), SMOOTH),
				k(0.6F, rot(0F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("petal_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.2F, rot(-3F, 0F, -3F), SMOOTH),
				k(0.6F, rot(0F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("root_0_end", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.25F, rot(2.8284F, 0F, 0F), SMOOTH),
				k(0.5F, rot(4F, 0F, 0F), SMOOTH),
				k(0.75F, rot(2.8284F, 0F, 0F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.25F, rot(-2.8284F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-4F, 0F, 0F), SMOOTH),
				k(1.75F, rot(-2.8284F, 0F, 0F), SMOOTH),
				k(2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("root_1_end", new AnimationChannel(ROT,
				k(0F, rot(3.3659F, 0F, 0F), SMOOTH),
				k(0.25F, rot(3.9082F, 0F, 0F), SMOOTH),
				k(0.5F, rot(2.1612F, 0F, 0F), SMOOTH),
				k(0.75F, rot(-0.8518F, 0F, 0F), SMOOTH),
				k(1F, rot(-3.3659F, 0F, 0F), SMOOTH),
				k(1.25F, rot(-3.9082F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-2.1612F, 0F, 0F), SMOOTH),
				k(1.75F, rot(0.8518F, 0F, 0F), SMOOTH),
				k(2F, rot(3.3659F, 0F, 0F), SMOOTH)))
			.addAnimation("root_2_end", new AnimationChannel(ROT,
				k(0F, rot(3.6372F, 0F, 0F), SMOOTH),
				k(0.25F, rot(1.3948F, 0F, 0F), SMOOTH),
				k(0.5F, rot(-1.6646F, 0F, 0F), SMOOTH),
				k(0.75F, rot(-3.7489F, 0F, 0F), SMOOTH),
				k(1F, rot(-3.6372F, 0F, 0F), SMOOTH),
				k(1.25F, rot(-1.3948F, 0F, 0F), SMOOTH),
				k(1.5F, rot(1.6646F, 0F, 0F), SMOOTH),
				k(1.75F, rot(3.7489F, 0F, 0F), SMOOTH),
				k(2F, rot(3.6372F, 0F, 0F), SMOOTH)))
			.addAnimation("root_3_end", new AnimationChannel(ROT,
				k(0F, rot(0.5645F, 0F, 0F), SMOOTH),
				k(0.25F, rot(-2.401F, 0F, 0F), SMOOTH),
				k(0.5F, rot(-3.96F, 0F, 0F), SMOOTH),
				k(0.75F, rot(-3.1993F, 0F, 0F), SMOOTH),
				k(1F, rot(-0.5645F, 0F, 0F), SMOOTH),
				k(1.25F, rot(2.401F, 0F, 0F), SMOOTH),
				k(1.5F, rot(3.96F, 0F, 0F), SMOOTH),
				k(1.75F, rot(3.1993F, 0F, 0F), SMOOTH),
				k(2F, rot(0.5645F, 0F, 0F), SMOOTH)))
			.addAnimation("root_4_end", new AnimationChannel(ROT,
				k(0F, rot(-3.0272F, 0F, 0F), SMOOTH),
				k(0.25F, rot(-3.9893F, 0F, 0F), SMOOTH),
				k(0.5F, rot(-2.6146F, 0F, 0F), SMOOTH),
				k(0.75F, rot(0.2918F, 0F, 0F), SMOOTH),
				k(1F, rot(3.0272F, 0F, 0F), SMOOTH),
				k(1.25F, rot(3.9893F, 0F, 0F), SMOOTH),
				k(1.5F, rot(2.6146F, 0F, 0F), SMOOTH),
				k(1.75F, rot(-0.2918F, 0F, 0F), SMOOTH),
				k(2F, rot(-3.0272F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition hatch() {
		return AnimationDefinition.Builder.withLength(1.8F)
			.addAnimation("sac", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.12F, scale(1.12F, 1.08F, 1.12F), SMOOTH),
				k(0.24F, scale(0.96F, 0.98F, 0.96F), SMOOTH),
				k(0.36F, scale(1.14F, 1.1F, 1.14F), SMOOTH),
				k(0.48F, scale(0.95F, 0.97F, 0.95F), SMOOTH),
				k(0.6F, scale(1.16F, 1.12F, 1.16F), SMOOTH),
				k(1F, scale(1.05F, 0.9F, 1.05F), SMOOTH),
				k(1.4F, scale(1.1F, 0.35F, 1.1F), SMOOTH),
				k(1.8F, scale(1.1F, 0.25F, 1.1F), SMOOTH)))
			.addAnimation("petal_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.7F, rot(-6F, 0F, 6F), SMOOTH),
				k(1F, rot(-62F, 0F, 62F), LINEAR),
				k(1.15F, rot(-55F, 0F, 55F), SMOOTH),
				k(1.8F, rot(-58F, 0F, 58F), SMOOTH)))
			.addAnimation("petal_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.7F, rot(6F, 0F, 6F), SMOOTH),
				k(1F, rot(62F, 0F, 62F), LINEAR),
				k(1.15F, rot(55F, 0F, 55F), SMOOTH),
				k(1.8F, rot(58F, 0F, 58F), SMOOTH)))
			.addAnimation("petal_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.7F, rot(6F, 0F, -6F), SMOOTH),
				k(1F, rot(62F, 0F, -62F), LINEAR),
				k(1.15F, rot(55F, 0F, -55F), SMOOTH),
				k(1.8F, rot(58F, 0F, -58F), SMOOTH)))
			.addAnimation("petal_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.7F, rot(-6F, 0F, -6F), SMOOTH),
				k(1F, rot(-62F, 0F, -62F), LINEAR),
				k(1.15F, rot(-55F, 0F, -55F), SMOOTH),
				k(1.8F, rot(-58F, 0F, -58F), SMOOTH)))
			.addAnimation("embryo", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 1F, 0F), SMOOTH),
				k(1.1F, pos(0F, 5F, 0F), SMOOTH),
				k(1.4F, pos(0F, 9F, 0F), SMOOTH),
				k(1.8F, pos(0F, 10F, 0F), SMOOTH)))
			.addAnimation("embryo", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(1.1F, scale(1.1F, 1.1F, 1.1F), SMOOTH),
				k(1.5F, scale(1.3F, 1.3F, 1.3F), SMOOTH),
				k(1.8F, scale(1.2F, 1.2F, 1.2F), SMOOTH)))
			.addAnimation("embryo", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.1F, rot(6F, 10F, 0F), LINEAR),
				k(0.18F, rot(-6F, -10F, 0F), LINEAR),
				k(0.26F, rot(6F, 10F, 0F), LINEAR),
				k(0.34F, rot(-6F, -10F, 0F), LINEAR),
				k(0.42F, rot(6F, 10F, 0F), LINEAR),
				k(0.5F, rot(-6F, -10F, 0F), LINEAR),
				k(0.58F, rot(6F, 10F, 0F), LINEAR),
				k(0.66F, rot(-6F, -10F, 0F), LINEAR),
				k(0.74F, rot(6F, 10F, 0F), LINEAR),
				k(0.82F, rot(-6F, -10F, 0F), LINEAR),
				k(0.9F, rot(6F, 10F, 0F), LINEAR),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("embryo_arm_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-30F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-50F, 0F, 0F), SMOOTH),
				k(1.8F, rot(-40F, 0F, 0F), SMOOTH)))
			.addAnimation("embryo_arm_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-30F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-50F, 0F, 0F), SMOOTH),
				k(1.8F, rot(-40F, 0F, 0F), SMOOTH)))
			.addAnimation("embryo_arm_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-30F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-50F, 0F, 0F), SMOOTH),
				k(1.8F, rot(-40F, 0F, 0F), SMOOTH)))
			.addAnimation("embryo_arm_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-30F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-50F, 0F, 0F), SMOOTH),
				k(1.8F, rot(-40F, 0F, 0F), SMOOTH)))
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

	private KrakenEggAnimations() {}
}
