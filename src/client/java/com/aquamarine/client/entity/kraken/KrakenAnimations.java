package com.aquamarine.client.entity.kraken;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

import org.joml.Vector3f;

/** Named animation clips matching the KrakenView contract. Attacks each have wind-up, hit and recovery. */
public final class KrakenAnimations {
	private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
	private static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;
	private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;

	public static final AnimationDefinition IDLE = clip(3.0F, true,
		motion("mantle", POS, 3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_0_segment_1", ROT, 3.0F, 0.0F, 0.0F, 0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_4_segment_1", ROT, 3.0F, 0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 0.0F, -3.0F, 0.0F, 0.0F, SMOOTH));

	public static final AnimationDefinition SWIM = clip(1.4F, true,
		motion("mantle", ROT, 1.4F, -2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 0.0F, -2.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_1_segment_0", ROT, 1.4F, -9.0F, 0.0F, 0.0F, 12.0F, 0.0F, 0.0F, -9.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_5_segment_0", ROT, 1.4F, 12.0F, 0.0F, 0.0F, -9.0F, 0.0F, 0.0F, 12.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_3_segment_1", ROT, 1.4F, 8.0F, 0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, SMOOTH));

	public static final AnimationDefinition EMERGE = clip(1.8F, false,
		motion("mantle", POS, 1.8F, 0.0F, 5.0F, 0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("head", ROT, 1.8F, 8.0F, 0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_0_segment_0", ROT, 1.8F, 0.0F, 0.0F, 0.0F, -18.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));

	public static final AnimationDefinition STUNNED = clip(1.1F, true,
		motion("mantle", ROT, 1.1F, 0.0F, -7.0F, 4.0F, 0.0F, 7.0F, -4.0F, 0.0F, -7.0F, 4.0F, SMOOTH),
		motion("arm_0_segment_0", ROT, 1.1F, -22.0F, 0.0F, 0.0F, 10.0F, 0.0F, 0.0F, -22.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_4_segment_0", ROT, 1.1F, 10.0F, 0.0F, 0.0F, -22.0F, 0.0F, 0.0F, 10.0F, 0.0F, 0.0F, SMOOTH));

	public static final AnimationDefinition PHASE_CHANGE = clip(2.2F, false,
		motion("mantle", ROT, 2.2F, -12.0F, 0.0F, 0.0F, 15.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("head", ROT, 2.2F, 0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 12.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_0_segment_0", ROT, 2.2F, 0.0F, 0.0F, 0.0F, -28.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_2_segment_0", ROT, 2.2F, 0.0F, 0.0F, 0.0F, -28.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_4_segment_0", ROT, 2.2F, 0.0F, 0.0F, 0.0F, -28.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_6_segment_0", ROT, 2.2F, 0.0F, 0.0F, 0.0F, -28.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));

	public static final AnimationDefinition DEATH = clip(2.4F, false,
		motion("mantle", ROT, 2.4F, 0.0F, 0.0F, 0.0F, 25.0F, 0.0F, 32.0F, 80.0F, 0.0F, 90.0F, SMOOTH),
		motion("head", ROT, 2.4F, 0.0F, 0.0F, 0.0F, -25.0F, 0.0F, -32.0F, -45.0F, 0.0F, -50.0F, SMOOTH),
		motion("arm_0_segment_0", ROT, 2.4F, 0.0F, 0.0F, 0.0F, 35.0F, 0.0F, 0.0F, 60.0F, 0.0F, 0.0F, SMOOTH));

	public static final AnimationDefinition SLAM = strike("arm_0_segment_0", "arm_0_segment_1", 1.25F, -48.0F, 42.0F);
	public static final AnimationDefinition GRAB = strike("arm_1_segment_0", "arm_1_segment_1", 1.55F, -32.0F, 34.0F);
	public static final AnimationDefinition INK = clip(1.35F, false,
		motion("mantle", POS, 1.35F, 0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("head", ROT, 1.35F, 12.0F, 0.0F, 0.0F, -22.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));
	public static final AnimationDefinition WHIRLPOOL = clip(2.0F, true,
		motion("arm_0_segment_0", ROT, 2.0F, -12.0F, -12.0F, 0.0F, -12.0F, 18.0F, 0.0F, -12.0F, 24.0F, 0.0F, SMOOTH),
		motion("arm_2_segment_0", ROT, 2.0F, -12.0F, -12.0F, 0.0F, -12.0F, 18.0F, 0.0F, -12.0F, 24.0F, 0.0F, SMOOTH),
		motion("arm_4_segment_0", ROT, 2.0F, -12.0F, -12.0F, 0.0F, -12.0F, 18.0F, 0.0F, -12.0F, 24.0F, 0.0F, SMOOTH),
		motion("arm_6_segment_0", ROT, 2.0F, -12.0F, -12.0F, 0.0F, -12.0F, 18.0F, 0.0F, -12.0F, 24.0F, 0.0F, SMOOTH));
	public static final AnimationDefinition SPAWN_YOUNG = clip(1.6F, false,
		motion("arm_4_segment_0", ROT, 1.6F, 0.0F, 0.0F, 0.0F, -20.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_5_segment_0", ROT, 1.6F, 0.0F, 0.0F, 0.0F, -26.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_6_segment_0", ROT, 1.6F, 0.0F, 0.0F, 0.0F, -20.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));
	public static final AnimationDefinition WATER_JET = clip(1.15F, false,
		motion("head", POS, 1.15F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("head", ROT, 1.15F, 8.0F, 0.0F, 0.0F, -18.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_1_segment_0", ROT, 1.15F, 0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));
	public static final AnimationDefinition ROAR = clip(1.7F, false,
		motion("mantle", POS, 1.7F, 0.0F, 0.0F, 0.0F, 0.0F, -2.5F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("head", ROT, 1.7F, 14.0F, 0.0F, 0.0F, -28.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_0_segment_0", ROT, 1.7F, 0.0F, 0.0F, 0.0F, -24.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_4_segment_0", ROT, 1.7F, 0.0F, 0.0F, 0.0F, -24.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));
	public static final AnimationDefinition BOLTS = clip(1.5F, false,
		motion("head", ROT, 1.5F, 0.0F, -8.0F, 0.0F, 0.0F, 16.0F, 0.0F, 0.0F, -8.0F, 0.0F, SMOOTH),
		motion("arm_2_segment_0", ROT, 1.5F, 0.0F, 0.0F, 0.0F, -22.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_6_segment_0", ROT, 1.5F, 0.0F, 0.0F, 0.0F, -22.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));
	public static final AnimationDefinition AMBUSH = clip(1.3F, false,
		motion("mantle", POS, 1.3F, 0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_0_segment_0", ROT, 1.3F, 28.0F, 0.0F, 0.0F, -52.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_0_segment_1", ROT, 1.3F, 10.0F, 0.0F, 0.0F, -34.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));
	public static final AnimationDefinition DOUBLE_GRAB = clip(1.7F, false,
		motion("arm_1_segment_0", ROT, 1.7F, -30.0F, 0.0F, 0.0F, 38.0F, 0.0F, 0.0F, -12.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_1_segment_1", ROT, 1.7F, 0.0F, 0.0F, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_5_segment_0", ROT, 1.7F, -30.0F, 0.0F, 0.0F, 38.0F, 0.0F, 0.0F, -12.0F, 0.0F, 0.0F, SMOOTH),
		motion("arm_5_segment_1", ROT, 1.7F, 0.0F, 0.0F, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));

	private static AnimationDefinition strike(String first, String second, float duration, float windUp, float impact) {
		return clip(duration, false,
			motion(first, ROT, duration, windUp, 0.0F, 0.0F, impact, 0.0F, 0.0F, -windUp * 0.15F, 0.0F, 0.0F, SMOOTH),
			motion(second, ROT, duration, windUp * 0.35F, 0.0F, 0.0F, impact * 0.55F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, SMOOTH));
	}

	private static AnimationDefinition clip(float length, boolean looping, AnimationChannel... channels) {
		AnimationDefinition.Builder builder = AnimationDefinition.Builder.withLength(length);
		if (looping) {
			builder.looping();
		}
		for (AnimationChannel channel : channels) {
			builder.addAnimation(channelPart(channel), channel);
		}
		return builder.build();
	}

	/* The target part is carried in a tiny registry so clip declarations stay readable. */
	private static String channelPart(AnimationChannel channel) {
		return ChannelParts.part(channel);
	}

	private static AnimationChannel motion(String part, AnimationChannel.Target target, float duration,
		float startX, float startY, float startZ, float peakX, float peakY, float peakZ,
		float endX, float endY, float endZ, AnimationChannel.Interpolation interpolation) {
		AnimationChannel channel = new AnimationChannel(target,
			key(target, 0.0F, startX, startY, startZ, interpolation),
			key(target, duration * 0.27F, peakX * 0.35F, peakY * 0.35F, peakZ * 0.35F, interpolation),
			key(target, duration * 0.58F, peakX, peakY, peakZ, interpolation),
			key(target, duration, endX, endY, endZ, interpolation));
		ChannelParts.put(channel, part);
		return channel;
	}

	private static Keyframe key(AnimationChannel.Target target, float time, float x, float y, float z,
		AnimationChannel.Interpolation interpolation) {
		Vector3f vector = target == AnimationChannel.Targets.ROTATION
			? KeyframeAnimations.degreeVec(x, y, z)
			: KeyframeAnimations.posVec(x, y, z);
		return new Keyframe(time, vector, interpolation);
	}

	private static final class ChannelParts {
		private static final java.util.IdentityHashMap<AnimationChannel, String> PARTS = new java.util.IdentityHashMap<>();
		static void put(AnimationChannel channel, String part) { PARTS.put(channel, part); }
		static String part(AnimationChannel channel) { return PARTS.get(channel); }
	}

	private KrakenAnimations() {}
}
