package com.aquamarine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Confere os recursos do mod sem abrir o jogo: modelos e texturas existem, todo item tem
 * nome em português e inglês, receitas apontam para itens que existem. Pega o erro mais comum
 * de quem adiciona item novo (esquecer um dos arquivos).
 */
class ResourceConsistencyTest {
	private static final Path RES = Path.of("src", "main", "resources");
	private static final Path ASSETS = RES.resolve("assets/aquamarine");
	private static final Path DATA = RES.resolve("data/aquamarine");

	private static JsonObject read(Path file) throws IOException {
		return JsonParser.parseString(Files.readString(file)).getAsJsonObject();
	}

	private static List<Path> jsonFiles(Path dir) throws IOException {
		try (Stream<Path> files = Files.walk(dir)) {
			return files.filter(p -> p.toString().endsWith(".json")).toList();
		}
	}

	private static String name(Path file) {
		String n = file.getFileName().toString();
		return n.substring(0, n.length() - ".json".length());
	}

	/** "aquamarine:item/x" -> caminho do arquivo dentro de assets/aquamarine/{folder}/... */
	private static Path asset(String id, String folder, String ext) {
		String path = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
		return ASSETS.resolve(folder).resolve(path + ext);
	}

	private static void collectModelIds(JsonElement element, List<String> out) {
		if (element.isJsonObject()) {
			JsonObject obj = element.getAsJsonObject();
			if (obj.has("model") && obj.get("model").isJsonPrimitive()) {
				out.add(obj.get("model").getAsString());
			}
			obj.entrySet().forEach(e -> collectModelIds(e.getValue(), out));
		} else if (element.isJsonArray()) {
			element.getAsJsonArray().forEach(e -> collectModelIds(e, out));
		}
	}

	@Test
	void itemAndBlockModelsPointToExistingFiles() throws IOException {
		List<String> missing = new ArrayList<>();
		List<Path> sources = new ArrayList<>(jsonFiles(ASSETS.resolve("items")));
		sources.addAll(jsonFiles(ASSETS.resolve("blockstates")));
		for (Path file : sources) {
			List<String> models = new ArrayList<>();
			collectModelIds(read(file), models);
			for (String model : models) {
				if (model.startsWith("aquamarine:") && !Files.exists(asset(model, "models", ".json"))) {
					missing.add(file.getFileName() + " -> " + model);
				}
			}
		}
		for (Path model : jsonFiles(ASSETS.resolve("models"))) {
			JsonObject json = read(model);
			if (json.has("textures")) {
				for (Map.Entry<String, JsonElement> tex : json.getAsJsonObject("textures").entrySet()) {
					String id = tex.getValue().getAsString();
					if (id.startsWith("aquamarine:") && !Files.exists(asset(id, "textures", ".png"))) {
						missing.add(model.getFileName() + " -> " + id);
					}
				}
			}
		}
		assertTrue(missing.isEmpty(), "Arquivos faltando: " + missing);
	}

	@Test
	void everyItemHasPortugueseAndEnglishName() throws IOException {
		JsonObject ptBr = read(ASSETS.resolve("lang/pt_br.json"));
		JsonObject enUs = read(ASSETS.resolve("lang/en_us.json"));
		assertEquals(new TreeSet<>(ptBr.keySet()), new TreeSet<>(enUs.keySet()), "pt_br e en_us com chaves diferentes");

		Set<String> blocks = new TreeSet<>();
		jsonFiles(ASSETS.resolve("blockstates")).forEach(f -> blocks.add(name(f)));
		List<String> missing = new ArrayList<>();
		for (Path item : jsonFiles(ASSETS.resolve("items"))) {
			String n = name(item);
			String key = (blocks.contains(n) ? "block" : "item") + ".aquamarine." + n;
			if (!ptBr.has(key)) {
				missing.add(key);
			}
		}
		assertTrue(missing.isEmpty(), "Sem nome em lang: " + missing);
	}

	@Test
	void recipesProduceExistingItems() throws IOException {
		List<String> missing = new ArrayList<>();
		for (Path recipe : jsonFiles(DATA.resolve("recipe"))) {
			String id = read(recipe).getAsJsonObject("result").get("id").getAsString();
			if (id.startsWith("aquamarine:") && !Files.exists(asset(id, "items", ".json"))) {
				missing.add(recipe.getFileName() + " -> " + id);
			}
		}
		assertTrue(missing.isEmpty(), "Receitas com resultado inexistente: " + missing);
	}

	@Test
	void equipmentAssetsHaveTextures() throws IOException {
		List<String> missing = new ArrayList<>();
		for (Path equipment : jsonFiles(ASSETS.resolve("equipment"))) {
			for (Map.Entry<String, JsonElement> layer : read(equipment).getAsJsonObject("layers").entrySet()) {
				for (JsonElement entry : layer.getValue().getAsJsonArray()) {
					String id = entry.getAsJsonObject().get("texture").getAsString();
					Path png = asset(id, "textures/entity/equipment/" + layer.getKey(), ".png");
					if (!Files.exists(png)) {
						missing.add(equipment.getFileName() + " -> " + png);
					}
				}
			}
		}
		assertTrue(missing.isEmpty(), "Camadas de equipamento sem textura: " + missing);
	}
}
