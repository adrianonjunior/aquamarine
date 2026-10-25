package com.aquamarine.command;

import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.KrakenEggEntity;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.TentacleEntity;
import com.aquamarine.entity.YoungKrakenEntity;
import com.aquamarine.entity.boss.KrakenAttackType;
import com.aquamarine.event.KrakenSummoning;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;

/**
 * Comandos de teste do Kraken (só operadores):
 * {@code /aquamarine kraken} faz o Kraken surgir 10 blocos abaixo de quem digita (precisa de água);
 * {@code /aquamarine kraken anim <nome>} dispara uma animação do contrato no Kraken mais próximo;
 * {@code /aquamarine kraken arena} constrói uma piscina de vidro de teste e põe o Kraken no centro.
 */
public final class KrakenCommand {
	/** Arena de teste: piscina de vidro 45 x 40 x 45, grande o bastante para a arena de 32 blocos. */
	public static final int ARENA_HALF = 48;
	public static final int ARENA_DEPTH = 64;

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> register(dispatcher));
	}

	private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("aquamarine")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.literal("kraken")
				.executes(ctx -> spawn(ctx.getSource()))
				.then(Commands.literal("arena").executes(ctx -> arena(ctx.getSource())))
				.then(Commands.literal("anim")
					.then(Commands.argument("nome", StringArgumentType.word())
						.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(KrakenEntity.ANIMATION_NAMES, builder))
						.executes(ctx -> anim(ctx.getSource(), StringArgumentType.getString(ctx, "nome")))))
				.then(Commands.literal("attack")
					.then(Commands.argument("nome", StringArgumentType.word())
						.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(KrakenAttackType.names(), builder))
						.executes(ctx -> attack(ctx.getSource(), StringArgumentType.getString(ctx, "nome")))))
				.then(creature("tentacle", ModEntities.TENTACLE, TentacleEntity.ANIMATION_NAMES))
				.then(creature("young", ModEntities.YOUNG_KRAKEN, YoungKrakenEntity.ANIMATION_NAMES))
				.then(creature("egg", ModEntities.KRAKEN_EGG, KrakenEggEntity.ANIMATION_NAMES))
				.then(Commands.literal("ai")
					.then(Commands.literal("on").executes(ctx -> ai(ctx.getSource(), true)))
					.then(Commands.literal("off").executes(ctx -> ai(ctx.getSource(), false))))));
	}

	private static int spawn(CommandSourceStack source) {
		ServerLevel level = source.getLevel();
		BlockPos pos = BlockPos.containing(source.getPosition()).below(10);
		KrakenEntity kraken = KrakenSummoning.spawnAt(level, pos, BlockPos.containing(source.getPosition()));
		if (kraken == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.kraken.none"));
			return 0;
		}
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.kraken.spawned", pos.getX(), pos.getY(), pos.getZ()), true);
		return 1;
	}

	private static int anim(CommandSourceStack source, String name) {
		ServerLevel level = source.getLevel();
		KrakenEntity kraken = level.getEntities(ModEntities.KRAKEN, k -> true).stream()
			.min(java.util.Comparator.comparingDouble(k -> k.distanceToSqr(source.getPosition()))).orElse(null);
		if (kraken == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.kraken.none"));
			return 0;
		}
		if (!kraken.playAnimation(name)) {
			source.sendFailure(Component.translatable("commands.aquamarine.kraken.anim_unknown", name));
			return 0;
		}
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.kraken.anim", name), false);
		return 1;
	}

	/** {@code <nome>} cria a criatura 6 blocos à frente; {@code <nome> anim <animação>} dispara na mais próxima. */
	private static <T extends Entity> com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> creature(
		String name, EntityType<T> type, List<String> animations) {
		return Commands.literal(name)
			.executes(ctx -> spawnCreature(ctx.getSource(), type, name))
			.then(Commands.literal("anim")
				.then(Commands.argument("nome", StringArgumentType.word())
					.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(animations, builder))
					.executes(ctx -> creatureAnim(ctx.getSource(), type, StringArgumentType.getString(ctx, "nome"), animations))));
	}

	private static <T extends Entity> int spawnCreature(CommandSourceStack source, EntityType<T> type, String name) {
		ServerLevel level = source.getLevel();
		Entity entity = type.create(level, EntitySpawnReason.COMMAND);
		if (entity == null) {
			return 0;
		}
		Vec3 look = source.getEntity() != null ? source.getEntity().getLookAngle().multiply(1, 0, 1).normalize() : new Vec3(0, 0, 1);
		Vec3 pos = source.getPosition().add(look.scale(6.0));
		entity.snapTo(pos.x, pos.y, pos.z, source.getRotation().y + 180.0F, 0.0F);
		level.addFreshEntity(entity);
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.kraken.creature", name), true);
		return 1;
	}

	private static <T extends Entity> int creatureAnim(CommandSourceStack source, EntityType<T> type, String animation, List<String> names) {
		T entity = source.getLevel().getEntities(type, e -> true).stream()
			.min(java.util.Comparator.comparingDouble(e -> e.distanceToSqr(source.getPosition()))).orElse(null);
		if (entity == null || !names.contains(animation)) {
			source.sendFailure(Component.translatable("commands.aquamarine.kraken.anim_unknown", animation));
			return 0;
		}
		boolean ok = entity instanceof TentacleEntity t ? t.playAnimation(animation)
			: entity instanceof YoungKrakenEntity y ? y.playAnimation(animation)
			: entity instanceof KrakenEggEntity e && e.playAnimation(animation);
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.kraken.anim", animation), false);
		return ok ? 1 : 0;
	}

	private static KrakenEntity nearestKraken(CommandSourceStack source) {
		return source.getLevel().getEntities(ModEntities.KRAKEN, k -> true).stream()
			.min(java.util.Comparator.comparingDouble(k -> k.distanceToSqr(source.getPosition()))).orElse(null);
	}

	/** {@code attack <nome>}: dispara o ataque (aviso e golpe) no jogador que digitou, ou no mais perto do Kraken. */
	private static int attack(CommandSourceStack source, String name) {
		KrakenEntity kraken = nearestKraken(source);
		if (kraken == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.kraken.none"));
			return 0;
		}
		KrakenAttackType type = KrakenAttackType.byName(name);
		if (type == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.kraken.attack_unknown", name));
			return 0;
		}
		ServerPlayer target = source.getPlayer();
		if (target == null) {
			target = source.getLevel().getNearestPlayer(kraken, 64.0) instanceof ServerPlayer p ? p : null;
		}
		if (target == null || !kraken.attacks().force(source.getLevel(), type, target)) {
			source.sendFailure(Component.translatable("commands.aquamarine.kraken.attack_failed", name));
			return 0;
		}
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.kraken.attack", name), false);
		return 1;
	}

	/** {@code ai on|off}: liga ou desliga o sorteio automático de ataques. */
	private static int ai(CommandSourceStack source, boolean on) {
		KrakenEntity kraken = nearestKraken(source);
		if (kraken == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.kraken.none"));
			return 0;
		}
		kraken.attacks().setEnabled(on);
		source.sendSuccess(() -> Component.translatable(on ? "commands.aquamarine.kraken.ai_on" : "commands.aquamarine.kraken.ai_off"), false);
		return 1;
	}

	private static int arena(CommandSourceStack source) {
		ServerLevel level = source.getLevel();
		BlockPos origin = BlockPos.containing(source.getPosition());
		buildArena(level, origin);
		BlockPos center = origin.offset(0, ARENA_DEPTH - 26, 0);
		KrakenSummoning.spawnAt(level, center, origin);
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.kraken.arena"), true);
		return 1;
	}

	/** Casca de vidro cheia de água; {@code origin} é o centro do chão. */
	public static void buildArena(ServerLevel level, BlockPos origin) {
		buildArena(level, origin, ARENA_HALF, ARENA_DEPTH);
	}

	/** Como {@link #buildArena(ServerLevel, BlockPos)}, com meia-largura e profundidade à escolha. */
	public static void buildArena(ServerLevel level, BlockPos origin, int arenaHalf, int arenaDepth) {
		BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
		for (int dx = -arenaHalf; dx <= arenaHalf; dx++) {
			for (int dz = -arenaHalf; dz <= arenaHalf; dz++) {
				for (int dy = 0; dy <= arenaDepth; dy++) {
					boolean wall = Math.abs(dx) == arenaHalf || Math.abs(dz) == arenaHalf || dy == 0;
					p.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
					level.setBlock(p, wall ? Blocks.GLASS.defaultBlockState() : dy == arenaDepth ? Blocks.AIR.defaultBlockState() : Blocks.WATER.defaultBlockState(), 2);
				}
			}
		}
	}

	private KrakenCommand() {}
}
