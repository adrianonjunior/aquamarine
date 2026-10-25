package com.aquamarine.gametest;

import com.aquamarine.entity.AbstractWhaleEntity;
import com.aquamarine.entity.MorayEntity;
import com.aquamarine.entity.SpermWhaleEntity;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

/**
 * As criaturas do oceano nascem sozinhas (docs/00-decisoes.md, "Criaturas do oceano"): num mundo normal,
 * parado no oceano fundo com o surgimento de mobs ligado, aparecem baleias e Moréias, sem lotar a área;
 * na tempestade, mais Moréias. Abre um mundo próprio (semente fixa, sem estruturas) e acelera o tempo com
 * {@code /tick sprint}. Grava a contagem no log para calibrar a densidade. Sem screenshots.
 */
final class OceanSpawnGameTest {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String SEED = "aquamarine";
	/** Cada medição: {@link #SAMPLES} trechos de {@link #SPRINT_TICKS} ticks (4 minutos de jogo), contando no fim de cada um. */
	private static final int SPRINT_TICKS = 800;
	private static final int SAMPLES = 6;
	private static final double RADIUS = 128.0;

	static void run(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().adjustSettings(OceanSpawnGameTest::normalWorld).create()) {
			TestServerContext server = world.getServer();
			world.getConnection().waitForChunksRender();
			server.runCommand("gamemode creative @a");
			server.runCommand("difficulty normal");
			server.runCommand("gamerule spawn_mobs true");
			server.runCommand("gamerule spawn_monsters true");
			server.runCommand("time set midnight");
			server.runCommand("weather clear");

			BlockPos deep = server.computeOnServer(s -> {
				Pair<BlockPos, Holder<Biome>> found = s.overworld().findClosestBiome3d(biome -> biome.is(BiomeTags.IS_DEEP_OCEAN),
					player(s).blockPosition(), 6400, 32, 64);
				return found == null ? null : found.getFirst();
			});
			check(deep != null, "Oceano: nenhum oceano fundo a 6400 blocos");
			int sea = server.computeOnServer(s -> s.overworld().getSeaLevel());
			server.runCommand(String.format(Locale.ROOT, "tp @p %.1f %d %.1f", deep.getX() + 0.5, sea - 4, deep.getZ() + 0.5));
			world.getConnection().waitForChunksRender();
			context.waitTicks(40);

			float[][] calm = measure(context, server);
			server.runCommand("weather thunder");
			float[][] storm = measure(context, server);
			String report = String.format(Locale.ROOT, "oceano fundo em %s (média/máximo em %d amostras): calmo %.1f/%.0f baleias "
				+ "(%.1f/%.0f cachalotes), %.1f/%.0f Moréias; tempestade %.1f/%.0f baleias, %.1f/%.0f Moréias", deep.toShortString(), SAMPLES,
				calm[0][0], calm[1][0], calm[0][1], calm[1][1], calm[0][2], calm[1][2], storm[0][0], storm[1][0], storm[0][2], storm[1][2]);
			LOGGER.info("[Aquamarine] Densidade do oceano: {}", report);

			check(calm[0][0] >= 1.5F, "Oceano: poucas baleias nascendo sozinhas (" + report + ")");
			check(calm[1][0] <= AbstractWhaleEntity.MAX_PER_AREA + 3, "Oceano: baleias demais (" + report + ")");
			check(calm[1][2] >= 1, "Oceano: nenhuma Moréia nasceu sozinha na fossa (" + report + ")");
			check(storm[1][2] <= 8, "Oceano: Moréias demais na tempestade (" + report + ")");
		}
	}

	/** {média, máximo} de {baleias, cachalotes, Moréias} nas amostras. */
	private static float[][] measure(ClientGameTestContext context, TestServerContext server) {
		float[][] out = new float[2][3];
		for (int i = 0; i < SAMPLES; i++) {
			int[] count = sprintAndCount(context, server);
			for (int k = 0; k < 3; k++) {
				out[0][k] += count[k] / (float) SAMPLES;
				out[1][k] = Math.max(out[1][k], count[k]);
			}
		}
		return out;
	}

	/** Roda {@link #SPRINT_TICKS} ticks acelerados e conta {baleias, cachalotes, Moréias} perto do jogador. */
	private static int[] sprintAndCount(ClientGameTestContext context, TestServerContext server) {
		server.runCommand("tick sprint " + SPRINT_TICKS);
		for (int i = 0; i < 600 && server.computeOnServer(s -> s.tickRateManager().isSprinting()); i++) {
			context.waitTicks(20);
		}
		return server.computeOnServer(s -> {
			ServerLevel level = s.overworld();
			AABB box = player(s).getBoundingBox().inflate(RADIUS);
			return new int[] {
				level.getEntitiesOfClass(AbstractWhaleEntity.class, box).size(),
				level.getEntitiesOfClass(SpermWhaleEntity.class, box).size(),
				level.getEntitiesOfClass(MorayEntity.class, box).size()};
		});
	}

	/** Mundo normal (as configurações "consistentes" do teste usam mundo plano). */
	private static void normalWorld(WorldCreationUiState creator) {
		creator.setSeed(SEED);
		creator.setGenerateStructures(false);
		creator.getNormalPresetList().stream().filter(entry -> entry.preset() != null && entry.preset().is(WorldPresets.NORMAL))
			.findFirst().ifPresent(creator::setWorldType);
	}

	private static ServerPlayer player(MinecraftServer s) {
		return s.getPlayerList().getPlayers().getFirst();
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private OceanSpawnGameTest() {}
}
