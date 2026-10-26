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

/**
 * Tentáculo de ataque do Kraken: um braço de ~8 blocos que sai da água fazendo um S, de seção arredondada, com ventosas em zigue-zague que brilham na parte de dentro, espinhos nas costas e ponta em gancho.
 * GERADO por tools/models/spawnlings/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class TentacleModel extends EntityModel<TentacleRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("tentacle"), "main");
	private final KeyframeAnimation idle;
	private final KeyframeAnimation rise;
	private final KeyframeAnimation slam;
	private final KeyframeAnimation grab;
	private final KeyframeAnimation release;

	public TentacleModel(ModelPart root) {
		super(root);
		this.idle = TentacleAnimations.IDLE.bake(root);
		this.rise = TentacleAnimations.RISE.bake(root);
		this.slam = TentacleAnimations.SLAM.bake(root);
		this.grab = TentacleAnimations.GRAB.bake(root);
		this.release = TentacleAnimations.RELEASE.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition part_base = root.addOrReplaceChild("base", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
		PartDefinition part_seg_0 = part_base.addOrReplaceChild("seg_0", CubeListBuilder.create()
			.texOffs(61, 35).addBox(-11F, -12F, -7.04F, 22F, 12F, 14.08F, new CubeDeformation(0F))
			.texOffs(0, 0).addBox(-7.04F, -12F, -11F, 14.08F, 12F, 22F, new CubeDeformation(0F))
			.texOffs(206, 135).addBox(-5.02F, -5.02F, -12F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(215, 135).addBox(2.02F, -9.64F, -12F, 3F, 3F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_seg_1 = part_seg_0.addOrReplaceChild("seg_1", CubeListBuilder.create()
			.texOffs(0, 65).addBox(-10.2F, -11F, -6.528F, 20.4F, 11F, 13.056F, new CubeDeformation(0F))
			.texOffs(75, 0).addBox(-6.528F, -11F, -10.2F, 13.056F, 11F, 20.4F, new CubeDeformation(0F))
			.texOffs(224, 135).addBox(-4.764F, -8.9F, -11.2F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(233, 135).addBox(1.764F, -4.7F, -11.2F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(107, 135).addBox(-0.5F, -8.5F, 9.2F, 1F, 5.5F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -11F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_seg_2 = part_seg_1.addOrReplaceChild("seg_2", CubeListBuilder.create()
			.texOffs(122, 65).addBox(-9.5F, -11F, -6.08F, 19F, 11F, 12.16F, new CubeDeformation(0F))
			.texOffs(146, 0).addBox(-6.08F, -11F, -9.5F, 12.16F, 11F, 19F, new CubeDeformation(0F))
			.texOffs(242, 135).addBox(-4.54F, -4.7F, -10.5F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(0, 150).addBox(1.54F, -8.9F, -10.5F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(116, 135).addBox(-0.5F, -8.5F, 8.5F, 1F, 5.5F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -10F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_seg_3 = part_seg_2.addOrReplaceChild("seg_3", CubeListBuilder.create()
			.texOffs(0, 91).addBox(-8.8F, -11F, -5.632F, 17.6F, 11F, 11.264F, new CubeDeformation(0F))
			.texOffs(0, 35).addBox(-5.632F, -11F, -8.8F, 11.264F, 11F, 17.6F, new CubeDeformation(0F))
			.texOffs(9, 150).addBox(-4.316F, -8.9F, -9.8F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(18, 150).addBox(1.316F, -4.7F, -9.8F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(125, 135).addBox(-0.5F, -8.5F, 7.8F, 1F, 5.5F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -10F, 0F, 0.0698F, 0F, 0F));
		PartDefinition part_seg_4 = part_seg_3.addOrReplaceChild("seg_4", CubeListBuilder.create()
			.texOffs(102, 91).addBox(-8.1F, -10F, -5.184F, 16.2F, 10F, 10.368F, new CubeDeformation(0F))
			.texOffs(136, 35).addBox(-5.184F, -10F, -8.1F, 10.368F, 10F, 16.2F, new CubeDeformation(0F))
			.texOffs(27, 150).addBox(-4.092F, -4.38F, -9.1F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(36, 150).addBox(1.092F, -8.16F, -9.1F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(134, 135).addBox(-0.5F, -7.65F, 7.1F, 1F, 4.95F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -10F, 0F, 0.0349F, 0F, 0F));
		PartDefinition part_seg_5 = part_seg_4.addOrReplaceChild("seg_5", CubeListBuilder.create()
			.texOffs(159, 91).addBox(-7.4F, -10F, -4.736F, 14.8F, 10F, 9.472F, new CubeDeformation(0F))
			.texOffs(71, 65).addBox(-4.736F, -10F, -7.4F, 9.472F, 10F, 14.8F, new CubeDeformation(0F))
			.texOffs(45, 150).addBox(-3.868F, -8.16F, -8.4F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(54, 150).addBox(0.868F, -4.38F, -8.4F, 3F, 3F, 1F, new CubeDeformation(0F))
			.texOffs(143, 135).addBox(-0.5F, -7.65F, 6.4F, 1F, 4.95F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -9F, 0F, -0.0349F, 0F, 0F));
		PartDefinition part_seg_6 = part_seg_5.addOrReplaceChild("seg_6", CubeListBuilder.create()
			.texOffs(0, 115).addBox(-6.7F, -10F, -4.288F, 13.4F, 10F, 8.576F, new CubeDeformation(0F))
			.texOffs(187, 65).addBox(-4.288F, -10F, -6.7F, 8.576F, 10F, 13.4F, new CubeDeformation(0F))
			.texOffs(63, 150).addBox(-3.144F, -3.88F, -7.7F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(70, 150).addBox(1.144F, -7.66F, -7.7F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(152, 135).addBox(-0.5F, -7.65F, 5.7F, 1F, 4.95F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -9F, 0F, -0.0698F, 0F, 0F));
		PartDefinition part_seg_7 = part_seg_6.addOrReplaceChild("seg_7", CubeListBuilder.create()
			.texOffs(80, 115).addBox(-6F, -10F, -3.84F, 12F, 10F, 7.68F, new CubeDeformation(0F))
			.texOffs(61, 91).addBox(-3.84F, -10F, -6F, 7.68F, 10F, 12F, new CubeDeformation(0F))
			.texOffs(77, 150).addBox(-2.92F, -7.66F, -7F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(84, 150).addBox(0.92F, -3.88F, -7F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(161, 135).addBox(-0.5F, -7.65F, 5F, 1F, 4.95F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -9F, 0F, -0.0873F, 0F, 0F));
		PartDefinition part_seg_8 = part_seg_7.addOrReplaceChild("seg_8", CubeListBuilder.create()
			.texOffs(148, 115).addBox(-5.3F, -9F, -3.392F, 10.6F, 9F, 6.784F, new CubeDeformation(0F))
			.texOffs(210, 91).addBox(-3.392F, -9F, -5.3F, 6.784F, 9F, 10.6F, new CubeDeformation(0F))
			.texOffs(91, 150).addBox(-2.696F, -3.56F, -6.3F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(98, 150).addBox(0.696F, -6.92F, -6.3F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(170, 135).addBox(-0.5F, -6.8F, 4.3F, 1F, 4.4F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -9F, 0F, -0.0698F, 0F, 0F));
		PartDefinition part_seg_9 = part_seg_8.addOrReplaceChild("seg_9", CubeListBuilder.create()
			.texOffs(185, 115).addBox(-4.6F, -9F, -2.944F, 9.2F, 9F, 5.888F, new CubeDeformation(0F))
			.texOffs(47, 115).addBox(-2.944F, -9F, -4.6F, 5.888F, 9F, 9.2F, new CubeDeformation(0F))
			.texOffs(105, 150).addBox(-2.472F, -6.92F, -5.6F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(112, 150).addBox(0.472F, -3.56F, -5.6F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(179, 135).addBox(-0.5F, -6.8F, 3.6F, 1F, 4.4F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -8F, 0F, -0.0349F, 0F, 0F));
		PartDefinition part_seg_10 = part_seg_9.addOrReplaceChild("seg_10", CubeListBuilder.create()
			.texOffs(0, 135).addBox(-3.9F, -9F, -2.496F, 7.8F, 9F, 4.992F, new CubeDeformation(0F))
			.texOffs(121, 115).addBox(-2.496F, -9F, -3.9F, 4.992F, 9F, 7.8F, new CubeDeformation(0F))
			.texOffs(119, 150).addBox(-2.248F, -3.56F, -4.9F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(126, 150).addBox(0.248F, -6.92F, -4.9F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(188, 135).addBox(-0.5F, -6.8F, 2.9F, 1F, 4.4F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -8F, 0F, 0.0524F, 0F, 0F));
		PartDefinition part_seg_11 = part_seg_10.addOrReplaceChild("seg_11", CubeListBuilder.create()
			.texOffs(48, 135).addBox(-3.2F, -8F, -2.048F, 6.4F, 8F, 4.096F, new CubeDeformation(0F))
			.texOffs(218, 115).addBox(-2.048F, -8F, -3.2F, 4.096F, 8F, 6.4F, new CubeDeformation(0F))
			.texOffs(133, 150).addBox(-2.024F, -6.18F, -4.2F, 2F, 2F, 1F, new CubeDeformation(0F))
			.texOffs(140, 150).addBox(0.024F, -3.24F, -4.2F, 2F, 2F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -8F, 0F, 0.1047F, 0F, 0F));
		PartDefinition part_tip = part_seg_11.addOrReplaceChild("tip", CubeListBuilder.create()
			.texOffs(73, 135).addBox(-2.6F, -8F, -1.664F, 5.2F, 8F, 3.328F, new CubeDeformation(0F))
			.texOffs(27, 135).addBox(-1.664F, -8F, -2.6F, 3.328F, 8F, 5.2F, new CubeDeformation(0F))
			.texOffs(147, 150).addBox(-1F, -6F, -3.6F, 2F, 2F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -7F, 0F, 0.1745F, 0F, 0F));
		PartDefinition part_hook = part_tip.addOrReplaceChild("hook", CubeListBuilder.create()
			.texOffs(94, 135).addBox(-1.5F, -6F, -1.5F, 3F, 6F, 3F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -8F, 0F, -0.5934F, 0F, 0F));
		PartDefinition part_hook_end = part_hook.addOrReplaceChild("hook_end", CubeListBuilder.create()
			.texOffs(197, 135).addBox(-1F, -4F, -1F, 2F, 4F, 2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -6F, 0F, -0.8029F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}

	@Override
	public void setupAnim(TentacleRenderState state) {
		this.resetPose();
		this.idle.apply(state.idle, state.ageInTicks);
		this.rise.apply(state.rise, state.ageInTicks);
		this.slam.apply(state.slam, state.ageInTicks);
		this.grab.apply(state.grab, state.ageInTicks);
		this.release.apply(state.release, state.ageInTicks);
	}
}
