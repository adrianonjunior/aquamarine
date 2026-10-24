package com.aquamarine.block;

import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.item.ModItems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Altar do Abismo (Praça Central da Cidade Perdida): usar o Chamariz Abissal nele começa a
 * invocação do Kraken se houver água funda por perto; senão avisa e não gasta o Chamariz.
 */
public class AbyssalAltarBlock extends Block {
	private static final VoxelShape SHAPE = Shapes.or(
		Block.box(0, 0, 0, 16, 4, 16),
		Block.box(3, 4, 3, 13, 12, 13),
		Block.box(1, 12, 1, 15, 16, 15));

	public AbyssalAltarBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
		InteractionHand hand, BlockHitResult hitResult) {
		if (!stack.is(ModItems.ABYSSAL_LURE)) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (level instanceof ServerLevel serverLevel) {
			if (KrakenSummoning.isActiveNear(serverLevel, pos)) {
				player.sendSystemMessage(Component.translatable("message.aquamarine.summon.busy").withStyle(ChatFormatting.GRAY));
				return InteractionResult.FAIL;
			}
			if (!KrakenSummoning.hasDeepWater(serverLevel, pos)) {
				player.sendSystemMessage(Component.translatable("message.aquamarine.summon.shallow").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
				return InteractionResult.FAIL;
			}
			stack.consume(1, player);
			KrakenSummoning.start(serverLevel, pos, false);
		}
		return InteractionResult.SUCCESS;
	}
}
