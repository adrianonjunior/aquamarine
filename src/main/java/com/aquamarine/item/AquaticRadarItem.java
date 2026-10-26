package com.aquamarine.item;

import com.aquamarine.entity.ModVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Radar Aquático (Fase 6, 6G2; docs/11-fase6.md): ligar gasta uma Bobina Voltaica (no criativo não gasta) e o deixa
 * ativo por 60 s. Enquanto isso, o HUD do cliente ({@code RadarHud}) mostra os bichos do mod por perto e a direção do
 * Porto mais próximo, achado uma vez ao ligar. Não substitui a Bússola de Maré nem o Mapa de Nereia.
 */
public class AquaticRadarItem extends Item {
	public static final int ACTIVE_TICKS = 60 * 20;
	/** Raio da busca pelo Porto, em pedaços de mundo. */
	public static final int HARBOR_SEARCH = 48;

	public AquaticRadarItem(Properties properties) {
		super(properties);
	}

	/** Até que tick (tempo do jogo) o radar fica ligado. */
	public static long activeUntil(ItemStack stack) {
		return stack.getOrDefault(ModComponents.RADAR_UNTIL, 0L);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		if (!player.getAbilities().instabuild && !consumeCoil(player.getInventory())) {
			player.sendOverlayMessage(Component.translatable("message.aquamarine.radar.no_coil"));
			return InteractionResult.FAIL;
		}
		stack.set(ModComponents.RADAR_UNTIL, server.getGameTime() + ACTIVE_TICKS);
		BlockPos harbor = server.findNearestMapStructure(ModVillagers.HARBORS, player.blockPosition(), HARBOR_SEARCH, false);
		if (harbor != null) {
			stack.set(ModComponents.RADAR_TARGET, harbor);
		} else {
			stack.remove(ModComponents.RADAR_TARGET);
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 0.6F, 1.6F);
		player.getCooldowns().addCooldown(stack, 40);
		return InteractionResult.SUCCESS;
	}

	private static boolean consumeCoil(Inventory inventory) {
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.is(ModItems.VOLTAIC_COIL)) {
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}
}
