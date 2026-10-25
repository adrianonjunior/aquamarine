package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.ModTags;
import com.aquamarine.entity.ModVillagers;
import com.aquamarine.item.ModItems;

import java.util.List;
import java.util.Locale;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.phys.AABB;

/**
 * Teste da faixa 3E (Porto dos Náufragos): a estrutura gerada por script entra no mundo com os 6
 * Mergulhadores das 3 profissões e as trocas saem dos dados sem nunca vender o que é proibido. Chamado por
 * {@link AquamarineClientGameTest}; 1 screenshot.
 */
final class HarborGameTest {
	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(-300, 0, -300);
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 8, o.getZ() - 8, o.getX() + 56, o.getZ() + 56));
		server.runCommand("gamemode spectator @a");
		tp(server, o.getX() + 24.5, o.getY() + 20, o.getZ() + 24.5, 0, 90);
		world.getConnection().waitForChunksRender();
		context.waitTicks(40);
		check(server.computeOnServer(s -> s.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET)
			.get(ResourceKey.create(Registries.STRUCTURE_SET, Aquamarine.id("harbor"))).isPresent()), "Porto: conjunto de estruturas não carregou");
		String place = String.format("place structure aquamarine:harbor %d %d %d", o.getX(), o.getY(), o.getZ());
		int placed = server.computeOnServer(s -> {
			try {
				return s.getCommands().getDispatcher().execute(place, s.createCommandSourceStack().withSuppressedOutput());
			} catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
				throw new AssertionError("Porto: /place falhou: " + e.getMessage());
			}
		});
		check(placed > 0, "Porto: /place não colocou a estrutura");
		context.waitTicks(40);

		List<Villager> divers = server.computeOnServer(s -> s.overworld().getEntitiesOfClass(Villager.class,
			new AABB(o).inflate(64), v -> v.getVillagerData().profession().unwrapKey().map(k -> k.identifier().getNamespace())
				.orElse("").equals(Aquamarine.MOD_ID)));
		check(divers.size() >= 6, "Porto: devia ter 6 Mergulhadores, achou " + divers.size());
		String floor = server.computeOnServer(s -> {
			BlockPos feet = divers.getFirst().blockPosition();
			StringBuilder column = new StringBuilder();
			for (int dy = -3; dy <= 2; dy++) {
				column.append(dy).append('=').append(s.overworld().getBlockState(feet.offset(0, dy, 0)).getBlock().getDescriptionId()).append(' ');
			}
			return column.toString();
		});
		check(floor.contains("prismarine") || floor.contains("sea_lantern"), "Porto: o piso da Vila não foi colocado: " + floor);
		for (ResourceKey<VillagerProfession> profession : List.of(ModVillagers.TIDE_SMITH, ModVillagers.NAUTICAL_CARTOGRAPHER,
			ModVillagers.ABYSS_ENCHANTER)) {
			check(divers.stream().anyMatch(v -> v.getVillagerData().profession().is(profession)), "Porto: faltou a profissão " + profession.identifier());
		}

		// O /place só põe os blocos e não registra a estrutura no mundo; por isso a Eclusa (que pergunta
		// "estou dentro do Porto?") só é conferida num mundo gerado de verdade.

		// Trocas: saem dos dados; Ferreiro e Encantador oferecem o da tabela; nunca o proibido.
		server.runOnServer(s -> {
			for (Villager v : divers) {
				for (MerchantOffer offer : v.getOffers()) {
					ItemStack result = offer.getResult();
					check(!result.is(ModTags.Items.HARPOONS) && !result.is(ModItems.ORICHALCUM_INGOT) && !result.is(ModItems.ORICHALCUM_HELMET)
						&& !result.is(ModItems.ORICHALCUM_CHESTPLATE), "Porto: troca proibida " + result);
					ItemEnchantments stored = result.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
					check(stored.keySet().stream().noneMatch(h -> h.is(ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("shell_breaker")))),
						"Porto: Quebra-Carapaça não se vende");
				}
			}
			Villager smith = divers.stream().filter(v -> v.getVillagerData().profession().is(ModVillagers.TIDE_SMITH)).findFirst().orElseThrow();
			check(!smith.getOffers().isEmpty() && smith.getOffers().stream().allMatch(offer -> offer.getResult().is(ModItems.HARPOON_QUIVER)
				|| offer.getResult().is(ModItems.POISON_HARPOON_TIP) || offer.getResult().is(Items.EMERALD)), "Ferreiro de Maré: trocas do nível 1 erradas");
			Villager enchanter = divers.stream().filter(v -> v.getVillagerData().profession().is(ModVillagers.ABYSS_ENCHANTER)).findFirst().orElseThrow();
			check(enchanter.getOffers().stream().anyMatch(offer -> offer.getResult().getOrDefault(DataComponents.STORED_ENCHANTMENTS,
				ItemEnchantments.EMPTY).keySet().stream().anyMatch(h -> h.is(ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("deep_breath"))))),
				"Encantador de Fossa: devia vender Fôlego Profundo I no nível 1");
		});

		// O jigsaw gira a peça: o centro da Cúpula é a média dos Mergulhadores, em volta do Sino.
		double cx = divers.stream().mapToDouble(v -> v.getX()).average().orElse(o.getX());
		double cy = divers.stream().mapToDouble(v -> v.getY()).min().orElse(o.getY());
		double cz = divers.stream().mapToDouble(v -> v.getZ()).average().orElse(o.getZ());
		tp(server, cx, cy + 3.0, cz + 10.0, 180, 15);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		shot(context, "35_porto_dos_naufragos");
		server.runCommand("gamemode creative @a");
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
		context.waitTicks(5);
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

	private HarborGameTest() {}
}
