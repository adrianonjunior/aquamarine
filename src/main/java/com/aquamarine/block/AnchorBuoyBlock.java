package com.aquamarine.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Bloco da plataforma da Boia de Âncora (docs/05-itens.md; visual da Fase 4, docs/10-fase4.md §5): 3x3 sobre a
 * água que dura {@link #LIFETIME_TICKS} e depois volta a ser água. Quem pisa nela tem a Pressão zerada. Os oito
 * blocos de fora são deque sobre flutuadores (com água em volta); o do meio leva a boia, com sino e uma lanterna
 * que pisca e muda de cor com o tempo que resta: verde, amarela e vermelha. O Kraken a derruba com {@link #collapse}.
 */
public class AnchorBuoyBlock extends Block implements SimpleWaterloggedBlock {
	public static final int LIFETIME_TICKS = 90 * 20;
	/** Quanto dura cada cor da lanterna do meio (somam {@link #LIFETIME_TICKS}). */
	public static final int[] STAGE_TICKS = {45 * 20, 30 * 20, 15 * 20};
	public static final BooleanProperty CENTER = BooleanProperty.create("center");
	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 2);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape CENTER_SHAPE = Shapes.or(Shapes.block(), Block.box(5, 16, 5, 11, 24, 11));

	public AnchorBuoyBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(CENTER, false).setValue(STAGE, 0).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(CENTER, STAGE, WATERLOGGED);
	}

	/** Estado de um bloco da plataforma recém-posta sobre a água. */
	public static BlockState placed(boolean center) {
		return ModBlocks.ANCHOR_BUOY.defaultBlockState().setValue(CENTER, center).setValue(WATERLOGGED, true);
	}

	/** Primeiro tique agendado: o meio troca de cor, o resto só desfaz no fim. */
	public static int firstTick(boolean center) {
		return center ? STAGE_TICKS[0] : LIFETIME_TICKS;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int stage = state.getValue(STAGE);
		if (state.getValue(CENTER) && stage < 2) {
			level.setBlockAndUpdate(pos, state.setValue(STAGE, stage + 1));
			level.scheduleTick(pos, this, STAGE_TICKS[stage + 1]);
			level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 0.5F, stage == 0 ? 1.4F : 1.8F);
			return;
		}
		collapse(level, pos);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(CENTER) ? CENTER_SHAPE : Shapes.block();
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	/** Desfaz um bloco da plataforma, devolvendo a água, com lascas de madeira e espirro. */
	public static void collapse(ServerLevel level, BlockPos pos) {
		if (level.getBlockState(pos).is(ModBlocks.ANCHOR_BUOY)) {
			level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
			double x = pos.getX() + 0.5, y = pos.getY() + 1.0, z = pos.getZ() + 0.5;
			level.sendParticles(ParticleTypes.BUBBLE_POP, x, y, z, 6, 0.3, 0.1, 0.3, 0.02);
			level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SPRUCE_PLANKS.defaultBlockState()),
				x, y - 0.1, z, 14, 0.35, 0.1, 0.35, 0.1);
			level.sendParticles(ParticleTypes.SPLASH, x, y, z, 10, 0.4, 0.05, 0.4, 0.1);
			level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 0.6F, 0.9F);
		}
	}

	/** O jogador está de pé numa Boia de Âncora. */
	public static boolean isStandingOn(Player player) {
		BlockPos below = BlockPos.containing(player.getX(), player.getY() - 0.05, player.getZ());
		return player.level().getBlockState(below).is(ModBlocks.ANCHOR_BUOY);
	}
}
