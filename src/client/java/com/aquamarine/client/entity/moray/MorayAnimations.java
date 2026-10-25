package com.aquamarine.client.entity.moray;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/** Gerado por tools/models/moray/build_model.py; edite o script, não este arquivo. */
public final class MorayAnimations {
	private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
	private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
	public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(4F).looping()
		.addAnimation("head", rot(k(0F, 0F, 2.35F, 0F), k(0.5F, 0F, 2.98F, 0F), k(1F, 0F, 1.86F, 0F), k(1.5F, 0F, -0.34F, 0F), k(2F, 0F, -2.35F, 0F), k(2.5F, 0F, -2.98F, 0F), k(3F, 0F, -1.86F, 0F), k(3.5F, 0F, 0.34F, 0F), k(4F, 0F, 2.35F, 0F)))
		.addAnimation("seg1", rot(k(0F, 0F, 0F, 0F), k(0.5F, 0F, 2.47F, 0F), k(1F, 0F, 3.5F, 0F), k(1.5F, 0F, 2.47F, 0F), k(2F, 0F, 0F, 0F), k(2.5F, 0F, -2.47F, 0F), k(3F, 0F, -3.5F, 0F), k(3.5F, 0F, -2.47F, 0F), k(4F, 0F, 0F, 0F)))
		.addAnimation("seg2", rot(k(0F, 0F, -3.83F, 0F), k(0.5F, 0F, -0.33F, 0F), k(1F, 0F, 3.37F, 0F), k(1.5F, 0F, 5.09F, 0F), k(2F, 0F, 3.83F, 0F), k(2.5F, 0F, 0.33F, 0F), k(3F, 0F, -3.37F, 0F), k(3.5F, 0F, -5.09F, 0F), k(4F, 0F, -3.83F, 0F)))
		.addAnimation("seg3", rot(k(0F, 0F, -6.64F, 0F), k(0.5F, 0F, -5.31F, 0F), k(1F, 0F, -0.86F, 0F), k(1.5F, 0F, 4.09F, 0F), k(2F, 0F, 6.64F, 0F), k(2.5F, 0F, 5.31F, 0F), k(3F, 0F, 0.86F, 0F), k(3.5F, 0F, -4.09F, 0F), k(4F, 0F, -6.64F, 0F)))
		.addAnimation("seg4", rot(k(0F, 0F, -4.63F, 0F), k(0.5F, 0F, -8.14F, 0F), k(1F, 0F, -6.89F, 0F), k(1.5F, 0F, -1.6F, 0F), k(2F, 0F, 4.63F, 0F), k(2.5F, 0F, 8.14F, 0F), k(3F, 0F, 6.89F, 0F), k(3.5F, 0F, 1.6F, 0F), k(4F, 0F, -4.63F, 0F)))
		.addAnimation("seg5", rot(k(0F, 0F, 2.53F, 0F), k(0.5F, 0F, -4.98F, 0F), k(1F, 0F, -9.57F, 0F), k(1.5F, 0F, -8.56F, 0F), k(2F, 0F, -2.53F, 0F), k(2.5F, 0F, 4.98F, 0F), k(3F, 0F, 9.57F, 0F), k(3.5F, 0F, 8.56F, 0F), k(4F, 0F, 2.53F, 0F)))
		.addAnimation("seg6", rot(k(0F, 0F, 10.29F, 0F), k(0.5F, 0F, 3.65F, 0F), k(1F, 0F, -5.13F, 0F), k(1.5F, 0F, -10.91F, 0F), k(2F, 0F, -10.29F, 0F), k(2.5F, 0F, -3.65F, 0F), k(3F, 0F, 5.13F, 0F), k(3.5F, 0F, 10.91F, 0F), k(4F, 0F, 10.29F, 0F)))
		.addAnimation("tail", rot(k(0F, 0F, 12.13F, 0F), k(0.5F, 0F, 12.08F, 0F), k(1F, 0F, 4.95F, 0F), k(1.5F, 0F, -5.07F, 0F), k(2F, 0F, -12.13F, 0F), k(2.5F, 0F, -12.08F, 0F), k(3F, 0F, -4.95F, 0F), k(3.5F, 0F, 5.07F, 0F), k(4F, 0F, 12.13F, 0F)))
		.addAnimation("jaw", rot(k(0F, 5F, 0F, 0F), k(0.5F, 7.83F, 0F, 0F), k(1F, 9F, 0F, 0F), k(1.5F, 7.83F, 0F, 0F), k(2F, 5F, 0F, 0F), k(2.5F, 2.17F, 0F, 0F), k(3F, 1F, 0F, 0F), k(3.5F, 2.17F, 0F, 0F), k(4F, 5F, 0F, 0F)))
		.build();
	public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(1.6F).looping()
		.addAnimation("head", rot(k(0F, 0F, 4.7F, 0F), k(0.2F, 0F, 5.96F, 0F), k(0.4F, 0F, 3.73F, 0F), k(0.6F, 0F, -0.69F, 0F), k(0.8F, 0F, -4.7F, 0F), k(1F, 0F, -5.96F, 0F), k(1.2F, 0F, -3.73F, 0F), k(1.4F, 0F, 0.69F, 0F), k(1.6F, 0F, 4.7F, 0F)))
		.addAnimation("seg1", rot(k(0F, 0F, 0F, 0F), k(0.2F, 0F, 6.36F, 0F), k(0.4F, 0F, 9F, 0F), k(0.6F, 0F, 6.36F, 0F), k(0.8F, 0F, 0F, 0F), k(1F, 0F, -6.36F, 0F), k(1.2F, 0F, -9F, 0F), k(1.4F, 0F, -6.36F, 0F), k(1.6F, 0F, 0F, 0F)))
		.addAnimation("seg2", rot(k(0F, 0F, -7.96F, 0F), k(0.2F, 0F, -0.68F, 0F), k(0.4F, 0F, 7F, 0F), k(0.6F, 0F, 10.58F, 0F), k(0.8F, 0F, 7.96F, 0F), k(1F, 0F, 0.68F, 0F), k(1.2F, 0F, -7F, 0F), k(1.4F, 0F, -10.58F, 0F), k(1.6F, 0F, -7.96F, 0F)))
		.addAnimation("seg3", rot(k(0F, 0F, -12.1F, 0F), k(0.2F, 0F, -9.67F, 0F), k(0.4F, 0F, -1.57F, 0F), k(0.6F, 0F, 7.44F, 0F), k(0.8F, 0F, 12.1F, 0F), k(1F, 0F, 9.67F, 0F), k(1.2F, 0F, 1.57F, 0F), k(1.4F, 0F, -7.44F, 0F), k(1.6F, 0F, -12.1F, 0F)))
		.addAnimation("seg4", rot(k(0F, 0F, -7.7F, 0F), k(0.2F, 0F, -13.54F, 0F), k(0.4F, 0F, -11.45F, 0F), k(0.6F, 0F, -2.66F, 0F), k(0.8F, 0F, 7.7F, 0F), k(1F, 0F, 13.54F, 0F), k(1.2F, 0F, 11.45F, 0F), k(1.4F, 0F, 2.66F, 0F), k(1.6F, 0F, -7.7F, 0F)))
		.addAnimation("seg5", rot(k(0F, 0F, 3.94F, 0F), k(0.2F, 0F, -7.75F, 0F), k(0.4F, 0F, -14.89F, 0F), k(0.6F, 0F, -13.31F, 0F), k(0.8F, 0F, -3.94F, 0F), k(1F, 0F, 7.75F, 0F), k(1.2F, 0F, 14.89F, 0F), k(1.4F, 0F, 13.31F, 0F), k(1.6F, 0F, 3.94F, 0F)))
		.addAnimation("seg6", rot(k(0F, 0F, 15.21F, 0F), k(0.2F, 0F, 5.4F, 0F), k(0.4F, 0F, -7.58F, 0F), k(0.6F, 0F, -16.12F, 0F), k(0.8F, 0F, -15.21F, 0F), k(1F, 0F, -5.4F, 0F), k(1.2F, 0F, 7.58F, 0F), k(1.4F, 0F, 16.12F, 0F), k(1.6F, 0F, 15.21F, 0F)))
		.addAnimation("tail", rot(k(0F, 0F, 17.22F, 0F), k(0.2F, 0F, 17.15F, 0F), k(0.4F, 0F, 7.03F, 0F), k(0.6F, 0F, -7.21F, 0F), k(0.8F, 0F, -17.22F, 0F), k(1F, 0F, -17.15F, 0F), k(1.2F, 0F, -7.03F, 0F), k(1.4F, 0F, 7.21F, 0F), k(1.6F, 0F, 17.22F, 0F)))
		.build();
	public static final AnimationDefinition STRIKE = AnimationDefinition.Builder.withLength(1.4F)
		.addAnimation("head", rot(k(0F, 0F, 0F, 0F), k(0.9F, -26F, 0F, 0F), k(1.05F, 6F, 0F, 0F), k(1.4F, 0F, 0F, 0F)))
		.addAnimation("jaw", rot(k(0F, 0F, 0F, 0F), k(0.9F, 22F, 0F, 0F), k(1.05F, 38F, 0F, 0F), k(1.2F, 2F, 0F, 0F), k(1.4F, 0F, 0F, 0F)))
		.addAnimation("seg1", rot(k(0F, 0F, 0F, 0F), k(0.9F, 28F, 10F, 0F), k(1.05F, -6F, 0F, 0F), k(1.4F, 0F, 0F, 0F)))
		.addAnimation("seg2", rot(k(0F, 0F, 0F, 0F), k(0.9F, -22F, -16F, 0F), k(1.05F, 2F, 0F, 0F), k(1.4F, 0F, 0F, 0F)))
		.addAnimation("seg3", rot(k(0F, 0F, 0F, 0F), k(0.9F, -10F, 18F, 0F), k(1.05F, 0F, 0F, 0F), k(1.4F, 0F, 0F, 0F)))
		.addAnimation("seg4", rot(k(0F, 0F, 0F, 0F), k(0.9F, 0F, -12F, 0F), k(1.4F, 0F, 0F, 0F)))
		.build();
	public static final AnimationDefinition CHAIN = AnimationDefinition.Builder.withLength(2F)
		.addAnimation("head", rot(k(0F, 0F, 0F, 0F), k(0.1F, -0.93F, 0F, 0F), k(0.2F, -1.87F, 0F, 0F), k(0.3F, -2.8F, 0F, 0F), k(0.4F, -3.73F, 0F, 0F), k(0.5F, -4.67F, 0F, 0F), k(0.6F, -5.6F, 0F, 0F), k(0.7F, -6.53F, 0F, 0F), k(0.8F, -7.47F, 0F, 0F), k(0.9F, -8.4F, 0F, 0F), k(1F, -9.33F, 0F, 0F), k(1.1F, -10.27F, 0F, 0F), k(1.2F, -11.2F, 0F, 0F), k(1.3F, -12.13F, 0F, 0F), k(1.4F, -13.07F, 0F, 0F), k(1.5F, -14F, 0F, 0F), k(1.6F, -22F, 0F, 0F), k(1.8F, -22F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.addAnimation("jaw", rot(k(0F, 0F, 0F, 0F), k(0.1F, 1.73F, 0F, 0F), k(0.2F, 3.47F, 0F, 0F), k(0.3F, 5.2F, 0F, 0F), k(0.4F, 6.93F, 0F, 0F), k(0.5F, 8.67F, 0F, 0F), k(0.6F, 10.4F, 0F, 0F), k(0.7F, 12.13F, 0F, 0F), k(0.8F, 13.87F, 0F, 0F), k(0.9F, 15.6F, 0F, 0F), k(1F, 17.33F, 0F, 0F), k(1.1F, 19.07F, 0F, 0F), k(1.2F, 20.8F, 0F, 0F), k(1.3F, 22.53F, 0F, 0F), k(1.4F, 24.27F, 0F, 0F), k(1.5F, 26F, 0F, 0F), k(1.6F, 34F, 0F, 0F), k(1.8F, 34F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.addAnimation("seg1", rot(k(0F, 0F, -1F, 0F), k(0.1F, 0F, 1.27F, 0F), k(0.2F, 0F, -1.53F, 0F), k(0.3F, 0F, 1.8F, 0F), k(0.4F, 0F, -2.07F, 0F), k(0.5F, 0F, 2.33F, 0F), k(0.6F, 0F, -2.6F, 0F), k(0.7F, 0F, 2.87F, 0F), k(0.8F, 0F, -3.13F, 0F), k(0.9F, 0F, 3.4F, 0F), k(1F, 0F, -3.67F, 0F), k(1.1F, 0F, 3.93F, 0F), k(1.2F, 0F, -4.2F, 0F), k(1.3F, 0F, 4.47F, 0F), k(1.4F, 0F, -4.73F, 0F), k(1.5F, 0F, 5F, 0F), k(1.6F, 0F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.addAnimation("seg2", rot(k(0F, 0F, 1F, 0F), k(0.1F, 0F, -1.27F, 0F), k(0.2F, 0F, 1.53F, 0F), k(0.3F, 0F, -1.8F, 0F), k(0.4F, 0F, 2.07F, 0F), k(0.5F, 0F, -2.33F, 0F), k(0.6F, 0F, 2.6F, 0F), k(0.7F, 0F, -2.87F, 0F), k(0.8F, 0F, 3.13F, 0F), k(0.9F, 0F, -3.4F, 0F), k(1F, 0F, 3.67F, 0F), k(1.1F, 0F, -3.93F, 0F), k(1.2F, 0F, 4.2F, 0F), k(1.3F, 0F, -4.47F, 0F), k(1.4F, 0F, 4.73F, 0F), k(1.5F, 0F, -5F, 0F), k(1.6F, 0F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.addAnimation("seg3", rot(k(0F, 0F, -1F, 0F), k(0.1F, 0F, 1.27F, 0F), k(0.2F, 0F, -1.53F, 0F), k(0.3F, 0F, 1.8F, 0F), k(0.4F, 0F, -2.07F, 0F), k(0.5F, 0F, 2.33F, 0F), k(0.6F, 0F, -2.6F, 0F), k(0.7F, 0F, 2.87F, 0F), k(0.8F, 0F, -3.13F, 0F), k(0.9F, 0F, 3.4F, 0F), k(1F, 0F, -3.67F, 0F), k(1.1F, 0F, 3.93F, 0F), k(1.2F, 0F, -4.2F, 0F), k(1.3F, 0F, 4.47F, 0F), k(1.4F, 0F, -4.73F, 0F), k(1.5F, 0F, 5F, 0F), k(1.6F, 0F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.addAnimation("seg4", rot(k(0F, 0F, 1F, 0F), k(0.1F, 0F, -1.27F, 0F), k(0.2F, 0F, 1.53F, 0F), k(0.3F, 0F, -1.8F, 0F), k(0.4F, 0F, 2.07F, 0F), k(0.5F, 0F, -2.33F, 0F), k(0.6F, 0F, 2.6F, 0F), k(0.7F, 0F, -2.87F, 0F), k(0.8F, 0F, 3.13F, 0F), k(0.9F, 0F, -3.4F, 0F), k(1F, 0F, 3.67F, 0F), k(1.1F, 0F, -3.93F, 0F), k(1.2F, 0F, 4.2F, 0F), k(1.3F, 0F, -4.47F, 0F), k(1.4F, 0F, 4.73F, 0F), k(1.5F, 0F, -5F, 0F), k(1.6F, 0F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.addAnimation("seg5", rot(k(0F, 0F, -1F, 0F), k(0.1F, 0F, 1.27F, 0F), k(0.2F, 0F, -1.53F, 0F), k(0.3F, 0F, 1.8F, 0F), k(0.4F, 0F, -2.07F, 0F), k(0.5F, 0F, 2.33F, 0F), k(0.6F, 0F, -2.6F, 0F), k(0.7F, 0F, 2.87F, 0F), k(0.8F, 0F, -3.13F, 0F), k(0.9F, 0F, 3.4F, 0F), k(1F, 0F, -3.67F, 0F), k(1.1F, 0F, 3.93F, 0F), k(1.2F, 0F, -4.2F, 0F), k(1.3F, 0F, 4.47F, 0F), k(1.4F, 0F, -4.73F, 0F), k(1.5F, 0F, 5F, 0F), k(1.6F, 0F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.addAnimation("seg6", rot(k(0F, 0F, 1F, 0F), k(0.1F, 0F, -1.27F, 0F), k(0.2F, 0F, 1.53F, 0F), k(0.3F, 0F, -1.8F, 0F), k(0.4F, 0F, 2.07F, 0F), k(0.5F, 0F, -2.33F, 0F), k(0.6F, 0F, 2.6F, 0F), k(0.7F, 0F, -2.87F, 0F), k(0.8F, 0F, 3.13F, 0F), k(0.9F, 0F, -3.4F, 0F), k(1F, 0F, 3.67F, 0F), k(1.1F, 0F, -3.93F, 0F), k(1.2F, 0F, 4.2F, 0F), k(1.3F, 0F, -4.47F, 0F), k(1.4F, 0F, 4.73F, 0F), k(1.5F, 0F, -5F, 0F), k(1.6F, 0F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.addAnimation("tail", rot(k(0F, 0F, -1F, 0F), k(0.1F, 0F, 1.27F, 0F), k(0.2F, 0F, -1.53F, 0F), k(0.3F, 0F, 1.8F, 0F), k(0.4F, 0F, -2.07F, 0F), k(0.5F, 0F, 2.33F, 0F), k(0.6F, 0F, -2.6F, 0F), k(0.7F, 0F, 2.87F, 0F), k(0.8F, 0F, -3.13F, 0F), k(0.9F, 0F, 3.4F, 0F), k(1F, 0F, -3.67F, 0F), k(1.1F, 0F, 3.93F, 0F), k(1.2F, 0F, -4.2F, 0F), k(1.3F, 0F, 4.47F, 0F), k(1.4F, 0F, -4.73F, 0F), k(1.5F, 0F, 5F, 0F), k(1.6F, 0F, 0F, 0F), k(2F, 0F, 0F, 0F)))
		.build();
	public static final AnimationDefinition COIL = AnimationDefinition.Builder.withLength(5F)
		.addAnimation("seg6", rot(k(0F, 0F, 0F, 0F), k(1F, -16F, 6F, 0F), k(1.6F, 0F, 42F, 0F), k(4.6F, 0F, 42F, 0F), k(5F, 0F, 0F, 0F)))
		.addAnimation("tail", rot(k(0F, 0F, 0F, 0F), k(0.5F, -30F, -8F, 0F), k(0.75F, -30F, 8F, 0F), k(1F, -30F, -8F, 0F), k(1.6F, 0F, 48F, 0F), k(4.6F, 0F, 48F, 0F), k(5F, 0F, 0F, 0F)))
		.addAnimation("head", rot(k(0F, 0F, 0F, 0F), k(1F, -8F, 0F, 0F), k(1.6F, -4F, -14F, 0F), k(4.6F, -4F, -14F, 0F), k(5F, 0F, 0F, 0F)))
		.addAnimation("jaw", rot(k(0F, 0F, 0F, 0F), k(1.6F, 16F, 0F, 0F), k(4.6F, 16F, 0F, 0F), k(5F, 0F, 0F, 0F)))
		.addAnimation("seg3", rot(k(0F, 0F, 0F, 0F), k(1F, 0F, 0F, 0F), k(1.6F, 0F, 34F, 0F), k(4.6F, 0F, 34F, 0F), k(5F, 0F, 0F, 0F)))
		.addAnimation("seg4", rot(k(0F, 0F, 0F, 0F), k(1F, 0F, 0F, 0F), k(1.6F, 0F, 38F, 0F), k(4.6F, 0F, 38F, 0F), k(5F, 0F, 0F, 0F)))
		.addAnimation("seg5", rot(k(0F, 0F, 0F, 0F), k(1F, 0F, 0F, 0F), k(1.6F, 0F, 40F, 0F), k(4.6F, 0F, 40F, 0F), k(5F, 0F, 0F, 0F)))
		.build();
	public static final AnimationDefinition STUNNED = AnimationDefinition.Builder.withLength(2.4F).looping()
		.addAnimation("body", rot(k(0F, 0F, 0F, 24F), k(0.4F, 0F, 0F, 28.33F), k(0.8F, 0F, 0F, 28.33F), k(1.2F, 0F, 0F, 24F), k(1.6F, 0F, 0F, 19.67F), k(2F, 0F, 0F, 19.67F), k(2.4F, 0F, 0F, 24F)))
		.addAnimation("head", rot(k(0F, 16F, 0F, 0F), k(0.4F, 16F, 3.46F, 0F), k(0.8F, 16F, 3.46F, 0F), k(1.2F, 16F, 0F, 0F), k(1.6F, 16F, -3.46F, 0F), k(2F, 16F, -3.46F, 0F), k(2.4F, 16F, 0F, 0F)))
		.addAnimation("jaw", rot(k(0F, 18F, 0F, 0F), k(0.4F, 18F, 0F, 0F), k(0.8F, 18F, 0F, 0F), k(1.2F, 18F, 0F, 0F), k(1.6F, 18F, 0F, 0F), k(2F, 18F, 0F, 0F), k(2.4F, 18F, 0F, 0F)))
		.addAnimation("seg1", rot(k(0F, 0F, 0F, 0F), k(0.4F, 2.6F, 1.73F, 0F), k(0.8F, 2.6F, 1.73F, 0F), k(1.2F, 0F, 0F, 0F), k(1.6F, -2.6F, -1.73F, 0F), k(2F, -2.6F, -1.73F, 0F), k(2.4F, 0F, 0F, 0F)))
		.addAnimation("seg2", rot(k(0F, -1.69F, -0.96F, 0F), k(0.4F, 1.3F, 1.04F, 0F), k(0.8F, 2.99F, 2F, 0F), k(1.2F, 1.69F, 0.96F, 0F), k(1.6F, -1.3F, -1.04F, 0F), k(2F, -2.99F, -2F, 0F), k(2.4F, -1.69F, -0.96F, 0F)))
		.addAnimation("seg3", rot(k(0F, -2.8F, -1.68F, 0F), k(0.4F, -0.46F, 0.09F, 0F), k(0.8F, 2.34F, 1.78F, 0F), k(1.2F, 2.8F, 1.68F, 0F), k(1.6F, 0.46F, -0.09F, 0F), k(2F, -2.34F, -1.78F, 0F), k(2.4F, -2.8F, -1.68F, 0F)))
		.addAnimation("seg4", rot(k(0F, -2.92F, -1.99F, 0F), k(0.4F, -2.05F, -0.87F, 0F), k(0.8F, 0.87F, 1.12F, 0F), k(1.2F, 2.92F, 1.99F, 0F), k(1.6F, 2.05F, 0.87F, 0F), k(2F, -0.87F, -1.12F, 0F), k(2.4F, -2.92F, -1.99F, 0F)))
		.addAnimation("seg5", rot(k(0F, -2.03F, -1.82F, 0F), k(0.4F, -2.93F, -1.63F, 0F), k(0.8F, -0.9F, 0.19F, 0F), k(1.2F, 2.03F, 1.82F, 0F), k(1.6F, 2.93F, 1.63F, 0F), k(2F, 0.9F, -0.19F, 0F), k(2.4F, -2.03F, -1.82F, 0F)))
		.addAnimation("seg6", rot(k(0F, -0.42F, -1.2F, 0F), k(0.4F, -2.78F, -1.99F, 0F), k(0.8F, -2.36F, -0.79F, 0F), k(1.2F, 0.42F, 1.2F, 0F), k(1.6F, 2.78F, 1.99F, 0F), k(2F, 2.36F, 0.79F, 0F), k(2.4F, -0.42F, -1.2F, 0F)))
		.addAnimation("tail", rot(k(0F, 1.33F, -0.28F, 0F), k(0.4F, -1.67F, -1.86F, 0F), k(0.8F, -2.99F, -1.57F, 0F), k(1.2F, -1.33F, 0.28F, 0F), k(1.6F, 1.67F, 1.86F, 0F), k(2F, 2.99F, 1.57F, 0F), k(2.4F, 1.33F, -0.28F, 0F)))
		.build();
	public static final AnimationDefinition DEATH = AnimationDefinition.Builder.withLength(2.5F)
		.addAnimation("body", rot(k(0F, 0F, 0F, 0F), k(0.8F, 0F, 0F, 60F), k(2F, 0F, 0F, 175F), k(2.5F, 0F, 0F, 180F)))
		.addAnimation("head", rot(k(0F, 0F, 0F, 0F), k(0.6F, -20F, 0F, 0F), k(2.5F, 10F, 0F, 0F)))
		.addAnimation("jaw", rot(k(0F, 0F, 0F, 0F), k(0.6F, 34F, 0F, 0F), k(2.5F, 24F, 0F, 0F)))
		.addAnimation("seg1", rot(k(0F, 0F, 0F, 0F), k(0.6F, 0F, -14F, 0F), k(2.5F, -4F, -3F, 0F)))
		.addAnimation("seg2", rot(k(0F, 0F, 0F, 0F), k(0.6F, 0F, 14F, 0F), k(2.5F, 4F, 3F, 0F)))
		.addAnimation("seg3", rot(k(0F, 0F, 0F, 0F), k(0.6F, 0F, -14F, 0F), k(2.5F, -4F, -3F, 0F)))
		.addAnimation("seg4", rot(k(0F, 0F, 0F, 0F), k(0.6F, 0F, 14F, 0F), k(2.5F, 4F, 3F, 0F)))
		.addAnimation("seg5", rot(k(0F, 0F, 0F, 0F), k(0.6F, 0F, -14F, 0F), k(2.5F, -4F, -3F, 0F)))
		.addAnimation("seg6", rot(k(0F, 0F, 0F, 0F), k(0.6F, 0F, 14F, 0F), k(2.5F, 4F, 3F, 0F)))
		.addAnimation("tail", rot(k(0F, 0F, 0F, 0F), k(0.6F, 0F, -14F, 0F), k(2.5F, -4F, -3F, 0F)))
		.build();

	private static AnimationChannel rot(Keyframe... frames) {
		return new AnimationChannel(ROT, frames);
	}

	private static Keyframe k(float t, float x, float y, float z) {
		return new Keyframe(t, KeyframeAnimations.degreeVec(x, y, z), SMOOTH);
	}

	private MorayAnimations() {}
}
