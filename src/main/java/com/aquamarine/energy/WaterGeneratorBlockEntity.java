package com.aquamarine.energy;

import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import com.aquamarine.machine.MachineView;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/**
 * Gerador aquático (6E): produz conforme o lugar e empurra a energia para os vizinhos (cabos ou máquinas). Desde a 0.6.1
 * (7B) também é uma máquina animada: o servidor decide parada, funcionando ou sobrecarga e manda ao cliente só quando
 * muda; o cliente guarda o relógio ({@link MachineMotion}) e solta vapor ou bolhas a cada golpe dos pistões da Fenda.
 */
public class WaterGeneratorBlockEntity extends BlockEntity implements MachineView {
	/** Duração do clipe {@code start} da Fenda (tools/models/machines/vent_generator/build_model.py). */
	public static final double VENT_START = 1.6;
	/** Meio ciclo do clipe {@code run} da Fenda: um golpe de pistão a 1x (o run tem 1,2 s). */
	public static final double VENT_STROKE = 0.6;
	/** A tampa da chaminé abre logo depois do golpe (0,12 s no run). */
	private static final double VENT_PUFF = 0.12;
	/** Sobrecarga: carga cheia por 2 s seguidos; volta a funcionar depois de 0,5 s escoando. */
	private static final int OVERLOAD_AFTER = 40;
	private static final int RELAX_AFTER = 10;
	/** Só para os testes de jogo: geradores que produzem como se o lugar fosse bom (vitrine em terra firme). */
	public static final Set<BlockPos> FORCED = ConcurrentHashMap.newKeySet();

	public final SimpleEnergyStorage energy = new SimpleEnergyStorage(WaterEnergy.GENERATOR_CAPACITY, 0, WaterEnergy.GENERATOR_PUSH) {
		@Override
		protected void onFinalCommit() {
			setChanged();
		}
	};
	private long lastRate;
	private MachineState machine = MachineState.IDLE;
	private float speed = 1.0F;
	private int fullTicks;
	private int drainTicks;
	private final MachineMotion motion = new MachineMotion();
	private long lastStroke = Long.MIN_VALUE;

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
			case VENT -> FORCED.contains(pos)
				|| level.getBlockState(pos.below()).is(Blocks.MAGMA_BLOCK) && level.getFluidState(pos.above()).is(FluidTags.WATER)
				? WaterEnergy.VENT_RATE : 0;
			case BUOY -> {
				if (!level.getFluidState(pos.below()).is(FluidTags.WATER) || !level.canSeeSky(pos.above())) {
					yield 0;
				}
				yield WaterEnergy.BUOY_RATE * (level.isThundering() ? 4 : level.isRaining() ? 2 : 1);
			}
		};
	}

	/** Produção normal de cada gerador: a animação anda a 1x nela (de 0,5x a 2x em volta). */
	private static long nominal(WaterGeneratorBlock.Kind kind) {
		return switch (kind) {
			case TURBINE -> WaterEnergy.TURBINE_PER_SIDE * 3;
			case VENT -> WaterEnergy.VENT_RATE;
			case BUOY -> WaterEnergy.BUOY_RATE;
		};
	}

	public long lastRate() {
		return lastRate;
	}

	public MachineState machineState() {
		return machine;
	}

	@Override
	public MachineMotion motion() {
		return motion;
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
		be.updateMachine(level, pos, rate);
	}

	/** Parada sem produzir; sobrecarga com a carga cheia por 2 s (nada sai); senão funcionando. Avisa o cliente se mudar. */
	private void updateMachine(Level level, BlockPos pos, long rate) {
		boolean full = energy.amount >= energy.capacity;
		fullTicks = full ? fullTicks + 1 : 0;
		drainTicks = full ? 0 : drainTicks + 1;
		MachineState next;
		if (rate <= 0) {
			next = MachineState.IDLE;
		} else if (machine == MachineState.OVERLOAD) {
			next = drainTicks >= RELAX_AFTER ? MachineState.RUNNING : MachineState.OVERLOAD;
		} else {
			next = fullTicks >= OVERLOAD_AFTER ? MachineState.OVERLOAD : MachineState.RUNNING;
		}
		float nextSpeed = rate <= 0 ? speed : Mth.clamp(Math.round(rate * 20.0F / nominal(kind())) / 20.0F, 0.5F, 2.0F);
		if (next != machine || nextSpeed != speed) {
			machine = next;
			speed = nextSpeed;
			setChanged();
			BlockState state = getBlockState();
			level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
		}
	}

	/** No cliente: acerta o relógio da animação e, na Fenda, solta vapor (terra) ou bolhas (água) a cada golpe. */
	public static void clientTick(Level level, BlockPos pos, BlockState state, WaterGeneratorBlockEntity be) {
		double now = level.getGameTime() / 20.0;
		be.motion.update(be.machine, be.speed, now, VENT_START);
		if (be.kind() != WaterGeneratorBlock.Kind.VENT) {
			return;
		}
		RandomSource random = level.getRandom();
		long stroke = be.lastStroke;
		int puffs = 0;
		if (be.machine == MachineState.RUNNING && now >= be.motion.since() + VENT_START) {
			stroke = (long) Math.floor((be.motion.runPhase(now) - VENT_PUFF) / VENT_STROKE);
			puffs = stroke != be.lastStroke && be.lastStroke != Long.MIN_VALUE ? 2 : 0;
		} else if (be.machine == MachineState.OVERLOAD && level.getGameTime() % 3 == 0) {
			puffs = 2;
		}
		be.lastStroke = stroke;
		if (puffs == 0) {
			return;
		}
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		Vec3 mouth = local(pos, facing, 2.25, 15.6, 12.75);
		boolean wet = state.getFluidState().is(FluidTags.WATER) || level.getFluidState(pos.above()).is(FluidTags.WATER);
		for (int i = 0; i < puffs; i++) {
			double dx = (random.nextDouble() - 0.5) * 0.08, dz = (random.nextDouble() - 0.5) * 0.08;
			if (wet) {
				level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, mouth.x + dx, mouth.y, mouth.z + dz, 0, 0.04, 0);
			} else {
				level.addParticle(i == 0 && be.machine == MachineState.OVERLOAD ? ParticleTypes.SMOKE : ParticleTypes.WHITE_SMOKE,
					mouth.x + dx, mouth.y, mouth.z + dz, dx * 0.2, 0.05, dz * 0.2);
			}
		}
	}

	/**
	 * Ponto do modelo em pixels de quem olha a frente da máquina (x da esquerda para a direita, y do chão, z da frente
	 * para trás; o mesmo de tools/models/machines/machine_kit.py) no mundo, com a frente virada para {@code facing}.
	 */
	public static Vec3 local(BlockPos pos, Direction facing, double x, double y, double z) {
		double ox = (8.0 - x) / 16.0, oz = (z - 8.0) / 16.0;
		double angle = Math.toRadians(180.0 - facing.toYRot());
		double cos = Math.cos(angle), sin = Math.sin(angle);
		return new Vec3(pos.getX() + 0.5 + ox * cos + oz * sin, pos.getY() + y / 16.0, pos.getZ() + 0.5 - ox * sin + oz * cos);
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
		machine = MachineState.byId(input.getByteOr("machine", (byte) 0));
		speed = input.getFloatOr("speed", 1.0F);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putLong("energy", energy.amount);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/** Só o que o desenho precisa: estado da máquina e velocidade (a carga fica no servidor). */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		tag.putByte("machine", (byte) machine.ordinal());
		tag.putFloat("speed", speed);
		return tag;
	}
}
