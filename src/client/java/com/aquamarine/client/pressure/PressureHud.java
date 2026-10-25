package com.aquamarine.client.pressure;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import com.aquamarine.pressure.PressureManager;
import com.aquamarine.pressure.PressurePayload;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

/**
 * Efeito visual e sonoro da Pressão para o próprio jogador (docs/02-kraken.md): vinheta azul a
 * partir do nível 2, bordas escuras e pulsantes no nível 4, e batimentos que aceleram do nível 3
 * em diante. O nível vem do servidor por {@link PressurePayload}.
 */
public final class PressureHud {
	private static final int STEPS = 24;
	private static int level;
	/** Intensidade suavizada de 0 a 1, para a vinheta entrar e sair devagar. */
	private static float shown;
	private static int ticks;

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(PressurePayload.TYPE, (payload, context) -> {
			int old = level;
			level = payload.level();
			if (level > old && level >= 2) {
				heartbeat(0.5F + 0.15F * level);
			}
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			level = 0;
			shown = 0;
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ticks++;
			shown += (targetIntensity(level) - shown) * 0.08F;
			if (level >= 3 && ticks % (level == 3 ? 40 : 24) == 0) {
				heartbeat(0.7F);
			}
		});
		HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, Aquamarine.id("pressure"), PressureHud::extract);
	}

	public static int level() {
		return level;
	}

	private static float targetIntensity(int level) {
		return switch (level) {
			case 2 -> 0.35F;
			case 3 -> 0.6F;
			case 4 -> 1.0F;
			default -> 0.0F;
		};
	}

	private static void heartbeat(float volume) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.level != null) {
			mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.WARDEN_HEARTBEAT,
				SoundSource.PLAYERS, volume, 0.8F, false);
		}
	}

	private static void extract(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker delta) {
		extractGauge(graphics);
		if (shown < 0.01F) {
			return;
		}
		int w = graphics.guiWidth();
		int h = graphics.guiHeight();
		boolean dark = level >= 4;
		float pulse = dark ? 0.85F + 0.15F * Mth.sin(ticks * 0.3F) : 1.0F;
		int rgb = dark ? 0x02060C : 0x0A3F7A;
		float strength = shown * pulse * (dark ? 0.85F : 0.55F);
		int band = Math.max(w, h) / 5;
		for (int i = 0; i < STEPS; i++) {
			float t = 1.0F - i / (float) STEPS;
			int color = ((int) (255 * strength * t * t) << 24) | rgb;
			int x0 = band * i / STEPS;
			int x1 = band * (i + 1) / STEPS;
			graphics.fill(x0, 0, x1, h, color);
			graphics.fill(w - x1, 0, w - x0, h, color);
			graphics.fill(x1, x0, w - x1, x1, color);
			graphics.fill(x1, h - x1, w - x1, h - x0, color);
		}
	}

	/** Profundidade abaixo do nível do mar, em blocos, arredondada para baixo (0 acima dele). */
	public static int depth(Minecraft mc) {
		return mc.player == null || mc.level == null ? 0 : Math.max(0, (int) (PressureManager.seaLevel(mc.level) - mc.player.getY()));
	}

	/** Medidor de Profundidade (docs/08-fase3.md, seção 1): com o item em qualquer mão, mostra profundidade e Pressão. */
	private static void extractGauge(GuiGraphicsExtractor graphics) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || !(mc.player.getMainHandItem().is(ModItems.DEPTH_GAUGE) || mc.player.getOffhandItem().is(ModItems.DEPTH_GAUGE))) {
			return;
		}
		Component text = Component.translatable("hud.aquamarine.depth_gauge", depth(mc), level);
		int color = level >= 4 ? 0xFFFF5555 : level >= 2 ? 0xFFFFD76A : 0xFF9FF0E0;
		graphics.centeredText(mc.font, text, graphics.guiWidth() / 2, graphics.guiHeight() - 62, color);
	}

	private PressureHud() {}
}
