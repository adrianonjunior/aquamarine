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
	void everyItemHasNameAndDescriptionInBothLanguages() throws IOException {
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
			// descrição curta do tooltip e do JEI (ItemDescriptions)
			if (!ptBr.has("tooltip.aquamarine." + n)) {
				missing.add("tooltip.aquamarine." + n);
			}
		}
		assertTrue(missing.isEmpty(), "Sem nome ou descrição em lang: " + missing);
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

	/**
	 * Cadeia do aceite da Fase 3 (docs/08-fase3.md): a Escama Elétrica da Moréia faz a Bobina Voltaica, e a
	 * Bobina faz o Propulsor de Maré e a Munição de Choque.
	 */
	@Test
	void phase3AcceptanceChainHasRecipes() throws IOException {
		assertTrue(read(DATA.resolve("recipe/voltaic_coil.json")).toString().contains("aquamarine:electric_scale"),
			"A Bobina Voltaica devia usar a Escama Elétrica");
		assertTrue(read(DATA.resolve("recipe/tide_thruster.json")).toString().contains("aquamarine:voltaic_coil"),
			"O Propulsor de Maré devia usar a Bobina Voltaica");
		assertTrue(read(DATA.resolve("recipe/shock_harpoon_tip.json")).toString().contains("aquamarine:voltaic_coil"),
			"A ponta de Choque devia usar a Bobina Voltaica");
	}

	/** O jogo recusa o mundo inteiro se uma receita com forma tiver símbolo sobrando ou faltando na chave. */
	@Test
	void shapedRecipeKeysMatchPattern() throws IOException {
		List<String> wrong = new ArrayList<>();
		for (Path recipe : jsonFiles(DATA.resolve("recipe"))) {
			JsonObject json = read(recipe);
			if (!json.has("pattern")) {
				continue;
			}
			Set<String> used = new TreeSet<>();
			json.getAsJsonArray("pattern").forEach(row -> row.getAsString().chars()
				.filter(c -> c != ' ').forEach(c -> used.add(String.valueOf((char) c))));
			if (!used.equals(new TreeSet<>(json.getAsJsonObject("key").keySet()))) {
				wrong.add(recipe.getFileName().toString());
			}
		}
		assertTrue(wrong.isEmpty(), "Receitas com chave e padrão diferentes: " + wrong);
	}

	/**
	 * Convenção dos biomas: cada bioma do mod tem a tag {@code minecraft:aquamarine_<nome>}, para
	 * achar com {@code /locate biome #aquamarine_<nome>} (sem namespace o jogo usa minecraft).
	 */
	@Test
	void everyBiomeHasShortLocateTag() throws IOException {
		Path tags = RES.resolve("data/minecraft/tags/worldgen/biome");
		List<String> problems = new ArrayList<>();
		if (Files.isDirectory(tags)) {
			for (Path tag : jsonFiles(tags)) {
				String n = name(tag);
				if (n.startsWith("aquamarine_") && read(tag).getAsJsonArray("values").isEmpty()) {
					problems.add(n + " está vazia");
				}
			}
		}
		Path biomes = DATA.resolve("worldgen/biome");
		if (Files.isDirectory(biomes)) {
			for (Path biome : jsonFiles(biomes)) {
				Path tag = tags.resolve("aquamarine_" + name(biome) + ".json");
				if (!Files.exists(tag) || !Files.readString(tag).contains("aquamarine:" + name(biome))) {
					problems.add("bioma " + name(biome) + " sem a tag aquamarine_" + name(biome));
				}
			}
		}
		assertTrue(problems.isEmpty(), "Tags de bioma: " + problems);
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
