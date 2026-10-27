package com.aquamarine.client.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

/**
 * Categoria "Como obter" no JEI: o que entra à esquerda, o que sai à direita (até seis por linha) e, embaixo, de onde
 * vem. Cobre o que não sai de nenhuma receita e o que tem origem especial, como a têmpera do Neutrônio.
 */
public class ObtainCategory extends AbstractRecipeCategory<ObtainRecipe> {
	private static final int WIDTH = 160;
	private static final int HEIGHT = 128;
	private static final int PER_ROW = 6;

	public ObtainCategory(IRecipeType<ObtainRecipe> type, IGuiHelper guiHelper) {
		super(type, Component.translatable("jei.aquamarine.obtain"), guiHelper.createDrawableItemLike(Items.SPYGLASS), WIDTH, HEIGHT);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ObtainRecipe recipe, IFocusGroup focuses) {
		for (int i = 0; i < recipe.inputs().size(); i++) {
			builder.addInputSlot(1, 1 + 19 * i).setStandardSlotBackground().addItemStack(recipe.inputs().get(i));
		}
		int x0 = recipe.inputs().isEmpty() ? 1 : 47;
		for (int i = 0; i < recipe.outputs().size(); i++) {
			builder.addOutputSlot(x0 + 19 * (i % PER_ROW), 1 + 19 * (i / PER_ROW)).setOutputSlotBackground()
				.addItemStack(recipe.outputs().get(i));
		}
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, ObtainRecipe recipe, IFocusGroup focuses) {
		if (!recipe.inputs().isEmpty()) {
			builder.addRecipeArrow().setPosition(21, 1);
		}
		int rows = Math.max(recipe.inputs().size(), (recipe.outputs().size() + PER_ROW - 1) / PER_ROW);
		int top = rows * 19 + 5;
		builder.addText(recipe.text(), WIDTH, HEIGHT - top).setPosition(0, top).setColor(0xFF3F3F3F).setLineSpacing(1);
	}
}
