package com.aquamarine.entity;

import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.entity.boss.AggroTracker;
import com.aquamarine.entity.boss.AquamarineBossBar;
import com.aquamarine.entity.boss.KrakenStats;

import java.util.List;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * O Kraken (docs/02-kraken.md). Esta classe cobre a lógica de luta da faixa 2A-C: vida e
 * escudo por fase, escala {@code f} por jogadores, transição de fase, aggro, fuga e abandono.
 * Ataques, tentáculos, Pressão e a tabela de dano por fonte vêm nas faixas 2B, 2C e 2D.
 * Tudo roda no servidor; o cliente recebe fase e escudo por {@link SynchedEntityData} e pela
 * barra ({@link AquamarineBossBar}).
 */
public class KrakenEntity extends Mob {
	/** Vida máxima do atributo do jogo (a escala interna; a vida real é {@link #realHealth()}). */
	public static final float HEALTH_UNITS = 1000.0F;
	public static final int ARENA_RADIUS = 32;
	public static final int MESSAGE_RADIUS = 128;
	/** Invulnerabilidade na troca de fase (3 s). */
	public static final int TRANSITION_TICKS = 60;
	/** Atordoamento ao quebrar o escudo (8 s). */
	public static final int STUN_TICKS = 160;
	/** 20 s sem dano ao escudo antes de ele começar a regenerar. */
	public static final int SHIELD_IDLE_TICKS = 400;
	/** Regeneração: 10% do máximo por segundo. */
	public static final float SHIELD_REGEN_PER_TICK = 0.10F / 20.0F;
	/** Na fase 3 o escudo também volta sozinho a cada 45 s (30 s no modo abissal). */
	public static final int PHASE3_SHIELD_TICKS = 45 * 20;
	public static final int PHASE3_SHIELD_TICKS_ABYSSAL = 30 * 20;
	/** Sem jogador na arena: mergulha e cura 10% após 30 s; abandona após 3 min. */
	public static final int FLEE_TICKS = 30 * 20;
	public static final int ABANDON_TICKS = 3 * 60 * 20;

	private static final EntityDataAccessor<Float> DATA_SHIELD = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_MAX_SHIELD = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_TRANSITION = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_STUN = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.INT);

	/** O atributo de vida máxima do jogo é limitado a 1024; a vida real do Kraken passa disso e fica aqui. */
	private static final EntityDataAccessor<Float> DATA_MAX_HEALTH = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.FLOAT);

	private final AquamarineBossBar bossBar = new AquamarineBossBar(this.getDisplayName());
	private final AggroTracker aggro = new AggroTracker();
	private float scale = 1.0F;
	private boolean initialized;
	private int sinceShieldDamage;
	private int sinceShieldRefill;
	private boolean shieldRegenerating;
	private int absentTicks;
	private boolean fled;
	private UUID target;

	public KrakenEntity(EntityType<? extends KrakenEntity> type, Level level) {
		super(type, level);
		// Sem setNoAi: com ele o jogo não chama customServerAiStep. Sem goals ele não se move.
		this.setNoGravity(true);
		this.setPersistenceRequired();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, HEALTH_UNITS)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_MAX_HEALTH, KrakenStats.maxHealth(1.0F, "normal"));
		builder.define(DATA_SHIELD, 0.0F);
		builder.define(DATA_MAX_SHIELD, 0.0F);
		builder.define(DATA_PHASE, 1);
		builder.define(DATA_TRANSITION, 0);
		builder.define(DATA_STUN, 0);
	}

	/** Vida máxima de verdade (já com a escala {@code f}); o atributo do jogo é só a régua de 0 a {@link #HEALTH_UNITS}. */
	public float realMaxHealth() {
		return entityData.get(DATA_MAX_HEALTH);
	}

	/** Vida de verdade, em pontos de vida do Kraken. */
	public float realHealth() {
		return getHealth() / getMaxHealth() * realMaxHealth();
	}

	private void setRealHealth(float real) {
		setHealth(Math.max(0.0F, real) / realMaxHealth() * getMaxHealth());
	}

	// --- Estado visível (o KrakenView do Codex lê daqui) ---

	public float shield() {
		return entityData.get(DATA_SHIELD);
	}

	public float maxShield() {
		return entityData.get(DATA_MAX_SHIELD);
	}

	public int phase() {
		return entityData.get(DATA_PHASE);
	}

	public float shieldFraction() {
		float max = maxShield();
		return max > 0 ? shield() / max : 0.0F;
	}

	public boolean isTransitioning() {
		return entityData.get(DATA_TRANSITION) > 0;
	}

	public boolean isStunned() {
		return entityData.get(DATA_STUN) > 0;
	}

	/** Fator de escala {@code f} em vigor (só diminui entre fases). */
	public float scaleFactor() {
		return scale;
	}

	/** Alvo atual pelo aggro (mais dano ao escudo nos últimos 10 s), ou {@code null}. */
	public UUID aggroTarget() {
		return target;
	}

	/**
	 * Define a escala por jogadores e enche vida e escudo da fase 1. Chamado pelo surgimento;
	 * sem isso o primeiro tick usa os jogadores da arena.
	 */
	public void setUp(ServerLevel level, int players) {
		this.scale = KrakenStats.scale(players, AquamarineConfig.get().scalingCap);
		applyMaxHealth(true);
		setShieldFull(1);
		entityData.set(DATA_PHASE, 1);
		this.initialized = true;
	}

	private String difficulty() {
		return AquamarineConfig.get().difficulty;
	}

	/** Recalcula a vida máxima por {@code scale}; {@code fill} enche, senão mantém a fração. */
	private void applyMaxHealth(boolean fill) {
		float fraction = fill ? 1.0F : getHealth() / getMaxHealth();
		entityData.set(DATA_MAX_HEALTH, KrakenStats.maxHealth(scale, difficulty()));
		setHealth(getMaxHealth() * fraction);
	}

	private void setShieldFull(int phase) {
		float max = KrakenStats.maxShield(phase, scale, difficulty());
		entityData.set(DATA_MAX_SHIELD, max);
		entityData.set(DATA_SHIELD, max);
		sinceShieldDamage = 0;
		sinceShieldRefill = 0;
		shieldRegenerating = false;
	}

	/** Jogadores vivos (não espectadores) a até {@link #ARENA_RADIUS} blocos. */
	public List<ServerPlayer> playersInArena(ServerLevel level) {
		return level.getEntitiesOfClass(ServerPlayer.class, getBoundingBox().inflate(ARENA_RADIUS),
			p -> p.isAlive() && !p.isSpectator() && p.distanceToSqr(this) <= (double) ARENA_RADIUS * ARENA_RADIUS);
	}

	// --- Dano ---

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return super.hurtServer(level, source, damage);
		}
		if (isTransitioning() || damage <= 0) {
			return false;
		}
		Entity attacker = source.getEntity();
		float shield = shield();
		if (shield > 0) {
			float absorbed = Math.min(shield, damage);
			recordAggro(attacker, absorbed, level);
			entityData.set(DATA_SHIELD, shield - absorbed);
			sinceShieldDamage = 0;
			shieldRegenerating = false;
			if (shield - absorbed <= 0) {
				breakShield(level);
			}
			return true;
		}
		recordAggro(attacker, damage, level);
		int phase = phase();
		float unit = getMaxHealth() / realMaxHealth();
		float floor = realMaxHealth() * KrakenStats.phaseFloorFraction(phase);
		float allowed = realHealth() - floor;
		if (phase < KrakenStats.PHASES && damage >= allowed) {
			// O golpe que cruza o limite para na fronteira da fase; o excesso se perde.
			setRealHealth(floor);
			startPhase(level, phase + 1);
			return true;
		}
		return super.hurtServer(level, source, damage * unit);
	}

	private void recordAggro(Entity attacker, float amount, ServerLevel level) {
		if (attacker instanceof Player player) {
			aggro.record(player.getUUID(), amount, level.getGameTime());
		}
	}

	private void breakShield(ServerLevel level) {
		entityData.set(DATA_STUN, STUN_TICKS);
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 3.0F, 0.5F);
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 2.0F, 0.6F);
		broadcast(level, Component.translatable("message.aquamarine.kraken.shield_broken").withStyle(ChatFormatting.AQUA));
	}

	private void startPhase(ServerLevel level, int phase) {
		// f só diminui entre fases (jogadores que saem); nunca sobe no meio da luta.
		int n = playersInArena(level).size();
		scale = Math.min(scale, KrakenStats.scale(n, AquamarineConfig.get().scalingCap));
		applyMaxHealth(false);
		entityData.set(DATA_PHASE, phase);
		setShieldFull(phase);
		entityData.set(DATA_STUN, 0);
		entityData.set(DATA_TRANSITION, TRANSITION_TICKS);
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 4.0F, 0.5F);
		broadcast(level, Component.translatable("message.aquamarine.kraken.phase" + phase)
			.withStyle(phase == 3 ? ChatFormatting.DARK_PURPLE : ChatFormatting.BLUE, ChatFormatting.BOLD));
	}

	private void broadcast(ServerLevel level, Component message) {
		for (ServerPlayer player : level.players()) {
			if (player.distanceToSqr(this) <= (double) MESSAGE_RADIUS * MESSAGE_RADIUS) {
				player.sendSystemMessage(message);
			}
		}
	}

	// --- Tick do servidor ---

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		List<ServerPlayer> inArena = playersInArena(level);
		if (!initialized) {
			setUp(level, inArena.size());
		}
		int phase = phase();
		tickCounters();
		tickShield(phase);
		target = aggro.top(level.getGameTime());
		faceTarget(level, inArena);
		tickAbsence(level, inArena);
		if (isAlive()) {
			bossBar.update(realHealth(), realMaxHealth(), shield(), maxShield(), phase, shieldRegenerating);
		}
	}

	private void tickCounters() {
		int transition = entityData.get(DATA_TRANSITION);
		if (transition > 0) {
			entityData.set(DATA_TRANSITION, transition - 1);
		}
		int stun = entityData.get(DATA_STUN);
		if (stun > 0) {
			entityData.set(DATA_STUN, stun - 1);
		}
	}

	private void tickShield(int phase) {
		float shield = shield();
		float max = maxShield();
		if (shield >= max) {
			sinceShieldDamage = 0;
			sinceShieldRefill = 0;
			shieldRegenerating = false;
			return;
		}
		if (isTransitioning() || isStunned()) {
			return;
		}
		sinceShieldDamage++;
		if (sinceShieldDamage >= SHIELD_IDLE_TICKS) {
			shieldRegenerating = true;
			entityData.set(DATA_SHIELD, Math.min(max, shield + max * SHIELD_REGEN_PER_TICK));
		}
		if (phase == 3) {
			sinceShieldRefill++;
			int period = "abissal".equals(difficulty()) ? PHASE3_SHIELD_TICKS_ABYSSAL : PHASE3_SHIELD_TICKS;
			if (sinceShieldRefill >= period) {
				entityData.set(DATA_SHIELD, max);
				sinceShieldRefill = 0;
			}
		}
	}

	private void faceTarget(ServerLevel level, List<ServerPlayer> inArena) {
		Player who = target == null ? null : level.getPlayerByUUID(target);
		if (who == null && !inArena.isEmpty()) {
			who = inArena.getFirst();
		}
		if (who != null) {
			getLookControl().setLookAt(who, 30.0F, 30.0F);
		}
	}

	private void tickAbsence(ServerLevel level, List<ServerPlayer> inArena) {
		if (!inArena.isEmpty()) {
			absentTicks = 0;
			fled = false;
			return;
		}
		absentTicks++;
		if (absentTicks == FLEE_TICKS && !fled) {
			fled = true;
			setHealth(Math.min(getMaxHealth(), getHealth() + getMaxHealth() * 0.10F));
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.ELDER_GUARDIAN_AMBIENT, SoundSource.HOSTILE, 3.0F, 0.4F);
		}
		if (absentTicks >= ABANDON_TICKS) {
			broadcast(level, Component.translatable("message.aquamarine.kraken.retreats").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			discard();
		}
	}

	/** O Kraken recuou: ticks seguidos sem jogador na arena (para testes e ataques). */
	public int absentTicks() {
		return absentTicks;
	}

	// --- Boilerplate ---

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		bossBar.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		bossBar.removePlayer(player);
	}

	@Override
	public void remove(RemovalReason reason) {
		bossBar.removeAllPlayers();
		super.remove(reason);
	}

	@Override
	public void setCustomName(Component name) {
		super.setCustomName(name);
		bossBar.setName(getDisplayName());
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putFloat("KrakenMaxHealth", realMaxHealth());
		output.putFloat("Shield", shield());
		output.putFloat("MaxShield", maxShield());
		output.putInt("Phase", phase());
		output.putFloat("Scale", scale);
		output.putInt("AbsentTicks", absentTicks);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		entityData.set(DATA_MAX_HEALTH, input.getFloatOr("KrakenMaxHealth", KrakenStats.maxHealth(1.0F, "normal")));
		super.readAdditionalSaveData(input);
		scale = input.getFloatOr("Scale", 1.0F);
		entityData.set(DATA_PHASE, input.getIntOr("Phase", 1));
		entityData.set(DATA_MAX_SHIELD, input.getFloatOr("MaxShield", 0.0F));
		entityData.set(DATA_SHIELD, input.getFloatOr("Shield", 0.0F));
		absentTicks = input.getIntOr("AbsentTicks", 0);
		initialized = maxShield() > 0;
		bossBar.setName(getDisplayName());
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}
}
