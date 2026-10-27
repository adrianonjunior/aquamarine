package com.aquamarine.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/** Ostra passiva: assenta no fundo, recolhe-se em 0,3 s e reabre em 1,5 s. */
public final class OysterEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> CLOSED = SynchedEntityData.defineId(OysterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PEARL = SynchedEntityData.defineId(OysterEntity.class, EntityDataSerializers.BOOLEAN);
    private float open = 1, previousOpen = 1;
    private int alarmTicks;

    public OysterEntity(EntityType<? extends OysterEntity> type, Level level) {
        super(type, level);
        entityData.set(PEARL, random.nextFloat() < 0.15F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 6).add(Attributes.MOVEMENT_SPEED, 0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    public static boolean validHabitat(ServerLevelAccessor level, BlockPos pos) {
        int depth = level.getLevel().getSeaLevel() - pos.getY();
        return (level.getBiome(pos).is(Biomes.WARM_OCEAN) || level.getBiome(pos).is(Biomes.LUKEWARM_OCEAN))
            && depth >= 1 && depth <= 20 && level.getFluidState(pos).is(FluidTags.WATER)
            && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    public static boolean checkSpawn(EntityType<OysterEntity> type, ServerLevelAccessor level, EntitySpawnReason reason,
        BlockPos pos, RandomSource random) {
        return validHabitat(level, pos);
    }

    @Override public boolean checkSpawnObstruction(LevelReader level) { return level.isUnobstructed(this); }
    @Override protected void registerGoals() {}
    @Override public boolean canBreatheUnderwater() { return true; }
    @Override public boolean isPushedByFluid() { return false; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLOSED, false);
        builder.define(PEARL, false);
    }

    @Override public void tick() {
        super.tick();
        previousOpen = open;
        open = Mth.clamp(open + (isClosed() ? -1F / 6 : 1F / 30), 0, 1);
        if (!level().isClientSide()) {
            var player = level().getNearestPlayer(this, 3);
            if (player != null && !player.isSpectator()) alarmTicks = 40;
            entityData.set(CLOSED, alarmTicks > 0);
            if (alarmTicks > 0) alarmTicks--;
            // Sem deriva: assenta por gravidade, nunca procura perseguir ou atacar.
            setDeltaMovement(new Vec3(0, Math.min(0, getDeltaMovement().y), 0));
        }
    }

    public boolean isClosed() { return entityData.get(CLOSED); }
    public boolean hasPearl() { return entityData.get(PEARL); }
    public float openness(float partialTick) { return Mth.lerp(partialTick, previousOpen, open); }

    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        alarmTicks = 100;
        return super.hurtServer(level, source, damage);
    }

    @Override protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean byPlayer) {
        super.dropCustomDeathLoot(level, source, byPlayer);
        if (hasPearl()) spawnAtLocation(level, new ItemStack(ModCultivation.OYSTER_PEARL));
    }

    @Override protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("pearl", hasPearl());
        output.putInt("alarm", alarmTicks);
    }

    @Override protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        entityData.set(PEARL, input.getBooleanOr("pearl", false));
        alarmTicks = Mth.clamp(input.getIntOr("alarm", 0), 0, 100);
        entityData.set(CLOSED, alarmTicks > 0);
    }
}

