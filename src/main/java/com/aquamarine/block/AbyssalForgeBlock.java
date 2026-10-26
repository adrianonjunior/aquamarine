package com.aquamarine.block;

import com.aquamarine.menu.AbyssalForgeMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Forja Abissal: abre uma grade 3x3 que aceita as receitas da forja e as da bancada comum. Desde a 6D2 é um modelo 3D
 * (tools/gen_phase6_art.py) que vira para quem a coloca e colide com o formato: corpo, bancada, cadinho e chaminé.
 */
public class AbyssalForgeBlock extends FacingShapeBlock {
	private static final Component CONTAINER_TITLE = Component.translatable("container.aquamarine.abyssal_forge");
	/** Formato com a frente para o norte (a chaminé acima de 16 px só aparece no desenho). */
	public static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 13, 16), Block.box(2, 13, 9, 7, 16, 14),
		Block.box(11, 13, 10, 14, 16, 13));

	public AbyssalForgeBlock(BlockBehaviour.Properties properties) {
		super(properties, SHAPE);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			player.openMenu(state.getMenuProvider(level, pos));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider(
			(containerId, inventory, player) -> new AbyssalForgeMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)),
			CONTAINER_TITLE);
	}

	/** Brasas na boca da lareira; da chaminé, bolhas debaixo d'água e fumaça no seco. */
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		Direction facing = state.getValue(FACING);
		if (random.nextInt(3) == 0) {
			double side = (random.nextDouble() - 0.5) * 0.4;
			level.addParticle(ParticleTypes.SMALL_FLAME, pos.getX() + 0.5 + facing.getStepX() * 0.48 + facing.getStepZ() * side,
				pos.getY() + 0.3 + random.nextDouble() * 0.2, pos.getZ() + 0.5 + facing.getStepZ() * 0.48 - facing.getStepX() * side,
				0.0, 0.01, 0.0);
		}
		// Chaminé em (12,5; 11,5) px com a frente para o norte, girada com o bloco.
		double x = 4.5 / 16, z = 3.5 / 16;
		double cx = switch (facing) {
			case EAST -> -z;
			case SOUTH -> -x;
			case WEST -> z;
			default -> x;
		};
		double cz = switch (facing) {
			case EAST -> x;
			case SOUTH -> -z;
			case WEST -> -x;
			default -> z;
		};
		double px = pos.getX() + 0.5 + cx, py = pos.getY() + 23.2 / 16, pz = pos.getZ() + 0.5 + cz;
		if (level.getFluidState(pos.above()).is(FluidTags.WATER)) {
			level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, px, py, pz, 0.0, 0.04, 0.0);
		} else if (random.nextInt(2) == 0) {
			level.addParticle(ParticleTypes.SMOKE, px, py, pz, 0.0, 0.03, 0.0);
		}
	}
}
