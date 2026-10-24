package com.aquamarine.menu;

import com.aquamarine.recipe.ModRecipes;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;

/**
 * Slot de resultado da Forja. Igual ao da bancada, mas calcula as sobras também para as
 * receitas da forja: o vanilla só procura receitas da bancada comum e, sem achar, devolveria
 * todos os ingredientes.
 */
public class AbyssalForgeResultSlot extends ResultSlot {
	private final CraftingContainer craftSlots;
	private final Player player;

	public AbyssalForgeResultSlot(Player player, CraftingContainer craftSlots, Container container, int id, int x, int y) {
		super(player, craftSlots, container, id, x, y);
		this.craftSlots = craftSlots;
		this.player = player;
	}

	private NonNullList<ItemStack> remainingItems(CraftingInput input) {
		if (player.level() instanceof ServerLevel serverLevel) {
			return ModRecipes.findForgeRecipe(serverLevel, input, null)
				.map(recipe -> recipe.value().getRemainingItems(input))
				.orElseGet(() -> CraftingRecipe.defaultCraftingReminder(input));
		}
		return CraftingRecipe.defaultCraftingReminder(input);
	}

	@Override
	public void onTake(Player player, ItemStack carried) {
		this.checkTakeAchievements(carried);
		CraftingInput.Positioned positioned = this.craftSlots.asPositionedCraftInput();
		CraftingInput input = positioned.input();
		NonNullList<ItemStack> remaining = remainingItems(input);

		for (int y = 0; y < input.height(); y++) {
			for (int x = 0; x < input.width(); x++) {
				int slot = x + positioned.left() + (y + positioned.top()) * this.craftSlots.getWidth();
				ItemStack current = this.craftSlots.getItem(slot);
				ItemStack replacement = remaining.get(x + y * input.width());
				if (!current.isEmpty()) {
					this.craftSlots.removeItem(slot, 1);
					current = this.craftSlots.getItem(slot);
				}
				if (!replacement.isEmpty()) {
					if (current.isEmpty()) {
						this.craftSlots.setItem(slot, replacement);
					} else if (ItemStack.isSameItemSameComponents(current, replacement)) {
						replacement.grow(current.getCount());
						this.craftSlots.setItem(slot, replacement);
					} else if (!this.player.getInventory().add(replacement)) {
						this.player.drop(replacement, false, Prediction.PREDICTED);
					}
				}
			}
		}
	}
}
