package com.aquamarine.recipe;

import com.aquamarine.Aquamarine;

import java.util.Optional;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import org.jspecify.annotations.Nullable;

/** Tipos e serializadores de receita do mod. */
public final class ModRecipes {
	public static final RecipeType<CraftingRecipe> ABYSSAL_FORGING = Registry.register(
		BuiltInRegistries.RECIPE_TYPE, Aquamarine.id("abyssal_forging"), new RecipeType<CraftingRecipe>() {
			@Override
			public String toString() {
				return "aquamarine:abyssal_forging";
			}
		});

	static {
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Aquamarine.id("abyssal_forging"), AbyssalForgingRecipe.SERIALIZER);
	}

	/** Receita que a Forja Abissal monta: primeiro as da forja, depois as da bancada comum. */
	public static Optional<RecipeHolder<CraftingRecipe>> findForgeRecipe(
		ServerLevel level, CraftingInput input, @Nullable RecipeHolder<CraftingRecipe> hint) {
		RecipeManager recipes = level.getServer().getRecipeManager();
		Optional<RecipeHolder<CraftingRecipe>> forged = recipes.getRecipeFor(ABYSSAL_FORGING, input, level, hint);
		return forged.isPresent() ? forged : recipes.getRecipeFor(RecipeType.CRAFTING, input, level, hint);
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModRecipes() {}
}
