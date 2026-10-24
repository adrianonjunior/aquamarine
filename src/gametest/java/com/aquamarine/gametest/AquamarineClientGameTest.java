package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.ModBlocks;
import com.aquamarine.client.compat.jei.AquamarineJeiPlugin;
import com.aquamarine.client.screen.AbyssalForgeScreen;
import com.aquamarine.command.KrakenCommand;
import com.aquamarine.entity.BossDummy;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.boss.ShieldDamage;
import com.aquamarine.event.KrakenSummoning;
import net.minecraft.world.entity.EntitySpawnReason;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.item.ModCreativeTab;
import com.aquamarine.item.ModItems;
import com.aquamarine.menu.AbyssalForgeMenu;

import java.util.List;

import net.fabricmc.fabric.api.client.creativetab.v1.FabricCreativeModeInventoryScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.client.CameraType;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Teste de cliente da Entrega 1: abre um mundo plano, monta cenas e tira screenshots em
 * {@code build/run/clientGameTest/screenshots/}. Rodar com {@code ./gradlew runClientGameTest}
 * (abre uma janela do jogo; não roda no CI). Também confere por código a Forja, a receita
 * exclusiva e o bônus do conjunto de Oricálcio.
 */
public class AquamarineClientGameTest implements FabricClientGameTest {
	private static final List<Item> HOTBAR = List.of(ModItems.ORICHALCUM_INGOT, ModItems.ABYSSAL_PEARL, ModItems.TIDE_CRYSTAL,
		ModItems.ANCIENT_SCALE, ModItems.ORICHALCUM_SWORD, ModItems.ORICHALCUM_PICKAXE, ModItems.ORICHALCUM_AXE,
		ModItems.ORICHALCUM_SHOVEL, ModItems.DIVING_GOGGLES);

	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			TestServerContext server = world.getServer();
			world.getConnection().waitForChunksRender();
			server.runCommand("time set noon");
			server.runCommand("weather clear");
			server.runCommand("gamemode creative @a");
			BlockPos base = server.computeOnServer(s -> player(s).blockPosition());
			check(server.computeOnServer(s -> s.registryAccess().lookupOrThrow(Registries.BIOME)
				.get(TagKey.create(Registries.BIOME, Identifier.withDefaultNamespace("aquamarine_fossa_de_nereia")))
				.map(set -> set.size() > 0).orElse(false)), "a tag #aquamarine_fossa_de_nereia não carregou");

			showcase(context, world, server, base);
			creativeTab(context, world);
			forge(context, world, server, base);
			underwater(context, world, server, base);
			bossBar(context, world, server, base);
			altar(context, world, server, base);
			kraken(context, world, server, base);
		}
	}

	private static void useLure(MinecraftServer s, BlockPos altar) {
		ServerPlayer player = player(s);
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(altar).add(0, 0.5, 0), Direction.UP, altar, false);
		player.gameMode.useItemOn(player, s.overworld(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
	}

	/** Altar do Abismo: recusa sem água funda (sem gastar o Chamariz) e invoca com água funda. */
	private static void altar(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos altar = base.offset(6, 0, -6);
		server.runCommand("gamemode survival @a");
		server.runOnServer(s -> {
			s.overworld().setBlockAndUpdate(altar, ModBlocks.ABYSSAL_ALTAR.defaultBlockState());
			ServerPlayer player = player(s);
			player.getInventory().setSelectedSlot(0);
			player.getInventory().setItem(0, new ItemStack(ModItems.ABYSSAL_LURE));
		});
		tp(server, altar.getX() + 0.5, altar.getY(), altar.getZ() - 2.5, 0, 25);
		world.getConnection().waitForChunksRender();
		context.runOnClient(mc -> mc.gui.hud.getChat().clearMessages(false));
		server.runOnServer(s -> useLure(s, altar));
		context.waitTicks(5);
		check(server.computeOnServer(s -> player(s).getMainHandItem().is(ModItems.ABYSSAL_LURE)), "o Chamariz foi gasto sem água funda");
		shot(context, "13_altar_aguas_rasas");

		// Coluna de água de 30 blocos ao lado (dentro do raio de 48 e na grade de amostragem).
		BlockPos pool = altar.offset(8, 0, 0);
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow",
			pool.getX() - 2, pool.getY() - 1, pool.getZ() - 2, pool.getX() + 2, pool.getY() + 30, pool.getZ() + 2));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water",
			pool.getX() - 1, pool.getY(), pool.getZ() - 1, pool.getX() + 1, pool.getY() + 29, pool.getZ() + 1));
		// sem tampa: a superfície tem que ser água aberta
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:air",
			pool.getX() - 1, pool.getY() + 30, pool.getZ() - 1, pool.getX() + 1, pool.getY() + 30, pool.getZ() + 1));
		context.waitTicks(2);
		server.runOnServer(s -> useLure(s, altar));
		context.waitTicks(2);
		check(server.computeOnServer(s -> player(s).getMainHandItem().isEmpty()), "o Chamariz não foi consumido");
		context.waitTicks(205);
		shot(context, "14_altar_kraken_despertou");
		server.runCommand("gamemode creative @a");
	}

	private static BossDummy dummy(MinecraftServer server) {
		return server.overworld().getEntities(ModEntities.BOSS_DUMMY, e -> true).getFirst();
	}

	/** Barra de vida customizada com a entidade de teste: cheia, escudo rachado, quebrando, fases. */
	private static void bossBar(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.getAbilities().flying = false;
			player.onUpdateAbilities();
		});
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() - 2.5, 180, 0);
		server.runCommand(String.format("summon aquamarine:boss_dummy %d %d %d", base.getX(), base.getY(), base.getZ() - 8));
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		shot(context, "08_barra_cheia");

		String hit = "damage @e[type=aquamarine:boss_dummy,limit=1] ";
		server.runCommand(hit + "70");
		context.waitTicks(4);
		check(server.computeOnServer(s -> dummy(s).getHealth() == BossDummy.MAX_HEALTH), "o escudo deixou dano passar para a vida");
		shot(context, "09_escudo_rachado");

		server.runCommand(hit + "60");
		context.waitTicks(2);
		check(server.computeOnServer(s -> dummy(s).shield() == 0), "o escudo não quebrou");
		shot(context, "10_escudo_quebrando");

		context.waitTicks(15);
		server.runCommand(hit + "120");
		// O pacote leva alguns ticks para chegar ao cliente; a barra fantasma segura 14 ticks.
		context.waitTicks(8);
		check(server.computeOnServer(s -> dummy(s).phase() == 2), "a entidade de teste não foi para a fase 2");
		shot(context, "11_fase2_dano_recente");

		server.runOnServer(s -> dummy(s).setHealth(60));
		context.waitTicks(30);
		shot(context, "12_fase3_pulso");
		server.runCommand("kill @e[type=aquamarine:boss_dummy]");
	}

	private static KrakenEntity kraken(MinecraftServer server) {
		return server.overworld().getEntities(ModEntities.KRAKEN, e -> true).getFirst();
	}

	private static boolean near(float a, float b) {
		return Math.abs(a - b) < 0.5F;
	}

	private static void krakenHit(TestServerContext server, float damage) {
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			// Mão vazia: o escudo recebe 25% do dano (tabela de 2C); multiplica por 4 para manter os números.
			float raw = kraken(s).shield() > 0 ? damage * 4.0F : damage;
			kraken(s).hurtServer(level, level.damageSources().playerAttack(player(s)), raw);
		});
	}

	/** Kraken: vida e escudo por fase, escala, transição, regeneração do escudo e aggro. */
	private static void kraken(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos origin = base.offset(200, 0, 0);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			level.getChunkAt(origin);
			KrakenCommand.buildArena(level, origin);
			KrakenSummoning.spawnAt(level, origin.offset(0, 25, 0), origin);
		});
		tp(server, origin.getX() + 0.5, origin.getY() + 28, origin.getZ() + 14.5, 0, 0);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		check(server.computeOnServer(s -> kraken(s).realMaxHealth() == 1500F && kraken(s).shield() == 400F
			&& kraken(s).phase() == 1 && kraken(s).scaleFactor() == 1F), "Kraken: valores da fase 1 com 1 jogador");
		check(server.computeOnServer(s -> near(kraken(s).realHealth(), 1500F)), "Kraken: vida inicial");
		shot(context, "13_kraken_fase1");
		for (String name : KrakenEntity.ANIMATION_NAMES) {
			server.runCommand("aquamarine kraken anim " + name);
			check(server.computeOnServer(s -> KrakenEntity.ANIMATION_NAMES.get(kraken(s).lastAnimation()).equals(name)), "Kraken: animação " + name);
		}

		krakenHit(server, 500);
		context.waitTicks(2);
		check(server.computeOnServer(s -> kraken(s).shield() == 0 && kraken(s).isStunned() && near(kraken(s).realHealth(), 1500F)),
			"Kraken: o escudo deve absorver e atordoar sem tirar vida");
		check(server.computeOnServer(s -> player(s).getUUID().equals(kraken(s).aggroTarget())), "Kraken: aggro no jogador que bateu");

		krakenHit(server, 700);
		context.waitTicks(2);
		check(server.computeOnServer(s -> kraken(s).phase() == 2 && near(kraken(s).realHealth(), 1000F) && kraken(s).shield() == 600F
			&& kraken(s).isTransitioning()), "Kraken: o golpe deve parar na fronteira da fase 2 com escudo cheio");
		krakenHit(server, 50);
		check(server.computeOnServer(s -> near(kraken(s).realHealth(), 1000F) && kraken(s).shield() == 600F), "Kraken: invulnerável na transição");
		shot(context, "14_kraken_fase2");

		context.waitTicks(70);
		check(server.computeOnServer(s -> !kraken(s).isTransitioning()), "Kraken: a transição devia ter acabado");
		krakenHit(server, 150);
		context.waitTicks(450);
		check(server.computeOnServer(s -> kraken(s).shield() == 600F), "Kraken: o escudo devia regenerar após 20 s sem dano");

		krakenHit(server, 600);
		krakenHit(server, 600);
		context.waitTicks(2);
		check(server.computeOnServer(s -> kraken(s).phase() == 3 && near(kraken(s).realHealth(), 500F) && kraken(s).shield() == 800F),
			"Kraken: fase 3 com 800 de escudo");
		shot(context, "15_kraken_fase3");

		context.waitTicks(70);
		// 2C: tabela de dano por fonte, Nós de Maré e exposição (escudo cheio de 800 na fase 3).
		check(server.computeOnServer(s -> kraken(s).nodeCount() == 4 && kraken(s).nodeCenters().stream().allMatch(c -> c.y < kraken(s).getY())),
			"Kraken: 4 Nós de Maré sob o corpo na fase 3");
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			KrakenEntity k = kraken(s);
			ServerPlayer p = player(s);
			k.hitShield(level, ShieldDamage.Source.COMMON, 40, p, false, 0);
			check(k.shield() == 790F, "Kraken: arma comum causa 25% no escudo");
			k.hitShield(level, ShieldDamage.Source.HARPOON, 10, p, true, 0);
			check(k.shield() == 730F, "Kraken: arpão (3x) em nó (2x) causa 60");
			k.hitShield(level, ShieldDamage.Source.PULSE, 0, p, true, 0);
			check(k.shield() == 630F, "Kraken: Carga de Pulso tira 100 fixos");
			k.expose(20);
			k.hitShield(level, ShieldDamage.Source.PULSE, 0, p, false, 0);
			check(k.shield() == 505F, "Kraken: exposto leva +25%");
		});

		server.runOnServer(s -> {
			KrakenEntity big = ModEntities.KRAKEN.create(s.overworld(), EntitySpawnReason.COMMAND);
			big.setUp(s.overworld(), 5);
			check(big.realMaxHealth() == 1500F * 2.2F && big.maxShield() == 400F * 2.2F && big.scaleFactor() == 2.2F, "Kraken: escala f com 5 jogadores");
		});
		server.runCommand("kill @e[type=aquamarine:kraken]");
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
	}

	/** Screenshot sem os avisos de "receita desbloqueada" cobrindo o canto da tela. */
	private static void shot(ClientGameTestContext context, String name) {
		context.runOnClient(mc -> mc.gui.toastManager().clear());
		context.waitTick();
		context.takeScreenshot(name);
	}

	private static ServerPlayer player(MinecraftServer server) {
		return server.getPlayerList().getPlayers().getFirst();
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void tp(TestServerContext server, double x, double y, double z, float yaw, float pitch) {
		server.runCommand(String.format(java.util.Locale.ROOT, "tp @p %.2f %.2f %.2f %.1f %.1f", x, y, z, yaw, pitch));
	}

	private static void setArmor(ServerPlayer player, Item head) {
		player.setItemSlot(EquipmentSlot.HEAD, head == null ? ItemStack.EMPTY : new ItemStack(head));
		player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.ORICHALCUM_CHESTPLATE));
		player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.ORICHALCUM_LEGGINGS));
		player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.ORICHALCUM_BOOTS));
	}

	/** Vitrine: blocos, suportes de armadura vestidos e a barra de itens. */
	private static void showcase(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			BlockState forge = ModBlocks.ABYSSAL_FORGE.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);
			level.setBlockAndUpdate(base.offset(-2, 0, 5), forge);
			level.setBlockAndUpdate(base.offset(0, 0, 5), ModBlocks.ORICHALCUM_ORE.defaultBlockState());
			level.setBlockAndUpdate(base.offset(2, 0, 5), ModBlocks.ORICHALCUM_BLOCK.defaultBlockState());

			spawnStand(level, base.offset(-1, 0, 3), ModItems.ORICHALCUM_HELMET, ModItems.ORICHALCUM_SWORD);
			spawnStand(level, base.offset(1, 0, 3), ModItems.DIVING_GOGGLES, ModItems.ORICHALCUM_PICKAXE);

			ServerPlayer player = player(s);
			for (int i = 0; i < HOTBAR.size(); i++) {
				player.getInventory().setItem(i, new ItemStack(HOTBAR.get(i)));
			}
		});
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 20);
		world.getConnection().waitForChunksRender();
		context.waitTicks(10);
		shot(context, "01_vitrine");

		// Jogador vestindo a armadura completa, câmera de frente, vitrine ao fundo.
		server.runOnServer(s -> setArmor(player(s), ModItems.ORICHALCUM_HELMET));
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() - 1.5, 180, 10);
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
		context.waitTicks(25);
		shot(context, "02_jogador_armadura");
		check(server.computeOnServer(s -> player(s).getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY)
			.hasModifier(Aquamarine.id("orichalcum_set_swim"))), "bônus de nado do conjunto completo não aplicado");

		server.runOnServer(s -> setArmor(player(s), ModItems.DIVING_GOGGLES));
		context.waitTicks(25);
		shot(context, "03_jogador_oculos");
		check(!server.computeOnServer(s -> player(s).getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY)
			.hasModifier(Aquamarine.id("orichalcum_set_swim"))), "bônus de nado continuou sem o capacete");
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.FIRST_PERSON));
	}

	private static void spawnStand(ServerLevel level, BlockPos pos, Item head, Item hand) {
		ArmorStand stand = new ArmorStand(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		stand.setYRot(180);
		stand.setYBodyRot(180);
		stand.setShowArms(true);
		stand.setNoBasePlate(true);
		stand.setItemSlot(EquipmentSlot.HEAD, new ItemStack(head));
		stand.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.ORICHALCUM_CHESTPLATE));
		stand.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.ORICHALCUM_LEGGINGS));
		stand.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.ORICHALCUM_BOOTS));
		stand.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(hand));
		level.addFreshEntity(stand);
	}

	private static void creativeTab(ClientGameTestContext context, TestSingleplayerContext world) {
		context.setScreen(() -> new CreativeModeInventoryScreen(world.getConnection().getClientPlayer(),
			world.getConnection().getClientLevel().enabledFeatures(), false));
		context.waitForScreen(CreativeModeInventoryScreen.class);
		context.runOnClient(mc -> ((FabricCreativeModeInventoryScreen) mc.gui.screen())
			.setSelectedTab(BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(ModCreativeTab.KEY)));
		context.waitTicks(5);
		shot(context, "04_aba_criativa");

		// Tooltip com a descrição (cursor sobre a picareta, 9º item da 1ª linha) e, com o JEI,
		// os usos da Forja (tecla U sobre ela, 7º item). Posições em pixels da janela 854x480.
		context.getInput().setCursorPos(265 + 8 * 36, 155);
		context.waitTicks(3);
		shot(context, "04b_tooltip");
		context.getInput().setCursorPos(265 + 6 * 36, 155);
		context.waitTicks(2);
		shot(context, "04c_tooltip_forja");
		context.runOnClient(mc -> AquamarineJeiPlugin.runtime().orElseThrow(() -> new AssertionError("JEI não carregou"))
			.getRecipesGui().showTypes(List.of(AquamarineJeiPlugin.ABYSSAL_FORGING)));
		context.waitTicks(10);
		shot(context, "04d_jei_forja");
		context.setScreen(() -> null);
	}

	/** Forja: monta uma picareta só aceita na Forja e confere que a bancada comum não a faz. */
	private static void forge(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos forgePos = base.offset(-2, 0, 5);
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			ServerLevel level = s.overworld();
			BlockState state = level.getBlockState(forgePos);
			player.openMenu(state.getMenuProvider(level, forgePos));
			check(player.containerMenu instanceof AbyssalForgeMenu, "a Forja não abriu o menu certo");
			ItemStack ingot = new ItemStack(ModItems.ORICHALCUM_INGOT);
			ItemStack stick = new ItemStack(Items.STICK);
			ItemStack[] grid = {ingot, ingot, ingot, ItemStack.EMPTY, stick, ItemStack.EMPTY, ItemStack.EMPTY, stick, ItemStack.EMPTY};
			for (int i = 0; i < grid.length; i++) {
				player.containerMenu.getSlot(1 + i).set(grid[i].copy());
			}
			player.containerMenu.broadcastChanges();
			check(player.containerMenu.getSlot(0).getItem().is(ModItems.ORICHALCUM_PICKAXE), "a Forja não montou a picareta");

			CraftingInput input = CraftingInput.of(3, 3, List.of(grid));
			check(s.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).isEmpty(),
				"a picareta de Oricálcio não deveria sair da bancada comum");
		});
		context.waitForScreen(AbyssalForgeScreen.class);
		context.waitTicks(5);
		shot(context, "05_forja");

		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.containerMenu.clicked(0, 0, ContainerInput.QUICK_MOVE, player);
			check(player.getInventory().contains(new ItemStack(ModItems.ORICHALCUM_PICKAXE)), "a picareta não foi para o inventário");
			for (int i = 1; i <= 9; i++) {
				check(player.containerMenu.getSlot(i).getItem().isEmpty(), "ingrediente sobrou na grade (slot " + i + ")");
			}
			player.closeContainer();
		});
		context.waitTicks(5);
	}

	/** Tanque de água com colunas de referência: sem e com os Óculos de Mergulho. */
	private static void underwater(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		int x = base.getX();
		int y = base.getY();
		int z = base.getZ() + 10;
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow", x - 12, y - 1, z, x + 12, y + 12, z + 44));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", x - 11, y, z + 1, x + 11, y + 11, z + 43));
		for (int d = 8; d <= 40; d += 8) {
			server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:sea_lantern", x - 3, y, z + d, x - 3, y + 5, z + d));
			server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:prismarine_bricks", x + 3, y, z + d, x + 3, y + 7, z + d));
		}
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
			player.getAbilities().flying = true;
			player.onUpdateAbilities();
		});
		tp(server, x + 0.5, y + 5, z + 2.5, 0, 5);
		world.getConnection().waitForChunksRender();
		context.waitTicks(40);
		shot(context, "06_agua_sem_oculos");

		server.runOnServer(s -> player(s).setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.DIVING_GOGGLES)));
		context.waitTicks(10);
		shot(context, "07_agua_com_oculos");
	}
}
