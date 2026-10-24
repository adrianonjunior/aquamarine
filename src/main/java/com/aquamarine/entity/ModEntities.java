package com.aquamarine.entity;

import com.aquamarine.Aquamarine;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** Tipos de entidade do mod. Renderizadores ficam em AquamarineClient. */
public final class ModEntities {
	private static final ResourceKey<EntityType<?>> BOSS_DUMMY_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("boss_dummy"));

	/** Só para testes (barra de vida); sem ovo e sem surgimento natural. */
	public static final EntityType<BossDummy> BOSS_DUMMY = Registry.register(BuiltInRegistries.ENTITY_TYPE, BOSS_DUMMY_KEY,
		EntityType.Builder.of(BossDummy::new, MobCategory.MISC).sized(2.0F, 2.0F).fireImmune().build(BOSS_DUMMY_KEY));

	private static final ResourceKey<EntityType<?>> KRAKEN_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("kraken"));

	/** O Kraken (docs/02-kraken.md). Caixa provisória; o tamanho final vem com o modelo. */
	public static final EntityType<KrakenEntity> KRAKEN = Registry.register(BuiltInRegistries.ENTITY_TYPE, KRAKEN_KEY,
		EntityType.Builder.of(KrakenEntity::new, MobCategory.MONSTER).sized(8.0F, 6.0F).fireImmune().clientTrackingRange(16).build(KRAKEN_KEY));

	private static final ResourceKey<EntityType<?>> TENTACLE_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("tentacle"));
	private static final ResourceKey<EntityType<?>> YOUNG_KRAKEN_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("young_kraken"));

	/** Tentáculo do Kraken (60 de vida). Caixa provisória até o modelo da 2D-M. */
	public static final EntityType<TentacleEntity> TENTACLE = Registry.register(BuiltInRegistries.ENTITY_TYPE, TENTACLE_KEY,
		EntityType.Builder.of(TentacleEntity::new, MobCategory.MONSTER).sized(1.5F, 6.0F).fireImmune().clientTrackingRange(16).build(TENTACLE_KEY));

	/** Cria de Kraken (8 de vida). Caixa provisória até o modelo da 2D-M. */
	public static final EntityType<YoungKrakenEntity> YOUNG_KRAKEN = Registry.register(BuiltInRegistries.ENTITY_TYPE, YOUNG_KRAKEN_KEY,
		EntityType.Builder.of(YoungKrakenEntity::new, MobCategory.MONSTER).sized(0.9F, 0.9F).fireImmune().clientTrackingRange(10).build(YOUNG_KRAKEN_KEY));

	private static final ResourceKey<EntityType<?>> KRAKEN_EGG_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("kraken_egg"));

	/** Ovo do Kraken (aviso do ataque Crias). */
	public static final EntityType<KrakenEggEntity> KRAKEN_EGG = Registry.register(BuiltInRegistries.ENTITY_TYPE, KRAKEN_EGG_KEY,
		EntityType.Builder.of(KrakenEggEntity::new, MobCategory.MISC).sized(0.8F, 1.0F).fireImmune().clientTrackingRange(10).build(KRAKEN_EGG_KEY));

	public static void register() {
		FabricDefaultAttributeRegistry.register(KRAKEN, KrakenEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(BOSS_DUMMY, BossDummy.createAttributes());
		FabricDefaultAttributeRegistry.register(TENTACLE, TentacleEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(YOUNG_KRAKEN, YoungKrakenEntity.createAttributes());
	}

	private ModEntities() {}
}
