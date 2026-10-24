package com.aquamarine.client;

import com.aquamarine.Aquamarine;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * Descrição curta de cada item do mod, mostrada no tooltip e na página de informação do JEI.
 * Texto em {@code lang/*.json}: {@code tooltip.aquamarine.<item>} e, se precisar de mais
 * linhas, {@code tooltip.aquamarine.<item>.2}, {@code .3}...
 */
public final class ItemDescriptions {
	private static final int MAX_LINES = 4;

	public static void register() {
		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			List<Component> description = lines(stack.getItem());
			if (!description.isEmpty()) {
				// Logo abaixo do nome, antes de atributos e encantamentos.
				lines.addAll(Math.min(1, lines.size()), description);
			}
		});
	}

	/** Linhas da descrição (cinza); vazia para itens sem descrição ou de outros mods. */
	public static List<Component> lines(Item item) {
		Identifier id = BuiltInRegistries.ITEM.getKey(item);
		List<Component> out = new ArrayList<>();
		if (!Aquamarine.MOD_ID.equals(id.getNamespace())) {
			return out;
		}
		String base = "tooltip.aquamarine." + id.getPath();
		Language language = Language.getInstance();
		for (int i = 1; i <= MAX_LINES; i++) {
			String key = i == 1 ? base : base + "." + i;
			if (!language.has(key)) {
				break;
			}
			out.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
		}
		return out;
	}

	private ItemDescriptions() {}
}
