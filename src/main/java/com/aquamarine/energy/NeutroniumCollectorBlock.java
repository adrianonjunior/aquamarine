package com.aquamarine.energy;

import com.aquamarine.block.FacingShapeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Coletor de Neutrônio (6E2): clique com a mão vazia para pegar o Pó Estelar guardado; sem pó, mostra a energia, o
 * andamento e se o lugar serve (oceano profundo, debaixo da água, 20 blocos abaixo do nível do mar).
 */
public class NeutroniumCollectorBlock extends FacingShapeBlock implements EntityBlock {
	public NeutroniumCollectorBlock(Properties properties, VoxelShape northShape) {
		super(properties, northShape);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WaterGeneratorBlock.ACTIVE);
	}

	static void setActive(Level level, BlockPos pos, BlockState state, boolean active) {
		WaterGeneratorBlock.setActive(level, pos, state, active);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new NeutroniumCollectorBlockEntity(pos, state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() || type != ModEnergy.COLLECTOR_ENTITY ? null
			: (BlockEntityTicker<T>) (BlockEntityTicker<NeutroniumCollectorBlockEntity>) NeutroniumCollectorBlockEntity::tick;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof NeutroniumCollectorBlockEntity be) {
			int dust = be.takeDust();
			if (dust > 0) {
				net.minecraft.world.item.ItemStack stack = be.dustStack(dust);
				if (!player.getInventory().add(stack)) {
					Block.popResource(level, pos.above(), stack);
				}
			} else {
				player.sendOverlayMessage(Component.translatable("message.aquamarine.energy.collector", be.energy.amount, be.energy.capacity,
					be.progress() * 100 / WaterEnergy.COLLECTOR_TICKS, Component.translatable(NeutroniumCollectorBlockEntity.validSpot(level, pos)
						? "message.aquamarine.energy.collector.ok" : "message.aquamarine.energy.collector.bad")));
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(WaterGeneratorBlock.ACTIVE) && random.nextInt(2) == 0) {
			level.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5, pos.getY() + 1.1,
				pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5, 0, -0.03, 0);
		}
	}
}
