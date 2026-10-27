package com.aquamarine.gametest;

import com.aquamarine.client.compat.jei.AquamarineJeiPlugin;
import com.aquamarine.client.compat.jei.ObtainRecipe;
import com.aquamarine.item.ModItems;
import java.util.Set;
import java.util.stream.Collectors;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.world.item.ItemStack;

/**
 * Receitas visíveis no JEI (pedido do Adriano em 26/10/2026): a categoria "Como obter" tem as páginas do que não sai de
 * receita, e apertar R no Lingote de Neutrônio mostra a têmpera. A parte do convidado (receitas mandadas pelo servidor)
 * fica no teste multiplayer.
 */
final class JeiGameTest {
	private JeiGameTest() {}

	static void run(ClientGameTestContext context) {
		Set<String> pages = context.computeOnClient(mc -> runtime().getRecipeManager().createRecipeLookup(AquamarineJeiPlugin.OBTAIN).get()
			.map(ObtainRecipe::key).collect(Collectors.toSet()));
		check(pages.containsAll(Set.of("tempering", "neutronium_ore", "collector", "kraken", "creative_only")),
			"JEI: faltam páginas de Como obter, tem " + pages);
		check(context.computeOnClient(mc -> runtime().getRecipeManager().createRecipeLookup(AquamarineJeiPlugin.ABYSSAL_FORGING).get()
			.anyMatch(holder -> holder.id().identifier().getPath().equals("unstable_neutronium_ingot"))), "JEI: a Forja devia mostrar o Lingote Instável");
		context.runOnClient(mc -> {
			IJeiRuntime runtime = runtime();
			runtime.getRecipesGui().show(runtime.getJeiHelpers().getFocusFactory()
				.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, new ItemStack(ModItems.NEUTRONIUM_INGOT)));
		});
		context.waitTicks(10);
		context.takeScreenshot("jei_00_lingote_neutronio");
		context.runOnClient(mc -> {
			IJeiRuntime runtime = runtime();
			runtime.getRecipesGui().show(runtime.getJeiHelpers().getFocusFactory()
				.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, new ItemStack(ModItems.KRAKEN_EYE)));
		});
		context.waitTicks(10);
		context.takeScreenshot("jei_01_olho_do_kraken");
		context.setScreen(() -> null);
	}

	static IJeiRuntime runtime() {
		return AquamarineJeiPlugin.runtime().orElseThrow(() -> new AssertionError("JEI não carregou"));
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
