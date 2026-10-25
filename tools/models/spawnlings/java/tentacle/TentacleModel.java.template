package com.aquamarine.client.entity.tentacle;

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

public final class TentacleModel extends EntityModel<TentacleRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("tentacle"), "main");
    private final KeyframeAnimation idle, rise, slam, grab, release;
    // UV:BEGIN gerado por tools/models/spawnlings/build_model.py (nao edite a mao)
    private static final int[][] SEGMENT_UV = {
        {0, 0}, {21, 0}, {42, 0}, {63, 0}, {80, 0}, {97, 0},
        {110, 0}
    };
    private static final int[][] SUCKER_UV = {
        {14, 11}, {23, 11}, {32, 11}, {41, 11}, {48, 11}, {55, 11},
        {62, 11}, {69, 11}, {76, 11}, {83, 11}, {90, 11}, {97, 11},
        {104, 11}, {111, 11}, {118, 11}, {0, 16}, {5, 16}, {10, 16},
        {15, 16}, {20, 16}, {25, 16}
    };
    private static final int[] HOOK_UV = {5, 11};
    // UV:END

    public TentacleModel(ModelPart root) {
        super(root);
        idle = TentacleAnimations.IDLE.bake(root); rise = TentacleAnimations.RISE.bake(root);
        slam = TentacleAnimations.SLAM.bake(root); grab = TentacleAnimations.GRAB.bake(root);
        release = TentacleAnimations.RELEASE.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition parent = root;
        for (int i = 0; i < 7; i++) {
            float r = 2.5F - i * .23F;
            PartDefinition segment = parent.addOrReplaceChild("segment_" + i,
                CubeListBuilder.create().texOffs(SEGMENT_UV[i][0], SEGMENT_UV[i][1]).addBox(-r, -4, -r, r * 2, 4.3F, r * 2, CubeDeformation.NONE),
                PartPose.offset(0, i == 0 ? -4 : 0, 0));
            for (int j = 0; j < 3; j++) {
                segment.addOrReplaceChild("sucker_" + j, CubeListBuilder.create().texOffs(SUCKER_UV[i * 3 + j][0], SUCKER_UV[i * 3 + j][1])
                    .addBox(-r + .35F, .2F, -r - .6F, Math.max(.65F, r - .4F), .55F, .8F, CubeDeformation.NONE),
                    PartPose.offset(0, -j * .2F, j * .1F));
            }
            parent = segment;
        }
        parent.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(HOOK_UV[0], HOOK_UV[1])
            .addBox(-1.5F, -2, -.5F, 3, 2, 1, CubeDeformation.NONE), PartPose.offset(0, -4, 0));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override public void setupAnim(TentacleRenderState state) {
        resetPose();
        idle.apply(state.idle, state.ageInTicks); rise.apply(state.rise, state.ageInTicks);
        slam.apply(state.slam, state.ageInTicks); grab.apply(state.grab, state.ageInTicks);
        release.apply(state.release, state.ageInTicks);
    }
}
