package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.ModTags;
import com.aquamarine.entity.ModVillagers;
import com.aquamarine.entity.MorayEntity;
import com.aquamarine.harbor.HarborAlert;
import com.aquamarine.harbor.HarborReputation;
import com.aquamarine.harbor.HarborTrades;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.phys.AABB;

/**
 * Teste da faixa 3E (Porto dos Náufragos): a estrutura gerada por script entra no mundo com os 9
 * Mergulhadores das 3 profissões (3 por oficina), cada um com as trocas de todos os níveis, que saem dos
 * dados sem nunca vender o que é proibido; a reputação do jogador abre os níveis (no cliente as ofertas
 * fechadas chegam esgotadas e o servidor recusa a troca); trocar soma reputação e reabre as ofertas; e o
 * Alerta de Moréia toca o Sino, solta a Moréia e paga quem a vence. O que depende do mundo gerado de
 * verdade (Eclusa, Torre de Vigia, elevadores, Mapa de Fossas) fica no {@link WorldgenGameTest}. Chamado
 * por {@link AquamarineClientGameTest}; 2 screenshots.
 */
final class HarborGameTest {
	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(-300, 0, -300);
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 88, o.getZ() - 88, o.getX() + 88, o.getZ() + 88));
		server.runCommand("gamemode spectator @a");
		tp(server, o.getX() + 0.5, o.getY() + 24, o.getZ() + 0.5, 0, 90);
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
			new AABB(o).inflate(96), HarborTrades::isDiver));
		check(divers.size() >= 9, "Porto: devia ter 9 Mergulhadores, achou " + divers.size());
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
			long count = divers.stream().filter(v -> v.getVillagerData().profession().is(profession)).count();
			check(count == 3, "Porto: devia ter 3 de " + profession.identifier() + ", achou " + count);
		}

		// O /place só põe os blocos e não registra a estrutura no mundo; por isso a Eclusa (que pergunta
		// "estou dentro do Porto?") só é conferida no mundo gerado de verdade (WorldgenGameTest).

		// Trocas: saem dos dados, todas de uma vez com o nível guardado; nunca o proibido.
		server.runOnServer(s -> {
			for (Villager v : divers) {
				MerchantOffers offers = v.getOffers();
				List<Integer> levels = v.getAttached(HarborTrades.OFFER_LEVELS);
				check(levels != null && levels.size() == offers.size(), "Porto: níveis das trocas não guardados em " + v);
				for (MerchantOffer offer : v.getOffers()) {
					ItemStack result = offer.getResult();
					check(!result.is(ModTags.Items.HARPOONS) && !result.is(ModItems.ABYSSAL_INGOT) && !result.is(ModItems.ABYSSAL_HELMET)
						&& !result.is(ModItems.ABYSSAL_CHESTPLATE), "Porto: troca proibida " + result);
					ItemEnchantments stored = result.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
					check(stored.keySet().stream().noneMatch(h -> h.is(ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("shell_breaker")))),
						"Porto: Quebra-Carapaça não se vende");
				}
			}
			Villager smith = first(divers, ModVillagers.TIDE_SMITH);
			List<Integer> levels = smith.getAttached(HarborTrades.OFFER_LEVELS);
			check(levels.contains(1) && levels.contains(5), "Ferreiro de Maré: devia ter as trocas dos níveis 1 a 5: " + levels);
			for (int k = 0; k < levels.size(); k++) {
				ItemStack result = smith.getOffers().get(k).getResult();
				if (levels.get(k) == 1) {
					check(result.is(ModItems.HARPOON_QUIVER) || result.is(ModItems.POISON_HARPOON_TIP) || result.is(Items.EMERALD),
						"Ferreiro de Maré: troca do nível 1 errada " + result);
				}
				if (levels.get(k) == 5) {
					check(result.is(ModItems.WHALE_SADDLE), "Ferreiro de Maré: o nível 5 devia ser a Sela de Baleia, era " + result);
				}
			}
			Villager enchanter = first(divers, ModVillagers.ABYSS_ENCHANTER);
			check(enchanter.getOffers().stream().anyMatch(offer -> offer.getResult().getOrDefault(DataComponents.STORED_ENCHANTMENTS,
				ItemEnchantments.EMPTY).keySet().stream().anyMatch(h -> h.is(ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("deep_breath"))))),
				"Encantador de Fossa: devia vender Fôlego Profundo I no nível 1");
		});

		// O centro da Cúpula é o Sino (o jigsaw gira a peça).
		double ax = divers.stream().mapToDouble(v -> v.getX()).average().orElse(o.getX());
		double ay = divers.stream().mapToDouble(v -> v.getY()).min().orElse(o.getY());
		double az = divers.stream().mapToDouble(v -> v.getZ()).average().orElse(o.getZ());
		BlockPos bell = server.computeOnServer(s -> HarborAlert.findBell(s.overworld(), BlockPos.containing(ax, ay, az), 24));
		check(bell != null, "Porto: o Sino do Porto não foi colocado");

		reputation(context, server, first(divers, ModVillagers.TIDE_SMITH));

		tp(server, bell.getX() + 0.5, bell.getY() + 2.0, bell.getZ() + 10.5, 180, 15);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		shot(context, "35_porto_dos_naufragos");

		alert(context, server, bell);
		server.runCommand("gamemode creative @a");
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
		context.waitTicks(5);
	}

	/** Reputação: com 0 só o nível 1 abre; trocar soma a XP da oferta e, ao subir de nível, reabre as ofertas. */
	private static void reputation(ClientGameTestContext context, TestServerContext server, Villager smith) {
		server.runCommand("gamemode survival @a");
		List<Integer> levels = server.computeOnServer(s -> List.copyOf(smith.getAttached(HarborTrades.OFFER_LEVELS)));
		int above1 = (int) levels.stream().filter(l -> l > 1).count();
		int above2 = (int) levels.stream().filter(l -> l > 2).count();
		int open = levels.indexOf(1);
		int locked = levels.indexOf(2);
		check(open >= 0 && locked > 0, "Ferreiro de Maré: faltam trocas dos níveis 1 e 2: " + levels);

		tp(server, smith.getX(), smith.getY(), smith.getZ(), 0, 0);
		context.waitTicks(5);
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			HarborReputation.get(s).set(player.getUUID(), 0);
			smith.mobInteract(player, InteractionHand.MAIN_HAND);
			check(player.containerMenu instanceof MerchantMenu, "Porto: a troca com o Ferreiro não abriu");
		});
		context.waitTicks(10);
		int[] seen = clientOffers(context);
		check(seen[0] == levels.size(), "Porto: o cliente devia ver todas as " + levels.size() + " ofertas, viu " + seen[0]);
		check(seen[1] == above1, "Porto: com reputação 0, " + above1 + " ofertas deviam chegar esgotadas, chegaram " + seen[1]);
		check(seen[2] == 1, "Porto: a tela devia mostrar o nível 1 do jogador, mostrou " + seen[2]);
		shot(context, "36_porto_troca_reputacao");

		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			MerchantMenu menu = (MerchantMenu) player.containerMenu;
			check(resultFor(menu, smith.getOffers().get(locked), locked).isEmpty(), "Porto: o servidor vendeu acima da reputação");
			check(!resultFor(menu, smith.getOffers().get(open), open).isEmpty(), "Porto: o servidor não vendeu o nível 1");
			// Faltando 1 ponto para o nível 2, uma troca abre o nível 2 e reenvia as ofertas.
			HarborReputation reputation = HarborReputation.get(s);
			reputation.set(player.getUUID(), HarborReputation.TIER_POINTS[1] - 1);
			MerchantOffer offer = smith.getOffers().get(open);
			check(offer.getXp() > 0, "Porto: a troca do nível 1 devia dar XP");
			smith.notifyTrade(offer);
			check(reputation.points(player.getUUID()) == HarborReputation.TIER_POINTS[1] - 1 + offer.getXp(),
				"Porto: a troca não somou a XP da oferta à reputação");
			check(!resultFor(menu, smith.getOffers().get(locked), locked).isEmpty(), "Porto: o nível 2 não abriu no servidor");
		});
		context.waitTicks(10);
		seen = clientOffers(context);
		check(seen[1] == above2 && seen[2] == 2, "Porto: depois de subir ao nível 2, " + above2 + " ofertas deviam chegar esgotadas (chegaram "
			+ seen[1] + ") e a tela devia mostrar o nível 2 (mostrou " + seen[2] + ")");
		server.runOnServer(s -> player(s).closeContainer());
		context.waitTicks(5);
		server.runCommand("gamemode spectator @a");
	}

	/** Ofertas no cliente: quantas, quantas esgotadas e o nível mostrado. */
	private static int[] clientOffers(ClientGameTestContext context) {
		return context.computeOnClient(mc -> {
			check(mc.player.containerMenu instanceof MerchantMenu, "Porto: a tela de troca não abriu no cliente");
			MerchantMenu menu = (MerchantMenu) mc.player.containerMenu;
			MerchantOffers offers = menu.getOffers();
			return new int[] {offers.size(), (int) offers.stream().filter(MerchantOffer::isOutOfStock).count(), menu.getTraderLevel()};
		});
	}

	/** Põe o pagamento da oferta nas casas de troca e devolve o que sai (vazio se o servidor recusar). */
	private static ItemStack resultFor(MerchantMenu menu, MerchantOffer offer, int index) {
		menu.setSelectionHint(index);
		menu.getSlot(0).set(offer.getCostA().copy());
		menu.getSlot(1).set(offer.getCostB().copy());
		ItemStack result = menu.getSlot(2).getItem().copy();
		menu.getSlot(0).set(ItemStack.EMPTY);
		menu.getSlot(1).set(ItemStack.EMPTY);
		return result;
	}

	/** Alerta de Moréia: o mundo plano não tem mar, então o teste põe um tanque de água fora da Cúpula. */
	private static void alert(ClientGameTestContext context, TestServerContext server, BlockPos bell) {
		BlockPos tank = bell.offset(18, 7, 0);
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow", tank.getX() - 3, tank.getY() - 3, tank.getZ() - 3,
			tank.getX() + 3, tank.getY() + 3, tank.getZ() + 3));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", tank.getX() - 2, tank.getY() - 2, tank.getZ() - 2,
			tank.getX() + 2, tank.getY() + 2, tank.getZ() + 2));
		server.runCommand("gamemode survival @a");
		tp(server, bell.getX() + 0.5, bell.getY(), bell.getZ() + 3.5, 0, 0);
		context.waitTicks(5);
		int scalesBefore = server.computeOnServer(s -> player(s).getInventory().countItem(ModItems.ELECTRIC_SCALE));
		int pointsBefore = server.computeOnServer(s -> HarborReputation.get(s).points(player(s).getUUID()));
		server.runCommand(String.format("execute positioned %d %d %d run aquamarine harbor alert", bell.getX(), bell.getY(), bell.getZ()));
		context.waitTicks(5);
		MorayEntity moray = server.computeOnServer(s -> s.overworld().getEntitiesOfClass(MorayEntity.class, new AABB(bell).inflate(40),
			m -> m.entityTags().contains(HarborAlert.TAG)).stream().findFirst().orElse(null));
		check(moray != null, "Alerta: a Moréia do Alerta não surgiu");
		check(HarborAlert.activeCount() == 1, "Alerta: devia haver 1 Alerta em andamento, há " + HarborAlert.activeCount());
		check(server.computeOnServer(s -> s.overworld().getBlockEntity(bell) instanceof BellBlockEntity b && b.shaking), "Alerta: o Sino não tocou");
		server.runOnServer(s -> moray.hurtServer(s.overworld(), s.overworld().damageSources().playerAttack(player(s)), 10_000.0F));
		context.waitTicks(30);
		int scales = server.computeOnServer(s -> player(s).getInventory().countItem(ModItems.ELECTRIC_SCALE)) - scalesBefore;
		int points = server.computeOnServer(s -> HarborReputation.get(s).points(player(s).getUUID())) - pointsBefore;
		check(scales >= HarborAlert.MIN_SCALES && scales <= HarborAlert.MAX_SCALES, "Alerta: devia dar 2 a 4 Escamas Elétricas, deu " + scales);
		check(points == HarborAlert.REPUTATION, "Alerta: devia dar " + HarborAlert.REPUTATION + " de reputação, deu " + points);
		check(HarborAlert.activeCount() == 0, "Alerta: não terminou com a Moréia morta");
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:air", tank.getX() - 3, tank.getY() - 3, tank.getZ() - 3,
			tank.getX() + 3, tank.getY() + 3, tank.getZ() + 3));
		server.runCommand("gamemode spectator @a");
	}

	private static Villager first(List<Villager> divers, ResourceKey<VillagerProfession> profession) {
		return divers.stream().filter(v -> v.getVillagerData().profession().is(profession)).findFirst().orElseThrow();
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

	private HarborGameTest() {}
}
