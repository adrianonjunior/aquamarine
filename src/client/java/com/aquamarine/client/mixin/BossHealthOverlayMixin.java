package com.aquamarine.client.mixin;

import com.aquamarine.client.bossbar.AquamarineBossBarHud;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Troca a barra vanilla pelos bosses do mod ({@link AquamarineBossBarHud}). Sem nenhum boss do
 * mod na tela, não faz nada; com algum, refaz o laço do vanilla (mesmo espaçamento) desenhando
 * a barra própria para os do mod e a vanilla para os outros.
 */
@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Final
	private Map<UUID, LerpingBossEvent> events;

	@Shadow
	protected abstract void extractBar(GuiGraphicsExtractor graphics, int x, int y, BossEvent event);

	@Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
	private void aquamarine$customBars(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		if (this.events.keySet().stream().noneMatch(AquamarineBossBarHud::handles)) {
			return;
		}
		ci.cancel();
		graphics.nextStratum();
		float partialTick = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
		int screenWidth = graphics.guiWidth();
		int yOffset = 12;
		for (LerpingBossEvent event : this.events.values()) {
			if (AquamarineBossBarHud.handles(event.getId())) {
				yOffset += AquamarineBossBarHud.extract(graphics, this.minecraft.font, screenWidth, yOffset - 9, event, partialTick);
			} else {
				this.extractBar(graphics, screenWidth / 2 - 91, yOffset, event);
				Component name = event.getName();
				graphics.text(this.minecraft.font, name, screenWidth / 2 - this.minecraft.font.width(name) / 2, yOffset - 9, -1);
				yOffset += 10 + 9;
			}
			if (yOffset >= graphics.guiHeight() / 3) {
				break;
			}
		}
	}
}
