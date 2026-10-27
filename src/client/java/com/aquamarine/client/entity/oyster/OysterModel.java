package com.aquamarine.client.entity.oyster;

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
 * Ostra de águas mornas: valvas estriadas de pedra e nácar, manto coral e pérola luminosa.
 * GERADO por tools/models/spawnlings/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class OysterModel extends EntityModel<OysterRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("oyster"), "main");
	private final KeyframeAnimation idle;
	private final KeyframeAnimation close;
	private final KeyframeAnimation open;
	private final KeyframeAnimation pearl_glow;

	public OysterModel(ModelPart root) {
		super(root);
		this.idle = OysterAnimations.IDLE.bake(root);
		this.close = OysterAnimations.CLOSE.bake(root);
		this.open = OysterAnimations.OPEN.bake(root);
		this.pearl_glow = OysterAnimations.PEARL_GLOW.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition part_body = root.addOrReplaceChild("body", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-4F, -1.5F, -3F, 8F, 1.5F, 6F, new CubeDeformation(0F))
			.texOffs(0, 9).addBox(-3F, -0.5F, -2.5F, 6F, 1F, 5F, new CubeDeformation(0F))
			.texOffs(23, 9).addBox(-3F, -1.7F, -2.5F, 6F, 0.5F, 5F, new CubeDeformation(0F))
			.texOffs(0, 23).addBox(-3.6F, 0.2F, -2F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(11, 23).addBox(-2.1F, 0.2F, -2F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(22, 23).addBox(-0.6F, 0.2F, -2F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(33, 23).addBox(0.9F, 0.2F, -2F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(44, 23).addBox(2.4F, 0.2F, -2F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(46, 16).addBox(-2.5F, -1.9F, -2F, 5F, 0.6F, 4F, new CubeDeformation(0F))
			.texOffs(0, 35).addBox(-1F, -1F, 2.5F, 2F, 1.2F, 1F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, 23F, 0F, 0F, 0F, 0F));
		PartDefinition part_lid = part_body.addOrReplaceChild("lid", CubeListBuilder.create()
			.texOffs(29, 0).addBox(-4F, -0.5F, -6F, 8F, 1.5F, 6F, new CubeDeformation(0F))
			.texOffs(0, 16).addBox(-3F, -1.5F, -5.5F, 6F, 1F, 5F, new CubeDeformation(0F))
			.texOffs(23, 16).addBox(-3F, 0.7F, -5.5F, 6F, 0.5F, 5F, new CubeDeformation(0F))
			.texOffs(0, 29).addBox(-3.6F, -1.8F, -5F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(11, 29).addBox(-2.1F, -1.8F, -5F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(22, 29).addBox(-0.6F, -1.8F, -5F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(33, 29).addBox(0.9F, -1.8F, -5F, 0.5F, 0.4F, 4F, new CubeDeformation(0F))
			.texOffs(44, 29).addBox(2.4F, -1.8F, -5F, 0.5F, 0.4F, 4F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -1F, 3F, 0F, 0F, 0F));
		PartDefinition part_pearl = part_body.addOrReplaceChild("pearl", CubeListBuilder.create()
			.texOffs(55, 29).addBox(-0.75F, -0.75F, -0.75F, 1.5F, 1.5F, 1.5F, new CubeDeformation(0F)),
			PartPose.offsetAndRotation(0F, -2.4F, -0.5F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void setupAnim(OysterRenderState state) {
		this.resetPose();
        this.root.getChild("body").getChild("lid").xRot = (float)Math.toRadians(-25 + 3*Math.sin(state.ageInTicks*Math.PI/40)) * state.openness;
        this.root.getChild("body").getChild("pearl").visible = state.pearl;
		this.idle.apply(state.idle, state.ageInTicks);
		this.close.apply(state.close, state.ageInTicks);
		this.open.apply(state.open, state.ageInTicks);
		this.pearl_glow.apply(state.pearl_glow, state.ageInTicks);
	}
}
