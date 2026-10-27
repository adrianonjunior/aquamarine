package com.aquamarine.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Chave de Maré (0.6.1, 7C): clique num conector de Cabo, Cano ou Tubo de Ar cicla o lado entre sai, entra e desligado
 * (entre dois canos, ligado e desligado); numa máquina com lados, cicla a face clicada; agachado, gira a máquina.
 */
public class TideWrenchItem extends Item {
	public TideWrenchItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Player player = context.getPlayer();
		if (player != null && player.isSecondaryUseActive() && state.hasProperty(HorizontalDirectionalBlock.FACING)) {
			if (!level.isClientSide()) {
				Direction facing = state.getValue(HorizontalDirectionalBlock.FACING).getClockWise();
				level.setBlock(pos, state.setValue(HorizontalDirectionalBlock.FACING, facing), Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, 1.3F);
			}
			return InteractionResult.SUCCESS;
		}
		if (state.getBlock() instanceof ConduitPiece && level.getBlockEntity(pos) instanceof ConduitBlockEntity be) {
			Direction side = sideHit(state, pos, context.getClickLocation(), context.getClickedFace());
			if (!level.isClientSide()) {
				PortMode mode = be.cycle(side);
				announce(player, side, mode);
				level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, 1.6F);
			}
			return InteractionResult.SUCCESS;
		}
		if (level.getBlockEntity(pos) instanceof PortConfigurable machine) {
			if (!level.isClientSide()) {
				Direction side = context.getClickedFace();
				machine.cyclePort(side);
				announce(player, side, machine.port(side));
				level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, 1.6F);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	private static void announce(Player player, Direction side, PortMode mode) {
		if (player != null) {
			player.sendOverlayMessage(Component.translatable("message.aquamarine.wrench.side",
				Component.translatable("message.aquamarine.side." + side.getSerializedName()), mode.label()));
		}
	}

	/** O lado mirado: o braço do cano que foi tocado; no miolo, a face clicada. Em válvula e visor, a ponta do eixo mais perto. */
	static Direction sideHit(BlockState state, BlockPos pos, Vec3 hit, Direction face) {
		double x = hit.x - pos.getX() - 0.5, y = hit.y - pos.getY() - 0.5, z = hit.z - pos.getZ() - 0.5;
		if (state.hasProperty(InlineConduitBlock.AXIS)) {
			return switch (state.getValue(InlineConduitBlock.AXIS)) {
				case X -> x > 0 ? Direction.EAST : Direction.WEST;
				case Y -> y > 0 ? Direction.UP : Direction.DOWN;
				case Z -> z > 0 ? Direction.SOUTH : Direction.NORTH;
			};
		}
		double ax = Math.abs(x), ay = Math.abs(y), az = Math.abs(z), max = Math.max(ax, Math.max(ay, az));
		if (max <= 3.5 / 16.0 + 1.0E-3) {
			return face;
		}
		if (max == ax) {
			return x > 0 ? Direction.EAST : Direction.WEST;
		}
		if (max == ay) {
			return y > 0 ? Direction.UP : Direction.DOWN;
		}
		return z > 0 ? Direction.SOUTH : Direction.NORTH;
	}
}
