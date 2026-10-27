package com.aquamarine.client.entity.oyster;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** GERADO por tools/models/spawnlings/build_model.py: não edite à mão. */
public final class OysterAnimations {
	private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
	private static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;
	private static final AnimationChannel.Target SCALE = AnimationChannel.Targets.SCALE;
	private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
	private static final AnimationChannel.Interpolation LINEAR = AnimationChannel.Interpolations.LINEAR;

	public static final AnimationDefinition IDLE = idle();
	public static final AnimationDefinition CLOSE = close();
	public static final AnimationDefinition OPEN = open();
	public static final AnimationDefinition PEARL_GLOW = pearl_glow();

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-25F, 0F, 0F), SMOOTH),
				k(0.25F, rot(-23.8519F, 0F, 0F), SMOOTH),
				k(0.5F, rot(-22.8787F, 0F, 0F), SMOOTH),
				k(0.75F, rot(-22.2284F, 0F, 0F), SMOOTH),
				k(1F, rot(-22F, 0F, 0F), SMOOTH),
				k(1.25F, rot(-22.2284F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-22.8787F, 0F, 0F), SMOOTH),
				k(1.75F, rot(-23.8519F, 0F, 0F), SMOOTH),
				k(2F, rot(-25F, 0F, 0F), SMOOTH),
				k(2.25F, rot(-26.1481F, 0F, 0F), SMOOTH),
				k(2.5F, rot(-27.1213F, 0F, 0F), SMOOTH),
				k(2.75F, rot(-27.7716F, 0F, 0F), SMOOTH),
				k(3F, rot(-28F, 0F, 0F), SMOOTH),
				k(3.25F, rot(-27.7716F, 0F, 0F), SMOOTH),
				k(3.5F, rot(-27.1213F, 0F, 0F), SMOOTH),
				k(3.75F, rot(-26.1481F, 0F, 0F), SMOOTH),
				k(4F, rot(-25F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition close() {
		return AnimationDefinition.Builder.withLength(0.3F)
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(-25F, 0F, 0F), SMOOTH),
				k(0.12F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.3F, rot(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition open() {
		return AnimationDefinition.Builder.withLength(1.5F)
			.addAnimation("lid", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(-5F, 0F, 0F), SMOOTH),
				k(1.5F, rot(-25F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition pearl_glow() {
		return AnimationDefinition.Builder.withLength(3F).looping()
			.addAnimation("pearl", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.1875F, scale(1.023F, 1.023F, 1.023F), SMOOTH),
				k(0.375F, scale(1.0424F, 1.0424F, 1.0424F), SMOOTH),
				k(0.5625F, scale(1.0554F, 1.0554F, 1.0554F), SMOOTH),
				k(0.75F, scale(1.06F, 1.06F, 1.06F), SMOOTH),
				k(0.9375F, scale(1.0554F, 1.0554F, 1.0554F), SMOOTH),
				k(1.125F, scale(1.0424F, 1.0424F, 1.0424F), SMOOTH),
				k(1.3125F, scale(1.023F, 1.023F, 1.023F), SMOOTH),
				k(1.5F, scale(1F, 1F, 1F), SMOOTH),
				k(1.6875F, scale(0.977F, 0.977F, 0.977F), SMOOTH),
				k(1.875F, scale(0.9576F, 0.9576F, 0.9576F), SMOOTH),
				k(2.0625F, scale(0.9446F, 0.9446F, 0.9446F), SMOOTH),
				k(2.25F, scale(0.94F, 0.94F, 0.94F), SMOOTH),
				k(2.4375F, scale(0.9446F, 0.9446F, 0.9446F), SMOOTH),
				k(2.625F, scale(0.9576F, 0.9576F, 0.9576F), SMOOTH),
				k(2.8125F, scale(0.977F, 0.977F, 0.977F), SMOOTH),
				k(3F, scale(1F, 1F, 1F), SMOOTH)))
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

	private OysterAnimations() {}
}
