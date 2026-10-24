package com.aquamarine.recipe;

import com.mojang.serialization.MapCodec;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

/**
 * Receita da Forja Abissal. O JSON é igual ao de {@code minecraft:crafting_shaped}, só muda o
 * {@code type} para {@code aquamarine:abyssal_forging}. Por ter tipo próprio, a bancada comum
 * não a encontra; só a Forja.
 */
public record AbyssalForgingRecipe(ShapedRecipe shaped) implements CraftingRecipe {
	public static final MapCodec<AbyssalForgingRecipe> MAP_CODEC =
		ShapedRecipe.MAP_CODEC.xmap(AbyssalForgingRecipe::new, AbyssalForgingRecipe::shaped);
	public static final StreamCodec<RegistryFriendlyByteBuf, AbyssalForgingRecipe> STREAM_CODEC =
		ShapedRecipe.STREAM_CODEC.map(AbyssalForgingRecipe::new, AbyssalForgingRecipe::shaped);
	public static final RecipeSerializer<AbyssalForgingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	@Override
	public boolean matches(CraftingInput input, Level level) {
		return shaped.matches(input, level);
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		return shaped.assemble(input);
	}

	@Override
	public boolean showNotification() {
		return shaped.showNotification();
	}

	@Override
	public String group() {
		return shaped.group();
	}

	@Override
	public CraftingBookCategory category() {
		return shaped.category();
	}

	@Override
	public PlacementInfo placementInfo() {
		return shaped.placementInfo();
	}

	@Override
	public List<RecipeDisplay> display() {
		return shaped.display();
	}

	@Override
	public RecipeSerializer<AbyssalForgingRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<CraftingRecipe> getType() {
		return ModRecipes.ABYSSAL_FORGING;
	}
}
