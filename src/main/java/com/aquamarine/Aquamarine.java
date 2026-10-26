package com.aquamarine;

import com.aquamarine.block.ModBlocks;
import com.aquamarine.block.ModBuildingBlocks;
import com.aquamarine.block.NeutroniumOre;
import com.aquamarine.command.CreatureCommand;
import com.aquamarine.command.HarborCommand;
import com.aquamarine.command.KrakenCommand;
import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.effect.ModEffects;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.ModVillagers;
import com.aquamarine.entity.boss.AquamarineBossBar;
import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.harbor.BiomeMapFunction;
import com.aquamarine.harbor.HarborAlert;
import com.aquamarine.harbor.HarborElevators;
import com.aquamarine.item.ModCreativeTab;
import com.aquamarine.item.HarpoonEffects;
import com.aquamarine.item.ModComponents;
import com.aquamarine.item.ModItems;
import com.aquamarine.item.TideThrusterItem;
import com.aquamarine.item.AbyssalEffects;
import com.aquamarine.item.NeutroniumEffects;
import com.aquamarine.menu.ModMenus;
import com.aquamarine.pressure.PressureManager;
import com.aquamarine.recipe.ModRecipes;
import com.aquamarine.sound.ModSounds;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aquamarine implements ModInitializer {
	public static final String MOD_ID = "aquamarine";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		AquamarineConfig.load();
		ModSounds.register();
		// Ordem importa: blocos antes dos itens de bloco.
		ModBlocks.register();
		ModComponents.register();
		ModItems.register();
		ModBuildingBlocks.register();
		ModRecipes.register();
		ModMenus.register();
		ModCreativeTab.register();
		ModEntities.register();
		ModVillagers.register();
		BiomeMapFunction.register();
		HarborAlert.register();
		HarborElevators.register();
		AquamarineBossBar.registerPayloads();
		AbyssalEffects.register();
		NeutroniumEffects.register();
		NeutroniumOre.register();
		ModEffects.register();
		PressureManager.register();
		HarpoonEffects.register();
		TideThrusterItem.register();
		KrakenSummoning.register();
		KrakenCommand.register();
		CreatureCommand.register();
		HarborCommand.register();
		LOGGER.info("Aquamarine carregado.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
