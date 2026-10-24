package com.aquamarine.client.entity.kraken;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.boss.KrakenView;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** Faceted deep-sea Kraken: armored mantle, luminous eyes, gills and eight articulated arms. */
public final class KrakenModel extends EntityModel<KrakenRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("kraken"), "main");

	private final ModelPart root;
	private final ModelPart mantle;
	private final ModelPart head;
	private final ModelPart[] arms = new ModelPart[8];
	private final ModelPart[] eyes = new ModelPart[2];
	private final ModelPart[] cracks = new ModelPart[3];
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

	public KrakenModel(ModelPart root) {
		super(root);
		this.root = root;
		this.mantle = root.getChild("mantle");
		this.head = root.getChild("head");
		this.eyes[0] = this.head.getChild("eye_glow_left");
		this.eyes[1] = this.head.getChild("eye_glow_right");
		this.cracks[0] = this.mantle.getChild("crack_75");
		this.cracks[1] = this.mantle.getChild("crack_50");
		this.cracks[2] = this.mantle.getChild("crack_25");
		this.idle = KrakenAnimations.IDLE.bake(root);
		this.swim = KrakenAnimations.SWIM.bake(root);
		this.emerge = KrakenAnimations.EMERGE.bake(root);
		this.stunned = KrakenAnimations.STUNNED.bake(root);
		this.phaseChange = KrakenAnimations.PHASE_CHANGE.bake(root);
		this.death = KrakenAnimations.DEATH.bake(root);
		this.slam = KrakenAnimations.SLAM.bake(root);
		this.grab = KrakenAnimations.GRAB.bake(root);
		this.ink = KrakenAnimations.INK.bake(root);
		this.whirlpool = KrakenAnimations.WHIRLPOOL.bake(root);
		this.spawnYoung = KrakenAnimations.SPAWN_YOUNG.bake(root);
		this.waterJet = KrakenAnimations.WATER_JET.bake(root);
		this.roar = KrakenAnimations.ROAR.bake(root);
		this.bolts = KrakenAnimations.BOLTS.bake(root);
		this.ambush = KrakenAnimations.AMBUSH.bake(root);
		this.doubleGrab = KrakenAnimations.DOUBLE_GRAB.bake(root);
		for (int i = 0; i < this.arms.length; i++) {
			this.arms[i] = root.getChild("arm_" + i);
		}
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		CubeDeformation none = CubeDeformation.NONE;

		PartDefinition mantle = root.addOrReplaceChild("mantle", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-10.0F, -5.0F, -12.0F, 20.0F, 8.0F, 24.0F, none)
			.texOffs(0, 34).addBox(-8.0F, -9.0F, -9.0F, 16.0F, 5.0F, 18.0F, none)
			.texOffs(48, 34).addBox(-5.0F, -12.0F, -6.0F, 10.0F, 4.0F, 12.0F, none),
			PartPose.offset(0.0F, -24.0F, -1.0F));
		mantle.addOrReplaceChild("mantle_ridge_left", CubeListBuilder.create().texOffs(80, 0)
			.addBox(-1.0F, -3.0F, -10.0F, 2.0F, 2.0F, 20.0F, none), PartPose.offset(-7.0F, -3.0F, 0.0F));
		mantle.addOrReplaceChild("mantle_ridge_right", CubeListBuilder.create().texOffs(80, 22)
			.addBox(-1.0F, -3.0F, -10.0F, 2.0F, 2.0F, 20.0F, none), PartPose.offset(7.0F, -3.0F, 0.0F));
		// Rachaduras: cascas um pouco infladas sobre coroa, elevação e placa do manto. A textura é
		// transparente fora das fendas, e a KrakenRenderer as desenha de novo com brilho próprio.
		CubeDeformation shell = new CubeDeformation(0.12F);
		mantle.addOrReplaceChild("crack_75", CubeListBuilder.create().texOffs(0, 128)
			.addBox(-5.0F, -12.0F, -6.0F, 10.0F, 4.0F, 12.0F, shell), PartPose.ZERO);
		mantle.addOrReplaceChild("crack_50", CubeListBuilder.create().texOffs(48, 128)
			.addBox(-8.0F, -9.0F, -9.0F, 16.0F, 5.0F, 18.0F, shell), PartPose.ZERO);
		mantle.addOrReplaceChild("crack_25", CubeListBuilder.create().texOffs(0, 152)
			.addBox(-10.0F, -5.0F, -12.0F, 20.0F, 8.0F, 24.0F, shell), PartPose.ZERO);

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(0, 60).addBox(-7.0F, -7.0F, -8.0F, 14.0F, 13.0F, 16.0F, none)
			.texOffs(60, 60).addBox(-5.0F, 2.0F, 7.0F, 10.0F, 3.0F, 5.0F, none),
			PartPose.offset(0.0F, -15.0F, 1.0F));
		for (int side : new int[] {-1, 1}) {
			String suffix = side < 0 ? "left" : "right";
			head.addOrReplaceChild("eye_socket_" + suffix, CubeListBuilder.create().texOffs(80, 48)
				.addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 2.0F, none),
				PartPose.offset(side * 4.2F, -1.0F, 7.2F));
			head.addOrReplaceChild("eye_glow_" + suffix, CubeListBuilder.create().texOffs(96, 48)
				.addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 1.0F, none),
				PartPose.offset(side * 4.2F, -1.0F, 9.0F));
			for (int gill = 0; gill < 4; gill++) {
				head.addOrReplaceChild("gill_" + suffix + "_" + gill, CubeListBuilder.create().texOffs(104, 48)
					.addBox(0.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F, none),
					PartPose.offset(side * (5.0F + gill * 0.35F), 2.0F, -4.0F + gill * 2.0F));
			}
		}
		head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(112, 56)
			.addBox(-2.0F, 0.0F, 0.0F, 4.0F, 3.0F, 4.0F, none), PartPose.offset(0.0F, 4.0F, 10.0F));

		for (int i = 0; i < 8; i++) {
			float angle = (float) (Math.PI * 2.0 * i / 8.0);
			float x = (float) Math.sin(angle) * 8.0F;
			float z = (float) Math.cos(angle) * 7.0F;
			float yaw = angle;
			float initialDroop = i % 2 == 0 ? -0.18F : -0.34F;
			PartDefinition arm = root.addOrReplaceChild("arm_" + i, CubeListBuilder.create()
				.texOffs(0, 92).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 4.0F, 5.0F, none),
			PartPose.offsetAndRotation(x, -10.0F, z, initialDroop, yaw, 0.0F));
			PartDefinition segment = arm;
			for (int joint = 0; joint < 5; joint++) {
				String name = "arm_" + i + "_segment_" + joint;
				float taper = Math.max(1.4F, 3.3F - joint * 0.38F);
				CubeListBuilder armCubes = CubeListBuilder.create().texOffs(24 + (joint % 3) * 24, 92)
					.addBox(-taper / 2.0F, -taper / 2.0F, 0.0F, taper, taper, 7.0F, none);
				segment = segment.addOrReplaceChild(name, armCubes,
					PartPose.offsetAndRotation(0.0F, 0.0F, joint == 0 ? 0.0F : 6.0F,
						joint == 0 ? 0.0F : -0.12F, 0.0F, 0.0F));
				for (int sucker = 0; sucker < 3; sucker++) {
					segment.addOrReplaceChild("sucker_" + sucker, CubeListBuilder.create()
						.texOffs(96, 92).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, none),
						PartPose.offset(0.0F, taper / 2.0F, 1.0F + sucker * 2.0F));
				}
			}
		}
		return LayerDefinition.create(mesh, 128, 256);
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
		float shield = Math.max(0.0F, Math.min(1.0F, state.shieldFraction));
		this.cracks[0].visible = shield <= 0.75F;
		this.cracks[1].visible = shield <= 0.50F;
		this.cracks[2].visible = shield <= 0.25F;
		float glow = 0.8F + (1.0F - shield) * 0.2F + Math.max(0, state.phase - 1) * 0.1F;
		for (ModelPart eye : this.eyes) {
			eye.xScale = glow;
			eye.yScale = glow;
			eye.zScale = glow;
		}
	}
}
