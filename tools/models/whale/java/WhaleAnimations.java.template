package com.aquamarine.client.entity.whale;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

public final class WhaleAnimations {
    private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
    private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(3.2F).looping()
        .addAnimation("body", rot(k(0,0,0,0),k(.9F,0,-1.5F,0),k(1.98F,0,0,0),k(3.2F,0,1.5F,0)))
        .addAnimation("tail", rot(k(0,0,0,0),k(.9F,0,0,3),k(1.98F,0,0,-3),k(3.2F,0,0,0)))
        .addAnimation("left_fin", rot(k(0,0,0,0),k(.9F,0,0,-3),k(1.98F,0,0,2),k(3.2F,0,0,0)))
        .addAnimation("right_fin", rot(k(0,0,0,0),k(.9F,0,0,3),k(1.98F,0,0,-2),k(3.2F,0,0,0))).build();
    public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(1.6F).looping()
        .addAnimation("body", rot(k(0,0,0,-2),k(.45F,0,0,3),k(.99F,0,0,-3),k(1.6F,0,0,-2)))
        .addAnimation("tail", rot(k(0,0,-8,0),k(.45F,0,16,0),k(.99F,0,-17,0),k(1.6F,0,-8,0)))
        .addAnimation("left_fin", rot(k(0,0,0,-4),k(.45F,0,0,13),k(.99F,0,0,-9),k(1.6F,0,0,-4)))
        .addAnimation("right_fin", rot(k(0,0,0,4),k(.45F,0,0,-13),k(.99F,0,0,9),k(1.6F,0,0,4))).build();
    public static final AnimationDefinition SING = AnimationDefinition.Builder.withLength(2.4F)
        .addAnimation("head", rot(k(0,0,0,0),k(.67F,0,0,-7),k(1.49F,0,0,4),k(2.4F,0,0,0)))
        .addAnimation("jaw", rot(k(0,0,0,0),k(.67F,0,0,10),k(1.49F,0,0,-3),k(2.4F,0,0,0)))
        .addAnimation("body", rot(k(0,0,0,0),k(.67F,0,-2,0),k(1.49F,0,3,0),k(2.4F,0,0,0)))
        .addAnimation("left_fin", rot(k(0,0,0,0),k(.67F,0,0,-14),k(1.49F,0,0,8),k(2.4F,0,0,0)))
        .addAnimation("right_fin", rot(k(0,0,0,0),k(.67F,0,0,14),k(1.49F,0,0,-8),k(2.4F,0,0,0))).build();
    public static final AnimationDefinition SURFACE = AnimationDefinition.Builder.withLength(2.0F)
        .addAnimation("body", rot(k(0,0,0,0),k(.56F,0,-10,0),k(1.24F,0,-18,0),k(2,0,0,0)))
        .addAnimation("head", rot(k(0,0,0,0),k(.56F,0,9,0),k(1.24F,0,14,0),k(2,0,0,0)))
        .addAnimation("tail", rot(k(0,0,0,0),k(.56F,18,0,0),k(1.24F,30,0,0),k(2,0,0,0)))
        .addAnimation("left_fin", rot(k(0,0,0,0),k(.56F,0,0,-12),k(1.24F,0,0,-20),k(2,0,0,0)))
        .addAnimation("right_fin", rot(k(0,0,0,0),k(.56F,0,0,12),k(1.24F,0,0,20),k(2,0,0,0))).build();
    public static final AnimationDefinition DEATH = AnimationDefinition.Builder.withLength(2.3F)
        .addAnimation("body", rot(k(0,0,0,0),k(.64F,0,0,-6),k(1.43F,0,0,12),k(2.3F,0,0,18)))
        .addAnimation("left_fin", rot(k(0,0,0,0),k(.64F,0,0,-18),k(1.43F,0,0,-35),k(2.3F,0,0,-48)))
        .addAnimation("right_fin", rot(k(0,0,0,0),k(.64F,0,0,18),k(1.43F,0,0,35),k(2.3F,0,0,48)))
        .addAnimation("tail", rot(k(0,0,0,0),k(.64F,0,0,12),k(1.43F,0,0,28),k(2.3F,0,0,35))).build();
    private static AnimationChannel rot(Keyframe... frames) { return new AnimationChannel(ROT, frames); }
    private static Keyframe k(float t, float x, float y, float z) { return new Keyframe(t, KeyframeAnimations.degreeVec(x,y,z), SMOOTH); }
    private WhaleAnimations() {}
}
