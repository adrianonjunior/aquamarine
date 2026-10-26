package com.aquamarine.entity.boss;

import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.event.KrakenWorldData;
import com.aquamarine.item.ModItems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Recompensas da vitória (docs/02-kraken.md, "Recompensas"): cada jogador que lutou recebe
 * Olho do Kraken, Tinta Abissal, 1.000 de XP e um Troféu (o Dourado, no modo abissal); o Coração
 * só na primeira vitória de cada jogador. Entregue direto ao jogador: o Kraken afunda e nada
 * se perde no fundo do mar.
 */
public final class KrakenRewards {
	public static final int XP = 1000;
	public static final int EYES = 1;
	public static final int INK_MIN = 2;
	public static final int INK_MAX = 4;

	/** Itens de um jogador: {@code firstWin} acrescenta o Coração e o Molde de Neutrônio, {@code abyssal} o Troféu Dourado. */
	public static List<ItemStack> plan(boolean firstWin, boolean abyssal, RandomSource random) {
		List<ItemStack> items = new ArrayList<>();
		items.add(new ItemStack(ModItems.KRAKEN_EYE, EYES));
		items.add(new ItemStack(ModItems.ABYSSAL_INK, INK_MIN + random.nextInt(INK_MAX - INK_MIN + 1)));
		items.add(new ItemStack(abyssal ? ModItems.GOLDEN_ABYSS_TROPHY : ModItems.KRAKEN_TROPHY));
		if (firstWin) {
			items.add(new ItemStack(ModItems.KRAKEN_HEART));
			items.add(new ItemStack(ModItems.NEUTRONIUM_UPGRADE_SMITHING_TEMPLATE));  // Fase 4: o Molde de Neutrônio
		}
		return items;
	}

	/** Entrega as recompensas a cada jogador de {@code participants}. */
	public static void grant(ServerLevel level, Collection<ServerPlayer> participants) {
		KrakenWorldData data = KrakenWorldData.get(level.getServer());
		boolean abyssal = AquamarineConfig.get().isAbyssal();
		for (ServerPlayer player : participants) {
			boolean firstWin = data.markHeartReceived(player.getUUID());
			for (ItemStack stack : plan(firstWin, abyssal, level.getRandom())) {
				player.getInventory().add(stack);
				if (!stack.isEmpty()) {
					level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack));
				}
			}
			player.giveExperiencePoints(XP);
			if (firstWin) {
				player.sendSystemMessage(Component.translatable("message.aquamarine.kraken.heart").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
			}
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0F, 1.0F);
		}
	}

	private KrakenRewards() {}
}
