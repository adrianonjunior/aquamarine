package com.aquamarine.entity.boss;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.AnchorBuoyBlock;
import com.aquamarine.block.ModBlocks;
import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.effect.ModEffects;
import com.aquamarine.entity.KrakenEggEntity;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.TentacleEntity;
import com.aquamarine.entity.YoungKrakenEntity;
import com.aquamarine.item.OrichalcumEffects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Os 8 tentáculos e os 10 ataques do Kraken (docs/02-kraken.md, "Tentáculos" e "Ataques e avisos").
 * Tudo no servidor. Cada ataque tem uma fase de aviso (partículas, som e a animação do contrato)
 * e, ao fim dela, o golpe. {@link KrakenEntity} chama {@link #tick} a cada tick; o comando
 * {@code /aquamarine kraken attack <nome>} usa {@link #force}.
 */
public final class KrakenAttacks {
	private static final double RING_RADIUS = 18.0;
	private static final SoundEvent[] NO_SOUND = {};

	/** Um ataque em fase de aviso. */
	private static final class Active {
		final KrakenAttackType type;
		int age;
		Vec3 point = Vec3.ZERO;
		Vec3 dir = Vec3.ZERO;
		List<UUID> targets = new ArrayList<>();
		int[] slots = new int[0];
		List<Vec3> eggs = new ArrayList<>();
		List<UUID> eggEntities = new ArrayList<>();

		Active(KrakenAttackType type) {
			this.type = type;
		}
	}

	/** Jogador preso por um ou dois tentáculos. */
	private static final class Grab {
		final UUID player;
		final int[] slots;
		final Vec3 anchor;
		int ticksLeft = KrakenAttackType.GRAB_TICKS;
		int sinceDamage;

		Grab(UUID player, int[] slots, Vec3 anchor) {
			this.player = player;
			this.slots = slots;
			this.anchor = anchor;
		}
	}

	private final KrakenEntity kraken;
	private final RandomSource random = RandomSource.create();
	private final UUID[] tentacles = new UUID[KrakenAttackType.TENTACLES];
	private final long[] cutAt = new long[KrakenAttackType.TENTACLES];
	private final List<Active> active = new ArrayList<>();
	private final List<Grab> grabs = new ArrayList<>();
	private final Set<UUID> youngs = new HashSet<>();
	private boolean adopted;
	private boolean exposedGiven;
	private boolean enabled = true;
	private int cooldown = 100;
	private int sinceArea = KrakenAttackType.AREA_COOLDOWN_TICKS;
	private KrakenAttackType last;
	private int whirlpoolTicks;
	private Vec3 whirlpoolCenter = Vec3.ZERO;

	public KrakenAttacks(KrakenEntity kraken) {
		this.kraken = kraken;
		java.util.Arrays.fill(cutAt, -1L);
	}

	// --- Controle externo ---

	/** Liga ou desliga o sorteio automático de ataques (o comando {@code ai on|off}). */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/** Tentáculos vivos agora. */
	public int aliveTentacles(ServerLevel level) {
		int n = 0;
		for (UUID id : tentacles) {
			if (id != null && level.getEntity(id) instanceof TentacleEntity t && t.isAlive()) {
				n++;
			}
		}
		return n;
	}

	/** Jogadores presos no momento. */
	public int grabbedCount() {
		return grabs.size();
	}

	public boolean isWhirlpoolActive() {
		return whirlpoolTicks > 0;
	}

	/** Locomoção (KrakenMovement): os tentáculos ociosos acompanham o corpo; os ocupados num ataque ficam. */
	public void follow(ServerLevel level, Vec3 delta) {
		if (delta.lengthSqr() < 1.0E-6) {
			return;
		}
		for (int slot = 0; slot < tentacles.length; slot++) {
			TentacleEntity t = tentacle(level, slot);
			if (t != null && !t.isBusy()) {
				t.snapTo(t.getX() + delta.x, t.getY() + delta.y, t.getZ() + delta.z, t.getYRot(), 0.0F);
			}
		}
	}

	/** Ataques em aviso agora (para testes e para o limite de simultâneos). */
	public int activeCount() {
		return active.size();
	}

	/** Crias vivas do Kraken. */
	public int youngCount(ServerLevel level) {
		youngs.removeIf(id -> !(level.getEntity(id) instanceof YoungKrakenEntity y) || !y.isAlive());
		return youngs.size();
	}

	/**
	 * Dispara {@code type} agora, ignorando fase, intervalo e limite de simultâneos (comando de
	 * teste). Devolve {@code false} se faltar alvo ou tentáculo livre.
	 */
	public boolean force(ServerLevel level, KrakenAttackType type, ServerPlayer target) {
		return begin(level, type, target, List.of(target));
	}

	// --- Tick ---

	public void tick(ServerLevel level) {
		adoptExisting(level);
		tickTentacles(level);
		if (!kraken.canAct()) {
			cancelAll(level);
			return;
		}
		for (Active a : new ArrayList<>(active)) {
			a.age++;
			if (a.age >= a.type.warnTicks()) {
				active.remove(a);
				strike(level, a);
			} else {
				warn(level, a);
			}
		}
		tickGrabs(level);
		tickWhirlpool(level);
		sinceArea++;
		if (cooldown > 0) {
			cooldown--;
		}
		if (enabled && cooldown <= 0) {
			launchNext(level);
		}
	}

	/** Cancela avisos, solta quem estava preso e para o redemoinho (atordoamento e troca de fase). */
	public void cancelAll(ServerLevel level) {
		for (Active a : active) {
			freeSlots(level, a.slots);
			discardEggs(level, a, false);
		}
		active.clear();
		for (Grab g : new ArrayList<>(grabs)) {
			release(level, g);
		}
		whirlpoolTicks = 0;
	}

	/** O Kraken saiu do mundo: leva tentáculos e crias junto. */
	public void cleanup(ServerLevel level) {
		for (Active a : active) {
			discardEggs(level, a, false);
		}
		for (UUID id : tentacles) {
			if (id != null && level.getEntity(id) != null) {
				level.getEntity(id).discard();
			}
		}
		for (UUID id : youngs) {
			Entity e = level.getEntity(id);
			if (e != null) {
				e.discard();
			}
		}
		youngs.clear();
	}

	// --- Tentáculos ---

	private void adoptExisting(ServerLevel level) {
		if (adopted) {
			return;
		}
		adopted = true;
		for (TentacleEntity t : level.getEntitiesOfClass(TentacleEntity.class, kraken.getBoundingBox().inflate(32.0),
			t -> kraken.getUUID().equals(t.owner()))) {
			if (t.slot() >= 0 && t.slot() < tentacles.length && tentacles[t.slot()] == null) {
				tentacles[t.slot()] = t.getUUID();
			}
		}
	}

	private Vec3 home(int slot) {
		double angle = slot * Math.PI * 2 / KrakenAttackType.TENTACLES;
		return kraken.position().add(Math.cos(angle) * RING_RADIUS, 0, Math.sin(angle) * RING_RADIUS);
	}

	private void tickTentacles(ServerLevel level) {
		long now = level.getGameTime();
		int cut = 0;
		for (int i = 0; i < tentacles.length; i++) {
			boolean alive = tentacles[i] != null && level.getEntity(tentacles[i]) instanceof TentacleEntity t && t.isAlive();
			if (alive) {
				continue;
			}
			if (cutAt[i] < 0) {
				cutAt[i] = tentacles[i] == null ? now - KrakenAttackType.TENTACLE_RESPAWN_TICKS : now;
			}
			if (now - cutAt[i] >= KrakenAttackType.TENTACLE_RESPAWN_TICKS) {
				spawnTentacle(level, i);
				cutAt[i] = -1L;
			} else {
				cut++;
			}
		}
		if (cut == tentacles.length && !exposedGiven) {
			exposedGiven = true;
			kraken.expose(KrakenAttackType.EXPOSED_TICKS);
		} else if (cut < tentacles.length) {
			exposedGiven = false;
		}
	}

	private void spawnTentacle(ServerLevel level, int slot) {
		TentacleEntity t = ModEntities.TENTACLE.create(level, EntitySpawnReason.EVENT);
		if (t == null) {
			return;
		}
		Vec3 pos = home(slot);
		t.snapTo(pos.x, pos.y, pos.z, (float) (slot * 45.0), 0.0F);
		t.bind(kraken.getUUID(), slot);
		level.addFreshEntity(t);
		tentacles[slot] = t.getUUID();
	}

	private TentacleEntity tentacle(ServerLevel level, int slot) {
		return tentacles[slot] != null && level.getEntity(tentacles[slot]) instanceof TentacleEntity t && t.isAlive() ? t : null;
	}

	private List<Integer> freeSlots(ServerLevel level) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < tentacles.length; i++) {
			TentacleEntity t = tentacle(level, i);
			if (t != null && !t.isBusy()) {
				list.add(i);
			}
		}
		return list;
	}

	/** Escolhe os {@code count} tentáculos livres mais próximos de {@code point} e os marca ocupados. */
	private int[] claim(ServerLevel level, int count, Vec3 point) {
		List<Integer> free = freeSlots(level);
		if (free.size() < count) {
			return null;
		}
		free.sort(Comparator.comparingDouble(i -> home(i).distanceToSqr(point)));
		int[] slots = new int[count];
		for (int n = 0; n < count; n++) {
			slots[n] = free.get(n);
			tentacle(level, slots[n]).setBusy(true);
		}
		return slots;
	}

	private void freeSlots(ServerLevel level, int[] slots) {
		for (int slot : slots) {
			TentacleEntity t = tentacle(level, slot);
			if (t != null) {
				t.setBusy(false);
				t.playAnimation("release");
				Vec3 h = home(slot);
				t.snapTo(h.x, h.y, h.z, t.getYRot(), 0.0F);
			}
		}
	}

	// --- Sorteio ---

	private List<ServerPlayer> targets(ServerLevel level) {
		List<ServerPlayer> list = new ArrayList<>();
		for (ServerPlayer p : kraken.playersInArena(level)) {
			if (!p.isCreative()) {
				list.add(p);
			}
		}
		return list;
	}

	private void launchNext(ServerLevel level) {
		List<ServerPlayer> players = targets(level);
		if (players.isEmpty()) {
			return;
		}
		if (active.size() + grabs.size() >= KrakenAttackType.maxSimultaneous(players.size())) {
			return;
		}
		Set<KrakenAttackType> running = EnumSet.noneOf(KrakenAttackType.class);
		for (Active a : active) {
			running.add(a.type);
		}
		List<KrakenAttackType> options = KrakenAttackType.candidates(kraken.phase(), last, running, sinceArea, freeSlots(level).size());
		if (options.isEmpty()) {
			return;
		}
		KrakenAttackType type = options.get(random.nextInt(options.size()));
		ServerPlayer target = players.get(random.nextInt(players.size()));
		if (kraken.aggroTarget() != null && random.nextInt(3) > 0) {
			for (ServerPlayer p : players) {
				if (p.getUUID().equals(kraken.aggroTarget())) {
					target = p;
				}
			}
		}
		boolean abyssal = AquamarineConfig.get().isAbyssal();
		cooldown = KrakenAttackType.attackInterval(kraken.phase(), abyssal);
		begin(level, type, target, players);
	}

	// --- Início do aviso ---

	private boolean begin(ServerLevel level, KrakenAttackType type, ServerPlayer target, List<ServerPlayer> players) {
		Active a = new Active(type);
		a.point = target.position();
		if (type.tentacles() > 0) {
			a.slots = claim(level, type.tentacles(), a.point);
			if (a.slots == null) {
				return false;
			}
			for (int n = 0; n < a.slots.length; n++) {
				Vec3 spot = a.point.add(n == 0 ? 0 : 1.2, -1.0, 0);
				TentacleEntity t = tentacle(level, a.slots[n]);
				t.snapTo(spot.x, spot.y, spot.z, t.getYRot(), 0.0F);
				t.playAnimation("rise");
			}
		}
		a.targets.add(target.getUUID());
		switch (type) {
			case WATER_JET -> {
				Vec3 from = kraken.position().add(0, kraken.getBbHeight() * 0.5, 0);
				a.point = from;
				a.dir = target.getEyePosition().subtract(from).normalize();
			}
			case SPAWN_YOUNG -> {
				int count = KrakenAttackType.youngCount(players.size());
				for (int n = 0; n < count; n++) {
					double angle = random.nextDouble() * Math.PI * 2;
					double r = 3.0 + random.nextDouble() * 5.0;
					Vec3 spot = kraken.position().add(Math.cos(angle) * r, 0.2, Math.sin(angle) * r);
					a.eggs.add(spot);
					KrakenEggEntity egg = ModEntities.KRAKEN_EGG.create(level, EntitySpawnReason.EVENT);
					if (egg != null) {
						egg.snapTo(spot.x, spot.y, spot.z, random.nextFloat() * 360.0F, 0.0F);
						level.addFreshEntity(egg);
						a.eggEntities.add(egg.getUUID());
					}
				}
			}
			case BOLTS -> {
				a.targets.clear();
				int marks = Math.max(1, Math.min(players.size(), 1 + players.size() / 3));
				for (int n = 0; n < marks; n++) {
					a.targets.add(players.get(n).getUUID());
				}
				if (!a.targets.contains(target.getUUID())) {
					a.targets.set(0, target.getUUID());
				}
			}
			default -> {
			}
		}
		if (type.isArea()) {
			sinceArea = 0;
		}
		last = type;
		kraken.playAnimation(type.animation());
		warnSound(level, a);
		active.add(a);
		return true;
	}

	private void warnSound(ServerLevel level, Active a) {
		SoundEvent sound = switch (a.type) {
			case SLAM -> SoundEvents.WARDEN_SONIC_CHARGE;
			case GRAB, DOUBLE_GRAB -> SoundEvents.ENDERMAN_STARE;
			case INK -> SoundEvents.SQUID_SQUIRT;
			case WHIRLPOOL -> SoundEvents.BEACON_DEACTIVATE;
			case SPAWN_YOUNG -> SoundEvents.TURTLE_EGG_CRACK;
			case WATER_JET -> SoundEvents.GUARDIAN_ATTACK;
			case ROAR -> SoundEvents.ENDER_DRAGON_GROWL;
			case BOLTS -> SoundEvents.TRIDENT_THUNDER.value();
			case AMBUSH -> SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT;
		};
		float pitch = a.type == KrakenAttackType.GRAB || a.type == KrakenAttackType.DOUBLE_GRAB ? 1.8F : 0.7F;
		Vec3 at = a.type == KrakenAttackType.SLAM || a.type == KrakenAttackType.AMBUSH ? a.point : kraken.position();
		level.playSound(null, at.x, at.y, at.z, sound, SoundSource.HOSTILE, 3.0F, pitch);
	}

	// --- Aviso (por tick) ---

	private void particles(ServerLevel level, ParticleOptions type, Vec3 p, int count, double spread, double speed) {
		level.sendParticles(type, p.x, p.y, p.z, count, spread, spread * 0.5, spread, speed);
	}

	private void ring(ServerLevel level, ParticleOptions type, Vec3 center, double radius, int points) {
		for (int i = 0; i < points; i++) {
			double angle = i * Math.PI * 2 / points;
			particles(level, type, center.add(Math.cos(angle) * radius, 0.1, Math.sin(angle) * radius), 1, 0.0, 0.0);
		}
	}

	private void warn(ServerLevel level, Active a) {
		float progress = a.age / (float) a.type.warnTicks();
		switch (a.type) {
			case SLAM -> {
				// Claro e brilhante: a tinta escura sumia na água funda (relatório 2Q, P2).
				ring(level, ParticleTypes.END_ROD, a.point, KrakenAttackType.SLAM_RADIUS, 24);
				ring(level, ParticleTypes.GLOW, a.point, KrakenAttackType.SLAM_RADIUS * 0.5, 12);
			}
			case GRAB, DOUBLE_GRAB -> {
				for (int slot : a.slots) {
					TentacleEntity t = tentacle(level, slot);
					if (t != null) {
						particles(level, ParticleTypes.WITCH, t.position().add(0, 2.0, 0), 6, 0.8, 0.02);
						particles(level, ParticleTypes.PORTAL, t.position().add(0, 1.0, 0), 4, 0.6, 0.3);
					}
				}
			}
			case INK -> {
				// espiral que cresce até o raio da nuvem
				double angle = a.age * 0.5;
				double r = KrakenAttackType.INK_RADIUS * progress;
				for (int arm = 0; arm < 3; arm++) {
					double ang = angle + arm * Math.PI * 2 / 3;
					particles(level, ParticleTypes.SQUID_INK, kraken.position().add(Math.cos(ang) * r, 2.0, Math.sin(ang) * r), 3, 0.5, 0.01);
				}
			}
			case WHIRLPOOL -> {
				double angle = a.age * 0.7;
				for (int arm = 0; arm < 2; arm++) {
					double r = KrakenAttackType.WHIRLPOOL_RADIUS * (1.0 - progress) + 2.0;
					double ang = angle + arm * Math.PI;
					particles(level, ParticleTypes.BUBBLE, kraken.position().add(Math.cos(ang) * r, 1.0, Math.sin(ang) * r), 4, 0.4, 0.05);
				}
			}
			case SPAWN_YOUNG -> {
				for (Vec3 egg : a.eggs) {
					particles(level, ParticleTypes.GLOW, egg, 2, 0.3, 0.02);
				}
			}
			case WATER_JET -> {
				for (int d = 1; d <= KrakenAttackType.JET_LENGTH; d += 2) {
					particles(level, ParticleTypes.BUBBLE, a.point.add(a.dir.scale(d)), 1, 0.15, 0.0);
				}
			}
			case ROAR -> {
				double r = 10.0 * (1.0 - progress) + 3.0;
				ring(level, ParticleTypes.BUBBLE_POP, kraken.position().add(0, 2.0, 0), r, 20);
			}
			case BOLTS -> {
				for (UUID id : a.targets) {
					if (level.getPlayerByUUID(id) instanceof ServerPlayer p) {
						particles(level, ParticleTypes.ELECTRIC_SPARK, p.position().add(0, p.getBbHeight() + 0.6, 0), 4, 0.3, 0.05);
						particles(level, ParticleTypes.SOUL_FIRE_FLAME, p.position().add(0, p.getBbHeight() + 0.3, 0), 2, 0.2, 0.01);
					}
				}
			}
			case AMBUSH -> {
				ring(level, ParticleTypes.BUBBLE, a.point, KrakenAttackType.AMBUSH_RADIUS, 24);
				particles(level, ParticleTypes.BUBBLE_COLUMN_UP, a.point, 6, KrakenAttackType.AMBUSH_RADIUS * 0.5, 0.1);
			}
		}
	}

	// --- Golpe ---

	private void hurt(ServerLevel level, Player player, float amount) {
		player.hurtServer(level, level.damageSources().mobAttack(kraken), amount);
	}

	private void strike(ServerLevel level, Active a) {
		switch (a.type) {
			case SLAM -> strikeSlam(level, a);
			case GRAB, DOUBLE_GRAB -> strikeGrab(level, a);
			case INK -> strikeInk(level);
			case WHIRLPOOL -> {
				whirlpoolTicks = KrakenAttackType.WHIRLPOOL_TICKS;
				whirlpoolCenter = kraken.position();
			}
			case SPAWN_YOUNG -> strikeSpawnYoung(level, a);
			case WATER_JET -> strikeJet(level, a);
			case ROAR -> strikeRoar(level);
			case BOLTS -> strikeBolts(level, a);
			case AMBUSH -> strikeAmbush(level, a);
		}
		if (a.type != KrakenAttackType.GRAB && a.type != KrakenAttackType.DOUBLE_GRAB) {
			freeSlots(level, a.slots);
		}
	}

	private void strikeSlam(ServerLevel level, Active a) {
		for (int slot : a.slots) {
			TentacleEntity t = tentacle(level, slot);
			if (t != null) {
				t.playAnimation("slam");
			}
		}
		AABB zone = new AABB(a.point, a.point).inflate(KrakenAttackType.SLAM_RADIUS, 4.0, KrakenAttackType.SLAM_RADIUS);
		for (Player p : level.getEntitiesOfClass(Player.class, zone, p -> p.isAlive() && !p.isSpectator())) {
			if (p.position().subtract(a.point).horizontalDistanceSqr() <= KrakenAttackType.SLAM_RADIUS * KrakenAttackType.SLAM_RADIUS) {
				hurt(level, p, KrakenAttackType.SLAM_DAMAGE);
			}
		}
		// a onda vira os barcos num raio maior
		for (Entity e : level.getEntities((Entity) null, new AABB(a.point, a.point).inflate(6.0), e -> e.is(EntityTypeTags.BOAT))) {
			e.kill(level);
		}
		collapseBuoys(level, a.point, 6);
		particles(level, ParticleTypes.EXPLOSION, a.point.add(0, 1.0, 0), 6, 1.5, 0.0);
		particles(level, ParticleTypes.SPLASH, a.point.add(0, 1.0, 0), 60, 2.5, 0.4);
		level.playSound(null, a.point.x, a.point.y, a.point.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 3.0F, 0.6F);
	}

	private void strikeGrab(ServerLevel level, Active a) {
		boolean any = false;
		for (ServerPlayer p : kraken.playersInArena(level)) {
			if (p.position().subtract(a.point).horizontalDistanceSqr() <= 16.0 && !p.isCreative() && !isGrabbed(p)) {
				grabs.add(new Grab(p.getUUID(), a.slots, a.point));
				for (int slot : a.slots) {
					TentacleEntity t = tentacle(level, slot);
					if (t != null) {
						t.playAnimation("grab");
					}
				}
				any = true;
				break;
			}
		}
		if (!any) {
			freeSlots(level, a.slots);
		}
	}

	private boolean isGrabbed(Player p) {
		for (Grab g : grabs) {
			if (g.player.equals(p.getUUID())) {
				return true;
			}
		}
		return false;
	}

	private void strikeInk(ServerLevel level) {
		for (ServerPlayer p : level.getEntitiesOfClass(ServerPlayer.class, kraken.getBoundingBox().inflate(KrakenAttackType.INK_RADIUS),
			p -> p.isAlive() && !p.isSpectator() && p.distanceTo(kraken) <= KrakenAttackType.INK_RADIUS)) {
			if (!OrichalcumEffects.hasFullSet(p)) {
				p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, KrakenAttackType.INK_BLIND_TICKS, 0));
			}
		}
		particles(level, ParticleTypes.SQUID_INK, kraken.position().add(0, 2.0, 0), 200, 8.0, 0.05);
	}

	/** Choca (ou some com) os ovos do aviso de um ataque. */
	private static void discardEggs(ServerLevel level, Active a, boolean hatch) {
		for (UUID id : a.eggEntities) {
			if (level.getEntity(id) instanceof KrakenEggEntity egg) {
				if (hatch) {
					egg.hatch();
				} else {
					egg.discard();
				}
			}
		}
		a.eggEntities.clear();
	}

	private void strikeSpawnYoung(ServerLevel level, Active a) {
		discardEggs(level, a, true);
		for (Vec3 egg : a.eggs) {
			if (youngCount(level) >= KrakenAttackType.MAX_YOUNG) {
				break;
			}
			YoungKrakenEntity young = ModEntities.YOUNG_KRAKEN.create(level, EntitySpawnReason.EVENT);
			if (young != null) {
				young.snapTo(egg.x, egg.y, egg.z, random.nextFloat() * 360.0F, 0.0F);
				level.addFreshEntity(young);
				youngs.add(young.getUUID());
				particles(level, ParticleTypes.POOF, egg, 8, 0.4, 0.05);
			}
		}
	}

	private void strikeJet(ServerLevel level, Active a) {
		Vec3 end = a.point.add(a.dir.scale(KrakenAttackType.JET_LENGTH));
		for (int d = 1; d <= KrakenAttackType.JET_LENGTH; d++) {
			particles(level, ParticleTypes.SPLASH, a.point.add(a.dir.scale(d)), 5, 0.3, 0.2);
		}
		AABB box = new AABB(a.point, end).inflate(2.0);
		for (Player p : level.getEntitiesOfClass(Player.class, box, p -> p.isAlive() && !p.isSpectator())) {
			if (p.getBoundingBox().inflate(0.75).clip(a.point, end).isPresent()) {
				hurt(level, p, KrakenAttackType.JET_DAMAGE);
			}
		}
		level.playSound(null, a.point.x, a.point.y, a.point.z, SoundEvents.DOLPHIN_SPLASH, SoundSource.HOSTILE, 3.0F, 0.5F);
	}

	private void strikeRoar(ServerLevel level) {
		Vec3 c = kraken.position();
		int radius = (int) KrakenAttackType.ROAR_RADIUS;
		int broken = 0;
		BlockPos origin = kraken.blockPosition();
		for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
			if (broken >= 500 || pos.distToCenterSqr(c) > radius * radius) {
				continue;
			}
			BlockState state = level.getBlockState(pos);
			if (state.isAir() || !state.getFluidState().isEmpty() && state.getBlock() instanceof net.minecraft.world.level.block.LiquidBlock) {
				continue;
			}
			if (state.is(ModBlocks.ANCHOR_BUOY)) {
				AnchorBuoyBlock.collapse(level, pos.immutable());
			} else if (state.getDestroySpeed(level, pos) >= 0.0F && state.getDestroySpeed(level, pos) <= 0.5F) {
				level.destroyBlock(pos, false);
				broken++;
			}
		}
		for (Player p : level.getEntitiesOfClass(Player.class, kraken.getBoundingBox().inflate(24.0), p -> p.isAlive() && !p.isSpectator())) {
			Entity vehicle = p.getVehicle();
			if (vehicle != null && vehicle.is(EntityTypeTags.BOAT)) {
				p.stopRiding();
				vehicle.kill(level);
				hurt(level, p, KrakenAttackType.ROAR_DAMAGE);
			}
		}
		particles(level, ParticleTypes.EXPLOSION_EMITTER, c.add(0, 2.0, 0), 1, 0.0, 0.0);
		level.playSound(null, c.x, c.y, c.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 4.0F, 0.4F);
	}

	private void strikeBolts(ServerLevel level, Active a) {
		for (UUID id : a.targets) {
			if (!(level.getPlayerByUUID(id) instanceof ServerPlayer p) || !p.isAlive() || p.isSpectator()) {
				continue;
			}
			if (p.isUnderWater()) {
				// submerso: a marca expira e o raio se dissipa
				particles(level, ParticleTypes.BUBBLE_POP, p.position().add(0, 1.0, 0), 12, 0.4, 0.05);
				continue;
			}
			for (int y = 0; y < 12; y++) {
				particles(level, ParticleTypes.ELECTRIC_SPARK, p.position().add(0, y, 0), 6, 0.3, 0.1);
			}
			level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 2.0F, 1.2F);
			hurt(level, p, KrakenAttackType.BOLT_DAMAGE);
		}
	}

	private void strikeAmbush(ServerLevel level, Active a) {
		double r = KrakenAttackType.AMBUSH_RADIUS;
		for (Player p : level.getEntitiesOfClass(Player.class, new AABB(a.point, a.point).inflate(r, 8.0, r), p -> p.isAlive() && !p.isSpectator())) {
			if (p.position().subtract(a.point).horizontalDistanceSqr() <= r * r) {
				hurt(level, p, KrakenAttackType.AMBUSH_DAMAGE);
				push((ServerPlayer) p, new Vec3(0, 0.9, 0));
			}
		}
		particles(level, ParticleTypes.POOF, a.point.add(0, 0.5, 0), 40, r * 0.5, 0.2);
		level.playSound(null, a.point.x, a.point.y, a.point.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.5F, 0.8F);
	}

	private void collapseBuoys(ServerLevel level, Vec3 center, int radius) {
		BlockPos origin = BlockPos.containing(center);
		for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
			if (level.getBlockState(pos).is(ModBlocks.ANCHOR_BUOY)) {
				AnchorBuoyBlock.collapse(level, pos.immutable());
			}
		}
	}

	// --- Efeitos que duram ---

	private void tickGrabs(ServerLevel level) {
		for (Grab g : new ArrayList<>(grabs)) {
			if (!(level.getPlayerByUUID(g.player) instanceof ServerPlayer p) || !p.isAlive()) {
				release(level, g);
				continue;
			}
			int alive = 0;
			for (int slot : g.slots) {
				if (tentacle(level, slot) != null) {
					alive++;
				}
			}
			if (alive == 0) {
				release(level, g);
				continue;
			}
			boolean partial = alive < g.slots.length;
			float dps = g.slots.length == 2 ? KrakenAttackType.DOUBLE_GRAB_DPS : KrakenAttackType.GRAB_DPS;
			if (partial) {
				dps /= 2.0F;
			}
			int rate = (ModEffects.hasTonic(p) ? 2 : 1) * (partial ? 2 : 1);
			g.ticksLeft -= rate;
			Vec3 pull = g.anchor.subtract(p.position());
			push(p, new Vec3(pull.x * 0.1, partial ? -0.15 : -0.35, pull.z * 0.1));
			particles(level, ParticleTypes.WITCH, p.position().add(0, 1.0, 0), 2, 0.4, 0.02);
			if (++g.sinceDamage >= 20) {
				g.sinceDamage = 0;
				hurt(level, p, dps);
			}
			if (g.ticksLeft <= 0) {
				release(level, g);
			}
		}
	}

	/** Define a velocidade do jogador e a envia na hora (o cliente é quem move o jogador). */
	private static void push(ServerPlayer p, Vec3 velocity) {
		p.setDeltaMovement(velocity);
		p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(p));
	}

	private void release(ServerLevel level, Grab g) {
		grabs.remove(g);
		freeSlots(level, g.slots);
	}

	private void tickWhirlpool(ServerLevel level) {
		if (whirlpoolTicks <= 0) {
			return;
		}
		whirlpoolTicks--;
		double angle = whirlpoolTicks * 0.6;
		for (int arm = 0; arm < 2; arm++) {
			double ang = angle + arm * Math.PI;
			particles(level, ParticleTypes.BUBBLE, whirlpoolCenter.add(Math.cos(ang) * 8.0, 1.0, Math.sin(ang) * 8.0), 4, 0.5, 0.1);
		}
		for (ServerPlayer p : level.getEntitiesOfClass(ServerPlayer.class, kraken.getBoundingBox().inflate(KrakenAttackType.WHIRLPOOL_RADIUS),
			p -> p.isAlive() && !p.isSpectator() && p.position().distanceTo(whirlpoolCenter) <= KrakenAttackType.WHIRLPOOL_RADIUS)) {
			if (hasCurrentAmulet(p)) {
				continue;
			}
			Vec3 pull = whirlpoolCenter.subtract(p.position());
			if (pull.lengthSqr() > 4.0) {
				// O servidor não simula o jogador: somar acumularia sem parar, então a velocidade é definida.
				push(p, pull.normalize().scale(0.1));
			}
		}
	}

	/** O Amuleto da Corrente (Entrega 3) protege do redemoinho; sem o item registrado, ninguém é imune. */
	private static boolean hasCurrentAmulet(Player p) {
		Item amulet = BuiltInRegistries.ITEM.getOptional(Aquamarine.id("current_amulet")).orElse(null);
		if (amulet == null) {
			return false;
		}
		for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
			if (p.getInventory().getItem(i).is(amulet)) {
				return true;
			}
		}
		return false;
	}
}
