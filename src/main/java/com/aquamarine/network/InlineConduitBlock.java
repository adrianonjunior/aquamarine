package com.aquamarine.network;

import com.aquamarine.block.Waterlogging;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Válvula e Visor das três redes (0.6.1, 7C): peças em linha, ligadas só pelo eixo. A Válvula abre e fecha com a mão
 * (fechada, corta a rede em duas); o Visor é um trecho de vidro largo que mostra o conteúdo ampliado. Não seguram a vazão.
 */
public class InlineConduitBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, ConduitPiece {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	private static final Map<Direction.Axis, VoxelShape> SHAPES = new EnumMap<>(Direction.Axis.class);

	static {
		SHAPES.put(Direction.Axis.X, Block.box(0, 3.5, 3.5, 16, 12.5, 12.5));
		SHAPES.put(Direction.Axis.Y, Block.box(3.5, 0, 3.5, 12.5, 16, 12.5));
		SHAPES.put(Direction.Axis.Z, Block.box(3.5, 3.5, 0, 12.5, 12.5, 16));
	}

	private final ConduitKind kind;
	private final boolean valve;

	public InlineConduitBlock(Properties properties, ConduitKind kind, boolean valve) {
		super(properties);
		this.kind = kind;
		this.valve = valve;
		registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.Z).setValue(OPEN, true).setValue(Waterlogging.WATERLOGGED, false));
	}

	@Override
	public ConduitKind kind() {
		return kind;
	}

	@Override
	public boolean reinforced() {
		return true;
	}

	public boolean valve() {
		return valve;
	}

	@Override
	public boolean opens(BlockState state, Direction side) {
		return side.getAxis() == state.getValue(AXIS) && (!valve || state.getValue(OPEN));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS, OPEN, Waterlogging.WATERLOGGED);
	}

	/** Encostada num pedaço da mesma rede, segue o eixo dele; senão, fica na direção para onde o jogador olha. */
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction face = context.getClickedFace();
		BlockState against = context.getLevel().getBlockState(context.getClickedPos().relative(face.getOpposite()));
		Direction.Axis axis = against.getBlock() instanceof ConduitPiece piece && piece.kind() == kind ? face.getAxis()
			: context.getHorizontalDirection().getAxis();
		return Waterlogging.placed(defaultBlockState().setValue(AXIS, axis), context);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!valve) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			boolean open = !state.getValue(OPEN);
			level.setBlock(pos, state.setValue(OPEN, open), Block.UPDATE_ALL);
			level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.6F, open ? 0.9F : 0.7F);
			ConduitNetwork.invalidateAround(level, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!level.isClientSide()) {
			ConduitNetwork.invalidateAround(level, pos);
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
		BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		Waterlogging.tick(state, level, ticks, pos);
		if (direction.getAxis() == state.getValue(AXIS) && level.getBlockEntity(pos) instanceof ConduitBlockEntity be) {
			be.invalidateNetwork();
		}
		return state;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(AXIS));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return Waterlogging.fluid(state);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
		ConduitBlock.spill(level, pos);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ConduitBlockEntity(pos, state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() || type != ModNetworks.CONDUIT_ENTITY ? null
			: (BlockEntityTicker<T>) (BlockEntityTicker<ConduitBlockEntity>) ConduitBlockEntity::serverTick;
	}
}
