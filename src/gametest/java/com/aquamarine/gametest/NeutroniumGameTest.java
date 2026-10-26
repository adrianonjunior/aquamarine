package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.ModBlocks;
import com.aquamarine.block.NeutroniumOre;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.phys.AABB;

/**
 * Fase 4F (docs/10-fase4.md, §8): a cadeia do Neutrônio (Forja, têmpera no fundo do poço, ferraria), os bônus na
 * água, o Molde na recompensa do Kraken e o minério registrado no mundo. Chamado por {@link AquamarineClientGameTest};
 * 2 screenshots.
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
