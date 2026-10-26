package com.aquamarine.entity;

import com.aquamarine.entity.boss.AnimationSync;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Base das baleias da Fase 3 (docs/08-fase3.md, seção 6): nado livre em água funda, fuga do Kraken
 * a {@link #KRAKEN_FLEE_RANGE} blocos (nunca entram na arena) e animações sincronizadas pelo nome.
 * O servidor decide tudo; o cliente só anima.
 */
public abstract class AbstractWhaleEntity extends PathfinderMob {
	/** As baleias fogem a 48 blocos do Kraken (docs/00-decisoes.md). */
	public static final double KRAKEN_FLEE_RANGE = 48.0;
	/** Surgem a pelo menos esta distância abaixo da superfície (não nascem boiando). */
	public static final int SPAWN_DEPTH = 6;
	/** Coluna d'água mínima no lugar (nível do mar menos o fundo): o oceano fundo tem uns 30 blocos, o comum 15 a 20. */
	public static final int MIN_WATER_DEPTH = 14;
	/** No máximo 4 baleias (e cachalotes) numa área de 16 x 16 chunks: sobra vaga para a Moréia, que divide a categoria. */
	public static final int MAX_PER_AREA = 4;
	/** Distância em que a baleia selvagem não some sozinha. */
	public static final double KEEP_NEAR = 64.0;

	private static final EntityDataAccessor<Integer> DATA_ANIM = SynchedEntityData.defineId(AbstractWhaleEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ANIM_SEQ = SynchedEntityData.defineId(AbstractWhaleEntity.class, EntityDataSerializers.INT);

	protected final AnimationSync animations;
	/** Partes de colisão (6B1): cabeça, fim do corpo e rabo; a caixa da baleia é o meio. */
	public static final int PARTS = 3;
	public static final int HEAD = 0;
	/**
	 * Por espécie (Baleia-de-Nereia, depois Cachalote) e parte: avanço ao longo do corpo em blocos (+ = frente), altura
	 * do centro (fração da caixa), largura e altura. Os modelos têm ~18 blocos de comprimento; a caixa só cobre o meio.
	 */
	static final float[][] PART_SHAPES = {
		{6.2F, 0.5F, 4.6F, 4.2F}, {-5.0F, 0.5F, 4.4F, 3.8F}, {-8.8F, 0.55F, 3.6F, 2.4F},
		{5.8F, 0.5F, 4.8F, 4.6F}, {-4.6F, 0.5F, 4.2F, 3.6F}, {-8.0F, 0.55F, 3.4F, 2.2F}};
	private final List<WhalePartEntity> parts = new ArrayList<>();
	private Vec3 wanderTarget;
	private int fleeTicks;

	protected AbstractWhaleEntity(EntityType<? extends AbstractWhaleEntity> type, Level level, List<String> animationNames) {
		super(type, level);
		this.animations = new AnimationSync(animationNames);
		this.setNoGravity(true);
	}

	/** Surgimento natural: dentro d'água funda o bastante para o corpo, sem lotar a área. */
	public static boolean checkSpawn(EntityType<? extends AbstractWhaleEntity> type, ServerLevelAccessor level, EntitySpawnReason reason,
		BlockPos pos, net.minecraft.util.RandomSource random) {
		if (reason != EntitySpawnReason.NATURAL && reason != EntitySpawnReason.CHUNK_GENERATION) {
			return true;
		}
		int sea = level.getLevel().getSeaLevel();
		return level.getFluidState(pos).is(FluidTags.WATER) && pos.getY() <= sea - SPAWN_DEPTH
			&& sea - level.getHeight(Heightmap.Types.OCEAN_FLOOR, pos.getX(), pos.getZ()) >= MIN_WATER_DEPTH
			&& level.getEntitiesOfClass(AbstractWhaleEntity.class, new AABB(pos).inflate(128.0, 64.0, 128.0)).size() < MAX_PER_AREA;
	}

	/** Baleia selvagem não some sozinha enquanto alguém está a até {@link #KEEP_NEAR} blocos (some longe, como as outras). */
	@Override
	public boolean removeWhenFarAway(double distSqr) {
		return distSqr > KEEP_NEAR * KEEP_NEAR;
	}

	/** O padrão de {@code Mob} recusa nascer com o corpo em líquido; baleias só nascem nele (como {@code WaterAnimal}). */
	@Override
	public boolean checkSpawnObstruction(LevelReader level) {
		return level.isUnobstructed(this);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_ANIM, 0);
		builder.define(DATA_ANIM_SEQ, 0);
	}

	/** Dispara uma animação pelo nome do contrato (servidor; o cliente recebe pelos dados sincronizados). */
	public boolean playAnimation(String name) {
		int index = animations.indexOf(name);
		if (index < 0) {
			return false;
		}
		entityData.set(DATA_ANIM, index);
		entityData.set(DATA_ANIM_SEQ, entityData.get(DATA_ANIM_SEQ) + 1);
		return true;
	}

	public List<String> animationNames() {
		return animations.names();
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (key.equals(DATA_ANIM_SEQ) && level().isClientSide() && entityData.get(DATA_ANIM_SEQ) > 0) {
			animations.start(entityData.get(DATA_ANIM), tickCount, 0);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level() instanceof ServerLevel level && isAlive()) {
			ensureParts(level);
		}
		if (level().isClientSide()) {
			animations.state("idle").startIfStopped(tickCount);
			animations.state("swim").animateWhen(getDeltaMovement().lengthSqr() > 2.5E-3, tickCount);
		}
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		setNoGravity(isInWater());
		if (isVehicle() && getControllingPassenger() != null) {
			return;
		}
		KrakenEntity kraken = nearestKraken();
		if (kraken != null) {
			fleeTicks = 60;
			Vec3 away = position().subtract(kraken.position()).multiply(1, 0.2, 1);
			steer(away, 0.22, 6.0F);
			return;
		}
		if (fleeTicks > 0) {
			fleeTicks--;
		}
		serverBehavior(level);
	}

	// ---- Partes de colisão (6B1) ----

	private int species() {
		return this instanceof SpermWhaleEntity ? 1 : 0;
	}

	public List<WhalePartEntity> parts() {
		return parts;
	}

	private void ensureParts(ServerLevel level) {
		parts.removeIf(Entity::isRemoved);
		if (parts.size() == PARTS) {
			return;
		}
		parts.forEach(Entity::discard);
		parts.clear();
		for (int i = 0; i < PARTS; i++) {
			WhalePartEntity part = new WhalePartEntity(level, this, species() * PARTS + i);
			placePart(part);
			level.addFreshEntity(part);
			parts.add(part);
		}
	}

	void placePart(WhalePartEntity part) {
		float[] s = PART_SHAPES[part.shape()];
		Vec3 c = position().add(0, getBbHeight() * s[1], 0).add(Vec3.directionFromRotation(getXRot(), yBodyRot).scale(s[0]));
		part.setPos(c.x, c.y - part.getBbHeight() / 2.0, c.z);
	}

	/** Dano igual em todas as partes; quem está montado não fere a própria baleia mirando na cabeça à frente. */
	boolean hurtPart(ServerLevel level, DamageSource source, float damage) {
		if (source.getEntity() != null && hasPassenger(source.getEntity())) {
			return false;
		}
		return hurtServer(level, source, damage);
	}

	/** Cabeça sólida: quem ficou dentro dela (a baleia avançou sobre o jogador) é empurrado para fora. */
	void pushOutOfHead(ServerLevel level, WhalePartEntity head) {
		for (Player p : level.getEntitiesOfClass(Player.class, head.getBoundingBox(), p -> !p.isSpectator() && !hasPassenger(p))) {
			Vec3 away = p.position().subtract(head.position()).multiply(1, 0, 1);
			if (away.lengthSqr() < 1.0E-4) {
				away = Vec3.directionFromRotation(0, yBodyRot);
			}
			p.push(away.normalize().scale(0.25));
			p.needsSync = true;
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);
		parts.forEach(Entity::discard);
		parts.clear();
	}

	/** Comportamento de cada espécie quando não está fugindo nem sendo pilotada. */
	protected abstract void serverBehavior(ServerLevel level);

	/** O Kraken mais perto a menos de {@link #KRAKEN_FLEE_RANGE} blocos (ou {@code null}). */
	public KrakenEntity nearestKraken() {
		KrakenEntity best = null;
		double bestDist = KRAKEN_FLEE_RANGE * KRAKEN_FLEE_RANGE;
		for (KrakenEntity k : level().getEntitiesOfClass(KrakenEntity.class, getBoundingBox().inflate(KRAKEN_FLEE_RANGE + 16.0))) {
			double d = k.distanceToSqr(this);
			if (k.isAlive() && d < bestDist) {
				best = k;
				bestDist = d;
			}
		}
		return best;
	}

	/** Vira o corpo para a direção do movimento, devagar (bicho grande). */
	protected void face(Vec3 dir, float maxTurn) {
		if (dir.lengthSqr() < 1.0E-6) {
			return;
		}
		float yaw = (float) (Mth.atan2(dir.z, dir.x) * Mth.RAD_TO_DEG) - 90.0F;
		float pitch = (float) -(Mth.atan2(dir.y, dir.horizontalDistance()) * Mth.RAD_TO_DEG);
		setYRot(Mth.approachDegrees(getYRot(), yaw, maxTurn));
		setXRot(Mth.approach(getXRot(), Mth.clamp(pitch, -30.0F, 30.0F), maxTurn * 0.5F));
		yBodyRot = getYRot();
		yHeadRot = getYRot();
	}

	protected void steer(Vec3 to, double speed, float turn) {
		Vec3 dir = to.lengthSqr() > 1.0E-4 ? to.normalize() : Vec3.ZERO;
		if (!isInWater()) {
			dir = new Vec3(dir.x, Math.min(dir.y, 0.0), dir.z);
		}
		setDeltaMovement(getDeltaMovement().scale(0.8).add(dir.scale(speed * 0.2)));
		face(dir, turn);
	}

	/** Nada devagar entre pontos de água a até 20 blocos, sem subir à superfície. */
	protected void wander(ServerLevel level, double speed) {
		if (wanderTarget == null || position().distanceToSqr(wanderTarget) < 9.0 || random.nextInt(300) == 0) {
			BlockPos pos = blockPosition().offset(random.nextInt(41) - 20, random.nextInt(7) - 3, random.nextInt(41) - 20);
			boolean deep = level.getFluidState(pos).is(FluidTags.WATER) && level.getFluidState(pos.above(4)).is(FluidTags.WATER);
			wanderTarget = deep ? Vec3.atCenterOf(pos) : null;
		}
		if (wanderTarget != null) {
			steer(wanderTarget.subtract(position()), speed, 2.5F);
		} else {
			setDeltaMovement(getDeltaMovement().scale(0.9));
		}
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}
}
