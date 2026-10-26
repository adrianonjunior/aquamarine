package com.aquamarine.client.entity;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Armadura de Neutrônio (docs/10-fase4.md, §8), no estilo Avaritia: a camada da peça com as placas estreladas e, por
 * cima, um céu de estrelas que escorre devagar com luz própria (o mesmo tipo de desenho do creeper eletrizado).
 */
public final class NeutroniumArmorRenderer implements ArmorRenderer {
	private static final Identifier BODY = Aquamarine.id("textures/entity/equipment/humanoid/neutronium.png");
	private static final Identifier LEGS = Aquamarine.id("textures/entity/equipment/humanoid_leggings/neutronium.png");
	private static final Identifier SWIRL = Aquamarine.id("textures/entity/neutronium_armor_swirl.png");
	private static final int FULL_BRIGHT = 0xF000F0;
	private final ArmorModelSet<HumanoidModel<HumanoidRenderState>> models;

	private NeutroniumArmorRenderer(EntityRendererProvider.Context context) {
		this.models = ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), HumanoidModel::new);
	}

	public static void register() {
		ArmorRenderer.register(NeutroniumArmorRenderer::new, ModItems.NEUTRONIUM_HELMET, ModItems.NEUTRONIUM_CHESTPLATE,
			ModItems.NEUTRONIUM_LEGGINGS, ModItems.NEUTRONIUM_BOOTS);
	}

	@Override
	public void render(PoseStack poseStack, SubmitNodeCollector collector, ItemStack stack, HumanoidRenderState state, EquipmentSlot slot,
		int light, HumanoidModel<HumanoidRenderState> contextModel) {
		HumanoidModel<HumanoidRenderState> model = models.get(slot);
		Identifier texture = slot == EquipmentSlot.LEGS ? LEGS : BODY;
		ArmorRenderer.submitTransformCopyingModel(contextModel, state, model, state, true, collector, poseStack,
			RenderTypes.armorCutoutNoCull(texture), light, OverlayTexture.NO_OVERLAY, state.outlineColor);
		float t = state.ageInTicks * 0.004F;
		ArmorRenderer.submitTransformCopyingModel(contextModel, state, model, state, true, collector.order(1), poseStack,
			RenderTypes.energySwirl(SWIRL, t % 1.0F, (t * 0.7F) % 1.0F), FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
	}
}
