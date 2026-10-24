package com.aquamarine.item;

import com.aquamarine.Aquamarine;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/** Aba criativa "Aquamarine" com todos os itens do mod. */
public final class ModCreativeTab {
	public static final ResourceKey<CreativeModeTab> KEY =
		ResourceKey.create(Registries.CREATIVE_MODE_TAB, Aquamarine.id("aquamarine"));

	public static void register() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, KEY, FabricCreativeModeTab.builder()
			.title(Component.translatable("itemGroup.aquamarine"))
			.icon(() -> new ItemStack(ModItems.ABYSSAL_PEARL))
			.displayItems((parameters, output) -> ModItems.tabOrder().forEach(output::accept))
			.build());
	}

	private ModCreativeTab() {}
}
