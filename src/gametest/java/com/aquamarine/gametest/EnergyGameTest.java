package com.aquamarine.gametest;

import com.aquamarine.energy.ModEnergy;
import com.aquamarine.energy.NeutroniumCollectorBlockEntity;
import com.aquamarine.energy.NeutroniumCollectorPartBlockEntity;
import com.aquamarine.energy.WaterEnergy;
import com.aquamarine.energy.WaterGeneratorBlock;
import com.aquamarine.energy.WaterGeneratorBlockEntity;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import com.aquamarine.menu.NeutroniumCollectorMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

/**
 * Fase 6, 6E (docs/11-fase6.md): os três geradores aquáticos produzem no lugar certo, o Cabo Aquático leva a energia
 * até o Coletor de Neutrônio, que junta Pó Estelar, e tudo aparece na API da Team Reborn Energy.
 */
final class EnergyGameTest {
	private EnergyGameTest() {}

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(40, 0, -30);
		int x = o.getX(), y = o.getY(), z = o.getZ();
		BlockPos collectorPos = new BlockPos(x + 5, y + 2, z);
		server.runCommand(String.format("forceload add %d %d %d %d", x - 4, z - 4, x + 10, z + 10));
		server.runCommand("weather clear");
		// Tanque: fenda (magma embaixo, água em cima), três cabos e o Coletor, tudo debaixo da água.
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow", x - 1, y, z - 2, x + 8, y + 4, z + 2));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", x, y + 1, z - 1, x + 7, y + 3, z + 1));
		server.runCommand(String.format("fillbiome %d %d %d %d %d %d minecraft:deep_ocean", x - 4, y - 4, z - 4, x + 10, y + 8, z + 10));
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			level.setBlockAndUpdate(new BlockPos(x, y + 1, z), Blocks.MAGMA_BLOCK.defaultBlockState());
			level.setBlockAndUpdate(new BlockPos(x, y + 2, z), ModEnergy.VENT_GENERATOR.defaultBlockState());
			for (int i = 1; i <= 3; i++) {
				level.setBlockAndUpdate(new BlockPos(x + i, y + 2, z), com.aquamarine.network.ModNetworks.AQUATIC_CABLE.defaultBlockState());
			}
			level.setBlockAndUpdate(collectorPos, ModEnergy.NEUTRONIUM_COLLECTOR.defaultBlockState());
			// Turbina com água corrente caindo ao lado; Boia com água embaixo e céu aberto.
			level.setBlockAndUpdate(new BlockPos(x, y, z + 6), ModEnergy.CURRENT_TURBINE.defaultBlockState());
			level.setBlockAndUpdate(new BlockPos(x, y + 1, z + 5), Blocks.WATER.defaultBlockState());
			level.setBlockAndUpdate(new BlockPos(x + 4, y - 1, z + 6), Blocks.WATER.defaultBlockState());
			level.setBlockAndUpdate(new BlockPos(x + 4, y, z + 6), ModEnergy.WAVE_BUOY.defaultBlockState());
		});
		// A chuva sobe e desce aos poucos (cerca de 100 ticks): espera o tempo limpo assentar.
		context.waitTicks(140);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			for (BlockPos p : new BlockPos[] {new BlockPos(x, y + 2, z), new BlockPos(x + 1, y + 2, z), new BlockPos(x + 4, y + 2, z),
				new BlockPos(x, y, z + 6), new BlockPos(x + 4, y, z + 6)}) {
				check(EnergyStorage.SIDED.find(level, p, Direction.UP) != null, "Energia: sem EnergyStorage da Team Reborn em " + p);
			}
			check(gen(level, new BlockPos(x, y + 2, z)).lastRate() == WaterEnergy.VENT_RATE, "Fenda: devia gerar 32 E/t");
			check(gen(level, new BlockPos(x, y, z + 6)).lastRate() > 0, "Turbina: devia gerar com a água corrente ao lado");
			long buoy = gen(level, new BlockPos(x + 4, y, z + 6)).lastRate();
			check(buoy == WaterEnergy.BUOY_RATE, "Boia: devia gerar 4 E/t no tempo limpo, gera " + buoy);
			check(level.getBlockState(new BlockPos(x, y + 2, z)).getValue(WaterGeneratorBlock.ACTIVE), "Fenda: devia ficar acesa");
			NeutroniumCollectorBlockEntity collector = collector(level, collectorPos);
			check(collector.energy.amount > 0, "Cabo: a energia da Fenda devia chegar ao Coletor");
			int structure = 0;
			for (int dy = 0; dy < 2; dy++) for (int dz = -1; dz <= 1; dz++) for (int dx = -1; dx <= 1; dx++) {
				if (level.getBlockState(collectorPos.offset(dx, dy, dz)).is(ModEnergy.NEUTRONIUM_COLLECTOR)
					|| level.getBlockState(collectorPos.offset(dx, dy, dz)).is(ModEnergy.NEUTRONIUM_COLLECTOR_PART)) structure++;
			}
			check(structure == 18, "Coletor: a migração/colocação devia montar 18 blocos, montou " + structure);
			check(level.getBlockEntity(new BlockPos(x + 4, y + 2, z)) instanceof NeutroniumCollectorPartBlockEntity,
				"Coletor: a face ligada ao Cabo devia ser uma parte externa");
			// O mundo plano é raso: o Coletor recusa o lugar e não trabalha.
			check(collector.progress() == 0, "Coletor: não devia trabalhar fora do fundo do oceano");
		});
		server.runCommand("weather thunder");
		context.waitTicks(140);
		check(server.computeOnServer(s -> WaterGeneratorBlockEntity.rate(s.overworld(), new BlockPos(x + 4, y, z + 6), WaterGeneratorBlock.Kind.BUOY))
			== WaterEnergy.BUOY_RATE * 4, "Boia: a tempestade devia quadruplicar");
		server.runCommand("weather clear");
		// Com o lugar liberado só para o teste, o Coletor termina um Pó Estelar.
		server.runOnServer(s -> {
			NeutroniumCollectorBlockEntity.forceValidSpot = true;
			NeutroniumCollectorBlockEntity collector = collector(s.overworld(), collectorPos);
			collector.energy.amount = collector.energy.capacity;
			collector.setProgress(WaterEnergy.COLLECTOR_TICKS - 3);
		});
		context.waitTicks(40);
		server.runOnServer(s -> {
			NeutroniumCollectorBlockEntity.forceValidSpot = false;
			NeutroniumCollectorBlockEntity collector = collector(s.overworld(), collectorPos);
			check(collector.dust() == 1, "Coletor: devia juntar 1 Pó Estelar, tem " + collector.dust());
			EnergyStorage outside = EnergyStorage.SIDED.find(s.overworld(), new BlockPos(x + 4, y + 2, z), Direction.WEST);
			check(outside == collector.energy, "Coletor: EnergyStorage da parte devia repassar ao controlador");
			collector.energy.amount = 0;
			try (Transaction tx = Transaction.openOuter()) { outside.insert(512, tx); tx.commit(); }
			check(collector.energy.amount == 512, "Coletor: entrada pela parte externa devia aceitar 512 E/t");
			net.minecraft.server.level.ServerPlayer player = s.getPlayerList().getPlayers().get(0);
			NeutroniumCollectorPartBlockEntity clickedPart = (NeutroniumCollectorPartBlockEntity) s.overworld()
				.getBlockEntity(new BlockPos(x + 4, y + 2, z));
			player.openMenu(clickedPart.controller());
			check(player.containerMenu instanceof NeutroniumCollectorMenu, "Coletor: clicar numa parte devia abrir a tela própria");
			NeutroniumCollectorMenu menu = (NeutroniumCollectorMenu) player.containerMenu;
			check(menu.energy() == 512 && menu.capacity() == 100_000 && menu.dust() == 1,
				"Coletor: dados da tela não batem com o controlador");
			check(menu.getSlot(0).remove(1).getCount() == 1 && collector.dust() == 0,
				"Coletor: retirar pelo slot devia esvaziar um Pó Estelar");
			player.closeContainer();
		});
		// Estrutura separada: uma parte quebrada desmonta tudo e deixa exatamente um item com o pó preservado.
		BlockPos breakCollector = new BlockPos(x + 14, y, z + 12);
		server.runOnServer(s -> {
			NeutroniumCollectorBlockEntity.forceValidSpot = true;
			s.overworld().setBlockAndUpdate(breakCollector, ModEnergy.NEUTRONIUM_COLLECTOR.defaultBlockState());
		});
		context.waitTicks(3);
		server.runOnServer(s -> {
			NeutroniumCollectorBlockEntity be = collector(s.overworld(), breakCollector);
			be.energy.amount = be.energy.capacity;
			be.setProgress(WaterEnergy.COLLECTOR_TICKS - 1);
		});
		context.waitTicks(3);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			NeutroniumCollectorBlockEntity.forceValidSpot = false;
			check(collector(level, breakCollector).dust() == 1, "Coletor: estrutura de quebra devia guardar um pó");
			level.destroyBlock(breakCollector.east(), false, null, 512);
		});
		context.waitTicks(2);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			int left = 0;
			for (int dy = 0; dy < 2; dy++) for (int dz = -1; dz <= 1; dz++) for (int dx = -1; dx <= 1; dx++) {
				if (level.getBlockState(breakCollector.offset(dx, dy, dz)).is(ModEnergy.NEUTRONIUM_COLLECTOR)
					|| level.getBlockState(breakCollector.offset(dx, dy, dz)).is(ModEnergy.NEUTRONIUM_COLLECTOR_PART)) left++;
			}
			check(left == 0, "Coletor: quebrar uma parte devia remover os 18 blocos, sobraram " + left);
			var drops = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
				new net.minecraft.world.phys.AABB(breakCollector).inflate(4), e -> e.getItem().is(ModEnergy.NEUTRONIUM_COLLECTOR.asItem()));
			check(drops.size() == 1, "Coletor: devia soltar exatamente um item, soltou " + drops.size());
			var stored = drops.get(0).getItem().get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
			check(stored != null && stored.copyTagWithoutId().getIntOr("dust", 0) == 1,
				"Coletor: o item único devia conservar o Pó Estelar");
		});
		// Migração de save antigo sem espaço: o controlador não deixa uma meia máquina no mundo.
		BlockPos blockedLegacy = breakCollector.east(7);
		server.runOnServer(s -> {
			s.overworld().setBlockAndUpdate(blockedLegacy.east(), Blocks.STONE.defaultBlockState());
			s.overworld().setBlockAndUpdate(blockedLegacy, ModEnergy.NEUTRONIUM_COLLECTOR.defaultBlockState());
		});
		context.waitTicks(3);
		check(server.computeOnServer(s -> !s.overworld().getBlockState(blockedLegacy).is(ModEnergy.NEUTRONIUM_COLLECTOR)),
			"Coletor antigo sem espaço devia quebrar e soltar o item");
		// 6F1: a Mesa de Encantamento Aquática abre a tela do jogo, continua aberta e só sorteia encantamentos da água.
		BlockPos table = new BlockPos(x + 8, y, z + 6);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			level.setBlockAndUpdate(table, com.aquamarine.block.ModBlocks.AQUATIC_ENCHANTING_TABLE.defaultBlockState());
			net.minecraft.server.level.ServerPlayer p = s.getPlayerList().getPlayers().get(0);
			p.teleportTo(table.getX() + 0.5, table.getY() + 1.0, table.getZ() - 1.5);
			p.openMenu(level.getBlockState(table).getMenuProvider(level, table));
			check(p.containerMenu instanceof net.minecraft.world.inventory.EnchantmentMenu, "Mesa aquática: não abriu a tela de encantar");
			p.containerMenu.getSlot(0).set(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_HELMET));
			p.containerMenu.getSlot(1).set(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.LAPIS_LAZULI, 3));
		});
		context.waitTicks(10);
		server.runOnServer(s -> {
			net.minecraft.server.level.ServerPlayer p = s.getPlayerList().getPlayers().get(0);
			check(p.containerMenu instanceof net.minecraft.world.inventory.EnchantmentMenu, "Mesa aquática: a tela fechou sozinha");
			net.minecraft.world.inventory.EnchantmentMenu menu = (net.minecraft.world.inventory.EnchantmentMenu) p.containerMenu;
			var registry = s.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
			int offers = 0;
			for (int clue : menu.enchantClue) {
				if (clue >= 0) {
					offers++;
					check(registry.get(clue).filter(h -> h.is(com.aquamarine.block.AquaticEnchantingTableBlock.AQUATIC_ENCHANTMENTS)).isPresent(),
						"Mesa aquática: ofereceu um encantamento que não é da água");
				}
			}
			check(offers > 0, "Mesa aquática: devia oferecer encantamentos para o capacete");
			p.closeContainer();
		});
		// 6C2: as três mesas da Vila abrem as telas do jogo e continuam abertas; a Bigorna de Maré não aumenta o custo.
		BlockPos stations = new BlockPos(x + 10, y, z + 6);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			level.setBlockAndUpdate(stations, com.aquamarine.block.ModBlocks.TIDE_ANVIL.defaultBlockState());
			level.setBlockAndUpdate(stations.east(), com.aquamarine.block.ModBlocks.CHART_TABLE.defaultBlockState());
			level.setBlockAndUpdate(stations.east(2), com.aquamarine.block.ModBlocks.SHELL_ALTAR.defaultBlockState());
		});
		for (int i = 0; i < 3; i++) {
			BlockPos at = stations.east(i);
			server.runOnServer(s -> {
				net.minecraft.server.level.ServerPlayer p = s.getPlayerList().getPlayers().get(0);
				p.teleportTo(at.getX() + 0.5, at.getY() + 1.0, at.getZ() - 1.5);
				p.openMenu(s.overworld().getBlockState(at).getMenuProvider(s.overworld(), at));
			});
			context.waitTicks(5);
			int n = i;
			server.runOnServer(s -> {
				net.minecraft.server.level.ServerPlayer p = s.getPlayerList().getPlayers().get(0);
				Class<?> want = n == 0 ? net.minecraft.world.inventory.AnvilMenu.class : n == 1
					? net.minecraft.world.inventory.CartographyTableMenu.class : net.minecraft.world.inventory.GrindstoneMenu.class;
				check(want.isInstance(p.containerMenu), "Mesa da Vila " + n + ": devia abrir " + want.getSimpleName() + " e continuar aberta");
				if (n == 0) {
					net.minecraft.world.item.ItemStack sword = new net.minecraft.world.item.ItemStack(com.aquamarine.item.ModItems.ABYSSAL_SWORD);
					sword.setDamageValue(500);
					p.containerMenu.getSlot(0).set(sword);
					p.containerMenu.getSlot(1).set(new net.minecraft.world.item.ItemStack(com.aquamarine.item.ModItems.ABYSSAL_INGOT));
					p.containerMenu.slotsChanged(null);
					net.minecraft.world.item.ItemStack out = p.containerMenu.getSlot(2).getItem();
					check(!out.isEmpty() && out.getOrDefault(net.minecraft.core.component.DataComponents.REPAIR_COST, 0) == 0,
						"Bigorna de Maré: o reparo não devia aumentar o custo, ficou " + out.getOrDefault(net.minecraft.core.component.DataComponents.REPAIR_COST, -1));
					p.containerMenu.getSlot(0).set(net.minecraft.world.item.ItemStack.EMPTY);
					p.containerMenu.getSlot(1).set(net.minecraft.world.item.ItemStack.EMPTY);
				}
				p.closeContainer();
			});
		}
		// Capturas da tela própria nos quatro estados relevantes; o mesmo menu permanece aberto para provar atualização ao vivo.
		server.runOnServer(s -> {
			NeutroniumCollectorBlockEntity.forceValidSpot = false;
			NeutroniumCollectorBlockEntity be = collector(s.overworld(), collectorPos);
			be.energy.amount = 0;
			be.setProgress(0);
		});
		context.waitTicks(22);
		server.runOnServer(s -> s.getPlayerList().getPlayers().get(0).openMenu(collector(s.overworld(), collectorPos)));
		context.waitTicks(5);
		check(context.computeOnClient(mc -> mc.gui.screen() instanceof com.aquamarine.client.screen.NeutroniumCollectorScreen),
			"Coletor: a tela personalizada não abriu no cliente");
		collectorShot(context, "coletor_tela_00_local_invalido");
		server.runOnServer(s -> {
			NeutroniumCollectorBlockEntity.forceValidSpot = true;
			NeutroniumCollectorBlockEntity be = collector(s.overworld(), collectorPos);
			be.energy.amount = 0;
			be.setProgress(0);
		});
		context.waitTicks(22);
		collectorShot(context, "coletor_tela_01_idle");
		server.runOnServer(s -> {
			NeutroniumCollectorBlockEntity be = collector(s.overworld(), collectorPos);
			be.energy.amount = be.energy.capacity;
			be.setProgress(WaterEnergy.COLLECTOR_TICKS / 2);
		});
		context.waitTicks(5);
		collectorShot(context, "coletor_tela_02_run");
		for (int i = 0; i < WaterEnergy.COLLECTOR_MAX_DUST; i++) {
			server.runOnServer(s -> {
				NeutroniumCollectorBlockEntity be = collector(s.overworld(), collectorPos);
				be.energy.amount = be.energy.capacity;
				be.setProgress(WaterEnergy.COLLECTOR_TICKS - 1);
			});
			context.waitTicks(2);
		}
		context.waitTicks(3);
		collectorShot(context, "coletor_tela_03_full");
		server.runOnServer(s -> {
			NeutroniumCollectorBlockEntity.forceValidSpot = false;
			s.getPlayerList().getPlayers().get(0).closeContainer();
		});
		// 6G2: ligar o Radar gasta uma Bobina e o deixa ativo por 60 s.
		server.runOnServer(s -> {
			net.minecraft.server.level.ServerPlayer p = s.getPlayerList().getPlayers().get(0);
			p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
			p.getInventory().clearContent();
			p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(com.aquamarine.item.ModItems.AQUATIC_RADAR));
			p.getInventory().add(new net.minecraft.world.item.ItemStack(com.aquamarine.item.ModItems.VOLTAIC_COIL, 2));
			p.getMainHandItem().use(s.overworld(), p, net.minecraft.world.InteractionHand.MAIN_HAND);
			check(com.aquamarine.item.AquaticRadarItem.activeUntil(p.getMainHandItem()) > s.overworld().getGameTime(), "Radar: devia ligar");
			check(p.getInventory().countItem(com.aquamarine.item.ModItems.VOLTAIC_COIL) == 1, "Radar: devia gastar uma Bobina");
			p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
		});
		tp(server, x + 2.5, y + 2.0, z - 5.5, 0, 20);
		world.getConnection().waitForChunksRender();
		context.waitTicks(10);
		context.runOnClient(mc -> mc.gui.toastManager().clear());
		context.waitTick();
		context.takeScreenshot("energia_00_geradores");
	}

	private static WaterGeneratorBlockEntity gen(ServerLevel level, BlockPos pos) {
		check(level.getBlockEntity(pos) instanceof WaterGeneratorBlockEntity, "Energia: sem gerador em " + pos);
		return (WaterGeneratorBlockEntity) level.getBlockEntity(pos);
	}

	private static NeutroniumCollectorBlockEntity collector(ServerLevel level, BlockPos pos) {
		check(level.getBlockEntity(pos) instanceof NeutroniumCollectorBlockEntity, "Energia: sem Coletor em " + pos);
		return (NeutroniumCollectorBlockEntity) level.getBlockEntity(pos);
	}

	private static void tp(TestServerContext server, double x, double y, double z, float yaw, float pitch) {
		server.runCommand(String.format(java.util.Locale.ROOT, "tp @p %.2f %.2f %.2f %.1f %.1f", x, y, z, yaw, pitch));
	}

	private static void collectorShot(ClientGameTestContext context, String name) {
		context.runOnClient(mc -> {
			mc.gui.toastManager().clear();
			mc.gui.hud.getChat().clearMessages(false);
		});
		context.waitTick();
		context.takeScreenshot(name);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
