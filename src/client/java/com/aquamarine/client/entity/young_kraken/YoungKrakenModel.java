package com.aquamarine.client.entity.young_kraken;

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

public final class YoungKrakenModel extends EntityModel<YoungKrakenRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("young_kraken"), "main");
    private final KeyframeAnimation idle, swim, stunned, hatch, attack, death;
    // UV:BEGIN gerado por tools/models/spawnlings/build_model.py (nao edite a mao)
    private static final int[] MANTLE_UV = {0, 0};
    private static final int[] RIDGE_UV = {86, 0};
    private static final int[] HEAD_UV = {47, 0};
    private static final int[] BEAK_UV = {0, 28};
    private static final int[][] EYE_UV = {
        {65, 28}, {72, 28}
    };
    private static final int[][] ARM_UV = {
        {117, 0}, {0, 22}, {11, 28}, {11, 22}, {22, 22}, {20, 28},
        {33, 22}, {44, 22}, {29, 28}, {55, 22}, {66, 22}, {38, 28},
        {77, 22}, {88, 22}, {47, 28}, {99, 22}, {110, 22}, {56, 28}
    };
    private static final int[][] SUCKER_UV = {
        {79, 28}, {86, 28}, {93, 28}, {100, 28}, {107, 28}, {114, 28},
        {121, 28}, {0, 33}, {7, 33}, {14, 33}, {21, 33}, {28, 33},
        {35, 33}, {42, 33}, {49, 33}, {56, 33}, {63, 33}, {70, 33}
    };
    // UV:END

    public YoungKrakenModel(ModelPart root) {
        super(root);
        idle = YoungKrakenAnimations.IDLE.bake(root); swim = YoungKrakenAnimations.SWIM.bake(root);
        stunned = YoungKrakenAnimations.STUNNED.bake(root); hatch = YoungKrakenAnimations.HATCH.bake(root);
        attack = YoungKrakenAnimations.ATTACK.bake(root); death = YoungKrakenAnimations.DEATH.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition mantle = root.addOrReplaceChild("mantle", CubeListBuilder.create()
            .texOffs(MANTLE_UV[0], MANTLE_UV[1]).addBox(-5, -5, -7, 10, 8, 13, CubeDeformation.NONE)
            .texOffs(RIDGE_UV[0], RIDGE_UV[1]).addBox(-3, -7, -5, 6, 3, 9, CubeDeformation.NONE), PartPose.offset(0, -12, -1));
        root.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(HEAD_UV[0], HEAD_UV[1]).addBox(-4, -4, -5, 8, 7, 11, CubeDeformation.NONE)
            .texOffs(BEAK_UV[0], BEAK_UV[1]).addBox(-1.5F, 1, 5, 3, 2, 2, CubeDeformation.NONE), PartPose.offset(0, -7, 1));
        for (int side : new int[] {-1, 1}) {
            root.getChild("head").addOrReplaceChild("eye_" + side, CubeListBuilder.create().texOffs(EYE_UV[side > 0 ? 1 : 0][0], EYE_UV[side > 0 ? 1 : 0][1])
                .addBox(-1, -1, 0, 2, 2, 1, CubeDeformation.NONE), PartPose.offset(side * 2.5F, -1, 5));
        }
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * 2 * i / 6;
            float x = (float) Math.sin(angle) * 3.1F, z = (float) Math.cos(angle) * 2.8F;
            PartDefinition parent = root;
            for (int j = 0; j < 3; j++) {
                String name = "arm_" + i + "_" + j;
                float taper = 1.55F - j * .32F;
                parent = parent.addOrReplaceChild(name, CubeListBuilder.create().texOffs(ARM_UV[i * 3 + j][0], ARM_UV[i * 3 + j][1])
                    .addBox(-taper / 2, -taper / 2, 0, taper, taper, 3, CubeDeformation.NONE),
                    PartPose.offset(j == 0 ? x : 0, j == 0 ? -7 : 0, j == 0 ? z : 3));
                parent.addOrReplaceChild("sucker", CubeListBuilder.create().texOffs(SUCKER_UV[i * 3 + j][0], SUCKER_UV[i * 3 + j][1])
                    .addBox(-.45F, taper / 2, .8F, .9F, .5F, 1.1F, CubeDeformation.NONE), PartPose.ZERO);
            }
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override public void setupAnim(YoungKrakenRenderState state) {
        resetPose();
        idle.apply(state.idle, state.ageInTicks); swim.apply(state.swim, state.ageInTicks);
        stunned.apply(state.stunned, state.ageInTicks); hatch.apply(state.hatch, state.ageInTicks);
        attack.apply(state.attack, state.ageInTicks); death.apply(state.death, state.ageInTicks);
    }
}
