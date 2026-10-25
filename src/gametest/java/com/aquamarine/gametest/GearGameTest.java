package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.effect.ModEffects;
import com.aquamarine.entity.ElectricDamage;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.TentacleEntity;
import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.item.DiveGear;
import com.aquamarine.item.HarpoonEffects;
import com.aquamarine.item.HarpoonQuiverItem;
import com.aquamarine.item.HarpoonTip;
import com.aquamarine.item.ModComponents;
import com.aquamarine.item.ModItems;
import com.aquamarine.item.QuiverContents;
import com.aquamarine.item.TideThrusterItem;

import java.util.List;
import java.util.Locale;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

/**
 * Testes das faixas 3C (Arpão, Aljava, pontas e os 7 encantamentos) e 3D (Propulsor de Maré:
 * impulso, cargas por Bobina, recarga, Sobrecarga e só na água). Tudo pelo servidor, como o
 * pacote do cliente faria. Chamado por {@link AquamarineClientGameTest}; 1 screenshot.
 */
final class GearGameTest {
	private static final int HALF = 10;
	private static final int HEIGHT = 12;
	private static final List<String> ENCHANTMENTS = List.of("deep_breath", "acclimation", "current", "isolation", "hook",
		"piercing_harpoon", "overcharge");

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(300, 0, -300);
		int top = o.getY() + HEIGHT - 3;
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 16, o.getZ() - 16, o.getX() + 16, o.getZ() + 16));
		context.waitTicks(20);
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow", o.getX() - HALF - 1, o.getY() - 1, o.getZ() - HALF - 1,
			o.getX() + HALF + 1, o.getY() + HEIGHT, o.getZ() + HALF + 1));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", o.getX() - HALF, o.getY(), o.getZ() - HALF,
			o.getX() + HALF, top, o.getZ() + HALF));
		server.runCommand("gamemode survival @a");
		server.runCommand("difficulty normal");
		server.runCommand("effect give @a minecraft:water_breathing 600 0 true");
		server.runCommand("effect give @a minecraft:night_vision 600 0 true");

		// ---- 3D: Propulsor ----
		tp(server, o.getX() + 0.5, o.getY() + 4, o.getZ() - 8.5, 0, 0);
		context.waitTicks(10);
		Vec3 start = server.computeOnServer(s -> {
			ServerPlayer p = player(s);
			p.getInventory().clearContent();
			p.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ModItems.TIDE_THRUSTER));
			p.getInventory().add(new ItemStack(ModItems.VOLTAIC_COIL, 1));
			check(TideThrusterItem.fire(p) == TideThrusterItem.Result.OK, "Propulsor: devia disparar na água com uma Bobina");
			check(TideThrusterItem.charges(p.getOffhandItem()) == TideThrusterItem.CHARGES_PER_COIL - 1, "Propulsor: 1 Bobina = 6 cargas, gasta 1");
			check(p.getInventory().countItem(ModItems.VOLTAIC_COIL) == 0, "Propulsor: devia gastar a Bobina");
			check(TideThrusterItem.fire(p) == TideThrusterItem.Result.COOLDOWN, "Propulsor: recarga de 3 s");
			return p.position();
		});
		context.waitTicks(10);
		shot(context, "34_propulsor_rastro");
		context.waitTicks(TideThrusterItem.BOOST_TICKS);
		double moved = server.computeOnServer(s -> player(s).position().distanceTo(start));
		check(moved >= 10.0, String.format(Locale.ROOT, "Propulsor: devia andar ~16 blocos, andou %.1f", moved));

		context.waitTicks(TideThrusterItem.COOLDOWN_TICKS);
		server.runOnServer(s -> {
			ServerPlayer p = player(s);
			ItemStack thruster = p.getOffhandItem();
			thruster.enchant(enchantment(s, "overcharge"), 2);
			thruster.set(ModComponents.THRUSTER_CHARGES, 0);
			p.getInventory().add(new ItemStack(ModItems.VOLTAIC_COIL, 1));
			check(TideThrusterItem.fire(p) == TideThrusterItem.Result.OK, "Sobrecarga: devia disparar");
			check(TideThrusterItem.charges(thruster) == 6 + 2 * 2 - 1, "Sobrecarga II: 10 cargas por Bobina, sobrou " + TideThrusterItem.charges(thruster));
		});
		context.waitTicks(TideThrusterItem.BOOST_TICKS + TideThrusterItem.COOLDOWN_TICKS);
		tp(server, o.getX() + 0.5, o.getY() + HEIGHT + 1, o.getZ() + 0.5, 0, 0);
		context.waitTicks(5);
		check(server.computeOnServer(s -> TideThrusterItem.fire(player(s))) == TideThrusterItem.Result.DRY, "Propulsor: fora da água não dispara");
		tp(server, o.getX() + 0.5, o.getY() + 4, o.getZ() + 0.5, 0, 0);
		context.waitTicks(5);

		// ---- 3C: Aljava e pontas ----
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			ServerPlayer p = player(s);
			p.getInventory().clearContent();
			p.getInventory().add(new ItemStack(ModItems.HARPOON_QUIVER));
			ItemStack quiver = HarpoonQuiverItem.find(p);
			p.getInventory().add(new ItemStack(ModItems.POISON_HARPOON_TIP, 3));
			p.getInventory().add(new ItemStack(ModItems.SHOCK_HARPOON_TIP, 2));
			QuiverContents contents = HarpoonQuiverItem.absorb(p.getInventory(), HarpoonQuiverItem.contents(quiver));
			quiver.set(ModComponents.QUIVER_CONTENTS, contents);
			check(contents.count(HarpoonTip.POISON) == 3 && contents.count(HarpoonTip.SHOCK) == 2, "Aljava: devia guardar 3 de veneno e 2 de choque");
			check(p.getInventory().countItem(ModItems.POISON_HARPOON_TIP) == 0, "Aljava: as pontas deviam sair do inventário");
			HarpoonQuiverItem.cycle(p);
			check(HarpoonQuiverItem.contents(HarpoonQuiverItem.find(p)).selectedTip() == HarpoonTip.SHOCK, "Aljava: R devia pular para o Choque");
			ThrownTrident trident = new ThrownTrident(level, p, new ItemStack(ModItems.ORICHALCUM_HARPOON));
			check(HarpoonEffects.loadTip(trident, p) == HarpoonTip.SHOCK, "Arpão: devia carregar a ponta escolhida");
			check(HarpoonQuiverItem.contents(HarpoonQuiverItem.find(p)).count(HarpoonTip.SHOCK) == 1, "Arpão: devia gastar 1 ponta");
		});

		// Efeitos das pontas em porcos na água.
		server.runOnServer(s -> {
			pig(s.overworld(), o.offset(3, 3, 0));
			pig(s.overworld(), o.offset(5, 3, 0));
		});
		// Mob recém-criado só "sente" a água no primeiro tick.
		context.waitTicks(5);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			ServerPlayer p = player(s);
			List<Pig> pigs = level.getEntitiesOfClass(Pig.class, new net.minecraft.world.phys.AABB(o).inflate(12), e -> e.isAlive())
				.stream().sorted(java.util.Comparator.comparingDouble(e -> e.getX())).toList();
			Pig a = pigs.get(0);
			Pig b = pigs.get(1);
			HarpoonEffects.applyTip(level, HarpoonTip.POISON, a, p);
			check(a.hasEffect(MobEffects.POISON), "Ponta de Veneno: devia envenenar");
			HarpoonEffects.applyTip(level, HarpoonTip.WITHER, a, p);
			check(a.hasEffect(MobEffects.WITHER), "Ponta de Wither: devia dar Wither");
			HarpoonEffects.applyTip(level, HarpoonTip.FIRE, a, p);
			check(a.hasEffect(ModEffects.ABYSSAL_FLAME), "Ponta de Fogo: devia dar Chama Abissal");
			float before = b.getHealth();
			HarpoonEffects.applyTip(level, HarpoonTip.SHOCK, a, p);
			check(b.getHealth() < before, "Ponta de Choque: o porco molhado ao lado devia levar a descarga");
		});
		context.waitTicks(30);
		check(server.computeOnServer(s -> s.overworld().getEntitiesOfClass(Pig.class, player(s).getBoundingBox().inflate(16))
			.stream().anyMatch(pig -> pig.getHealth() < pig.getMaxHealth() - 3.0F)), "Chama Abissal: devia queimar debaixo d'água");
		server.runCommand("kill @e[type=minecraft:pig]");

		// Fisgada e Perfurante: puxa o alvo; o seguinte na linha também leva o dano.
		server.runOnServer(s -> {
			pig(s.overworld(), o.offset(0, 3, 4));
			pig(s.overworld(), o.offset(0, 3, 7));
		});
		context.waitTicks(5);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			ServerPlayer p = player(s);
			List<Pig> pigs = level.getEntitiesOfClass(Pig.class, new net.minecraft.world.phys.AABB(o).inflate(12), e -> e.isAlive())
				.stream().sorted(java.util.Comparator.comparingDouble(e -> e.getZ())).toList();
			ItemStack harpoon = new ItemStack(ModItems.ORICHALCUM_HARPOON);
			harpoon.enchant(enchantment(s, "hook"), 2);
			harpoon.enchant(enchantment(s, "piercing_harpoon"), 1);
			Pig first = pigs.get(0);
			Pig second = pigs.get(1);
			ThrownTrident trident = new ThrownTrident(level, p, harpoon);
			trident.setPos(first.getX(), first.getY() + 0.5, first.getZ() - 1.0);
			trident.setDeltaMovement(0, 0, 2.0);
			float secondBefore = second.getHealth();
			HarpoonEffects.onHit(trident, first);
			check(first.getDeltaMovement().z < -0.1, "Fisgada: devia puxar o alvo para o dono");
			check(second.getHealth() < secondBefore, "Perfurante: o segundo alvo na linha devia levar dano");
		});
		server.runCommand("kill @e[type=minecraft:pig]");

		// Kraken: veneno não pega; fogo e choque somam no escudo; o Arpão prende o tentáculo.
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			ServerPlayer p = player(s);
			KrakenEntity kraken = KrakenSummoning.spawnAt(level, o.offset(0, 2, 0), o);
			check(kraken != null, "Arpão: o Kraken de teste não surgiu");
			kraken.attacks().setEnabled(false);
			float shield = kraken.shield();
			HarpoonEffects.applyTip(level, HarpoonTip.POISON, kraken, p);
			check(kraken.shield() == shield && !kraken.hasEffect(MobEffects.POISON), "Ponta de Veneno: não pega no Kraken");
			HarpoonEffects.applyTip(level, HarpoonTip.SHOCK, kraken, p);
			float shock = shield - kraken.shield();
			check(Math.abs(shock - HarpoonEffects.BASE_DAMAGE * 0.4F * 3.0F) < 0.5F, "Ponta de Choque: +40% do dano base no escudo (×3), tirou " + shock);
			TentacleEntity tentacle = ModEntities.TENTACLE.create(level, EntitySpawnReason.COMMAND);
			tentacle.snapTo(o.getX() + 4.5, o.getY() + 2, o.getZ() + 4.5, 0, 0);
			level.addFreshEntity(tentacle);
			HarpoonEffects.onHit(new ThrownTrident(level, p, new ItemStack(ModItems.ORICHALCUM_HARPOON)), tentacle);
			check(tentacle.isPinned() && tentacle.isBusy(), "Arpão: devia prender o tentáculo");
		});
		server.runCommand("kill @e[type=aquamarine:kraken]");
		server.runCommand("kill @e[type=aquamarine:tentacle]");

		// Encantamentos: os 7 existem, fora da mesa de encantamento, e cada um faz o seu efeito.
		server.runOnServer(s -> {
			var registry = s.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
			for (String name : ENCHANTMENTS) {
				Holder<Enchantment> holder = enchantment(s, name);
				check(!holder.is(EnchantmentTags.IN_ENCHANTING_TABLE), "Encantamento " + name + ": não pode sair na mesa comum");
			}
			check(registry.get(ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("shell_breaker"))).isPresent(), "Quebra-Carapaça sumiu");
			ServerPlayer p = player(s);
			ItemStack helmet = new ItemStack(ModItems.DEEP_DIVING_HELMET);
			helmet.enchant(enchantment(s, "acclimation"), 2);
			helmet.enchant(enchantment(s, "deep_breath"), 3);
			ItemStack chest = new ItemStack(ModItems.DEEP_DIVING_CHESTPLATE);
			chest.enchant(enchantment(s, "isolation"), 3);
			ItemStack boots = new ItemStack(ModItems.ORICHALCUM_BOOTS);
			boots.enchant(enchantment(s, "current"), 3);
			p.setItemSlot(EquipmentSlot.HEAD, helmet);
			p.setItemSlot(EquipmentSlot.CHEST, chest);
			p.setItemSlot(EquipmentSlot.FEET, boots);
			check(DiveGear.acclimationLevels(p) == 2, "Aclimatação II: devia tirar 2 níveis da Pressão da profundidade");
			// 2 peças do Traje (−80%) e Isolamento III (−45%): passa de 100%, fica imune.
			check(Math.abs(ElectricDamage.multiplier(0, 3) - 0.55F) < 1.0E-4F, "Isolamento III: −45% de dano elétrico");
			check(ElectricDamage.multiplier(p) == 0.0F, "Isolamento + Traje: devia ficar imune");
		});
		context.waitTicks(5);
		server.runOnServer(s -> {
			ServerPlayer p = player(s);
			check(p.getAttributeValue(Attributes.OXYGEN_BONUS) >= 0.89, "Fôlego Profundo III: +0,9 de bônus de ar, era " + p.getAttributeValue(Attributes.OXYGEN_BONUS));
			check(p.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY) >= 0.29, "Correnteza III: +0,3 de eficiência na água");
			for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
				p.setItemSlot(slot, ItemStack.EMPTY);
			}
			p.getInventory().clearContent();
		});

		server.runCommand("effect clear @a");
		server.runOnServer(s -> player(s).setHealth(20.0F));
		server.runCommand("gamemode creative @a");
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
		context.waitTicks(5);
	}

	private static Holder<Enchantment> enchantment(MinecraftServer server, String name) {
		return server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
			.getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id(name)));
	}

	private static Pig pig(ServerLevel level, BlockPos pos) {
		Pig pig = EntityTypes.PIG.create(level, EntitySpawnReason.COMMAND);
		pig.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
		pig.setPersistenceRequired();
		level.addFreshEntity(pig);
		return pig;
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

	private GearGameTest() {}
}
