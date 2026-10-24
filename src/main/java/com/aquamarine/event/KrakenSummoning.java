package com.aquamarine.event;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.ModEntities;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Sequência de invocação do Kraken (docs/03-invocacao-e-chat.md): mensagens aos jogadores a
 * até 128 blocos em 0, 5 e 10 s. A versão falsa (aviso falso) repete as três mensagens, não
 * traz o Kraken e termina com "As águas voltam ao silêncio..." 20 s depois.
 */
public final class KrakenSummoning {
	public static final int MESSAGE_RADIUS = 128;
	/** Água aberta exigida pelo Chamariz (docs/02-kraken.md, "Arena"). */
	public static final int REQUIRED_DEPTH = 30;
	public static final int DEEP_WATER_RADIUS = 48;
	/** Quantos blocos abaixo da superfície o Kraken surge. */
	private static final int KRAKEN_SUBMERGED_DEPTH = 24;

	private static final int STIRS_TICK = 100;
	private static final int AWAKENS_TICK = 200;
	private static final int FALSE_SILENCE_TICK = AWAKENS_TICK + 400;

	private static final List<Sequence> ACTIVE = new ArrayList<>();

	public static void register() {
		SummonEffectPayload.register();
		ServerTickEvents.END_SERVER_TICK.register(KrakenSummoning::tick);
		ServerTickEvents.END_SERVER_TICK.register(FalseWarning::tick);
	}

	/** Começa a sequência em {@code pos}. {@code fake} = aviso falso (o Kraken não vem). */
	public static void start(ServerLevel level, BlockPos pos, boolean fake) {
		ACTIVE.add(new Sequence(level, pos.immutable(), fake));
		// 0 s: os sons do oceano param até o despertar.
		sendEffect(level, pos, SummonEffectPayload.SILENCE, AWAKENS_TICK);
		broadcast(level, pos, Component.translatable("message.aquamarine.summon.calm").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
			SoundEvents.CONDUIT_DEACTIVATE, 0.6F);
	}

	/** Já há uma invocação (real ou falsa) acontecendo perto de {@code pos}. */
	public static boolean isActiveNear(ServerLevel level, BlockPos pos) {
		return ACTIVE.stream().anyMatch(s -> s.level == level && s.pos.closerThan(pos, MESSAGE_RADIUS));
	}

	/** Há água funda (ver {@link #findDeepWater}) perto de {@code pos}. */
	public static boolean hasDeepWater(ServerLevel level, BlockPos pos) {
		return findDeepWater(level, pos) != null;
	}

	/**
	 * Superfície da coluna de água aberta com pelo menos {@link #REQUIRED_DEPTH} blocos de
	 * profundidade mais próxima de {@code pos}, a até {@link #DEEP_WATER_RADIUS} blocos
	 * (amostra a cada 4 blocos), ou {@code null}.
	 */
	public static BlockPos findDeepWater(ServerLevel level, BlockPos pos) {
		BlockPos best = null;
		double bestDist = Double.MAX_VALUE;
		for (int dx = -DEEP_WATER_RADIUS; dx <= DEEP_WATER_RADIUS; dx += 4) {
			for (int dz = -DEEP_WATER_RADIUS; dz <= DEEP_WATER_RADIUS; dz += 4) {
				double dist = dx * dx + dz * dz;
				if (dist > DEEP_WATER_RADIUS * DEEP_WATER_RADIUS || dist >= bestDist) {
					continue;
				}
				int x = pos.getX() + dx;
				int z = pos.getZ() + dz;
				int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
				int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z);
				if (surface - floor + 1 >= REQUIRED_DEPTH
					&& level.getFluidState(new BlockPos(x, surface, z)).is(FluidTags.WATER)) {
					best = new BlockPos(x, surface, z);
					bestDist = dist;
				}
			}
		}
		return best;
	}

	private static void tick(MinecraftServer server) {
		ACTIVE.removeIf(Sequence::tick);
	}

	/** Manda um efeito de tela aos jogadores a até {@link #MESSAGE_RADIUS} blocos de {@code pos}. */
	public static void sendEffect(ServerLevel level, BlockPos pos, int effect, int ticks) {
		for (ServerPlayer player : level.players()) {
			if (player.blockPosition().closerThan(pos, MESSAGE_RADIUS)) {
				ServerPlayNetworking.send(player, new SummonEffectPayload(effect, ticks));
			}
		}
	}

	private static void broadcast(ServerLevel level, BlockPos pos, Component message, SoundEvent sound, float pitch) {
		for (ServerPlayer player : level.players()) {
			if (player.blockPosition().closerThan(pos, MESSAGE_RADIUS)) {
				player.sendSystemMessage(message);
				level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.HOSTILE, 1.0F, pitch);
			}
		}
	}

	/** Kraken surge submerso na água funda mais próxima do altar (fase 1: só os tentáculos saem). */
	private static void spawnKraken(ServerLevel level, BlockPos altar) {
		BlockPos surface = findDeepWater(level, altar);
		if (surface == null) {
			Aquamarine.LOGGER.warn("Invocação do Kraken em {} sem água funda; o Kraken não surgiu.", altar);
			return;
		}
		spawnAt(level, surface.below(KRAKEN_SUBMERGED_DEPTH), altar);
	}

	/** Cria o Kraken em {@code pos}; a escala conta os jogadores a até a arena do ponto dado. */
	public static KrakenEntity spawnAt(ServerLevel level, BlockPos pos, BlockPos nearPlayersOf) {
		KrakenEntity kraken = ModEntities.KRAKEN.create(level, EntitySpawnReason.EVENT);
		if (kraken == null) {
			return null;
		}
		kraken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		int players = (int) level.players().stream()
			.filter(p -> p.isAlive() && !p.isSpectator() && p.blockPosition().closerThan(nearPlayersOf, KrakenEntity.ARENA_RADIUS))
			.count();
		kraken.setUp(level, players);
		level.addFreshEntity(kraken);
		return kraken;
	}

	private static final class Sequence {
		final ServerLevel level;
		final BlockPos pos;
		final boolean fake;
		int ticks;

		Sequence(ServerLevel level, BlockPos pos, boolean fake) {
			this.level = level;
			this.pos = pos;
			this.fake = fake;
		}

		/** Avança um tick; devolve true quando terminou. */
		boolean tick() {
			ticks++;
			if (ticks == STIRS_TICK) {
				broadcast(level, pos, Component.translatable("message.aquamarine.summon.stirs").withStyle(ChatFormatting.BLUE),
					SoundEvents.ELDER_GUARDIAN_AMBIENT, 0.5F);
				sendEffect(level, pos, SummonEffectPayload.SHAKE, AWAKENS_TICK - STIRS_TICK);
			}
			if (ticks >= STIRS_TICK && ticks < AWAKENS_TICK && ticks % 5 == 0) {
				level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
					12, 2.5, 0.5, 2.5, 0.2);
			}
			if (ticks == AWAKENS_TICK) {
				awaken();
				if (fake) {
					sendEffect(level, pos, SummonEffectPayload.DARKEN, FALSE_SILENCE_TICK - AWAKENS_TICK);
				}
				if (!fake) {
					spawnKraken(level, pos);
					return true;
				}
			}
			if (fake && ticks == FALSE_SILENCE_TICK) {
				broadcast(level, pos, Component.translatable("message.aquamarine.summon.silence").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
					SoundEvents.CONDUIT_AMBIENT, 0.8F);
				return true;
			}
			return false;
		}

		private void awaken() {
			broadcast(level, pos, Component.translatable("message.aquamarine.summon.awakens").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
				SoundEvents.LIGHTNING_BOLT_THUNDER, 0.6F);
			Component title = Component.translatable("title.aquamarine.kraken").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD);
			for (ServerPlayer player : level.players()) {
				if (player.blockPosition().closerThan(pos, MESSAGE_RADIUS)) {
					player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 60, 20));
					player.connection.send(new ClientboundSetTitleTextPacket(title));
				}
			}
			MinecraftServer server = level.getServer();
			server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "weather thunder 6000");
		}
	}

	private KrakenSummoning() {}
}
