package com.aquamarine.client.entity.harpoon;

import com.aquamarine.Aquamarine;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;

/**
 * Arpão em voo (Fase 6, 6D3): haste, punho, colar, lâmina com farpas e aletas na cauda. Mesma orientação do tridente
 * do jogo (ponta em -y). As texturas por ponta saem de tools/gen_phase6_art.py com estas mesmas posições de UV.
 */
public final class HarpoonModel extends EntityModel<ThrownTridentRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("harpoon"), "main");

	public HarpoonModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		mesh.getRoot().addOrReplaceChild("harpoon", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 22.0F, 1.0F)
			.texOffs(4, 0).addBox(-1.0F, 14.0F, -1.0F, 2.0F, 4.0F, 2.0F)
			.texOffs(4, 6).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F)
			.texOffs(4, 11).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F)
			.texOffs(12, 11).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 2.0F, 1.0F)
			.texOffs(16, 0).addBox(-2.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F)
			.texOffs(20, 0).addBox(1.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F)
			.texOffs(16, 6).addBox(-2.0F, 21.0F, -0.25F, 4.0F, 4.0F, 0.5F)
			.texOffs(16, 12).addBox(-0.25F, 21.0F, -2.0F, 0.5F, 4.0F, 4.0F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 32, 32);
	}
}
