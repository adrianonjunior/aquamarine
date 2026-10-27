package com.aquamarine.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.EnergyStorage;

/** Parte sem estoque próprio: clique e EnergyStorage.SIDED sempre alcançam o controlador. */
public final class NeutroniumCollectorPartBlockEntity extends BlockEntity {
	private BlockPos controller = BlockPos.ZERO;

	public NeutroniumCollectorPartBlockEntity(BlockPos pos, BlockState state) {
		super(ModEnergy.COLLECTOR_PART_ENTITY, pos, state);
	}

	public BlockPos controllerPos() { return controller; }
	public void setController(BlockPos pos) { controller = pos.immutable(); setChanged(); }
	public NeutroniumCollectorBlockEntity controller() {
		return level != null && level.getBlockEntity(controller) instanceof NeutroniumCollectorBlockEntity be ? be : null;
	}
	public EnergyStorage energy(Direction side) {
		NeutroniumCollectorBlockEntity be = controller();
		return be == null ? null : be.energy;
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state);
		if (level != null && !level.isClientSide()) NeutroniumCollectorBlock.dismantle(level, controller, true);
	}

	@Override protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		controller = BlockPos.of(input.getLongOr("controller", 0L));
	}
	@Override protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putLong("controller", controller.asLong());
	}
}
