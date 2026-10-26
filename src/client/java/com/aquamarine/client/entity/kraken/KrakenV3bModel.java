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
 * Candidata B, Vampiro-abissal: manto em cúpula alta com duas barbatanas de orelha que remam, olhos na frente da cabeça e 8 braços abertos em guarda-chuva, ligados por membrana, com espinhos nas costas e lanternas acesas nas pontas; 2 filamentos longos de caça.
 * GERADO por tools/models/kraken_v3/build_game.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class KrakenV3bModel extends EntityModel<KrakenRenderState> {
	private final KrakenV3Motion.Parts motion;
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("kraken_v3b"), "main");
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

	public KrakenV3bModel(ModelPart root) {
		super(root);
		this.motion = new KrakenV3Motion.Parts(KrakenV3Motion.part(root, "body", "head"),
			new ModelPart[] {KrakenV3Motion.part(root, "body", "head", "mantle")},
			new ModelPart[][] {
			{KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3", "arm_0_4"), KrakenV3Motion.part(root, "body", "head", "arm_0", "arm_0_0", "arm_0_1", "arm_0_2", "arm_0_3", "arm_0_4", "arm_0_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3", "arm_1_4"), KrakenV3Motion.part(root, "body", "head", "arm_1", "arm_1_0", "arm_1_1", "arm_1_2", "arm_1_3", "arm_1_4", "arm_1_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3", "arm_2_4"), KrakenV3Motion.part(root, "body", "head", "arm_2", "arm_2_0", "arm_2_1", "arm_2_2", "arm_2_3", "arm_2_4", "arm_2_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3", "arm_3_4"), KrakenV3Motion.part(root, "body", "head", "arm_3", "arm_3_0", "arm_3_1", "arm_3_2", "arm_3_3", "arm_3_4", "arm_3_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3", "arm_4_4"), KrakenV3Motion.part(root, "body", "head", "arm_4", "arm_4_0", "arm_4_1", "arm_4_2", "arm_4_3", "arm_4_4", "arm_4_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3", "arm_5_4"), KrakenV3Motion.part(root, "body", "head", "arm_5", "arm_5_0", "arm_5_1", "arm_5_2", "arm_5_3", "arm_5_4", "arm_5_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3", "arm_6_4"), KrakenV3Motion.part(root, "body", "head", "arm_6", "arm_6_0", "arm_6_1", "arm_6_2", "arm_6_3", "arm_6_4", "arm_6_5")},
			{KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3", "arm_7_4"), KrakenV3Motion.part(root, "body", "head", "arm_7", "arm_7_0", "arm_7_1", "arm_7_2", "arm_7_3", "arm_7_4", "arm_7_5")}});
		this.idle = KrakenV3bAnimations.IDLE.bake(root);
		this.swim = KrakenV3bAnimations.SWIM.bake(root);
		this.emerge = KrakenV3bAnimations.EMERGE.bake(root);
		this.stunned = KrakenV3bAnimations.STUNNED.bake(root);
		this.phaseChange = KrakenV3bAnimations.PHASE_CHANGE.bake(root);
		this.death = KrakenV3bAnimations.DEATH.bake(root);
		this.slam = KrakenV3bAnimations.SLAM.bake(root);
		this.grab = KrakenV3bAnimations.GRAB.bake(root);
		this.ink = KrakenV3bAnimations.INK.bake(root);
		this.whirlpool = KrakenV3bAnimations.WHIRLPOOL.bake(root);
		this.spawnYoung = KrakenV3bAnimations.SPAWN_YOUNG.bake(root);
		this.waterJet = KrakenV3bAnimations.WATER_JET.bake(root);
		this.roar = KrakenV3bAnimations.ROAR.bake(root);
		this.bolts = KrakenV3bAnimations.BOLTS.bake(root);
		this.ambush = KrakenV3bAnimations.AMBUSH.bake(root);
		this.doubleGrab = KrakenV3bAnimations.DOUBLE_GRAB.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition part_body = root.addOrReplaceChild("body", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_head = part_body.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(0, 78).addBox(-7F, -5F, -4.48F, 14F, 10F, 8.96F, new CubeDeformation(0F))
			.texOffs(0, 31).addBox(-4.48F, -5F, -7F, 8.96F, 10F, 14F, new CubeDeformation(0F))
			.texOffs(147, 98).addBox(-5.5F, 5F, -3.52F, 11F, 2F, 7.04F, new CubeDeformation(0F))
			.texOffs(214, 78).addBox(-3.52F, 5F, -5.5F, 7.04F, 2F, 11F, new CubeDeformation(0F))
			.texOffs(204, 111).addBox(-8.2F, -4.6F, -5.6F, 1.8F, 1.4F, 7F, new CubeDeformation(0F))
			.texOffs(223, 111).addBox(6.4F, -4.6F, -5.6F, 1.8F, 1.4F, 7F, new CubeDeformation(0F))
			.texOffs(0, 98).addBox(-8.4F, -3F, -5.2F, 1.6F, 6F, 6F, new CubeDeformation(0F))
			.texOffs(17, 98).addBox(6.8F, -3F, -5.2F, 1.6F, 6F, 6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_lid_left = part_head.addOrReplaceChild("lid_left", CubeListBuilder.create()
			.texOffs(34, 98).addBox(-8.1F, -2.75F, -4.95F, 1F, 5.5F, 5.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_lid_right = part_head.addOrReplaceChild("lid_right", CubeListBuilder.create()
			.texOffs(49, 98).addBox(7.1F, -2.75F, -4.95F, 1F, 5.5F, 5.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_beak = part_head.addOrReplaceChild("beak", CubeListBuilder.create()
			.texOffs(124, 146).addBox(-1.8F, 0F, -1.8F, 3.6F, 2F, 3.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 7F, 0F, 0F, 0F, 0F));
		PartDefinition part_jaw = part_beak.addOrReplaceChild("jaw", CubeListBuilder.create()
			.texOffs(59, 153).addBox(-1.2F, 0F, -1.2F, 2.4F, 1.5F, 2.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
		PartDefinition part_mantle = part_head.addOrReplaceChild("mantle", CubeListBuilder.create()
			.texOffs(112, 78).addBox(-9F, -3F, -5.76F, 18F, 3F, 11.52F, new CubeDeformation(0F))
			.texOffs(0, 56).addBox(-5.76F, -3F, -9F, 11.52F, 3F, 18F, new CubeDeformation(0F))
			.texOffs(61, 56).addBox(-12F, -7F, -7.68F, 24F, 4F, 15.36F, new CubeDeformation(0F))
			.texOffs(87, 0).addBox(-7.68F, -7F, -12F, 15.36F, 4F, 24F, new CubeDeformation(0F))
			.texOffs(112, 31).addBox(-13F, -11F, -8.32F, 26F, 4F, 16.64F, new CubeDeformation(0F))
			.texOffs(0, 0).addBox(-8.32F, -11F, -13F, 16.64F, 4F, 26F, new CubeDeformation(0F))
			.texOffs(90, 153).addBox(-13.6F, -10F, -1F, 2F, 2F, 2F, new CubeDeformation(0F))
			.texOffs(99, 153).addBox(11.6F, -10F, -1F, 2F, 2F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -5F, 0.5F, -0.1745F, 0F, 0F));
		PartDefinition part_mantle_tip = part_mantle.addOrReplaceChild("mantle_tip", CubeListBuilder.create()
			.texOffs(142, 56).addBox(-12F, -4F, -7.68F, 24F, 4F, 15.36F, new CubeDeformation(0F))
			.texOffs(168, 0).addBox(-7.68F, -4F, -12F, 15.36F, 4F, 24F, new CubeDeformation(0F))
			.texOffs(47, 78).addBox(-9.5F, -7F, -6.08F, 19F, 3F, 12.16F, new CubeDeformation(0F))
			.texOffs(47, 31).addBox(-6.08F, -7F, -9.5F, 12.16F, 3F, 19F, new CubeDeformation(0F))
			.texOffs(106, 98).addBox(-6F, -9F, -3.84F, 12F, 2F, 7.68F, new CubeDeformation(0F))
			.texOffs(173, 78).addBox(-3.84F, -9F, -6F, 7.68F, 2F, 12F, new CubeDeformation(0F))
			.texOffs(40, 153).addBox(-2.5F, -10F, -1.6F, 5F, 1F, 3.2F, new CubeDeformation(0F))
			.texOffs(105, 146).addBox(-1.6F, -10F, -2.5F, 3.2F, 1F, 5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -11F, 0F, 0F, 0F, 0F));
		PartDefinition part_ear_left = part_mantle.addOrReplaceChild("ear_left", CubeListBuilder.create()
			.texOffs(64, 98).addBox(-8F, -4.5F, -0.6F, 8F, 9F, 1.2F, new CubeDeformation(0F))
			.texOffs(195, 122).addBox(-11F, -3F, -0.6F, 3F, 6F, 1.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-12.5F, -9F, 0F, 0F, 0F, -0.3491F));
		PartDefinition part_ear_right = part_mantle.addOrReplaceChild("ear_right", CubeListBuilder.create()
			.texOffs(85, 98).addBox(0F, -4.5F, -0.6F, 8F, 9F, 1.2F, new CubeDeformation(0F))
			.texOffs(206, 122).addBox(8F, -3F, -0.6F, 3F, 6F, 1.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(12.5F, -9F, 0F, 0F, 0F, 0.3491F));
		PartDefinition part_arm_0 = part_head.addOrReplaceChild("arm_0", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.9134F, 6F, 4.6194F, 0.9076F, 0.3927F, 0F));
		PartDefinition part_arm_0_0 = part_arm_0.addOrReplaceChild("arm_0_0", CubeListBuilder.create()
			.texOffs(186, 98).addBox(-1.8F, 0F, -1.8F, 3.6F, 5.4F, 3.6F, new CubeDeformation(0F))
			.texOffs(162, 131).addBox(1.6F, 0F, -0.4F, 1.7346F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_0_1 = part_arm_0_0.addOrReplaceChild("arm_0_1", CubeListBuilder.create()
			.texOffs(203, 98).addBox(-1.6F, 0F, -1.6F, 3.2F, 5.4F, 3.2F, new CubeDeformation(0F))
			.texOffs(180, 153).addBox(-0.4F, 1.5F, 1.3F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(217, 122).addBox(1.4F, 0F, -0.4F, 5.1502F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_arm_0_2 = part_arm_0_1.addOrReplaceChild("arm_0_2", CubeListBuilder.create()
			.texOffs(242, 111).addBox(-1.4F, 0F, -1.4F, 2.8F, 4.9F, 2.8F, new CubeDeformation(0F))
			.texOffs(187, 153).addBox(-0.4F, 1.35F, 1.1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(189, 139).addBox(1.2F, 0F, -0.4F, 8.415F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_0_3 = part_arm_0_2.addOrReplaceChild("arm_0_3", CubeListBuilder.create()
			.texOffs(0, 122).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F))
			.texOffs(194, 153).addBox(-0.4F, 1.35F, 0.9F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(218, 131).addBox(1F, 0F, -0.4F, 11.529F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_0_4 = part_arm_0_3.addOrReplaceChild("arm_0_4", CubeListBuilder.create()
			.texOffs(90, 131).addBox(-1F, 0F, -1F, 2F, 4.4F, 2F, new CubeDeformation(0F))
			.texOffs(201, 153).addBox(-0.4F, 1.2F, 0.7F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_0_5 = part_arm_0_4.addOrReplaceChild("arm_0_5", CubeListBuilder.create()
			.texOffs(141, 146).addBox(-0.8F, 0F, -0.8F, 1.6F, 3.9F, 1.6F, new CubeDeformation(0F))
			.texOffs(108, 153).addBox(-0.8F, 3.5F, -0.8F, 1.6F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_1 = part_head.addOrReplaceChild("arm_1", CubeListBuilder.create(),
			PartPose.offsetAndRotation(4.6194F, 6F, 1.9134F, 0.9076F, 1.1781F, 0F));
		PartDefinition part_arm_1_0 = part_arm_1.addOrReplaceChild("arm_1_0", CubeListBuilder.create()
			.texOffs(220, 98).addBox(-1.8F, 0F, -1.8F, 3.6F, 5.4F, 3.6F, new CubeDeformation(0F))
			.texOffs(169, 131).addBox(1.6F, 0F, -0.4F, 1.7346F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_1_1 = part_arm_1_0.addOrReplaceChild("arm_1_1", CubeListBuilder.create()
			.texOffs(237, 98).addBox(-1.6F, 0F, -1.6F, 3.2F, 5.4F, 3.2F, new CubeDeformation(0F))
			.texOffs(208, 153).addBox(-0.4F, 1.5F, 1.3F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(232, 122).addBox(1.4F, 0F, -0.4F, 5.1502F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_arm_1_2 = part_arm_1_1.addOrReplaceChild("arm_1_2", CubeListBuilder.create()
			.texOffs(13, 122).addBox(-1.4F, 0F, -1.4F, 2.8F, 4.9F, 2.8F, new CubeDeformation(0F))
			.texOffs(215, 153).addBox(-0.4F, 1.35F, 1.1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(210, 139).addBox(1.2F, 0F, -0.4F, 8.415F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_1_3 = part_arm_1_2.addOrReplaceChild("arm_1_3", CubeListBuilder.create()
			.texOffs(26, 122).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F))
			.texOffs(222, 153).addBox(-0.4F, 1.35F, 0.9F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(0, 139).addBox(1F, 0F, -0.4F, 11.529F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_1_4 = part_arm_1_3.addOrReplaceChild("arm_1_4", CubeListBuilder.create()
			.texOffs(99, 131).addBox(-1F, 0F, -1F, 2F, 4.4F, 2F, new CubeDeformation(0F))
			.texOffs(229, 153).addBox(-0.4F, 1.2F, 0.7F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_1_5 = part_arm_1_4.addOrReplaceChild("arm_1_5", CubeListBuilder.create()
			.texOffs(150, 146).addBox(-0.8F, 0F, -0.8F, 1.6F, 3.9F, 1.6F, new CubeDeformation(0F))
			.texOffs(117, 153).addBox(-0.8F, 3.5F, -0.8F, 1.6F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_2 = part_head.addOrReplaceChild("arm_2", CubeListBuilder.create(),
			PartPose.offsetAndRotation(4.6194F, 6F, -1.9134F, 0.9076F, 1.9635F, 0F));
		PartDefinition part_arm_2_0 = part_arm_2.addOrReplaceChild("arm_2_0", CubeListBuilder.create()
			.texOffs(0, 111).addBox(-1.8F, 0F, -1.8F, 3.6F, 5.4F, 3.6F, new CubeDeformation(0F))
			.texOffs(176, 131).addBox(1.6F, 0F, -0.4F, 1.7346F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_2_1 = part_arm_2_0.addOrReplaceChild("arm_2_1", CubeListBuilder.create()
			.texOffs(17, 111).addBox(-1.6F, 0F, -1.6F, 3.2F, 5.4F, 3.2F, new CubeDeformation(0F))
			.texOffs(236, 153).addBox(-0.4F, 1.5F, 1.3F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(0, 131).addBox(1.4F, 0F, -0.4F, 5.1502F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_arm_2_2 = part_arm_2_1.addOrReplaceChild("arm_2_2", CubeListBuilder.create()
			.texOffs(39, 122).addBox(-1.4F, 0F, -1.4F, 2.8F, 4.9F, 2.8F, new CubeDeformation(0F))
			.texOffs(243, 153).addBox(-0.4F, 1.35F, 1.1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(231, 139).addBox(1.2F, 0F, -0.4F, 8.415F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_2_3 = part_arm_2_2.addOrReplaceChild("arm_2_3", CubeListBuilder.create()
			.texOffs(52, 122).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F))
			.texOffs(250, 153).addBox(-0.4F, 1.35F, 0.9F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(27, 139).addBox(1F, 0F, -0.4F, 11.529F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_2_4 = part_arm_2_3.addOrReplaceChild("arm_2_4", CubeListBuilder.create()
			.texOffs(108, 131).addBox(-1F, 0F, -1F, 2F, 4.4F, 2F, new CubeDeformation(0F))
			.texOffs(0, 160).addBox(-0.4F, 1.2F, 0.7F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_2_5 = part_arm_2_4.addOrReplaceChild("arm_2_5", CubeListBuilder.create()
			.texOffs(159, 146).addBox(-0.8F, 0F, -0.8F, 1.6F, 3.9F, 1.6F, new CubeDeformation(0F))
			.texOffs(126, 153).addBox(-0.8F, 3.5F, -0.8F, 1.6F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_3 = part_head.addOrReplaceChild("arm_3", CubeListBuilder.create(),
			PartPose.offsetAndRotation(1.9134F, 6F, -4.6194F, 0.9076F, 2.7489F, 0F));
		PartDefinition part_arm_3_0 = part_arm_3.addOrReplaceChild("arm_3_0", CubeListBuilder.create()
			.texOffs(34, 111).addBox(-1.8F, 0F, -1.8F, 3.6F, 5.4F, 3.6F, new CubeDeformation(0F))
			.texOffs(183, 131).addBox(1.6F, 0F, -0.4F, 1.7346F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_3_1 = part_arm_3_0.addOrReplaceChild("arm_3_1", CubeListBuilder.create()
			.texOffs(51, 111).addBox(-1.6F, 0F, -1.6F, 3.2F, 5.4F, 3.2F, new CubeDeformation(0F))
			.texOffs(7, 160).addBox(-0.4F, 1.5F, 1.3F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(15, 131).addBox(1.4F, 0F, -0.4F, 5.1502F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_arm_3_2 = part_arm_3_1.addOrReplaceChild("arm_3_2", CubeListBuilder.create()
			.texOffs(65, 122).addBox(-1.4F, 0F, -1.4F, 2.8F, 4.9F, 2.8F, new CubeDeformation(0F))
			.texOffs(14, 160).addBox(-0.4F, 1.35F, 1.1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(0, 146).addBox(1.2F, 0F, -0.4F, 8.415F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_3_3 = part_arm_3_2.addOrReplaceChild("arm_3_3", CubeListBuilder.create()
			.texOffs(78, 122).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F))
			.texOffs(21, 160).addBox(-0.4F, 1.35F, 0.9F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(54, 139).addBox(1F, 0F, -0.4F, 11.529F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_3_4 = part_arm_3_3.addOrReplaceChild("arm_3_4", CubeListBuilder.create()
			.texOffs(117, 131).addBox(-1F, 0F, -1F, 2F, 4.4F, 2F, new CubeDeformation(0F))
			.texOffs(28, 160).addBox(-0.4F, 1.2F, 0.7F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_3_5 = part_arm_3_4.addOrReplaceChild("arm_3_5", CubeListBuilder.create()
			.texOffs(168, 146).addBox(-0.8F, 0F, -0.8F, 1.6F, 3.9F, 1.6F, new CubeDeformation(0F))
			.texOffs(135, 153).addBox(-0.8F, 3.5F, -0.8F, 1.6F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_4 = part_head.addOrReplaceChild("arm_4", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.9134F, 6F, -4.6194F, 0.9076F, 3.5343F, 0F));
		PartDefinition part_arm_4_0 = part_arm_4.addOrReplaceChild("arm_4_0", CubeListBuilder.create()
			.texOffs(68, 111).addBox(-1.8F, 0F, -1.8F, 3.6F, 5.4F, 3.6F, new CubeDeformation(0F))
			.texOffs(190, 131).addBox(1.6F, 0F, -0.4F, 1.7346F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_4_1 = part_arm_4_0.addOrReplaceChild("arm_4_1", CubeListBuilder.create()
			.texOffs(85, 111).addBox(-1.6F, 0F, -1.6F, 3.2F, 5.4F, 3.2F, new CubeDeformation(0F))
			.texOffs(35, 160).addBox(-0.4F, 1.5F, 1.3F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(30, 131).addBox(1.4F, 0F, -0.4F, 5.1502F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_arm_4_2 = part_arm_4_1.addOrReplaceChild("arm_4_2", CubeListBuilder.create()
			.texOffs(91, 122).addBox(-1.4F, 0F, -1.4F, 2.8F, 4.9F, 2.8F, new CubeDeformation(0F))
			.texOffs(42, 160).addBox(-0.4F, 1.35F, 1.1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(21, 146).addBox(1.2F, 0F, -0.4F, 8.415F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_4_3 = part_arm_4_2.addOrReplaceChild("arm_4_3", CubeListBuilder.create()
			.texOffs(104, 122).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F))
			.texOffs(49, 160).addBox(-0.4F, 1.35F, 0.9F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(81, 139).addBox(1F, 0F, -0.4F, 11.529F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_4_4 = part_arm_4_3.addOrReplaceChild("arm_4_4", CubeListBuilder.create()
			.texOffs(126, 131).addBox(-1F, 0F, -1F, 2F, 4.4F, 2F, new CubeDeformation(0F))
			.texOffs(56, 160).addBox(-0.4F, 1.2F, 0.7F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_4_5 = part_arm_4_4.addOrReplaceChild("arm_4_5", CubeListBuilder.create()
			.texOffs(177, 146).addBox(-0.8F, 0F, -0.8F, 1.6F, 3.9F, 1.6F, new CubeDeformation(0F))
			.texOffs(144, 153).addBox(-0.8F, 3.5F, -0.8F, 1.6F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_5 = part_head.addOrReplaceChild("arm_5", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-4.6194F, 6F, -1.9134F, 0.9076F, 4.3197F, 0F));
		PartDefinition part_arm_5_0 = part_arm_5.addOrReplaceChild("arm_5_0", CubeListBuilder.create()
			.texOffs(102, 111).addBox(-1.8F, 0F, -1.8F, 3.6F, 5.4F, 3.6F, new CubeDeformation(0F))
			.texOffs(197, 131).addBox(1.6F, 0F, -0.4F, 1.7346F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_5_1 = part_arm_5_0.addOrReplaceChild("arm_5_1", CubeListBuilder.create()
			.texOffs(119, 111).addBox(-1.6F, 0F, -1.6F, 3.2F, 5.4F, 3.2F, new CubeDeformation(0F))
			.texOffs(63, 160).addBox(-0.4F, 1.5F, 1.3F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(45, 131).addBox(1.4F, 0F, -0.4F, 5.1502F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_arm_5_2 = part_arm_5_1.addOrReplaceChild("arm_5_2", CubeListBuilder.create()
			.texOffs(117, 122).addBox(-1.4F, 0F, -1.4F, 2.8F, 4.9F, 2.8F, new CubeDeformation(0F))
			.texOffs(70, 160).addBox(-0.4F, 1.35F, 1.1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(42, 146).addBox(1.2F, 0F, -0.4F, 8.415F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_5_3 = part_arm_5_2.addOrReplaceChild("arm_5_3", CubeListBuilder.create()
			.texOffs(130, 122).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F))
			.texOffs(77, 160).addBox(-0.4F, 1.35F, 0.9F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(108, 139).addBox(1F, 0F, -0.4F, 11.529F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_5_4 = part_arm_5_3.addOrReplaceChild("arm_5_4", CubeListBuilder.create()
			.texOffs(135, 131).addBox(-1F, 0F, -1F, 2F, 4.4F, 2F, new CubeDeformation(0F))
			.texOffs(84, 160).addBox(-0.4F, 1.2F, 0.7F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_5_5 = part_arm_5_4.addOrReplaceChild("arm_5_5", CubeListBuilder.create()
			.texOffs(186, 146).addBox(-0.8F, 0F, -0.8F, 1.6F, 3.9F, 1.6F, new CubeDeformation(0F))
			.texOffs(153, 153).addBox(-0.8F, 3.5F, -0.8F, 1.6F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_6 = part_head.addOrReplaceChild("arm_6", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-4.6194F, 6F, 1.9134F, 0.9076F, 5.1051F, 0F));
		PartDefinition part_arm_6_0 = part_arm_6.addOrReplaceChild("arm_6_0", CubeListBuilder.create()
			.texOffs(136, 111).addBox(-1.8F, 0F, -1.8F, 3.6F, 5.4F, 3.6F, new CubeDeformation(0F))
			.texOffs(204, 131).addBox(1.6F, 0F, -0.4F, 1.7346F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_6_1 = part_arm_6_0.addOrReplaceChild("arm_6_1", CubeListBuilder.create()
			.texOffs(153, 111).addBox(-1.6F, 0F, -1.6F, 3.2F, 5.4F, 3.2F, new CubeDeformation(0F))
			.texOffs(91, 160).addBox(-0.4F, 1.5F, 1.3F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(60, 131).addBox(1.4F, 0F, -0.4F, 5.1502F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_arm_6_2 = part_arm_6_1.addOrReplaceChild("arm_6_2", CubeListBuilder.create()
			.texOffs(143, 122).addBox(-1.4F, 0F, -1.4F, 2.8F, 4.9F, 2.8F, new CubeDeformation(0F))
			.texOffs(98, 160).addBox(-0.4F, 1.35F, 1.1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(63, 146).addBox(1.2F, 0F, -0.4F, 8.415F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_6_3 = part_arm_6_2.addOrReplaceChild("arm_6_3", CubeListBuilder.create()
			.texOffs(156, 122).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F))
			.texOffs(105, 160).addBox(-0.4F, 1.35F, 0.9F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(135, 139).addBox(1F, 0F, -0.4F, 11.529F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_6_4 = part_arm_6_3.addOrReplaceChild("arm_6_4", CubeListBuilder.create()
			.texOffs(144, 131).addBox(-1F, 0F, -1F, 2F, 4.4F, 2F, new CubeDeformation(0F))
			.texOffs(112, 160).addBox(-0.4F, 1.2F, 0.7F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_6_5 = part_arm_6_4.addOrReplaceChild("arm_6_5", CubeListBuilder.create()
			.texOffs(195, 146).addBox(-0.8F, 0F, -0.8F, 1.6F, 3.9F, 1.6F, new CubeDeformation(0F))
			.texOffs(162, 153).addBox(-0.8F, 3.5F, -0.8F, 1.6F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_arm_7 = part_head.addOrReplaceChild("arm_7", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-1.9134F, 6F, 4.6194F, 0.9076F, 5.8905F, 0F));
		PartDefinition part_arm_7_0 = part_arm_7.addOrReplaceChild("arm_7_0", CubeListBuilder.create()
			.texOffs(170, 111).addBox(-1.8F, 0F, -1.8F, 3.6F, 5.4F, 3.6F, new CubeDeformation(0F))
			.texOffs(211, 131).addBox(1.6F, 0F, -0.4F, 1.7346F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_arm_7_1 = part_arm_7_0.addOrReplaceChild("arm_7_1", CubeListBuilder.create()
			.texOffs(187, 111).addBox(-1.6F, 0F, -1.6F, 3.2F, 5.4F, 3.2F, new CubeDeformation(0F))
			.texOffs(119, 160).addBox(-0.4F, 1.5F, 1.3F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(75, 131).addBox(1.4F, 0F, -0.4F, 5.1502F, 5.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_arm_7_2 = part_arm_7_1.addOrReplaceChild("arm_7_2", CubeListBuilder.create()
			.texOffs(169, 122).addBox(-1.4F, 0F, -1.4F, 2.8F, 4.9F, 2.8F, new CubeDeformation(0F))
			.texOffs(126, 160).addBox(-0.4F, 1.35F, 1.1F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(84, 146).addBox(1.2F, 0F, -0.4F, 8.415F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 5F, 0F, 0.0873F, 0F, 0F));
		PartDefinition part_arm_7_3 = part_arm_7_2.addOrReplaceChild("arm_7_3", CubeListBuilder.create()
			.texOffs(182, 122).addBox(-1.2F, 0F, -1.2F, 2.4F, 4.9F, 2.4F, new CubeDeformation(0F))
			.texOffs(133, 160).addBox(-0.4F, 1.35F, 0.9F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F))
			.texOffs(162, 139).addBox(1F, 0F, -0.4F, 11.529F, 4.9F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1222F, 0F, 0F));
		PartDefinition part_arm_7_4 = part_arm_7_3.addOrReplaceChild("arm_7_4", CubeListBuilder.create()
			.texOffs(153, 131).addBox(-1F, 0F, -1F, 2F, 4.4F, 2F, new CubeDeformation(0F))
			.texOffs(140, 160).addBox(-0.4F, 1.2F, 0.7F, 0.8F, 1.2F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4.5F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_arm_7_5 = part_arm_7_4.addOrReplaceChild("arm_7_5", CubeListBuilder.create()
			.texOffs(204, 146).addBox(-0.8F, 0F, -0.8F, 1.6F, 3.9F, 1.6F, new CubeDeformation(0F))
			.texOffs(171, 153).addBox(-0.8F, 3.5F, -0.8F, 1.6F, 1.6F, 1.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2443F, 0F, 0F));
		PartDefinition part_feeder_l = part_head.addOrReplaceChild("feeder_l", CubeListBuilder.create(),
			PartPose.offsetAndRotation(-2.5F, 6F, -1.5F, 0.4887F, 3.4558F, 0F));
		PartDefinition part_feeder_l_0 = part_feeder_l.addOrReplaceChild("feeder_l_0", CubeListBuilder.create()
			.texOffs(213, 146).addBox(-0.5F, 0F, -0.5F, 1F, 4.4F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_feeder_l_1 = part_feeder_l_0.addOrReplaceChild("feeder_l_1", CubeListBuilder.create()
			.texOffs(218, 146).addBox(-0.475F, 0F, -0.475F, 0.95F, 4.4F, 0.95F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_l_2 = part_feeder_l_1.addOrReplaceChild("feeder_l_2", CubeListBuilder.create()
			.texOffs(223, 146).addBox(-0.45F, 0F, -0.45F, 0.9F, 4.4F, 0.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_l_3 = part_feeder_l_2.addOrReplaceChild("feeder_l_3", CubeListBuilder.create()
			.texOffs(228, 146).addBox(-0.425F, 0F, -0.425F, 0.85F, 4.4F, 0.85F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_l_4 = part_feeder_l_3.addOrReplaceChild("feeder_l_4", CubeListBuilder.create()
			.texOffs(233, 146).addBox(-0.4F, 0F, -0.4F, 0.8F, 4.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_l_5 = part_feeder_l_4.addOrReplaceChild("feeder_l_5", CubeListBuilder.create()
			.texOffs(238, 146).addBox(-0.375F, 0F, -0.375F, 0.75F, 4.4F, 0.75F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_l_6 = part_feeder_l_5.addOrReplaceChild("feeder_l_6", CubeListBuilder.create()
			.texOffs(243, 146).addBox(-0.35F, 0F, -0.35F, 0.7F, 4.4F, 0.7F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_feeder_l_7 = part_feeder_l_6.addOrReplaceChild("feeder_l_7", CubeListBuilder.create()
			.texOffs(248, 146).addBox(-0.325F, 0F, -0.325F, 0.65F, 4.4F, 0.65F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_feeder_l_club = part_feeder_l_7.addOrReplaceChild("feeder_l_club", CubeListBuilder.create()
			.texOffs(72, 153).addBox(-0.8F, 0F, -0.56F, 1.6F, 3F, 1.12F, new CubeDeformation(0F))
			.texOffs(147, 160).addBox(-0.48F, 3F, -0.4F, 0.96F, 1.6F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2094F, 0F, 0F));
		PartDefinition part_feeder_r = part_head.addOrReplaceChild("feeder_r", CubeListBuilder.create(),
			PartPose.offsetAndRotation(2.5F, 6F, -1.5F, 0.4887F, 2.8274F, 0F));
		PartDefinition part_feeder_r_0 = part_feeder_r.addOrReplaceChild("feeder_r_0", CubeListBuilder.create()
			.texOffs(0, 153).addBox(-0.5F, 0F, -0.5F, 1F, 4.4F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_feeder_r_1 = part_feeder_r_0.addOrReplaceChild("feeder_r_1", CubeListBuilder.create()
			.texOffs(5, 153).addBox(-0.475F, 0F, -0.475F, 0.95F, 4.4F, 0.95F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_r_2 = part_feeder_r_1.addOrReplaceChild("feeder_r_2", CubeListBuilder.create()
			.texOffs(10, 153).addBox(-0.45F, 0F, -0.45F, 0.9F, 4.4F, 0.9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_r_3 = part_feeder_r_2.addOrReplaceChild("feeder_r_3", CubeListBuilder.create()
			.texOffs(15, 153).addBox(-0.425F, 0F, -0.425F, 0.85F, 4.4F, 0.85F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_feeder_r_4 = part_feeder_r_3.addOrReplaceChild("feeder_r_4", CubeListBuilder.create()
			.texOffs(20, 153).addBox(-0.4F, 0F, -0.4F, 0.8F, 4.4F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_r_5 = part_feeder_r_4.addOrReplaceChild("feeder_r_5", CubeListBuilder.create()
			.texOffs(25, 153).addBox(-0.375F, 0F, -0.375F, 0.75F, 4.4F, 0.75F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_feeder_r_6 = part_feeder_r_5.addOrReplaceChild("feeder_r_6", CubeListBuilder.create()
			.texOffs(30, 153).addBox(-0.35F, 0F, -0.35F, 0.7F, 4.4F, 0.7F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_feeder_r_7 = part_feeder_r_6.addOrReplaceChild("feeder_r_7", CubeListBuilder.create()
			.texOffs(35, 153).addBox(-0.325F, 0F, -0.325F, 0.65F, 4.4F, 0.65F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_feeder_r_club = part_feeder_r_7.addOrReplaceChild("feeder_r_club", CubeListBuilder.create()
			.texOffs(81, 153).addBox(-0.8F, 0F, -0.56F, 1.6F, 3F, 1.12F, new CubeDeformation(0F))
			.texOffs(152, 160).addBox(-0.48F, 3F, -0.4F, 0.96F, 1.6F, 0.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 4F, 0F, 0.2094F, 0F, 0F));
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
