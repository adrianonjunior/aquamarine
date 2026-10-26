package com.aquamarine.client.bossbar;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.boss.BossBarPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;

/**
 * Barra de vida customizada (docs/02-kraken.md): moldura de coral e Abissal, vida azul
 * profunda com brilho verde-água, escudo de cristal com rachaduras em 75/50/25% e estilhaços
 * ao quebrar, marcadores de fase, barra fantasma do dano recente e pulso roxo na fase 3 ou
 * enquanto o escudo regenera. Texturas: textures/gui/boss_bar.png (tools/gen_textures.py).
 */
public final class AquamarineBossBarHud {
	public static final int WIDTH = 220;
	public static final int HEIGHT = 22;
	private static final int BAR_X = 10;
	private static final int BAR_W = 200;
	private static final int SHIELD_Y = 4;
	private static final int SHIELD_H = 4;
	private static final int HEALTH_Y = 9;
	private static final int HEALTH_H = 8;
	private static final int TEX_W = 256;
	private static final int TEX_H = 96;
	private static final Identifier TEXTURE = Aquamarine.id("textures/gui/boss_bar.png");

	private static final Map<UUID, State> STATES = new HashMap<>();
	private static final RandomSource RANDOM = RandomSource.create();
	private static int clientTicks;

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(BossBarPayload.TYPE,
			(payload, context) -> STATES.computeIfAbsent(payload.barId(), id -> new State()).apply(payload));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			clientTicks++;
			STATES.values().forEach(State::tick);
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> STATES.clear());
	}

	/** O evento de boss {@code id} tem barra própria (e a vanilla deve ser escondida). */
	public static boolean handles(UUID id) {
		return STATES.containsKey(id);
	}

	/** Desenha a barra com o título começando em {@code top}; devolve a altura usada. */
	public static int extract(GuiGraphicsExtractor graphics, Font font, int screenWidth, int top, BossEvent event, float partialTick) {
		State s = STATES.get(event.getId());
		int x = screenWidth / 2 - WIDTH / 2;
		int y = top + 10;
		Component title = Component.translatable("bossbar.aquamarine.title", event.getName(), s.phase);
		graphics.centeredText(font, title, screenWidth / 2, top, s.phase >= 3 ? 0xFFD8C2FF : 0xFFE6FBFF);

		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, TEX_W, TEX_H);

		int ghostW = width(s.ghost, s.maxHealth);
		int healthW = width(s.displayHealth, s.maxHealth);
		if (ghostW > 0) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + BAR_X, y + HEALTH_Y, 0, 30, ghostW, HEALTH_H, TEX_W, TEX_H);
		}
		if (healthW > 0) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + BAR_X, y + HEALTH_Y, 0, 22, healthW, HEALTH_H, TEX_W, TEX_H);
		}
		for (int i = 1; i <= 2; i++) {
			int mx = x + BAR_X + BAR_W * i / 3;
			graphics.fill(mx, y + HEALTH_Y, mx + 1, y + HEALTH_Y + HEALTH_H, 0xFFE8C25A);
			graphics.fill(mx + 1, y + HEALTH_Y, mx + 2, y + HEALTH_Y + HEALTH_H, 0x803A240E);
		}

		int shieldW = width(s.displayShield, s.maxShield);
		if (shieldW > 0) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + BAR_X, y + SHIELD_Y, 0, 38, shieldW, SHIELD_H, TEX_W, TEX_H);
			float fraction = s.shield / Math.max(1, s.maxShield);
			for (int crack = 0; crack < 3; crack++) {
				if (fraction <= 0.75F - 0.25F * crack) {
					graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + BAR_X, y + SHIELD_Y, 0, 42 + crack * 4, shieldW, SHIELD_H, TEX_W, TEX_H);
				}
			}
		}

		if (s.phase >= 3 || s.regenerating) {
			float wave = 0.5F + 0.5F * Mth.sin((clientTicks + partialTick) * (s.regenerating ? 0.35F : 0.2F));
			int alpha = (int) (60 + 150 * wave);
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 56, WIDTH, HEIGHT, TEX_W, TEX_H, (alpha << 24) | 0xB48CFF);
			graphics.fill(x + BAR_X, y + SHIELD_Y, x + BAR_X + BAR_W, y + HEALTH_Y + HEALTH_H, ((int) (45 * wave) << 24) | 0x8A5AF0);
		}

		for (Shard shard : s.shards) {
			int alpha = Mth.clamp(shard.life * 255 / 12, 0, 255);
			int sx = x + Math.round(shard.x);
			int sy = y + Math.round(shard.y);
			graphics.fill(sx, sy, sx + shard.size, sy + shard.size, (alpha << 24) | shard.color);
		}
		return 10 + HEIGHT + 6;
	}

	private static int width(float value, float max) {
		return max <= 0 ? 0 : Mth.clamp(Math.round(BAR_W * value / max), 0, BAR_W);
	}

	private static final class Shard {
		float x, y, vx, vy;
		int life, size, color;
	}

	private static final class State {
		private static final int[] SHARD_COLORS = {0xE6FBFF, 0x9FE6FF, 0x5CBCF0, 0xFFFFFF, 0xB48CFF};

		float health, maxHealth = 1, shield, maxShield = 1;
		float displayHealth, displayShield, ghost;
		int phase = 1;
		boolean regenerating;
		int ghostHold;
		boolean initialized;
		final List<Shard> shards = new ArrayList<>();

		void apply(BossBarPayload p) {
			if (!initialized) {
				displayHealth = ghost = p.health();
				displayShield = p.shield();
				initialized = true;
			} else {
				if (p.health() < health) {
					ghostHold = 14;
				}
				if (shield > 0 && p.shield() <= 0) {
					shatter();
				}
			}
			health = p.health();
			maxHealth = p.maxHealth();
			shield = p.shield();
			maxShield = p.maxShield();
			phase = p.phase();
			regenerating = p.shieldRegenerating();
		}

		void shatter() {
			for (int i = 0; i < 28; i++) {
				Shard shard = new Shard();
				shard.x = BAR_X + RANDOM.nextFloat() * Math.max(8, width(displayShield, maxShield) + 20);
				shard.y = SHIELD_Y + RANDOM.nextFloat() * SHIELD_H;
				shard.vx = (RANDOM.nextFloat() - 0.5F) * 4.0F;
				shard.vy = -0.5F - RANDOM.nextFloat() * 2.5F;
				shard.life = 14 + RANDOM.nextInt(12);
				shard.size = RANDOM.nextFloat() < 0.3F ? 2 : 1;
				shard.color = SHARD_COLORS[RANDOM.nextInt(SHARD_COLORS.length)];
				shards.add(shard);
			}
		}

		void tick() {
			displayHealth += (health - displayHealth) * 0.35F;
			displayShield += (shield - displayShield) * 0.35F;
			if (Math.abs(displayShield - shield) < 0.01F) {
				displayShield = shield;
			}
			if (ghostHold > 0) {
				ghostHold--;
			} else if (ghost > displayHealth) {
				ghost = Math.max(displayHealth, ghost - maxHealth * 0.012F);
			}
			ghost = Math.max(ghost, displayHealth);
			shards.removeIf(shard -> --shard.life <= 0);
			for (Shard shard : shards) {
				shard.x += shard.vx;
				shard.y += shard.vy;
				shard.vy += 0.3F;
				shard.vx *= 0.94F;
			}
		}
	}

	private AquamarineBossBarHud() {}
}
