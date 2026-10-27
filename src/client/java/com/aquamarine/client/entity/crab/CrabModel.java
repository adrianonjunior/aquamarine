package com.aquamarine.client.entity.crab;

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
 * Caranguejo de praia: casco coral, oito pernas articuladas, pinça assimétrica e concha marfim na garra.
 * GERADO por tools/models/crab/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class CrabModel extends EntityModel<CrabRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("crab"), "main");
	private final KeyframeAnimation idle_eyes;
	private final KeyframeAnimation walk_side;
	private final KeyframeAnimation flee;
	private final KeyframeAnimation claw_wave;
	private final KeyframeAnimation burrow;
	private final KeyframeAnimation emerge;
	private final KeyframeAnimation pinch;
	private final KeyframeAnimation sleep;

	public CrabModel(ModelPart root) {
		super(root);
		this.idle_eyes = CrabAnimations.IDLE_EYES.bake(root);
		this.walk_side = CrabAnimations.WALK_SIDE.bake(root);
		this.flee = CrabAnimations.FLEE.bake(root);
		this.claw_wave = CrabAnimations.CLAW_WAVE.bake(root);
		this.burrow = CrabAnimations.BURROW.bake(root);
		this.emerge = CrabAnimations.EMERGE.bake(root);
		this.pinch = CrabAnimations.PINCH.bake(root);
		this.sleep = CrabAnimations.SLEEP.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition part_body = root.addOrReplaceChild("body", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-4.5F, -2F, -3F, 9F, 3F, 6F, new CubeDeformation(0F))
			.texOffs(31, 0).addBox(-3.5F, -3F, -2.5F, 7F, 1F, 5F, new CubeDeformation(0F))
			.texOffs(0, 10).addBox(-3.5F, 1F, -2.5F, 7F, 1F, 5F, new CubeDeformation(0F))
			.texOffs(11, 17).addBox(-5F, -1F, -2F, 1F, 1F, 4F, new CubeDeformation(0F))
			.texOffs(22, 17).addBox(4F, -1F, -2F, 1F, 1F, 4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 20F, 0F, 0F, 0F, 0F));
		PartDefinition part_mouth = part_body.addOrReplaceChild("mouth", CubeListBuilder.create()
			.texOffs(18, 38).addBox(-1F, -0.2F, -0.5F, 2F, 1F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, -3F, 0F, 0F, 0F));
		PartDefinition part_eye_l = part_body.addOrReplaceChild("eye_l", CubeListBuilder.create()
			.texOffs(0, 34).addBox(-0.4F, -2F, -0.4F, 0.8F, 2F, 0.8F, new CubeDeformation(0F))
			.texOffs(33, 29).addBox(-0.75F, -3F, -0.7F, 1.5F, 1.3F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-2F, -2F, -2.5F, 0F, 0F, -0.2094F));
		PartDefinition part_leg_l0 = part_body.addOrReplaceChild("leg_l0", CubeListBuilder.create()
			.texOffs(51, 17).addBox(-3F, -0.55F, -0.55F, 3F, 1.1F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-4F, 0.25F, -2F, 0F, -0.4363F, 0.2793F));
		PartDefinition part_leg_l0_tip = part_leg_l0.addOrReplaceChild("leg_l0_tip", CubeListBuilder.create()
			.texOffs(10, 34).addBox(-2.8F, -0.4F, -0.45F, 2.8F, 0.8F, 0.9F, new CubeDeformation(0F))
			.texOffs(32, 38).addBox(-3.2F, -0.25F, -0.3F, 0.6F, 0.5F, 0.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-2.7F, 0F, 0F, 0F, 0F, -0.8378F));
		PartDefinition part_leg_l1 = part_body.addOrReplaceChild("leg_l1", CubeListBuilder.create()
			.texOffs(0, 24).addBox(-3F, -0.55F, -0.55F, 3F, 1.1F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-4F, 0.25F, -0.7F, 0F, -0.1571F, 0.2793F));
		PartDefinition part_leg_l1_tip = part_leg_l1.addOrReplaceChild("leg_l1_tip", CubeListBuilder.create()
			.texOffs(19, 34).addBox(-2.8F, -0.4F, -0.45F, 2.8F, 0.8F, 0.9F, new CubeDeformation(0F))
			.texOffs(37, 38).addBox(-3.2F, -0.25F, -0.3F, 0.6F, 0.5F, 0.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-2.7F, 0F, 0F, 0F, 0F, -0.8378F));
		PartDefinition part_leg_l2 = part_body.addOrReplaceChild("leg_l2", CubeListBuilder.create()
			.texOffs(11, 24).addBox(-3F, -0.55F, -0.55F, 3F, 1.1F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-4F, 0.25F, 0.6F, 0F, 0.1222F, 0.2793F));
		PartDefinition part_leg_l2_tip = part_leg_l2.addOrReplaceChild("leg_l2_tip", CubeListBuilder.create()
			.texOffs(28, 34).addBox(-2.8F, -0.4F, -0.45F, 2.8F, 0.8F, 0.9F, new CubeDeformation(0F))
			.texOffs(42, 38).addBox(-3.2F, -0.25F, -0.3F, 0.6F, 0.5F, 0.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-2.7F, 0F, 0F, 0F, 0F, -0.8378F));
		PartDefinition part_leg_l3 = part_body.addOrReplaceChild("leg_l3", CubeListBuilder.create()
			.texOffs(22, 24).addBox(-3F, -0.55F, -0.55F, 3F, 1.1F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-4F, 0.25F, 1.9F, 0F, 0.4014F, 0.2793F));
		PartDefinition part_leg_l3_tip = part_leg_l3.addOrReplaceChild("leg_l3_tip", CubeListBuilder.create()
			.texOffs(37, 34).addBox(-2.8F, -0.4F, -0.45F, 2.8F, 0.8F, 0.9F, new CubeDeformation(0F))
			.texOffs(47, 38).addBox(-3.2F, -0.25F, -0.3F, 0.6F, 0.5F, 0.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-2.7F, 0F, 0F, 0F, 0F, -0.8378F));
		PartDefinition part_arm_l = part_body.addOrReplaceChild("arm_l", CubeListBuilder.create()
			.texOffs(25, 10).addBox(-0.6F, -0.5F, -3F, 1.2F, 1.1F, 3.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-3.4F, 0F, -2F, 0F, 0.4363F, -0.2094F));
		PartDefinition part_claw_l = part_arm_l.addOrReplaceChild("claw_l", CubeListBuilder.create()
			.texOffs(0, 17).addBox(-1F, -1.4F, -2.7F, 2F, 2.5F, 3F, new CubeDeformation(0F))
			.texOffs(51, 29).addBox(-1F, -0.9F, -4F, 0.9F, 1.7F, 1.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, -3F, 0F, -0.2793F, 0F));
		PartDefinition part_finger_l = part_claw_l.addOrReplaceChild("finger_l", CubeListBuilder.create()
			.texOffs(33, 17).addBox(-0.8F, -0.9F, -2.2F, 0.9F, 1.7F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(1F, 0F, -1.8F, 0F, -0.3491F, 0F));
		PartDefinition part_held_shell = part_claw_l.addOrReplaceChild("held_shell", CubeListBuilder.create()
			.texOffs(22, 29).addBox(-1.1F, -1.6F, -0.7F, 2.2F, 2F, 1.4F, new CubeDeformation(0F))
			.texOffs(25, 38).addBox(-0.65F, -2F, -0.5F, 1.3F, 1F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -0.5F, -3.5F, 0F, 0F, -0.3142F));
		PartDefinition part_eye_r = part_body.addOrReplaceChild("eye_r", CubeListBuilder.create()
			.texOffs(5, 34).addBox(-0.4F, -2F, -0.4F, 0.8F, 2F, 0.8F, new CubeDeformation(0F))
			.texOffs(42, 29).addBox(-0.75F, -3F, -0.7F, 1.5F, 1.3F, 1.4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(2F, -2F, -2.5F, 0F, 0F, 0.2094F));
		PartDefinition part_leg_r0 = part_body.addOrReplaceChild("leg_r0", CubeListBuilder.create()
			.texOffs(33, 24).addBox(0F, -0.55F, -0.55F, 3F, 1.1F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(4F, 0.25F, -2F, 0F, 0.4363F, -0.2793F));
		PartDefinition part_leg_r0_tip = part_leg_r0.addOrReplaceChild("leg_r0_tip", CubeListBuilder.create()
			.texOffs(46, 34).addBox(0F, -0.4F, -0.45F, 2.8F, 0.8F, 0.9F, new CubeDeformation(0F))
			.texOffs(52, 38).addBox(2.6F, -0.25F, -0.3F, 0.6F, 0.5F, 0.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(2.7F, 0F, 0F, 0F, 0F, 0.8378F));
		PartDefinition part_leg_r1 = part_body.addOrReplaceChild("leg_r1", CubeListBuilder.create()
			.texOffs(44, 24).addBox(0F, -0.55F, -0.55F, 3F, 1.1F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(4F, 0.25F, -0.7F, 0F, 0.1571F, -0.2793F));
		PartDefinition part_leg_r1_tip = part_leg_r1.addOrReplaceChild("leg_r1_tip", CubeListBuilder.create()
			.texOffs(55, 34).addBox(0F, -0.4F, -0.45F, 2.8F, 0.8F, 0.9F, new CubeDeformation(0F))
			.texOffs(57, 38).addBox(2.6F, -0.25F, -0.3F, 0.6F, 0.5F, 0.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(2.7F, 0F, 0F, 0F, 0F, 0.8378F));
		PartDefinition part_leg_r2 = part_body.addOrReplaceChild("leg_r2", CubeListBuilder.create()
			.texOffs(0, 29).addBox(0F, -0.55F, -0.55F, 3F, 1.1F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(4F, 0.25F, 0.6F, 0F, -0.1222F, -0.2793F));
		PartDefinition part_leg_r2_tip = part_leg_r2.addOrReplaceChild("leg_r2_tip", CubeListBuilder.create()
			.texOffs(0, 38).addBox(0F, -0.4F, -0.45F, 2.8F, 0.8F, 0.9F, new CubeDeformation(0F))
			.texOffs(0, 41).addBox(2.6F, -0.25F, -0.3F, 0.6F, 0.5F, 0.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(2.7F, 0F, 0F, 0F, 0F, 0.8378F));
		PartDefinition part_leg_r3 = part_body.addOrReplaceChild("leg_r3", CubeListBuilder.create()
			.texOffs(11, 29).addBox(0F, -0.55F, -0.55F, 3F, 1.1F, 1.1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(4F, 0.25F, 1.9F, 0F, -0.4014F, -0.2793F));
		PartDefinition part_leg_r3_tip = part_leg_r3.addOrReplaceChild("leg_r3_tip", CubeListBuilder.create()
			.texOffs(9, 38).addBox(0F, -0.4F, -0.45F, 2.8F, 0.8F, 0.9F, new CubeDeformation(0F))
			.texOffs(5, 41).addBox(2.6F, -0.25F, -0.3F, 0.6F, 0.5F, 0.6F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(2.7F, 0F, 0F, 0F, 0F, 0.8378F));
		PartDefinition part_arm_r = part_body.addOrReplaceChild("arm_r", CubeListBuilder.create()
			.texOffs(38, 10).addBox(-0.6F, -0.5F, -3F, 1.2F, 1.1F, 3.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(3.4F, 0F, -2F, 0F, -0.4363F, 0.2094F));
		PartDefinition part_claw_r = part_arm_r.addOrReplaceChild("claw_r", CubeListBuilder.create()
			.texOffs(51, 10).addBox(-1.3F, -1.4F, -2.7F, 2.6F, 2.5F, 3F, new CubeDeformation(0F))
			.texOffs(58, 29).addBox(-1.3F, -0.9F, -4F, 0.9F, 1.7F, 1.8F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, -3F, 0F, 0.2793F, 0F));
		PartDefinition part_finger_r = part_claw_r.addOrReplaceChild("finger_r", CubeListBuilder.create()
			.texOffs(42, 17).addBox(-0.8F, -0.9F, -2.2F, 0.9F, 1.7F, 2.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(1.3F, 0F, -1.8F, 0F, -0.3491F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void setupAnim(CrabRenderState state) {
		this.resetPose();
		this.idle_eyes.apply(state.idle_eyes, state.ageInTicks);
		this.walk_side.apply(state.walk_side, state.ageInTicks);
		this.flee.apply(state.flee, state.ageInTicks);
		this.claw_wave.apply(state.claw_wave, state.ageInTicks);
		this.burrow.apply(state.burrow, state.ageInTicks);
		this.emerge.apply(state.emerge, state.ageInTicks);
		this.pinch.apply(state.pinch, state.ageInTicks);
		this.sleep.apply(state.sleep, state.ageInTicks);
	}
}
