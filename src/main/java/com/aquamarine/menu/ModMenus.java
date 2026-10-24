package com.aquamarine.menu;

import com.aquamarine.Aquamarine;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

/** Tipos de menu (telas de inventário) do mod. As telas do cliente ficam em AquamarineClient. */
public final class ModMenus {
	public static final MenuType<AbyssalForgeMenu> ABYSSAL_FORGE = Registry.register(
		BuiltInRegistries.MENU, Aquamarine.id("abyssal_forge"), new MenuType<>(AbyssalForgeMenu::new, FeatureFlags.VANILLA_SET));

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModMenus() {}
}
