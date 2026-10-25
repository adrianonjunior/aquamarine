package com.aquamarine.mixin;

import com.aquamarine.harbor.HarborTrades;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;

/** Mergulhadores do Porto: todas as trocas de uma vez, abertas pela reputação de quem negocia. */
@Mixin(Villager.class)
public abstract class VillagerMixin {
	@Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
	private void aquamarine$diverTrades(ServerLevel level, CallbackInfo ci) {
		Villager self = (Villager) (Object) this;
		if (HarborTrades.isDiver(self)) {
			HarborTrades.ensureTrades(self, level, self.getOffers());
			ci.cancel();
		}
	}

	@Inject(method = "startTrading", at = @At("HEAD"))
	private void aquamarine$diverStartTrading(Player player, CallbackInfo ci) {
		Villager self = (Villager) (Object) this;
		if (HarborTrades.isDiver(self) && self.level() instanceof ServerLevel level) {
			HarborTrades.ensureTrades(self, level, self.getOffers());
		}
	}
}
