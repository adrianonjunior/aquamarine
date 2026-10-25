package com.aquamarine.client.entity.sperm_whale;

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

public final class SpermWhaleModel extends EntityModel<SpermWhaleRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("sperm_whale"), "main");
    private final KeyframeAnimation idle, swim, sonic, death;

    public SpermWhaleModel(ModelPart root) {
        super(root);
        idle = SpermWhaleAnimations.IDLE.bake(root); swim = SpermWhaleAnimations.SWIM.bake(root);
        sonic = SpermWhaleAnimations.SONIC.bake(root); death = SpermWhaleAnimations.DEATH.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition(); PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(SpermWhaleUv.TORPEDO_MANTLE_U,SpermWhaleUv.TORPEDO_MANTLE_V).addBox(-35,-28,-79,70,56,158)
            .texOffs(SpermWhaleUv.BELLY_KEEL_U,SpermWhaleUv.BELLY_KEEL_V).addBox(-26,19,-61,52,18,123)
            .texOffs(SpermWhaleUv.SHOULDER_HUMP_U,SpermWhaleUv.SHOULDER_HUMP_V).addBox(-38,-39,-29,76,20,74)
            .texOffs(SpermWhaleUv.BACK_RIDGE_U,SpermWhaleUv.BACK_RIDGE_V).addBox(-16,-44,3,32,8,52)
            .texOffs(SpermWhaleUv.SCAR_LEFT_1_U,SpermWhaleUv.SCAR_LEFT_1_V).addBox(-38,-5,-2,3,4,27)
            .texOffs(SpermWhaleUv.SCAR_LEFT_2_U,SpermWhaleUv.SCAR_LEFT_2_V).addBox(-38,1,18,3,4,22)
            .texOffs(SpermWhaleUv.SCAR_LEFT_3_U,SpermWhaleUv.SCAR_LEFT_3_V).addBox(-38,6,35,3,3,16)
            .texOffs(SpermWhaleUv.SCAR_RIGHT_1_U,SpermWhaleUv.SCAR_RIGHT_1_V).addBox(35,-4,4,3,4,25)
            .texOffs(SpermWhaleUv.SCAR_RIGHT_2_U,SpermWhaleUv.SCAR_RIGHT_2_V).addBox(35,2,24,3,4,20)
            .texOffs(SpermWhaleUv.SCAR_RIGHT_3_U,SpermWhaleUv.SCAR_RIGHT_3_V).addBox(35,7,40,3,3,13), PartPose.offset(0,-46,0));
        body.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(SpermWhaleUv.SQUARE_SPERMACETI_HEAD_U,SpermWhaleUv.SQUARE_SPERMACETI_HEAD_V).addBox(-39,-34,-64,78,61,55)
            .texOffs(SpermWhaleUv.FOREHEAD_BOSS_U,SpermWhaleUv.FOREHEAD_BOSS_V).addBox(-34,-42,-59,68,11,42)
            .texOffs(SpermWhaleUv.LEFT_EYE_SOCKET_U,SpermWhaleUv.LEFT_EYE_SOCKET_V).addBox(-40,-4,-48,6,9,12)
            .texOffs(SpermWhaleUv.RIGHT_EYE_SOCKET_U,SpermWhaleUv.RIGHT_EYE_SOCKET_V).addBox(34,-4,-48,6,9,12)
            .texOffs(SpermWhaleUv.LEFT_EYE_GLINT_U,SpermWhaleUv.LEFT_EYE_GLINT_V).addBox(-41,-3,-50,2,4,6)
            .texOffs(SpermWhaleUv.RIGHT_EYE_GLINT_U,SpermWhaleUv.RIGHT_EYE_GLINT_V).addBox(39,-3,-50,2,4,6)
            .texOffs(SpermWhaleUv.OFFSET_BLOWHOLE_RIM_U,SpermWhaleUv.OFFSET_BLOWHOLE_RIM_V).addBox(-19,-45,-46,22,5,16)
            .texOffs(SpermWhaleUv.OFFSET_BLOWHOLE_U,SpermWhaleUv.OFFSET_BLOWHOLE_V).addBox(-13,-48,-43,12,3,9), PartPose.offset(0,0,-60));
        body.addOrReplaceChild("jaw", CubeListBuilder.create()
            .texOffs(SpermWhaleUv.LOWER_JAW_U,SpermWhaleUv.LOWER_JAW_V).addBox(-24,25,-69,48,19,94)
            .texOffs(SpermWhaleUv.JAW_KEEL_U,SpermWhaleUv.JAW_KEEL_V).addBox(-18,39,-47,36,7,61)
            .texOffs(SpermWhaleUv.JAW_RAKE_LEFT_U,SpermWhaleUv.JAW_RAKE_LEFT_V).addBox(-22,39,5,4,1,36)
            .texOffs(SpermWhaleUv.JAW_RAKE_MID_U,SpermWhaleUv.JAW_RAKE_MID_V).addBox(-2,39,5,4,1,36)
            .texOffs(SpermWhaleUv.JAW_RAKE_RIGHT_U,SpermWhaleUv.JAW_RAKE_RIGHT_V).addBox(18,39,5,4,1,36), PartPose.offset(0,0,-50));
        body.addOrReplaceChild("left_fin", CubeListBuilder.create()
            .texOffs(SpermWhaleUv.LEFT_PECTORAL_U,SpermWhaleUv.LEFT_PECTORAL_V).addBox(-66,2,-9,63,12,68)
            .texOffs(SpermWhaleUv.LEFT_FIN_EDGE_U,SpermWhaleUv.LEFT_FIN_EDGE_V).addBox(-59,13,-3,52,5,49), PartPose.offset(-25,10,-35));
        body.addOrReplaceChild("right_fin", CubeListBuilder.create()
            .texOffs(SpermWhaleUv.RIGHT_PECTORAL_U,SpermWhaleUv.RIGHT_PECTORAL_V).addBox(3,2,-9,63,12,68)
            .texOffs(SpermWhaleUv.RIGHT_FIN_EDGE_U,SpermWhaleUv.RIGHT_FIN_EDGE_V).addBox(7,13,-3,52,5,49), PartPose.offset(25,10,-35));
        body.addOrReplaceChild("tail", CubeListBuilder.create()
            .texOffs(SpermWhaleUv.TAIL_PEDUNCLE_U,SpermWhaleUv.TAIL_PEDUNCLE_V).addBox(-13,-7,-2,26,23,49)
            .texOffs(SpermWhaleUv.LEFT_FLUKE_U,SpermWhaleUv.LEFT_FLUKE_V).addBox(-71,-2,35,58,8,42)
            .texOffs(SpermWhaleUv.RIGHT_FLUKE_U,SpermWhaleUv.RIGHT_FLUKE_V).addBox(13,-2,35,58,8,42)
            .texOffs(SpermWhaleUv.FLUKE_KEEL_U,SpermWhaleUv.FLUKE_KEEL_V).addBox(-14,5,42,28,12,23), PartPose.offset(0,0,65));
        body.addOrReplaceChild("dorsal", CubeListBuilder.create()
            .texOffs(SpermWhaleUv.DORSAL_FIN_U,SpermWhaleUv.DORSAL_FIN_V).addBox(-8,-15,7,16,22,29), PartPose.offset(0,-38,24));
        return LayerDefinition.create(mesh, SpermWhaleUv.SIZE, SpermWhaleUv.SIZE);
    }

    @Override public void setupAnim(SpermWhaleRenderState state) {
        resetPose(); idle.apply(state.idle,state.ageInTicks); swim.apply(state.swim,state.ageInTicks);
        sonic.apply(state.sonic,state.ageInTicks); death.apply(state.death,state.ageInTicks);
    }
}
