package com.aquamarine.client;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.HarpoonEffects;
import com.aquamarine.item.TideThrusterItem;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Teclas da Fase 3 (configuráveis em Controles, categoria Aquamarine): R troca a ponta da Aljava;
 * V segura para mirar o Propulsor de Maré e, ao soltar, pede o impulso. O servidor valida tudo.
 */
public final class AquamarineKeys {
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Aquamarine.id("main"));
	public static final KeyMapping QUIVER = KeyMappingHelper.registerKeyMapping(
		new KeyMapping("key.aquamarine.quiver", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, CATEGORY));
	public static final KeyMapping THRUSTER = KeyMappingHelper.registerKeyMapping(
		new KeyMapping("key.aquamarine.thruster", InputConstants.Type.KEYBOARD, InputConstants.KEY_V, CATEGORY));

	private static boolean aiming;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(AquamarineKeys::tick);
	}

	private static void tick(Minecraft mc) {
		if (mc.player == null) {
			aiming = false;
			return;
		}
		while (QUIVER.consumeClick()) {
			ClientPlayNetworking.send(new HarpoonEffects.CyclePayload());
		}
		boolean down = THRUSTER.isDown() && mc.player.getOffhandItem().getItem() instanceof TideThrusterItem;
		if (down && !aiming) {
			int charges = TideThrusterItem.charges(mc.player.getOffhandItem());
			mc.player.sendOverlayMessage(Component.translatable("message.aquamarine.thruster.aiming", charges));
		} else if (!down && aiming) {
			ClientPlayNetworking.send(new TideThrusterItem.FirePayload());
		}
		aiming = down;
	}

	private AquamarineKeys() {}
}
