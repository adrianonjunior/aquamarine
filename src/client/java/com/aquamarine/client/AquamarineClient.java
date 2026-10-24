package com.aquamarine.client;

import com.aquamarine.client.bossbar.AquamarineBossBarHud;
import com.aquamarine.client.entity.kraken.KrakenModel;
import com.aquamarine.client.entity.kraken.KrakenRenderer;
import com.aquamarine.client.pressure.PressureHud;
import com.aquamarine.client.screen.AbyssalForgeScreen;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.menu.ModMenus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.NoopRenderer;

public class AquamarineClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.ABYSSAL_FORGE, AbyssalForgeScreen::new);
		EntityRendererRegistry.register(ModEntities.BOSS_DUMMY, NoopRenderer::new);
		ModelLayerRegistry.registerModelLayer(KrakenModel.LAYER, KrakenModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.KRAKEN, KrakenRenderer::new);
		AquamarineBossBarHud.register();
		PressureHud.register();
		SummonEffects.register();
		ItemDescriptions.register();
		KrakenEffects.register();
		// Renderizadores e HUD do Kraken entram aqui.
	}
}
