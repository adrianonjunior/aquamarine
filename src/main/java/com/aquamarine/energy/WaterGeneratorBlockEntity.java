package com.aquamarine.energy;

import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import com.aquamarine.machine.MachineView;
import com.aquamarine.network.ModNetworks;
import com.aquamarine.network.GasPort;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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
 * Na 7E a Fenda ganhou o modo fluido (água e lava pelo Cano, em qualquer lugar) e na 7F todo gerador pode ser regulado:
 * com Ar Comprimido chegando pelo Tubo de Ar rende 25% a mais (docs/12-industria.md, 7E e 7F).
 */
public class WaterGeneratorBlockEntity extends BlockEntity implements MachineView, GasPort {
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
	/** Modo fluido da Fenda: um balde de água a cada 30 s e um de lava a cada 2,5 min (valores iniciais). */
	public static final long WATER_USE = FluidConstants.BUCKET / 600;
	public static final long LAVA_USE = FluidConstants.BUCKET / 3000;
	/** Regulação: um balde de Ar Comprimido a cada 30 s; rende 5/4 da produção. */
	public static final long AIR_USE = FluidConstants.BUCKET / 600;
	public static final long TANK = 4 * FluidConstants.BUCKET;
	public static final long AIR_TANK = 2 * FluidConstants.BUCKET;

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
	private final Tank water = new Tank(Fluids.WATER, TANK);
	private final Tank lava = new Tank(Fluids.LAVA, TANK);
	private final Tank air = new Tank(ModNetworks.COMPRESSED_AIR, AIR_TANK);
	/** O que entra pelos anéis: cada fluido vai para o seu tanque; nada sai. */
	private final Storage<FluidVariant> inlet = FilteringStorage.insertOnlyOf(new CombinedStorage<>(List.of(water, lava, air)));
	private boolean regulated;

	/** Tanque de um fluido só, que avisa o bloco quando muda. */
	private final class Tank extends SingleVariantStorage<FluidVariant> {
		private final Fluid fluid;
		private final long capacity;

		Tank(Fluid fluid, long capacity) {
			this.fluid = fluid;
			this.capacity = capacity;
		}

		@Override
		protected FluidVariant getBlankVariant() {
			return FluidVariant.blank();
		}

		@Override
		protected long getCapacity(FluidVariant variant) {
			return capacity;
		}

		@Override
		protected boolean canInsert(FluidVariant variant) {
			return variant.isOf(fluid) && (fluid == ModNetworks.COMPRESSED_AIR || kind() == WaterGeneratorBlock.Kind.VENT);
		}

		@Override
		protected void onFinalCommit() {
			setChanged();
		}

		boolean has(long use) {
			return amount >= use;
		}

		void use(long use) {
			amount -= use;
			if (amount <= 0) {
				amount = 0;
				variant = FluidVariant.blank();
			}
		}

		void load(long stored) {
			amount = Math.clamp(stored, 0, capacity);
			variant = amount > 0 ? FluidVariant.of(fluid) : FluidVariant.blank();
		}
	}

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

	/** Água, lava e ar guardados (em gotas da Fabric). */
	public long water() {
		return water.amount;
	}

	public long lava() {
		return lava.amount;
	}

	public long air() {
		return air.amount;
	}

	/** Recebeu Ar Comprimido no último tick em que produziu. */
	public boolean regulated() {
		return regulated;
	}

	private Direction front() {
		BlockState state = getBlockState();
		return state.hasProperty(HorizontalDirectionalBlock.FACING) ? state.getValue(HorizontalDirectionalBlock.FACING) : null;
	}

	/** O Tubo de Ar liga em qualquer lado; na Fenda, menos na frente (a do mostrador). */
	@Override
	public boolean gasSide(Direction side) {
		return kind() != WaterGeneratorBlock.Kind.VENT || side != front();
	}

	/** Só a Fenda recebe água e lava pelo Cano, pelos mesmos lados do ar. */
	@Override
	public boolean liquidSide(Direction side) {
		return kind() == WaterGeneratorBlock.Kind.VENT && side != front();
	}

	/** API de fluidos: a entrada dos anéis, pelos lados em que o Cano ou o Tubo ligam. */
	public Storage<FluidVariant> fluidPort(Direction side) {
		return side == null || gasSide(side) ? inlet : null;
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
		// Modo fluido (7E): sem fenda no lugar, a Fenda gera com água e lava dos tanques, em qualquer lugar.
		boolean fluid = rate == 0 && be.kind() == WaterGeneratorBlock.Kind.VENT && be.water.has(WATER_USE) && be.lava.has(LAVA_USE);
		if (fluid) {
			rate = WaterEnergy.VENT_RATE;
		}
		be.regulated = rate > 0 && be.air.has(AIR_USE);
		if (be.regulated) {
			rate = rate * 5 / 4;
		}
		be.lastRate = rate;
		// Carga cheia: a máquina segue no estado (sobrecarga), mas não gasta água, lava nem ar.
		if (rate > 0 && be.energy.amount < be.energy.capacity) {
			be.energy.amount = Math.min(be.energy.capacity, be.energy.amount + rate);
			if (fluid) {
				be.water.use(WATER_USE);
				be.lava.use(LAVA_USE);
			}
			if (be.regulated) {
				be.air.use(AIR_USE);
			}
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
		water.load(input.getLongOr("water", 0L));
		lava.load(input.getLongOr("lava", 0L));
		air.load(input.getLongOr("air", 0L));
		machine = MachineState.byId(input.getByteOr("machine", (byte) 0));
		speed = input.getFloatOr("speed", 1.0F);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putLong("energy", energy.amount);
		output.putLong("water", water.amount);
		output.putLong("lava", lava.amount);
		output.putLong("air", air.amount);
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
