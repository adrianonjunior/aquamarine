package com.aquamarine.mixin;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.WorkstationBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bigorna de Maré (6C2): a tela da bigorna do jogo vale perto dela e, no equipamento do mod, reparar e renomear não
 * aumentam o custo de reparo da peça. Ela não está na tag de bigornas, então não se desgasta com o uso.
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
	protected AnvilMenuMixin(MenuType<?> type, int id, Inventory inventory, ContainerLevelAccess access,
		ItemCombinerMenuSlotDefinition definition) {
		super(type, id, inventory, access, definition);
	}

	@Inject(method = "isValidBlock", at = @At("RETURN"), cancellable = true)
	private void aquamarine$tideAnvil(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof WorkstationBlock station && station.kind() == WorkstationBlock.Kind.ANVIL) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "createResult", at = @At("TAIL"))
	private void aquamarine$noCostGrowth(CallbackInfo ci) {
		if (!WorkstationBlock.is(access, WorkstationBlock.Kind.ANVIL)) {
			return;
		}
		ItemStack input = inputSlots.getItem(0);
		ItemStack result = resultSlots.getItem(0);
		if (!result.isEmpty() && Aquamarine.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(input.getItem()).getNamespace())) {
			result.set(DataComponents.REPAIR_COST, input.getOrDefault(DataComponents.REPAIR_COST, 0));
		}
	}
}
