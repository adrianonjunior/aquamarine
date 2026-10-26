package com.aquamarine.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Bloco com modelo 3D (Fase 4, docs/10-fase4.md §4): vira de frente para quem o coloca e colide com o formato
 * do modelo em vez de um cubo. Troféus e os blocos de trabalho da Vila.
 */
public class FacingShapeBlock extends HorizontalDirectionalBlock {
	private final Map<Direction, VoxelShape> shapes;

	/** @param northShape formato com a frente para o norte, em pixels ({@link Block#box}) */
	public FacingShapeBlock(Properties properties, VoxelShape northShape) {
		super(properties);
		this.shapes = Shapes.rotateHorizontal(northShape);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapes.get(state.getValue(FACING));
	}
}
