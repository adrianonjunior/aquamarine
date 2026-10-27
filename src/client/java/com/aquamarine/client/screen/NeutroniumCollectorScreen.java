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

/** Painel de operação: instrumentos independentes, estado em duas linhas e inventário integrado. */
public final class NeutroniumCollectorScreen extends AbstractContainerScreen<NeutroniumCollectorMenu> {
	private static final Identifier BACKGROUND = Aquamarine.id("textures/gui/container/neutronium_collector.png");
	private static final int PANEL_W = 220;
	private static final int PANEL_H = 224;

	private static final int HEADER_X = 8, HEADER_Y = 6, HEADER_W = 204, HEADER_H = 15;
	private static final int ENERGY_X = 6, ENERGY_Y = 25, ENERGY_W = 60, ENERGY_H = 72;
	private static final int CORE_X = 70, CORE_Y = 25, CORE_W = 76, CORE_H = 72;
	private static final int STOCK_X = 150, STOCK_Y = 25, STOCK_W = 64, STOCK_H = 72;
	private static final int STATUS_X = 6, STATUS_Y = 101, STATUS_W = 208, STATUS_H = 27;
	private static final int INVENTORY_X = 6, INVENTORY_Y = 131, INVENTORY_W = 208, INVENTORY_H = 90;

	private static final int TUBE_X = 12, TUBE_Y = 45, TUBE_W = 17, TUBE_H = 45;
	private static final int CORE_CENTER_X = 108, CORE_CENTER_Y = 60;
	private static final int CORE_SPRITE_SIZE = 24;
	private static final int CORE_SPRITE_V = 232;

	public NeutroniumCollectorScreen(NeutroniumCollectorMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, PANEL_W, PANEL_H);
	}

	@Override
	protected void init() {
		super.init();
		titleLabelX = HEADER_X + 3;
		titleLabelY = HEADER_Y + 3;
		inventoryLabelX = INVENTORY_X + 8;
		inventoryLabelY = INVENTORY_Y + 3;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		int x = leftPos, y = topPos;
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0, 0, PANEL_W, PANEL_H, 256, 256);
		drawEnergy(graphics, x, y);
		drawCore(graphics, x, y, partialTick);
		drawStatusIcon(graphics, x, y);
	}

	private void drawEnergy(GuiGraphicsExtractor g, int x, int y) {
		int capacity = Math.max(1, menu.capacity());
		int fill = Math.min(TUBE_H - 4, menu.energy() * (TUBE_H - 4) / capacity);
		int bottom = y + TUBE_Y + TUBE_H - 2;
		if (fill > 0) {
			int top = bottom - fill;
			g.fillGradient(x + TUBE_X + 2, top, x + TUBE_X + TUBE_W - 2, bottom,
				0xff9ff8ff, 0xff168aa8);
			g.fill(x + TUBE_X + 3, top, x + TUBE_X + TUBE_W - 3, top + 1, 0xffe5ffff);
			long time = minecraft.level == null ? 0 : minecraft.level.getGameTime();
			for (int ray = 0; ray < 3; ray++) {
				int yy = bottom - Math.floorMod((int) time + ray * 11, Math.max(1, fill));
				if (yy > top + 1) g.fill(x + TUBE_X + 3, yy, x + TUBE_X + TUBE_W - 3, yy + 1, 0xffcaffff);
			}
		}
	}

	private void drawCore(GuiGraphicsExtractor g, int x, int y, float partialTick) {
		int progress = menu.progressPercent();
		int lit = Math.round(progress * 28 / 100.0F);
		for (int i = 0; i < 28; i++) {
			double angle = Math.PI * 2 * i / 28.0 - Math.PI / 2;
			int px = x + CORE_CENTER_X + (int) Math.round(Math.cos(angle) * 26);
			int py = y + CORE_CENTER_Y + (int) Math.round(Math.sin(angle) * 14);
			int color = i < lit ? 0xffc8fbff : 0xff31545b;
			g.fill(px - 1, py - 1, px + 1, py + 1, color);
		}

		double time = (minecraft.level == null ? 0 : minecraft.level.getGameTime()) + partialTick;
		int cadence = menu.status() == 0 ? 3 : 7;
		int frame = Math.floorMod((int) (time / cadence), 8);
		g.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND,
			x + CORE_CENTER_X - CORE_SPRITE_SIZE / 2, y + CORE_CENTER_Y - CORE_SPRITE_SIZE / 2,
			frame * CORE_SPRITE_SIZE, CORE_SPRITE_V, CORE_SPRITE_SIZE, CORE_SPRITE_SIZE, 256, 256);
		if (progress >= 96) {
			int glow = ((int) time & 2) == 0 ? 0xffeaffff : 0xff72dce9;
			g.fill(x + CORE_CENTER_X - 15, y + CORE_CENTER_Y, x + CORE_CENTER_X - 12, y + CORE_CENTER_Y + 1, glow);
			g.fill(x + CORE_CENTER_X + 13, y + CORE_CENTER_Y, x + CORE_CENTER_X + 16, y + CORE_CENTER_Y + 1, glow);
		}
	}

	private void drawStatusIcon(GuiGraphicsExtractor g, int x, int y) {
		int color = statusColor();
		g.fill(x + STATUS_X + 7, y + STATUS_Y + 9, x + STATUS_X + 14, y + STATUS_Y + 16, color);
		g.fill(x + STATUS_X + 9, y + STATUS_Y + 7, x + STATUS_X + 12, y + STATUS_Y + 18, color);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		centered(graphics, title, HEADER_X + 3, HEADER_X + HEADER_W - 3, HEADER_Y + 3, 0xff2a1b0c);
		centered(graphics, Component.translatable("gui.aquamarine.collector.energy"),
			ENERGY_X + 4, ENERGY_X + ENERGY_W - 4, ENERGY_Y + 4, 0xff2a1b0c);
		centered(graphics, Component.translatable("gui.aquamarine.collector.progress"),
			CORE_X + 4, CORE_X + CORE_W - 4, CORE_Y + 4, 0xff2a1b0c);
		centered(graphics, Component.translatable("gui.aquamarine.collector.stock"),
			STOCK_X + 3, STOCK_X + STOCK_W - 3, STOCK_Y + 4, 0xff2a1b0c);

		int percent = Math.min(100, menu.energy() * 100 / Math.max(1, menu.capacity()));
		centered(graphics, Component.literal(compactEnergy(menu.energy())), 32, 64, 55, 0xffeafcff);
		centered(graphics, Component.literal(percent + "%"), 32, 64, 70, 0xff91edf4);
		centered(graphics, Component.literal(menu.progressPercent() + "%"), 73, 105, 82, 0xffeafcff);
		centered(graphics, Component.literal(remaining()), 108, 143, 82, 0xffeafcff);
		centered(graphics, Component.literal(menu.dust() + " / " + WaterEnergy.COLLECTOR_MAX_DUST),
			155, 209, 82, 0xffeafcff);

		graphics.text(font, statusTitle(), STATUS_X + 25, STATUS_Y + 4, statusColor(), false);
		graphics.text(font, statusDetail(), STATUS_X + 25, STATUS_Y + 14, 0xffe8f4f2, false);
		centered(graphics, playerInventoryTitle, 12, 84, INVENTORY_Y + 3, 0xff2a1b0c);
	}

	private void centered(GuiGraphicsExtractor graphics, Component text, int x0, int x1, int y, int color) {
		graphics.centeredText(font, text, (x0 + x1) / 2, y, color);
	}

	private static String compactEnergy(int energy) {
		if (energy >= 1_000_000) return Math.round(energy / 100_000.0F) / 10.0F + "ME";
		if (energy >= 1_000) return Math.round(energy / 1_000.0F) + "kE";
		return energy + "E";
	}

	private String remaining() {
		int seconds = (menu.ticksRemaining() + 19) / 20;
		return "%d:%02d".formatted(seconds / 60, seconds % 60);
	}

	private Component statusTitle() {
		return Component.translatable("gui.aquamarine.collector.status.title." + statusKey());
	}

	private Component statusDetail() {
		String key = "gui.aquamarine.collector.status.detail." + statusKey();
		return menu.status() == 4 ? Component.translatable(key, menu.missingDepth()) : Component.translatable(key);
	}

	private Component statusTooltip() {
		String key = "gui.aquamarine.collector.tooltip.status." + statusKey();
		return menu.status() == 4 ? Component.translatable(key, menu.missingDepth()) : Component.translatable(key);
	}

	private String statusKey() {
		return switch (menu.status()) {
			case 1 -> "no_energy";
			case 2 -> "deep_ocean";
			case 3 -> "water";
			case 4 -> "depth";
			case 5 -> "full";
			default -> "running";
		};
	}

	private int statusColor() {
		return switch (menu.status()) {
			case 0 -> 0xff63dff2;
			case 1 -> 0xffffbd55;
			case 5 -> 0xfff4fbff;
			default -> 0xffff6f6f;
		};
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		super.extractTooltip(graphics, mouseX, mouseY);
		if (isHovering(ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H, mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(font, Component.translatable("gui.aquamarine.collector.tooltip.energy",
				menu.energy(), menu.capacity(), WaterEnergy.COLLECTOR_USE), mouseX, mouseY);
		} else if (isHovering(CORE_X, CORE_Y, CORE_W, CORE_H, mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(font, Component.translatable("gui.aquamarine.collector.tooltip.progress",
				menu.progressPercent(), remaining()), mouseX, mouseY);
		} else if (isHovering(STOCK_X, STOCK_Y, STOCK_W, STOCK_H, mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(font, Component.translatable("gui.aquamarine.collector.tooltip.stock",
				menu.dust(), WaterEnergy.COLLECTOR_MAX_DUST), mouseX, mouseY);
		} else if (isHovering(STATUS_X, STATUS_Y, STATUS_W, STATUS_H, mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(font, statusTooltip(), mouseX, mouseY);
		}
	}
}
