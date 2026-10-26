package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.ModBlocks;
import com.aquamarine.block.NeutroniumOre;
import com.aquamarine.entity.CavitationPulseEntity;
import com.aquamarine.entity.boss.KrakenRewards;
import com.aquamarine.item.ModItems;
import com.aquamarine.item.NeutroniumEffects;
import com.aquamarine.item.NeutroniumTempering;
import com.aquamarine.pressure.PressureManager;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.CameraType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.phys.AABB;

/**
 * Fase 4F (docs/10-fase4.md, §8): a cadeia do Neutrônio (Forja, têmpera no fundo do poço, ferraria), os bônus na
 * água, o Molde na recompensa do Kraken e o minério registrado no mundo. Chamado por {@link AquamarineClientGameTest};
 * 5 screenshots (vitrine, armadura submersa, pulso, Cavitador em primeira e terceira pessoa).
 */
final class NeutroniumGameTest {
	private static final int HALF = 4;
	private static final int DEPTH = 10;

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(-300, 0, 300);
		BlockPos v = base.offset(4, 0, -24);
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 16, o.getZ() - 16, o.getX() + 16, o.getZ() + 16));
		context.waitTicks(20);
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow", o.getX() - HALF - 1, o.getY() - 1, o.getZ() - HALF - 1,
			o.getX() + HALF + 1, o.getY() + DEPTH, o.getZ() + HALF + 1));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", o.getX() - HALF, o.getY(), o.getZ() - HALF,
			o.getX() + HALF, o.getY() + DEPTH - 1, o.getZ() + HALF));
		server.runCommand("time set noon");
		// Nível do mar de teste: o fundo do poço fica a 75 blocos de profundidade (Pressão 3).
		server.runOnServer(s -> PressureManager.setTestSeaLevel(o.getY() + 75));
		try {
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				check(s.getRecipeManager().byKey(recipe("neutronium_dust")).isPresent(), "sem a receita da Poeira na Forja");
				check(s.getRecipeManager().byKey(recipe("unstable_neutronium_ingot")).isPresent(), "sem a receita do Lingote Instável");
				SmithingRecipeInput input = new SmithingRecipeInput(new ItemStack(ModItems.NEUTRONIUM_UPGRADE_SMITHING_TEMPLATE),
					new ItemStack(ModItems.ABYSSAL_PICKAXE), new ItemStack(ModItems.NEUTRONIUM_INGOT));
				ItemStack upgraded = s.getRecipeManager().getRecipeFor(RecipeType.SMITHING, input, level)
					.map(holder -> holder.value().assemble(input)).orElse(ItemStack.EMPTY);
				check(upgraded.is(ModItems.NEUTRONIUM_PICKAXE), "a ferraria não transformou a picareta Abissal: " + upgraded);
				check(KrakenRewards.plan(true, false, level.getRandom()).stream().anyMatch(st -> st.is(ModItems.NEUTRONIUM_UPGRADE_SMITHING_TEMPLATE)),
					"a primeira vitória sobre o Kraken não deu o Molde");
				check(KrakenRewards.plan(false, false, level.getRandom()).stream().noneMatch(st -> st.is(ModItems.NEUTRONIUM_UPGRADE_SMITHING_TEMPLATE)),
					"o Molde não deveria vir de novo");
				check(s.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).get(NeutroniumOre.PLACED).isPresent(),
					"o minério de Neutrônio não está no mundo");

				// Vitrine ao ar livre: suporte com o conjunto e a espada, entre o minério e o bloco.
				ArmorStand stand = new ArmorStand(level, v.getX() + 0.5, v.getY(), v.getZ() + 0.5);
				stand.setShowArms(true);
				stand.setNoBasePlate(true);
				stand.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.NEUTRONIUM_HELMET));
				stand.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.NEUTRONIUM_CHESTPLATE));
				stand.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.NEUTRONIUM_LEGGINGS));
				stand.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.NEUTRONIUM_BOOTS));
				stand.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.NEUTRONIUM_SWORD));
				level.addFreshEntity(stand);
				level.setBlockAndUpdate(v.offset(-2, 0, 0), ModBlocks.NEUTRONIUM_ORE.defaultBlockState());
				level.setBlockAndUpdate(v.offset(2, 0, 0), ModBlocks.NEUTRONIUM_BLOCK.defaultBlockState());

				// Têmpera: o lingote solto no alto do poço afunda.
				ItemEntity ingot = new ItemEntity(level, o.getX() + 0.5, o.getY() + DEPTH - 1.5, o.getZ() + 0.5,
					new ItemStack(ModItems.UNSTABLE_NEUTRONIUM_INGOT, 2));
				ingot.setNeverPickUp();
				level.addFreshEntity(ingot);
			});
			context.waitTicks(60);
			double y = server.computeOnServer(s -> ingots(s, o).stream().mapToDouble(ItemEntity::getY).min().orElse(999));
			check(y < o.getY() + DEPTH - 4, "o Lingote Instável não afundou (y " + y + ")");
			tp(server, v.getX() + 0.5, v.getY() + 0.4, v.getZ() + 3.6, 180, 6);
			world.getConnection().waitForChunksRender();
			context.waitTicks(20);
			shot(context, "neutronio_00_vitrine");
			context.waitTicks(NeutroniumTempering.TEMPER_TICKS + 40);
			server.runOnServer(s -> {
				List<ItemEntity> found = ingots(s, o);
				check(found.stream().anyMatch(e -> e.getItem().is(ModItems.NEUTRONIUM_INGOT) && e.getItem().getCount() == 2),
					"a têmpera não estabilizou o lingote no fundo: " + found.stream().map(ItemEntity::getItem).toList());
				found.forEach(ItemEntity::discard);
			});

			// Bônus na água com o conjunto completo e a espada.
			server.runOnServer(s -> {
				ServerPlayer p = player(s);
				p.removeAllEffects();
				p.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.NEUTRONIUM_HELMET));
				p.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.NEUTRONIUM_CHESTPLATE));
				p.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.NEUTRONIUM_LEGGINGS));
				p.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.NEUTRONIUM_BOOTS));
				p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.NEUTRONIUM_SWORD));
			});
			tp(server, o.getX() + 0.5, o.getY() + 2, o.getZ() + 0.5, 180, 0);
			context.waitTicks(40);
			server.runOnServer(s -> {
				ServerPlayer p = player(s);
				check(p.hasEffect(MobEffects.WATER_BREATHING) && p.hasEffect(MobEffects.NIGHT_VISION), "capacete: sem respiração ou visão");
				check(p.hasEffect(MobEffects.DOLPHINS_GRACE), "calças: sem Graça do Golfinho");
				check(p.hasEffect(MobEffects.REGENERATION), "conjunto: sem regeneração submerso");
				check(p.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(Aquamarine.id("neutronium_set_damage")), "conjunto: sem +20% de dano");
				check(p.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(Aquamarine.id("neutronium_sword_underwater")), "espada: sem +3 submersa");
				check(NeutroniumEffects.hasFullSet(p) && PressureManager.rawAmbientCap(p) == 3 && PressureManager.level(p) == 0,
					"peitoral: a Pressão da profundidade subiu (" + PressureManager.level(p) + ")");
			});
			tp(server, o.getX() + 0.5, o.getY() + 1.4, o.getZ() + 3.4, 180, 0);
			server.runCommand("summon minecraft:armor_stand " + String.format(Locale.ROOT, "%.1f %d %.1f", o.getX() + 0.5, o.getY() + 2, o.getZ() + 0.5)
				+ " {NoGravity:1b,ShowArms:1b,NoBasePlate:1b,equipment:{head:{id:\"aquamarine:neutronium_helmet\"},chest:{id:\"aquamarine:neutronium_chestplate\"},"
				+ "legs:{id:\"aquamarine:neutronium_leggings\"},feet:{id:\"aquamarine:neutronium_boots\"},mainhand:{id:\"aquamarine:neutronium_sword\"}}}");
			context.waitTicks(20);
			shot(context, "neutronio_01_submerso");
			server.runCommand("kill @e[type=minecraft:armor_stand]");

			// 4G: Cavitador. Debaixo d'água o pulso acerta um afogado parado 4 blocos à frente.
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				ServerPlayer p = player(s);
				p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.NEUTRONIUM_CAVITATOR));
				Drowned target = new Drowned(EntityTypes.DROWNED, level);
				target.setPos(o.getX() + 0.5, o.getY() + 1, o.getZ() - 3.5);
				target.setNoAi(true);
				target.setPersistenceRequired();
				level.addFreshEntity(target);
			});
			tp(server, o.getX() + 0.5, o.getY() + 0.6, o.getZ() + 0.5, 180, 0);
			context.waitTicks(10);
			float before = server.computeOnServer(s -> drowned(s, o).getHealth());
			server.runOnServer(s -> CavitationPulseEntity.shoot(s.overworld(), player(s), true,
				player(s).getMainHandItem()));
			context.waitTicks(2);
			shot(context, "neutronio_02_pulso");
			context.waitTicks(20);
			server.runOnServer(s -> {
				float after = drowned(s, o) == null ? 0 : drowned(s, o).getHealth();
				check(before - after >= CavitationPulseEntity.FULL_DAMAGE * 0.8F, "o pulso não acertou o afogado (a armadura dele tira um pouco): " + before + " -> " + after);
				check(s.overworld().getEntitiesOfClass(CavitationPulseEntity.class, new AABB(o).inflate(40)).isEmpty(),
					"o pulso não implodiu");
				s.overworld().getEntitiesOfClass(Drowned.class, new AABB(o).inflate(8)).forEach(Drowned::discard);
			});

			// Fora d'água: lança um arpão (sem pulso).
			tp(server, v.getX() + 0.5, v.getY(), v.getZ() + 3.6, 180, 0);
			context.waitTicks(10);
			server.runOnServer(s -> {
				ServerPlayer p = player(s);
				ItemStack cavitator = p.getMainHandItem();
				check(cavitator.getItem().releaseUsing(cavitator, s.overworld(), p, 72000 - 20), "o Cavitador não disparou fora d'água");
				check(!s.overworld().getEntitiesOfClass(ThrownTrident.class, p.getBoundingBox().inflate(6)).isEmpty(),
					"fora d'água o Cavitador não lançou arpão");
				check(s.overworld().getEntitiesOfClass(CavitationPulseEntity.class, p.getBoundingBox().inflate(6)).isEmpty(),
					"fora d'água não pode sair pulso");
			});
			// 6D3: o arpão do Cavitador sai com o desenho próprio; vitrine dos 6 desenhos parados no ar, de pé.
			server.runOnServer(s -> {
				ServerPlayer p = player(s);
				check(s.overworld().getEntitiesOfClass(ThrownTrident.class, p.getBoundingBox().inflate(8)).stream()
					.anyMatch(t -> ((com.aquamarine.item.HarpoonStyleHolder) t).aquamarine$harpoonStyle() == com.aquamarine.item.HarpoonStyleHolder.CAVITATOR),
					"o arpão do Cavitador saiu sem o desenho próprio");
				for (int i = 1; i < com.aquamarine.item.HarpoonStyleHolder.COUNT; i++) {
					ThrownTrident t = new ThrownTrident(net.minecraft.world.entity.EntityTypes.TRIDENT, s.overworld());
					t.snapTo(p.getX() - 2.5 + (i - 1), p.getY() + 0.6, p.getZ() - 3.0, 0.0F, 0.0F);
					t.setNoGravity(true);
					t.setDeltaMovement(0.0, 0.002, 0.0);
					((com.aquamarine.item.HarpoonStyleHolder) t).aquamarine$setHarpoonStyle(i);
					s.overworld().addFreshEntity(t);
				}
			});
			context.waitTicks(4);
			shot(context, "neutronio_03b_arpoes");
			server.runOnServer(s -> s.overworld().getEntitiesOfClass(ThrownTrident.class, player(s).getBoundingBox().inflate(8))
				.forEach(net.minecraft.world.entity.Entity::discard));
			context.waitTicks(21);
			shot(context, "neutronio_03_cavitador_mao");
			context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
			context.waitTicks(5);
			shot(context, "neutronio_04_cavitador_terceira");
			context.runOnClient(mc -> mc.options.setCameraType(CameraType.FIRST_PERSON));
		} finally {
			server.runOnServer(s -> {
				PressureManager.setTestSeaLevel(null);
				ServerPlayer p = player(s);
				for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
					EquipmentSlot.MAINHAND}) {
					p.setItemSlot(slot, ItemStack.EMPTY);
				}
				p.removeAllEffects();
			});
		}
	}

	private static Drowned drowned(MinecraftServer s, BlockPos o) {
		return s.overworld().getEntitiesOfClass(Drowned.class, new AABB(o).inflate(8)).stream().findFirst().orElse(null);
	}

	private static List<ItemEntity> ingots(MinecraftServer s, BlockPos o) {
		return s.overworld().getEntitiesOfClass(ItemEntity.class, new AABB(o).inflate(HALF + 1, DEPTH + 2, HALF + 1));
	}

	private static ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> recipe(String name) {
		return ResourceKey.create(Registries.RECIPE, Aquamarine.id(name));
	}

	private static ServerPlayer player(MinecraftServer server) {
		return server.getPlayerList().getPlayers().getFirst();
	}

	private static void shot(ClientGameTestContext context, String name) {
		context.runOnClient(mc -> mc.gui.toastManager().clear());
		context.waitTick();
		context.takeScreenshot(name);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void tp(TestServerContext server, double x, double y, double z, float yaw, float pitch) {
		server.runCommand(String.format(Locale.ROOT, "tp @p %.2f %.2f %.2f %.1f %.1f", x, y, z, yaw, pitch));
	}

	private NeutroniumGameTest() {}
}
