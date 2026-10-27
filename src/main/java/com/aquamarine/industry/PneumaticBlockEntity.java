package com.aquamarine.industry;

import com.aquamarine.energy.WaterGeneratorBlockEntity;
import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import com.aquamarine.machine.MachineView;
import com.aquamarine.network.ConduitNetwork;
import com.aquamarine.network.GasPort;
import com.aquamarine.network.ModNetworks;
import com.aquamarine.network.PortConfigurable;
import com.aquamarine.network.PortMode;
import java.util.Arrays;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Compressor de Ar e Pulmão de Ferro (0.6.1, 7F; docs/12-industria.md). O Compressor gasta energia e faz Ar Comprimido,
 * que empurra pelos lados em "sai"; o Pulmão guarda muito ar, recebe pelos lados em "entra" e solta aos poucos pelo lado
 * de trás (padrão). Quem regula é o gerador que recebe o ar ({@link WaterGeneratorBlockEntity}): o Pulmão é o fôlego da
 * linha. Nunca explode: cheio, só abre a válvula de alívio (o clipe {@code full}).
 */
public class PneumaticBlockEntity extends BlockEntity implements MachineView, PortConfigurable, GasPort {
	public static final long BUCKET = FluidConstants.BUCKET;
	public static final long COMPRESSOR_CAPACITY = 8 * BUCKET;
	public static final long LUNG_CAPACITY = 256 * BUCKET;
	/** Compressor: 8 E por tick fazem um balde de ar a cada 10 s (valores iniciais). */
	public static final long COMPRESSOR_COST = 8;
	public static final long COMPRESSOR_MAKES = BUCKET / 200;
	/** O que cada um empurra por tick: o Compressor 2 baldes/s (a vazão do Tubo), o Pulmão 1 balde/s. */
	private static final long COMPRESSOR_PUSH = BUCKET / 10;
	private static final long LUNG_PUSH = BUCKET / 20;

	private final PortMode[] modes = new PortMode[6];
	private final MachineMotion motion = new MachineMotion();
	private MachineState machine = MachineState.IDLE;
	private boolean dirty = true;
	private long activeUntil;
	private long shownAmount;
	private float smoothFill;
	private final SingleVariantStorage<FluidVariant> air = new SingleVariantStorage<>() {
		@Override
		protected FluidVariant getBlankVariant() {
			return FluidVariant.blank();
		}

		@Override
		protected long getCapacity(FluidVariant variant) {
			return capacity();
		}

		@Override
		protected boolean canInsert(FluidVariant variant) {
			return variant.isOf(ModNetworks.COMPRESSED_AIR);
		}

		@Override
		protected void onFinalCommit() {
			changed();
		}
	};
	public final MachineEnergy energy = new MachineEnergy(16_000, 256, () -> {
		setChanged();
		dirty = true;
	});

	public PneumaticBlockEntity(BlockPos pos, BlockState state) {
		super(ModPneumatics.ENTITY, pos, state);
		boolean compressor = kind() == PneumaticBlock.Kind.COMPRESSOR;
		Arrays.fill(modes, compressor ? PortMode.OUT : PortMode.IN);
		if (!compressor && state.hasProperty(HorizontalDirectionalBlock.FACING)) {
			modes[state.getValue(HorizontalDirectionalBlock.FACING).getOpposite().ordinal()] = PortMode.OUT;
		}
	}

	public PneumaticBlock.Kind kind() {
		return getBlockState().getBlock() instanceof PneumaticBlock block ? block.kind() : PneumaticBlock.Kind.LUNG;
	}

	public long capacity() {
		return kind() == PneumaticBlock.Kind.COMPRESSOR ? COMPRESSOR_CAPACITY : LUNG_CAPACITY;
	}

	public long amount() {
		return air.amount;
	}

	public MachineState machineState() {
		return machine;
	}

	/** Cliente: quanto está cheio (0 a 1), suavizado entre os avisos do servidor. */
	public float fill(float partial) {
		return smoothFill + (shownAmount / (float) capacity() - smoothFill) * partial * 0.2F;
	}

	@Override
	public MachineMotion motion() {
		return motion;
	}

	@Override
	public PortMode port(Direction side) {
		return modes[side.ordinal()];
	}

	@Override
	public void cyclePort(Direction side) {
		modes[side.ordinal()] = port(side).next(true);
		changed();
		if (level != null) {
			ConduitNetwork.invalidateAround(level, worldPosition);
			level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
		}
	}

	private boolean enabled(Direction side) {
		return !isRemoved() && (side == null || port(side) != PortMode.OFF);
	}

	/** API de fluidos: entra pelos lados em "entra" (só o Pulmão), sai pelos lados em "sai"; desligado some. */
	public Storage<FluidVariant> fluidPort(Direction side) {
		if (!enabled(side)) {
			return null;
		}
		return new SidedFluidStorage(() -> air,
			() -> enabled(side) && kind() == PneumaticBlock.Kind.LUNG && (side == null || port(side) == PortMode.IN),
			() -> enabled(side) && (side == null || port(side) == PortMode.OUT));
	}

	/** API de energia: só o Compressor, pelos lados ligados. */
	public MachineEnergy energyPort(Direction side) {
		return kind() == PneumaticBlock.Kind.COMPRESSOR && enabled(side) ? energy : null;
	}

	private void changed() {
		setChanged();
		dirty = true;
		if (level != null) {
			activeUntil = level.getGameTime() + 24;
		}
	}

	/** Gasta a energia e guarda o ar na mesma transação: sem energia ou sem lugar, nada muda. */
	private void compress() {
		if (energy.amount < COMPRESSOR_COST || air.amount + COMPRESSOR_MAKES > capacity()) {
			return;
		}
		try (Transaction tx = Transaction.openOuter()) {
			if (air.insert(FluidVariant.of(ModNetworks.COMPRESSED_AIR), COMPRESSOR_MAKES, tx) == COMPRESSOR_MAKES
				&& energy.consume(COMPRESSOR_COST, tx)) {
				tx.commit();
			}
		}
	}

	private void push() {
		long left = kind() == PneumaticBlock.Kind.COMPRESSOR ? COMPRESSOR_PUSH : LUNG_PUSH;
		for (Direction d : Direction.values()) {
			if (port(d) != PortMode.OUT || left <= 0 || air.amount == 0) {
				continue;
			}
			BlockPos target = worldPosition.relative(d);
			if (!level.isLoaded(target)) {
				continue;
			}
			Storage<FluidVariant> to = FluidStorage.SIDED.find(level, target, d.getOpposite());
			if (to != null) {
				try (Transaction tx = Transaction.openOuter()) {
					left -= StorageUtil.move(air, to, v -> true, left, tx);
					tx.commit();
				}
			}
		}
	}

	public static void tick(Level level, BlockPos pos, BlockState state, PneumaticBlockEntity be) {
		if (be.kind() == PneumaticBlock.Kind.COMPRESSOR) {
			be.compress();
		}
		be.push();
		MachineState next = be.air.amount >= be.capacity() ? MachineState.FULL
			: level.getGameTime() < be.activeUntil ? MachineState.RUNNING : MachineState.IDLE;
		if (next != be.machine) {
			be.machine = next;
			be.dirty = true;
		}
		// O Pulmão mostra o nível: avisa o cliente a cada 1/64 da capacidade; o Compressor, só a troca de estado.
		long step = be.capacity() / 64;
		boolean level64 = be.kind() == PneumaticBlock.Kind.LUNG && Math.abs(be.air.amount - be.shownAmount) >= step;
		if ((be.dirty || level64) && level.getGameTime() % 5 == 0) {
			be.dirty = false;
			be.shownAmount = be.air.amount;
			level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
		}
	}

	/** Cliente: relógio da animação, nível suave e o ar que sai (vapor branco em terra, bolhas na água). */
	public static void clientTick(Level level, BlockPos pos, BlockState state, PneumaticBlockEntity be) {
		be.motion.update(be.machine, 1, level.getGameTime() / 20.0, 1.6);
		be.smoothFill += (be.shownAmount / (float) be.capacity() - be.smoothFill) * 0.2F;
		boolean compressor = be.kind() == PneumaticBlock.Kind.COMPRESSOR;
		// Compressor: uma baforada por curso; Pulmão cheio: rajadas curtas da válvula de alívio (6 de cada 24 ticks).
		boolean puff = compressor ? be.machine == MachineState.RUNNING && level.getGameTime() % 16 == 0
			: be.machine == MachineState.FULL && level.getGameTime() % 24 < 6;
		if (!puff || !state.hasProperty(HorizontalDirectionalBlock.FACING)) {
			return;
		}
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		// Bocal da válvula de cada modelo (tools/models/machines/*/build_model.py, em pixels de quem olha a frente).
		Vec3 mouth = compressor ? WaterGeneratorBlockEntity.local(pos, facing, 13.5, 15.6, 12.5)
			: WaterGeneratorBlockEntity.local(pos, facing, 6.0, 14.6, 8.0);
		boolean wet = state.getFluidState().is(FluidTags.WATER) || level.getFluidState(pos.above()).is(FluidTags.WATER);
		RandomSource random = level.getRandom();
		double dx = (random.nextDouble() - 0.5) * 0.06, dz = (random.nextDouble() - 0.5) * 0.06;
		if (wet) {
			level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, mouth.x + dx, mouth.y, mouth.z + dz, 0, 0.06, 0);
		} else {
			level.addParticle(ParticleTypes.WHITE_SMOKE, mouth.x + dx, mouth.y, mouth.z + dz, dx * 0.3, compressor ? 0.04 : 0.16, dz * 0.3);
		}
	}

	private int packedModes() {
		int packed = 0;
		for (int i = 0; i < 6; i++) {
			packed |= modes[i].ordinal() << (2 * i);
		}
		return packed;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putLong("air", air.amount);
		output.putLong("energy", energy.amount);
		output.putInt("modes", packedModes());
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		air.amount = Math.clamp(input.getLongOr("air", 0L), 0, capacity());
		air.variant = air.amount > 0 ? FluidVariant.of(ModNetworks.COMPRESSED_AIR) : FluidVariant.blank();
		energy.amount = Math.clamp(input.getLongOr("energy", 0L), 0, energy.capacity);
		int packed = input.getIntOr("modes", packedModes());
		for (int i = 0; i < 6; i++) {
			modes[i] = PortMode.byId((packed >> (2 * i)) & 3);
		}
		machine = MachineState.byId(input.getByteOr("machine", (byte) 0));
		shownAmount = input.getLongOr("shown", air.amount);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/** Para o desenho: modos dos lados, estado e o nível de ar. */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("modes", packedModes());
		tag.putByte("machine", (byte) machine.ordinal());
		tag.putLong("shown", air.amount);
		tag.putLong("air", air.amount);
		return tag;
	}
}
