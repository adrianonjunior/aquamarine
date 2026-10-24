package com.aquamarine.client.entity.young_kraken;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

public final class YoungKrakenAnimations {
    private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
    private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
    public static final AnimationDefinition IDLE = clip(2.2F, true, 3, -2, "mantle", "arm_0_1", "arm_2_1", "arm_4_1");
    public static final AnimationDefinition SWIM = clip(1.2F, true, -20, 8, "arm_0_0", "arm_1_0", "arm_2_0", "arm_3_0", "arm_4_0", "arm_5_0");
    public static final AnimationDefinition STUNNED = clip(1.1F, true, -12, 10, "mantle", "head");
    public static final AnimationDefinition HATCH = clip(1.8F, false, -8, 14, "head", "arm_0_0", "arm_3_0", "arm_4_0");
    public static final AnimationDefinition ATTACK = clip(1.25F, false, -16, 30, "head", "arm_0_0", "arm_3_0");
    public static final AnimationDefinition DEATH = clip(1.7F, false, 18, 80, "mantle", "arm_0_0", "arm_1_0", "arm_2_0");

    private static AnimationDefinition clip(float duration, boolean loop, float windup, float peak, String... bones) {
        var builder = AnimationDefinition.Builder.withLength(duration);
        if (loop) builder.looping();
        for (int i = 0; i < bones.length; i++) {
            float direction = i % 2 == 0 ? 1 : -1;
            builder.addAnimation(bones[i], new AnimationChannel(ROT,
                frame(0, 0), frame(duration * .28F, windup * direction),
                frame(duration * .58F, peak * direction), frame(duration, 0)));
        }
        return builder.build();
    }
    private static Keyframe frame(float time, float z) {
        Vector3f vector = KeyframeAnimations.degreeVec(0, 0, z);
        return new Keyframe(time, vector, SMOOTH);
    }
    private YoungKrakenAnimations() {}
}
