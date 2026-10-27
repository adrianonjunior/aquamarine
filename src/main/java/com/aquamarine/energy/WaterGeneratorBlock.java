package com.aquamarine.energy;

import com.aquamarine.block.FacingShapeBlock;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Os três geradores aquáticos (6E1): Turbina de Correnteza (água corrente nos lados), Gerador de Fenda (magma embaixo,
 * água em cima) e Boia de Ondas (na superfície, mais forte na chuva). Clique com a mão vazia mostra a carga.
 */
public class WaterGeneratorBlock extends FacingShapeBlock implements EntityBlock {
	public static final BooleanProperty ACTIVE = BlockStateProperties.LIT;

	public enum Kind { TURBINE, VENT, BUOY }

	private final Kind kind;

	public WaterGeneratorBlock(Properties properties, VoxelShape northShape, Kind kind) {
		super(properties, northShape);
		this.kind = kind;
	}

	public Kind kind() {
		return kind;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}

	static void setActive(Level level, BlockPos pos, BlockState state, boolean active) {
		if (state.hasProperty(ACTIVE) && state.getValue(ACTIVE) != active) {
			level.setBlock(pos, state.setValue(ACTIVE, active), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WaterGeneratorBlockEntity(pos, state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (type != ModEnergy.WATER_GENERATOR_ENTITY) {
			return null;
		}
		if (level.isClientSide()) {
			return kind == Kind.VENT ? (BlockEntityTicker<T>) (BlockEntityTicker<WaterGeneratorBlockEntity>) WaterGeneratorBlockEntity::clientTick : null;
		}
		return (BlockEntityTicker<T>) (BlockEntityTicker<WaterGeneratorBlockEntity>) WaterGeneratorBlockEntity::tick;
	}

	/** A Fenda é uma máquina animada (0.6.1, 7B): quem a desenha é o renderizador do block entity, não o chunk. */
	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return kind == Kind.VENT ? RenderShape.INVISIBLE : RenderShape.MODEL;
	}

	private static String bucket(long droplets) {
		return String.format(Locale.ROOT, "%.1f", droplets / 81000.0);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof WaterGeneratorBlockEntity be) {
			Component line = Component.translatable("message.aquamarine.energy.generator", be.energy.amount, be.energy.capacity,
				be.lastRate());
			// Fenda com tanques ou gerador com ar: mostra também o que guarda (em baldes, com uma casa).
			if (kind == Kind.VENT && be.water() + be.lava() > 0 || be.air() > 0) {
				line = Component.translatable("message.aquamarine.energy.generator.tanks", line, bucket(be.water()), bucket(be.lava()),
					bucket(be.air()));
			}
			player.sendOverlayMessage(line);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (!state.getValue(ACTIVE) || random.nextInt(3) != 0) {
			return;
		}
		double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6, z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
		switch (kind) {
			case VENT -> { } // vapor e bolhas saem da chaminé a cada golpe (WaterGeneratorBlockEntity.clientTick)
			case TURBINE -> level.addParticle(ParticleTypes.BUBBLE, x, pos.getY() + 0.5, z, 0, 0.02, 0);
			case BUOY -> level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 0, 0.02, 0);
		}
	}
}
