package com.aquamarine.harbor;

import com.aquamarine.Aquamarine;
import com.aquamarine.mixin.AbstractVillagerInvoker;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;

/**
 * Comércio dos Mergulhadores por reputação (docs/08-fase3.md, seção 8). Cada Mergulhador já nasce com as
 * trocas de todos os níveis da profissão; o nível de cada oferta fica guardado nele ({@link #OFFER_LEVELS}).
 * Quem negocia só compra até o nível que a própria reputação abre: as ofertas acima chegam ao cliente
 * esgotadas, e o servidor recusa a troca mesmo assim. A tela mostra o nível e a barra de reputação do
 * jogador no lugar dos do aldeão. Ligado ao jogo pelos mixins de aldeão, menu e jogador.
 */
public final class HarborTrades {
	/** Nível de comércio de cada oferta de um Mergulhador, na mesma ordem das ofertas. */
	public static final AttachmentType<List<Integer>> OFFER_LEVELS = AttachmentRegistry.create(Aquamarine.id("offer_levels"),
		builder -> builder.persistent(Codec.INT.listOf()));

	/** Mergulhador: aldeão com uma das profissões do Porto. */
	public static boolean isDiver(Villager villager) {
		return villager.getVillagerData().profession().unwrapKey()
			.map(key -> key.identifier().getNamespace().equals(Aquamarine.MOD_ID)).orElse(false);
	}

	/**
	 * Monta as trocas de todos os níveis, se ainda não foram montadas (ou se vieram de uma versão sem os
	 * níveis guardados). Devolve true se montou.
	 */
	public static boolean ensureTrades(Villager villager, ServerLevel level, MerchantOffers offers) {
		List<Integer> levels = villager.getAttached(OFFER_LEVELS);
		if (levels != null && !offers.isEmpty() && levels.size() == offers.size()) {
			return false;
		}
		offers.clear();
		List<Integer> built = new ArrayList<>();
		VillagerProfession profession = villager.getVillagerData().profession().value();
		for (int tier = 1; tier <= HarborReputation.MAX_TIER; tier++) {
			ResourceKey<TradeSet> trades = profession.getTrades(tier);
			if (trades == null) {
				continue;
			}
			int before = offers.size();
			((AbstractVillagerInvoker) villager).aquamarine$addOffersFromTradeSet(level, offers, trades);
			for (int k = before; k < offers.size(); k++) {
				built.add(tier);
			}
		}
		villager.setAttached(OFFER_LEVELS, List.copyOf(built));
		return true;
	}

	/** Nível de comércio de uma oferta do Mergulhador (1 se não souber). */
	public static int offerTier(Villager villager, MerchantOffer offer) {
		return levelAt(villager.getAttached(OFFER_LEVELS), villager.getOffers().indexOf(offer));
	}

	private static int levelAt(List<Integer> levels, int index) {
		return levels == null || index < 0 || index >= levels.size() ? 1 : levels.get(index);
	}

	public static boolean isLocked(Villager villager, Player player, MerchantOffer offer) {
		ServerLevel level = (ServerLevel) villager.level();
		return offerTier(villager, offer) > HarborReputation.get(level.getServer()).tier(player.getUUID());
	}

	/** Manda ao jogador as ofertas do Mergulhador como ele as vê: acima da reputação, esgotadas. */
	public static void sendOffers(ServerPlayer player, Villager villager, int containerId, boolean canRestock) {
		MerchantOffers real = villager.getOffers();
		List<Integer> levels = villager.getAttached(OFFER_LEVELS);
		int points = HarborReputation.get(player.level().getServer()).points(player.getUUID());
		int tier = HarborReputation.tier(points);
		MerchantOffers shown = new MerchantOffers();
		for (int k = 0; k < real.size(); k++) {
			MerchantOffer offer = real.get(k);
			if (levelAt(levels, k) > tier) {
				offer = offer.copy();
				offer.setToOutOfStock();
			}
			shown.add(offer);
		}
		player.connection.send(new ClientboundMerchantOffersPacket(containerId, shown, tier, points, true, canRestock));
	}

	/** Depois de cada troca: a XP da oferta vira reputação; se abriu um nível, reenvia as ofertas. */
	public static void onTrade(Villager villager, ServerPlayer player, MerchantOffer offer) {
		HarborReputation reputation = HarborReputation.get(player.level().getServer());
		int before = reputation.tier(player.getUUID());
		reputation.add(player.getUUID(), offer.getXp());
		int after = reputation.tier(player.getUUID());
		if (after > before) {
			player.sendSystemMessage(Component.translatable("message.aquamarine.harbor.tier_up",
				Component.translatable("merchant.level." + after)), true);
			if (player.containerMenu instanceof MerchantMenu menu) {
				sendOffers(player, villager, menu.containerId, villager.canRestock());
			}
		}
	}

	private HarborTrades() {}
}
