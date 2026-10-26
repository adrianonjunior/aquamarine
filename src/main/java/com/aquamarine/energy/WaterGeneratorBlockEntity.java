package com.aquamarine.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/** Gerador aquático (6E1): produz conforme o lugar e empurra a energia para os vizinhos (cabos ou máquinas). */
public class WaterGeneratorBlockEntity extends BlockEntity {
	public final SimpleEnergyStorage energy = new SimpleEnergyStorage(WaterEnergy.GENERATOR_CAPACITY, 0, WaterEnergy.GENERATOR_PUSH) {
		@Override
		protected void onFinalCommit() {
			setChanged();
		}
	};
	private long lastRate;

	public WaterGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(ModEnergy.WATER_GENERATOR_ENTITY, pos, state);
	}

	public WaterGeneratorBlock.Kind kind() {
		return getBlockState().getBlock() instanceof WaterGeneratorBlock block ? block.kind() : WaterGeneratorBlock.Kind.TURBINE;
	}

	/** Energia por tick que o lugar dá agora. */
	public static long rate(Level level, BlockPos pos, WaterGeneratorBlock.Kind kind) {
		return switch (kind) {
			case TURBINE -> {
				long sides = 0;
				for (Direction d : Direction.values()) {
					FluidState fluid = level.getFluidState(pos.relative(d));
					if (fluid.is(FluidTags.WATER) && !fluid.isSource()) {
						sides++;
					}
				}
				yield sides * WaterEnergy.TURBINE_PER_SIDE;
			}
			case VENT -> level.getBlockState(pos.below()).is(Blocks.MAGMA_BLOCK) && level.getFluidState(pos.above()).is(FluidTags.WATER)
				? WaterEnergy.VENT_RATE : 0;
			case BUOY -> {
				if (!level.getFluidState(pos.below()).is(FluidTags.WATER) || !level.canSeeSky(pos.above())) {
					yield 0;
				}
				yield WaterEnergy.BUOY_RATE * (level.isThundering() ? 4 : level.isRaining() ? 2 : 1);
			}
		};
	}

	public long lastRate() {
		return lastRate;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, WaterGeneratorBlockEntity be) {
		if (!(level instanceof ServerLevel)) {
			return;
		}
		long rate = rate(level, pos, be.kind());
		be.lastRate = rate;
		if (rate > 0 && be.energy.amount < be.energy.capacity) {
			be.energy.amount = Math.min(be.energy.capacity, be.energy.amount + rate);
			be.setChanged();
		}
		WaterGeneratorBlock.setActive(level, pos, state, rate > 0);
		push(level, pos, be.energy, WaterEnergy.GENERATOR_PUSH);
	}

	/** Empurra para cada vizinho que aceita energia. */
	static void push(Level level, BlockPos pos, EnergyStorage from, long max) {
		for (Direction d : Direction.values()) {
			if (from.getAmount() <= 0) {
				return;
			}
			EnergyStorage to = EnergyStorage.SIDED.find(level, pos.relative(d), d.getOpposite());
			if (to != null && to.supportsInsertion()) {
				try (Transaction tx = Transaction.openOuter()) {
					EnergyStorageUtil.move(from, to, max, tx);
					tx.commit();
				}
			}
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		energy.amount = input.getLongOr("energy", 0L);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putLong("energy", energy.amount);
	}
}
