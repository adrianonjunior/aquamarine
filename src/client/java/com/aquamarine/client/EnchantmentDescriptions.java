package com.aquamarine.client;

import com.aquamarine.Aquamarine;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * Descrição dos encantamentos do mod (Fase 6, 6F2): o que fazem, quanto por nível e onde valem. Aparece no tooltip
 * dos livros encantados e na página de informação do JEI. Texto em {@code lang/*.json}:
 * {@code enchantment.aquamarine.<id>.desc}, a mesma chave que os mods de descrição de encantamento leem.
 */
public final class EnchantmentDescriptions {
	public static void register() {
		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			if (!stack.is(Items.ENCHANTED_BOOK)) {
				return;
			}
			for (Holder<Enchantment> holder : EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet()) {
				Component line = line(holder);
				if (line != null) {
					lines.add(line);
				}
			}
		});
	}

	/** A descrição de um encantamento do mod; {@code null} para os do jogo e de outros mods. */
	public static Component line(Holder<Enchantment> holder) {
		return holder.unwrapKey().filter(key -> Aquamarine.MOD_ID.equals(key.identifier().getNamespace()))
			.map(key -> (Component) Component.translatable("enchantment.aquamarine." + key.identifier().getPath() + ".desc")
				.withStyle(ChatFormatting.DARK_AQUA))
			.orElse(null);
	}

	private EnchantmentDescriptions() {}
}
