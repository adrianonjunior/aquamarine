package com.aquamarine.entity;

import java.util.List;
import com.aquamarine.entity.boss.AnimationSync;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/** Caranguejo de praia (7H): procura abrigo na areia; belisca apenas quando não consegue fugir. */
public final class CrabEntity extends PathfinderMob {
    public static final List<String> ANIMATIONS = List.of("idle_eyes", "walk_side", "claw_wave", "burrow", "emerge", "pinch", "flee", "sleep");
    public enum Activity { IDLE, WALK, WAVE, BURROW, HIDDEN, EMERGE, PINCH, FLEE, SLEEP }
    private static final EntityDataAccessor<Integer> ACTIVITY = SynchedEntityData.defineId(CrabEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SEQUENCE = SynchedEntityData.defineId(CrabEntity.class, EntityDataSerializers.INT);
    public static final int BURROW_TICKS = 24;
    public static final int EMERGE_TICKS = 24;
    public static final int FED_TRUST_TICKS = 1200;
    private final AnimationSync animations = new AnimationSync(ANIMATIONS);
    private int activityTicks;
    private int fedTicks;
    private int pinchCooldown;
    private int calmTicks;

    public CrabEntity(EntityType<? extends CrabEntity> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 8).add(Attributes.MOVEMENT_SPEED, .20)
            .add(Attributes.ATTACK_DAMAGE, 2).add(Attributes.STEP_HEIGHT, .6);
    }

    public static boolean checkSpawn(EntityType<CrabEntity> type, LevelAccessor world, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return world.getBiome(pos).is(BiomeTags.IS_BEACH) && world.getBlockState(pos.below()).is(BlockTags.SAND)
            && world.getFluidState(pos).isEmpty() && world.getRawBrightness(pos, 0) > 7
            && checkMobSpawnRules(type, world, reason, pos, random);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); builder.define(ACTIVITY, 0); builder.define(SEQUENCE, 0);
    }

    public Activity activity() { return Activity.values()[entityData.get(ACTIVITY)]; }
    public int activityTicks() { return activityTicks; }
    public int fedTicks() { return fedTicks; }
    public AnimationState animation(String name) { return animations.state(name); }

    private void changeActivity(Activity next) {
        if (activity() == next) return;
        entityData.set(ACTIVITY, next.ordinal());
        entityData.set(SEQUENCE, entityData.get(SEQUENCE) + 1);
        activityTicks = 0;
        if (next != Activity.WALK && next != Activity.FLEE) getNavigation().stop();
        setInvisible(next == Activity.HIDDEN);
    }

    private String clip() {
        return switch (activity()) {
            case WALK -> "walk_side"; case WAVE -> "claw_wave"; case BURROW, HIDDEN -> "burrow";
            case EMERGE -> "emerge"; case PINCH -> "pinch"; case FLEE -> "flee"; case SLEEP -> "sleep";
            default -> "idle_eyes";
        };
    }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(SEQUENCE) && level().isClientSide()) animations.start(animations.indexOf(clip()), tickCount, -1);
    }

    @Override public void tick() {
        super.tick();
        if (level().isClientSide()) animations.state(clip()).startIfStopped(tickCount);
        // A navegação aponta para o destino; só o casco gira 90 graus, dando o passo lateral real.
        if (activity() == Activity.WALK || activity() == Activity.FLEE) { yBodyRot = getYRot() + 90; yHeadRot = yBodyRot; }
    }

    private boolean sand() { return level().getBlockState(blockPosition().below()).is(BlockTags.SAND); }

    private Player threat() {
        if (fedTicks > 0) return null;
        return level().getNearestPlayer(getX(), getY(), getZ(), 6, e -> e instanceof Player p && p.isAlive() && !p.isSpectator() && !p.isCreative());
    }

    @Override protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        activityTicks++;
        if (fedTicks > 0) fedTicks--;
        if (pinchCooldown > 0) pinchCooldown--;
        Player player = threat();
        switch (activity()) {
            case BURROW -> {
                setDeltaMovement(0, getDeltaMovement().y, 0);
                if (!sand()) { changeActivity(Activity.EMERGE); return; }
                if (activityTicks % 4 == 0) sandParticles(level);
                if (activityTicks >= BURROW_TICKS) { calmTicks = 0; changeActivity(Activity.HIDDEN); }
                return;
            }
            case HIDDEN -> {
                setDeltaMovement(0, getDeltaMovement().y, 0);
                calmTicks = player == null ? calmTicks + 1 : 0;
                if (!sand() || calmTicks >= 60 || activityTicks >= 200) changeActivity(Activity.EMERGE);
                return;
            }
            case EMERGE -> {
                if (activityTicks % 6 == 0 && sand()) sandParticles(level);
                if (activityTicks >= EMERGE_TICKS) changeActivity(Activity.IDLE);
                return;
            }
            case PINCH -> {
                if (player != null && activityTicks == 8 && distanceToSqr(player) < 2.9 && hasLineOfSight(player))
                    player.hurtServer(level, damageSources().mobAttack(this), 2);
                if (activityTicks >= 16) { pinchCooldown = 40; changeActivity(Activity.FLEE); }
                return;
            }
            default -> { }
        }
        if (player != null) {
            if (activity() != Activity.FLEE) changeActivity(Activity.FLEE);
            if (sand() && (activityTicks >= 20 || distanceToSqr(player) < 4)) { changeActivity(Activity.BURROW); return; }
            if (activityTicks % 10 == 1) {
                Vec3 away = position().subtract(player.position()).multiply(1, 0, 1).normalize();
                if (away.lengthSqr() < .01) away = new Vec3(1, 0, 0);
                Vec3 destination = position().add(away.scale(5));
                getNavigation().moveTo(destination.x, destination.y, destination.z, 1.45);
            }
            if (pinchCooldown == 0 && distanceToSqr(player) < 2.9 && (horizontalCollision || getNavigation().isDone()))
                changeActivity(Activity.PINCH);
            return;
        }
        if (activity() == Activity.FLEE) changeActivity(Activity.IDLE);
        if (activity() == Activity.WAVE && activityTicks < 35) return;
        boolean day = level.isBrightOutside();
        if (day && fedTicks == 0 && (activity() == Activity.SLEEP || random.nextInt(160) == 0)) {
            changeActivity(Activity.SLEEP); return;
        }
        if (activity() == Activity.SLEEP && !day) changeActivity(Activity.IDLE);
        if (getNavigation().isDone()) {
            changeActivity(Activity.IDLE);
            if (random.nextInt(day ? 100 : 35) == 0) {
                BlockPos dest = blockPosition().offset(random.nextInt(9) - 4, 0, random.nextInt(9) - 4);
                if (level.getBlockState(dest.below()).isSolid() && level.getFluidState(dest).isEmpty()
                    && getNavigation().moveTo(dest.getX() + .5, dest.getY(), dest.getZ() + .5, day ? .65 : 1.0)) changeActivity(Activity.WALK);
            } else if (random.nextInt(180) == 0) changeActivity(Activity.WAVE);
        }
    }

    private void sandParticles(ServerLevel level) {
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPosition().below())),
            getX(), getY() + .05, getZ(), 4, .24, .05, .24, .025);
    }

    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack food = player.getItemInHand(hand);
        if ((food.is(Items.COD) || food.is(Items.SALMON) || food.is(Items.TROPICAL_FISH)) && activity() != Activity.HIDDEN) {
            if (!level().isClientSide() && fedTicks < FED_TRUST_TICKS - 100) {
                if (!player.isCreative()) food.shrink(1);
                fedTicks = FED_TRUST_TICKS; heal(2); setPersistenceRequired(); changeActivity(Activity.WAVE);
                ((ServerLevel) level()).sendParticles(ParticleTypes.HEART, getX(), getY() + .5, getZ(), 3, .2, .1, .2, 0);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(level, source, damage);
        if (hurt) { fedTicks = 0; if (activity() == Activity.HIDDEN || activity() == Activity.BURROW) changeActivity(Activity.EMERGE); }
        return hurt;
    }

    @Override protected void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("CrabActivity", activity().ordinal()); out.putInt("CrabActivityTicks", activityTicks);
        out.putInt("CrabFedTicks", fedTicks); out.putInt("CrabPinchCooldown", pinchCooldown);
    }

    @Override protected void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        int saved = Math.clamp(in.getIntOr("CrabActivity", 0), 0, Activity.values().length - 1);
        entityData.set(ACTIVITY, saved); entityData.set(SEQUENCE, entityData.get(SEQUENCE) + 1);
        activityTicks = Math.clamp(in.getIntOr("CrabActivityTicks", 0), 0, 200);
        fedTicks = Math.clamp(in.getIntOr("CrabFedTicks", 0), 0, FED_TRUST_TICKS);
        pinchCooldown = Math.clamp(in.getIntOr("CrabPinchCooldown", 0), 0, 40);
        setInvisible(activity() == Activity.HIDDEN);
    }
}
