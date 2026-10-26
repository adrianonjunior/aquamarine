package com.aquamarine.client.entity.kraken;

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
 * Candidata A, Lula-colosso: corpo deitado como um torpedo, manto longo com faixas de fotóforos e barbatana em ponta de flecha, coroa de 8 braços para a frente e 2 tentáculos de caça com clava de ganchos; olhos enormes com fotóforos embaixo.
 * GERADO por tools/models/kraken_v3/build_game.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class KrakenV3aModel extends EntityModel<KrakenRenderState> {
	private final KrakenV3Motion.Parts motion;
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("kraken_v3a"), "main");
	private final KeyframeAnimation idle;
	private final KeyframeAnimation swim;
	private final KeyframeAnimation emerge;
	private final KeyframeAnimation stunned;
	private final KeyframeAnimation phaseChange;
	private final KeyframeAnimation death;
	private final KeyframeAnimation slam;
	private final KeyframeAnimation grab;
	private final KeyframeAnimation ink;
	private final KeyframeAnimation whirlpool;
	private final KeyframeAnimation spawnYoung;
	private final KeyframeAnimation waterJet;
	private final KeyframeAnimation roar;
	private final KeyframeAnimation bolts;
	private final KeyframeAnimation ambush;
	private final KeyframeAnimation doubleGrab;

	public KrakenV3aModel(ModelPart root) {
		super(root);
		this.motion = new KrakenV3Motion.Parts(KrakenV3Motion.part(root, "body", "head"),
			new ModelPart[] {KrakenV3Motion.part(root, "body", "mantle")},
			new ModelPart[][] {
			{KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3", "arm_0_4"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3", "arm_0_4", "arm_0_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3", "arm_1_4"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3", "arm_1_4", "arm_1_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3", "arm_2_4"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3", "arm_2_4", "arm_2_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3", "arm_3_4"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3", "arm_3_4", "arm_3_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3", "arm_4_4"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3", "arm_4_4", "arm_4_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3", "arm_5_4"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3", "arm_5_4", "arm_5_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3", "arm_6_4"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3", "arm_6_4", "arm_6_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3", "arm_7_4"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3", "arm_7_4", "arm_7_5")}});
		this.idle = KrakenV3aAnimations.IDLE.bake(root);
		this.swim = KrakenV3aAnimations.SWIM.bake(root);
		this.emerge = KrakenV3aAnimations.EMERGE.bake(root);
		this.stunned = KrakenV3aAnimations.STUNNED.bake(root);
		this.phaseChange = KrakenV3aAnimations.PHASE_CHANGE.bake(root);
		this.death = KrakenV3aAnimations.DEATH.bake(root);
		this.slam = KrakenV3aAnimations.SLAM.bake(root);
		this.grab = KrakenV3aAnimations.GRAB.bake(root);
		this.ink = KrakenV3aAnimations.INK.bake(root);
		this.whirlpool = KrakenV3aAnimations.WHIRLPOOL.bake(root);
		this.spawnYoung = KrakenV3aAnimations.SPAWN_YOUNG.bake(root);
		this.waterJet = KrakenV3aAnimations.WATER_JET.bake(root);
		this.roar = KrakenV3aAnimations.ROAR.bake(root);
		this.bolts = KrakenV3aAnimations.BOLTS.bake(root);
		this.ambush = KrakenV3aAnimations.AMBUSH.bake(root);
		this.doubleGrab = KrakenV3aAnimations.DOUBLE_GRAB.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition part_body = root.addOrReplaceChild("body", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_head = part_body.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(0, 30).addBox(-7.5F, -6F, -4.8F, 15F, 12F, 9.6F, new CubeDeformation(0F))
			.texOffs(57, 0).addBox(-4.8F, -6F, -7.5F, 9.6F, 12F, 15F, new CubeDeformation(0F))
			.texOffs(167, 82).addBox(-5.5F, -8F, -3.52F, 11F, 2F, 7.04F, new CubeDeformation(0F))
			.texOffs(172, 53).addBox(-3.52F, -8F, -5.5F, 7.04F, 2F, 11F, new CubeDeformation(0F))
			.texOffs(211, 53).addBox(-8.5F, -4.8F, -5.6F, 1.2F, 1.6F, 9.6F, new CubeDeformation(0F))
			.texOffs(19, 104).addBox(-8.2F, 4.4F, -4F, 1F, 1F, 6F, new CubeDeformation(0F))
			.texOffs(0, 69).addBox(7.3F, -4.8F, -5.6F, 1.2F, 1.6F, 9.6F, new CubeDeformation(0F))
			.texOffs(34, 104).addBox(7.2F, 4.4F, -4F, 1F, 1F, 6F, new CubeDeformation(0F))
			.texOffs(0, 53).addBox(-8.8F, -3F, -5F, 1.5F, 7F, 8F, new CubeDeformation(0F))
			.texOffs(21, 53).addBox(7.3F, -3F, -5F, 1.5F, 7F, 8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_lid_left = part_head.addOrReplaceChild("lid_left", CubeListBuilder.create()
			.texOffs(42, 53).addBox(-8.5F, -2.75F, -4.75F, 0.9F, 6.5F, 7.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_lid_right = part_head.addOrReplaceChild("lid_right", CubeListBuilder.create()
			.texOffs(61, 53).addBox(7.6F, -2.75F, -4.75F, 0.9F, 6.5F, 7.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_beak = part_head.addOrReplaceChild("beak", CubeListBuilder.create()
			.texOffs(63, 112).addBox(-2F, -1.5F, -3F, 4F, 3F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 1.5F, -7.5F, 0F, 0F, 0F));
		PartDefinition part_jaw = part_beak.addOrReplaceChild("jaw", CubeListBuilder.create()
			.texOffs(78, 112).addBox(-1.5F, 0F, -2.5F, 3F, 1.8F, 2.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 1.5F, -0.5F, 0F, 0F, 0F));
		PartDefinition part_arm_0 = part_head.addOrReplaceChild("arm_0", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.6073F, 5.3803F, -6.5F, -1.2566F, 0F, 0.3927F));
		PartDefinition part_arm_0_0 = part_arm_0.addOrReplaceChild("arm_0_0", CubeListBuilder.create()
			.texOffs(25, 69).addBox(-2.1F, 0F, -2.1F, 4.2F, 6.9F, 4.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_0_1 = part_arm_0_0.addOrReplaceChild("arm_0_1", CubeListBuilder.create()
			.texOffs(31, 82).addBox(-1.8F, 0F, -1.8F, 3.6F, 6.4F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_0_2 = part_arm_0_1.addOrReplaceChild("arm_0_2", CubeListBuilder.create()
			.texOffs(0, 94).addBox(-1.5F, 0F, -1.5F, 3F, 5.9F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_0_3 = part_arm_0_2.addOrReplaceChild("arm_0_3", CubeListBuilder.create()
			.texOffs(13, 94).addBox(-1.25F, 0F, -1.25F, 2.5F, 5.4F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_0_4 = part_arm_0_3.addOrReplaceChild("arm_0_4", CubeListBuilder.create()
			.texOffs(49, 104).addBox(-1F, 0F, -1F, 2F, 4.9F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1571F, 0F, 0F));
		PartDefinition part_arm_0_5 = part_arm_0_4.addOrReplaceChild("arm_0_5", CubeListBuilder.create()
			.texOffs(58, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.4F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_1 = part_head.addOrReplaceChild("arm_1", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-3.8803F, 3.1073F, -6.5F, -1.2566F, 0F, 1.1781F));
		PartDefinition part_arm_1_0 = part_arm_1.addOrReplaceChild("arm_1_0", CubeListBuilder.create()
			.texOffs(46, 69).addBox(-2.1F, 0F, -2.1F, 4.2F, 6.9F, 4.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_1_1 = part_arm_1_0.addOrReplaceChild("arm_1_1", CubeListBuilder.create()
			.texOffs(48, 82).addBox(-1.8F, 0F, -1.8F, 3.6F, 6.4F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_1_2 = part_arm_1_1.addOrReplaceChild("arm_1_2", CubeListBuilder.create()
			.texOffs(26, 94).addBox(-1.5F, 0F, -1.5F, 3F, 5.9F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_1_3 = part_arm_1_2.addOrReplaceChild("arm_1_3", CubeListBuilder.create()
			.texOffs(39, 94).addBox(-1.25F, 0F, -1.25F, 2.5F, 5.4F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_1_4 = part_arm_1_3.addOrReplaceChild("arm_1_4", CubeListBuilder.create()
			.texOffs(67, 104).addBox(-1F, 0F, -1F, 2F, 4.9F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1571F, 0F, 0F));
		PartDefinition part_arm_1_5 = part_arm_1_4.addOrReplaceChild("arm_1_5", CubeListBuilder.create()
			.texOffs(76, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.4F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_2 = part_head.addOrReplaceChild("arm_2", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-3.8803F, -0.1073F, -6.5F, -1.2566F, 0F, 1.9635F));
		PartDefinition part_arm_2_0 = part_arm_2.addOrReplaceChild("arm_2_0", CubeListBuilder.create()
			.texOffs(67, 69).addBox(-2.1F, 0F, -2.1F, 4.2F, 6.9F, 4.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_2_1 = part_arm_2_0.addOrReplaceChild("arm_2_1", CubeListBuilder.create()
			.texOffs(65, 82).addBox(-1.8F, 0F, -1.8F, 3.6F, 6.4F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_2_2 = part_arm_2_1.addOrReplaceChild("arm_2_2", CubeListBuilder.create()
			.texOffs(52, 94).addBox(-1.5F, 0F, -1.5F, 3F, 5.9F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_2_3 = part_arm_2_2.addOrReplaceChild("arm_2_3", CubeListBuilder.create()
			.texOffs(65, 94).addBox(-1.25F, 0F, -1.25F, 2.5F, 5.4F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_2_4 = part_arm_2_3.addOrReplaceChild("arm_2_4", CubeListBuilder.create()
			.texOffs(85, 104).addBox(-1F, 0F, -1F, 2F, 4.9F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1571F, 0F, 0F));
		PartDefinition part_arm_2_5 = part_arm_2_4.addOrReplaceChild("arm_2_5", CubeListBuilder.create()
			.texOffs(94, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.4F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_3 = part_head.addOrReplaceChild("arm_3", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.6073F, -2.3803F, -6.5F, -1.2566F, 0F, 2.7489F));
		PartDefinition part_arm_3_0 = part_arm_3.addOrReplaceChild("arm_3_0", CubeListBuilder.create()
			.texOffs(88, 69).addBox(-2.1F, 0F, -2.1F, 4.2F, 6.9F, 4.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_3_1 = part_arm_3_0.addOrReplaceChild("arm_3_1", CubeListBuilder.create()
			.texOffs(82, 82).addBox(-1.8F, 0F, -1.8F, 3.6F, 6.4F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_3_2 = part_arm_3_1.addOrReplaceChild("arm_3_2", CubeListBuilder.create()
			.texOffs(78, 94).addBox(-1.5F, 0F, -1.5F, 3F, 5.9F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_3_3 = part_arm_3_2.addOrReplaceChild("arm_3_3", CubeListBuilder.create()
			.texOffs(91, 94).addBox(-1.25F, 0F, -1.25F, 2.5F, 5.4F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_3_4 = part_arm_3_3.addOrReplaceChild("arm_3_4", CubeListBuilder.create()
			.texOffs(103, 104).addBox(-1F, 0F, -1F, 2F, 4.9F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1571F, 0F, 0F));
		PartDefinition part_arm_3_5 = part_arm_3_4.addOrReplaceChild("arm_3_5", CubeListBuilder.create()
			.texOffs(112, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.4F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_4 = part_head.addOrReplaceChild("arm_4", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.6073F, -2.3803F, -6.5F, -1.2566F, 0F, 3.5343F));
		PartDefinition part_arm_4_0 = part_arm_4.addOrReplaceChild("arm_4_0", CubeListBuilder.create()
			.texOffs(109, 69).addBox(-2.1F, 0F, -2.1F, 4.2F, 6.9F, 4.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_4_1 = part_arm_4_0.addOrReplaceChild("arm_4_1", CubeListBuilder.create()
			.texOffs(99, 82).addBox(-1.8F, 0F, -1.8F, 3.6F, 6.4F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_4_2 = part_arm_4_1.addOrReplaceChild("arm_4_2", CubeListBuilder.create()
			.texOffs(104, 94).addBox(-1.5F, 0F, -1.5F, 3F, 5.9F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_4_3 = part_arm_4_2.addOrReplaceChild("arm_4_3", CubeListBuilder.create()
			.texOffs(117, 94).addBox(-1.25F, 0F, -1.25F, 2.5F, 5.4F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_4_4 = part_arm_4_3.addOrReplaceChild("arm_4_4", CubeListBuilder.create()
			.texOffs(121, 104).addBox(-1F, 0F, -1F, 2F, 4.9F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1571F, 0F, 0F));
		PartDefinition part_arm_4_5 = part_arm_4_4.addOrReplaceChild("arm_4_5", CubeListBuilder.create()
			.texOffs(130, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.4F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_5 = part_head.addOrReplaceChild("arm_5", CubeListBuilder.create(),
			PartPose.offsetAndRotation(3.8803F, -0.1073F, -6.5F, -1.2566F, 0F, 4.3197F));
		PartDefinition part_arm_5_0 = part_arm_5.addOrReplaceChild("arm_5_0", CubeListBuilder.create()
			.texOffs(130, 69).addBox(-2.1F, 0F, -2.1F, 4.2F, 6.9F, 4.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_5_1 = part_arm_5_0.addOrReplaceChild("arm_5_1", CubeListBuilder.create()
			.texOffs(116, 82).addBox(-1.8F, 0F, -1.8F, 3.6F, 6.4F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_5_2 = part_arm_5_1.addOrReplaceChild("arm_5_2", CubeListBuilder.create()
			.texOffs(130, 94).addBox(-1.5F, 0F, -1.5F, 3F, 5.9F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_5_3 = part_arm_5_2.addOrReplaceChild("arm_5_3", CubeListBuilder.create()
			.texOffs(143, 94).addBox(-1.25F, 0F, -1.25F, 2.5F, 5.4F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_5_4 = part_arm_5_3.addOrReplaceChild("arm_5_4", CubeListBuilder.create()
			.texOffs(139, 104).addBox(-1F, 0F, -1F, 2F, 4.9F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1571F, 0F, 0F));
		PartDefinition part_arm_5_5 = part_arm_5_4.addOrReplaceChild("arm_5_5", CubeListBuilder.create()
			.texOffs(148, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.4F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_6 = part_head.addOrReplaceChild("arm_6", CubeListBuilder.create(),
			PartPose.offsetAndRotation(3.8803F, 3.1073F, -6.5F, -1.2566F, 0F, 5.1051F));
		PartDefinition part_arm_6_0 = part_arm_6.addOrReplaceChild("arm_6_0", CubeListBuilder.create()
			.texOffs(151, 69).addBox(-2.1F, 0F, -2.1F, 4.2F, 6.9F, 4.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_6_1 = part_arm_6_0.addOrReplaceChild("arm_6_1", CubeListBuilder.create()
			.texOffs(133, 82).addBox(-1.8F, 0F, -1.8F, 3.6F, 6.4F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_6_2 = part_arm_6_1.addOrReplaceChild("arm_6_2", CubeListBuilder.create()
			.texOffs(156, 94).addBox(-1.5F, 0F, -1.5F, 3F, 5.9F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_6_3 = part_arm_6_2.addOrReplaceChild("arm_6_3", CubeListBuilder.create()
			.texOffs(169, 94).addBox(-1.25F, 0F, -1.25F, 2.5F, 5.4F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_6_4 = part_arm_6_3.addOrReplaceChild("arm_6_4", CubeListBuilder.create()
			.texOffs(157, 104).addBox(-1F, 0F, -1F, 2F, 4.9F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1571F, 0F, 0F));
		PartDefinition part_arm_6_5 = part_arm_6_4.addOrReplaceChild("arm_6_5", CubeListBuilder.create()
			.texOffs(166, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.4F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_arm_7 = part_head.addOrReplaceChild("arm_7", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.6073F, 5.3803F, -6.5F, -1.2566F, 0F, 5.8905F));
		PartDefinition part_arm_7_0 = part_arm_7.addOrReplaceChild("arm_7_0", CubeListBuilder.create()
			.texOffs(172, 69).addBox(-2.1F, 0F, -2.1F, 4.2F, 6.9F, 4.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_7_1 = part_arm_7_0.addOrReplaceChild("arm_7_1", CubeListBuilder.create()
			.texOffs(150, 82).addBox(-1.8F, 0F, -1.8F, 3.6F, 6.4F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_7_2 = part_arm_7_1.addOrReplaceChild("arm_7_2", CubeListBuilder.create()
			.texOffs(182, 94).addBox(-1.5F, 0F, -1.5F, 3F, 5.9F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 6F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_7_3 = part_arm_7_2.addOrReplaceChild("arm_7_3", CubeListBuilder.create()
			.texOffs(195, 94).addBox(-1.25F, 0F, -1.25F, 2.5F, 5.4F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_7_4 = part_arm_7_3.addOrReplaceChild("arm_7_4", CubeListBuilder.create()
			.texOffs(175, 104).addBox(-1F, 0F, -1F, 2F, 4.9F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1571F, 0F, 0F));
		PartDefinition part_arm_7_5 = part_arm_7_4.addOrReplaceChild("arm_7_5", CubeListBuilder.create()
			.texOffs(184, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.4F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_feeder_l = part_head.addOrReplaceChild("feeder_l", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.5F, 3.8F, -7F, -1.3963F, 0F, -0.1396F));
		PartDefinition part_feeder_l_0 = part_feeder_l.addOrReplaceChild("feeder_l_0", CubeListBuilder.create()
			.texOffs(193, 104).addBox(-0.95F, 0F, -0.95F, 1.9F, 4.9F, 1.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_feeder_l_1 = part_feeder_l_0.addOrReplaceChild("feeder_l_1", CubeListBuilder.create()
			.texOffs(202, 104).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.9F, 1.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_feeder_l_2 = part_feeder_l_1.addOrReplaceChild("feeder_l_2", CubeListBuilder.create()
			.texOffs(211, 104).addBox(-0.85F, 0F, -0.85F, 1.7F, 4.9F, 1.7F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_feeder_l_3 = part_feeder_l_2.addOrReplaceChild("feeder_l_3", CubeListBuilder.create()
			.texOffs(220, 104).addBox(-0.8F, 0F, -0.8F, 1.6F, 4.9F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_l_4 = part_feeder_l_3.addOrReplaceChild("feeder_l_4", CubeListBuilder.create()
			.texOffs(229, 104).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.9F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_l_5 = part_feeder_l_4.addOrReplaceChild("feeder_l_5", CubeListBuilder.create()
			.texOffs(238, 104).addBox(-0.7F, 0F, -0.7F, 1.4F, 4.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_l_6 = part_feeder_l_5.addOrReplaceChild("feeder_l_6", CubeListBuilder.create()
			.texOffs(247, 104).addBox(-0.65F, 0F, -0.65F, 1.3F, 4.9F, 1.3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_l_club = part_feeder_l_6.addOrReplaceChild("feeder_l_club", CubeListBuilder.create()
			.texOffs(206, 82).addBox(-1.6F, 0F, -1.12F, 3.2F, 7F, 2.24F, new CubeDeformation(0F))
			.texOffs(113, 112).addBox(-0.96F, 7F, -0.8F, 1.92F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_feeder_r = part_head.addOrReplaceChild("feeder_r", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.5F, 3.8F, -7F, -1.3963F, 0F, 0.1396F));
		PartDefinition part_feeder_r_0 = part_feeder_r.addOrReplaceChild("feeder_r_0", CubeListBuilder.create()
			.texOffs(0, 112).addBox(-0.95F, 0F, -0.95F, 1.9F, 4.9F, 1.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_feeder_r_1 = part_feeder_r_0.addOrReplaceChild("feeder_r_1", CubeListBuilder.create()
			.texOffs(9, 112).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.9F, 1.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_feeder_r_2 = part_feeder_r_1.addOrReplaceChild("feeder_r_2", CubeListBuilder.create()
			.texOffs(18, 112).addBox(-0.85F, 0F, -0.85F, 1.7F, 4.9F, 1.7F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_feeder_r_3 = part_feeder_r_2.addOrReplaceChild("feeder_r_3", CubeListBuilder.create()
			.texOffs(27, 112).addBox(-0.8F, 0F, -0.8F, 1.6F, 4.9F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_r_4 = part_feeder_r_3.addOrReplaceChild("feeder_r_4", CubeListBuilder.create()
			.texOffs(36, 112).addBox(-0.75F, 0F, -0.75F, 1.5F, 4.9F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_r_5 = part_feeder_r_4.addOrReplaceChild("feeder_r_5", CubeListBuilder.create()
			.texOffs(45, 112).addBox(-0.7F, 0F, -0.7F, 1.4F, 4.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_r_6 = part_feeder_r_5.addOrReplaceChild("feeder_r_6", CubeListBuilder.create()
			.texOffs(54, 112).addBox(-0.65F, 0F, -0.65F, 1.3F, 4.9F, 1.3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_r_club = part_feeder_r_6.addOrReplaceChild("feeder_r_club", CubeListBuilder.create()
			.texOffs(221, 82).addBox(-1.6F, 0F, -1.12F, 3.2F, 7F, 2.24F, new CubeDeformation(0F))
			.texOffs(122, 112).addBox(-0.96F, 7F, -0.8F, 1.92F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_mantle = part_body.addOrReplaceChild("mantle", CubeListBuilder.create()
			.texOffs(80, 53).addBox(-8.8F, -1.5F, -5.632F, 17.6F, 2F, 11.264F, new CubeDeformation(0F))
			.texOffs(96, 30).addBox(-5.632F, -1.5F, -8.8F, 11.264F, 2F, 17.6F, new CubeDeformation(0F))
			.texOffs(161, 0).addBox(-8.2F, -12F, -5.248F, 16.4F, 12F, 10.496F, new CubeDeformation(0F))
			.texOffs(0, 0).addBox(-5.248F, -12F, -8.2F, 10.496F, 12F, 16.4F, new CubeDeformation(0F))
			.texOffs(157, 30).addBox(-7.6F, -22F, -4.864F, 15.2F, 10F, 9.728F, new CubeDeformation(0F))
			.texOffs(108, 0).addBox(-4.864F, -22F, -7.6F, 9.728F, 10F, 15.2F, new CubeDeformation(0F))
			.texOffs(236, 82).addBox(-8.5F, -11F, -0.5F, 0.6F, 9F, 1F, new CubeDeformation(0F))
			.texOffs(241, 82).addBox(-7.9F, -21F, -0.5F, 0.6F, 9F, 1F, new CubeDeformation(0F))
			.texOffs(246, 82).addBox(7.9F, -11F, -0.5F, 0.6F, 9F, 1F, new CubeDeformation(0F))
			.texOffs(251, 82).addBox(7.3F, -21F, -0.5F, 0.6F, 9F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -4F, 5F, -1.3265F, 0F, 0F));
		PartDefinition part_mantle_tip = part_mantle.addOrReplaceChild("mantle_tip", CubeListBuilder.create()
			.texOffs(210, 30).addBox(-6.4F, -8F, -4.096F, 12.8F, 8F, 8.192F, new CubeDeformation(0F))
			.texOffs(51, 30).addBox(-4.096F, -8F, -6.4F, 8.192F, 8F, 12.8F, new CubeDeformation(0F))
			.texOffs(0, 82).addBox(-4.4F, -13F, -2.816F, 8.8F, 5F, 5.632F, new CubeDeformation(0F))
			.texOffs(141, 53).addBox(-2.816F, -13F, -4.4F, 5.632F, 5F, 8.8F, new CubeDeformation(0F))
			.texOffs(0, 104).addBox(-2.4F, -16F, -1.536F, 4.8F, 3F, 3.072F, new CubeDeformation(0F))
			.texOffs(208, 94).addBox(-1.536F, -16F, -2.4F, 3.072F, 3F, 4.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -22F, 0F, 0F, 0F, 0F));
		PartDefinition part_fin_left = part_mantle_tip.addOrReplaceChild("fin_left", CubeListBuilder.create()
			.texOffs(193, 69).addBox(-6F, -10F, -0.6F, 6F, 10F, 1.2F, new CubeDeformation(0F))
			.texOffs(227, 94).addBox(-10F, -8F, -0.6F, 4F, 6F, 1.2F, new CubeDeformation(0F))
			.texOffs(91, 112).addBox(-12.5F, -6F, -0.6F, 2.5F, 3F, 1.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-4F, -9F, 0F, 0F, 0F, 0F));
		PartDefinition part_fin_right = part_mantle_tip.addOrReplaceChild("fin_right", CubeListBuilder.create()
			.texOffs(210, 69).addBox(0F, -10F, -0.6F, 6F, 10F, 1.2F, new CubeDeformation(0F))
			.texOffs(240, 94).addBox(6F, -8F, -0.6F, 4F, 6F, 1.2F, new CubeDeformation(0F))
			.texOffs(102, 112).addBox(10F, -6F, -0.6F, 2.5F, 3F, 1.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(4F, -9F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}

	@Override
	public void setupAnim(KrakenRenderState state) {
		this.resetPose();
		this.idle.apply(state.idle, state.ageInTicks);
		this.swim.apply(state.swim, state.ageInTicks);
		this.emerge.apply(state.emerge, state.ageInTicks);
		this.stunned.apply(state.stunned, state.ageInTicks);
		this.phaseChange.apply(state.phaseChange, state.ageInTicks);
		this.death.apply(state.death, state.ageInTicks);
		this.slam.apply(state.slam, state.ageInTicks);
		this.grab.apply(state.grab, state.ageInTicks);
		this.ink.apply(state.ink, state.ageInTicks);
		this.whirlpool.apply(state.whirlpool, state.ageInTicks);
		this.spawnYoung.apply(state.spawnYoung, state.ageInTicks);
		this.waterJet.apply(state.waterJet, state.ageInTicks);
		this.roar.apply(state.roar, state.ageInTicks);
		this.bolts.apply(state.bolts, state.ageInTicks);
		this.ambush.apply(state.ambush, state.ageInTicks);
		this.doubleGrab.apply(state.doubleGrab, state.ageInTicks);
		KrakenV3Motion.alive(state, this.motion);
	}
}
