package com.aquamarine.item;

import com.aquamarine.Aquamarine;

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

		addToVanilla(CreativeModeTabs.INGREDIENTS, ModItems.ORICHALCUM_INGOT, ModItems.ABYSSAL_PEARL, ModItems.TIDE_CRYSTAL,
			ModItems.ANCIENT_SCALE, ModItems.LURE_BLUEPRINT, ModItems.ABYSSAL_LURE, ModItems.KRAKEN_EYE, ModItems.ABYSSAL_INK,
			ModItems.KRAKEN_HEART);
		addToVanilla(CreativeModeTabs.NATURAL_BLOCKS, ModItems.ORICHALCUM_ORE);
		addToVanilla(CreativeModeTabs.BUILDING_BLOCKS, ModItems.ORICHALCUM_BLOCK);
		addToVanilla(CreativeModeTabs.FUNCTIONAL_BLOCKS, ModItems.ABYSSAL_FORGE, ModItems.ABYSSAL_ALTAR,
			ModItems.KRAKEN_TROPHY, ModItems.GOLDEN_ABYSS_TROPHY);
		addToVanilla(CreativeModeTabs.TOOLS_AND_UTILITIES, ModItems.ORICHALCUM_SHOVEL, ModItems.ORICHALCUM_PICKAXE,
			ModItems.ORICHALCUM_AXE, ModItems.ORICHALCUM_HOE, ModItems.DIVING_GOGGLES);
		addToVanilla(CreativeModeTabs.COMBAT, ModItems.ORICHALCUM_SWORD, ModItems.ORICHALCUM_AXE, ModItems.ORICHALCUM_HELMET,
			ModItems.ORICHALCUM_CHESTPLATE, ModItems.ORICHALCUM_LEGGINGS, ModItems.ORICHALCUM_BOOTS, ModItems.DIVING_GOGGLES);
	}

	private static void addToVanilla(ResourceKey<CreativeModeTab> tab, Item... items) {
		List<Item> list = List.of(items);
		CreativeModeTabEvents.modifyOutputEvent(tab).register(output -> list.forEach(output::accept));
	}

	private ModCreativeTab() {}
}
