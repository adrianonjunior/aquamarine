package com.aquamarine.command;

import com.aquamarine.entity.MorayEntity;
import com.aquamarine.harbor.HarborAlert;
import com.aquamarine.harbor.HarborReputation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Comandos de teste do Porto dos Náufragos: {@code /aquamarine harbor alert} começa o Alerta de Moréia no
 * Sino mais próximo (até 48 blocos) e {@code /aquamarine harbor reputation <jogador> [set <pontos>]} lê ou
 * muda a reputação. Só para operadores.
 */
public final class HarborCommand {
	private static final int BELL_SEARCH = 48;

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> register(dispatcher));
	}

	private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		// Operador em cada subcomando, não na raiz: /aquamarine help (ModManual) vale para qualquer jogador.
		dispatcher.register(Commands.literal("aquamarine")
			.then(Commands.literal("harbor").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(Commands.literal("alert").executes(ctx -> alert(ctx.getSource())))
				.then(Commands.literal("reputation")
					.then(Commands.argument("player", EntityArgument.player())
						.executes(ctx -> reputation(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))
						.then(Commands.literal("set")
							.then(Commands.argument("points", IntegerArgumentType.integer(0))
								.executes(ctx -> setReputation(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"),
									IntegerArgumentType.getInteger(ctx, "points")))))))));
	}

	private static int alert(CommandSourceStack source) throws CommandSyntaxException {
		BlockPos origin = BlockPos.containing(source.getPosition());
		BlockPos bell = null;
		for (int radius = 8; radius <= BELL_SEARCH && bell == null; radius += 8) {
			bell = HarborAlert.findBell(source.getLevel(), origin.offset(0, -radius / 2, 0), radius);
		}
		if (bell == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.harbor.no_bell"));
			return 0;
		}
		MorayEntity moray = HarborAlert.start(source.getLevel(), bell);
		if (moray == null) {
			source.sendFailure(Component.translatable("commands.aquamarine.harbor.no_water"));
			return 0;
		}
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.harbor.alert",
			moray.getBlockX(), moray.getBlockY(), moray.getBlockZ()), true);
		return 1;
	}

	private static int reputation(CommandSourceStack source, ServerPlayer player) {
		HarborReputation data = HarborReputation.get(source.getServer());
		int points = data.points(player.getUUID());
		source.sendSuccess(() -> Component.translatable("commands.aquamarine.harbor.reputation", player.getDisplayName(), points,
			HarborReputation.tier(points)), false);
		return points;
	}

	private static int setReputation(CommandSourceStack source, ServerPlayer player, int points) {
		HarborReputation.get(source.getServer()).set(player.getUUID(), points);
		return reputation(source, player);
	}

	private HarborCommand() {}
}
