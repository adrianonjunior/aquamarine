package com.aquamarine.client.screen;

import com.aquamarine.Aquamarine;
import com.aquamarine.menu.AbyssalForgeMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** Tela da Forja Abissal: mesmo layout da bancada, com fundo próprio (tools/gen_textures.py). */
public class AbyssalForgeScreen extends AbstractContainerScreen<AbyssalForgeMenu> {
	private static final Identifier BACKGROUND = Aquamarine.id("textures/gui/container/abyssal_forge.png");

	public AbyssalForgeScreen(AbyssalForgeMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = 29;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		int xo = (this.width - this.imageWidth) / 2;
		int yo = (this.height - this.imageHeight) / 2;
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
	}
}
