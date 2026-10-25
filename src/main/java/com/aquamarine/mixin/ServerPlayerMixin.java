package com.aquamarine.mixin;

import com.aquamarine.harbor.HarborTrades;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;

/** Ofertas de um Mergulhador saem filtradas pela reputação de quem negocia ({@link HarborTrades#sendOffers}). */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	@Inject(method = "sendMerchantOffers", at = @At("HEAD"), cancellable = true)
	private void aquamarine$diverOffers(int containerId, MerchantOffers offers, int merchantLevel, int merchantXp,
		boolean showProgressBar, boolean canRestock, CallbackInfo ci) {
		ServerPlayer self = (ServerPlayer) (Object) this;
		if (self.containerMenu instanceof MerchantMenu menu && menu.containerId == containerId
			&& ((MerchantMenuAccessor) menu).aquamarine$trader() instanceof Villager villager && HarborTrades.isDiver(villager)) {
			HarborTrades.sendOffers(self, villager, containerId, canRestock);
			ci.cancel();
		}
	}
}
