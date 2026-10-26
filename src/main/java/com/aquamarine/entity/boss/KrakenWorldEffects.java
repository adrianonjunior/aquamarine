package com.aquamarine.entity.boss;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.ModBuildingBlocks;
import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.entity.KrakenEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.WeatherData;
import net.minecraft.world.phys.Vec3;

/**
 * A luta do Kraken marca o mundo (docs/02-kraken.md, "A luta muda o mundo"): tempestade enquanto há
 * jogadores na arena (trovoada a partir da fase 2), raios caindo em volta, o recife morrendo e as algas
 * arrancadas perto do Kraken, crateras das Pancadas e das Emboscadas e a onda de choque da troca de fase.
 * O céu tingido por fase é do cliente ({@code KrakenSky}). A destruição precisa da regra {@code mob_griefing}
 * e da opção {@code krakenWorldDamage}; a tempestade, da opção {@code krakenStorm}.
 */
public final class KrakenWorldEffects {
	/** Duração da tempestade, renovada enquanto a luta dura; quando o Kraken some, o tempo limpa. */
	public static final int STORM_TICKS = 2400;
	/** Raio do recife que morre em volta do Kraken (cresce com a fase). */
	public static final int BLIGHT_RADIUS = 20;
	public static final int BLIGHT_RADIUS_PER_PHASE = 6;
	/** Blocos sorteados a cada meio segundo para o recife morrer e as algas soltarem. */
	public static final int BLIGHT_SAMPLES = 40;
	/** Raios de ambiente: um a cada 4 a 10 s na fase 2, mais rápido na 3; nunca a menos disto de um jogador. */
	public static final int LIGHTNING_MIN_TICKS = 80;
	public static final int LIGHTNING_MAX_TICKS = 200;
	public static final double LIGHTNING_SAFE_DISTANCE = 8.0;
	/** Dureza máxima que as crateras quebram (pedra 1,5, madeira 2; minérios e obsidiana ficam). */
	public static final float CRATER_MAX_HARDNESS = 2.0F;
	/** Onda de choque da troca de fase: quebra o que é frágil (vidro, folhas, gelo, plantas). */
	public static final int SHOCKWAVE_RADIUS = 14;
	public static final float SHOCKWAVE_MAX_HARDNESS = 0.6F;

	private int nextLightning = LIGHTNING_MIN_TICKS;
	private boolean storm;

	/** Chamado a cada tick do Kraken vivo no servidor. */
	public void tick(KrakenEntity kraken, ServerLevel level, int phase, List<ServerPlayer> inArena) {
		if (inArena.isEmpty() || !kraken.isAlive() || level.dimension() != Level.OVERWORLD) {
			return;
		}
		RandomSource random = kraken.getRandom();
		if (AquamarineConfig.get().krakenStorm) {
			boolean thunder = phase >= 2;
			WeatherData weather = level.getServer().getWeatherData();
			if (!weather.isRaining() || thunder && !weather.isThundering() || level.getGameTime() % 200 == 0) {
				level.getServer().setWeatherParameters(0, STORM_TICKS, true, thunder);
				storm = true;
			}
			if (thunder && --nextLightning <= 0) {
				strikeAround(level, kraken.position(), inArena, random);
				int span = LIGHTNING_MAX_TICKS - LIGHTNING_MIN_TICKS;
				nextLightning = (int) ((LIGHTNING_MIN_TICKS + random.nextInt(span)) / (phase >= 3 ? 1.6F : 1.0F));
			}
		}
		if (level.getGameTime() % 10 == 0 && destructionAllowed(level)) {
			blight(level, kraken.blockPosition(), BLIGHT_RADIUS + BLIGHT_RADIUS_PER_PHASE * (phase - 1), random);
		}
	}

	/** O Kraken morreu ou recuou: a tempestade que ele trouxe passa. */
	public void endStorm(ServerLevel level) {
		if (storm) {
			level.getServer().setWeatherParameters(6000, 0, false, false);
			storm = false;
		}
	}

	public static boolean destructionAllowed(ServerLevel level) {
		return AquamarineConfig.get().krakenWorldDamage && level.getGameRules().get(GameRules.MOB_GRIEFING);
	}

	/** Raio de ambiente num ponto da arena; só de enfeite se cair perto de alguém. */
	public static void strikeAround(ServerLevel level, Vec3 center, List<ServerPlayer> players, RandomSource random) {
		double angle = random.nextDouble() * Math.PI * 2;
		double r = 12.0 + random.nextDouble() * 24.0;
		int x = (int) Math.floor(center.x + Math.cos(angle) * r);
		int z = (int) Math.floor(center.z + Math.sin(angle) * r);
		Vec3 at = new Vec3(x + 0.5, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z + 0.5);
		boolean near = players.stream().anyMatch(p -> p.position().distanceTo(at) < LIGHTNING_SAFE_DISTANCE);
		lightning(level, at, near);
	}

	public static void lightning(ServerLevel level, Vec3 at, boolean visualOnly) {
		LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
		if (bolt != null) {
			bolt.snapTo(at.x, at.y, at.z, 0.0F, 0.0F);
			bolt.setVisualOnly(visualOnly);
			level.addFreshEntity(bolt);
		}
	}

	/** O recife morre e as algas soltam em volta do Kraken (amostras aleatórias, sem varrer tudo). */
	static void blight(ServerLevel level, BlockPos center, int radius, RandomSource random) {
		for (int i = 0; i < BLIGHT_SAMPLES; i++) {
			BlockPos pos = center.offset(random.nextInt(radius * 2 + 1) - radius, random.nextInt(radius + 1) - radius,
				random.nextInt(radius * 2 + 1) - radius);
			if (pos.distSqr(center) > (double) radius * radius) {
				continue;
			}
			BlockState state = level.getBlockState(pos);
			BlockState dead = deadCoral(state);
			if (dead != null) {
				level.setBlock(pos, dead, Block.UPDATE_CLIENTS);
				level.sendParticles(ParticleTypes.ASH, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 4, 0.3, 0.3, 0.3, 0.01);
			} else if (isSeaweed(state)) {
				tearSeaweed(level, pos);
			}
		}
	}

	/** Coral vivo vira o morto de mesmo nome ({@code dead_...}), com os mesmos estados (submerso, direção). */
	public static BlockState deadCoral(BlockState state) {
		if (!state.is(BlockTags.CORALS) && !state.is(BlockTags.CORAL_BLOCKS) && !state.is(BlockTags.WALL_CORALS)) {
			return null;
		}
		Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
		if (id.getPath().startsWith("dead_")) {
			return null;
		}
		return BuiltInRegistries.BLOCK.getOptional(id.withPath("dead_" + id.getPath()))
			.map(dead -> dead.withPropertiesOf(state)).orElse(null);
	}

	private static boolean isSeaweed(BlockState state) {
		return state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS) || state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT);
	}

	/** Arranca a alga: a coluna inteira vira água, sem itens soltos. */
	private static void tearSeaweed(ServerLevel level, BlockPos pos) {
		BlockPos.MutableBlockPos cursor = pos.mutable();
		while (cursor.getY() > level.getMinY() && isSeaweed(level.getBlockState(cursor.below()))) {
			cursor.move(0, -1, 0);
		}
		while (isSeaweed(level.getBlockState(cursor))) {
			level.setBlock(cursor, Blocks.WATER.defaultBlockState(), Block.UPDATE_CLIENTS);
			level.sendParticles(ParticleTypes.BUBBLE, cursor.getX() + 0.5, cursor.getY() + 0.5, cursor.getZ() + 0.5, 3, 0.2, 0.3, 0.2, 0.02);
			cursor.move(0, 1, 0);
		}
	}

	/** Cratera: quebra o que não é duro demais numa esfera (sem itens); abaixo do mar, vira água. */
	public static int crater(ServerLevel level, Vec3 center, double radius, float maxHardness) {
		if (!destructionAllowed(level)) {
			return 0;
		}
		int broken = 0;
		int r = (int) Math.ceil(radius);
		BlockPos origin = BlockPos.containing(center);
		for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-r, -r, -r), origin.offset(r, r, r))) {
			if (pos.distToCenterSqr(center) > radius * radius) {
				continue;
			}
			BlockState state = level.getBlockState(pos);
			if (!breakable(level, pos, state, maxHardness)) {
				continue;
			}
			BlockParticleOption debris = new BlockParticleOption(ParticleTypes.BLOCK, state);
			level.sendParticles(debris, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4, 0.3, 0.3, 0.3, 0.2);
			level.setBlock(pos.immutable(), pos.getY() < level.getSeaLevel() ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(),
				Block.UPDATE_ALL);
			broken++;
		}
		return broken;
	}

	/**
	 * Fase 4 (docs/10-fase4.md, §7): no anel em volta da cratera os vidros e vidraças do jogo racham em vez de quebrar
	 * (os de dentro a cratera já quebrou). Devolve quantos racharam.
	 */
	public static int crackGlass(ServerLevel level, Vec3 center, double inner, double outer) {
		int count = 0;
		int r = (int) Math.ceil(outer);
		BlockPos origin = BlockPos.containing(center);
		for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-r, -r, -r), origin.offset(r, r, r))) {
			double distance = pos.distToCenterSqr(center);
			if (distance > outer * outer || distance < inner * inner) {
				continue;
			}
			BlockState state = level.getBlockState(pos);
			Block cracked = ModBuildingBlocks.cracked(state.getBlock());
			if (cracked != null) {
				level.setBlock(pos.immutable(), cracked.withPropertiesOf(state), Block.UPDATE_ALL);
				count++;
			}
		}
		return count;
	}

	/** Emboscada: a erupção abre uma cratera no fundo do mar embaixo do ponto. */
	public static int seabedCrater(ServerLevel level, Vec3 point, double radius) {
		int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR, (int) Math.floor(point.x), (int) Math.floor(point.z));
		return crater(level, new Vec3(point.x, floor - 1.0, point.z), radius, CRATER_MAX_HARDNESS);
	}

	/** Onda de choque da troca de fase: o frágil quebra e as algas soltam num raio grande. */
	public static int shockwave(ServerLevel level, Vec3 center) {
		if (!destructionAllowed(level)) {
			return 0;
		}
		int broken = crater(level, center, SHOCKWAVE_RADIUS, SHOCKWAVE_MAX_HARDNESS);
		crackGlass(level, center, SHOCKWAVE_RADIUS, SHOCKWAVE_RADIUS * 1.6);
		blight(level, BlockPos.containing(center), SHOCKWAVE_RADIUS, level.getRandom());
		return broken;
	}

	private static boolean breakable(ServerLevel level, BlockPos pos, BlockState state, float maxHardness) {
		if (state.isAir() || state.getBlock() instanceof LiquidBlock || state.hasBlockEntity()) {
			return false;
		}
		if (BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals(Aquamarine.MOD_ID)) {
			return false;
		}
		float hardness = state.getDestroySpeed(level, pos);
		return hardness >= 0.0F && hardness <= maxHardness;
	}
}
