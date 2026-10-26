package com.aquamarine.client.entity.young_kraken;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** GERADO por tools/models/spawnlings/build_model.py: não edite à mão. */
public final class YoungKrakenAnimations {
	private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
	private static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;
	private static final AnimationChannel.Target SCALE = AnimationChannel.Targets.SCALE;
	private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
	private static final AnimationChannel.Interpolation LINEAR = AnimationChannel.Interpolations.LINEAR;

	public static final AnimationDefinition IDLE = idle();
	public static final AnimationDefinition SWIM = swim();
	public static final AnimationDefinition STUNNED = stunned();
	public static final AnimationDefinition HATCH = hatch();
	public static final AnimationDefinition ATTACK = attack();
	public static final AnimationDefinition DEATH = death();

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(2.2F).looping()
			.addAnimation("body", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.275F, pos(0F, 0.495F, 0F), SMOOTH),
				k(0.55F, pos(0F, 0.7F, 0F), SMOOTH),
				k(0.825F, pos(0F, 0.495F, 0F), SMOOTH),
				k(1.1F, pos(0F, 0F, 0F), SMOOTH),
				k(1.375F, pos(0F, -0.495F, 0F), SMOOTH),
				k(1.65F, pos(0F, -0.7F, 0F), SMOOTH),
				k(1.925F, pos(0F, -0.495F, 0F), SMOOTH),
				k(2.2F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("mantle", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.275F, scale(1.0283F, 0.9788F, 1.0283F), SMOOTH),
				k(0.55F, scale(1.04F, 0.97F, 1.04F), SMOOTH),
				k(0.825F, scale(1.0283F, 0.9788F, 1.0283F), SMOOTH),
				k(1.1F, scale(1F, 1F, 1F), SMOOTH),
				k(1.375F, scale(0.9717F, 1.0212F, 0.9717F), SMOOTH),
				k(1.65F, scale(0.96F, 1.03F, 0.96F), SMOOTH),
				k(1.925F, scale(0.9717F, 1.0212F, 0.9717F), SMOOTH),
				k(2.2F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("fin_left", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.275F, rot(0F, 0F, -14F), SMOOTH),
				k(0.55F, rot(0F, 0F, 0F), SMOOTH),
				k(0.825F, rot(0F, 0F, 14F), SMOOTH),
				k(1.1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.375F, rot(0F, 0F, -14F), SMOOTH),
				k(1.65F, rot(0F, 0F, 0F), SMOOTH),
				k(1.925F, rot(0F, 0F, 14F), SMOOTH),
				k(2.2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("fin_right", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.275F, rot(0F, 0F, 14F), SMOOTH),
				k(0.55F, rot(0F, 0F, 0F), SMOOTH),
				k(0.825F, rot(0F, 0F, -14F), SMOOTH),
				k(1.1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.375F, rot(0F, 0F, 14F), SMOOTH),
				k(1.65F, rot(0F, 0F, 0F), SMOOTH),
				k(1.925F, rot(0F, 0F, -14F), SMOOTH),
				k(2.2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 3F, 0F), SMOOTH),
				k(0.275F, rot(3.5355F, 2.1213F, 0F), SMOOTH),
				k(0.55F, rot(5F, 0F, 0F), SMOOTH),
				k(0.825F, rot(3.5355F, -2.1213F, 0F), SMOOTH),
				k(1.1F, rot(0F, -3F, 0F), SMOOTH),
				k(1.375F, rot(-3.5355F, -2.1213F, 0F), SMOOTH),
				k(1.65F, rot(-5F, 0F, 0F), SMOOTH),
				k(1.925F, rot(-3.5355F, 2.1213F, 0F), SMOOTH),
				k(2.2F, rot(0F, 3F, 0F), SMOOTH)))
			.addAnimation("arm_0_1", new AnimationChannel(ROT,
				k(0F, rot(-5.798F, 4.1267F, 0F), SMOOTH),
				k(0.275F, rot(0.7676F, 4.9143F, 0F), SMOOTH),
				k(0.55F, rot(6.8836F, 2.8232F, 0F), SMOOTH),
				k(0.825F, rot(8.9672F, -0.9217F, 0F), SMOOTH),
				k(1.1F, rot(5.798F, -4.1267F, 0F), SMOOTH),
				k(1.375F, rot(-0.7676F, -4.9143F, 0F), SMOOTH),
				k(1.65F, rot(-6.8836F, -2.8232F, 0F), SMOOTH),
				k(1.925F, rot(-8.9672F, 0.9217F, 0F), SMOOTH),
				k(2.2F, rot(-5.798F, 4.1267F, 0F), SMOOTH)))
			.addAnimation("arm_0_2", new AnimationChannel(ROT,
				k(0F, rot(-12.8108F, 2.5365F, 0F), SMOOTH),
				k(0.275F, rot(-7.4962F, 6.4069F, 0F), SMOOTH),
				k(0.55F, rot(2.2096F, 6.5243F, 0F), SMOOTH),
				k(0.825F, rot(10.621F, 2.8198F, 0F), SMOOTH),
				k(1.1F, rot(12.8108F, -2.5365F, 0F), SMOOTH),
				k(1.375F, rot(7.4962F, -6.4069F, 0F), SMOOTH),
				k(1.65F, rot(-2.2096F, -6.5243F, 0F), SMOOTH),
				k(1.925F, rot(-10.621F, -2.8198F, 0F), SMOOTH),
				k(2.2F, rot(-12.8108F, 2.5365F, 0F), SMOOTH)))
			.addAnimation("arm_0_3", new AnimationChannel(ROT,
				k(0F, rot(-14.6746F, -2.0448F, 0F), SMOOTH),
				k(0.275F, rot(-16.4451F, 4.7516F, 0F), SMOOTH),
				k(0.55F, rot(-8.5824F, 8.7646F, 0F), SMOOTH),
				k(0.825F, rot(4.3078F, 7.6434F, 0F), SMOOTH),
				k(1.1F, rot(14.6746F, 2.0448F, 0F), SMOOTH),
				k(1.375F, rot(16.4451F, -4.7516F, 0F), SMOOTH),
				k(1.65F, rot(8.5824F, -8.7646F, 0F), SMOOTH),
				k(1.925F, rot(-4.3078F, -7.6434F, 0F), SMOOTH),
				k(2.2F, rot(-14.6746F, -2.0448F, 0F), SMOOTH)))
			.addAnimation("arm_1_0", new AnimationChannel(ROT,
				k(0F, rot(4.3371F, 1.6209F, 0F), SMOOTH),
				k(0.275F, rot(4.826F, -0.6389F, 0F), SMOOTH),
				k(0.55F, rot(2.4879F, -2.5244F, 0F), SMOOTH),
				k(0.825F, rot(-1.3076F, -2.9312F, 0F), SMOOTH),
				k(1.1F, rot(-4.3371F, -1.6209F, 0F), SMOOTH),
				k(1.375F, rot(-4.826F, 0.6389F, 0F), SMOOTH),
				k(1.65F, rot(-2.4879F, 2.5244F, 0F), SMOOTH),
				k(1.925F, rot(1.3076F, 2.9312F, 0F), SMOOTH),
				k(2.2F, rot(4.3371F, 1.6209F, 0F), SMOOTH)))
			.addAnimation("arm_1_1", new AnimationChannel(ROT,
				k(0F, rot(3.0861F, 4.6053F, 0F), SMOOTH),
				k(0.275F, rot(8.1603F, 1.8796F, 0F), SMOOTH),
				k(0.55F, rot(8.4544F, -1.9471F, 0F), SMOOTH),
				k(0.825F, rot(3.7959F, -4.6332F, 0F), SMOOTH),
				k(1.1F, rot(-3.0861F, -4.6053F, 0F), SMOOTH),
				k(1.375F, rot(-8.1603F, -1.8796F, 0F), SMOOTH),
				k(1.65F, rot(-8.4544F, 1.9471F, 0F), SMOOTH),
				k(1.925F, rot(-3.7959F, 4.6332F, 0F), SMOOTH),
				k(2.2F, rot(3.0861F, 4.6053F, 0F), SMOOTH)))
			.addAnimation("arm_1_2", new AnimationChannel(ROT,
				k(0F, rot(-4.4577F, 6.8605F, 0F), SMOOTH),
				k(0.275F, rot(5.483F, 5.8344F, 0F), SMOOTH),
				k(0.55F, rot(12.2118F, 1.3907F, 0F), SMOOTH),
				k(0.825F, rot(11.7871F, -3.8677F, 0F), SMOOTH),
				k(1.1F, rot(4.4577F, -6.8605F, 0F), SMOOTH),
				k(1.375F, rot(-5.483F, -5.8344F, 0F), SMOOTH),
				k(1.65F, rot(-12.2118F, -1.3907F, 0F), SMOOTH),
				k(1.925F, rot(-11.7871F, 3.8677F, 0F), SMOOTH),
				k(2.2F, rot(-4.4577F, 6.8605F, 0F), SMOOTH)))
			.addAnimation("arm_1_3", new AnimationChannel(ROT,
				k(0F, rot(-14.7462F, 6.2704F, 0F), SMOOTH),
				k(0.275F, rot(-4.4459F, 8.999F, 0F), SMOOTH),
				k(0.55F, rot(8.4587F, 6.4562F, 0F), SMOOTH),
				k(0.825F, rot(16.4083F, 0.1314F, 0F), SMOOTH),
				k(1.1F, rot(14.7462F, -6.2704F, 0F), SMOOTH),
				k(1.375F, rot(4.4459F, -8.999F, 0F), SMOOTH),
				k(1.65F, rot(-8.4587F, -6.4562F, 0F), SMOOTH),
				k(1.925F, rot(-16.4083F, -0.1314F, 0F), SMOOTH),
				k(2.2F, rot(-14.7462F, 6.2704F, 0F), SMOOTH)))
			.addAnimation("arm_2_0", new AnimationChannel(ROT,
				k(0F, rot(4.316F, -1.2484F, 0F), SMOOTH),
				k(0.275F, rot(1.267F, -2.8117F, 0F), SMOOTH),
				k(0.55F, rot(-2.5242F, -2.7279F, 0F), SMOOTH),
				k(0.825F, rot(-4.8368F, -1.0461F, 0F), SMOOTH),
				k(1.1F, rot(-4.316F, 1.2484F, 0F), SMOOTH),
				k(1.375F, rot(-1.267F, 2.8117F, 0F), SMOOTH),
				k(1.65F, rot(2.5242F, 2.7279F, 0F), SMOOTH),
				k(1.925F, rot(4.8368F, 1.0461F, 0F), SMOOTH),
				k(2.2F, rot(4.316F, -1.2484F, 0F), SMOOTH)))
			.addAnimation("arm_2_1", new AnimationChannel(ROT,
				k(0F, rot(8.869F, 0.8498F, 0F), SMOOTH),
				k(0.275F, rot(7.353F, -2.8832F, 0F), SMOOTH),
				k(0.55F, rot(1.5297F, -4.9272F, 0F), SMOOTH),
				k(0.825F, rot(-5.1897F, -4.085F, 0F), SMOOTH),
				k(1.1F, rot(-8.869F, -0.8498F, 0F), SMOOTH),
				k(1.375F, rot(-7.353F, 2.8832F, 0F), SMOOTH),
				k(1.65F, rot(-1.5297F, 4.9272F, 0F), SMOOTH),
				k(1.925F, rot(5.1897F, 4.085F, 0F), SMOOTH),
				k(2.2F, rot(8.869F, 0.8498F, 0F), SMOOTH)))
			.addAnimation("arm_2_2", new AnimationChannel(ROT,
				k(0F, rot(8.3748F, 4.8769F, 0F), SMOOTH),
				k(0.275F, rot(12.9526F, -0.1022F, 0F), SMOOTH),
				k(0.55F, rot(9.9429F, -5.0215F, 0F), SMOOTH),
				k(0.825F, rot(1.1088F, -6.9993F, 0F), SMOOTH),
				k(1.1F, rot(-8.3748F, -4.8769F, 0F), SMOOTH),
				k(1.375F, rot(-12.9526F, 0.1022F, 0F), SMOOTH),
				k(1.65F, rot(-9.9429F, 5.0215F, 0F), SMOOTH),
				k(1.925F, rot(-1.1088F, 6.9993F, 0F), SMOOTH),
				k(2.2F, rot(8.3748F, 4.8769F, 0F), SMOOTH)))
			.addAnimation("arm_2_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 8.8206F, 0F), SMOOTH),
				k(0.275F, rot(12.0208F, 4.9728F, 0F), SMOOTH),
				k(0.55F, rot(17F, -1.788F, 0F), SMOOTH),
				k(0.825F, rot(12.0208F, -7.5014F, 0F), SMOOTH),
				k(1.1F, rot(0F, -8.8206F, 0F), SMOOTH),
				k(1.375F, rot(-12.0208F, -4.9728F, 0F), SMOOTH),
				k(1.65F, rot(-17F, 1.788F, 0F), SMOOTH),
				k(1.925F, rot(-12.0208F, 7.5014F, 0F), SMOOTH),
				k(2.2F, rot(0F, 8.8206F, 0F), SMOOTH)))
			.addAnimation("arm_3_0", new AnimationChannel(ROT,
				k(0F, rot(-0.042F, -2.97F, 0F), SMOOTH),
				k(0.275F, rot(-3.5651F, -2.3995F, 0F), SMOOTH),
				k(0.55F, rot(-4.9998F, -0.4234F, 0F), SMOOTH),
				k(0.825F, rot(-3.5057F, 1.8007F, 0F), SMOOTH),
				k(1.1F, rot(0.042F, 2.97F, 0F), SMOOTH),
				k(1.375F, rot(3.5651F, 2.3995F, 0F), SMOOTH),
				k(1.65F, rot(4.9998F, 0.4234F, 0F), SMOOTH),
				k(1.925F, rot(3.5057F, -1.8007F, 0F), SMOOTH),
				k(2.2F, rot(-0.042F, -2.97F, 0F), SMOOTH)))
			.addAnimation("arm_3_1", new AnimationChannel(ROT,
				k(0F, rot(5.7399F, -3.687F, 0F), SMOOTH),
				k(0.275F, rot(-0.843F, -4.9952F, 0F), SMOOTH),
				k(0.55F, rot(-6.9321F, -3.3773F, 0F), SMOOTH),
				k(0.825F, rot(-8.9604F, 0.219F, 0F), SMOOTH),
				k(1.1F, rot(-5.7399F, 3.687F, 0F), SMOOTH),
				k(1.375F, rot(0.843F, 4.9952F, 0F), SMOOTH),
				k(1.65F, rot(6.9321F, 3.3773F, 0F), SMOOTH),
				k(1.925F, rot(8.9604F, -0.219F, 0F), SMOOTH),
				k(2.2F, rot(5.7399F, -3.687F, 0F), SMOOTH)))
			.addAnimation("arm_3_2", new AnimationChannel(ROT,
				k(0F, rot(12.7918F, -1.5904F, 0F), SMOOTH),
				k(0.275F, rot(7.4067F, -5.9449F, 0F), SMOOTH),
				k(0.55F, rot(-2.3172F, -6.8169F, 0F), SMOOTH),
				k(0.825F, rot(-10.6837F, -3.6957F, 0F), SMOOTH),
				k(1.1F, rot(-12.7918F, 1.5904F, 0F), SMOOTH),
				k(1.375F, rot(-7.4067F, 5.9449F, 0F), SMOOTH),
				k(1.65F, rot(2.3172F, 6.8169F, 0F), SMOOTH),
				k(1.925F, rot(10.6837F, 3.6957F, 0F), SMOOTH),
				k(2.2F, rot(12.7918F, -1.5904F, 0F), SMOOTH)))
			.addAnimation("arm_3_3", new AnimationChannel(ROT,
				k(0F, rot(14.7462F, 3.2612F, 0F), SMOOTH),
				k(0.275F, rot(16.4083F, -3.6254F, 0F), SMOOTH),
				k(0.55F, rot(8.4587F, -8.3884F, 0F), SMOOTH),
				k(0.825F, rot(-4.4459F, -8.2375F, 0F), SMOOTH),
				k(1.1F, rot(-14.7462F, -3.2612F, 0F), SMOOTH),
				k(1.375F, rot(-16.4083F, 3.6254F, 0F), SMOOTH),
				k(1.65F, rot(-8.4587F, 8.3884F, 0F), SMOOTH),
				k(1.925F, rot(4.4459F, 8.2375F, 0F), SMOOTH),
				k(2.2F, rot(14.7462F, 3.2612F, 0F), SMOOTH)))
			.addAnimation("arm_4_0", new AnimationChannel(ROT,
				k(0F, rot(-4.3579F, -1.9609F, 0F), SMOOTH),
				k(0.275F, rot(-4.8148F, 0.2188F, 0F), SMOOTH),
				k(0.55F, rot(-2.4513F, 2.2704F, 0F), SMOOTH),
				k(0.825F, rot(1.3482F, 2.992F, 0F), SMOOTH),
				k(1.1F, rot(4.3579F, 1.9609F, 0F), SMOOTH),
				k(1.375F, rot(4.8148F, -0.2188F, 0F), SMOOTH),
				k(1.65F, rot(2.4513F, -2.2704F, 0F), SMOOTH),
				k(1.925F, rot(-1.3482F, -2.992F, 0F), SMOOTH),
				k(2.2F, rot(-4.3579F, -1.9609F, 0F), SMOOTH)))
			.addAnimation("arm_4_1", new AnimationChannel(ROT,
				k(0F, rot(-3.157F, -4.834F, 0F), SMOOTH),
				k(0.275F, rot(-8.1919F, -2.5147F, 0F), SMOOTH),
				k(0.55F, rot(-8.4281F, 1.2777F, 0F), SMOOTH),
				k(0.825F, rot(-3.7272F, 4.3216F, 0F), SMOOTH),
				k(1.1F, rot(3.157F, 4.834F, 0F), SMOOTH),
				k(1.375F, rot(8.1919F, 2.5147F, 0F), SMOOTH),
				k(1.65F, rot(8.4281F, -1.2777F, 0F), SMOOTH),
				k(1.925F, rot(3.7272F, -4.3216F, 0F), SMOOTH),
				k(2.2F, rot(-3.157F, -4.834F, 0F), SMOOTH)))
			.addAnimation("arm_4_2", new AnimationChannel(ROT,
				k(0F, rot(4.3548F, -6.5956F, 0F), SMOOTH),
				k(0.275F, rot(-5.5819F, -6.3219F, 0F), SMOOTH),
				k(0.55F, rot(-12.2489F, -2.3449F, 0F), SMOOTH),
				k(0.825F, rot(-11.7406F, 3.0057F, 0F), SMOOTH),
				k(1.1F, rot(-4.3548F, 6.5956F, 0F), SMOOTH),
				k(1.375F, rot(5.5819F, 6.3219F, 0F), SMOOTH),
				k(1.65F, rot(12.2489F, 2.3449F, 0F), SMOOTH),
				k(1.925F, rot(11.7406F, -3.0057F, 0F), SMOOTH),
				k(2.2F, rot(4.3548F, -6.5956F, 0F), SMOOTH)))
			.addAnimation("arm_4_3", new AnimationChannel(ROT,
				k(0F, rot(14.6746F, -5.2965F, 0F), SMOOTH),
				k(0.275F, rot(4.3078F, -8.8904F, 0F), SMOOTH),
				k(0.55F, rot(-8.5824F, -7.2765F, 0F), SMOOTH),
				k(0.825F, rot(-16.4451F, -1.4F, 0F), SMOOTH),
				k(1.1F, rot(-14.6746F, 5.2965F, 0F), SMOOTH),
				k(1.375F, rot(-4.3078F, 8.8904F, 0F), SMOOTH),
				k(1.65F, rot(8.5824F, 7.2765F, 0F), SMOOTH),
				k(1.925F, rot(16.4451F, 1.4F, 0F), SMOOTH),
				k(2.2F, rot(14.6746F, -5.2965F, 0F), SMOOTH)))
			.addAnimation("arm_5_0", new AnimationChannel(ROT,
				k(0F, rot(-4.2947F, 0.851F, 0F), SMOOTH),
				k(0.275F, rot(-1.2263F, 2.6359F, 0F), SMOOTH),
				k(0.55F, rot(2.5604F, 2.8768F, 0F), SMOOTH),
				k(0.825F, rot(4.8473F, 1.4324F, 0F), SMOOTH),
				k(1.1F, rot(4.2947F, -0.851F, 0F), SMOOTH),
				k(1.375F, rot(1.2263F, -2.6359F, 0F), SMOOTH),
				k(1.65F, rot(-2.5604F, -2.8768F, 0F), SMOOTH),
				k(1.925F, rot(-4.8473F, -1.4324F, 0F), SMOOTH),
				k(2.2F, rot(-4.2947F, 0.851F, 0F), SMOOTH)))
			.addAnimation("arm_5_1", new AnimationChannel(ROT,
				k(0F, rot(-8.8816F, -1.5367F, 0F), SMOOTH),
				k(0.275F, rot(-7.3091F, 2.2778F, 0F), SMOOTH),
				k(0.55F, rot(-1.4551F, 4.758F, 0F), SMOOTH),
				k(0.825F, rot(5.2513F, 4.451F, 0F), SMOOTH),
				k(1.1F, rot(8.8816F, 1.5367F, 0F), SMOOTH),
				k(1.375F, rot(7.3091F, -2.2778F, 0F), SMOOTH),
				k(1.65F, rot(1.4551F, -4.758F, 0F), SMOOTH),
				k(1.925F, rot(-5.2513F, -4.451F, 0F), SMOOTH),
				k(2.2F, rot(-8.8816F, -1.5367F, 0F), SMOOTH)))
			.addAnimation("arm_5_2", new AnimationChannel(ROT,
				k(0F, rot(-8.4581F, -5.5368F, 0F), SMOOTH),
				k(0.275F, rot(-12.9615F, -0.8865F, 0F), SMOOTH),
				k(0.55F, rot(-9.8722F, 4.283F, 0F), SMOOTH),
				k(0.825F, rot(-0.9999F, 6.9436F, 0F), SMOOTH),
				k(1.1F, rot(8.4581F, 5.5368F, 0F), SMOOTH),
				k(1.375F, rot(12.9615F, 0.8865F, 0F), SMOOTH),
				k(1.65F, rot(9.8722F, -4.283F, 0F), SMOOTH),
				k(1.925F, rot(0.9999F, -6.9436F, 0F), SMOOTH),
				k(2.2F, rot(-8.4581F, -5.5368F, 0F), SMOOTH)))
			.addAnimation("arm_5_3", new AnimationChannel(ROT,
				k(0F, rot(-0.1429F, -8.9847F, 0F), SMOOTH),
				k(0.275F, rot(-12.1215F, -5.9816F, 0F), SMOOTH),
				k(0.55F, rot(-16.9994F, 0.5254F, 0F), SMOOTH),
				k(0.825F, rot(-11.9193F, 6.7246F, 0F), SMOOTH),
				k(1.1F, rot(0.1429F, 8.9847F, 0F), SMOOTH),
				k(1.375F, rot(12.1215F, 5.9816F, 0F), SMOOTH),
				k(1.65F, rot(16.9994F, -0.5254F, 0F), SMOOTH),
				k(1.925F, rot(11.9193F, -6.7246F, 0F), SMOOTH),
				k(2.2F, rot(-0.1429F, -8.9847F, 0F), SMOOTH)))
			.addAnimation("feeder_l_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.275F, rot(4.2426F, 0F, 0F), SMOOTH),
				k(0.55F, rot(6F, 0F, 0F), SMOOTH),
				k(0.825F, rot(4.2426F, 0F, 0F), SMOOTH),
				k(1.1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.375F, rot(-4.2426F, 0F, 0F), SMOOTH),
				k(1.65F, rot(-6F, 0F, 0F), SMOOTH),
				k(1.925F, rot(-4.2426F, 0F, 0F), SMOOTH),
				k(2.2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_1", new AnimationChannel(ROT,
				k(0F, rot(-2.6098F, 0F, 0F), SMOOTH),
				k(0.275F, rot(1.9749F, 0F, 0F), SMOOTH),
				k(0.55F, rot(5.4027F, 0F, 0F), SMOOTH),
				k(0.825F, rot(5.6657F, 0F, 0F), SMOOTH),
				k(1.1F, rot(2.6098F, 0F, 0F), SMOOTH),
				k(1.375F, rot(-1.9749F, 0F, 0F), SMOOTH),
				k(1.65F, rot(-5.4027F, 0F, 0F), SMOOTH),
				k(1.925F, rot(-5.6657F, 0F, 0F), SMOOTH),
				k(2.2F, rot(-2.6098F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_2", new AnimationChannel(ROT,
				k(0F, rot(-4.7F, 0F, 0F), SMOOTH),
				k(0.275F, rot(-0.6861F, 0F, 0F), SMOOTH),
				k(0.55F, rot(3.7297F, 0F, 0F), SMOOTH),
				k(0.825F, rot(5.9606F, 0F, 0F), SMOOTH),
				k(1.1F, rot(4.7F, 0F, 0F), SMOOTH),
				k(1.375F, rot(0.6861F, 0F, 0F), SMOOTH),
				k(1.65F, rot(-3.7297F, 0F, 0F), SMOOTH),
				k(1.925F, rot(-5.9606F, 0F, 0F), SMOOTH),
				k(2.2F, rot(-4.7F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_3", new AnimationChannel(ROT,
				k(0F, rot(-5.8543F, 0F, 0F), SMOOTH),
				k(0.275F, rot(-3.2105F, 0F, 0F), SMOOTH),
				k(0.55F, rot(1.314F, 0F, 0F), SMOOTH),
				k(0.825F, rot(5.0688F, 0F, 0F), SMOOTH),
				k(1.1F, rot(5.8543F, 0F, 0F), SMOOTH),
				k(1.375F, rot(3.2105F, 0F, 0F), SMOOTH),
				k(1.65F, rot(-1.314F, 0F, 0F), SMOOTH),
				k(1.925F, rot(-5.0688F, 0F, 0F), SMOOTH),
				k(2.2F, rot(-5.8543F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_4", new AnimationChannel(ROT,
				k(0F, rot(-5.8431F, 0F, 0F), SMOOTH),
				k(0.275F, rot(-5.0956F, 0F, 0F), SMOOTH),
				k(0.55F, rot(-1.3632F, 0F, 0F), SMOOTH),
				k(0.825F, rot(3.1677F, 0F, 0F), SMOOTH),
				k(1.1F, rot(5.8431F, 0F, 0F), SMOOTH),
				k(1.375F, rot(5.0956F, 0F, 0F), SMOOTH),
				k(1.65F, rot(1.3632F, 0F, 0F), SMOOTH),
				k(1.925F, rot(-3.1677F, 0F, 0F), SMOOTH),
				k(2.2F, rot(-5.8431F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_club", new AnimationChannel(ROT,
				k(0F, rot(-4.6684F, 0F, 0F), SMOOTH),
				k(0.275F, rot(-5.9662F, 0F, 0F), SMOOTH),
				k(0.55F, rot(-3.769F, 0F, 0F), SMOOTH),
				k(0.825F, rot(0.636F, 0F, 0F), SMOOTH),
				k(1.1F, rot(4.6684F, 0F, 0F), SMOOTH),
				k(1.375F, rot(5.9662F, 0F, 0F), SMOOTH),
				k(1.65F, rot(3.769F, 0F, 0F), SMOOTH),
				k(1.925F, rot(-0.636F, 0F, 0F), SMOOTH),
				k(2.2F, rot(-4.6684F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_0", new AnimationChannel(ROT,
				k(0F, rot(-2.5643F, 0F, 0F), SMOOTH),
				k(0.275F, rot(-5.6489F, 0F, 0F), SMOOTH),
				k(0.55F, rot(-5.4244F, 0F, 0F), SMOOTH),
				k(0.825F, rot(-2.0224F, 0F, 0F), SMOOTH),
				k(1.1F, rot(2.5643F, 0F, 0F), SMOOTH),
				k(1.375F, rot(5.6489F, 0F, 0F), SMOOTH),
				k(1.65F, rot(5.4244F, 0F, 0F), SMOOTH),
				k(1.925F, rot(2.0224F, 0F, 0F), SMOOTH),
				k(2.2F, rot(-2.5643F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_1", new AnimationChannel(ROT,
				k(0F, rot(0.0504F, 0F, 0F), SMOOTH),
				k(0.275F, rot(-4.2068F, 0F, 0F), SMOOTH),
				k(0.55F, rot(-5.9998F, 0F, 0F), SMOOTH),
				k(0.825F, rot(-4.2782F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-0.0504F, 0F, 0F), SMOOTH),
				k(1.375F, rot(4.2068F, 0F, 0F), SMOOTH),
				k(1.65F, rot(5.9998F, 0F, 0F), SMOOTH),
				k(1.925F, rot(4.2782F, 0F, 0F), SMOOTH),
				k(2.2F, rot(0.0504F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_2", new AnimationChannel(ROT,
				k(0F, rot(2.6551F, 0F, 0F), SMOOTH),
				k(0.275F, rot(-1.9272F, 0F, 0F), SMOOTH),
				k(0.55F, rot(-5.3806F, 0F, 0F), SMOOTH),
				k(0.825F, rot(-5.6821F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-2.6551F, 0F, 0F), SMOOTH),
				k(1.375F, rot(1.9272F, 0F, 0F), SMOOTH),
				k(1.65F, rot(5.3806F, 0F, 0F), SMOOTH),
				k(1.925F, rot(5.6821F, 0F, 0F), SMOOTH),
				k(2.2F, rot(2.6551F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_3", new AnimationChannel(ROT,
				k(0F, rot(4.7312F, 0F, 0F), SMOOTH),
				k(0.275F, rot(0.7362F, 0F, 0F), SMOOTH),
				k(0.55F, rot(-3.69F, 0F, 0F), SMOOTH),
				k(0.825F, rot(-5.9547F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-4.7312F, 0F, 0F), SMOOTH),
				k(1.375F, rot(-0.7362F, 0F, 0F), SMOOTH),
				k(1.65F, rot(3.69F, 0F, 0F), SMOOTH),
				k(1.925F, rot(5.9547F, 0F, 0F), SMOOTH),
				k(2.2F, rot(4.7312F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_4", new AnimationChannel(ROT,
				k(0F, rot(5.8652F, 0F, 0F), SMOOTH),
				k(0.275F, rot(3.253F, 0F, 0F), SMOOTH),
				k(0.55F, rot(-1.2648F, 0F, 0F), SMOOTH),
				k(0.825F, rot(-5.0416F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-5.8652F, 0F, 0F), SMOOTH),
				k(1.375F, rot(-3.253F, 0F, 0F), SMOOTH),
				k(1.65F, rot(1.2648F, 0F, 0F), SMOOTH),
				k(1.925F, rot(5.0416F, 0F, 0F), SMOOTH),
				k(2.2F, rot(5.8652F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_club", new AnimationChannel(ROT,
				k(0F, rot(5.8314F, 0F, 0F), SMOOTH),
				k(0.275F, rot(5.1221F, 0F, 0F), SMOOTH),
				k(0.55F, rot(1.4123F, 0F, 0F), SMOOTH),
				k(0.825F, rot(-3.1248F, 0F, 0F), SMOOTH),
				k(1.1F, rot(-5.8314F, 0F, 0F), SMOOTH),
				k(1.375F, rot(-5.1221F, 0F, 0F), SMOOTH),
				k(1.65F, rot(-1.4123F, 0F, 0F), SMOOTH),
				k(1.925F, rot(3.1248F, 0F, 0F), SMOOTH),
				k(2.2F, rot(5.8314F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition swim() {
		return AnimationDefinition.Builder.withLength(1.2F).looping()
			.addAnimation("body", new AnimationChannel(ROT,
				k(0F, rot(-24F, 0F, 0F), SMOOTH),
				k(1.2F, rot(-24F, 0F, 0F), SMOOTH)))
			.addAnimation("mantle", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.3F, scale(0.86F, 1.1F, 0.86F), SMOOTH),
				k(0.6F, scale(1.06F, 0.96F, 1.06F), SMOOTH),
				k(1.2F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("body", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.3F, pos(0F, 0F, -1.2F), SMOOTH),
				k(0.6F, pos(0F, 0F, -0.4F), SMOOTH),
				k(1.2F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_0", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-22F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_1", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-18F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_2", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-14F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_3", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-10F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_0", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-22F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_1", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-18F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_2", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-14F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_3", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-10F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_0", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-22F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_1", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-18F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_2", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-14F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_3", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-10F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_0", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-22F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_1", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-18F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_2", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-14F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_3", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-10F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_0", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-22F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_1", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-18F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_2", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-14F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_3", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-10F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_0", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-22F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_1", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-18F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_2", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-14F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_3", new AnimationChannel(ROT,
				k(0F, rot(8F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-10F, 0F, 0F), SMOOTH),
				k(0.6F, rot(18F, 0F, 0F), SMOOTH),
				k(1.2F, rot(8F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_0", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_1", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_2", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_3", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_4", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_club", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_0", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_1", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_2", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_3", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_4", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_club", new AnimationChannel(ROT,
				k(0F, rot(4F, 0F, 0F), SMOOTH),
				k(0.3F, rot(-12F, 0F, 0F), SMOOTH),
				k(0.7F, rot(10F, 0F, 0F), SMOOTH),
				k(1.2F, rot(4F, 0F, 0F), SMOOTH)))
			.addAnimation("fin_left", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.1F, rot(0F, 0F, -26F), SMOOTH),
				k(0.2F, rot(0F, 0F, 0F), SMOOTH),
				k(0.3F, rot(0F, 0F, 26F), SMOOTH),
				k(0.4F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0F, 0F, -26F), SMOOTH),
				k(0.6F, rot(0F, 0F, 0F), SMOOTH),
				k(0.7F, rot(0F, 0F, 26F), SMOOTH),
				k(0.8F, rot(0F, 0F, 0F), SMOOTH),
				k(0.9F, rot(0F, 0F, -26F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.1F, rot(0F, 0F, 26F), SMOOTH),
				k(1.2F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("fin_right", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.1F, rot(0F, 0F, 26F), SMOOTH),
				k(0.2F, rot(0F, 0F, 0F), SMOOTH),
				k(0.3F, rot(0F, 0F, -26F), SMOOTH),
				k(0.4F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0F, 0F, 26F), SMOOTH),
				k(0.6F, rot(0F, 0F, 0F), SMOOTH),
				k(0.7F, rot(0F, 0F, -26F), SMOOTH),
				k(0.8F, rot(0F, 0F, 0F), SMOOTH),
				k(0.9F, rot(0F, 0F, 26F), SMOOTH),
				k(1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.1F, rot(0F, 0F, -26F), SMOOTH),
				k(1.2F, rot(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition stunned() {
		return AnimationDefinition.Builder.withLength(1.1F).looping()
			.addAnimation("body", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, -18F), SMOOTH),
				k(0.55F, rot(0F, 0F, 18F), SMOOTH),
				k(1.1F, rot(0F, 0F, -18F), SMOOTH)))
			.addAnimation("head", new AnimationChannel(ROT,
				k(0F, rot(10F, 0F, 0F), SMOOTH),
				k(0.55F, rot(16F, 0F, 0F), SMOOTH),
				k(1.1F, rot(10F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_0", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_1", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_2", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_3", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_0", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_1", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_2", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_3", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_0", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_1", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_2", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_3", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_0", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_1", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_2", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_3", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_0", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_1", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_2", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_3", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_0", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_1", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_2", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_3", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_0", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_1", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_2", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_3", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_4", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_club", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_0", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_1", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_2", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_3", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_4", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_club", new AnimationChannel(ROT,
				k(0F, rot(22F, 0F, 0F), SMOOTH),
				k(0.55F, rot(30F, 0F, 0F), SMOOTH),
				k(1.1F, rot(22F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition hatch() {
		return AnimationDefinition.Builder.withLength(1.8F)
			.addAnimation("body", new AnimationChannel(SCALE,
				k(0F, scale(0.25F, 0.25F, 0.25F), SMOOTH),
				k(0.6F, scale(0.7F, 0.5F, 0.7F), SMOOTH),
				k(1F, scale(1.15F, 1.2F, 1.15F), SMOOTH),
				k(1.3F, scale(0.95F, 0.95F, 0.95F), SMOOTH),
				k(1.8F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("body", new AnimationChannel(POS,
				k(0F, pos(0F, -4F, 0F), SMOOTH),
				k(1F, pos(0F, 1.5F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_0", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_1", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_2", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_3", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_0", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_1", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_2", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_3", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_0", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_1", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_2", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_3", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_0", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_1", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_2", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_3", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_0", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_1", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_2", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_3", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_0", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_1", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_2", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_3", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_0", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_1", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_2", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_3", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_4", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_club", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_0", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_1", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_2", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_3", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_4", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_club", new AnimationChannel(ROT,
				k(0F, rot(-60F, 0F, 0F), SMOOTH),
				k(0.8F, rot(-40F, 0F, 0F), SMOOTH),
				k(1.2F, rot(18F, 0F, 0F), SMOOTH),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("body", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(1.2F, rot(0F, 0F, 8F), LINEAR),
				k(1.28F, rot(0F, 0F, -8F), LINEAR),
				k(1.36F, rot(0F, 0F, 8F), LINEAR),
				k(1.44F, rot(0F, 0F, -8F), LINEAR),
				k(1.52F, rot(0F, 0F, 8F), LINEAR),
				k(1.6F, rot(0F, 0F, -8F), LINEAR),
				k(1.8F, rot(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition attack() {
		return AnimationDefinition.Builder.withLength(1.25F)
			.addAnimation("mantle", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.42F, scale(1.14F, 1.06F, 1.14F), SMOOTH),
				k(0.55F, scale(0.88F, 0.94F, 0.88F), LINEAR),
				k(1.25F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("body", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.42F, pos(0F, 0F, 1.5F), SMOOTH),
				k(0.56F, pos(0F, 0F, -4.5F), LINEAR),
				k(0.8F, pos(0F, 0F, -2F), SMOOTH),
				k(1.25F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("beak_lower", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.45F, rot(0F, 0F, 0F), SMOOTH),
				k(0.55F, rot(32F, 0F, 0F), LINEAR),
				k(0.75F, rot(5F, 0F, 0F), SMOOTH),
				k(0.85F, rot(28F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(40F, 0F, 0F), SMOOTH),
				k(0.56F, rot(-34F, 0F, 0F), LINEAR),
				k(0.9F, rot(-10F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_4", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_club", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_4", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_club", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.42F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.56F, rot(44F, 0F, 0F), LINEAR),
				k(0.9F, rot(12F, 0F, 0F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition death() {
		return AnimationDefinition.Builder.withLength(1.7F)
			.addAnimation("body", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.05F, rot(8F, 0F, 10F), LINEAR),
				k(0.11F, rot(-8F, 0F, -10F), LINEAR),
				k(0.17F, rot(8F, 0F, 10F), LINEAR),
				k(0.23F, rot(-8F, 0F, -10F), LINEAR),
				k(0.29F, rot(8F, 0F, 10F), LINEAR),
				k(0.35F, rot(-8F, 0F, -10F), LINEAR),
				k(0.41F, rot(8F, 0F, 10F), LINEAR),
				k(0.47F, rot(-8F, 0F, -10F), LINEAR),
				k(1.4F, rot(0F, 0F, 175F), SMOOTH),
				k(1.7F, rot(0F, 0F, 180F), SMOOTH)))
			.addAnimation("body", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.5F, pos(0F, 1F, 0F), SMOOTH),
				k(1.7F, pos(0F, -5F, 0F), SMOOTH)))
			.addAnimation("arm_0_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_0_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_1_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_2_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_3_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_4_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("arm_5_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_4", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_l_club", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_0", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_1", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_2", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_3", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_4", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
			.addAnimation("feeder_r_club", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(20F, 0F, 0F), SMOOTH),
				k(1.7F, rot(-70F, 0F, 0F), SMOOTH)))
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

	private YoungKrakenAnimations() {}
}
