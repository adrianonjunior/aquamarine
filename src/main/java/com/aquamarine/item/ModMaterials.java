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
	 * Oricálcio: um pouco abaixo da netherite. Minera o mesmo que diamante, mais rápido
	 * que diamante; a espada fica com 7 de dano (1 base + 3 do material + 3 da espada).
	 */
	public static final ToolMaterial ORICHALCUM_TOOL = new ToolMaterial(
		BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1800, 8.5F, 3.0F, 18, ModTags.Items.ORICHALCUM_TOOL_MATERIALS);

	public static final ResourceKey<EquipmentAsset> ORICHALCUM_ASSET =
		ResourceKey.create(EquipmentAssets.ROOT_ID, Aquamarine.id("orichalcum"));
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
	public static final ArmorMaterial ORICHALCUM_ARMOR = new ArmorMaterial(
		35,
		Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 7, ArmorType.LEGGINGS, 5, ArmorType.BOOTS, 3, ArmorType.BODY, 11),
		18,
		SoundEvents.ARMOR_EQUIP_GOLD,
		2.0F,
		0.05F,
		ModTags.Items.REPAIRS_ORICHALCUM_ARMOR,
		ORICHALCUM_ASSET);

	private ModMaterials() {}
}
