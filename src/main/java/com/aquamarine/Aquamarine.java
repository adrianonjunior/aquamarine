package com.aquamarine;

import com.aquamarine.block.ModBlocks;
import com.aquamarine.command.KrakenCommand;
import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.boss.AquamarineBossBar;
import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.item.ModCreativeTab;
import com.aquamarine.item.ModItems;
import com.aquamarine.item.OrichalcumEffects;
import com.aquamarine.menu.ModMenus;
import com.aquamarine.recipe.ModRecipes;

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
		// Ordem importa: blocos antes dos itens de bloco.
		ModBlocks.register();
		ModItems.register();
		ModRecipes.register();
		ModMenus.register();
		ModCreativeTab.register();
		ModEntities.register();
		AquamarineBossBar.registerPayloads();
		OrichalcumEffects.register();
		KrakenSummoning.register();
		KrakenCommand.register();
		LOGGER.info("Aquamarine carregado.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
