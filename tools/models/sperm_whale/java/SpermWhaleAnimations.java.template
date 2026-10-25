package com.aquamarine.client.entity.sperm_whale;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

public final class SpermWhaleAnimations {
    private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
    private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(3.4F).looping()
        .addAnimation("body",rot(k(0,0,0,0),k(.95F,0,-1,0),k(2.1F,0,1,0),k(3.4F,0,0,0)))
        .addAnimation("tail",rot(k(0,0,0,0),k(.95F,0,0,2),k(2.1F,0,0,-2),k(3.4F,0,0,0)))
        .addAnimation("head",rot(k(0,0,0,0),k(.95F,1,0,0),k(2.1F,-1,0,0),k(3.4F,0,0,0))).build();
    public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(1.5F).looping()
        .addAnimation("body",rot(k(0,0,0,-2),k(.42F,0,0,3),k(.93F,0,0,-3),k(1.5F,0,0,-2)))
        .addAnimation("tail",rot(k(0,0,-8,0),k(.42F,0,17,0),k(.93F,0,-18,0),k(1.5F,0,-8,0)))
        .addAnimation("left_fin",rot(k(0,0,0,-3),k(.42F,0,0,11),k(.93F,0,0,-8),k(1.5F,0,0,-3)))
        .addAnimation("right_fin",rot(k(0,0,0,3),k(.42F,0,0,-11),k(.93F,0,0,8),k(1.5F,0,0,3))).build();
    public static final AnimationDefinition SONIC = AnimationDefinition.Builder.withLength(1.8F)
        .addAnimation("head",rot(k(0,0,0,0),k(.5F,0,-7,0),k(1.12F,0,8,0),k(1.8F,0,0,0)))
        .addAnimation("jaw",rot(k(0,0,0,0),k(.5F,0,0,5),k(1.12F,0,0,-14),k(1.8F,0,0,0)))
        .addAnimation("body",rot(k(0,0,0,0),k(.5F,0,-3,0),k(1.12F,0,6,0),k(1.8F,0,0,0)))
        .addAnimation("tail",rot(k(0,0,0,0),k(.5F,0,0,-9),k(1.12F,0,0,18),k(1.8F,0,0,0)))
        .addAnimation("left_fin",rot(k(0,0,0,0),k(.5F,0,0,-8),k(1.12F,0,0,14),k(1.8F,0,0,0)))
        .addAnimation("right_fin",rot(k(0,0,0,0),k(.5F,0,0,8),k(1.12F,0,0,-14),k(1.8F,0,0,0))).build();
    public static final AnimationDefinition DEATH = AnimationDefinition.Builder.withLength(2.2F)
        .addAnimation("body",rot(k(0,0,0,0),k(.62F,0,0,-9),k(1.36F,0,0,16),k(2.2F,0,0,22)))
        .addAnimation("head",rot(k(0,0,0,0),k(.62F,0,0,8),k(1.36F,0,0,-10),k(2.2F,0,0,-16)))
        .addAnimation("left_fin",rot(k(0,0,0,0),k(.62F,0,0,-15),k(1.36F,0,0,-32),k(2.2F,0,0,-45)))
        .addAnimation("right_fin",rot(k(0,0,0,0),k(.62F,0,0,15),k(1.36F,0,0,32),k(2.2F,0,0,45)))
        .addAnimation("tail",rot(k(0,0,0,0),k(.62F,0,0,14),k(1.36F,0,0,28),k(2.2F,0,0,38))).build();
    private static AnimationChannel rot(Keyframe... frames) { return new AnimationChannel(ROT,frames); }
    private static Keyframe k(float t,float x,float y,float z) { return new Keyframe(t,KeyframeAnimations.degreeVec(x,y,z),SMOOTH); }
    private SpermWhaleAnimations() {}
}
