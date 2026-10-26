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

/**
 * Ovo do Kraken: bolsa alta de membrana translúcida com um filhote enrolado e aceso dentro, raízes que prendem no fundo e quatro pétalas no topo que abrem na eclosão.
 * GERADO por tools/models/spawnlings/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class KrakenEggModel extends EntityModel<KrakenEggRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("kraken_egg"), "main");
	private final KeyframeAnimation pulse;
	private final KeyframeAnimation hatch;

	public KrakenEggModel(ModelPart root) {
		super(root);
		this.pulse = KrakenEggAnimations.PULSE.bake(root);
		this.hatch = KrakenEggAnimations.HATCH.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition part_anchor = root.addOrReplaceChild("anchor", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
		PartDefinition part_root_0 = part_anchor.addOrReplaceChild("root_0", CubeListBuilder.create()
			.texOffs(42, 28).addBox(-0.6F, -1.2F, 0F, 1.2F, 1.2F, 5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(1.7634F, 0F, 2.4271F, -0.1396F, 0.6283F, 0F));
		PartDefinition part_root_0_end = part_root_0.addOrReplaceChild("root_0_end", CubeListBuilder.create()
			.texOffs(117, 28).addBox(-0.4F, -0.9F, 0F, 0.8F, 0.9F, 3.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 5F, -0.2094F, -0.3142F, 0F));
		PartDefinition part_root_1 = part_anchor.addOrReplaceChild("root_1", CubeListBuilder.create()
			.texOffs(57, 28).addBox(-0.6F, -1.2F, 0F, 1.2F, 1.2F, 5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(2.8532F, 0F, -0.9271F, -0.1396F, 1.885F, 0F));
		PartDefinition part_root_1_end = part_root_1.addOrReplaceChild("root_1_end", CubeListBuilder.create()
			.texOffs(0, 36).addBox(-0.4F, -0.9F, 0F, 0.8F, 0.9F, 3.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 5F, -0.2094F, 0.3142F, 0F));
		PartDefinition part_root_2 = part_anchor.addOrReplaceChild("root_2", CubeListBuilder.create()
			.texOffs(72, 28).addBox(-0.6F, -1.2F, 0F, 1.2F, 1.2F, 5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, -3F, -0.1396F, 3.1416F, 0F));
		PartDefinition part_root_2_end = part_root_2.addOrReplaceChild("root_2_end", CubeListBuilder.create()
			.texOffs(11, 36).addBox(-0.4F, -0.9F, 0F, 0.8F, 0.9F, 3.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 5F, -0.2094F, -0.3142F, 0F));
		PartDefinition part_root_3 = part_anchor.addOrReplaceChild("root_3", CubeListBuilder.create()
			.texOffs(87, 28).addBox(-0.6F, -1.2F, 0F, 1.2F, 1.2F, 5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-2.8532F, 0F, -0.9271F, -0.1396F, 4.3982F, 0F));
		PartDefinition part_root_3_end = part_root_3.addOrReplaceChild("root_3_end", CubeListBuilder.create()
			.texOffs(22, 36).addBox(-0.4F, -0.9F, 0F, 0.8F, 0.9F, 3.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 5F, -0.2094F, 0.3142F, 0F));
		PartDefinition part_root_4 = part_anchor.addOrReplaceChild("root_4", CubeListBuilder.create()
			.texOffs(102, 28).addBox(-0.6F, -1.2F, 0F, 1.2F, 1.2F, 5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-1.7634F, 0F, 2.4271F, -0.1396F, 5.6549F, 0F));
		PartDefinition part_root_4_end = part_root_4.addOrReplaceChild("root_4_end", CubeListBuilder.create()
			.texOffs(33, 36).addBox(-0.4F, -0.9F, 0F, 0.8F, 0.9F, 3.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 5F, -0.2094F, -0.3142F, 0F));
		PartDefinition part_embryo = part_anchor.addOrReplaceChild("embryo", CubeListBuilder.create()
			.texOffs(0, 17).addBox(-2.5F, -2.5F, -2.5F, 5F, 5F, 5F, new CubeDeformation(0.2F))
			.texOffs(50, 17).addBox(-2F, -6F, -1F, 4F, 4F, 4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -8.5F, 0F, 0F, 0F, 0F));
		PartDefinition part_embryo_arm_0 = part_embryo.addOrReplaceChild("embryo_arm_0", CubeListBuilder.create()
			.texOffs(44, 36).addBox(-0.6F, 0F, -0.6F, 1.2F, 3F, 1.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(1.1314F, 2.5F, 1.1314F, 0.8727F, 0.7854F, 0F));
		PartDefinition part_embryo_arm_1 = part_embryo.addOrReplaceChild("embryo_arm_1", CubeListBuilder.create()
			.texOffs(53, 36).addBox(-0.6F, 0F, -0.6F, 1.2F, 3F, 1.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(1.1314F, 2.5F, -1.1314F, 0.8727F, 2.3562F, 0F));
		PartDefinition part_embryo_arm_2 = part_embryo.addOrReplaceChild("embryo_arm_2", CubeListBuilder.create()
			.texOffs(62, 36).addBox(-0.6F, 0F, -0.6F, 1.2F, 3F, 1.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-1.1314F, 2.5F, -1.1314F, 0.8727F, 3.927F, 0F));
		PartDefinition part_embryo_arm_3 = part_embryo.addOrReplaceChild("embryo_arm_3", CubeListBuilder.create()
			.texOffs(71, 36).addBox(-0.6F, 0F, -0.6F, 1.2F, 3F, 1.2F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-1.1314F, 2.5F, 1.1314F, 0.8727F, 5.4978F, 0F));
		PartDefinition part_sac = part_anchor.addOrReplaceChild("sac", CubeListBuilder.create()
			.texOffs(21, 17).addBox(-3.5F, -2F, -3.5F, 7F, 2F, 7F, new CubeDeformation(0F))
			.texOffs(45, 0).addBox(-5F, -7F, -5F, 10F, 5F, 10F, new CubeDeformation(0F))
			.texOffs(0, 0).addBox(-5.5F, -12F, -5.5F, 11F, 5F, 11F, new CubeDeformation(0F))
			.texOffs(86, 0).addBox(-4.5F, -16F, -4.5F, 9F, 4F, 9F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition part_petal_0 = part_sac.addOrReplaceChild("petal_0", CubeListBuilder.create()
			.texOffs(67, 17).addBox(-4.5F, -2F, -4.5F, 4.5F, 2F, 4.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(4.5F, -16F, 4.5F, 0F, 0F, 0F));
		PartDefinition part_petal_1 = part_sac.addOrReplaceChild("petal_1", CubeListBuilder.create()
			.texOffs(88, 17).addBox(-4.5F, -2F, 0F, 4.5F, 2F, 4.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(4.5F, -16F, -4.5F, 0F, 0F, 0F));
		PartDefinition part_petal_2 = part_sac.addOrReplaceChild("petal_2", CubeListBuilder.create()
			.texOffs(0, 28).addBox(0F, -2F, 0F, 4.5F, 2F, 4.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-4.5F, -16F, -4.5F, 0F, 0F, 0F));
		PartDefinition part_petal_3 = part_sac.addOrReplaceChild("petal_3", CubeListBuilder.create()
			.texOffs(21, 28).addBox(0F, -2F, -4.5F, 4.5F, 2F, 4.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(-4.5F, -16F, 4.5F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	@Override
	public void setupAnim(KrakenEggRenderState state) {
		this.resetPose();
		this.pulse.apply(state.pulse, state.ageInTicks);
		this.hatch.apply(state.hatch, state.ageInTicks);
	}
}
