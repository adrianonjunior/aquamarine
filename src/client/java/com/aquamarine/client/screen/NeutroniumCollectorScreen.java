package com.aquamarine.client.screen;

import com.aquamarine.Aquamarine;
import com.aquamarine.energy.WaterEnergy;
import com.aquamarine.menu.NeutroniumCollectorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** Painel de operação: energia à esquerda, compressão no centro e estoque/saída à direita. */
public final class NeutroniumCollectorScreen extends AbstractContainerScreen<NeutroniumCollectorMenu> {
	private static final Identifier BACKGROUND = Aquamarine.id("textures/gui/container/neutronium_collector.png");
	private static final int PANEL_W = 220;
	private static final int PANEL_H = 202;

	public NeutroniumCollectorScreen(NeutroniumCollectorMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, PANEL_W, PANEL_H);
	}

	@Override protected void init() {
		super.init();
		titleLabelX = 10;
		titleLabelY = 5;
		inventoryLabelX = 29;
		inventoryLabelY = 110;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		int x = leftPos, y = topPos;
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0, 0, PANEL_W, PANEL_H, 256, 256);
		drawEnergy(graphics, x, y);
		drawCore(graphics, x, y, partialTick);
	}

	private void drawEnergy(GuiGraphicsExtractor g, int x, int y) {
		int capacity = Math.max(1, menu.capacity());
		int fill = Math.min(50, menu.energy() * 50 / capacity);
		g.fillGradient(x + 20, y + 79 - fill, x + 30, y + 79, 0xffd9ffff, 0xff1789aa);
		long time = minecraft.level == null ? 0 : minecraft.level.getGameTime();
		for (int ray = 0; ray < 3; ray++) {
			int yy = y + 76 - Math.floorMod((int) time + ray * 17, Math.max(1, fill));
			if (fill > 3) g.fill(x + 21, yy, x + 29, yy + 1, 0xffeaffff);
		}
	}

	private void drawCore(GuiGraphicsExtractor g, int x, int y, float partialTick) {
		int lit = Math.round(menu.progressPercent() * 24 / 100.0F);
		for (int i = 0; i < 24; i++) {
			double a = Math.PI * 2 * i / 24.0 - Math.PI / 2;
			int px = x + 107 + (int) Math.round(Math.cos(a) * 31);
			int py = y + 52 + (int) Math.round(Math.sin(a) * 22);
			g.fill(px - 1, py - 1, px + 2, py + 2, i < lit ? 0xffbffcff : 0xff3f5b61);
		}
		double time = (minecraft.level == null ? 0 : minecraft.level.getGameTime()) + partialTick;
		int pulse = menu.status() == 0 ? (int) Math.round(2 + Math.sin(time * .35) * 2) : 1;
		int size = 5 + menu.progressPercent() * 7 / 100 + pulse;
		g.fillGradient(x + 107 - size, y + 52 - size, x + 108 + size, y + 53 + size, 0xffffffff, 0xff3da8c7);
		g.fill(x + 105, y + 38, x + 110, y + 67, 0xffd9ffff);
		g.fill(x + 93, y + 50, x + 122, y + 55, 0xffd9ffff);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX, titleLabelY, 0xff2a1b0c, false);
		graphics.centeredText(font, Component.translatable("gui.aquamarine.collector.energy"), 34, 15, 0xffeefcff);
		graphics.centeredText(font, Component.translatable("gui.aquamarine.collector.progress"), 107, 15, 0xffeefcff);
		graphics.centeredText(font, Component.translatable("gui.aquamarine.collector.stock"), 183, 15, 0xff2a1b0c);
		graphics.centeredText(font, Component.literal(menu.progressPercent() + "%"), 107, 72, 0xffd8f8ff);
		graphics.centeredText(font, Component.translatable("gui.aquamarine.collector.remaining", remaining()), 107, 82, 0xff29424a);
		graphics.centeredText(font, Component.literal(menu.dust() + " / " + WaterEnergy.COLLECTOR_MAX_DUST), 183, 31, 0xff2a1b0c);
		graphics.centeredText(font, Component.literal(menu.energy() + " / " + menu.capacity() + " E · "
			+ WaterEnergy.COLLECTOR_USE + " E/t"), PANEL_W / 2, 89, 0xffd8f8ff);
		graphics.textWithWordWrap(font, status(), 12, 101, PANEL_W - 24, statusColor());
	}

	private String remaining() {
		int seconds = (menu.ticksRemaining() + 19) / 20;
		return "%d:%02d".formatted(seconds / 60, seconds % 60);
	}

	private Component status() {
		return switch (menu.status()) {
			case 1 -> Component.translatable("gui.aquamarine.collector.status.no_energy");
			case 2 -> Component.translatable("gui.aquamarine.collector.status.deep_ocean");
			case 3 -> Component.translatable("gui.aquamarine.collector.status.water");
			case 4 -> Component.translatable("gui.aquamarine.collector.status.depth", menu.missingDepth());
			case 5 -> Component.translatable("gui.aquamarine.collector.status.full");
			default -> Component.translatable("gui.aquamarine.collector.status.running");
		};
	}

	private int statusColor() {
		return switch (menu.status()) { case 0 -> 0xffbdf7ee; case 1 -> 0xffffc45c; case 5 -> 0xffd6f9ff; default -> 0xffff8a8a; };
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		super.extractTooltip(graphics, mouseX, mouseY);
		if (isHovering(13, 23, 42, 62, mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(font, Component.translatable("gui.aquamarine.collector.tooltip.energy",
				menu.energy(), menu.capacity(), WaterEnergy.COLLECTOR_USE), mouseX, mouseY);
		} else if (isHovering(62, 18, 91, 69, mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(font, Component.translatable("gui.aquamarine.collector.tooltip.progress",
				menu.progressPercent(), remaining()), mouseX, mouseY);
		} else if (isHovering(160, 20, 48, 63, mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(font, Component.translatable("gui.aquamarine.collector.tooltip.stock",
				menu.dust(), WaterEnergy.COLLECTOR_MAX_DUST), mouseX, mouseY);
		} else if (isHovering(10, 99, PANEL_W - 20, 18, mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(font, status(), mouseX, mouseY);
		}
	}
}
