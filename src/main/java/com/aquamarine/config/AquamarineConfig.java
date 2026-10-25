package com.aquamarine.config;

import com.aquamarine.Aquamarine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuração do mod em {@code config/aquamarine.json}. Campos e padrões em docs/06-tecnico.md.
 * O arquivo é criado na primeira execução e regravado ao carregar, para ganhar campos novos.
 */
public final class AquamarineConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static AquamarineConfig instance = new AquamarineConfig();

	/** Mensagens de invocação no chat sem o Kraken aparecer (aviso falso). */
	public boolean falseWarning = true;
	/** Nós de Maré (pontos fracos do escudo do Kraken). */
	public boolean weakPoints = true;
	/** {@code normal} ou {@code abissal}. */
	public String difficulty = "normal";
	/** Máximo de jogadores considerados na escala de vida e escudo. */
	public int scalingCap = 12;
	/** Liga ou desliga o surgimento natural do Walker. */
	public boolean naturalWalkers = true;
	/** Só o dono da Baleia-de-Nereia pilota; os outros só vão de passageiro. */
	public boolean whaleOwnerOnlyPilots = true;
	/** Evento "Alerta de Moréia" da Vila (uma vez a cada 3 dias de jogo, em noite de tempestade). */
	public boolean harborAlert = true;
	/** A luta do Kraken traz tempestade (trovoada a partir da fase 2) e raios em volta da arena. */
	public boolean krakenStorm = true;
	/** A luta do Kraken marca o mundo: recife morto, algas arrancadas, crateras (também precisa de mob_griefing). */
	public boolean krakenWorldDamage = true;

	public static AquamarineConfig get() {
		return instance;
	}

	public static void load() {
		instance = loadFrom(FabricLoader.getInstance().getConfigDir().resolve(Aquamarine.MOD_ID + ".json"));
	}

	public static AquamarineConfig loadFrom(Path file) {
		AquamarineConfig config = new AquamarineConfig();
		if (Files.exists(file)) {
			try (Reader reader = Files.newBufferedReader(file)) {
				AquamarineConfig read = GSON.fromJson(reader, AquamarineConfig.class);
				if (read != null) {
					config = read;
				}
			} catch (IOException | JsonParseException e) {
				Aquamarine.LOGGER.error("Não foi possível ler {}; usando os valores padrão.", file, e);
				return config;
			}
		}
		config.sanitize();
		config.save(file);
		return config;
	}

	public void save(Path file) {
		try {
			Files.createDirectories(file.getParent());
			try (Writer writer = Files.newBufferedWriter(file)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			Aquamarine.LOGGER.error("Não foi possível gravar {}.", file, e);
		}
	}

	public boolean isAbyssal() {
		return "abissal".equals(difficulty);
	}

	private void sanitize() {
		if (!"normal".equals(difficulty) && !"abissal".equals(difficulty)) {
			difficulty = "normal";
		}
		scalingCap = Math.max(1, scalingCap);
	}
}
