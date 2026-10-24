package com.aquamarine.client;

import com.aquamarine.client.screen.AbyssalForgeScreen;
import com.aquamarine.menu.ModMenus;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.gui.screens.MenuScreens;

public class AquamarineClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.ABYSSAL_FORGE, AbyssalForgeScreen::new);
		// Renderizadores, barra de vida customizada do Kraken e HUD entram aqui.
	}
}
