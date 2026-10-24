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

	public static void register() {
		FabricDefaultAttributeRegistry.register(BOSS_DUMMY, BossDummy.createAttributes());
	}

	private ModEntities() {}
}
