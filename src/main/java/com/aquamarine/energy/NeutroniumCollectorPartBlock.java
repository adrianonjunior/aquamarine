package com.aquamarine.energy;

import com.aquamarine.block.Waterlogging;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Uma das 17 partes sem item; guarda apenas o índice geométrico e o endereço do controlador. */
public final class NeutroniumCollectorPartBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty PART = IntegerProperty.create("part", 0, 16);
	private static final List<Map<Direction, VoxelShape>> SHAPES = shapes();

	public NeutroniumCollectorPartBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, 0)
			.setValue(Waterlogging.WATERLOGGED, false));
	}

	private static List<Map<Direction, VoxelShape>> shapes() {
		List<Map<Direction, VoxelShape>> out = new ArrayList<>();
		for (int[] p : NeutroniumCollectorBlock.PARTS) {
			VoxelShape shape;
			if (p[1] == 0) {
				shape = Block.box(.5, 0, .5, 15.5, 3.6, 15.5);
				if (p[2] == -1) shape = Shapes.or(shape, Block.box(1, 3, 0, 15, 10, 8));
				if (Math.abs(p[0]) == 1 || Math.abs(p[2]) == 1) {
					shape = Shapes.or(shape, Block.box(1, 3, 1, 3, 15, 3), Block.box(13, 3, 13, 15, 15, 15));
				}
			} else if (p[0] == 0 && p[2] == 0) {
				shape = Shapes.or(Block.box(0, 0, 0, 16, 14, 16), Block.box(2, 14, 2, 14, 16, 14));
			} else {
				shape = Shapes.or(Block.box(1, 0, 1, 3, 15, 3), Block.box(13, 0, 13, 15, 15, 15));
				if (p[2] == -1) shape = Shapes.or(shape, Block.box(1, 0, 0, 15, 10, 5));
			}
			out.add(Shapes.rotateHorizontal(shape));
		}
		return List.copyOf(out);
	}

	@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, PART, Waterlogging.WATERLOGGED);
	}
	@Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(PART)).get(state.getValue(FACING));
	}
	@Override protected FluidState getFluidState(BlockState state) { return Waterlogging.fluid(state); }
	@Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
		Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		Waterlogging.tick(state, level, ticks, pos);
		return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
	}
	@Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return NeutroniumCollectorBlock.open(level, pos, player);
	}
	@Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
		InteractionHand hand, BlockHitResult hit) { return NeutroniumCollectorBlock.open(level, pos, player); }
	@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new NeutroniumCollectorPartBlockEntity(pos, state); }
}
