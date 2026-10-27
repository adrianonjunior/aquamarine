package com.aquamarine.network;

import com.aquamarine.block.Waterlogging;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Cabo, Cano e Tubo de Ar (0.6.1, 7C; docs/12-industria.md): ligam sozinhos nos vizinhos do mesmo tipo (peça reta,
 * cotovelo, T e cruz saem das ligações) e nas máquinas que guardam o mesmo conteúdo, com o anel de ligação da cor da rede.
 * A Chave de Maré cicla o modo de cada lado ({@link PortMode}); o estoque fica na rede ({@link ConduitNetwork}). Quebrar um
 * Cano com água solta uma fonte no lugar; lava e gás somem.
 */
public class ConduitBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, ConduitPiece {
	/** O que há em cada lado: nada, outro pedaço da rede ou uma máquina (com anel de ligação). */
	public enum Link implements StringRepresentable {
		NONE, PIPE, PORT;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(java.util.Locale.ROOT);
		}
	}

	public static final Map<Direction, EnumProperty<Link>> SIDES = new EnumMap<>(Direction.class);

	static {
		for (Direction d : Direction.values()) {
			SIDES.put(d, EnumProperty.create(d.getSerializedName(), Link.class));
		}
	}

	private static final VoxelShape CORE = Block.box(4.5, 4.5, 4.5, 11.5, 11.5, 11.5);
	private static final Map<Direction, VoxelShape> ARMS = new EnumMap<>(Direction.class);

	static {
		ARMS.put(Direction.DOWN, Block.box(5, 0, 5, 11, 4.5, 11));
		ARMS.put(Direction.UP, Block.box(5, 11.5, 5, 11, 16, 11));
		ARMS.put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 4.5));
		ARMS.put(Direction.SOUTH, Block.box(5, 5, 11.5, 11, 11, 16));
		ARMS.put(Direction.WEST, Block.box(0, 5, 5, 4.5, 11, 11));
		ARMS.put(Direction.EAST, Block.box(11.5, 5, 5, 16, 11, 11));
	}

	private final ConduitKind kind;
	private final boolean reinforced;
	private final Map<BlockState, VoxelShape> shapes = new ConcurrentHashMap<>();

	public ConduitBlock(Properties properties, ConduitKind kind, boolean reinforced) {
		super(properties);
		this.kind = kind;
		this.reinforced = reinforced;
		BlockState state = stateDefinition.any().setValue(Waterlogging.WATERLOGGED, false);
		for (EnumProperty<Link> side : SIDES.values()) {
			state = state.setValue(side, Link.NONE);
		}
		registerDefaultState(state);
	}

	@Override
	public ConduitKind kind() {
		return kind;
	}

	@Override
	public boolean reinforced() {
		return reinforced;
	}

	@Override
	public boolean opens(BlockState state, Direction side) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		SIDES.values().forEach(builder::add);
		builder.add(Waterlogging.WATERLOGGED);
	}

	/** O que fica do lado {@code side}: o modo desligado corta; pedaço da mesma rede vira cano; máquina vira conector. */
	public Link link(LevelReader level, BlockPos pos, BlockState state, Direction side) {
		if (level.getBlockEntity(pos) instanceof ConduitBlockEntity be && be.mode(side) == PortMode.OFF) {
			return Link.NONE;
		}
		BlockPos other = pos.relative(side);
		BlockState neighbor = level.getBlockState(other);
		if (neighbor.getBlock() instanceof ConduitPiece piece) {
			if (piece.kind() != kind || !piece.opens(neighbor, side.getOpposite())) {
				return Link.NONE;
			}
			return level.getBlockEntity(other) instanceof ConduitBlockEntity be && be.mode(side.getOpposite()) == PortMode.OFF
				? Link.NONE : Link.PIPE;
		}
		return level instanceof Level real && ConduitNetwork.isPort(real, other, side.getOpposite(), kind) ? Link.PORT : Link.NONE;
	}

	/** Estado com todas as ligações recalculadas. */
	public BlockState linked(LevelReader level, BlockPos pos, BlockState state) {
		for (Map.Entry<Direction, EnumProperty<Link>> e : SIDES.entrySet()) {
			state = state.setValue(e.getValue(), link(level, pos, state, e.getKey()));
		}
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return Waterlogging.placed(linked(context.getLevel(), context.getClickedPos(), defaultBlockState()), context);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (level.isClientSide()) {
			return;
		}
		BlockState linked = linked(level, pos, state);
		if (linked != state) {
			level.setBlock(pos, linked, Block.UPDATE_CLIENTS);
		}
		ConduitNetwork.invalidateAround(level, pos);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
		BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		Waterlogging.tick(state, level, ticks, pos);
		Link link = link(level, pos, state, direction);
		if (link != state.getValue(SIDES.get(direction)) && level.getBlockEntity(pos) instanceof ConduitBlockEntity be) {
			be.invalidateNetwork();
		}
		return state.setValue(SIDES.get(direction), link);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapes.computeIfAbsent(state, s -> {
			VoxelShape shape = CORE;
			for (Map.Entry<Direction, EnumProperty<Link>> e : SIDES.entrySet()) {
				if (s.getValue(e.getValue()) != Link.NONE) {
					shape = Shapes.or(shape, ARMS.get(e.getKey()));
				}
			}
			return shape;
		});
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return Waterlogging.fluid(state);
	}

	/** Pedaços de Cano com água que acabaram de sair (o block entity anota antes de ir embora). */
	static final Set<GlobalPos> SPILL = ConcurrentHashMap.newKeySet();

	/** Cano com água quebrado solta uma fonte no lugar; lava e gás somem. */
	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
		spill(level, pos);
	}

	static void spill(ServerLevel level, BlockPos pos) {
		if (SPILL.remove(GlobalPos.of(level.dimension(), pos)) && level.getBlockState(pos).isAir()) {
			level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
		}
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
