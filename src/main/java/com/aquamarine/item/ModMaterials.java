package com.aquamarine.item;

import com.aquamarine.Aquamarine;
import com.aquamarine.ModTags;

import java.util.Map;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/** Materiais de ferramenta e armadura. Números em docs/05-itens.md. */
public final class ModMaterials {
	/**
	 * Abissal: um pouco abaixo da netherite. Minera o mesmo que netherite (até o Neutrônio), mais rápido
	 * que diamante; a espada fica com 7 de dano (1 base + 3 do material + 3 da espada).
	 */
	public static final ToolMaterial ABYSSAL_TOOL = new ToolMaterial(
		BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 1800, 8.5F, 3.0F, 18, ModTags.Items.ABYSSAL_TOOL_MATERIALS);
	/**
	 * Neutrônio (Fase 4): acima da netherite. A espada fica com 9 de dano (1 base + 5 do material + 3 da espada), e
	 * a netherite tem 8. Como o Abissal, minera o Minério de Neutrônio, que o diamante não minera.
	 */
	public static final ToolMaterial NEUTRONIUM_TOOL = new ToolMaterial(
		BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2800, 10.0F, 5.0F, 18, ModTags.Items.NEUTRONIUM_TOOL_MATERIALS);

	public static final ResourceKey<EquipmentAsset> ABYSSAL_ASSET =
		ResourceKey.create(EquipmentAssets.ROOT_ID, Aquamarine.id("abyssal"));
	public static final ResourceKey<EquipmentAsset> NEUTRONIUM_ASSET =
		ResourceKey.create(EquipmentAssets.ROOT_ID, Aquamarine.id("neutronium"));
	public static final ResourceKey<EquipmentAsset> DIVING_GOGGLES_ASSET =
		ResourceKey.create(EquipmentAssets.ROOT_ID, Aquamarine.id("diving_goggles"));

	public static final ResourceKey<EquipmentAsset> DEEP_DIVING_ASSET =
		ResourceKey.create(EquipmentAssets.ROOT_ID, Aquamarine.id("deep_diving"));

	/** Traje de Mergulho Profundo: defesa 2/6/5/2, sem resistência; o valor dele é o ar e a Pressão. */
	public static final ArmorMaterial DEEP_DIVING_ARMOR = new ArmorMaterial(
		25,
		Map.of(ArmorType.HELMET, 2, ArmorType.CHESTPLATE, 6, ArmorType.LEGGINGS, 5, ArmorType.BOOTS, 2, ArmorType.BODY, 9),
		12,
		SoundEvents.ARMOR_EQUIP_TURTLE,
		0.0F,
		0.0F,
		ModTags.Items.REPAIRS_DEEP_DIVING_ARMOR,
		DEEP_DIVING_ASSET);

	/** Defesa 3/7/5/3, resistência 2, durabilidade 35 (multiplicador). */
	public static final ArmorMaterial ABYSSAL_ARMOR = new ArmorMaterial(
		35,
		Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 7, ArmorType.LEGGINGS, 5, ArmorType.BOOTS, 3, ArmorType.BODY, 11),
		18,
		SoundEvents.ARMOR_EQUIP_GOLD,
		2.0F,
		0.05F,
		ModTags.Items.REPAIRS_ABYSSAL_ARMOR,
		ABYSSAL_ASSET);

	/** Neutrônio: defesa 4/9/7/4, resistência 4, repulsão 0,15 e durabilidade 60 (a netherite: 3/8/6/3, 3, 0,1, 37). */
	public static final ArmorMaterial NEUTRONIUM_ARMOR = new ArmorMaterial(
		60,
		Map.of(ArmorType.HELMET, 4, ArmorType.CHESTPLATE, 9, ArmorType.LEGGINGS, 7, ArmorType.BOOTS, 4, ArmorType.BODY, 13),
		20,
		SoundEvents.ARMOR_EQUIP_NETHERITE,
		4.0F,
		0.15F,
		ModTags.Items.REPAIRS_NEUTRONIUM_ARMOR,
		NEUTRONIUM_ASSET);

	private ModMaterials() {}
}
