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

/**
 * Cria do Kraken: Kraken em miniatura com manto cônico e barbatanas na ponta, olhos grandes que brilham, bico, seis braços com ventosas e dois tentáculos de caça longos com clava.
 * GERADO por tools/models/spawnlings/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class YoungKrakenModel extends EntityModel<YoungKrakenRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("young_kraken"), "main");
	private final KeyframeAnimation idle;
	private final KeyframeAnimation swim;
	private final KeyframeAnimation stunned;
	private final KeyframeAnimation hatch;
	private final KeyframeAnimation attack;
	private final KeyframeAnimation death;

	public YoungKrakenModel(ModelPart root) {
		super(root);
		this.idle = YoungKrakenAnimations.IDLE.bake(root);
		this.swim = YoungKrakenAnimations.SWIM.bake(root);
		this.stunned = YoungKrakenAnimations.STUNNED.bake(root);
		this.hatch = YoungKrakenAnimations.HATCH.bake(root);
		this.attack = YoungKrakenAnimations.ATTACK.bake(root);
		this.death = YoungKrakenAnimations.DEATH.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition part_body = root.addOrReplaceChild("body", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0F, 13F, 0F, 0F, 0F, 0F));
		PartDefinition part_mantle = part_body.addOrReplaceChild("mantle", CubeListBuilder.create()
			.texOffs(60, 0).addBox(-4F, -8F, -2.56F, 8F, 8F, 5.12F, new CubeDeformation(0F))
			.texOffs(31, 0).addBox(-2.56F, -8F, -4F, 5.12F, 8F, 8F, new CubeDeformation(0F))
			.texOffs(51, 17).addBox(-3F, -11.5F, -1.92F, 6F, 4F, 3.84F, new CubeDeformation(0F))
			.texOffs(30, 17).addBox(-1.92F, -11.5F, -3F, 3.84F, 4F, 6F, new CubeDeformation(0F))
			.texOffs(54, 38).addBox(-1.8F, -14F, -1.152F, 3.6F, 3F, 2.304F, new CubeDeformation(0F))
			.texOffs(62, 29).addBox(-1.152F, -14F, -1.8F, 2.304F, 3F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -3F, 2F, 0.6981F, 0F, 0F));
		PartDefinition part_fin_left = part_mantle.addOrReplaceChild("fin_left", CubeListBuilder.create()
			.texOffs(77, 46).addBox(-3.5F, -2.5F, -0.5F, 3.5F, 4F, 1F, new CubeDeformation(0F))
			.texOffs(118, 46).addBox(-2F, -3.5F, -0.5F, 2F, 1F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-3.2F, -7F, 0.5F, 0F, 0.4363F, 0.1396F));
		PartDefinition part_fin_right = part_mantle.addOrReplaceChild("fin_right", CubeListBuilder.create()
			.texOffs(88, 46).addBox(0F, -2.5F, -0.5F, 3.5F, 4F, 1F, new CubeDeformation(0F))
			.texOffs(0, 53).addBox(0F, -3.5F, -0.5F, 2F, 1F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(3.2F, -7F, 0.5F, 0F, -0.4363F, -0.1396F));
		PartDefinition part_head = part_body.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(89, 0).addBox(-4.5F, -4.5F, -2.88F, 9F, 7F, 5.76F, new CubeDeformation(0F))
			.texOffs(0, 0).addBox(-2.88F, -4.5F, -4.5F, 5.76F, 7F, 9F, new CubeDeformation(0F))
			.texOffs(0, 17).addBox(-5.5F, -3.5F, -3F, 1F, 5F, 6F, new CubeDeformation(0F))
			.texOffs(15, 17).addBox(4.5F, -3.5F, -3F, 1F, 5F, 6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, -1F, 0F, 0F, 0F));
		PartDefinition part_beak = part_head.addOrReplaceChild("beak", CubeListBuilder.create()
			.texOffs(64, 46).addBox(-1.5F, 0F, -2F, 3F, 2F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 2.5F, -2.5F, 0F, 0F, 0F));
		PartDefinition part_beak_lower = part_beak.addOrReplaceChild("beak_lower", CubeListBuilder.create()
			.texOffs(109, 46).addBox(-1F, 0F, -1.5F, 2F, 1F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 2F, -0.5F, 0F, 0F, 0F));
		PartDefinition part_arm_0 = part_head.addOrReplaceChild("arm_0", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.4F, 2.5F, 2.4249F, 0.5934F, 0.5236F, 0F));
		PartDefinition part_arm_0_0 = part_arm_0.addOrReplaceChild("arm_0_0", CubeListBuilder.create()
			.texOffs(72, 17).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_0_1 = part_arm_0_0.addOrReplaceChild("arm_0_1", CubeListBuilder.create()
			.texOffs(103, 29).addBox(-0.95F, 0F, -0.95F, 1.9F, 4.4F, 1.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_0_2 = part_arm_0_1.addOrReplaceChild("arm_0_2", CubeListBuilder.create()
			.texOffs(69, 38).addBox(-0.75F, 0F, -0.75F, 1.5F, 3.9F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_0_3 = part_arm_0_2.addOrReplaceChild("arm_0_3", CubeListBuilder.create()
			.texOffs(78, 38).addBox(-0.55F, 0F, -0.55F, 1.1F, 3.4F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.2793F, 0F, 0F));
		PartDefinition part_arm_1 = part_head.addOrReplaceChild("arm_1", CubeListBuilder.create(),
			PartPose.offsetAndRotation(2.8F, 2.5F, 0F, 0.5934F, 1.5708F, 0F));
		PartDefinition part_arm_1_0 = part_arm_1.addOrReplaceChild("arm_1_0", CubeListBuilder.create()
			.texOffs(85, 17).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_1_1 = part_arm_1_0.addOrReplaceChild("arm_1_1", CubeListBuilder.create()
			.texOffs(112, 29).addBox(-0.95F, 0F, -0.95F, 1.9F, 4.4F, 1.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_1_2 = part_arm_1_1.addOrReplaceChild("arm_1_2", CubeListBuilder.create()
			.texOffs(87, 38).addBox(-0.75F, 0F, -0.75F, 1.5F, 3.9F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_1_3 = part_arm_1_2.addOrReplaceChild("arm_1_3", CubeListBuilder.create()
			.texOffs(96, 38).addBox(-0.55F, 0F, -0.55F, 1.1F, 3.4F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.2793F, 0F, 0F));
		PartDefinition part_arm_2 = part_head.addOrReplaceChild("arm_2", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.4F, 2.5F, -2.4249F, 0.5934F, 2.618F, 0F));
		PartDefinition part_arm_2_0 = part_arm_2.addOrReplaceChild("arm_2_0", CubeListBuilder.create()
			.texOffs(98, 17).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_2_1 = part_arm_2_0.addOrReplaceChild("arm_2_1", CubeListBuilder.create()
			.texOffs(0, 38).addBox(-0.95F, 0F, -0.95F, 1.9F, 4.4F, 1.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_2_2 = part_arm_2_1.addOrReplaceChild("arm_2_2", CubeListBuilder.create()
			.texOffs(105, 38).addBox(-0.75F, 0F, -0.75F, 1.5F, 3.9F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_2_3 = part_arm_2_2.addOrReplaceChild("arm_2_3", CubeListBuilder.create()
			.texOffs(114, 38).addBox(-0.55F, 0F, -0.55F, 1.1F, 3.4F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.2793F, 0F, 0F));
		PartDefinition part_arm_3 = part_head.addOrReplaceChild("arm_3", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.4F, 2.5F, -2.4249F, 0.5934F, 3.6652F, 0F));
		PartDefinition part_arm_3_0 = part_arm_3.addOrReplaceChild("arm_3_0", CubeListBuilder.create()
			.texOffs(111, 17).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_3_1 = part_arm_3_0.addOrReplaceChild("arm_3_1", CubeListBuilder.create()
			.texOffs(9, 38).addBox(-0.95F, 0F, -0.95F, 1.9F, 4.4F, 1.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_3_2 = part_arm_3_1.addOrReplaceChild("arm_3_2", CubeListBuilder.create()
			.texOffs(0, 46).addBox(-0.75F, 0F, -0.75F, 1.5F, 3.9F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_3_3 = part_arm_3_2.addOrReplaceChild("arm_3_3", CubeListBuilder.create()
			.texOffs(9, 46).addBox(-0.55F, 0F, -0.55F, 1.1F, 3.4F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.2793F, 0F, 0F));
		PartDefinition part_arm_4 = part_head.addOrReplaceChild("arm_4", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-2.8F, 2.5F, 0F, 0.5934F, 4.7124F, 0F));
		PartDefinition part_arm_4_0 = part_arm_4.addOrReplaceChild("arm_4_0", CubeListBuilder.create()
			.texOffs(0, 29).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_4_1 = part_arm_4_0.addOrReplaceChild("arm_4_1", CubeListBuilder.create()
			.texOffs(18, 38).addBox(-0.95F, 0F, -0.95F, 1.9F, 4.4F, 1.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_4_2 = part_arm_4_1.addOrReplaceChild("arm_4_2", CubeListBuilder.create()
			.texOffs(18, 46).addBox(-0.75F, 0F, -0.75F, 1.5F, 3.9F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_4_3 = part_arm_4_2.addOrReplaceChild("arm_4_3", CubeListBuilder.create()
			.texOffs(27, 46).addBox(-0.55F, 0F, -0.55F, 1.1F, 3.4F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.2793F, 0F, 0F));
		PartDefinition part_arm_5 = part_head.addOrReplaceChild("arm_5", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.4F, 2.5F, 2.4249F, 0.5934F, 5.7596F, 0F));
		PartDefinition part_arm_5_0 = part_arm_5.addOrReplaceChild("arm_5_0", CubeListBuilder.create()
			.texOffs(13, 29).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_5_1 = part_arm_5_0.addOrReplaceChild("arm_5_1", CubeListBuilder.create()
			.texOffs(27, 38).addBox(-0.95F, 0F, -0.95F, 1.9F, 4.4F, 1.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_5_2 = part_arm_5_1.addOrReplaceChild("arm_5_2", CubeListBuilder.create()
			.texOffs(36, 46).addBox(-0.75F, 0F, -0.75F, 1.5F, 3.9F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_5_3 = part_arm_5_2.addOrReplaceChild("arm_5_3", CubeListBuilder.create()
			.texOffs(45, 46).addBox(-0.55F, 0F, -0.55F, 1.1F, 3.4F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.2793F, 0F, 0F));
		PartDefinition part_feeder_l = part_head.addOrReplaceChild("feeder_l", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.5F, 2.5F, -2.4F, 0.6981F, 3.4208F, 0F));
		PartDefinition part_feeder_l_0 = part_feeder_l.addOrReplaceChild("feeder_l_0", CubeListBuilder.create()
			.texOffs(26, 29).addBox(-0.75F, 0F, -0.75F, 1.5F, 5.3F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_feeder_l_1 = part_feeder_l_0.addOrReplaceChild("feeder_l_1", CubeListBuilder.create()
			.texOffs(35, 29).addBox(-0.65F, 0F, -0.65F, 1.3F, 5.3F, 1.3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_l_2 = part_feeder_l_1.addOrReplaceChild("feeder_l_2", CubeListBuilder.create()
			.texOffs(36, 38).addBox(-0.55F, 0F, -0.55F, 1.1F, 4.8F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_l_3 = part_feeder_l_2.addOrReplaceChild("feeder_l_3", CubeListBuilder.create()
			.texOffs(54, 46).addBox(-0.5F, 0F, -0.5F, 1F, 4.3F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_l_4 = part_feeder_l_3.addOrReplaceChild("feeder_l_4", CubeListBuilder.create()
			.texOffs(99, 46).addBox(-0.45F, 0F, -0.45F, 0.9F, 3.8F, 0.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_l_club = part_feeder_l_4.addOrReplaceChild("feeder_l_club", CubeListBuilder.create()
			.texOffs(77, 29).addBox(-1.3F, 0F, -1.1F, 2.6F, 3.2F, 2.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_feeder_r = part_head.addOrReplaceChild("feeder_r", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.5F, 2.5F, -2.4F, 0.6981F, 2.8623F, 0F));
		PartDefinition part_feeder_r_0 = part_feeder_r.addOrReplaceChild("feeder_r_0", CubeListBuilder.create()
			.texOffs(44, 29).addBox(-0.75F, 0F, -0.75F, 1.5F, 5.3F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_feeder_r_1 = part_feeder_r_0.addOrReplaceChild("feeder_r_1", CubeListBuilder.create()
			.texOffs(53, 29).addBox(-0.65F, 0F, -0.65F, 1.3F, 5.3F, 1.3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_r_2 = part_feeder_r_1.addOrReplaceChild("feeder_r_2", CubeListBuilder.create()
			.texOffs(45, 38).addBox(-0.55F, 0F, -0.55F, 1.1F, 4.8F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_r_3 = part_feeder_r_2.addOrReplaceChild("feeder_r_3", CubeListBuilder.create()
			.texOffs(59, 46).addBox(-0.5F, 0F, -0.5F, 1F, 4.3F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_r_4 = part_feeder_r_3.addOrReplaceChild("feeder_r_4", CubeListBuilder.create()
			.texOffs(104, 46).addBox(-0.45F, 0F, -0.45F, 0.9F, 3.8F, 0.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_r_club = part_feeder_r_4.addOrReplaceChild("feeder_r_club", CubeListBuilder.create()
			.texOffs(90, 29).addBox(-1.3F, 0F, -1.1F, 2.6F, 3.2F, 2.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.1745F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	@Override
	public void setupAnim(YoungKrakenRenderState state) {
		this.resetPose();
		this.idle.apply(state.idle, state.ageInTicks);
		this.swim.apply(state.swim, state.ageInTicks);
		this.stunned.apply(state.stunned, state.ageInTicks);
		this.hatch.apply(state.hatch, state.ageInTicks);
		this.attack.apply(state.attack, state.ageInTicks);
		this.death.apply(state.death, state.ageInTicks);
	}
}
