package com.aquamarine.guide;

import com.aquamarine.item.ModItems;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Manual do Aquamarine: o item, o pacote que o abre e o comando {@code /aquamarine help} (qualquer jogador): entrega o
 * Manual se ele não estiver no inventário e o abre na hora.
 */
public final class ModManual {
	public static final Item MANUAL = ModItems.register("manual", ManualItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

	public static void register() {
		OpenManualPayload.register();
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) ->
			dispatcher.register(Commands.literal("aquamarine").then(Commands.literal("help").executes(ModManual::help))));
	}

	private static int help(CommandContext<CommandSourceStack> ctx) {
		ServerPlayer player = ctx.getSource().getPlayer();
		if (player == null) {
			ctx.getSource().sendFailure(Component.translatable("commands.aquamarine.help.player"));
			return 0;
		}
		boolean has = player.getInventory().contains(stack -> stack.is(MANUAL));
		if (!has && !player.getInventory().add(new ItemStack(MANUAL))) {
			player.spawnAtLocation(player.level(), new ItemStack(MANUAL));
		}
		ServerPlayNetworking.send(player, OpenManualPayload.INSTANCE);
		ctx.getSource().sendSuccess(() -> Component.translatable(has ? "commands.aquamarine.help.open" : "commands.aquamarine.help.given"), false);
		return 1;
	}

	private ModManual() {}
}
