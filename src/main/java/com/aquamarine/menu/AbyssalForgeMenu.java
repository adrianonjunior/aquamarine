package com.aquamarine.menu;

import com.aquamarine.block.ModBlocks;
import com.aquamarine.recipe.ModRecipes;

import java.util.List;
import java.util.Optional;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.jspecify.annotations.Nullable;

/**
 * Grade 3x3 da Forja Abissal, no mesmo layout da bancada comum. Aceita as receitas
 * {@code aquamarine:abyssal_forging} e também as da bancada comum.
 */
public class AbyssalForgeMenu extends AbstractCraftingMenu {
	private static final int RESULT_SLOT = 0;
	private static final int GRID_START = 1;
	private static final int GRID_END = 10;
	private static final int INV_START = 10;
	private static final int HOTBAR_START = 37;
	private static final int INV_END = 46;

	private final ContainerLevelAccess access;
	private final Player player;
	private boolean placingRecipe;

	public AbyssalForgeMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, ContainerLevelAccess.NULL);
	}

	public AbyssalForgeMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
		super(ModMenus.ABYSSAL_FORGE, containerId, 3, 3);
		this.access = access;
		this.player = inventory.player;
		this.addSlot(new AbyssalForgeResultSlot(this.player, this.craftSlots, this.resultSlots, RESULT_SLOT, 124, 35));
		this.addCraftingGridSlots(30, 17);
		this.addStandardInventorySlots(inventory, 8, 84);
	}

	private void updateResult(ServerLevel level, @Nullable RecipeHolder<CraftingRecipe> hint) {
		CraftingInput input = this.craftSlots.asCraftInput();
		ServerPlayer serverPlayer = (ServerPlayer) this.player;
		ItemStack result = ItemStack.EMPTY;
		Optional<RecipeHolder<CraftingRecipe>> recipe = ModRecipes.findForgeRecipe(level, input, hint);
		if (recipe.isPresent() && this.resultSlots.setRecipeUsed(serverPlayer, recipe.get())) {
			ItemStack assembled = recipe.get().value().assemble(input);
			if (assembled.isItemEnabled(level.enabledFeatures())) {
				result = assembled;
			}
		}
		this.resultSlots.setItem(RESULT_SLOT, result);
		this.setRemoteSlot(RESULT_SLOT, result);
		serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), RESULT_SLOT, result));
	}

	@Override
	public void slotsChanged(Container container) {
		if (!this.placingRecipe) {
			this.access.execute((level, pos) -> {
				if (level instanceof ServerLevel serverLevel) {
					updateResult(serverLevel, null);
				}
			});
		}
	}

	@Override
	protected void beginPlacingRecipe() {
		this.placingRecipe = true;
	}

	@Override
	protected void finishPlacingRecipe(ServerLevel level, RecipeHolder<CraftingRecipe> recipe) {
		this.placingRecipe = false;
		updateResult(level, recipe);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.access.execute((level, pos) -> this.clearContainer(player, this.craftSlots));
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(this.access, player, ModBlocks.ABYSSAL_FORGE);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		ItemStack clicked = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			clicked = stack.copy();
			if (slotIndex == RESULT_SLOT) {
				stack.getItem().onCraftedBy(stack, player);
				if (!this.moveItemStackTo(stack, INV_START, INV_END, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(stack, clicked);
			} else if (slotIndex >= INV_START && slotIndex < INV_END) {
				if (!this.moveItemStackTo(stack, GRID_START, GRID_END, false)) {
					if (slotIndex < HOTBAR_START) {
						if (!this.moveItemStackTo(stack, HOTBAR_START, INV_END, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.moveItemStackTo(stack, INV_START, HOTBAR_START, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(stack, INV_START, INV_END, false)) {
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (stack.getCount() == clicked.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, stack);
			if (slotIndex == RESULT_SLOT) {
				player.drop(stack, false, Prediction.PREDICTED);
			}
		}
		return clicked;
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack carried, Slot target) {
		return target.container != this.resultSlots && super.canTakeItemForPickAll(carried, target);
	}

	@Override
	public Slot getResultSlot() {
		return this.slots.get(RESULT_SLOT);
	}

	@Override
	public List<Slot> getInputGridSlots() {
		return this.slots.subList(GRID_START, GRID_END);
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	protected Player owner() {
		return this.player;
	}
}
