package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.ModBlocks;
import com.aquamarine.client.screen.AbyssalForgeScreen;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

			showcase(context, world, server, base);
			creativeTab(context, world);
			forge(context, world, server, base);
			underwater(context, world, server, base);
		}
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
		context.takeScreenshot("01_vitrine");

		// Jogador vestindo a armadura completa, câmera de frente, vitrine ao fundo.
		server.runOnServer(s -> setArmor(player(s), ModItems.ORICHALCUM_HELMET));
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() - 1.5, 180, 10);
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
		context.waitTicks(25);
		context.takeScreenshot("02_jogador_armadura");
		check(server.computeOnServer(s -> player(s).getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY)
			.hasModifier(Aquamarine.id("orichalcum_set_swim"))), "bônus de nado do conjunto completo não aplicado");

		server.runOnServer(s -> setArmor(player(s), ModItems.DIVING_GOGGLES));
		context.waitTicks(25);
		context.takeScreenshot("03_jogador_oculos");
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
		context.takeScreenshot("04_aba_criativa");
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
		context.takeScreenshot("05_forja");

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
		context.takeScreenshot("06_agua_sem_oculos");

		server.runOnServer(s -> player(s).setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.DIVING_GOGGLES)));
		context.waitTicks(10);
		context.takeScreenshot("07_agua_com_oculos");
	}
}
