package com.aquamarine.client.entity.kraken_egg;

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

public final class KrakenEggModel extends EntityModel<KrakenEggRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("kraken_egg"), "main");
    private final KeyframeAnimation pulse, hatch;
    // UV:BEGIN gerado por tools/models/spawnlings/build_model.py (nao edite a mao)
    private static final int[] BODY_UV = {0, 0};
    private static final int[] BAND_UV = {33, 0};
    private static final int[] CROWN_UV = {70, 0};
    private static final int[] WINDOW_UV = {105, 0};
    private static final int[][] TENDRIL_UV = {
        {95, 0}, {100, 0}
    };
    // UV:END

    public KrakenEggModel(ModelPart root) {
        super(root);
        pulse = KrakenEggAnimations.PULSE.bake(root); hatch = KrakenEggAnimations.HATCH.bake(root);
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition shell = root.addOrReplaceChild("shell", CubeListBuilder.create()
            .texOffs(BODY_UV[0], BODY_UV[1]).addBox(-4, -6, -4, 8, 12, 8, CubeDeformation.NONE)
            .texOffs(BAND_UV[0], BAND_UV[1]).addBox(-4.3F, -2, -4.3F, 8.6F, 2, 8.6F, CubeDeformation.NONE)
            .texOffs(WINDOW_UV[0], WINDOW_UV[1]).addBox(-1.5F, -1, -4.5F, 3, 3, .6F, CubeDeformation.NONE), PartPose.offset(0, -6, 0));
        shell.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(CROWN_UV[0], CROWN_UV[1])
            .addBox(-2.8F, -4, -2.8F, 5.6F, 4, 5.6F, CubeDeformation.NONE), PartPose.offset(0, -3, 0));
        for (int side : new int[] {-1, 1}) {
            shell.addOrReplaceChild("tendril_" + side, CubeListBuilder.create().texOffs(TENDRIL_UV[side > 0 ? 1 : 0][0], TENDRIL_UV[side > 0 ? 1 : 0][1])
                .addBox(-.5F, 0, -.5F, 1, 6, 1, CubeDeformation.NONE), PartPose.offset(side * 2, 1, side * 2));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }
    @Override public void setupAnim(KrakenEggRenderState state) {
        resetPose(); pulse.apply(state.pulse, state.ageInTicks); hatch.apply(state.hatch, state.ageInTicks);
    }
}
