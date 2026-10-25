package com.aquamarine.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Deixa o Porto montar as trocas de todos os níveis de um Mergulhador ({@code HarborTrades}). */
@Mixin(AbstractVillager.class)
public interface AbstractVillagerInvoker {
	@Invoker("addOffersFromTradeSet")
	void aquamarine$addOffersFromTradeSet(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> trades);
}
