package com.aquamarine.client.compat.jei;

import com.aquamarine.item.ModItems;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;

/** Categoria "Forja Abissal" no JEI: mesma grade 3x3 da bancada, com as receitas exclusivas da Forja. */
public class AbyssalForgeCategory extends AbstractRecipeCategory<RecipeHolder<CraftingRecipe>> {
	private static final int WIDTH = 116;
	private static final int HEIGHT = 54;

	private final ICraftingGridHelper grid;

	public AbyssalForgeCategory(IRecipeHolderType<CraftingRecipe> type, IGuiHelper guiHelper) {
		super(type, Component.translatable("container.aquamarine.abyssal_forge"),
			guiHelper.createDrawableItemLike(ModItems.ABYSSAL_FORGE), WIDTH, HEIGHT);
		this.grid = guiHelper.createCraftingGridHelper();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CraftingRecipe> recipe, IFocusGroup focuses) {
		for (RecipeDisplay display : recipe.value().display()) {
			if (display instanceof ShapedCraftingRecipeDisplay shaped) {
				grid.createAndSetIngredientsFromDisplays(builder, shaped.ingredients(), shaped.width(), shaped.height());
				grid.createAndSetOutputs(builder, shaped.result());
				return;
			}
		}
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<CraftingRecipe> recipe, IFocusGroup focuses) {
		builder.addRecipeArrow().setPosition(61, 19);
	}
}
