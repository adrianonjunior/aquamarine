package com.aquamarine;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

/** Tags do mod. Os arquivos ficam em {@code data/aquamarine/tags/}. */
public final class ModTags {
	public static final class Items {
		/** Itens que causam 60% de dano no escudo do Kraken (ferramentas Abissais). */
		public static final TagKey<Item> SHIELD_BREAKERS = create("shield_breakers");
		/** Arpões (dano triplo no escudo do Kraken); o Arpão Abissal entra aqui. */
		public static final TagKey<Item> HARPOONS = create("harpoons");
		/** Armaduras de metal: a Enguia Elétrica causa 1,5x de dano em quem as usa. */
		public static final TagKey<Item> METAL_ARMOR = create("metal_armor");
		/** Material de reparo das ferramentas Abissais. */
		public static final TagKey<Item> ABYSSAL_TOOL_MATERIALS = create("abyssal_tool_materials");
		/** Material de reparo da armadura Abissal. */
		public static final TagKey<Item> REPAIRS_ABYSSAL_ARMOR = create("repairs_abyssal_armor");

		/** Material de reparo do Traje de Mergulho Profundo. */
		public static final TagKey<Item> REPAIRS_DEEP_DIVING_ARMOR = create("repairs_deep_diving_armor");

		/** Material de reparo das ferramentas de Neutrônio. */
		public static final TagKey<Item> NEUTRONIUM_TOOL_MATERIALS = create("neutronium_tool_materials");
		/** Material de reparo da armadura de Neutrônio. */
		public static final TagKey<Item> REPAIRS_NEUTRONIUM_ARMOR = create("repairs_neutronium_armor");
		/** Ferramentas de Neutrônio: 80% de dano no escudo do Kraken. */
		public static final TagKey<Item> NEUTRONIUM_TOOLS = create("neutronium_tools");
		/** Itens da família do Neutrônio: afundam na água. */
		public static final TagKey<Item> NEUTRONIUM_DENSE = create("neutronium_dense");

		private static TagKey<Item> create(String path) {
			return TagKey.create(Registries.ITEM, Aquamarine.id(path));
		}

		private Items() {}
	}

	public static final class DamageTypes {
		/** O que não estraga itens de Neutrônio: fogo e explosão. */
		public static final TagKey<DamageType> NEUTRONIUM_IMMUNE = TagKey.create(Registries.DAMAGE_TYPE, Aquamarine.id("neutronium_immune"));

		private DamageTypes() {}
	}

	public static final class Biomes {
		/**
		 * Fossas onde a Moréia surge sem tempestade. É a tag da Fossa de Nereia
		 * ({@code /locate biome #aquamarine_fossa_de_nereia}), por enquanto o oceano profundo.
		 */
		public static final TagKey<Biome> MORAY_TRENCHES = TagKey.create(Registries.BIOME,
			Identifier.withDefaultNamespace("aquamarine_fossa_de_nereia"));

		private Biomes() {}
	}

	public static final class Structures {
		/** Cidades Perdidas: o canto da Baleia-de-Nereia aponta a mais próxima (vazia até a Cidade existir). */
		public static final TagKey<Structure> LOST_CITY = TagKey.create(Registries.STRUCTURE, Aquamarine.id("lost_city"));

		private Structures() {}
	}

	private ModTags() {}
}
