package com.aquamarine.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bloco da plataforma da Boia de Âncora (docs/05-itens.md): 3x3 sobre a água que dura
 * {@link #LIFETIME_TICKS} e depois volta a ser água. Quem pisa nela tem a Pressão zerada.
 * O Kraken a derruba com {@link #collapse}.
 */
public class AnchorBuoyBlock extends Block {
	public static final int LIFETIME_TICKS = 90 * 20;

	public AnchorBuoyBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		collapse(level, pos);
	}

	/** Desfaz um bloco da plataforma, devolvendo a água. */
	public static void collapse(ServerLevel level, BlockPos pos) {
		if (level.getBlockState(pos).is(ModBlocks.ANCHOR_BUOY)) {
			level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
			level.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 6, 0.3, 0.1, 0.3, 0.02);
			level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 0.6F, 0.9F);
		}
	}

	/** O jogador está de pé numa Boia de Âncora. */
	public static boolean isStandingOn(Player player) {
		BlockPos below = BlockPos.containing(player.getX(), player.getY() - 0.05, player.getZ());
		return player.level().getBlockState(below).is(ModBlocks.ANCHOR_BUOY);
	}
}
