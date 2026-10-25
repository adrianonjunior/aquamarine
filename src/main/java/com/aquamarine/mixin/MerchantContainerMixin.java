package com.aquamarine.mixin;

import com.aquamarine.harbor.HarborTrades;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;

/** O servidor recusa a troca de um Mergulhador acima do nível que a reputação de quem negocia abre. */
@Mixin(MerchantContainer.class)
public abstract class MerchantContainerMixin {
	@Shadow
	@Final
	private Merchant merchant;

	@Shadow
	private @Nullable MerchantOffer activeOffer;

	@Inject(method = "updateSellItem", at = @At("TAIL"))
	private void aquamarine$lockedByReputation(CallbackInfo ci) {
		if (activeOffer != null && merchant instanceof Villager villager && !villager.level().isClientSide()
			&& HarborTrades.isDiver(villager) && merchant.getTradingPlayer() != null
			&& HarborTrades.isLocked(villager, merchant.getTradingPlayer(), activeOffer)) {
			((MerchantContainer) (Object) this).setItem(2, ItemStack.EMPTY);
		}
	}
}
