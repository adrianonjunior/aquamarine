package com.aquamarine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AquamarineConfigTest {
	@TempDir
	Path dir;

	@Test
	void createsFileWithDefaults() {
		Path file = dir.resolve("aquamarine.json");
		AquamarineConfig config = AquamarineConfig.loadFrom(file);
		assertTrue(Files.exists(file));
		assertTrue(config.falseWarning);
		assertTrue(config.weakPoints);
		assertEquals("normal", config.difficulty);
		assertEquals(12, config.scalingCap);
		assertTrue(config.naturalWalkers);
	}

	@Test
	void keepsValuesAndFixesInvalidOnes() throws IOException {
		Path file = dir.resolve("aquamarine.json");
		Files.writeString(file, "{\"falseWarning\": false, \"difficulty\": \"impossivel\", \"scalingCap\": 0}");
		AquamarineConfig config = AquamarineConfig.loadFrom(file);
		assertFalse(config.falseWarning);
		assertEquals("normal", config.difficulty);
		assertEquals(1, config.scalingCap);
		assertTrue(config.naturalWalkers, "campo ausente no arquivo fica com o padrão");
		assertTrue(Files.readString(file).contains("naturalWalkers"), "arquivo regravado com os campos novos");
	}

	@Test
	void brokenFileFallsBackToDefaults() throws IOException {
		Path file = dir.resolve("aquamarine.json");
		Files.writeString(file, "{ isto não é json");
		AquamarineConfig config = AquamarineConfig.loadFrom(file);
		assertEquals("normal", config.difficulty);
	}
}
