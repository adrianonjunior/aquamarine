package com.aquamarine.entity;

import com.aquamarine.Aquamarine;
import com.aquamarine.particle.ModParticles;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Pulso de cavitação do Cavitador de Neutrônio (docs/10-fase4.md, §9): uma bolha que corre rápido pela água,
 * atravessa até {@link #MAX_PIERCE} criaturas e implode no fim com um anel de choque. Fora d'água ela se desfaz na
 * hora. Não tem modelo: o rastro de bolhas e centelhas é o que se vê.
 */
public class CavitationPulseEntity extends Projectile {
	public static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Aquamarine.id("cavitation"));
	public static final float FULL_DAMAGE = 10.0F;
	public static final float QUICK_DAMAGE = 5.0F;
	public static final float IMPLOSION_DAMAGE = 4.0F;
	public static final double SPEED = 1.6;
	public static final double RANGE = 32.0;
	public static final double IMPLOSION_RADIUS = 2.0;
	public static final int MAX_PIERCE = 3;
	private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(CavitationPulseEntity.class, EntityDataSerializers.BOOLEAN);
	private final IntSet struck = new IntOpenHashSet();
	private ItemStack weapon = ItemStack.EMPTY;
	private double travelled;

	public CavitationPulseEntity(EntityType<? extends CavitationPulseEntity> type, Level level) {
		super(type, level);
		setNoGravity(true);
	}

	/** Dispara da boca do cano na direção do olhar. */
	public static CavitationPulseEntity shoot(ServerLevel level, LivingEntity owner, boolean charged, ItemStack weapon) {
		CavitationPulseEntity pulse = new CavitationPulseEntity(ModEntities.CAVITATION_PULSE, level);
		Vec3 look = owner.getLookAngle();
		pulse.setOwner(owner);
		pulse.weapon = weapon.copy();
		pulse.entityData.set(CHARGED, charged);
		pulse.setPos(owner.getEyePosition().add(look.scale(0.9)).add(0, -0.2, 0));
		pulse.setDeltaMovement(look.scale(SPEED));
		level.addFreshEntity(pulse);
		Vec3 muzzle = pulse.position();
		level.sendParticles(ModParticles.NEUTRON_SPARK, muzzle.x, muzzle.y, muzzle.z, charged ? 10 : 5, 0.12, 0.12, 0.12, 0.08);
		level.sendParticles(ModParticles.CAVITATION_BUBBLE, muzzle.x, muzzle.y, muzzle.z, 8, 0.15, 0.15, 0.15, 0.05);
		level.playSound(null, muzzle.x, muzzle.y, muzzle.z, SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.PLAYERS, 1.0F, charged ? 1.3F : 1.7F);
		return pulse;
	}

	public boolean charged() {
		return entityData.get(CHARGED);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(CHARGED, false);
	}

	@Override
	public ItemStack getWeaponItem() {
		return weapon;
	}

	@Override
	public void tick() {
		super.tick();
		Vec3 start = position();
		Vec3 motion = getDeltaMovement();
		if (!isInWater()) {
			if (level() instanceof ServerLevel server) {
				dissipate(server, start);
			}
			return;
		}
		if (level().isClientSide()) {
			trail(start, motion);
			setPos(start.add(motion));
			return;
		}
		ServerLevel server = (ServerLevel) level();
		Vec3 end = start.add(motion);
		BlockHitResult block = server.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (block.getType() != HitResult.Type.MISS) {
			end = block.getLocation();
		}
		Vec3 from = start, to = end;
		List<Entity> targets = server.getEntities(this, getBoundingBox().expandTowards(motion).inflate(0.6),
			e -> e instanceof LivingEntity && canHitEntity(e) && !struck.contains(e.getId())
				&& e.getBoundingBox().inflate(0.35).clip(from, to).isPresent());
		targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(from)));
		for (Entity target : targets) {
			struck.add(target.getId());
			target.hurtServer(server, damage(server), charged() ? FULL_DAMAGE : QUICK_DAMAGE);
			server.sendParticles(ModParticles.NEUTRON_SPARK, target.getX(), target.getY(0.5), target.getZ(), 6, 0.2, 0.3, 0.2, 0.1);
			if (struck.size() >= MAX_PIERCE) {
				implode(server, target.position().add(0, target.getBbHeight() * 0.5, 0));
				return;
			}
		}
		if (block.getType() != HitResult.Type.MISS) {
			implode(server, block.getLocation());
			return;
		}
		setPos(end);
		travelled += SPEED;
		if (travelled >= RANGE) {
			implode(server, end);
		}
	}

	private DamageSource damage(ServerLevel level) {
		return level.damageSources().source(DAMAGE, this, getOwner());
	}

	/** A bolha colapsa: dano em volta, empurrão para fora, anel de choque e nuvem de bolhas. */
	private void implode(ServerLevel level, Vec3 at) {
		float scale = charged() ? 1.0F : 0.5F;
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(IMPLOSION_RADIUS + 1.0),
			e -> e.isAlive() && e != getOwner() && e.distanceToSqr(at) <= (IMPLOSION_RADIUS + 0.5) * (IMPLOSION_RADIUS + 0.5))) {
			e.hurtServer(level, damage(level), IMPLOSION_DAMAGE * scale);
			Vec3 push = e.position().subtract(at).normalize().scale(0.6 * scale);
			e.push(push.x, push.y * 0.5 + 0.15, push.z);
			if (e instanceof ServerPlayer player) {  // o jogador se move no próprio cliente: o empurrão vai por pacote
				player.connection.send(new ClientboundSetEntityMotionPacket(player));
			}
		}
		level.sendParticles(ModParticles.SHOCK_RING, at.x, at.y, at.z, 1, 0, 0, 0, 0);
		level.sendParticles(ModParticles.CAVITATION_BUBBLE, at.x, at.y, at.z, charged() ? 40 : 20, 0.7, 0.7, 0.7, 0.12);
		level.sendParticles(ModParticles.NEUTRON_SPARK, at.x, at.y, at.z, charged() ? 26 : 12, 0.5, 0.5, 0.5, 0.22);
		level.sendParticles(ParticleTypes.BUBBLE_POP, at.x, at.y, at.z, 20, 0.8, 0.8, 0.8, 0.05);
		level.playSound(null, at.x, at.y, at.z, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundSource.PLAYERS, 1.2F, 1.8F);
		level.playSound(null, at.x, at.y, at.z, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.PLAYERS, 0.8F, 2.0F);
		discard();
	}

	private void dissipate(ServerLevel level, Vec3 at) {
		level.sendParticles(ParticleTypes.CLOUD, at.x, at.y, at.z, 4, 0.1, 0.1, 0.1, 0.02);
		level.sendParticles(ModParticles.CAVITATION_BUBBLE, at.x, at.y, at.z, 4, 0.1, 0.1, 0.1, 0.02);
		discard();
	}

	/** Rastro no cliente: bolhas em toda a linha percorrida e a cabeça acesa. */
	private void trail(Vec3 start, Vec3 motion) {
		Level level = level();
		for (int i = 0; i < 4; i++) {
			Vec3 p = start.add(motion.scale(i / 4.0));
			level.addParticle(ModParticles.CAVITATION_BUBBLE, p.x, p.y, p.z, (random.nextDouble() - 0.5) * 0.04, 0.01,
				(random.nextDouble() - 0.5) * 0.04);
			if (i % 2 == 0) {
				level.addParticle(ParticleTypes.BUBBLE, p.x, p.y, p.z, 0, 0.02, 0);
			}
		}
		level.addParticle(ModParticles.NEUTRON_SPARK, start.x, start.y, start.z, 0, 0, 0);
		if (charged()) {
			level.addParticle(ModParticles.NEUTRON_SPARK, start.x + motion.x * 0.5, start.y + motion.y * 0.5, start.z + motion.z * 0.5, 0, 0, 0);
		}
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putDouble("Travelled", travelled);
		output.putBoolean("Charged", charged());
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		travelled = input.getDoubleOr("Travelled", 0.0);
		entityData.set(CHARGED, input.getBooleanOr("Charged", false));
	}
}
