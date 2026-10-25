package com.aquamarine.client;

import com.aquamarine.client.bossbar.AquamarineBossBarHud;
import com.aquamarine.client.entity.kraken.KrakenModel;
import com.aquamarine.client.entity.kraken_egg.KrakenEggModel;
import com.aquamarine.client.entity.kraken_egg.KrakenEggRenderer;
import com.aquamarine.client.entity.tentacle.TentacleModel;
import com.aquamarine.client.entity.tentacle.TentacleRenderer;
import com.aquamarine.client.entity.young_kraken.YoungKrakenModel;
import com.aquamarine.client.entity.young_kraken.YoungKrakenRenderer;
import com.aquamarine.client.entity.kraken.KrakenRenderer;
import com.aquamarine.client.entity.moray.MorayModel;
import com.aquamarine.client.entity.moray.MorayRenderer;
import com.aquamarine.client.entity.sperm_whale.SpermWhaleModel;
import com.aquamarine.client.entity.sperm_whale.SpermWhaleRenderer;
import com.aquamarine.client.entity.whale.WhaleModel;
import com.aquamarine.client.entity.whale.WhaleRenderer;
import com.aquamarine.client.pressure.PressureHud;
import com.aquamarine.client.screen.AbyssalForgeScreen;
import com.aquamarine.client.music.KrakenMusic;
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
		ModelLayerRegistry.registerModelLayer(TentacleModel.LAYER, TentacleModel::createBodyLayer);
		ModelLayerRegistry.registerModelLayer(YoungKrakenModel.LAYER, YoungKrakenModel::createBodyLayer);
		ModelLayerRegistry.registerModelLayer(KrakenEggModel.LAYER, KrakenEggModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.TENTACLE, TentacleRenderer::new);
		EntityRendererRegistry.register(ModEntities.YOUNG_KRAKEN, YoungKrakenRenderer::new);
		EntityRendererRegistry.register(ModEntities.KRAKEN_EGG, KrakenEggRenderer::new);
		ModelLayerRegistry.registerModelLayer(MorayModel.LAYER, MorayModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.MORAY, MorayRenderer::new);
		EntityRendererRegistry.register(ModEntities.MORAY_PART, NoopRenderer::new);
		ModelLayerRegistry.registerModelLayer(WhaleModel.LAYER, WhaleModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.WHALE, WhaleRenderer::new);
		ModelLayerRegistry.registerModelLayer(SpermWhaleModel.LAYER, SpermWhaleModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.SPERM_WHALE, SpermWhaleRenderer::new);
		AquamarineBossBarHud.register();
		PressureHud.register();
		SummonEffects.register();
		ItemDescriptions.register();
		KrakenEffects.register();
		KrakenMusic.register();
		AquamarineKeys.register();
		// Renderizadores e HUD do Kraken entram aqui.
	}
}
