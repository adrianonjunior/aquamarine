package com.aquamarine.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/** Cabo Aquático: iguala a carga com os cabos vizinhos e entrega às máquinas que não são cabo nem gerador. */
public class AquaticCableBlockEntity extends BlockEntity {
	public final SimpleEnergyStorage energy = new SimpleEnergyStorage(WaterEnergy.CABLE_CAPACITY, WaterEnergy.CABLE_RATE,
		WaterEnergy.CABLE_RATE) {
		@Override
		protected void onFinalCommit() {
			setChanged();
		}
	};

	public AquaticCableBlockEntity(BlockPos pos, BlockState state) {
		super(ModEnergy.CABLE_ENTITY, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, AquaticCableBlockEntity be) {
		for (Direction d : Direction.values()) {
			if (be.energy.amount <= 0) {
				return;
			}
			BlockPos to = pos.relative(d);
			if (level.getBlockEntity(to) instanceof AquaticCableBlockEntity cable) {
				long half = (be.energy.amount - cable.energy.amount) / 2;
				if (half > 0) {
					move(be.energy, cable.energy, Math.min(half, WaterEnergy.CABLE_RATE));
				}
			} else if (!(level.getBlockEntity(to) instanceof WaterGeneratorBlockEntity)) {
				EnergyStorage target = EnergyStorage.SIDED.find(level, to, d.getOpposite());
				if (target != null && target.supportsInsertion()) {
					move(be.energy, target, WaterEnergy.CABLE_RATE);
				}
			}
		}
	}

	private static void move(EnergyStorage from, EnergyStorage to, long max) {
		try (Transaction tx = Transaction.openOuter()) {
			EnergyStorageUtil.move(from, to, max, tx);
			tx.commit();
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
