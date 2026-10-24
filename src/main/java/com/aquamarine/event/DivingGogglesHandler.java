package com.aquamarine.event;

import com.aquamarine.item.ModItems;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Enquanto o jogador usa os Óculos de Mergulho debaixo d'água, recebe visão noturna
 * discreta (sem partículas). O efeito é marcado como "ambient" e curto, para sumir
 * logo que ele sai da água ou tira os óculos.
 */
public final class DivingGogglesHandler {
	private static final int DURATION = 40;

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tick(player);
			}
		});
	}

	private static void tick(ServerPlayer player) {
		boolean active = player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.DIVING_GOGGLES)
			&& player.isUnderWater();
		MobEffectInstance current = player.getEffect(MobEffects.NIGHT_VISION);
		if (active) {
			if (current == null || (current.isAmbient() && current.getDuration() < DURATION - 5)) {
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, DURATION, 0, true, false, false));
			}
		} else if (current != null && current.isAmbient() && current.getDuration() <= DURATION) {
			player.removeEffect(MobEffects.NIGHT_VISION);
		}
	}

	private DivingGogglesHandler() {}
}
