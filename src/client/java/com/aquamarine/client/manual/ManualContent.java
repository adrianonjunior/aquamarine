package com.aquamarine.client.manual;

import com.aquamarine.Aquamarine;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;

/**
 * Texto do Manual do Aquamarine, em {@code assets/aquamarine/manual/<idioma>.json} (sem o idioma, inglês; sem inglês,
 * português). Cada capítulo é uma lista de elementos que a tela distribui pelas páginas: título, texto, passo numerado,
 * imagem (um print de {@code textures/gui/manual/}), receita, fileira de itens, dica e quebra de página. Nos textos,
 * {@code **assim**} sai em negrito na cor do título.
 */
public final class ManualContent {
	public enum Kind { CHAPTER, TITLE, TEXT, STEP, IMAGE, RECIPE, ITEMS, TIP, BREAK }

	/** Um elemento; só os campos do seu tipo vêm preenchidos. */
	public record Element(Kind kind, String text, String icon, String image, String recipe, String station, List<String> items) {
		static Element of(Kind kind, String text) {
			return new Element(kind, text, null, null, null, null, List.of());
		}
	}

	public record Chapter(String title, String icon, int color, List<Element> content) {}

	public final String title;
	public final String subtitle;
	public final List<Element> intro;
	public final List<Chapter> chapters;

	private ManualContent(String title, String subtitle, List<Element> intro, List<Chapter> chapters) {
		this.title = title;
		this.subtitle = subtitle;
		this.intro = intro;
		this.chapters = chapters;
	}

	public static ManualContent load() {
		Minecraft mc = Minecraft.getInstance();
		String selected = mc.getLanguageManager().getSelected();
		for (String code : new String[] {selected, "en_us", "pt_br"}) {
			Optional<Resource> resource = mc.getResourceManager().getResource(Aquamarine.id("manual/" + code + ".json"));
			if (resource.isEmpty()) {
				continue;
			}
			try (Reader reader = resource.get().openAsReader()) {
				return parse(JsonParser.parseReader(reader).getAsJsonObject());
			} catch (Exception e) {
				Aquamarine.LOGGER.error("Manual: não consegui ler manual/{}.json", code, e);
			}
		}
		return new ManualContent("Aquamarine", "", List.of(Element.of(Kind.TEXT, "?")), List.of());
	}

	private static ManualContent parse(JsonObject root) {
		List<Chapter> chapters = new ArrayList<>();
		for (JsonElement entry : root.getAsJsonArray("chapters")) {
			JsonObject chapter = entry.getAsJsonObject();
			int color = Integer.parseUnsignedInt(string(chapter, "color", "#2a8c9c").substring(1), 16) | 0xFF000000;
			chapters.add(new Chapter(string(chapter, "title", "?"), string(chapter, "icon", null), color, elements(chapter.getAsJsonArray("content"))));
		}
		return new ManualContent(string(root, "title", "Aquamarine"), string(root, "subtitle", ""), elements(root.getAsJsonArray("intro")), chapters);
	}

	private static List<Element> elements(JsonArray array) {
		List<Element> out = new ArrayList<>();
		if (array == null) {
			return out;
		}
		for (JsonElement entry : array) {
			JsonObject o = entry.getAsJsonObject();
			if (o.has("title")) {
				out.add(new Element(Kind.TITLE, string(o, "title", ""), string(o, "icon", null), null, null, null, List.of()));
			} else if (o.has("step")) {
				out.add(Element.of(Kind.STEP, string(o, "step", "")));
			} else if (o.has("image")) {
				out.add(new Element(Kind.IMAGE, string(o, "caption", ""), null, string(o, "image", ""), null, null, List.of()));
			} else if (o.has("recipe")) {
				out.add(new Element(Kind.RECIPE, string(o, "caption", ""), null, null, string(o, "recipe", ""), string(o, "station", null), List.of()));
			} else if (o.has("items")) {
				List<String> items = new ArrayList<>();
				o.getAsJsonArray("items").forEach(item -> items.add(item.getAsString()));
				out.add(new Element(Kind.ITEMS, string(o, "caption", ""), null, null, null, null, items));
			} else if (o.has("tip")) {
				out.add(Element.of(Kind.TIP, string(o, "tip", "")));
			} else if (o.has("break")) {
				out.add(Element.of(Kind.BREAK, ""));
			} else if (o.has("text")) {
				out.add(Element.of(Kind.TEXT, string(o, "text", "")));
			}
		}
		return out;
	}

	private static String string(JsonObject o, String key, String fallback) {
		return o.has(key) ? o.get(key).getAsString() : fallback;
	}

	/** Item pelo id ({@code aquamarine:tide_pump}); vazio se não existir. */
	public static ItemStack stack(String id) {
		Identifier key = id == null ? null : Identifier.tryParse(id);
		if (key == null) {
			return ItemStack.EMPTY;
		}
		return BuiltInRegistries.ITEM.getOptional(key).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}
}
