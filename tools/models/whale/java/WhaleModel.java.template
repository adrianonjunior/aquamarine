package com.aquamarine.client.entity.whale;

import com.aquamarine.Aquamarine;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public final class WhaleModel extends EntityModel<WhaleRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("whale"), "main");
    private final ModelPart saddle;
    private final KeyframeAnimation idle, swim, sing, surface, death;

    public WhaleModel(ModelPart root) {
        super(root);
        saddle = root.getChild("body").getChild("saddle");
        idle = WhaleAnimations.IDLE.bake(root); swim = WhaleAnimations.SWIM.bake(root);
        sing = WhaleAnimations.SING.bake(root); surface = WhaleAnimations.SURFACE.bake(root);
        death = WhaleAnimations.DEATH.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(WhaleUv.MAIN_MANTLE_U,WhaleUv.MAIN_MANTLE_V).addBox(-40,-29,-96,80,58,192)
            .texOffs(WhaleUv.BELLY_KEEL_U,WhaleUv.BELLY_KEEL_V).addBox(-30,20,-75,60,20,145)
            .texOffs(WhaleUv.BACK_RIDGE_U,WhaleUv.BACK_RIDGE_V).addBox(-24,-39,-57,48,12,102)
            .texOffs(WhaleUv.LEFT_GILL_PLATE_U,WhaleUv.LEFT_GILL_PLATE_V).addBox(-41,-7,-36,5,28,63)
            .texOffs(WhaleUv.RIGHT_GILL_PLATE_U,WhaleUv.RIGHT_GILL_PLATE_V).addBox(36,-7,-36,5,28,63)
            .texOffs(WhaleUv.GLOW_LEFT_GILL_U,WhaleUv.GLOW_LEFT_GILL_V).addBox(-43,-4,-25,3,5,51)
            .texOffs(WhaleUv.GLOW_RIGHT_GILL_U,WhaleUv.GLOW_RIGHT_GILL_V).addBox(40,-4,-25,3,5,51), PartPose.offset(0,-40,0));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(WhaleUv.ROSTRUM_U,WhaleUv.ROSTRUM_V).addBox(-34,-22,-63,68,38,53)
            .texOffs(WhaleUv.LEFT_BROW_U,WhaleUv.LEFT_BROW_V).addBox(-42,-28,-55,16,12,33)
            .texOffs(WhaleUv.RIGHT_BROW_U,WhaleUv.RIGHT_BROW_V).addBox(26,-28,-55,16,12,33)
            .texOffs(WhaleUv.LEFT_EYE_SOCKET_U,WhaleUv.LEFT_EYE_SOCKET_V).addBox(-37,-22,-56,6,8,10)
            .texOffs(WhaleUv.RIGHT_EYE_SOCKET_U,WhaleUv.RIGHT_EYE_SOCKET_V).addBox(31,-22,-56,6,8,10)
            .texOffs(WhaleUv.LEFT_LUMINOUS_EYE_U,WhaleUv.LEFT_LUMINOUS_EYE_V).addBox(-38,-21,-58,2,4,5)
            .texOffs(WhaleUv.RIGHT_LUMINOUS_EYE_U,WhaleUv.RIGHT_LUMINOUS_EYE_V).addBox(36,-21,-58,2,4,5)
            .texOffs(WhaleUv.BLOWHOLE_RIM_U,WhaleUv.BLOWHOLE_RIM_V).addBox(-11,-33,-35,22,5,14)
            .texOffs(WhaleUv.BLOWHOLE_U,WhaleUv.BLOWHOLE_V).addBox(-6,-36,-32,12,3,8), PartPose.offset(0,0,-72));
        body.addOrReplaceChild("jaw", CubeListBuilder.create()
            .texOffs(WhaleUv.LOWER_JAW_U,WhaleUv.LOWER_JAW_V).addBox(-30,7,-58,60,20,49)
            .texOffs(WhaleUv.CHIN_PLATE_U,WhaleUv.CHIN_PLATE_V).addBox(-23,20,-43,46,8,28), PartPose.offset(0,0,-72));
        body.addOrReplaceChild("left_fin", CubeListBuilder.create()
            .texOffs(WhaleUv.LEFT_PECTORAL_U,WhaleUv.LEFT_PECTORAL_V).addBox(-72,2,-8,77,11,78)
            .texOffs(WhaleUv.LEFT_FIN_KEEL_U,WhaleUv.LEFT_FIN_KEEL_V).addBox(-65,13,-2,67,5,61), PartPose.offset(-31,11,-36));
        body.addOrReplaceChild("right_fin", CubeListBuilder.create()
            .texOffs(WhaleUv.RIGHT_PECTORAL_U,WhaleUv.RIGHT_PECTORAL_V).addBox(-5,2,-8,77,11,78)
            .texOffs(WhaleUv.RIGHT_FIN_KEEL_U,WhaleUv.RIGHT_FIN_KEEL_V).addBox(-2,13,-2,67,5,61), PartPose.offset(31,11,-36));
        body.addOrReplaceChild("tail", CubeListBuilder.create()
            .texOffs(WhaleUv.TAIL_PEDUNCLE_U,WhaleUv.TAIL_PEDUNCLE_V).addBox(-15,-10,-3,30,28,52)
            .texOffs(WhaleUv.LEFT_FLUKE_U,WhaleUv.LEFT_FLUKE_V).addBox(-81,-4,35,69,8,45)
            .texOffs(WhaleUv.RIGHT_FLUKE_U,WhaleUv.RIGHT_FLUKE_V).addBox(12,-4,35,69,8,45)
            .texOffs(WhaleUv.FLUKE_KEEL_U,WhaleUv.FLUKE_KEEL_V).addBox(-18,3,41,36,13,28), PartPose.offset(0,0,75));
        body.addOrReplaceChild("dorsal", CubeListBuilder.create()
            .texOffs(WhaleUv.DORSAL_SAIL_U,WhaleUv.DORSAL_SAIL_V).addBox(-11,-18,4,22,28,31)
            .texOffs(WhaleUv.DORSAL_SPUR_U,WhaleUv.DORSAL_SPUR_V).addBox(-6,-26,21,12,10,13), PartPose.offset(0,-36,22));
        body.addOrReplaceChild("saddle", CubeListBuilder.create()
            .texOffs(WhaleUv.SADDLE_PAD_U,WhaleUv.SADDLE_PAD_V).addBox(-24,-8,-12,48,8,42)
            .texOffs(WhaleUv.SADDLE_SEAT_U,WhaleUv.SADDLE_SEAT_V).addBox(-20,-20,-5,40,12,25)
            .texOffs(WhaleUv.SADDLE_FRONT_ARCH_U,WhaleUv.SADDLE_FRONT_ARCH_V).addBox(-21,-21,-12,42,4,5)
            .texOffs(WhaleUv.SADDLE_BACK_ARCH_U,WhaleUv.SADDLE_BACK_ARCH_V).addBox(-21,-21,22,42,4,5)
            .texOffs(WhaleUv.LEFT_SADDLE_STRAP_U,WhaleUv.LEFT_SADDLE_STRAP_V).addBox(-28,-4,-13,4,27,6)
            .texOffs(WhaleUv.RIGHT_SADDLE_STRAP_U,WhaleUv.RIGHT_SADDLE_STRAP_V).addBox(24,-4,-13,4,27,6), PartPose.offset(0,-29,-8));
        return LayerDefinition.create(mesh, WhaleUv.SIZE, WhaleUv.SIZE);
    }

    @Override public void setupAnim(WhaleRenderState state) {
        resetPose();
        saddle.visible = state.saddled;
        idle.apply(state.idle, state.ageInTicks); swim.apply(state.swim, state.ageInTicks);
        sing.apply(state.sing, state.ageInTicks); surface.apply(state.surface, state.ageInTicks);
        death.apply(state.death, state.ageInTicks);
    }
}
