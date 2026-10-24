package com.aquamarine.client.entity.tentacle;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

public final class TentacleAnimations {
    private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
    private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
    public static final AnimationDefinition IDLE = clip(2.4F, true, 10, -4, "segment_1", "segment_2", "segment_3", "segment_4", "segment_5", "segment_6");
    public static final AnimationDefinition RISE = clip(1.2F, false, -28, 14, "segment_1", "segment_2", "segment_3");
    public static final AnimationDefinition SLAM = clip(1.35F, false, -34, 52, "segment_1", "segment_2", "segment_3");
    public static final AnimationDefinition GRAB = clip(1.6F, false, -25, 42, "segment_2", "segment_3", "segment_4");
    public static final AnimationDefinition RELEASE = clip(1.1F, false, 20, -10, "segment_3", "segment_4", "segment_5");

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
    private TentacleAnimations() {}
}
