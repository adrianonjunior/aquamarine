package com.aquamarine.event;

import com.aquamarine.Aquamarine;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

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

	private static final int STIRS_TICK = 100;
	private static final int AWAKENS_TICK = 200;
	private static final int FALSE_SILENCE_TICK = AWAKENS_TICK + 400;

	private static final List<Sequence> ACTIVE = new ArrayList<>();

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(KrakenSummoning::tick);
	}

	/** Começa a sequência em {@code pos}. {@code fake} = aviso falso (o Kraken não vem). */
	public static void start(ServerLevel level, BlockPos pos, boolean fake) {
		ACTIVE.add(new Sequence(level, pos.immutable(), fake));
		broadcast(level, pos, Component.translatable("message.aquamarine.summon.calm").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
			SoundEvents.CONDUIT_DEACTIVATE, 0.6F);
	}

	/** Já há uma invocação (real ou falsa) acontecendo perto de {@code pos}. */
	public static boolean isActiveNear(ServerLevel level, BlockPos pos) {
		return ACTIVE.stream().anyMatch(s -> s.level == level && s.pos.closerThan(pos, MESSAGE_RADIUS));
	}

	/**
	 * Há uma coluna de água aberta com pelo menos {@link #REQUIRED_DEPTH} blocos de profundidade
	 * a até {@link #DEEP_WATER_RADIUS} blocos de {@code pos} (amostra a cada 4 blocos).
	 */
	public static boolean hasDeepWater(ServerLevel level, BlockPos pos) {
		for (int dx = -DEEP_WATER_RADIUS; dx <= DEEP_WATER_RADIUS; dx += 4) {
			for (int dz = -DEEP_WATER_RADIUS; dz <= DEEP_WATER_RADIUS; dz += 4) {
				if (dx * dx + dz * dz > DEEP_WATER_RADIUS * DEEP_WATER_RADIUS) {
					continue;
				}
				int x = pos.getX() + dx;
				int z = pos.getZ() + dz;
				int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
				int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z);
				if (surface - floor + 1 >= REQUIRED_DEPTH
					&& level.getFluidState(new BlockPos(x, surface, z)).is(FluidTags.WATER)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void tick(MinecraftServer server) {
		ACTIVE.removeIf(Sequence::tick);
	}

	private static void broadcast(ServerLevel level, BlockPos pos, Component message, SoundEvent sound, float pitch) {
		for (ServerPlayer player : level.players()) {
			if (player.blockPosition().closerThan(pos, MESSAGE_RADIUS)) {
				player.sendSystemMessage(message);
				level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.HOSTILE, 1.0F, pitch);
			}
		}
	}

	/**
	 * Ponto onde o Kraken entra na luta. O Kraken ainda não existe (fora da Entrega 1):
	 * quem implementá-lo troca este corpo pelo surgimento dele na água funda mais próxima.
	 */
	private static void spawnKraken(ServerLevel level, BlockPos altar) {
		Aquamarine.LOGGER.info("Invocação do Kraken concluída em {} (o Kraken ainda não foi implementado).", altar);
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
			}
			if (ticks >= STIRS_TICK && ticks < AWAKENS_TICK && ticks % 5 == 0) {
				level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
					12, 2.5, 0.5, 2.5, 0.2);
			}
			if (ticks == AWAKENS_TICK) {
				awaken();
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
