package com.aquamarine.client;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.KrakenEggEntity;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.MorayEntity;
import com.aquamarine.entity.SpermWhaleEntity;
import com.aquamarine.entity.TentacleEntity;
import com.aquamarine.entity.WhaleEntity;
import com.aquamarine.entity.YoungKrakenEntity;
import com.aquamarine.item.AquaticRadarItem;
import com.aquamarine.item.ModComponents;
import com.aquamarine.item.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/**
 * HUD do Radar Aquático (6G2): um quadro no canto de cima, com a frente do jogador para cima. Pontos: Kraken (vermelho
 * grande), tentáculos (vermelho), Crias e ovos (laranja), Moréias (amarelo), Baleias-de-Nereia (azul) e Cachalotes
 * (roxo), até {@link #RANGE} blocos; o Porto mais perto aparece na borda com a distância. Só com um radar ligado no
 * inventário.
 */
public final class RadarHud {
	public static final int RANGE = 64;
	private static final int SIZE = 64;
	private static final int MARGIN = 6;

	public static void register() {
		HudElementRegistry.addLast(Aquamarine.id("aquatic_radar"), RadarHud::extract);
	}

	/** O radar ligado do jogador (qualquer espaço do inventário), ou vazio. */
	static ItemStack active(LocalPlayer player) {
		long now = player.level().getGameTime();
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.is(ModItems.AQUATIC_RADAR) && AquaticRadarItem.activeUntil(stack) > now) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	private static void extract(GuiGraphicsExtractor graphics, DeltaTracker delta) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null) {
			return;
		}
		ItemStack radar = active(player);
		if (radar.isEmpty()) {
			return;
		}
		int x0 = graphics.guiWidth() - SIZE - MARGIN, y0 = MARGIN + 20, cx = x0 + SIZE / 2, cy = y0 + SIZE / 2;
		graphics.fill(x0, y0, x0 + SIZE, y0 + SIZE, 0x9A04141E);
		graphics.outline(x0, y0, SIZE, SIZE, 0xFF46D2DC);
		for (int r = 10; r < SIZE / 2; r += 10) {
			graphics.outline(cx - r, cy - r, 2 * r, 2 * r, 0x3346D2DC);
		}
		double yaw = Math.toRadians(player.getYRot());
		double cos = Math.cos(yaw), sin = Math.sin(yaw);
		for (Entity e : player.level().getEntities(player, new AABB(player.blockPosition()).inflate(RANGE))) {
			int color = color(e);
			if (color == 0) {
				continue;
			}
			double dx = e.getX() - player.getX(), dz = e.getZ() - player.getZ();
			// Gira para a frente do jogador ficar para cima.
			double rx = dx * cos + dz * sin, rz = -dx * sin + dz * cos;
			int px = cx - (int) Math.round(rx * (SIZE / 2.0 - 3) / RANGE), py = cy - (int) Math.round(rz * (SIZE / 2.0 - 3) / RANGE);
			int half = e instanceof KrakenEntity ? 2 : 1;
			if (Math.abs(px - cx) < SIZE / 2 - 1 && Math.abs(py - cy) < SIZE / 2 - 1) {
				graphics.fill(px - half, py - half, px + half + 1, py + half + 1, color);
			}
		}
		graphics.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFFFFFF);
		BlockPos harbor = radar.get(ModComponents.RADAR_TARGET);
		if (harbor != null) {
			double dx = harbor.getX() + 0.5 - player.getX(), dz = harbor.getZ() + 0.5 - player.getZ();
			double rx = dx * cos + dz * sin, rz = -dx * sin + dz * cos;
			double len = Math.max(1.0, Math.hypot(rx, rz));
			int px = cx - (int) Math.round(rx / len * (SIZE / 2.0 - 3)), py = cy - (int) Math.round(rz / len * (SIZE / 2.0 - 3));
			graphics.fill(px - 2, py - 2, px + 3, py + 3, 0xFF6CF0A0);
			graphics.text(mc.font, Component.translatable("hud.aquamarine.radar.harbor", (int) Math.hypot(dx, dz)).getString(),
				x0, y0 + SIZE + 3, 0xFF6CF0A0, true);
		}
		long left = (AquaticRadarItem.activeUntil(radar) - player.level().getGameTime()) / 20;
		graphics.text(mc.font, Component.translatable("hud.aquamarine.radar.time", left).getString(), x0, y0 - 10, 0xFFBFEFF2, true);
	}

	private static int color(Entity e) {
		if (e instanceof KrakenEntity) {
			return 0xFFFF3A3A;
		} else if (e instanceof TentacleEntity) {
			return 0xFFC02040;
		} else if (e instanceof YoungKrakenEntity || e instanceof KrakenEggEntity) {
			return 0xFFFF9A3A;
		} else if (e instanceof MorayEntity) {
			return 0xFFFFE04A;
		} else if (e instanceof SpermWhaleEntity) {
			return 0xFFB27CFF;
		} else if (e instanceof WhaleEntity) {
			return 0xFF5AA8FF;
		}
		return 0;
	}

	private RadarHud() {}
}
