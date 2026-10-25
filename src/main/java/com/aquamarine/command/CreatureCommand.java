package com.aquamarine.command;

import com.aquamarine.entity.AbstractWhaleEntity;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.MorayEntity;
import com.aquamarine.entity.SpermWhaleEntity;
import com.aquamarine.entity.WhaleEntity;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Comandos de teste das criaturas da Fase 3 (só operadores), no mesmo {@code /aquamarine} do Kraken:
 * {@code /aquamarine moray} faz a Moréia surgir à frente; {@code moray anim <nome>},
 * {@code moray attack <strike|chain|coil>} e {@code moray ai <on|off>} agem na mais próxima;
 * {@code whale} e {@code sperm_whale} fazem as baleias surgirem ({@code anim <nome>} nelas) e
 * {@code whale tame} doma e sela a Baleia-de-Nereia mais próxima.
 */
public final class CreatureCommand {
	private static final List<String> MORAY_ATTACKS = Arrays.stream(MorayEntity.Attack.values())
		.filter(a -> a != MorayEntity.Attack.NONE).map(a -> a.name().toLowerCase(Locale.ROOT)).toList();

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> register(dispatcher));
	}

	private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("aquamarine")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.literal("moray")
				.executes(ctx -> spawn(ctx.getSource(), ModEntities.MORAY, "commands.aquamarine.moray.spawned"))
				.then(anim(ModEntities.MORAY, MorayEntity.ANIMATION_NAMES))
				.then(Commands.literal("attack")
					.then(Commands.argument("nome", StringArgumentType.word())
						.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(MORAY_ATTACKS, builder))
						.executes(ctx -> morayAttack(ctx.getSource(), StringArgumentType.getString(ctx, "nome")))))
				.then(Commands.literal("ai")
					.then(Commands.literal("on").executes(ctx -> morayAi(ctx.getSource(), true)))
					.then(Commands.literal("off").executes(ctx -> morayAi(ctx.getSource(), false)))))
			.then(Commands.literal("whale")
				.executes(ctx -> spawn(ctx.getSource(), ModEntities.WHALE, "commands.aquamarine.whale.spawned"))
				.then(anim(ModEntities.WHALE, WhaleEntity.ANIMATION_NAMES))
				.then(Commands.literal("tame").executes(ctx -> tameWhale(ctx.getSource()))))
			.then(Commands.literal("sperm_whale")
				.executes(ctx -> spawn(ctx.getSource(), ModEntities.SPERM_WHALE, "commands.aquamarine.whale.spawned"))
				.then(anim(ModEntities.SPERM_WHALE, SpermWhaleEntity.ANIMATION_NAMES))));
	}

	private static <T extends Entity> int spawn(CommandSourceStack source, EntityType<T> type, String message) {
		ServerLevel level = source.getLevel();
		T entity = type.create(level, EntitySpawnReason.COMMAND);
		if (entity == null) {
			return 0;
		}
		Vec3 look = source.getEntity() != null ? source.getEntity().getLookAngle().multiply(1, 0, 1).normalize() : new Vec3(0, 0, 1);
		Vec3 pos = source.getPosition().add(look.scale(6.0));
		entity.snapTo(pos.x, pos.y, pos.z, source.getRotation().y + 180.0F, 0.0F);
		level.addFreshEntity(entity);
		source.sendSuccess(() -> Component.translatable(message, (int) pos.x, (int) pos.y, (int) pos.z), true);
		return 1;
	}

	private static <T extends Entity> LiteralArgumentBuilder<CommandSourceStack> anim(EntityType<T> type, List<String> names) {
		return Commands.literal("anim")
			.then(Commands.argument("nome", StringArgumentType.word())
				.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(names, builder))
				.executes(ctx -> anim(ctx.getSource(), type, StringArgumentType.getString(ctx, "nome"))));
	}

	private static <T extends Entity> int anim(CommandSourceStack source, EntityType<T> type, String name) {
		T entity = nearest(source, type);
		if (entity == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.creature.none"));
			return 0;
		}
		boolean ok = entity instanceof MorayEntity moray ? moray.playAnimation(name)
			: entity instanceof AbstractWhaleEntity whale && whale.playAnimation(name);
		if (!ok) {
			source.sendFailure(Component.translatable("commands.aquamarine.creature.anim_unknown", name));
			return 0;
		}
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.creature.anim", name), false);
		return 1;
	}

	/** {@code whale tame}: doma e sela a Baleia-de-Nereia mais próxima para quem digitou. */
	private static int tameWhale(CommandSourceStack source) {
		WhaleEntity whale = nearest(source, ModEntities.WHALE);
		if (whale == null || !(source.getEntity() instanceof Player player)) {
			source.sendFailure(Component.translatable("commands.aquamarine.creature.none"));
			return 0;
		}
		whale.tame(player);
		whale.setSaddled(true);
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.whale.tamed"), false);
		return 1;
	}

	private static int morayAttack(CommandSourceStack source, String name) {
		MorayEntity moray = nearest(source, ModEntities.MORAY);
		if (moray == null || !MORAY_ATTACKS.contains(name)) {
			source.sendFailure(Component.translatable("commands.aquamarine.creature.none"));
			return 0;
		}
		if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity living) {
			moray.setTarget(living);
		}
		moray.start(source.getLevel(), MorayEntity.Attack.valueOf(name.toUpperCase(Locale.ROOT)));
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.moray.attack", name), false);
		return 1;
	}

	private static int morayAi(CommandSourceStack source, boolean on) {
		MorayEntity moray = nearest(source, ModEntities.MORAY);
		if (moray == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.creature.none"));
			return 0;
		}
		moray.setAiFrozen(!on);
		source.sendSuccess(() -> Component.translatable(on ? "commands.aquamarine.moray.ai_on" : "commands.aquamarine.moray.ai_off"), false);
		return 1;
	}

	private static <T extends Entity> T nearest(CommandSourceStack source, EntityType<T> type) {
		return source.getLevel().getEntities(type, e -> e.isAlive()).stream()
			.min(Comparator.comparingDouble(e -> e.distanceToSqr(source.getPosition()))).orElse(null);
	}

	private CreatureCommand() {}
}
