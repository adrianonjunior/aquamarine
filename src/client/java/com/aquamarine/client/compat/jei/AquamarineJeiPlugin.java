package com.aquamarine.client.compat.jei;

import com.aquamarine.Aquamarine;
import com.aquamarine.client.ItemDescriptions;
import com.aquamarine.item.ModItems;
import com.aquamarine.recipe.ModRecipes;

import java.util.List;
import java.util.Optional;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Integração com o JEI (entrypoint {@code jei_mod_plugin}; o JEI é opcional). Mostra a
 * categoria "Forja Abissal" com as receitas exclusivas, a Forja como estação também da bancada
 * comum (ela aceita essas receitas) e a descrição de cada item na página de informação.
 * <p>
 * Desde o 1.21.2 o cliente não recebe as receitas do servidor; aqui elas vêm do servidor
 * integrado (um jogador). Em servidor dedicado a categoria fica vazia, como as do vanilla
 * quando o JEI não está no servidor.
 */
public class AquamarineJeiPlugin implements IModPlugin {
	public static final IRecipeHolderType<CraftingRecipe> ABYSSAL_FORGING = IRecipeHolderType.create(ModRecipes.ABYSSAL_FORGING);

	private static IJeiRuntime runtime;

	/** O JEI em execução (para abrir telas dele por código, como no teste de cliente). */
	public static Optional<IJeiRuntime> runtime() {
		return Optional.ofNullable(runtime);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
	}

	@Override
	public void onRuntimeUnavailable() {
		runtime = null;
	}

	@Override
	public Identifier getPluginUid() {
		return Aquamarine.id("jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new AbyssalForgeCategory(ABYSSAL_FORGING, registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
		if (server != null) {
			@SuppressWarnings("unchecked")
			List<RecipeHolder<CraftingRecipe>> recipes = server.getRecipeManager().getRecipes().stream()
				.filter(holder -> holder.value().getType() == ModRecipes.ABYSSAL_FORGING)
				.map(holder -> (RecipeHolder<CraftingRecipe>) holder)
				.toList();
			registration.addRecipes(ABYSSAL_FORGING, recipes);
		}
		for (Item item : ModItems.tabOrder()) {
			List<Component> lines = ItemDescriptions.lines(item);
			if (!lines.isEmpty()) {
				registration.addIngredientInfo(item, lines.toArray(Component[]::new));
			}
		}
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(ABYSSAL_FORGING, ModItems.ABYSSAL_FORGE);
		registration.addCraftingStation(RecipeTypes.CRAFTING, ModItems.ABYSSAL_FORGE);
	}
}
