package com.aquamarine.client;

import com.aquamarine.client.bossbar.AquamarineBossBarHud;
import com.aquamarine.client.entity.NeutroniumArmorRenderer;
import com.aquamarine.client.particle.AquamarineParticles;
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
import com.aquamarine.client.machine.MachineRenderer;
import com.aquamarine.client.machine.VentGeneratorAnimations;
import com.aquamarine.client.machine.VentGeneratorModel;
import com.aquamarine.energy.ModEnergy;
import com.aquamarine.energy.WaterGeneratorBlock;
import com.aquamarine.energy.WaterGeneratorBlockEntity;
import com.aquamarine.client.pressure.PressureHud;
import com.aquamarine.client.screen.AbyssalForgeScreen;
import com.aquamarine.client.music.KrakenMusic;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.menu.ModMenus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
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
		// Fase 6: a v3a é o Kraken; a v3b e a v3c têm IDs próprios (KrakenRenderer.Variant).
		ModelLayerRegistry.registerModelLayer(com.aquamarine.client.entity.kraken.KrakenV3aModel.LAYER,
			com.aquamarine.client.entity.kraken.KrakenV3aModel::createBodyLayer);
		ModelLayerRegistry.registerModelLayer(com.aquamarine.client.entity.kraken.KrakenV3bModel.LAYER,
			com.aquamarine.client.entity.kraken.KrakenV3bModel::createBodyLayer);
		ModelLayerRegistry.registerModelLayer(com.aquamarine.client.entity.kraken.KrakenV3cModel.LAYER,
			com.aquamarine.client.entity.kraken.KrakenV3cModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.KRAKEN, ctx -> new KrakenRenderer<>(ctx, KrakenRenderer.Variant.V3A));
		EntityRendererRegistry.register(ModEntities.KRAKEN_VAMPIRE, ctx -> new KrakenRenderer<>(ctx, KrakenRenderer.Variant.V3B));
		EntityRendererRegistry.register(ModEntities.KRAKEN_AMMONITE, ctx -> new KrakenRenderer<>(ctx, KrakenRenderer.Variant.V3C));
		ModelLayerRegistry.registerModelLayer(TentacleModel.LAYER, TentacleModel::createBodyLayer);
		ModelLayerRegistry.registerModelLayer(YoungKrakenModel.LAYER, YoungKrakenModel::createBodyLayer);
		ModelLayerRegistry.registerModelLayer(KrakenEggModel.LAYER, KrakenEggModel::createBodyLayer);
		// 6D3: o tridente em voo passa por este renderer; só o arpão do mod troca de desenho.
		ModelLayerRegistry.registerModelLayer(com.aquamarine.client.entity.harpoon.HarpoonModel.LAYER,
			com.aquamarine.client.entity.harpoon.HarpoonModel::createBodyLayer);
		EntityRendererRegistry.register(net.minecraft.world.entity.EntityTypes.TRIDENT, com.aquamarine.client.entity.harpoon.HarpoonRenderer::new);
		EntityRendererRegistry.register(ModEntities.TENTACLE, TentacleRenderer::new);
		EntityRendererRegistry.register(ModEntities.YOUNG_KRAKEN, YoungKrakenRenderer::new);
		EntityRendererRegistry.register(ModEntities.KRAKEN_EGG, KrakenEggRenderer::new);
		ModelLayerRegistry.registerModelLayer(MorayModel.LAYER, MorayModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.MORAY, MorayRenderer::new);
		EntityRendererRegistry.register(ModEntities.MORAY_PART, NoopRenderer::new);
		EntityRendererRegistry.register(ModEntities.WHALE_PART, NoopRenderer::new);
		ModelLayerRegistry.registerModelLayer(WhaleModel.LAYER, WhaleModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.WHALE, WhaleRenderer::new);
		ModelLayerRegistry.registerModelLayer(SpermWhaleModel.LAYER, SpermWhaleModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.SPERM_WHALE, SpermWhaleRenderer::new);
		NeutroniumArmorRenderer.register();
		// 0.6.1 (7C): o que passa dentro de Cabos, Canos e Tubos de Ar.
		BlockEntityRendererRegistry.register(com.aquamarine.network.ModNetworks.CONDUIT_ENTITY,
			ctx -> new com.aquamarine.client.network.ConduitRenderer());
		// 0.6.1 (7B): máquinas animadas desenhadas pelo block entity; o Gerador de Fenda é a primeira.
		BlockEntityRendererRegistry.register(ModEnergy.WATER_GENERATOR_ENTITY, ctx -> {
			MachineRenderer.Machine vent = MachineRenderer.Machine.of(VentGeneratorModel::create, VentGeneratorAnimations.CLIPS,
				VentGeneratorModel.GLASS, VentGeneratorModel.TEXTURE, VentGeneratorModel.GLOW);
			return new MachineRenderer<WaterGeneratorBlockEntity>(be -> be.kind() == WaterGeneratorBlock.Kind.VENT ? vent : null);
		});
		AquamarineParticles.register();
		com.aquamarine.client.IndustryClient.register();
		com.aquamarine.client.manual.ManualClient.register();
		EntityRendererRegistry.register(ModEntities.CAVITATION_PULSE, NoopRenderer::new);
		AquamarineBossBarHud.register();
		PressureHud.register();
		SummonEffects.register();
		ItemDescriptions.register();
		EnchantmentDescriptions.register();
		RadarHud.register();
		KrakenEffects.register();
		KrakenMusic.register();
		AquamarineKeys.register();
		SyncedRecipes.register();
		// Renderizadores e HUD do Kraken entram aqui.
	}
}
