package com.aquamarine.client.entity.moray;

import com.aquamarine.Aquamarine;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** Gerado por tools/models/moray/build_model.py; edite o script, não este arquivo. */
public final class MorayModel extends EntityModel<MorayRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("moray"), "main");
	public static final int TEXTURE_SIZE = 512;
	private final ModelPart body;
	private final KeyframeAnimation idle, swim, strike, chain, coil, stunned, death;

	public MorayModel(ModelPart root) {
		super(root);
		body = root.getChild("body");
		idle = MorayAnimations.IDLE.bake(root);
		swim = MorayAnimations.SWIM.bake(root);
		strike = MorayAnimations.STRIKE.bake(root);
		chain = MorayAnimations.CHAIN.bake(root);
		coil = MorayAnimations.COIL.bake(root);
		stunned = MorayAnimations.STUNNED.bake(root);
		death = MorayAnimations.DEATH.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(),
			PartPose.offset(0F, 11F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(0, 54).addBox(-11F, -8F, -30F, 22F, 13F, 30F)
			.texOffs(80, 154).addBox(-8F, -5F, -37F, 16F, 9F, 7F)
			.texOffs(128, 154).addBox(-12F, -9F, -26F, 24F, 3F, 10F)
			.texOffs(216, 99).addBox(-1F, -14F, -24F, 2F, 6F, 22F)
			.texOffs(498, 54).addBox(10F, -5F, -27F, 2F, 3F, 4F)
			.texOffs(198, 154).addBox(-12F, -5F, -27F, 2F, 3F, 4F)
			.texOffs(484, 130).addBox(11F, -7F, -10F, 2F, 9F, 8F)
			.texOffs(476, 54).addBox(-13F, -7F, -10F, 2F, 9F, 8F)
			.texOffs(500, 0).addBox(5F, 4F, -36F, 2F, 3F, 2F)
			.texOffs(212, 154).addBox(-7F, 4F, -36F, 2F, 3F, 2F),
			PartPose.offset(0F, 0F, 14F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create()
			.texOffs(274, 54).addBox(-9F, 0F, -28F, 18F, 5F, 28F)
			.texOffs(222, 154).addBox(4F, -2F, -27F, 2F, 2F, 2F)
			.texOffs(232, 154).addBox(-6F, -2F, -27F, 2F, 2F, 2F)
			.texOffs(494, 99).addBox(-1F, 5F, -24F, 2F, 3F, 6F),
			PartPose.offset(0F, 4F, -8F));
		PartDefinition seg1 = body.addOrReplaceChild("seg1", CubeListBuilder.create()
			.texOffs(58, 0).addBox(-10F, -10F, 0F, 20F, 20F, 28F)
			.texOffs(368, 54).addBox(-1F, -15F, 2F, 2F, 5F, 24F)
			.texOffs(450, 99).addBox(10F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(0, 130).addBox(-11F, -1F, 4F, 1F, 2F, 20F),
			PartPose.offset(0F, 0F, 14F));
		PartDefinition seg2 = seg1.addOrReplaceChild("seg2", CubeListBuilder.create()
			.texOffs(156, 0).addBox(-9F, -9F, 0F, 18F, 18F, 28F)
			.texOffs(422, 54).addBox(-1F, -14F, 2F, 2F, 5F, 24F)
			.texOffs(44, 130).addBox(9F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(88, 130).addBox(-10F, -1F, 4F, 1F, 2F, 20F),
			PartPose.offset(0F, 0F, 28F));
		PartDefinition seg3 = seg2.addOrReplaceChild("seg3", CubeListBuilder.create()
			.texOffs(250, 0).addBox(-9F, -9F, 0F, 18F, 18F, 28F)
			.texOffs(0, 99).addBox(-1F, -14F, 2F, 2F, 5F, 24F)
			.texOffs(132, 130).addBox(9F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(176, 130).addBox(-10F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(266, 99).addBox(-1F, 9F, 4F, 2F, 3F, 20F),
			PartPose.offset(0F, 0F, 28F));
		PartDefinition seg4 = seg3.addOrReplaceChild("seg4", CubeListBuilder.create()
			.texOffs(344, 0).addBox(-8F, -8F, 0F, 16F, 16F, 28F)
			.texOffs(54, 99).addBox(-1F, -13F, 2F, 2F, 5F, 24F)
			.texOffs(220, 130).addBox(8F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(264, 130).addBox(-9F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(312, 99).addBox(-1F, 8F, 4F, 2F, 3F, 20F),
			PartPose.offset(0F, 0F, 28F));
		PartDefinition seg5 = seg4.addOrReplaceChild("seg5", CubeListBuilder.create()
			.texOffs(106, 54).addBox(-7F, -7F, 0F, 14F, 14F, 28F)
			.texOffs(108, 99).addBox(-1F, -12F, 2F, 2F, 5F, 24F)
			.texOffs(308, 130).addBox(7F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(352, 130).addBox(-8F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(358, 99).addBox(-1F, 7F, 4F, 2F, 3F, 20F),
			PartPose.offset(0F, 0F, 28F));
		PartDefinition seg6 = seg5.addOrReplaceChild("seg6", CubeListBuilder.create()
			.texOffs(192, 54).addBox(-6F, -6F, 0F, 12F, 12F, 28F)
			.texOffs(162, 99).addBox(-1F, -11F, 2F, 2F, 5F, 24F)
			.texOffs(396, 130).addBox(6F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(440, 130).addBox(-7F, -1F, 4F, 1F, 2F, 20F)
			.texOffs(404, 99).addBox(-1F, 6F, 4F, 2F, 3F, 20F),
			PartPose.offset(0F, 0F, 28F));
		seg6.addOrReplaceChild("tail", CubeListBuilder.create()
			.texOffs(434, 0).addBox(-4F, -5F, 0F, 8F, 10F, 24F)
			.texOffs(0, 0).addBox(-1F, -13F, 4F, 2F, 26F, 26F)
			.texOffs(0, 154).addBox(4F, -1F, 2F, 1F, 2F, 18F)
			.texOffs(40, 154).addBox(-5F, -1F, 2F, 1F, 2F, 18F),
			PartPose.offset(0F, 0F, 28F));
		return LayerDefinition.create(mesh, TEXTURE_SIZE, TEXTURE_SIZE);
	}

	@Override
	public void setupAnim(MorayRenderState state) {
		resetPose();
		// Nada em 3D: o corpo inteiro inclina com o olhar, como o golfinho.
		body.xRot = state.xRot * ((float) Math.PI / 180.0F);
		idle.apply(state.idle, state.ageInTicks);
		swim.apply(state.swim, state.ageInTicks);
		strike.apply(state.strike, state.ageInTicks);
		chain.apply(state.chain, state.ageInTicks);
		coil.apply(state.coil, state.ageInTicks);
		stunned.apply(state.stunned, state.ageInTicks);
		death.apply(state.death, state.ageInTicks);
	}
}
