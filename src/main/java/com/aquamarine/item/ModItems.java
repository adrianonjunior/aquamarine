package com.aquamarine.item;

import com.aquamarine.Aquamarine;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class ModItems {
	public static final ResourceKey<Item> DIVING_GOGGLES_KEY =
		ResourceKey.create(Registries.ITEM, Aquamarine.id("diving_goggles"));
	public static final ResourceKey<EquipmentAsset> DIVING_GOGGLES_ASSET =
		ResourceKey.create(EquipmentAssets.ROOT_ID, Aquamarine.id("diving_goggles"));

	/** Óculos de Mergulho: equipados na cabeça, dão visão clara enquanto o jogador está submerso. */
	public static final Item DIVING_GOGGLES = Registry.register(
		BuiltInRegistries.ITEM,
		DIVING_GOGGLES_KEY,
		new Item(new Item.Properties()
			.setId(DIVING_GOGGLES_KEY)
			.stacksTo(1)
			.component(net.minecraft.core.component.DataComponents.EQUIPPABLE,
				Equippable.builder(EquipmentSlot.HEAD).setAsset(DIVING_GOGGLES_ASSET).build())));

	public static void register() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
			.register(entries -> entries.accept(DIVING_GOGGLES));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
			.register(entries -> entries.accept(DIVING_GOGGLES));
	}

	private ModItems() {}
}
