package com.aquamarine.mixin;

import com.aquamarine.harbor.HarborTrades;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.MerchantOffer;

/** Cada troca com um Mergulhador soma reputação no Porto a quem negociou. */
@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {
	@Inject(method = "notifyTrade", at = @At("TAIL"))
	private void aquamarine$harborReputation(MerchantOffer offer, CallbackInfo ci) {
		if ((Object) this instanceof Villager villager && HarborTrades.isDiver(villager)
			&& villager.getTradingPlayer() instanceof ServerPlayer player) {
			HarborTrades.onTrade(villager, player, offer);
		}
	}
}
