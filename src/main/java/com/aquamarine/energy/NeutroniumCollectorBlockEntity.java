package com.aquamarine.energy;

import com.aquamarine.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/**
 * Coletor de Neutrônio (6E2): no fundo dos oceanos profundos, gasta energia e junta Pó Estelar de Neutrônio, muito
 * devagar (um a cada 5 minutos com energia contínua). O pó vira Poeira de Neutrônio na Forja, caro (8 para 1).
 */
public class NeutroniumCollectorBlockEntity extends BlockEntity {
	public final SimpleEnergyStorage energy = new SimpleEnergyStorage(WaterEnergy.COLLECTOR_CAPACITY, WaterEnergy.COLLECTOR_INSERT, 0) {
		@Override
		protected void onFinalCommit() {
			setChanged();
		}
	};
	private int progress;
	private int dust;

	public NeutroniumCollectorBlockEntity(BlockPos pos, BlockState state) {
		super(ModEnergy.COLLECTOR_ENTITY, pos, state);
	}

	/** O lugar serve: oceano profundo, debaixo da água e pelo menos 20 blocos abaixo do nível do mar. */
	public static boolean validSpot(Level level, BlockPos pos) {
		return level.getBiome(pos).is(BiomeTags.IS_DEEP_OCEAN) && level.getFluidState(pos.above()).is(FluidTags.WATER)
			&& pos.getY() <= level.getSeaLevel() - WaterEnergy.COLLECTOR_MIN_DEPTH || forceValidSpot;
	}

	/** Só para testes: o mundo plano do GameTest não tem oceano fundo. */
	public static boolean forceValidSpot;

	public int progress() {
		return progress;
	}

	public int dust() {
		return dust;
	}

	/** Para testes: adianta o trabalho. */
	public void setProgress(int ticks) {
		progress = Math.max(0, Math.min(WaterEnergy.COLLECTOR_TICKS, ticks));
	}

	/** Tira o pó guardado (clique com a mão vazia). */
	public int takeDust() {
		int out = dust;
		dust = 0;
		setChanged();
		return out;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, NeutroniumCollectorBlockEntity be) {
		if (!(level instanceof ServerLevel)) {
			return;
		}
		boolean working = be.dust < WaterEnergy.COLLECTOR_MAX_DUST && be.energy.amount >= WaterEnergy.COLLECTOR_USE
			&& validSpotCached(be, level, pos);
		if (working) {
			be.energy.amount -= WaterEnergy.COLLECTOR_USE;
			if (++be.progress >= WaterEnergy.COLLECTOR_TICKS) {
				be.progress = 0;
				be.dust++;
			}
			be.setChanged();
		}
		NeutroniumCollectorBlock.setActive(level, pos, state, working);
	}

	private boolean spotOk;
	private long spotCheckedAt = -100;

	/** O bioma e a água só mudam raramente: confere a cada segundo. */
	private static boolean validSpotCached(NeutroniumCollectorBlockEntity be, Level level, BlockPos pos) {
		if (level.getGameTime() - be.spotCheckedAt >= 20) {
			be.spotOk = validSpot(level, pos);
			be.spotCheckedAt = level.getGameTime();
		}
		return be.spotOk;
	}

	public net.minecraft.world.item.ItemStack dustStack(int count) {
		return new net.minecraft.world.item.ItemStack(ModItems.NEUTRONIUM_STARDUST, count);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		energy.amount = input.getLongOr("energy", 0L);
		progress = input.getIntOr("progress", 0);
		dust = input.getIntOr("dust", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putLong("energy", energy.amount);
		output.putInt("progress", progress);
		output.putInt("dust", dust);
	}
}
