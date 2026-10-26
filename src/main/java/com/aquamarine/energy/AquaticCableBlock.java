package com.aquamarine.energy;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;

/**
 * Cabo Aquático (6E): leva a energia dos geradores até as máquinas. Liga sozinho nos vizinhos que guardam energia
 * (cabos, geradores, o Coletor ou máquinas de outros mods com a Team Reborn Energy) e iguala a carga com eles.
 */
public class AquaticCableBlock extends Block implements EntityBlock {
	private static final Map<Direction, BooleanProperty> SIDES = PipeBlock.PROPERTY_BY_DIRECTION;
	private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
	private static final Map<Direction, VoxelShape> ARMS = new EnumMap<>(Direction.class);

	static {
		ARMS.put(Direction.DOWN, Block.box(6, 0, 6, 10, 5, 10));
		ARMS.put(Direction.UP, Block.box(6, 11, 6, 10, 16, 10));
		ARMS.put(Direction.NORTH, Block.box(6, 6, 0, 10, 10, 5));
		ARMS.put(Direction.SOUTH, Block.box(6, 6, 11, 10, 10, 16));
		ARMS.put(Direction.WEST, Block.box(0, 6, 6, 5, 10, 10));
		ARMS.put(Direction.EAST, Block.box(11, 6, 6, 16, 10, 10));
	}

	public AquaticCableBlock(Properties properties) {
		super(properties);
		BlockState state = stateDefinition.any();
		for (BooleanProperty side : SIDES.values()) {
			state = state.setValue(side, false);
		}
		registerDefaultState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		SIDES.values().forEach(builder::add);
	}

	private static boolean connects(LevelReader level, BlockPos pos, Direction side) {
		BlockState other = level.getBlockState(pos.relative(side));
		return other.getBlock() instanceof AquaticCableBlock || other.getBlock() instanceof WaterGeneratorBlock
			|| other.getBlock() instanceof NeutroniumCollectorBlock
			|| (level instanceof Level real && team.reborn.energy.api.EnergyStorage.SIDED.find(real, pos.relative(side), side.getOpposite()) != null);
	}

	private BlockState connected(LevelReader level, BlockPos pos, BlockState state) {
		for (Map.Entry<Direction, BooleanProperty> e : SIDES.entrySet()) {
			state = state.setValue(e.getValue(), connects(level, pos, e.getKey()));
		}
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return connected(context.getLevel(), context.getClickedPos(), defaultBlockState());
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
		BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		return state.setValue(SIDES.get(direction), connects(level, pos, direction));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = CORE;
		for (Map.Entry<Direction, BooleanProperty> e : SIDES.entrySet()) {
			if (state.getValue(e.getValue())) {
				shape = Shapes.or(shape, ARMS.get(e.getKey()));
			}
		}
		return shape;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AquaticCableBlockEntity(pos, state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() || type != ModEnergy.CABLE_ENTITY ? null
			: (BlockEntityTicker<T>) (BlockEntityTicker<AquaticCableBlockEntity>) AquaticCableBlockEntity::tick;
	}
}
