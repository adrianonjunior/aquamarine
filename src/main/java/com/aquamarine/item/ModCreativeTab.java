package com.aquamarine.item;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.ModBuildingBlocks;

import java.util.List;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Aba criativa "Aquamarine" com todos os itens do mod. Abas de mod ficam na página 2 do
 * inventário criativo (setas no canto), então os itens também entram no fim das abas vanilla
 * que fazem sentido, para quem não acha a página 2.
 */
public final class ModCreativeTab {
	public static final ResourceKey<CreativeModeTab> KEY =
		ResourceKey.create(Registries.CREATIVE_MODE_TAB, Aquamarine.id("aquamarine"));

	public static void register() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, KEY, FabricCreativeModeTab.builder()
			.title(Component.translatable("itemGroup.aquamarine"))
			.icon(() -> new ItemStack(ModItems.ABYSSAL_PEARL))
			.displayItems((parameters, output) -> ModItems.tabOrder().forEach(output::accept))
			.build());

		addToVanilla(CreativeModeTabs.INGREDIENTS, ModItems.ABYSSAL_INGOT, ModItems.ABYSSAL_PEARL, ModItems.TIDE_CRYSTAL,
			ModItems.ANCIENT_SCALE, ModItems.LURE_BLUEPRINT, ModItems.ABYSSAL_LURE, ModItems.KRAKEN_EYE, ModItems.ABYSSAL_INK,
			ModItems.KRAKEN_HEART, ModItems.ELECTRIC_SCALE, ModItems.VOLTAIC_COIL);
		addToVanilla(CreativeModeTabs.NATURAL_BLOCKS, ModItems.ABYSSAL_ORE);
		addToVanilla(CreativeModeTabs.BUILDING_BLOCKS, ModItems.ABYSSAL_BLOCK);
		// Fase 4: o Neutrônio nas abas do jogo; blocos aquáticos na de construção e os vidros rachados na de coloridos.
		addToVanilla(CreativeModeTabs.INGREDIENTS, ModItems.RAW_NEUTRONIUM, ModItems.NEUTRONIUM_DUST, ModItems.UNSTABLE_NEUTRONIUM_INGOT,
			ModItems.NEUTRONIUM_INGOT, ModItems.NEUTRONIUM_UPGRADE_SMITHING_TEMPLATE);
		addToVanilla(CreativeModeTabs.NATURAL_BLOCKS, ModItems.NEUTRONIUM_ORE);
		addToVanilla(CreativeModeTabs.BUILDING_BLOCKS, ModItems.NEUTRONIUM_BLOCK);
		addToVanilla(CreativeModeTabs.TOOLS_AND_UTILITIES, ModItems.NEUTRONIUM_SHOVEL, ModItems.NEUTRONIUM_PICKAXE, ModItems.NEUTRONIUM_AXE,
			ModItems.NEUTRONIUM_HOE);
		addToVanilla(CreativeModeTabs.COMBAT, ModItems.NEUTRONIUM_SWORD, ModItems.NEUTRONIUM_AXE, ModItems.NEUTRONIUM_HELMET,
			ModItems.NEUTRONIUM_CHESTPLATE, ModItems.NEUTRONIUM_LEGGINGS, ModItems.NEUTRONIUM_BOOTS);
		addToVanilla(CreativeModeTabs.BUILDING_BLOCKS, ModBuildingBlocks.building().stream().map(b -> b.asItem()).toArray(Item[]::new));
		addToVanilla(CreativeModeTabs.COLORED_BLOCKS, ModBuildingBlocks.glass().stream().map(b -> b.asItem()).toArray(Item[]::new));
		addToVanilla(CreativeModeTabs.FUNCTIONAL_BLOCKS, ModItems.ABYSSAL_FORGE, ModItems.ABYSSAL_ALTAR,
			ModItems.KRAKEN_TROPHY, ModItems.GOLDEN_ABYSS_TROPHY, ModItems.TIDE_ANVIL, ModItems.CHART_TABLE, ModItems.SHELL_ALTAR);
		addToVanilla(CreativeModeTabs.TOOLS_AND_UTILITIES, ModItems.ABYSSAL_SHOVEL, ModItems.ABYSSAL_PICKAXE,
			ModItems.ABYSSAL_AXE, ModItems.ABYSSAL_HOE, ModItems.DIVING_GOGGLES, ModItems.DEPTH_GAUGE,
			ModItems.PRESSURE_REGULATOR, ModItems.PRESSURE_TONIC, ModItems.ANCHOR_BUOY, ModItems.TIDE_THRUSTER, ModItems.WHALE_SADDLE);
		addToVanilla(CreativeModeTabs.COMBAT, ModItems.ABYSSAL_SWORD, ModItems.ABYSSAL_AXE, ModItems.ABYSSAL_HELMET,
			ModItems.ABYSSAL_CHESTPLATE, ModItems.ABYSSAL_LEGGINGS, ModItems.ABYSSAL_BOOTS, ModItems.DIVING_GOGGLES,
			ModItems.DEEP_DIVING_HELMET, ModItems.DEEP_DIVING_CHESTPLATE, ModItems.DEEP_DIVING_LEGGINGS, ModItems.DEEP_DIVING_BOOTS,
			ModItems.MORAY_WHIP, ModItems.ABYSSAL_HARPOON, ModItems.HARPOON_QUIVER, ModItems.ABYSSAL_HARPOON_TIP, ModItems.POISON_HARPOON_TIP,
			ModItems.FIRE_HARPOON_TIP, ModItems.WITHER_HARPOON_TIP, ModItems.SHOCK_HARPOON_TIP);
	}

	private static void addToVanilla(ResourceKey<CreativeModeTab> tab, Item... items) {
		List<Item> list = List.of(items);
		CreativeModeTabEvents.modifyOutputEvent(tab).register(output -> list.forEach(output::accept));
	}

	private ModCreativeTab() {}
}
