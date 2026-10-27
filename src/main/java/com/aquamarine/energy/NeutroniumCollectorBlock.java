package com.aquamarine.energy;

import com.aquamarine.block.FacingShapeBlock;
import com.aquamarine.block.Waterlogging;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Controlador central da base do Coletor 3×2×3; as outras 17 posições são {@link NeutroniumCollectorPartBlock}. */
public final class NeutroniumCollectorBlock extends FacingShapeBlock implements EntityBlock {
	public static final int WIDTH = 3;
	public static final int HEIGHT = 2;
	/** Profundidade decidida pelo plano; constante única para montagem, migração e testes. */
	public static final int DEPTH = 3;
	static final int[][] PARTS = parts();
	private static final ThreadLocal<Boolean> REMOVING = ThreadLocal.withInitial(() -> false);

	public NeutroniumCollectorBlock(Properties properties, VoxelShape northShape) { super(properties, northShape); }

	private static int[][] parts() {
		List<int[]> out = new ArrayList<>();
		for (int y = 0; y < HEIGHT; y++) for (int z = -DEPTH / 2; z <= DEPTH / 2; z++) for (int x = -WIDTH / 2; x <= WIDTH / 2; x++) {
			if (x != 0 || y != 0 || z != 0) out.add(new int[] {x, y, z});
		}
		return out.toArray(int[][]::new);
	}

	@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WaterGeneratorBlock.ACTIVE);
	}

	static void setActive(Level level, BlockPos pos, BlockState state, boolean active) { WaterGeneratorBlock.setActive(level, pos, state, active); }

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state != null && hasRoom(context.getLevel(), context.getClickedPos(), state.getValue(FACING)) ? state : null;
	}

	static boolean hasRoom(Level level, BlockPos controller, Direction facing) {
		for (int i = 0; i < PARTS.length; i++) {
			BlockPos at = partPos(controller, facing, i);
			BlockState state = level.getBlockState(at);
			if (!state.isAir() && !(state.getFluidState().is(FluidTags.WATER) && state.canBeReplaced())) return false;
		}
		return true;
	}

	static boolean assemble(Level level, BlockPos controller, BlockState state, NeutroniumCollectorBlockEntity be) {
		Direction facing = state.getValue(FACING);
		if (!hasRoom(level, controller, facing)) return false;
		for (int i = 0; i < PARTS.length; i++) {
			BlockPos at = partPos(controller, facing, i);
			boolean water = level.getFluidState(at).is(FluidTags.WATER);
			BlockState part = ModEnergy.NEUTRONIUM_COLLECTOR_PART.defaultBlockState()
				.setValue(NeutroniumCollectorPartBlock.PART, i).setValue(NeutroniumCollectorPartBlock.FACING, facing)
				.setValue(Waterlogging.WATERLOGGED, water);
			level.setBlock(at, part, Block.UPDATE_ALL);
			if (level.getBlockEntity(at) instanceof NeutroniumCollectorPartBlockEntity piece) piece.setController(controller);
		}
		be.markAssembled();
		return true;
	}

	static BlockPos partPos(BlockPos controller, Direction facing, int part) {
		int[] p = PARTS[part];
		return controller.offset(rotate(facing, p[0], p[1], p[2]));
	}

	private static BlockPos rotate(Direction facing, int x, int y, int z) {
		return switch (facing) {
			case SOUTH -> new BlockPos(-x, y, -z);
			case WEST -> new BlockPos(z, y, -x);
			case EAST -> new BlockPos(-z, y, x);
			default -> new BlockPos(x, y, z);
		};
	}

	/** Remove controller e partes sem recursão, restaura a água e solta um item único com o pó guardado. */
	static void dismantle(Level level, BlockPos controller, boolean drop) {
		if (level.isClientSide() || REMOVING.get()) return;
		BlockState controllerState = level.getBlockState(controller);
		if (!(controllerState.getBlock() instanceof NeutroniumCollectorBlock)) return;
		Direction facing = controllerState.getValue(FACING);
		ItemStack stack = level.getBlockEntity(controller) instanceof NeutroniumCollectorBlockEntity be
			? be.machineStack() : new ItemStack(ModEnergy.NEUTRONIUM_COLLECTOR);
		REMOVING.set(true);
		try {
			for (int i = 0; i < PARTS.length; i++) replaceWithFluid(level, partPos(controller, facing, i));
			replaceWithFluid(level, controller);
			if (drop) Block.popResource(level, controller.above(), stack);
		} finally {
			REMOVING.set(false);
		}
	}

	private static void replaceWithFluid(Level level, BlockPos pos) {
		BlockState old = level.getBlockState(pos);
		BlockState replacement = old.getFluidState().is(FluidTags.WATER)
			? Fluids.WATER.getSource(false).createLegacyBlock() : Blocks.AIR.defaultBlockState();
		level.setBlock(pos, replacement, Block.UPDATE_ALL);
	}

	static NeutroniumCollectorBlockEntity controller(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof NeutroniumCollectorBlockEntity be) return be;
		if (level.getBlockEntity(pos) instanceof NeutroniumCollectorPartBlockEntity part) return part.controller();
		return null;
	}

	static InteractionResult open(Level level, BlockPos pos, Player player) {
		NeutroniumCollectorBlockEntity be = controller(level, pos);
		if (be == null) return InteractionResult.PASS;
		if (!level.isClientSide()) player.openMenu(be);
		return InteractionResult.SUCCESS;
	}

	@Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return open(level, pos, player);
	}
	@Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
		InteractionHand hand, BlockHitResult hit) { return open(level, pos, player); }

	@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new NeutroniumCollectorBlockEntity(pos, state); }
	@Override @SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return type != ModEnergy.COLLECTOR_ENTITY ? null
			: (BlockEntityTicker<T>) (BlockEntityTicker<NeutroniumCollectorBlockEntity>) NeutroniumCollectorBlockEntity::tick;
	}
}
