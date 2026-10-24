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
                CubeListBuilder.create().texOffs(i * 12, 0).addBox(-r, -4, -r, r * 2, 4.3F, r * 2, CubeDeformation.NONE),
                PartPose.offset(0, i == 0 ? -4 : 0, 0));
            for (int j = 0; j < 3; j++) {
                segment.addOrReplaceChild("sucker_" + j, CubeListBuilder.create().texOffs(i * 12, 20 + j * 4)
                    .addBox(-r + .35F, .2F, -r - .6F, Math.max(.65F, r - .4F), .55F, .8F, CubeDeformation.NONE),
                    PartPose.offset(0, -j * .2F, j * .1F));
            }
            parent = segment;
        }
        parent.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(96, 0)
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
