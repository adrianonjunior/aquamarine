package com.aquamarine.entity;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.ModBlocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * A Vila subaquática, o Porto dos Náufragos (docs/08-fase3.md, seção 8): as três profissões dos
 * Mergulhadores, seus blocos de trabalho e a estrutura. As trocas são dados
 * ({@code data/aquamarine/trade_set/} e {@code villager_trade/}); a estrutura vem de
 * {@code tools/gen_harbor.py}.
 */
public final class ModVillagers {
	public static final ResourceKey<Structure> HARBOR = ResourceKey.create(Registries.STRUCTURE, Aquamarine.id("harbor"));
	/** Tag dos Portos (mapa do Cartógrafo e {@code /locate structure #aquamarine:harbor}). */
	public static final TagKey<Structure> HARBORS = TagKey.create(Registries.STRUCTURE, Aquamarine.id("harbor"));

	public static final ResourceKey<PoiType> TIDE_ANVIL_POI = poi("tide_anvil", ModBlocks.TIDE_ANVIL);
	public static final ResourceKey<PoiType> CHART_TABLE_POI = poi("chart_table", ModBlocks.CHART_TABLE);
	public static final ResourceKey<PoiType> SHELL_ALTAR_POI = poi("shell_altar", ModBlocks.SHELL_ALTAR);

	/** Ferreiro de Maré: Aljava, pontas, Medidor, Regulador e Sela. */
	public static final ResourceKey<VillagerProfession> TIDE_SMITH = profession("tide_smith", TIDE_ANVIL_POI, 5);
	/** Cartógrafo Náutico: mapas. */
	public static final ResourceKey<VillagerProfession> NAUTICAL_CARTOGRAPHER = profession("nautical_cartographer", CHART_TABLE_POI, 4);
	/** Encantador de Fossa: os livros dos encantamentos da Fase 3. */
	public static final ResourceKey<VillagerProfession> ABYSS_ENCHANTER = profession("abyss_enchanter", SHELL_ALTAR_POI, 5);

	private static ResourceKey<PoiType> poi(String name, Block block) {
		PoiHelper.register(Aquamarine.id(name), 1, 1, block);
		return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Aquamarine.id(name));
	}

	private static ResourceKey<VillagerProfession> profession(String name, ResourceKey<PoiType> jobSite, int levels) {
		ResourceKey<VillagerProfession> key = ResourceKey.create(Registries.VILLAGER_PROFESSION, Aquamarine.id(name));
		Int2ObjectMap<ResourceKey<TradeSet>> trades = new Int2ObjectOpenHashMap<>();
		for (int level = 1; level <= levels; level++) {
			trades.put(level, ResourceKey.create(Registries.TRADE_SET, Aquamarine.id(name + "/level_" + level)));
		}
		Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, key, new VillagerProfession(
			Component.translatable("entity.aquamarine.villager." + name),
			holder -> holder.is(jobSite), holder -> holder.is(jobSite),
			ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER, trades));
		return key;
	}

	/** Dentro de um Porto dos Náufragos (qualquer peça da estrutura). */
	public static boolean isInHarbor(ServerLevel level, BlockPos pos) {
		return level.structureManager().getStructureWithPieceAt(pos, HARBORS).isValid();
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModVillagers() {}
}
