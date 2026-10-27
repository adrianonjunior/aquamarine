package com.aquamarine.client.compat.jei;

import com.aquamarine.Aquamarine;
import com.aquamarine.client.EnchantmentDescriptions;
import com.aquamarine.client.ItemDescriptions;
import com.aquamarine.client.SyncedRecipes;
import com.aquamarine.item.ModItems;
import com.aquamarine.recipe.ModRecipes;

import java.util.List;
import java.util.Optional;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.stream.IntStream;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Integração com o JEI (entrypoint {@code jei_mod_plugin}; o JEI é opcional). Mostra a
 * categoria "Forja Abissal" com as receitas exclusivas, a Forja como estação também da bancada
 * comum (ela aceita essas receitas) e a descrição de cada item na página de informação.
 * <p>
 * Desde o 1.21.2 o cliente não recebe as receitas do servidor: no mundo próprio elas vêm do servidor
 * integrado; num servidor ou num mundo em LAN de outra pessoa, das que o mod manda ao entrar
 * ({@link SyncedRecipes}), em todas as categorias (bancada, fornalhas, ferraria, cortador e Forja).
 * A categoria "Como obter" ({@link ObtainCategory}) mostra a origem do que não tem receita.
 */
public class AquamarineJeiPlugin implements IModPlugin {
	public static final IRecipeHolderType<CraftingRecipe> ABYSSAL_FORGING = IRecipeHolderType.create(ModRecipes.ABYSSAL_FORGING);
	public static final IRecipeType<ObtainRecipe> OBTAIN = IRecipeType.create(Aquamarine.id("obtain"), ObtainRecipe.class);

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
		registration.addRecipeCategories(new ObtainCategory(OBTAIN, registration.getJeiHelpers().getGuiHelper()));
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
		} else if (Minecraft.getInstance().level != null) {
			// Convidado: as receitas do mod que o servidor mandou, cada uma na categoria do JEI.
			List<RecipeHolder<?>> synced = SyncedRecipes.decode(Minecraft.getInstance().level.registryAccess());
			addSynced(registration, RecipeTypes.CRAFTING, RecipeType.CRAFTING, synced);
			addSynced(registration, RecipeTypes.SMELTING, RecipeType.SMELTING, synced);
			addSynced(registration, RecipeTypes.BLASTING, RecipeType.BLASTING, synced);
			addSynced(registration, RecipeTypes.SMOKING, RecipeType.SMOKING, synced);
			addSynced(registration, RecipeTypes.CAMPFIRE_COOKING, RecipeType.CAMPFIRE_COOKING, synced);
			addSynced(registration, RecipeTypes.STONECUTTING, RecipeType.STONECUTTING, synced);
			addSynced(registration, RecipeTypes.SMITHING, RecipeType.SMITHING, synced);
			addSynced(registration, ABYSSAL_FORGING, ModRecipes.ABYSSAL_FORGING, synced);
		}
		registration.addRecipes(OBTAIN, ObtainRecipe.all());
		for (Item item : ModItems.tabOrder()) {
			List<Component> lines = ItemDescriptions.lines(item);
			if (!lines.isEmpty()) {
				registration.addIngredientInfo(item, lines.toArray(Component[]::new));
			}
		}
		// 6F2: os livros de cada encantamento do mod, em todos os níveis, com a descrição na página de informação.
		if (Minecraft.getInstance().level != null) {
			Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements()
				.filter(holder -> EnchantmentDescriptions.line(holder) != null)
				.forEach(holder -> registration.addIngredientInfo(
					IntStream.rangeClosed(1, holder.value().getMaxLevel())
						.mapToObj(level -> EnchantmentHelper.createBook(new EnchantmentInstance(holder, level))).toList(),
					VanillaTypes.ITEM_STACK, EnchantmentDescriptions.line(holder)));
		}
	}

	@SuppressWarnings("unchecked")
	private static <R extends Recipe<?>> void addSynced(IRecipeRegistration registration, IRecipeHolderType<R> jeiType,
		RecipeType<?> type, List<RecipeHolder<?>> synced) {
		List<RecipeHolder<R>> recipes = synced.stream().filter(holder -> holder.value().getType() == type)
			.map(holder -> (RecipeHolder<R>) holder).toList();
		if (!recipes.isEmpty()) {
			registration.addRecipes(jeiType, recipes);
		}
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(ABYSSAL_FORGING, ModItems.ABYSSAL_FORGE);
		registration.addCraftingStation(RecipeTypes.CRAFTING, ModItems.ABYSSAL_FORGE);
	}
}
