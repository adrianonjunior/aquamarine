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
 * Candidata C, Amonita-colosso: concha em espiral nas costas, com costelas, sutura acesa e espinhos na quilha (casa com o escudo do Kraken), capuz duro sobre a cabeça, borda de nácar que brilha na boca da concha e 8 braços em leque para baixo com 2 tentáculos de caça.
 * GERADO por tools/models/kraken_v3/build_game.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class KrakenV3cModel extends EntityModel<KrakenRenderState> {
	private final KrakenV3Motion.Parts motion;
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("kraken_v3c"), "main");
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

	public KrakenV3cModel(ModelPart root) {
		super(root);
		this.motion = new KrakenV3Motion.Parts(KrakenV3Motion.part(root, "body", "head"),
			new ModelPart[] {KrakenV3Motion.part(root, "body", "head", "hood")},
			new ModelPart[][] {
			{KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3", "arm_0_4"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3", "arm_0_4", "arm_0_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3", "arm_1_4"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3", "arm_1_4", "arm_1_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3", "arm_2_4"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3", "arm_2_4", "arm_2_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3", "arm_3_4"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3", "arm_3_4", "arm_3_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3", "arm_4_4"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3", "arm_4_4", "arm_4_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3", "arm_5_4"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3", "arm_5_4", "arm_5_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3", "arm_6_4"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3", "arm_6_4", "arm_6_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3", "arm_7_4"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3", "arm_7_4", "arm_7_5")}});
		this.idle = KrakenV3cAnimations.IDLE.bake(root);
		this.swim = KrakenV3cAnimations.SWIM.bake(root);
		this.emerge = KrakenV3cAnimations.EMERGE.bake(root);
		this.stunned = KrakenV3cAnimations.STUNNED.bake(root);
		this.phaseChange = KrakenV3cAnimations.PHASE_CHANGE.bake(root);
		this.death = KrakenV3cAnimations.DEATH.bake(root);
		this.slam = KrakenV3cAnimations.SLAM.bake(root);
		this.grab = KrakenV3cAnimations.GRAB.bake(root);
		this.ink = KrakenV3cAnimations.INK.bake(root);
		this.whirlpool = KrakenV3cAnimations.WHIRLPOOL.bake(root);
		this.spawnYoung = KrakenV3cAnimations.SPAWN_YOUNG.bake(root);
		this.waterJet = KrakenV3cAnimations.WATER_JET.bake(root);
		this.roar = KrakenV3cAnimations.ROAR.bake(root);
		this.bolts = KrakenV3cAnimations.BOLTS.bake(root);
		this.ambush = KrakenV3cAnimations.AMBUSH.bake(root);
		this.doubleGrab = KrakenV3cAnimations.DOUBLE_GRAB.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition part_body = root.addOrReplaceChild("body", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_head = part_body.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(175, 25).addBox(-7F, -5F, -4.48F, 14F, 10F, 8.96F, new CubeDeformation(0F))
			.texOffs(126, 0).addBox(-4.48F, -5F, -7F, 8.96F, 10F, 14F, new CubeDeformation(0F))
			.texOffs(119, 81).addBox(-8.4F, -3F, -3.5F, 1.6F, 5F, 5F, new CubeDeformation(0F))
			.texOffs(134, 81).addBox(6.8F, -3F, -3.5F, 1.6F, 5F, 5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_lid_left = part_head.addOrReplaceChild("lid_left", CubeListBuilder.create()
			.texOffs(149, 81).addBox(-8.1F, -2.75F, -3.25F, 1F, 4.5F, 4.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_lid_right = part_head.addOrReplaceChild("lid_right", CubeListBuilder.create()
			.texOffs(162, 81).addBox(7.1F, -2.75F, -3.25F, 1F, 4.5F, 4.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_hood = part_head.addOrReplaceChild("hood", CubeListBuilder.create()
			.texOffs(47, 47).addBox(-8F, -3F, -9F, 16F, 3F, 14F, new CubeDeformation(0F))
			.texOffs(87, 111).addBox(-6.5F, -1.5F, -10F, 13F, 2.5F, 2F, new CubeDeformation(0F))
			.texOffs(35, 66).addBox(-1F, -4F, -8F, 2F, 1.2F, 12F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -5F, 1F, 0.1396F, 0F, 0F));
		PartDefinition part_beak = part_head.addOrReplaceChild("beak", CubeListBuilder.create()
			.texOffs(0, 111).addBox(-1.8F, -1F, -2.5F, 3.6F, 2.5F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 2.5F, -7F, 0F, 0F, 0F));
		PartDefinition part_jaw = part_beak.addOrReplaceChild("jaw", CubeListBuilder.create()
			.texOffs(118, 111).addBox(-1.4F, 0F, -2F, 2.8F, 1.6F, 2.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 1.5F, -0.5F, 0F, 0F, 0F));
		PartDefinition part_arm_0 = part_head.addOrReplaceChild("arm_0", CubeListBuilder.create(),
			PartPose.offsetAndRotation(3.4808F, 4.23F, -4.5F, -0.5236F, 0F, -1.4661F));
		PartDefinition part_arm_0_0 = part_arm_0.addOrReplaceChild("arm_0_0", CubeListBuilder.create()
			.texOffs(228, 66).addBox(-1.7F, 0F, -1.7F, 3.4F, 5.9F, 3.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_0_1 = part_arm_0_0.addOrReplaceChild("arm_0_1", CubeListBuilder.create()
			.texOffs(215, 81).addBox(-1.5F, 0F, -1.5F, 3F, 5.4F, 3F, new CubeDeformation(0F))
			.texOffs(204, 111).addBox(-0.4F, 1.5F, 1.2F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_0_2 = part_arm_0_1.addOrReplaceChild("arm_0_2", CubeListBuilder.create()
			.texOffs(65, 92).addBox(-1.3F, 0F, -1.3F, 2.6F, 4.9F, 2.6F, new CubeDeformation(0F))
			.texOffs(211, 111).addBox(-0.4F, 1.35F, 1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_arm_0_3 = part_arm_0_2.addOrReplaceChild("arm_0_3", CubeListBuilder.create()
			.texOffs(78, 92).addBox(-1.1F, 0F, -1.1F, 2.2F, 4.9F, 2.2F, new CubeDeformation(0F))
			.texOffs(218, 111).addBox(-0.4F, 1.35F, 0.8F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1396F, 0F, 0F));
		PartDefinition part_arm_0_4 = part_arm_0_3.addOrReplaceChild("arm_0_4", CubeListBuilder.create()
			.texOffs(48, 102).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.4F, 1.8F, new CubeDeformation(0F))
			.texOffs(225, 111).addBox(-0.4F, 1.2F, 0.6F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_0_5 = part_arm_0_4.addOrReplaceChild("arm_0_5", CubeListBuilder.create()
			.texOffs(15, 111).addBox(-0.7F, 0F, -0.7F, 1.4F, 3.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_arm_1 = part_head.addOrReplaceChild("arm_1", CubeListBuilder.create(),
			PartPose.offsetAndRotation(3.0311F, 5.1F, -4.5F, -0.5236F, 0F, -1.0472F));
		PartDefinition part_arm_1_0 = part_arm_1.addOrReplaceChild("arm_1_0", CubeListBuilder.create()
			.texOffs(0, 81).addBox(-1.7F, 0F, -1.7F, 3.4F, 5.9F, 3.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_1_1 = part_arm_1_0.addOrReplaceChild("arm_1_1", CubeListBuilder.create()
			.texOffs(228, 81).addBox(-1.5F, 0F, -1.5F, 3F, 5.4F, 3F, new CubeDeformation(0F))
			.texOffs(232, 111).addBox(-0.4F, 1.5F, 1.2F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_1_2 = part_arm_1_1.addOrReplaceChild("arm_1_2", CubeListBuilder.create()
			.texOffs(91, 92).addBox(-1.3F, 0F, -1.3F, 2.6F, 4.9F, 2.6F, new CubeDeformation(0F))
			.texOffs(239, 111).addBox(-0.4F, 1.35F, 1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_arm_1_3 = part_arm_1_2.addOrReplaceChild("arm_1_3", CubeListBuilder.create()
			.texOffs(104, 92).addBox(-1.1F, 0F, -1.1F, 2.2F, 4.9F, 2.2F, new CubeDeformation(0F))
			.texOffs(246, 111).addBox(-0.4F, 1.35F, 0.8F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1396F, 0F, 0F));
		PartDefinition part_arm_1_4 = part_arm_1_3.addOrReplaceChild("arm_1_4", CubeListBuilder.create()
			.texOffs(57, 102).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.4F, 1.8F, new CubeDeformation(0F))
			.texOffs(0, 118).addBox(-0.4F, 1.2F, 0.6F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_1_5 = part_arm_1_4.addOrReplaceChild("arm_1_5", CubeListBuilder.create()
			.texOffs(24, 111).addBox(-0.7F, 0F, -0.7F, 1.4F, 3.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_arm_2 = part_head.addOrReplaceChild("arm_2", CubeListBuilder.create(),
			PartPose.offsetAndRotation(2.0572F, 5.7798F, -4.5F, -0.5236F, 0F, -0.6283F));
		PartDefinition part_arm_2_0 = part_arm_2.addOrReplaceChild("arm_2_0", CubeListBuilder.create()
			.texOffs(17, 81).addBox(-1.7F, 0F, -1.7F, 3.4F, 5.9F, 3.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_2_1 = part_arm_2_0.addOrReplaceChild("arm_2_1", CubeListBuilder.create()
			.texOffs(241, 81).addBox(-1.5F, 0F, -1.5F, 3F, 5.4F, 3F, new CubeDeformation(0F))
			.texOffs(7, 118).addBox(-0.4F, 1.5F, 1.2F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_2_2 = part_arm_2_1.addOrReplaceChild("arm_2_2", CubeListBuilder.create()
			.texOffs(117, 92).addBox(-1.3F, 0F, -1.3F, 2.6F, 4.9F, 2.6F, new CubeDeformation(0F))
			.texOffs(14, 118).addBox(-0.4F, 1.35F, 1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_arm_2_3 = part_arm_2_2.addOrReplaceChild("arm_2_3", CubeListBuilder.create()
			.texOffs(130, 92).addBox(-1.1F, 0F, -1.1F, 2.2F, 4.9F, 2.2F, new CubeDeformation(0F))
			.texOffs(21, 118).addBox(-0.4F, 1.35F, 0.8F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1396F, 0F, 0F));
		PartDefinition part_arm_2_4 = part_arm_2_3.addOrReplaceChild("arm_2_4", CubeListBuilder.create()
			.texOffs(66, 102).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.4F, 1.8F, new CubeDeformation(0F))
			.texOffs(28, 118).addBox(-0.4F, 1.2F, 0.6F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_2_5 = part_arm_2_4.addOrReplaceChild("arm_2_5", CubeListBuilder.create()
			.texOffs(33, 111).addBox(-0.7F, 0F, -0.7F, 1.4F, 3.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_arm_3 = part_head.addOrReplaceChild("arm_3", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0.7277F, 6.1519F, -4.5F, -0.5236F, 0F, -0.2094F));
		PartDefinition part_arm_3_0 = part_arm_3.addOrReplaceChild("arm_3_0", CubeListBuilder.create()
			.texOffs(34, 81).addBox(-1.7F, 0F, -1.7F, 3.4F, 5.9F, 3.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_3_1 = part_arm_3_0.addOrReplaceChild("arm_3_1", CubeListBuilder.create()
			.texOffs(0, 92).addBox(-1.5F, 0F, -1.5F, 3F, 5.4F, 3F, new CubeDeformation(0F))
			.texOffs(35, 118).addBox(-0.4F, 1.5F, 1.2F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_3_2 = part_arm_3_1.addOrReplaceChild("arm_3_2", CubeListBuilder.create()
			.texOffs(143, 92).addBox(-1.3F, 0F, -1.3F, 2.6F, 4.9F, 2.6F, new CubeDeformation(0F))
			.texOffs(42, 118).addBox(-0.4F, 1.35F, 1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_arm_3_3 = part_arm_3_2.addOrReplaceChild("arm_3_3", CubeListBuilder.create()
			.texOffs(156, 92).addBox(-1.1F, 0F, -1.1F, 2.2F, 4.9F, 2.2F, new CubeDeformation(0F))
			.texOffs(49, 118).addBox(-0.4F, 1.35F, 0.8F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1396F, 0F, 0F));
		PartDefinition part_arm_3_4 = part_arm_3_3.addOrReplaceChild("arm_3_4", CubeListBuilder.create()
			.texOffs(75, 102).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.4F, 1.8F, new CubeDeformation(0F))
			.texOffs(56, 118).addBox(-0.4F, 1.2F, 0.6F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_3_5 = part_arm_3_4.addOrReplaceChild("arm_3_5", CubeListBuilder.create()
			.texOffs(42, 111).addBox(-0.7F, 0F, -0.7F, 1.4F, 3.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_arm_4 = part_head.addOrReplaceChild("arm_4", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-0.7277F, 6.1519F, -4.5F, -0.5236F, 0F, 0.2094F));
		PartDefinition part_arm_4_0 = part_arm_4.addOrReplaceChild("arm_4_0", CubeListBuilder.create()
			.texOffs(51, 81).addBox(-1.7F, 0F, -1.7F, 3.4F, 5.9F, 3.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_4_1 = part_arm_4_0.addOrReplaceChild("arm_4_1", CubeListBuilder.create()
			.texOffs(13, 92).addBox(-1.5F, 0F, -1.5F, 3F, 5.4F, 3F, new CubeDeformation(0F))
			.texOffs(63, 118).addBox(-0.4F, 1.5F, 1.2F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_4_2 = part_arm_4_1.addOrReplaceChild("arm_4_2", CubeListBuilder.create()
			.texOffs(169, 92).addBox(-1.3F, 0F, -1.3F, 2.6F, 4.9F, 2.6F, new CubeDeformation(0F))
			.texOffs(70, 118).addBox(-0.4F, 1.35F, 1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_arm_4_3 = part_arm_4_2.addOrReplaceChild("arm_4_3", CubeListBuilder.create()
			.texOffs(182, 92).addBox(-1.1F, 0F, -1.1F, 2.2F, 4.9F, 2.2F, new CubeDeformation(0F))
			.texOffs(77, 118).addBox(-0.4F, 1.35F, 0.8F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1396F, 0F, 0F));
		PartDefinition part_arm_4_4 = part_arm_4_3.addOrReplaceChild("arm_4_4", CubeListBuilder.create()
			.texOffs(84, 102).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.4F, 1.8F, new CubeDeformation(0F))
			.texOffs(84, 118).addBox(-0.4F, 1.2F, 0.6F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_4_5 = part_arm_4_4.addOrReplaceChild("arm_4_5", CubeListBuilder.create()
			.texOffs(51, 111).addBox(-0.7F, 0F, -0.7F, 1.4F, 3.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_arm_5 = part_head.addOrReplaceChild("arm_5", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-2.0572F, 5.7798F, -4.5F, -0.5236F, 0F, 0.6283F));
		PartDefinition part_arm_5_0 = part_arm_5.addOrReplaceChild("arm_5_0", CubeListBuilder.create()
			.texOffs(68, 81).addBox(-1.7F, 0F, -1.7F, 3.4F, 5.9F, 3.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_5_1 = part_arm_5_0.addOrReplaceChild("arm_5_1", CubeListBuilder.create()
			.texOffs(26, 92).addBox(-1.5F, 0F, -1.5F, 3F, 5.4F, 3F, new CubeDeformation(0F))
			.texOffs(91, 118).addBox(-0.4F, 1.5F, 1.2F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_5_2 = part_arm_5_1.addOrReplaceChild("arm_5_2", CubeListBuilder.create()
			.texOffs(195, 92).addBox(-1.3F, 0F, -1.3F, 2.6F, 4.9F, 2.6F, new CubeDeformation(0F))
			.texOffs(98, 118).addBox(-0.4F, 1.35F, 1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_arm_5_3 = part_arm_5_2.addOrReplaceChild("arm_5_3", CubeListBuilder.create()
			.texOffs(208, 92).addBox(-1.1F, 0F, -1.1F, 2.2F, 4.9F, 2.2F, new CubeDeformation(0F))
			.texOffs(105, 118).addBox(-0.4F, 1.35F, 0.8F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1396F, 0F, 0F));
		PartDefinition part_arm_5_4 = part_arm_5_3.addOrReplaceChild("arm_5_4", CubeListBuilder.create()
			.texOffs(93, 102).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.4F, 1.8F, new CubeDeformation(0F))
			.texOffs(112, 118).addBox(-0.4F, 1.2F, 0.6F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_5_5 = part_arm_5_4.addOrReplaceChild("arm_5_5", CubeListBuilder.create()
			.texOffs(60, 111).addBox(-0.7F, 0F, -0.7F, 1.4F, 3.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_arm_6 = part_head.addOrReplaceChild("arm_6", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-3.0311F, 5.1F, -4.5F, -0.5236F, 0F, 1.0472F));
		PartDefinition part_arm_6_0 = part_arm_6.addOrReplaceChild("arm_6_0", CubeListBuilder.create()
			.texOffs(85, 81).addBox(-1.7F, 0F, -1.7F, 3.4F, 5.9F, 3.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_6_1 = part_arm_6_0.addOrReplaceChild("arm_6_1", CubeListBuilder.create()
			.texOffs(39, 92).addBox(-1.5F, 0F, -1.5F, 3F, 5.4F, 3F, new CubeDeformation(0F))
			.texOffs(119, 118).addBox(-0.4F, 1.5F, 1.2F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_6_2 = part_arm_6_1.addOrReplaceChild("arm_6_2", CubeListBuilder.create()
			.texOffs(221, 92).addBox(-1.3F, 0F, -1.3F, 2.6F, 4.9F, 2.6F, new CubeDeformation(0F))
			.texOffs(126, 118).addBox(-0.4F, 1.35F, 1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_arm_6_3 = part_arm_6_2.addOrReplaceChild("arm_6_3", CubeListBuilder.create()
			.texOffs(234, 92).addBox(-1.1F, 0F, -1.1F, 2.2F, 4.9F, 2.2F, new CubeDeformation(0F))
			.texOffs(133, 118).addBox(-0.4F, 1.35F, 0.8F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1396F, 0F, 0F));
		PartDefinition part_arm_6_4 = part_arm_6_3.addOrReplaceChild("arm_6_4", CubeListBuilder.create()
			.texOffs(102, 102).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.4F, 1.8F, new CubeDeformation(0F))
			.texOffs(140, 118).addBox(-0.4F, 1.2F, 0.6F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_6_5 = part_arm_6_4.addOrReplaceChild("arm_6_5", CubeListBuilder.create()
			.texOffs(69, 111).addBox(-0.7F, 0F, -0.7F, 1.4F, 3.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_arm_7 = part_head.addOrReplaceChild("arm_7", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-3.4808F, 4.23F, -4.5F, -0.5236F, 0F, 1.4661F));
		PartDefinition part_arm_7_0 = part_arm_7.addOrReplaceChild("arm_7_0", CubeListBuilder.create()
			.texOffs(102, 81).addBox(-1.7F, 0F, -1.7F, 3.4F, 5.9F, 3.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_7_1 = part_arm_7_0.addOrReplaceChild("arm_7_1", CubeListBuilder.create()
			.texOffs(52, 92).addBox(-1.5F, 0F, -1.5F, 3F, 5.4F, 3F, new CubeDeformation(0F))
			.texOffs(147, 118).addBox(-0.4F, 1.5F, 1.2F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5.5F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_arm_7_2 = part_arm_7_1.addOrReplaceChild("arm_7_2", CubeListBuilder.create()
			.texOffs(0, 102).addBox(-1.3F, 0F, -1.3F, 2.6F, 4.9F, 2.6F, new CubeDeformation(0F))
			.texOffs(154, 118).addBox(-0.4F, 1.35F, 1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_arm_7_3 = part_arm_7_2.addOrReplaceChild("arm_7_3", CubeListBuilder.create()
			.texOffs(13, 102).addBox(-1.1F, 0F, -1.1F, 2.2F, 4.9F, 2.2F, new CubeDeformation(0F))
			.texOffs(161, 118).addBox(-0.4F, 1.35F, 0.8F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1396F, 0F, 0F));
		PartDefinition part_arm_7_4 = part_arm_7_3.addOrReplaceChild("arm_7_4", CubeListBuilder.create()
			.texOffs(111, 102).addBox(-0.9F, 0F, -0.9F, 1.8F, 4.4F, 1.8F, new CubeDeformation(0F))
			.texOffs(168, 118).addBox(-0.4F, 1.2F, 0.6F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_7_5 = part_arm_7_4.addOrReplaceChild("arm_7_5", CubeListBuilder.create()
			.texOffs(78, 111).addBox(-0.7F, 0F, -0.7F, 1.4F, 3.9F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2269F, 0F, 0F));
		PartDefinition part_feeder_l = part_head.addOrReplaceChild("feeder_l", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.6F, 3F, -6.5F, -0.9599F, 0F, -0.1745F));
		PartDefinition part_feeder_l_0 = part_feeder_l.addOrReplaceChild("feeder_l_0", CubeListBuilder.create()
			.texOffs(120, 102).addBox(-0.85F, 0F, -0.85F, 1.7F, 4.6F, 1.7F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_feeder_l_1 = part_feeder_l_0.addOrReplaceChild("feeder_l_1", CubeListBuilder.create()
			.texOffs(129, 102).addBox(-0.81F, 0F, -0.81F, 1.62F, 4.6F, 1.62F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_l_2 = part_feeder_l_1.addOrReplaceChild("feeder_l_2", CubeListBuilder.create()
			.texOffs(138, 102).addBox(-0.77F, 0F, -0.77F, 1.54F, 4.6F, 1.54F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_l_3 = part_feeder_l_2.addOrReplaceChild("feeder_l_3", CubeListBuilder.create()
			.texOffs(147, 102).addBox(-0.73F, 0F, -0.73F, 1.46F, 4.6F, 1.46F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_l_4 = part_feeder_l_3.addOrReplaceChild("feeder_l_4", CubeListBuilder.create()
			.texOffs(156, 102).addBox(-0.69F, 0F, -0.69F, 1.38F, 4.6F, 1.38F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_l_5 = part_feeder_l_4.addOrReplaceChild("feeder_l_5", CubeListBuilder.create()
			.texOffs(165, 102).addBox(-0.65F, 0F, -0.65F, 1.3F, 4.6F, 1.3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_feeder_l_6 = part_feeder_l_5.addOrReplaceChild("feeder_l_6", CubeListBuilder.create()
			.texOffs(174, 102).addBox(-0.61F, 0F, -0.61F, 1.22F, 4.6F, 1.22F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_feeder_l_club = part_feeder_l_6.addOrReplaceChild("feeder_l_club", CubeListBuilder.create()
			.texOffs(26, 102).addBox(-1.4F, 0F, -0.98F, 2.8F, 5F, 1.96F, new CubeDeformation(0F))
			.texOffs(186, 111).addBox(-0.84F, 5F, -0.7F, 1.68F, 1.6F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_feeder_r = part_head.addOrReplaceChild("feeder_r", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.6F, 3F, -6.5F, -0.9599F, 0F, 0.1745F));
		PartDefinition part_feeder_r_0 = part_feeder_r.addOrReplaceChild("feeder_r_0", CubeListBuilder.create()
			.texOffs(183, 102).addBox(-0.85F, 0F, -0.85F, 1.7F, 4.6F, 1.7F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_feeder_r_1 = part_feeder_r_0.addOrReplaceChild("feeder_r_1", CubeListBuilder.create()
			.texOffs(192, 102).addBox(-0.81F, 0F, -0.81F, 1.62F, 4.6F, 1.62F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_r_2 = part_feeder_r_1.addOrReplaceChild("feeder_r_2", CubeListBuilder.create()
			.texOffs(201, 102).addBox(-0.77F, 0F, -0.77F, 1.54F, 4.6F, 1.54F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_r_3 = part_feeder_r_2.addOrReplaceChild("feeder_r_3", CubeListBuilder.create()
			.texOffs(210, 102).addBox(-0.73F, 0F, -0.73F, 1.46F, 4.6F, 1.46F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_r_4 = part_feeder_r_3.addOrReplaceChild("feeder_r_4", CubeListBuilder.create()
			.texOffs(219, 102).addBox(-0.69F, 0F, -0.69F, 1.38F, 4.6F, 1.38F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_r_5 = part_feeder_r_4.addOrReplaceChild("feeder_r_5", CubeListBuilder.create()
			.texOffs(228, 102).addBox(-0.65F, 0F, -0.65F, 1.3F, 4.6F, 1.3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_feeder_r_6 = part_feeder_r_5.addOrReplaceChild("feeder_r_6", CubeListBuilder.create()
			.texOffs(237, 102).addBox(-0.61F, 0F, -0.61F, 1.22F, 4.6F, 1.22F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_feeder_r_club = part_feeder_r_6.addOrReplaceChild("feeder_r_club", CubeListBuilder.create()
			.texOffs(37, 102).addBox(-1.4F, 0F, -0.98F, 2.8F, 5F, 1.96F, new CubeDeformation(0F))
			.texOffs(195, 111).addBox(-0.84F, 5F, -0.7F, 1.68F, 1.6F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.2F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_shell = part_body.addOrReplaceChild("shell", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0F, 3F, 5F, -1.5708F, 0F, 0F));
		PartDefinition part_whorl_0 = part_shell.addOrReplaceChild("whorl_0", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-7.5F, -6.8F, -2.55F, 15F, 6.8F, 17F, new CubeDeformation(0F))
			.texOffs(0, 25).addBox(-8.1F, -1.2F, -3.15F, 16.2F, 1.2F, 18.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_whorl_1 = part_whorl_0.addOrReplaceChild("whorl_1", CubeListBuilder.create()
			.texOffs(65, 0).addBox(-6.9375F, -6.296F, -2.3588F, 13.875F, 7.496F, 15.725F, new CubeDeformation(0F))
			.texOffs(131, 111).addBox(-0.7F, -3.0576F, 13.0663F, 1.4F, 1.4F, 2.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -5.6F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_2 = part_whorl_1.addOrReplaceChild("whorl_2", CubeListBuilder.create()
			.texOffs(173, 0).addBox(-6.4172F, -5.8374F, -2.1818F, 12.8344F, 7.0374F, 14.5456F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -5.096F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_3 = part_whorl_2.addOrReplaceChild("whorl_3", CubeListBuilder.create()
			.texOffs(73, 25).addBox(-5.9359F, -5.42F, -2.0182F, 11.8718F, 6.62F, 13.4547F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -4.6374F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_4 = part_whorl_3.addOrReplaceChild("whorl_4", CubeListBuilder.create()
			.texOffs(126, 25).addBox(-5.4907F, -5.0402F, -1.8668F, 10.9814F, 6.2402F, 12.4456F, new CubeDeformation(0F))
			.texOffs(142, 111).addBox(-0.7F, -2.3041F, 10.2788F, 1.4F, 1.4F, 2.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -4.22F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_5 = part_whorl_4.addOrReplaceChild("whorl_5", CubeListBuilder.create()
			.texOffs(0, 47).addBox(-5.0789F, -4.6946F, -1.7268F, 10.1578F, 5.8946F, 11.5122F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -3.8402F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_6 = part_whorl_5.addOrReplaceChild("whorl_6", CubeListBuilder.create()
			.texOffs(108, 47).addBox(-4.698F, -4.3801F, -1.5973F, 9.396F, 5.5801F, 10.6488F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -3.4946F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_7 = part_whorl_6.addOrReplaceChild("whorl_7", CubeListBuilder.create()
			.texOffs(151, 47).addBox(-4.3456F, -4.0939F, -1.4775F, 8.6913F, 5.2939F, 9.8501F, new CubeDeformation(0F))
			.texOffs(153, 111).addBox(-0.7F, -1.7363F, 8.0726F, 1.4F, 1.4F, 2.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -3.1801F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_8 = part_whorl_7.addOrReplaceChild("whorl_8", CubeListBuilder.create()
			.texOffs(190, 47).addBox(-4.0197F, -3.8334F, -1.3667F, 8.0394F, 5.0334F, 9.1114F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -2.8939F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_9 = part_whorl_8.addOrReplaceChild("whorl_9", CubeListBuilder.create()
			.texOffs(0, 66).addBox(-3.7182F, -3.5964F, -1.2642F, 7.4365F, 4.7964F, 8.428F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -2.6334F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_10 = part_whorl_9.addOrReplaceChild("whorl_10", CubeListBuilder.create()
			.texOffs(64, 66).addBox(-3.4394F, -3.3807F, -1.1694F, 6.8787F, 4.5807F, 7.7959F, new CubeDeformation(0F))
			.texOffs(164, 111).addBox(-0.7F, -1.3084F, 6.3265F, 1.4F, 1.4F, 2.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -2.3964F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_11 = part_whorl_10.addOrReplaceChild("whorl_11", CubeListBuilder.create()
			.texOffs(95, 66).addBox(-3.1814F, -3.1845F, -1.0817F, 6.3628F, 4.3845F, 7.2112F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -2.1807F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_12 = part_whorl_11.addOrReplaceChild("whorl_12", CubeListBuilder.create()
			.texOffs(126, 66).addBox(-2.9428F, -3.0059F, -1.0006F, 5.8856F, 4.2059F, 6.6704F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -1.9845F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_13 = part_whorl_12.addOrReplaceChild("whorl_13", CubeListBuilder.create()
			.texOffs(153, 66).addBox(-2.7221F, -2.8433F, -0.9255F, 5.4442F, 4.0433F, 6.1701F, new CubeDeformation(0F))
			.texOffs(175, 111).addBox(-0.7F, -0.986F, 4.9446F, 1.4F, 1.4F, 2.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -1.8059F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_14 = part_whorl_13.addOrReplaceChild("whorl_14", CubeListBuilder.create()
			.texOffs(180, 66).addBox(-2.5179F, -2.6954F, -0.8561F, 5.0359F, 3.8954F, 5.7073F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -1.6433F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_15 = part_whorl_14.addOrReplaceChild("whorl_15", CubeListBuilder.create()
			.texOffs(205, 66).addBox(-2.3291F, -2.5608F, -0.7919F, 4.6582F, 3.7608F, 5.2793F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -1.4954F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_16 = part_whorl_15.addOrReplaceChild("whorl_16", CubeListBuilder.create()
			.texOffs(175, 81).addBox(-2.1544F, -2.4384F, -0.7325F, 4.3088F, 3.6384F, 4.8833F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -1.3608F, 0F, 0.3665F, 0F, 0F));
		PartDefinition part_whorl_17 = part_whorl_16.addOrReplaceChild("whorl_17", CubeListBuilder.create()
			.texOffs(196, 81).addBox(-1.9928F, -2.3269F, -0.6776F, 3.9857F, 3.5269F, 4.5171F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -1.2384F, 0F, 0.3665F, 0F, 0F));
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
