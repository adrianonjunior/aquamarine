package com.aquamarine.entity;

import com.aquamarine.Aquamarine;
import com.aquamarine.ModTags;
import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.entity.boss.AggroTracker;
import com.aquamarine.entity.boss.AquamarineBossBar;
import com.aquamarine.entity.boss.KrakenAttacks;
import com.aquamarine.entity.boss.KrakenMovement;
import com.aquamarine.entity.boss.KrakenRewards;
import com.aquamarine.entity.boss.KrakenStats;
import com.aquamarine.entity.boss.KrakenView;
import com.aquamarine.entity.boss.KrakenWorldEffects;
import com.aquamarine.entity.boss.ShieldDamage;
import com.aquamarine.entity.boss.TideNodes;
import com.aquamarine.sound.ModSounds;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
public class KrakenEntity extends Mob implements KrakenView {
	/** Vida máxima do atributo do jogo (a escala interna; a vida real é {@link #realHealth()}). */
	public static final float HEALTH_UNITS = 1000.0F;
	public static final int ARENA_RADIUS = 48;
	/** Alcance (em blocos) do olhar do atacante ao testar acerto num Nó de Maré. */
	public static final double NODE_REACH = 8.0;
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
	/** Quantos Nós de Maré existem (0 com a opção desligada); o cliente os posiciona por fase e rotação. */
	private static final EntityDataAccessor<Integer> DATA_NODES = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_STUN = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.INT);

	/** O atributo de vida máxima do jogo é limitado a 1024; a vida real do Kraken passa disso e fica aqui. */
	private static final EntityDataAccessor<Float> DATA_MAX_HEALTH = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.FLOAT);

	private final AnimationState idleAnimation = new AnimationState();
	private final AnimationState swimAnimation = new AnimationState();
	private final AnimationState emergeAnimation = new AnimationState();
	private final AnimationState stunnedAnimation = new AnimationState();
	private final AnimationState phaseChangeAnimation = new AnimationState();
	private final AnimationState deathAnimation = new AnimationState();
	private final AnimationState slamAnimation = new AnimationState();
	private final AnimationState grabAnimation = new AnimationState();
	private final AnimationState inkAnimation = new AnimationState();
	private final AnimationState whirlpoolAnimation = new AnimationState();
	private final AnimationState spawnYoungAnimation = new AnimationState();
	private final AnimationState waterJetAnimation = new AnimationState();
	private final AnimationState roarAnimation = new AnimationState();
	private final AnimationState boltsAnimation = new AnimationState();
	private final AnimationState ambushAnimation = new AnimationState();
	private final AnimationState doubleGrabAnimation = new AnimationState();
	/** Nomes das animações do contrato (docs/06-tecnico.md), na ordem de {@link #animationStates()}. */
	public static final List<String> ANIMATION_NAMES = List.of("idle", "swim", "emerge", "stunned", "phaseChange", "death",
		"slam", "grab", "ink", "whirlpool", "spawnYoung", "waterJet", "roar", "bolts", "ambush", "doubleGrab");
	private static final EntityDataAccessor<Integer> DATA_ANIM = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ANIM_SEQ = SynchedEntityData.defineId(KrakenEntity.class, EntityDataSerializers.INT);
	/** Cliente: estado do tick anterior, para ligar e desligar stunned/phaseChange só na mudança. */
	private boolean wasStunned;
	private boolean wasTransitioning;

	private final AquamarineBossBar bossBar = new AquamarineBossBar(this.getDisplayName());
	private final KrakenWorldEffects worldEffects = new KrakenWorldEffects();
	private final AggroTracker aggro = new AggroTracker();
	/** Tentáculos e ataques (docs/02-kraken.md); a IA roda no servidor. */
	private final KrakenAttacks attacks = new KrakenAttacks(this);
	/** Deslocamento pela arena (servidor). */
	private final KrakenMovement movement = new KrakenMovement();
	private Vec3 lastPosition;
	/** Quem causou dano nesta luta (recebe as recompensas se estiver por perto na vitória). */
	private final Set<UUID> damagers = new HashSet<>();
	private float scale = 1.0F;
	private boolean initialized;
	private int sinceShieldDamage;
	private int sinceShieldRefill;
	private boolean shieldRegenerating;
	private int absentTicks;
	private boolean fled;
	private UUID target;
	/** Ticks restantes com os 8 tentáculos cortados: escudo sem regenerar e +25% de dano (2D). */
	private int exposedTicks;

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
		builder.define(DATA_ANIM, 0);
		builder.define(DATA_ANIM_SEQ, 0);
		builder.define(DATA_SHIELD, 0.0F);
		builder.define(DATA_MAX_SHIELD, 0.0F);
		builder.define(DATA_PHASE, 1);
		builder.define(DATA_TRANSITION, 0);
		builder.define(DATA_STUN, 0);
		builder.define(DATA_NODES, 0);
	}

	// --- KrakenView: o cliente liga e desliga as animações conforme o estado sincronizado ---

	@Override
	public AnimationState idleAnimation() {
		return idleAnimation;
	}

	@Override
	public AnimationState swimAnimation() {
		return swimAnimation;
	}

	@Override
	public AnimationState emergeAnimation() {
		return emergeAnimation;
	}

	@Override
	public AnimationState stunnedAnimation() {
		return stunnedAnimation;
	}

	@Override
	public AnimationState phaseChangeAnimation() {
		return phaseChangeAnimation;
	}

	@Override
	public AnimationState deathAnimation() {
		return deathAnimation;
	}

	@Override
	public AnimationState slamAnimation() {
		return slamAnimation;
	}

	@Override
	public AnimationState grabAnimation() {
		return grabAnimation;
	}

	@Override
	public AnimationState inkAnimation() {
		return inkAnimation;
	}

	@Override
	public AnimationState whirlpoolAnimation() {
		return whirlpoolAnimation;
	}

	@Override
	public AnimationState spawnYoungAnimation() {
		return spawnYoungAnimation;
	}

	@Override
	public AnimationState waterJetAnimation() {
		return waterJetAnimation;
	}

	@Override
	public AnimationState roarAnimation() {
		return roarAnimation;
	}

	@Override
	public AnimationState boltsAnimation() {
		return boltsAnimation;
	}

	@Override
	public AnimationState ambushAnimation() {
		return ambushAnimation;
	}

	@Override
	public AnimationState doubleGrabAnimation() {
		return doubleGrabAnimation;
	}

	private AnimationState[] animationStates() {
		return new AnimationState[] {idleAnimation, swimAnimation, emergeAnimation, stunnedAnimation, phaseChangeAnimation, deathAnimation,
			slamAnimation, grabAnimation, inkAnimation, whirlpoolAnimation, spawnYoungAnimation, waterJetAnimation, roarAnimation,
			boltsAnimation, ambushAnimation, doubleGrabAnimation};
	}

	/** Dispara uma animação pelo nome do contrato (servidor; o cliente recebe por dados sincronizados). */
	public boolean playAnimation(String name) {
		int index = ANIMATION_NAMES.indexOf(name);
		if (index < 0) {
			return false;
		}
		entityData.set(DATA_ANIM, index);
		entityData.set(DATA_ANIM_SEQ, entityData.get(DATA_ANIM_SEQ) + 1);
		return true;
	}

	/** Índice da última animação disparada por {@link #playAnimation}. */
	public int lastAnimation() {
		return entityData.get(DATA_ANIM);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (key.equals(DATA_ANIM_SEQ) && level().isClientSide() && entityData.get(DATA_ANIM_SEQ) > 0) {
			AnimationState[] states = animationStates();
			int index = entityData.get(DATA_ANIM);
			for (int i = 1; i < states.length; i++) {
				if (i != index) {
					states[i].stop();
				}
			}
			states[index].start(tickCount);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			idleAnimation.startIfStopped(tickCount);
			// Só reage quando o estado muda: assim `kraken anim stunned/phaseChange` não é cortado no tick seguinte.
			if (isStunned() != wasStunned) {
				wasStunned = isStunned();
				stunnedAnimation.animateWhen(wasStunned, tickCount);
			}
			if (isTransitioning() != wasTransitioning) {
				wasTransitioning = isTransitioning();
				phaseChangeAnimation.animateWhen(wasTransitioning, tickCount);
			}
			if (isDeadOrDying()) {
				deathAnimation.startIfStopped(tickCount);
			}
		}
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

	/** Atordoado ou em transição de fase: não ataca nem muda de posição (as faixas de ataque consultam isto). */
	public boolean canAct() {
		return !isStunned() && !isTransitioning();
	}

	/** Tentáculos e ataques do Kraken (comandos e testes). */
	public KrakenAttacks attacks() {
		return attacks;
	}

	public boolean isExposed() {
		return exposedTicks > 0;
	}

	/** Deixa o Kraken exposto por {@code ticks} (todos os tentáculos cortados). */
	public void expose(int ticks) {
		exposedTicks = Math.max(exposedTicks, ticks);
	}

	public int nodeCount() {
		return entityData.get(DATA_NODES);
	}

	/** Centros dos Nós de Maré no mundo (a fase e a rotação do corpo definem onde estão). */
	public List<Vec3> nodeCenters() {
		int count = nodeCount();
		List<Vec3> centers = new java.util.ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			TideNodes.Offset o = TideNodes.rotate(TideNodes.offset(phase(), i, count), getYRot());
			centers.add(position().add(o.x() * TideNodes.WORLD_SCALE, o.y() * TideNodes.WORLD_SCALE - KrakenStats.MODEL_DROP,
				o.z() * TideNodes.WORLD_SCALE));
		}
		return centers;
	}

	private void setNodes(int players) {
		entityData.set(DATA_NODES, AquamarineConfig.get().weakPoints ? TideNodes.count(players) : 0);
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
		setNodes(players);
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
		if (shield() > 0) {
			ItemStack weapon = source.getWeaponItem();
			hitShield(level, classify(weapon), damage, attacker, isNodeHit(source), shatterLevel(level, weapon));
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

	/** Fonte do golpe pela arma usada (itens especiais entram por tag, ver docs/02-kraken.md). */
	static ShieldDamage.Source classify(ItemStack weapon) {
		if (weapon != null && weapon.is(ModTags.Items.HARPOONS)) {
			return ShieldDamage.Source.HARPOON;
		}
		if (weapon != null && weapon.is(ModTags.Items.SHIELD_BREAKERS)) {
			return ShieldDamage.Source.ORICHALCUM;
		}
		return ShieldDamage.Source.COMMON;
	}

	private static final ResourceKey<Enchantment> SHELL_BREAKER = ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("shell_breaker"));

	private static int shatterLevel(ServerLevel level, ItemStack weapon) {
		if (weapon == null || weapon.isEmpty()) {
			return 0;
		}
		return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(SHELL_BREAKER)
			.map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, weapon)).orElse(0);
	}

	/**
	 * O golpe acertou um Nó de Maré? Projétil: a caixa dele toca o nó. Corpo a corpo: o olhar do
	 * atacante atravessa a caixa do nó dentro do alcance.
	 */
	private boolean isNodeHit(DamageSource source) {
		if (nodeCount() == 0) {
			return false;
		}
		Entity direct = source.getDirectEntity();
		for (Vec3 center : nodeCenters()) {
			AABB box = AABB.ofSize(center, TideNodes.SIZE, TideNodes.SIZE, TideNodes.SIZE);
			if (direct instanceof Projectile) {
				if (direct.getBoundingBox().inflate(0.5).intersects(box)) {
					return true;
				}
			} else if (direct instanceof LivingEntity living) {
				Vec3 from = living.getEyePosition();
				if (box.clip(from, from.add(living.getLookAngle().scale(NODE_REACH))).isPresent()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Aplica dano ao escudo pela tabela ({@link ShieldDamage}) e trata a quebra. As fontes sem
	 * arma (Carga de Pulso, Pilar Ressonante) chamam isto direto. Devolve o que o escudo absorveu.
	 */
	public float hitShield(ServerLevel level, ShieldDamage.Source source, float damage, Entity attacker, boolean node, int shatterLevel) {
		float shield = shield();
		if (shield <= 0 || isTransitioning()) {
			return 0.0F;
		}
		float amount = ShieldDamage.amount(source, damage, maxShield(), shatterLevel, node, isExposed());
		float absorbed = Math.min(shield, amount);
		recordAggro(attacker, absorbed, level);
		entityData.set(DATA_SHIELD, shield - absorbed);
		sinceShieldDamage = 0;
		shieldRegenerating = false;
		if (shield - absorbed <= 0) {
			breakShield(level);
		}
		return absorbed;
	}

	private void recordAggro(Entity attacker, float amount, ServerLevel level) {
		if (attacker instanceof Player player) {
			aggro.record(player.getUUID(), amount, level.getGameTime());
			damagers.add(player.getUUID());
		}
	}

	private void breakShield(ServerLevel level) {
		entityData.set(DATA_STUN, STUN_TICKS);
		level.playSound(null, getX(), getY(), getZ(), ModSounds.KRAKEN_SHIELD_BREAK, SoundSource.HOSTILE, 5.0F, 1.0F);
		broadcast(level, Component.translatable("message.aquamarine.kraken.shield_broken").withStyle(ChatFormatting.AQUA));
	}

	private void startPhase(ServerLevel level, int phase) {
		// f só diminui entre fases (jogadores que saem); nunca sobe no meio da luta.
		int n = playersInArena(level).size();
		scale = Math.min(scale, KrakenStats.scale(n, AquamarineConfig.get().scalingCap));
		applyMaxHealth(false);
		setNodes(n);
		attacks.cancelAll(level);
		entityData.set(DATA_PHASE, phase);
		setShieldFull(phase);
		entityData.set(DATA_STUN, 0);
		entityData.set(DATA_TRANSITION, TRANSITION_TICKS);
		level.playSound(null, getX(), getY(), getZ(), ModSounds.KRAKEN_ROAR, SoundSource.HOSTILE, 6.0F, 0.85F);
		KrakenWorldEffects.shockwave(level, position());
		KrakenWorldEffects.lightning(level, position().add(0, getBbHeight() * 0.5, 0), true);
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

	// --- Vitória ---

	@Override
	public void die(DamageSource source) {
		if (level() instanceof ServerLevel level && !isRemoved() && !dead) {
			broadcast(level, Component.translatable("message.aquamarine.kraken.defeated").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
			KrakenRewards.grant(level, participants(level));
		}
		super.die(source);
	}

	/** Jogadores que recebem as recompensas: quem está na arena ou causou dano e está a até 128 blocos. */
	public List<ServerPlayer> participants(ServerLevel level) {
		Set<ServerPlayer> set = new java.util.LinkedHashSet<>(playersInArena(level));
		for (ServerPlayer player : level.players()) {
			if (damagers.contains(player.getUUID()) && !player.isSpectator() && player.distanceToSqr(this) <= (double) MESSAGE_RADIUS * MESSAGE_RADIUS) {
				set.add(player);
			}
		}
		return List.copyOf(set);
	}

	// --- Sons do corpo (docs/09-audio.md) ---

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSounds.KRAKEN_AMBIENT;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 220;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ModSounds.KRAKEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.KRAKEN_DEATH;
	}

	/** Bicho de 32 blocos: ouvido de longe (o alcance do som cresce com o volume). */
	@Override
	protected float getSoundVolume() {
		return 5.0F;
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
		attacks.tick(level);
		worldEffects.tick(this, level, phase, inArena);
		target = aggro.top(level.getGameTime());
		faceTarget(level, inArena);
		tickMovement(level, inArena, phase);
		tickAbsence(level, inArena);
		if (isAlive()) {
			bossBar.update(realHealth(), realMaxHealth(), shield(), maxShield(), phase, shieldRegenerating);
		}
	}

	/** Locomoção (KrakenMovement): só com a IA ligada, fora de atordoamento e transição. */
	private void tickMovement(ServerLevel level, List<ServerPlayer> inArena, int phase) {
		Player who = target == null ? null : level.getPlayerByUUID(target);
		if (who == null && !inArena.isEmpty()) {
			who = inArena.getFirst();
		}
		movement.tick(this, who, phase, isAlive() && canAct() && attacks.isEnabled(), attacks.activeCount() > 0, getRandom());
		if (lastPosition != null) {
			attacks.follow(level, position().subtract(lastPosition));
		}
		lastPosition = position();
	}

	private void tickCounters() {
		int transition = entityData.get(DATA_TRANSITION);
		if (transition > 0) {
			entityData.set(DATA_TRANSITION, transition - 1);
		}
		if (exposedTicks > 0) {
			exposedTicks--;
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
		if (isTransitioning() || isStunned() || isExposed()) {
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
			level.playSound(null, getX(), getY(), getZ(), ModSounds.KRAKEN_RETREAT, SoundSource.HOSTILE, 4.0F, 1.0F);
		}
		if (absentTicks >= ABANDON_TICKS) {
			broadcast(level, Component.translatable("message.aquamarine.kraken.retreats").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			level.playSound(null, getX(), getY(), getZ(), ModSounds.KRAKEN_RETREAT, SoundSource.HOSTILE, 6.0F, 0.8F);
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
		if (reason.shouldDestroy() && level() instanceof ServerLevel level) {
			attacks.cleanup(level);
			worldEffects.endStorm(level);
		}
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
		if (movement.home() != null) {
			output.store("Home", Vec3.CODEC, movement.home());
		}
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
		input.read("Home", Vec3.CODEC).ifPresent(movement::setHome);
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

	/** Com a hitbox de 32 x 20, empurrar jogadores para fora do corpo os jogaria para longe da luta. */
	@Override
	protected void doPush(net.minecraft.world.entity.Entity entity) {}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}
}
