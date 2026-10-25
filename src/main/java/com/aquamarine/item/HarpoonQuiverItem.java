package com.aquamarine.item;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * Aljava de Arpões (docs/08-fase3.md, seção 5): guarda até 4 tipos de ponta. Usar recolhe as pontas
 * do inventário; agachado, devolve. A tecla R (configurável) troca a ponta escolhida; o Arpão gasta
 * uma ao ser arremessado ({@link HarpoonEffects}).
 */
public class HarpoonQuiverItem extends Item {
	public HarpoonQuiverItem(Properties properties) {
		super(properties.stacksTo(1).component(ModComponents.QUIVER_CONTENTS, QuiverContents.EMPTY));
	}

	public static QuiverContents contents(ItemStack stack) {
		return stack.getOrDefault(ModComponents.QUIVER_CONTENTS, QuiverContents.EMPTY);
	}

	/** A primeira Aljava do inventário (mãos primeiro), ou vazio. */
	public static ItemStack find(Player player) {
		for (InteractionHand hand : InteractionHand.values()) {
			if (player.getItemInHand(hand).getItem() instanceof HarpoonQuiverItem) {
				return player.getItemInHand(hand);
			}
		}
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (inventory.getItem(i).getItem() instanceof HarpoonQuiverItem) {
				return inventory.getItem(i);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack quiver = player.getItemInHand(hand);
		if (!(player instanceof ServerPlayer)) {
			return InteractionResult.SUCCESS;
		}
		QuiverContents contents = contents(quiver);
		if (player.isSecondaryUseActive()) {
			for (HarpoonTip tip : HarpoonTip.values()) {
				int count = contents.count(tip);
				if (count > 0) {
					player.getInventory().placeItemBackInInventory(new ItemStack(tip.item(), count), Prediction.PREDICTED);
					contents = contents.with(tip, 0);
				}
			}
			player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
		} else {
			contents = absorb(player.getInventory(), contents);
			player.playSound(SoundEvents.BUNDLE_INSERT, 1.0F, 1.0F);
		}
		quiver.set(ModComponents.QUIVER_CONTENTS, contents);
		return InteractionResult.SUCCESS_SERVER;
	}

	/** Puxa as pontas especiais do inventário para a Aljava, até {@link QuiverContents#MAX_PER_TIP} de cada. */
	public static QuiverContents absorb(Inventory inventory, QuiverContents contents) {
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			HarpoonTip tip = HarpoonTip.of(stack.getItem());
			if (tip == null) {
				continue;
			}
			int room = QuiverContents.MAX_PER_TIP - contents.count(tip);
			int moved = Math.min(room, stack.getCount());
			if (moved > 0) {
				contents = contents.with(tip, contents.count(tip) + moved);
				stack.shrink(moved);
			}
		}
		return contents;
	}

	/** Tecla R: troca para a próxima ponta com estoque e avisa na barra de ação. */
	public static void cycle(ServerPlayer player) {
		ItemStack quiver = find(player);
		if (quiver.isEmpty()) {
			return;
		}
		QuiverContents next = contents(quiver).cycle();
		quiver.set(ModComponents.QUIVER_CONTENTS, next);
		HarpoonTip tip = next.selectedTip();
		player.sendOverlayMessage(Component.translatable("message.aquamarine.quiver.selected",
			Component.translatable(tip.item().getDescriptionId()), next.count(tip)));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		QuiverContents contents = contents(stack);
		for (HarpoonTip tip : HarpoonTip.values()) {
			boolean chosen = contents.selectedTip() == tip;
			builder.accept(Component.translatable("tooltip.aquamarine.quiver.line", chosen ? "▶" : " ",
					Component.translatable(tip.item().getDescriptionId()), contents.count(tip))
				.withStyle(chosen ? ChatFormatting.AQUA : ChatFormatting.GRAY));
		}
	}
}
