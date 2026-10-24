package com.aquamarine.client.entity.kraken_egg;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

public final class KrakenEggAnimations {
    private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
    private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
    public static final AnimationDefinition PULSE = clip(2.0F, true, 3, -2, "shell", "tendril_-1", "tendril_1");
    public static final AnimationDefinition HATCH = clip(1.8F, false, 18, -8, "cap", "tendril_-1", "tendril_1");

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
    private KrakenEggAnimations() {}
}
