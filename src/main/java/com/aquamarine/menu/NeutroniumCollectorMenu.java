package com.aquamarine.menu;

import com.aquamarine.energy.ModEnergy;
import com.aquamarine.energy.NeutroniumCollectorBlockEntity;
import com.aquamarine.energy.WaterEnergy;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Tela viva do Coletor; valores grandes viajam em duas partes de 15 bits para não estourar o pacote curto vanilla. */
public final class NeutroniumCollectorMenu extends AbstractContainerMenu {
	private static final int OUTPUT = 0;
	private static final int INVENTORY_START = 1;
	private static final int HOTBAR_START = 28;
	private static final int END = 37;
	private static final int DATA_COUNT = 8;
	private final ContainerData data;
	private final ContainerLevelAccess access;

	public NeutroniumCollectorMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(1), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
	}

	public NeutroniumCollectorMenu(int id, Inventory inventory, NeutroniumCollectorBlockEntity be) {
		this(id, inventory, new DustContainer(be), new CollectorData(be), ContainerLevelAccess.create(be.getLevel(), be.getBlockPos()));
	}

	private NeutroniumCollectorMenu(int id, Inventory inventory, Container output, ContainerData data, ContainerLevelAccess access) {
		super(ModMenus.NEUTRONIUM_COLLECTOR, id);
		this.data = data;
		this.access = access;
		addSlot(new Slot(output, 0, 178, 47) {
			@Override public boolean mayPlace(ItemStack stack) { return false; }
		});
		addStandardInventorySlots(inventory, 29, 120);
		addDataSlots(data);
	}

	private static int join(int low, int high) { return (low & 0x7fff) | (high & 0x7fff) << 15; }
	public int energy() { return join(data.get(0), data.get(1)); }
	public int capacity() { return join(data.get(2), data.get(3)); }
	public int progress() { return data.get(4); }
	public int dust() { return data.get(5); }
	public int status() { return data.get(6); }
	public int missingDepth() { return data.get(7); }
	public int progressPercent() { return progress() * 100 / WaterEnergy.COLLECTOR_TICKS; }
	public int ticksRemaining() { return Math.max(0, WaterEnergy.COLLECTOR_TICKS - progress()); }

	@Override public boolean stillValid(Player player) { return stillValid(access, player, ModEnergy.NEUTRONIUM_COLLECTOR); }

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack original = slot.getItem();
		ItemStack copy = original.copy();
		if (index == OUTPUT) {
			if (!moveItemStackTo(original, INVENTORY_START, END, true)) return ItemStack.EMPTY;
		} else if (index < HOTBAR_START) {
			if (!moveItemStackTo(original, HOTBAR_START, END, false)) return ItemStack.EMPTY;
		} else if (!moveItemStackTo(original, INVENTORY_START, HOTBAR_START, false)) return ItemStack.EMPTY;
		if (original.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
		if (original.getCount() == copy.getCount()) return ItemStack.EMPTY;
		slot.onTake(player, original);
		return copy;
	}

	private static final class DustContainer implements Container {
		private final NeutroniumCollectorBlockEntity be;
		DustContainer(NeutroniumCollectorBlockEntity be) { this.be = be; }
		@Override public int getContainerSize() { return 1; }
		@Override public boolean isEmpty() { return be.dust() == 0; }
		@Override public ItemStack getItem(int slot) { return slot == 0 ? be.dustStack(be.dust()) : ItemStack.EMPTY; }
		@Override public ItemStack removeItem(int slot, int amount) { return slot == 0 ? be.takeDust(amount) : ItemStack.EMPTY; }
		@Override public ItemStack removeItemNoUpdate(int slot) { return slot == 0 ? be.takeDust(WaterEnergy.COLLECTOR_MAX_DUST) : ItemStack.EMPTY; }
		@Override public void setItem(int slot, ItemStack stack) {}
		@Override public void setChanged() { be.setChanged(); }
		@Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(be, player); }
		@Override public void clearContent() {}
	}

	private record CollectorData(NeutroniumCollectorBlockEntity be) implements ContainerData {
		private static int part(long value, int shift) { return (int) (value >>> shift & 0x7fff); }
		@Override public int get(int index) {
			return switch (index) {
				case 0 -> part(be.energy.amount, 0);
				case 1 -> part(be.energy.amount, 15);
				case 2 -> part(be.energy.capacity, 0);
				case 3 -> part(be.energy.capacity, 15);
				case 4 -> be.progress();
				case 5 -> be.dust();
				case 6 -> status(be);
				case 7 -> be.getLevel() == null ? 0 : Math.max(0, be.getBlockPos().getY()
					- (be.getLevel().getSeaLevel() - WaterEnergy.COLLECTOR_MIN_DEPTH));
				default -> 0;
			};
		}
		private static int status(NeutroniumCollectorBlockEntity be) {
			if (be.dust() >= WaterEnergy.COLLECTOR_MAX_DUST) return 5;
			if (be.spotReason() != NeutroniumCollectorBlockEntity.SPOT_OK) return 1 + be.spotReason();
			return be.energy.amount < WaterEnergy.COLLECTOR_USE ? 1 : 0;
		}
		@Override public void set(int index, int value) {}
		@Override public int getCount() { return DATA_COUNT; }
	}
}
