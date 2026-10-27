package com.aquamarine.client.manual;

import com.aquamarine.client.SyncedRecipes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

/**
 * Receitas que o Manual desenha: do servidor integrado (dono do mundo) ou das que o servidor mandou ao entrar
 * ({@link SyncedRecipes}, convidados de LAN e servidores). Cada uma vira uma grade 3x3 já resolvida em itens.
 */
final class ManualRecipes {
	/** Grade de 9 casas (cada uma com as opções que ela aceita), o resultado e a estação da receita. */
	record View(List<List<ItemStack>> grid, ItemStack result, ItemStack station, boolean shapeless) {}

	private final Map<String, Optional<View>> cache = new HashMap<>();
	private List<RecipeHolder<?>> synced;

	Optional<View> get(String id) {
		return cache.computeIfAbsent(id, this::resolve);
	}

	private Optional<View> resolve(String id) {
		Minecraft mc = Minecraft.getInstance();
		Identifier key = Identifier.tryParse(id);
		if (key == null || mc.level == null) {
			return Optional.empty();
		}
		ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, key);
		MinecraftServer server = mc.getSingleplayerServer();
		Optional<RecipeHolder<?>> holder;
		if (server != null) {
			holder = server.getRecipeManager().byKey(recipeKey);
		} else {
			if (synced == null) {
				synced = SyncedRecipes.decode(mc.level.registryAccess());
			}
			holder = synced.stream().filter(h -> h.id().equals(recipeKey)).findFirst();
		}
		if (holder.isEmpty() || holder.get().value().display().isEmpty()) {
			return Optional.empty();
		}
		ContextMap context = SlotDisplayContext.fromLevel(mc.level);
		RecipeDisplay display = holder.get().value().display().getFirst();
		List<List<ItemStack>> grid = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			grid.add(List.of());
		}
		boolean shapeless = false;
		if (display instanceof ShapedCraftingRecipeDisplay shaped) {
			// Receita menor que 3x3 fica centrada na grade.
			int dx = (3 - shaped.width()) / 2, dy = (3 - shaped.height()) / 2;
			for (int row = 0; row < shaped.height(); row++) {
				for (int col = 0; col < shaped.width(); col++) {
					grid.set((row + dy) * 3 + col + dx, stacks(shaped.ingredients().get(row * shaped.width() + col), context));
				}
			}
		} else if (display instanceof ShapelessCraftingRecipeDisplay shapelessDisplay) {
			shapeless = true;
			List<SlotDisplay> ingredients = shapelessDisplay.ingredients();
			for (int i = 0; i < ingredients.size() && i < 9; i++) {
				grid.set(i, stacks(ingredients.get(i), context));
			}
		} else {
			return Optional.empty();
		}
		ItemStack station = display.craftingStation().resolveForFirstStack(context);
		return Optional.of(new View(grid, display.result().resolveForFirstStack(context), station, shapeless));
	}

	private static List<ItemStack> stacks(SlotDisplay slot, ContextMap context) {
		return slot.resolveForStacks(context).stream().filter(stack -> !stack.isEmpty()).toList();
	}
}
