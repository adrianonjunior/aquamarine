package com.aquamarine.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/**
 * Uma rede de pedaços ligados do mesmo tipo (0.6.1, 7C; docs/12-industria.md): um estoque só, do tamanho da soma dos
 * pedaços. A cada tick puxa dos conectores em "entra" e reparte entre os conectores em "sai" em partes iguais (quem fica
 * para trás num tick começa na frente no próximo), cada conector na vazão do seu pedaço; o comprimento da rede não atrasa
 * nada. Conteúdo diferente não se mistura: um pedaço com outro fluido fica numa rede separada, e o estoque recusa o
 * segundo fluido. A rede se refaz sozinha quando um pedaço entra, sai, muda de modo ou uma válvula fecha.
 */
public final class ConduitNetwork {
	/** Um conector numa máquina: o pedaço, o lado e o descanso depois de mover um balde inteiro (caldeirão). */
	private static final class Port {
		final int member;
		final Direction side;
		int rest;

		Port(int member, Direction side) {
			this.member = member;
			this.side = side;
		}
	}

	private static final int FAR = Integer.MAX_VALUE;
	final ConduitKind kind;
	final long capacity;
	final SingleVariantStorage<FluidVariant> fluid;
	final SimpleEnergyStorage energy;
	boolean valid = true;
	private final Level level;
	private final List<ConduitBlockEntity> members;
	private final int[][] next;
	private final List<Port> ports = new ArrayList<>();
	private final int[] distance;
	private long lastTick = Long.MIN_VALUE;
	private int rotate;
	private int active;
	private boolean dirty;

	private ConduitNetwork(Level level, ConduitKind kind, List<ConduitBlockEntity> members, int[][] next, FluidVariant variant, long amount) {
		this.level = level;
		this.kind = kind;
		this.members = members;
		this.next = next;
		long total = 0;
		for (ConduitBlockEntity m : members) {
			total += m.capacity();
		}
		this.capacity = Math.max(1, total);
		if (kind == ConduitKind.ENERGY) {
			this.fluid = null;
			this.energy = new SimpleEnergyStorage(capacity, capacity, capacity) {
				@Override
				protected void onFinalCommit() {
					dirty = true;
				}
			};
			this.energy.amount = Math.min(capacity, amount);
		} else {
			this.energy = null;
			this.fluid = new SingleVariantStorage<>() {
				@Override
				protected FluidVariant getBlankVariant() {
					return FluidVariant.blank();
				}

				@Override
				protected long getCapacity(FluidVariant variant) {
					return ConduitNetwork.this.capacity;
				}

				@Override
				protected boolean canInsert(FluidVariant variant) {
					return ConduitNetwork.this.kind.accepts(variant);
				}

				@Override
				protected void onFinalCommit() {
					dirty = true;
				}
			};
			if (!variant.isBlank() && amount > 0) {
				this.fluid.variant = variant;
				this.fluid.amount = Math.min(capacity, amount);
			}
		}
		this.distance = new int[members.size()];
	}

	/** Lado de uma máquina que serve de conector para esta rede (os pedaços de rede não contam). */
	public static boolean isPort(Level level, BlockPos pos, Direction side, ConduitKind kind) {
		if (level.getBlockState(pos).getBlock() instanceof ConduitPiece) {
			return false;
		}
		return switch (kind) {
			case ENERGY -> EnergyStorage.SIDED.find(level, pos, side) != null;
			case FLUID -> !(level.getBlockEntity(pos) instanceof GasPort) && FluidStorage.SIDED.find(level, pos, side) != null;
			case GAS -> level.getBlockEntity(pos) instanceof GasPort && FluidStorage.SIDED.find(level, pos, side) != null;
		};
	}

	/** Um pedaço entrou ou mudou em {@code pos}: as redes em volta se refazem (e se juntam). */
	public static void invalidateAround(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ConduitBlockEntity self) {
			self.invalidateNetwork();
		}
		for (Direction d : Direction.values()) {
			if (level.getBlockEntity(pos.relative(d)) instanceof ConduitBlockEntity other) {
				other.invalidateNetwork();
			}
		}
	}

	/** Percorre os pedaços ligados a partir de {@code start} e monta a rede com a soma das partes de cada um. */
	static ConduitNetwork build(Level level, ConduitBlockEntity start) {
		ConduitKind kind = start.kind();
		List<ConduitBlockEntity> members = new ArrayList<>();
		Map<BlockPos, Integer> index = new HashMap<>();
		ArrayDeque<ConduitBlockEntity> queue = new ArrayDeque<>();
		FluidVariant variant = start.shareVariant();
		members.add(start);
		index.put(start.getBlockPos(), 0);
		queue.add(start);
		List<int[]> links = new ArrayList<>();
		while (!queue.isEmpty()) {
			ConduitBlockEntity c = queue.poll();
			for (Direction d : Direction.values()) {
				if (!c.opens(d) || c.mode(d) == PortMode.OFF) {
					continue;
				}
				BlockPos np = c.getBlockPos().relative(d);
				if (!level.isLoaded(np) || !(level.getBlockEntity(np) instanceof ConduitBlockEntity o) || o.kind() != kind
					|| !o.opens(d.getOpposite()) || o.mode(d.getOpposite()) == PortMode.OFF || o.isRemoved()) {
					continue;
				}
				Integer known = index.get(np);
				if (known == null) {
					FluidVariant theirs = o.shareVariant();
					if (!theirs.isBlank() && !variant.isBlank() && !theirs.equals(variant)) {
						continue; // outro fluido: fica numa rede à parte
					}
					if (variant.isBlank()) {
						variant = theirs;
					}
					known = members.size();
					index.put(np, known);
					members.add(o);
					queue.add(o);
				}
				links.add(new int[] {index.get(c.getBlockPos()), d.ordinal(), known});
			}
		}
		int[][] next = new int[members.size()][6];
		for (int[] row : next) {
			Arrays.fill(row, -1);
		}
		for (int[] link : links) {
			next[link[0]][link[1]] = link[2];
		}
		long amount = 0;
		for (ConduitBlockEntity m : members) {
			if (m.network != null && m.network.valid) {
				m.network.invalidate(); // a rede antiga devolve as partes antes de juntar
			}
			if (kind == ConduitKind.ENERGY || m.shareVariant().isBlank() || m.shareVariant().equals(variant)) {
				amount += m.share();
			}
		}
		ConduitNetwork network = new ConduitNetwork(level, kind, members, next, variant, amount);
		for (ConduitBlockEntity m : members) {
			m.network = network;
		}
		network.findPorts();
		return network;
	}

	private void findPorts() {
		for (int i = 0; i < members.size(); i++) {
			ConduitBlockEntity m = members.get(i);
			for (Direction d : Direction.values()) {
				if (next[i][d.ordinal()] < 0 && m.opens(d) && m.mode(d) != PortMode.OFF
					&& isPort(level, m.getBlockPos().relative(d), d.getOpposite(), kind)) {
					ports.add(new Port(i, d));
				}
			}
		}
		// Distância de cada pedaço até a saída mais perto: o fluxo desenhado corre para onde ela diminui.
		Arrays.fill(distance, FAR);
		ArrayDeque<Integer> queue = new ArrayDeque<>();
		for (Port p : ports) {
			if (members.get(p.member).mode(p.side) == PortMode.OUT && distance[p.member] != 0) {
				distance[p.member] = 0;
				queue.add(p.member);
			}
		}
		while (!queue.isEmpty()) {
			int i = queue.poll();
			for (int j : next[i]) {
				if (j >= 0 && distance[j] == FAR) {
					distance[j] = distance[i] + 1;
					queue.add(j);
				}
			}
		}
	}

	/** Devolve a cada pedaço a parte dele (proporcional ao que ele soma) e deixa a rede para ser refeita. */
	void invalidate() {
		if (!valid) {
			return;
		}
		valid = false;
		FluidVariant variant = variant();
		long total = amount(), left = total;
		for (int i = 0; i < members.size(); i++) {
			ConduitBlockEntity m = members.get(i);
			long part = i == members.size() - 1 ? left : total * m.capacity() / capacity;
			left -= part;
			m.setShare(part > 0 ? variant : FluidVariant.blank(), part);
		}
	}

	FluidVariant variant() {
		return fluid == null || fluid.amount <= 0 ? FluidVariant.blank() : fluid.variant;
	}

	long amount() {
		return energy != null ? energy.amount : fluid.amount;
	}

	long shareOf(ConduitBlockEntity member) {
		return amount() * member.capacity() / capacity;
	}

	void tick(ServerLevel level) {
		long time = level.getGameTime();
		if (!valid || time == lastTick) {
			return;
		}
		lastTick = time;
		boolean moved = pull(level);
		moved |= push(level);
		active = moved ? 12 : Math.max(0, active - 1);
		if (dirty && time % 20 == 0) {
			members.forEach(ConduitBlockEntity::setChanged);
			dirty = false;
		}
		publish();
	}

	/** Conectores em "entra": puxam da máquina para a rede, cada um na vazão do seu pedaço. */
	private boolean pull(ServerLevel level) {
		boolean moved = false;
		for (Port p : ports) {
			ConduitBlockEntity m = members.get(p.member);
			if (m.mode(p.side) != PortMode.IN || amount() >= capacity) {
				continue;
			}
			BlockPos at = m.getBlockPos().relative(p.side);
			if (kind == ConduitKind.ENERGY) {
				EnergyStorage from = EnergyStorage.SIDED.find(level, at, p.side.getOpposite());
				if (from != null) {
					moved |= moveEnergy(from, energy, m.rate()) > 0;
				}
			} else {
				Storage<FluidVariant> from = FluidStorage.SIDED.find(level, at, p.side.getOpposite());
				if (from != null) {
					moved |= moveFluid(from, fluid, p, m.rate()) > 0;
				}
			}
		}
		return moved;
	}

	/** Conectores em "sai": repartem o estoque em partes iguais, começando por um diferente a cada tick. */
	private boolean push(ServerLevel level) {
		List<Port> outs = new ArrayList<>();
		for (Port p : ports) {
			if (members.get(p.member).mode(p.side) == PortMode.OUT) {
				outs.add(p);
			}
		}
		if (outs.isEmpty() || amount() <= 0) {
			return false;
		}
		boolean moved = false;
		int n = outs.size();
		rotate = (rotate + 1) % n;
		for (int k = 0; k < n && amount() > 0; k++) {
			Port p = outs.get((rotate + k) % n);
			ConduitBlockEntity m = members.get(p.member);
			long fair = Math.min(m.rate(), Math.max(1, (amount() + (n - k) - 1) / (n - k)));
			BlockPos at = m.getBlockPos().relative(p.side);
			if (kind == ConduitKind.ENERGY) {
				EnergyStorage to = EnergyStorage.SIDED.find(level, at, p.side.getOpposite());
				if (to != null && to.supportsInsertion()) {
					moved |= moveEnergy(energy, to, fair) > 0;
				}
			} else {
				Storage<FluidVariant> to = FluidStorage.SIDED.find(level, at, p.side.getOpposite());
				if (to != null) {
					moved |= moveFluid(fluid, to, p, fair) > 0;
				}
			}
		}
		return moved;
	}

	private static long moveEnergy(EnergyStorage from, EnergyStorage to, long max) {
		try (Transaction tx = Transaction.openOuter()) {
			long moved = EnergyStorageUtil.move(from, to, max, tx);
			tx.commit();
			return moved;
		}
	}

	/**
	 * Move fluido na vazão; se nada passar e o outro lado só aceitar baldes inteiros (caldeirão), move um balde e o conector
	 * descansa o tempo que a vazão levaria para mover esse balde.
	 */
	private long moveFluid(Storage<FluidVariant> from, Storage<FluidVariant> to, Port port, long rate) {
		if (port.rest > 0) {
			port.rest--;
			return 0;
		}
		long moved;
		try (Transaction tx = Transaction.openOuter()) {
			moved = StorageUtil.move(from, to, kind::accepts, rate, tx);
			tx.commit();
		}
		if (moved == 0 && rate < FluidConstants.BUCKET) {
			try (Transaction tx = Transaction.openOuter()) {
				moved = StorageUtil.move(from, to, kind::accepts, FluidConstants.BUCKET, tx);
				tx.commit();
			}
			if (moved > 0) {
				port.rest = (int) Math.max(0, moved / Math.max(1, rate) - 1);
			}
		}
		return moved;
	}

	/** Manda a cada pedaço o que o desenho mostra: conteúdo, nível de 0 a 8 e o sentido em cada lado enquanto há fluxo. */
	private void publish() {
		long amount = amount();
		int level8 = amount <= 0 ? 0 : (int) Math.max(1, Math.min(8, (amount * 8 + capacity - 1) / capacity));
		FluidVariant variant = variant();
		for (int i = 0; i < members.size(); i++) {
			members.get(i).show(variant, level8, active > 0 ? flow(i) : 0, active > 0);
		}
	}

	private int flow(int i) {
		int mask = 0;
		for (int k = 0; k < 6; k++) {
			int j = next[i][k];
			int bits = 0;
			if (j >= 0 && distance[i] != FAR) {
				bits = distance[j] < distance[i] ? 1 : distance[j] > distance[i] ? 2 : 0;
			}
			mask |= bits << (2 * k);
		}
		for (Port p : ports) {
			if (p.member == i) {
				PortMode mode = members.get(i).mode(p.side);
				mask &= ~(3 << (2 * p.side.ordinal()));
				mask |= (mode == PortMode.OUT ? 1 : mode == PortMode.IN ? 2 : 0) << (2 * p.side.ordinal());
			}
		}
		return mask;
	}
}
