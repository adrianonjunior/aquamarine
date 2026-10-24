package com.aquamarine;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/** Tags do mod. Os arquivos ficam em {@code data/aquamarine/tags/}. */
public final class ModTags {
	public static final class Items {
		/** Itens que causam 60% de dano no escudo do Kraken (ferramentas de Oricálcio). */
		public static final TagKey<Item> SHIELD_BREAKERS = create("shield_breakers");
		/** Armaduras de metal: a Enguia Elétrica causa 1,5x de dano em quem as usa. */
		public static final TagKey<Item> METAL_ARMOR = create("metal_armor");
		/** Material de reparo das ferramentas de Oricálcio. */
		public static final TagKey<Item> ORICHALCUM_TOOL_MATERIALS = create("orichalcum_tool_materials");
		/** Material de reparo da armadura de Oricálcio. */
		public static final TagKey<Item> REPAIRS_ORICHALCUM_ARMOR = create("repairs_orichalcum_armor");

		private static TagKey<Item> create(String path) {
			return TagKey.create(Registries.ITEM, Aquamarine.id(path));
		}

		private Items() {}
	}

	private ModTags() {}
}
