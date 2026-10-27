package com.aquamarine.client.compat.jei;

import com.aquamarine.energy.ModEnergy;
import com.aquamarine.item.ModItems;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Uma página "Como obter" do JEI (pedido do Adriano em 26/10/2026): o que não sai de bancada, fornalha ou Forja aparece
 * aqui ao apertar R no item, com o que entra (se houver), o que sai e de onde vem. O texto fica em
 * {@code jei.aquamarine.obtain.<chave>.<n>} nos dois idiomas.
 */
public record ObtainRecipe(String key, List<ItemStack> inputs, List<ItemStack> outputs, int lines) {
	public List<FormattedText> text() {
		return IntStream.rangeClosed(1, lines).mapToObj(i -> (FormattedText) Component.translatable("jei.aquamarine.obtain." + key + "." + i)).toList();
	}

	private static ObtainRecipe of(String key, List<ItemLike> inputs, List<ItemLike> outputs, int lines) {
		return new ObtainRecipe(key, inputs.stream().map(ItemStack::new).toList(), outputs.stream().map(ItemStack::new).toList(), lines);
	}

	/** Todas as páginas: têmpera e Coletor do Neutrônio, minérios, Kraken, Moréia, baleias, Porto e o que ainda é só do criativo. */
	public static List<ObtainRecipe> all() {
		return List.of(
			of("oyster", List.of(com.aquamarine.farming.ModCultivation.CULTIVATION_JAR), List.of(com.aquamarine.farming.ModCultivation.OYSTER_PEARL), 2),
			of("crab", List.of(), List.of(ModItems.CRAB_SHELL), 1),
			of("crab", List.of(), List.of(ModItems.CRAB_SHELL), 2),
			of("tempering", List.of(ModItems.UNSTABLE_NEUTRONIUM_INGOT), List.of(ModItems.NEUTRONIUM_INGOT), 2),
			of("neutronium_ore", List.of(), List.of(ModItems.NEUTRONIUM_ORE, ModItems.RAW_NEUTRONIUM), 2),
			of("collector", List.of(ModEnergy.NEUTRONIUM_COLLECTOR), List.of(ModItems.NEUTRONIUM_STARDUST), 2),
			of("kraken", List.of(ModItems.ABYSSAL_LURE), List.of(ModItems.KRAKEN_EYE, ModItems.ABYSSAL_INK, ModItems.KRAKEN_TROPHY,
				ModItems.GOLDEN_ABYSS_TROPHY, ModItems.KRAKEN_HEART, ModItems.NEUTRONIUM_UPGRADE_SMITHING_TEMPLATE), 3),
			of("aurium_ore", List.of(), List.of(ModItems.AURIUM_ORE, ModItems.RAW_AURIUM), 2),
			of("moray", List.of(), List.of(ModItems.ELECTRIC_SCALE), 2),
			of("whale", List.of(), List.of(ModItems.RAW_WHALE_MEAT), 1),
			of("harbor_chests", List.of(), List.of(ModItems.ABYSSAL_PEARL, ModItems.TIDE_CRYSTAL, ModItems.ANCIENT_SCALE), 2),
			of("harbor_workshops", List.of(), List.of(ModItems.TIDE_ANVIL, ModItems.CHART_TABLE, ModItems.SHELL_ALTAR), 1),
			of("creative_only", List.of(), List.of(ModItems.ABYSSAL_ALTAR, ModItems.LURE_BLUEPRINT, ModItems.ABYSSAL_ORE), 2));
	}
}
