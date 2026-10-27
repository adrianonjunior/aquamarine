package com.aquamarine.client.manual;

import com.aquamarine.Aquamarine;
import com.aquamarine.client.manual.ManualContent.Chapter;
import com.aquamarine.client.manual.ManualContent.Element;
import com.aquamarine.client.manual.ManualContent.Kind;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

/**
 * Tela do Manual do Aquamarine: livro aberto de couro com cantos de latão, duas páginas de pergaminho e abas de capítulo
 * à direita (arte em tools/gen_manual_art.py). O conteúdo ({@link ManualContent}) corre sozinho pelas páginas; os prints
 * ampliam no clique; as receitas vêm do jogo ({@link ManualRecipes}). A página aberta fica guardada até a próxima vez.
 */
public class ManualScreen extends Screen {
	private static final Identifier BOOK = Aquamarine.id("textures/gui/manual/book.png");
	private static final int TEX = 512, TEX_H = 256;
	/** Livro aberto: tamanho na textura e na tela. */
	private static final int W = 304, H = 196;
	/** Área de texto de cada página, relativa ao livro. */
	private static final int[] PAGE_X = {20, 164};
	private static final int PAGE_W = 120, TOP = 16, BOTTOM = 170;
	/** Prints: 320 x 180 na textura, 120 x 68 na página. */
	private static final int SHOT_W = 320, SHOT_H = 180, IMG_W = 120, IMG_H = 68;
	private static final int INK = 0xFF3B2F22, INK_SOFT = 0xFF75644B, TEAL = 0xFF175A68, BRASS = 0xFFB08A3E;
	private static final int TAB_H = 18;

	private static int remembered;

	private ManualContent content;
	private final ManualRecipes recipes = new ManualRecipes();
	private final List<List<Placed>> pages = new ArrayList<>();
	private final List<Integer> chapterPage = new ArrayList<>();
	private final Map<String, Boolean> images = new HashMap<>();
	private int page;
	private int bx, by;
	private Placed zoom;

	/** Um elemento já na página: altura, linhas de texto (a parte que coube aqui) e o número do passo. */
	private record Placed(Element element, int chapter, int y, int h, List<FormattedCharSequence> lines, int number) {}

	public ManualScreen() {
		super(Component.translatable("item.aquamarine.manual"));
	}

	@Override
	protected void init() {
		content = ManualContent.load();
		layout();
		page = Math.min(remembered, lastSpread());
		bx = (width - W) / 2 - 8;
		by = (height - H) / 2;
	}

	// ------------------------------------------------------------------ paginação

	private int lastSpread() {
		return Math.max(0, (pages.size() - 1) & ~1);
	}

	private void layout() {
		pages.clear();
		chapterPage.clear();
		List<Placed> cover = new ArrayList<>();
		int y = TOP + 40;
		for (Element element : content.intro) {
			List<FormattedCharSequence> lines = lines(element);
			int h = height(element, lines.size());
			cover.add(new Placed(element, -1, y, h, lines, 0));
			y += h;
		}
		pages.add(cover);
		pages.add(new ArrayList<>());
		for (int c = 0; c < content.chapters.size(); c++) {
			Chapter chapter = content.chapters.get(c);
			List<Placed> current = new ArrayList<>();
			pages.add(current);
			chapterPage.add(pages.size() - 1);
			Element header = new Element(Kind.CHAPTER, chapter.title(), chapter.icon(), null, null, null, List.of());
			current.add(new Placed(header, c, TOP, 30, List.of(), c + 1));
			y = TOP + 30;
			int step = 0;
			for (Element element : chapter.content()) {
				if (element.kind() == Kind.BREAK) {
					current = new ArrayList<>();
					pages.add(current);
					y = TOP;
					continue;
				}
				if (element.kind() == Kind.TITLE) {
					step = 0;
				}
				int number = element.kind() == Kind.STEP ? ++step : 0;
				List<FormattedCharSequence> lines = lines(element);
				boolean splits = element.kind() == Kind.TEXT || element.kind() == Kind.TIP || element.kind() == Kind.STEP;
				while (true) {
					int h = height(element, lines.size());
					// Na página vazia entra de qualquer jeito (nada é mais alto que uma página; só evita laço).
					if (y + h <= BOTTOM || y == TOP && current.isEmpty()) {
						current.add(new Placed(element, c, y, h, lines, number));
						y += h;
						break;
					}
					// Texto comprido continua na página seguinte; o resto (imagem, receita, título) passa inteiro.
					int fit = (BOTTOM - y - (height(element, 0))) / 9;
					if (splits && fit >= 2 && fit < lines.size()) {
						List<FormattedCharSequence> head = lines.subList(0, fit);
						current.add(new Placed(element, c, y, height(element, fit), head, number));
						lines = lines.subList(fit, lines.size());
						number = element.kind() == Kind.STEP ? -number : 0;
					}
					// Um título nunca fica sozinho no fim da página: desce junto com o que vem depois.
					if (!current.isEmpty() && current.getLast().element().kind() == Kind.TITLE && current.size() > 1) {
						Placed orphan = current.removeLast();
						current = new ArrayList<>();
						pages.add(current);
						current.add(new Placed(orphan.element(), c, TOP, orphan.h(), orphan.lines(), orphan.number()));
						y = TOP + orphan.h();
					} else {
						current = new ArrayList<>();
						pages.add(current);
						y = TOP;
					}
				}
			}
		}
		if (pages.size() % 2 == 1) {
			pages.add(new ArrayList<>());
		}
	}

	private int textWidth(Element element) {
		return switch (element.kind()) {
			case STEP -> PAGE_W - 16;
			case TIP -> PAGE_W - 12;
			case TITLE -> element.icon() != null ? PAGE_W - 20 : PAGE_W;
			default -> PAGE_W;
		};
	}

	private List<FormattedCharSequence> lines(Element element) {
		String text = element.text();
		if (text == null || text.isEmpty() || element.kind() == Kind.CHAPTER) {
			return List.of();
		}
		MutableComponent styled = styled(text, element.kind() == Kind.TITLE ? TEAL : INK, element.kind() == Kind.TITLE);
		if (element.kind() == Kind.IMAGE || element.kind() == Kind.RECIPE || element.kind() == Kind.ITEMS) {
			styled = styled(text, INK_SOFT, false).withStyle(ChatFormatting.ITALIC);
		}
		return font.split(styled, textWidth(element));
	}

	/** Altura de um elemento com {@code n} linhas de texto. */
	private static int height(Element element, int n) {
		return switch (element.kind()) {
			case CHAPTER -> 30;
			case TITLE -> Math.max(18, n * 9 + 2) + 4;
			case TEXT -> n * 9 + 4;
			case STEP -> Math.max(12, n * 9) + 4;
			case TIP -> n * 9 + 10 + 4;
			case IMAGE -> IMG_H + 3 + n * 9 + 5;
			case RECIPE -> 54 + 3 + 18 + n * 9 + 4;
			case ITEMS -> ((element.items().size() + 5) / 6) * 20 + n * 9 + 4;
			case BREAK -> 0;
		};
	}

	/** {@code **assim**} fica em negrito na cor dos títulos. */
	private static MutableComponent styled(String text, int color, boolean bold) {
		MutableComponent out = Component.empty();
		String[] parts = text.split("\\*\\*", -1);
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].isEmpty()) {
				continue;
			}
			boolean strong = i % 2 == 1;
			out.append(Component.literal(parts[i]).withStyle(Style.EMPTY.withColor(strong ? TEAL : color).withBold(bold || strong)));
		}
		return out;
	}

	// ------------------------------------------------------------------ desenho

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float partial) {
		super.extractRenderState(g, mx, my, partial);
		drawTabs(g, mx, my);
		g.blit(RenderPipelines.GUI_TEXTURED, BOOK, bx, by, 0, 0, W, H, TEX, TEX_H);
		for (int side = 0; side < 2; side++) {
			int index = page + side;
			if (index >= pages.size()) {
				continue;
			}
			int x = bx + PAGE_X[side];
			if (index == 0) {
				drawCover(g, x);
			} else if (index == 1) {
				drawIndex(g, x, mx, my);
			}
			for (Placed placed : pages.get(index)) {
				draw(g, placed, x, by + placed.y(), mx, my);
			}
			if (index > 1) {
				String number = Integer.toString(index - 1);
				g.text(font, number, x + PAGE_W / 2 - font.width(number) / 2, by + H - 20, INK_SOFT, false);
			}
		}
		drawArrows(g, mx, my);
		if (zoom != null) {
			// Camada nova: ícones e textos das páginas ficam embaixo do print ampliado.
			g.nextStratum();
			drawZoom(g);
		}
	}

	private void drawCover(GuiGraphicsExtractor g, int x) {
		var pose = g.pose();
		pose.pushMatrix();
		pose.translate(x + PAGE_W / 2.0F, by + TOP + 3);
		pose.scale(1.5F, 1.5F);
		g.centeredText(font, Component.literal(content.title).withStyle(Style.EMPTY.withColor(TEAL).withBold(true)), 0, 0, TEAL);
		pose.popMatrix();
		g.centeredText(font, Component.literal(content.subtitle).withStyle(ChatFormatting.ITALIC), x + PAGE_W / 2, by + TOP + 19, INK_SOFT);
		ornament(g, x + PAGE_W / 2, by + TOP + 32);
	}

	/** Enfeite de latão com uma pérola no meio. */
	private static void ornament(GuiGraphicsExtractor g, int cx, int y) {
		g.fill(cx - 40, y, cx - 4, y + 1, BRASS);
		g.fill(cx + 4, y, cx + 40, y + 1, BRASS);
		g.fill(cx - 2, y - 2, cx + 2, y + 3, 0xFFE9E4D6);
		g.fill(cx - 1, y - 1, cx + 1, y + 1, 0xFFFFFFFF);
	}

	private void drawIndex(GuiGraphicsExtractor g, int x, int mx, int my) {
		g.text(font, Component.translatable("manual.aquamarine.index").withStyle(Style.EMPTY.withColor(TEAL).withBold(true)), x, by + TOP, TEAL, false);
		g.fill(x, by + TOP + 11, x + PAGE_W, by + TOP + 12, BRASS);
		for (int c = 0; c < content.chapters.size(); c++) {
			Chapter chapter = content.chapters.get(c);
			int y = by + TOP + 16 + c * 18;
			boolean hover = mx >= x && mx < x + PAGE_W && my >= y && my < y + 18;
			if (hover) {
				g.fill(x - 2, y, x + PAGE_W + 2, y + 18, 0x22175A68);
			}
			g.fill(x, y + 2, x + 2, y + 16, chapter.color());
			g.item(ManualContent.stack(chapter.icon()), x + 4, y + 1);
			// Título comprido quebra em duas linhas dentro da mesma faixa.
			List<FormattedCharSequence> title = font.split(Component.literal(chapter.title()), PAGE_W - 39);
			int ty = title.size() > 1 ? y + 1 : y + 5;
			for (FormattedCharSequence line : title.subList(0, Math.min(2, title.size()))) {
				g.text(font, line, x + 23, ty, hover ? TEAL : INK, false);
				ty += 9;
			}
			String number = Integer.toString(chapterPage.get(c) - 1);
			g.text(font, number, x + PAGE_W - font.width(number), y + 5, INK_SOFT, false);
		}
	}

	private void draw(GuiGraphicsExtractor g, Placed p, int x, int y, int mx, int my) {
		Element e = p.element();
		switch (e.kind()) {
			case CHAPTER -> {
				int color = content.chapters.get(p.chapter()).color();
				g.fill(x - 2, y, x + PAGE_W + 2, y + 24, (color & 0x00FFFFFF) | 0x33000000);
				g.fill(x - 2, y + 24, x + PAGE_W + 2, y + 25, color);
				g.item(ManualContent.stack(e.icon()), x + 2, y + 4);
				g.text(font, Component.translatable("manual.aquamarine.chapter", p.number()).withStyle(ChatFormatting.ITALIC), x + 22, y + 3, INK_SOFT, false);
				g.text(font, Component.literal(font.plainSubstrByWidth(e.text(), PAGE_W - 24)).withStyle(Style.EMPTY.withBold(true)),
					x + 22, y + 13, TEAL, false);
			}
			case TITLE -> {
				int tx = x;
				if (e.icon() != null) {
					g.item(ManualContent.stack(e.icon()), x, y);
					tx += 20;
				}
				int ty = y + (p.lines().size() == 1 ? 5 : 0);
				for (FormattedCharSequence line : p.lines()) {
					g.text(font, line, tx, ty, TEAL, false);
					ty += 9;
				}
				g.fill(x, y + p.h() - 3, x + PAGE_W, y + p.h() - 2, BRASS);
			}
			case TEXT -> lines(g, p.lines(), x, y, INK);
			case STEP -> {
				if (p.number() > 0) {
					g.blit(RenderPipelines.GUI_TEXTURED, BOOK, x, y, 0, 216, 12, 12, TEX, TEX_H);
					String n = Integer.toString(p.number());
					g.text(font, n, x + 6 - font.width(n) / 2, y + 2, 0xFF2B1D0E, false);
				}
				lines(g, p.lines(), x + 16, y + 2, INK);
			}
			case TIP -> {
				g.fill(x, y, x + PAGE_W, y + p.h() - 4, 0x2A2A8C9C);
				g.fill(x, y, x + 2, y + p.h() - 4, 0xFF2A8C9C);
				g.blit(RenderPipelines.GUI_TEXTURED, BOOK, x + 4, y + 4, 14, 216, 6, 6, TEX, TEX_H);
				lines(g, p.lines(), x + 12, y + 5, 0xFF1F4B55);
			}
			case IMAGE -> {
				boolean hover = mx >= x && mx < x + IMG_W && my >= y && my < y + IMG_H;
				image(g, e.image(), x, y, IMG_W, IMG_H, hover);
				lines(g, p.lines(), x, y + IMG_H + 3, INK_SOFT);
				if (hover && zoom == null) {
					g.setTooltipForNextFrame(Component.translatable("manual.aquamarine.zoom"), mx, my);
				}
			}
			case RECIPE -> recipe(g, p, x, y, mx, my);
			case ITEMS -> {
				for (int i = 0; i < e.items().size(); i++) {
					int ix = x + (i % 6) * 20, iy = y + (i / 6) * 20;
					slot(g, ix, iy);
					ItemStack stack = ManualContent.stack(e.items().get(i));
					g.item(stack, ix + 1, iy + 1);
					if (mx >= ix && mx < ix + 18 && my >= iy && my < iy + 18 && !stack.isEmpty() && zoom == null) {
						g.setTooltipForNextFrame(font, stack, mx, my);
					}
				}
				lines(g, p.lines(), x, y + ((e.items().size() + 5) / 6) * 20, INK_SOFT);
			}
			default -> {}
		}
	}

	private void lines(GuiGraphicsExtractor g, List<FormattedCharSequence> lines, int x, int y, int color) {
		for (FormattedCharSequence line : lines) {
			g.text(font, line, x, y, color, false);
			y += 9;
		}
	}

	private void image(GuiGraphicsExtractor g, String name, int x, int y, int w, int h, boolean hover) {
		Identifier id = Aquamarine.id("textures/gui/manual/" + name + ".png");
		boolean present = images.computeIfAbsent(name, n -> Minecraft.getInstance().getResourceManager().getResource(id).isPresent());
		g.fill(x + 2, y + 2, x + w + 2, y + h + 2, 0x40000000);
		g.fill(x - 1, y - 1, x + w + 1, y + h + 1, hover ? 0xFF2A8C9C : 0xFF5A4630);
		if (present) {
			g.blit(RenderPipelines.GUI_TEXTURED, id, x, y, 0, 0, w, h, SHOT_W, SHOT_H, SHOT_W, SHOT_H);
		} else {
			g.fill(x, y, x + w, y + h, 0xFF2B3A40);
			g.centeredText(font, Component.translatable("manual.aquamarine.no_image"), x + w / 2, y + h / 2 - 4, 0xFFBFD8DC);
		}
	}

	private static void slot(GuiGraphicsExtractor g, int x, int y) {
		g.blit(RenderPipelines.GUI_TEXTURED, BOOK, x, y, 32, 216, 18, 18, TEX, TEX_H);
	}

	private void recipe(GuiGraphicsExtractor g, Placed p, int x, int y, int mx, int my) {
		Optional<ManualRecipes.View> found = recipes.get(p.element().recipe());
		if (found.isEmpty()) {
			lines(g, font.split(Component.translatable("manual.aquamarine.no_recipe"), PAGE_W), x, y + 20, INK_SOFT);
			return;
		}
		ManualRecipes.View view = found.get();
		long tick = System.currentTimeMillis() / 1000;
		ItemStack hovered = ItemStack.EMPTY;
		for (int i = 0; i < 9; i++) {
			int sx = x + 2 + (i % 3) * 18, sy = y + (i / 3) * 18;
			slot(g, sx, sy);
			List<ItemStack> options = view.grid().get(i);
			if (options.isEmpty()) {
				continue;
			}
			ItemStack stack = options.get((int) (tick % options.size()));
			g.item(stack, sx + 1, sy + 1);
			if (mx >= sx && mx < sx + 18 && my >= sy && my < sy + 18) {
				hovered = stack;
			}
		}
		g.blit(RenderPipelines.GUI_TEXTURED, BOOK, x + 62, y + 19, 52, 216, 22, 15, TEX, TEX_H);
		int rx = x + 90, ry = y + 14;
		g.blit(RenderPipelines.GUI_TEXTURED, BOOK, rx, ry, 76, 216, 26, 26, TEX, TEX_H);
		g.item(view.result(), rx + 5, ry + 5);
		g.itemDecorations(font, view.result(), rx + 5, ry + 5);
		if (mx >= rx && mx < rx + 26 && my >= ry && my < ry + 26) {
			hovered = view.result();
		}
		ItemStack station = p.element().station() != null ? ManualContent.stack(p.element().station()) : view.station();
		Component where = station.isEmpty() ? Component.translatable("manual.aquamarine.crafting")
			: Component.translatable("manual.aquamarine.station", station.getHoverName());
		if (view.shapeless()) {
			where = where.copy().append(Component.translatable("manual.aquamarine.shapeless"));
		}
		// Onde se faz (até duas linhas: o nome da estação pode ser comprido) e a legenda embaixo.
		List<FormattedCharSequence> label = font.split(where.copy().withStyle(ChatFormatting.ITALIC), PAGE_W);
		lines(g, label.subList(0, Math.min(2, label.size())), x, y + 57, INK_SOFT);
		lines(g, p.lines(), x, y + 75, INK_SOFT);
		if (!hovered.isEmpty() && zoom == null) {
			g.setTooltipForNextFrame(font, hovered, mx, my);
		}
	}

	private void drawArrows(GuiGraphicsExtractor g, int mx, int my) {
		if (page > 0) {
			boolean hover = over(mx, my, bx + 22, by + H - 22, 18, 12);
			g.blit(RenderPipelines.GUI_TEXTURED, BOOK, bx + 22, by + H - 22, 104, hover ? 230 : 216, 18, 12, TEX, TEX_H);
		}
		if (page + 2 < pages.size()) {
			boolean hover = over(mx, my, bx + W - 40, by + H - 22, 18, 12);
			g.blit(RenderPipelines.GUI_TEXTURED, BOOK, bx + W - 40, by + H - 22, 124, hover ? 230 : 216, 18, 12, TEX, TEX_H);
		}
	}

	/** Abas à direita: sumário e um por capítulo, na cor dele; a do capítulo aberto sai mais para fora. */
	private void drawTabs(GuiGraphicsExtractor g, int mx, int my) {
		int open = chapterAt(page);
		for (int t = 0; t <= content.chapters.size(); t++) {
			int y = tabY(t);
			boolean selected = t == 0 ? page == 0 : open == t - 1;
			boolean hover = over(mx, my, bx + W - 6, y, 30, TAB_H);
			int out = selected ? 26 : hover ? 24 : 20;
			int color = t == 0 ? 0xFFC9B48A : content.chapters.get(t - 1).color();
			g.blit(RenderPipelines.GUI_TEXTURED, BOOK, bx + W - 30 + out, y, 148, 216, 30, TAB_H, TEX, TEX_H, color);
			ItemStack icon = t == 0 ? ManualContent.stack("aquamarine:manual") : ManualContent.stack(content.chapters.get(t - 1).icon());
			g.item(icon, bx + W - 30 + out + 10, y + 1);
			if (hover && zoom == null) {
				Component name = t == 0 ? Component.translatable("manual.aquamarine.index") : Component.literal(content.chapters.get(t - 1).title());
				g.setTooltipForNextFrame(name, mx, my);
			}
		}
	}

	private int tabY(int t) {
		return by + 6 + t * (TAB_H + 2);
	}

	/** Capítulo da página da esquerda aberta (ou -1 no começo do livro). */
	private int chapterAt(int index) {
		int found = -1;
		for (int c = 0; c < chapterPage.size(); c++) {
			if (chapterPage.get(c) <= index + 1) {
				found = c;
			}
		}
		return found;
	}

	private void drawZoom(GuiGraphicsExtractor g) {
		g.fill(0, 0, width, height, 0xD0050B10);
		float scale = Math.min((width - 40) / (float) SHOT_W, (height - 50) / (float) SHOT_H);
		scale = Math.min(scale, 2.0F);
		int w = Math.round(SHOT_W * scale), h = Math.round(SHOT_H * scale);
		int x = (width - w) / 2, y = (height - h) / 2 - 8;
		image(g, zoom.element().image(), x, y, w, h, false);
		List<FormattedCharSequence> caption = font.split(styled(zoom.element().text(), 0xFFE6F2F2, false), Math.max(120, w));
		int cy = y + h + 6;
		for (FormattedCharSequence line : caption) {
			g.centeredText(font, line, width / 2, cy, 0xFFE6F2F2);
			cy += 9;
		}
	}

	// ------------------------------------------------------------------ entrada

	private static boolean over(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	private void turn(int to) {
		int target = Math.clamp(to & ~1, 0, lastSpread());
		if (target != page) {
			page = target;
			remembered = page;
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x(), my = event.y();
		if (zoom != null) {
			zoom = null;
			return true;
		}
		if (event.button() != 0) {
			return super.mouseClicked(event, doubleClick);
		}
		if (page > 0 && over(mx, my, bx + 22, by + H - 22, 18, 12)) {
			turn(page - 2);
			return true;
		}
		if (page + 2 < pages.size() && over(mx, my, bx + W - 40, by + H - 22, 18, 12)) {
			turn(page + 2);
			return true;
		}
		for (int t = 0; t <= content.chapters.size(); t++) {
			if (over(mx, my, bx + W - 6, tabY(t), 30, TAB_H)) {
				turn(t == 0 ? 0 : chapterPage.get(t - 1));
				return true;
			}
		}
		if (page == 0) {
			int x = bx + PAGE_X[1];
			for (int c = 0; c < content.chapters.size(); c++) {
				if (over(mx, my, x - 2, by + TOP + 16 + c * 18, PAGE_W + 4, 18)) {
					turn(chapterPage.get(c));
					return true;
				}
			}
		}
		for (int side = 0; side < 2 && page + side < pages.size(); side++) {
			int x = bx + PAGE_X[side];
			for (Placed placed : pages.get(page + side)) {
				if (placed.element().kind() == Kind.IMAGE && over(mx, my, x, by + placed.y(), IMG_W, IMG_H)) {
					zoom = placed;
					return true;
				}
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
		if (zoom == null && scrollY != 0) {
			turn(page + (scrollY < 0 ? 2 : -2));
			return true;
		}
		return super.mouseScrolled(mx, my, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int key = event.input();
		if (zoom != null && (key == InputConstants.KEY_ESCAPE || key == InputConstants.KEY_SPACE || key == InputConstants.KEY_RETURN)) {
			zoom = null;
			return true;
		}
		switch (key) {
			case InputConstants.KEY_RIGHT, InputConstants.KEY_PAGEDOWN, InputConstants.KEY_D -> turn(page + 2);
			case InputConstants.KEY_LEFT, InputConstants.KEY_PAGEUP, InputConstants.KEY_A -> turn(page - 2);
			case InputConstants.KEY_HOME -> turn(0);
			case InputConstants.KEY_END -> turn(lastSpread());
			default -> {
				return super.keyPressed(event);
			}
		}
		return true;
	}

	/** Só para os testes: abre direto num capítulo (0 = sumário). */
	public void openChapter(int chapter) {
		turn(chapter <= 0 ? 0 : chapterPage.get(Math.min(chapter, chapterPage.size()) - 1));
	}

	/** Só para os testes: {@code spreads} viradas depois do começo do capítulo e, se pedir, o primeiro print ampliado. */
	public void showAfter(int chapter, int spreads, boolean zoomFirst) {
		openChapter(chapter);
		turn(page + 2 * spreads);
		zoom = null;
		for (int side = 0; zoomFirst && zoom == null && side < 2 && page + side < pages.size(); side++) {
			zoom = pages.get(page + side).stream().filter(p -> p.element().kind() == Kind.IMAGE).findFirst().orElse(null);
		}
	}

	public int chapterCount() {
		return content.chapters.size();
	}
}
