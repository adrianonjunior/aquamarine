package com.aquamarine.client;

import com.aquamarine.Aquamarine;
import com.aquamarine.event.SummonEffectPayload;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

/**
 * Efeitos de tela da invocação do Kraken (docs/03-invocacao-e-chat.md): silêncio do oceano
 * (0 s), tremor de tela (5 s) e céu escurecendo no fim do aviso falso. O servidor manda
 * {@link SummonEffectPayload}; aqui só há contadores de ticks. A parte visual fica em
 * {@code CameraMixin} (tremor), {@code SoundEngineMixin} (silêncio) e no elemento de HUD abaixo.
 */
public final class SummonEffects {
	private static final int FADE_TICKS = 40;
	private static final int DARKEN_MAX_ALPHA = 140;
	private static final float SHAKE_DEGREES = 1.2F;

	private static int shakeTicks;
	private static int shakeTotal = 1;
	private static int silenceTicks;
	private static int darkenTicks;
	private static int darkenTotal = 1;
	private static int clientTicks;

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(SummonEffectPayload.TYPE, (payload, context) -> {
			switch (payload.effect()) {
				case SummonEffectPayload.SHAKE -> shakeTicks = shakeTotal = Math.max(1, payload.ticks());
				case SummonEffectPayload.SILENCE -> {
					silenceTicks = payload.ticks();
					Minecraft.getInstance().getSoundManager().stop(null, SoundSource.AMBIENT);
				}
				case SummonEffectPayload.DARKEN -> darkenTicks = darkenTotal = Math.max(1, payload.ticks());
				default -> { }
			}
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			clientTicks++;
			shakeTicks = Math.max(0, shakeTicks - 1);
			silenceTicks = Math.max(0, silenceTicks - 1);
			darkenTicks = Math.max(0, darkenTicks - 1);
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> shakeTicks = silenceTicks = darkenTicks = 0);
		HudElementRegistry.addFirst(Aquamarine.id("summon_darkness"), SummonEffects::extractDarkness);
	}

	/** Os sons ambientes do oceano estão silenciados (a invocação começou e o Kraken ainda não despertou). */
	public static boolean silenced() {
		return silenceTicks > 0;
	}

	/** Tremor acontecendo (para testes). */
	public static boolean shaking() {
		return shakeTicks > 0;
	}

	public static boolean darkening() {
		return darkenTicks > 0;
	}

	/** Desvio de rotação da câmera em graus (yaw, pitch); zero fora do tremor. Cresce até o despertar. */
	public static float[] shakeOffset(float partialTick) {
		if (shakeTicks <= 0) {
			return new float[] {0, 0};
		}
		float strength = SHAKE_DEGREES * (1.0F - 0.6F * shakeTicks / shakeTotal);
		float t = clientTicks + partialTick;
		return new float[] {Mth.sin(t * 2.7F) * strength, Mth.cos(t * 3.9F) * strength * 0.6F};
	}

	private static void extractDarkness(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker delta) {
		if (darkenTicks <= 0) {
			return;
		}
		int elapsed = darkenTotal - darkenTicks;
		float fade = Math.min(1.0F, Math.min(elapsed, darkenTicks) / (float) FADE_TICKS);
		int alpha = (int) (DARKEN_MAX_ALPHA * Mth.clamp(fade, 0.0F, 1.0F));
		Minecraft minecraft = Minecraft.getInstance();
		graphics.fill(0, 0, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight(), (alpha << 24) | 0x040818);
	}

	private SummonEffects() {}
}
