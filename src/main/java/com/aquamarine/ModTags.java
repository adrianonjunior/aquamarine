package com.aquamarine;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

/** Tags do mod. Os arquivos ficam em {@code data/aquamarine/tags/}. */
public final class ModTags {
	public static final class Items {
		/** Itens que causam 60% de dano no escudo do Kraken (ferramentas de Oricálcio). */
		public static final TagKey<Item> SHIELD_BREAKERS = create("shield_breakers");
		/** Arpões (dano triplo no escudo do Kraken); o Arpão de Oricálcio entra aqui. */
		public static final TagKey<Item> HARPOONS = create("harpoons");
		/** Armaduras de metal: a Enguia Elétrica causa 1,5x de dano em quem as usa. */
		public static final TagKey<Item> METAL_ARMOR = create("metal_armor");
		/** Material de reparo das ferramentas de Oricálcio. */
		public static final TagKey<Item> ORICHALCUM_TOOL_MATERIALS = create("orichalcum_tool_materials");
		/** Material de reparo da armadura de Oricálcio. */
		public static final TagKey<Item> REPAIRS_ORICHALCUM_ARMOR = create("repairs_orichalcum_armor");

		/** Material de reparo do Traje de Mergulho Profundo. */
		public static final TagKey<Item> REPAIRS_DEEP_DIVING_ARMOR = create("repairs_deep_diving_armor");

		private static TagKey<Item> create(String path) {
			return TagKey.create(Registries.ITEM, Aquamarine.id(path));
		}

		private Items() {}
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
