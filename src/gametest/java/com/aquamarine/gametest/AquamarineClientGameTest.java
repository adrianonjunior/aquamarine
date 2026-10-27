package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.AnchorBuoyBlock;
import com.aquamarine.block.ModBuildingBlocks;
import com.aquamarine.block.ModBlocks;
import com.aquamarine.client.compat.jei.AquamarineJeiPlugin;
import com.aquamarine.client.screen.AbyssalForgeScreen;
import com.aquamarine.command.KrakenCommand;
import com.aquamarine.entity.BossDummy;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.boss.KrakenWorldEffects;
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
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
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
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Teste de cliente da Entrega 1: abre um mundo plano, monta cenas e tira screenshots em
 * {@code build/run/clientGameTest/screenshots/}. Rodar com {@code ./gradlew runClientGameTest}
 * (abre uma janela do jogo; não roda no CI). Também confere por código a Forja, a receita
 * exclusiva e o bônus do conjunto Abissal.
 */
public class AquamarineClientGameTest implements FabricClientGameTest {
	private static final List<Item> HOTBAR = List.of(ModItems.ABYSSAL_INGOT, ModItems.ABYSSAL_PEARL, ModItems.TIDE_CRYSTAL,
		ModItems.ANCIENT_SCALE, ModItems.ABYSSAL_SWORD, ModItems.ABYSSAL_PICKAXE, ModItems.ABYSSAL_AXE,
		ModItems.ABYSSAL_SHOVEL, ModItems.DIVING_GOGGLES);

	/**
	 * Só para iterar mais rápido: {@code AQUAMARINE_GAMETEST_ONLY=moray,whale ./gradlew runClientGameTest}
	 * roda só essas partes (nomes nos {@code enabled(...)} abaixo). Sem a variável, roda tudo.
	 */
	private static final String ONLY = System.getenv("AQUAMARINE_GAMETEST_ONLY");

	private static boolean enabled(String name) {
		return ONLY == null || ONLY.isBlank() || List.of(ONLY.split(",")).contains(name);
	}

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

			if (enabled("showcase")) {
				showcase(context, world, server, base);
			}
			if (enabled("blocks")) {
				blockVisuals(context, world, server, base);
			}
			if (enabled("building")) {
				buildingVisuals(context, world, server, base);
			}
			if (enabled("spawnlings")) {
				spawnlingVisuals(context, world, server, base);
			}
			if (enabled("creative")) {
				creativeTab(context, world);
			}
			if (enabled("forge")) {
				forge(context, world, server, base);
			}
			if (enabled("underwater")) {
				underwater(context, world, server, base);
			}
			if (enabled("bossbar")) {
				bossBar(context, world, server, base);
			}
			if (enabled("altar")) {
				altar(context, world, server, base);
			}
			if (enabled("kraken")) {
				kraken(context, world, server, base);
			}
			if (enabled("pressure")) {
				PressureGameTest.run(context, world, server, base);
			}
			if (enabled("depth")) {
				DepthGameTest.run(context, world, server, base);
			}
			if (enabled("moray")) {
				MorayGameTest.run(context, world, server, base);
			}
			if (enabled("whale")) {
				WhaleGameTest.run(context, world, server, base);
			}
			if (enabled("gear")) {
				GearGameTest.run(context, world, server, base);
			}
			if (enabled("neutronium")) {
				NeutroniumGameTest.run(context, world, server, base);
			}
			if (enabled("krakens")) {
				KrakenVariantsGameTest.run(context, world, server, base);
			}
			if (enabled("energy")) {
				EnergyGameTest.run(context, world, server, base);
			}
			if (enabled("waterlog")) {
				WaterlogGameTest.run(context, world, server, base);
			}
			if (enabled("machines")) {
				MachineGameTest.run(context, world, server, base);
			}
			if (enabled("networks")) {
				NetworkGameTest.run(context, world, server, base);
			}
			if (enabled("industry3")) {
				IndustryWave3GameTest.run(context, world, server, base);
			}
			if (enabled("industry4")) {
				IndustryWave4GameTest.run(context, world, server, base);
			}
			if (enabled("jei")) {
				JeiGameTest.run(context);
			}
			if (enabled("manualshots")) {
				ManualGameTest.shots(context, world, server, base);
			}
			if (enabled("manual")) {
				ManualGameTest.book(context, world, server);
			}
			if (enabled("harbor")) {
				HarborGameTest.run(context, world, server, base);
			}
			if (enabled("attack")) {
				AttackGameTest.run(context, world, server, base);
			}
			if (enabled("visual")) {
				KrakenVisualGameTest.run(context, world, server, base);
			}
			if (enabled("worldfx")) {
				KrakenWorldGameTest.run(context, world, server, base);
			}
		}
		if (enabled("worldgen")) {
			WorldgenGameTest.run(context);
		}
		if (enabled("spawn")) {
			OceanSpawnGameTest.run(context);
		}
		if (enabled("multiplayer")) {
			MultiplayerGameTest.run(context);
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

	/** A água leva o jogador para baixo; reposiciona de frente para o Kraken antes de cada foto. */
	private static void aimAtKraken(ClientGameTestContext context, TestServerContext server, BlockPos origin) {
		tp(server, origin.getX() + 0.5, origin.getY() + 42, origin.getZ() + 18.0, 180, 42);
		context.waitTicks(3);
	}

	/** Kraken: vida e escudo por fase, escala, transição, regeneração do escudo e aggro. */
	private static void kraken(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		// O teste do altar pode ter deixado um Kraken vivo; kraken(s) pega o primeiro da lista.
		// discard, não kill: matar dá a recompensa e gasta a "primeira vitória" que rewards() confere.
		server.runOnServer(s -> s.overworld().getEntities(ModEntities.KRAKEN, e -> true).forEach(Entity::discard));
		context.waitTicks(120);
		BlockPos origin = base.offset(200, 0, 0);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			level.getChunkAt(origin);
			KrakenCommand.buildArena(level, origin);
			KrakenSummoning.spawnAt(level, origin.offset(0, 25, 0), origin).attacks().setEnabled(false);
		});
		tp(server, origin.getX() + 0.5, origin.getY() + 27, origin.getZ() + 7.5, 180, 12);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		check(server.computeOnServer(s -> kraken(s).realMaxHealth() == 1500F && kraken(s).shield() == 400F
			&& kraken(s).phase() == 1 && kraken(s).scaleFactor() == 1F), "Kraken: valores da fase 1 com 1 jogador");
		check(server.computeOnServer(s -> near(kraken(s).realHealth(), 1500F)), "Kraken: vida inicial");
		check(context.computeOnClient(mc -> com.aquamarine.client.music.KrakenMusic.currentPhase() == 1),
			"Música: a Tormenta devia tocar com o Kraken perto");
		check(context.computeOnClient(mc -> com.aquamarine.client.music.KrakenMusic.framesMixed() > 0),
			"Música: o misturador de camadas não tocou nada (o fluxo não abriu?)");
		check(server.computeOnServer(s -> s.getWeatherData().isRaining()), "Mundo: a luta devia trazer chuva");
		aimAtKraken(context, server, origin);
		shot(context, "13_kraken_fase1_sem_rachaduras");
		for (String name : KrakenEntity.ANIMATION_NAMES) {
			server.runCommand("aquamarine kraken anim " + name);
			check(server.computeOnServer(s -> KrakenEntity.ANIMATION_NAMES.get(kraken(s).lastAnimation()).equals(name)), "Kraken: animação " + name);
		}

		krakenHit(server, 250);
		context.waitTicks(6);
		aimAtKraken(context, server, origin);
		shot(context, "14_kraken_fase1_rachaduras");
		krakenHit(server, 250);
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
		aimAtKraken(context, server, origin);
		shot(context, "15_kraken_fase2_escudo_cheio");

		context.waitTicks(70);
		check(server.computeOnServer(s -> !kraken(s).isTransitioning()), "Kraken: a transição devia ter acabado");
		check(context.computeOnClient(mc -> com.aquamarine.client.music.KrakenMusic.currentPhase() == 2
			&& com.aquamarine.client.music.KrakenSky.intensity() > 0.5F), "Música e céu: a Fúria devia tocar com o céu tingido");
		check(server.computeOnServer(s -> s.getWeatherData().isThundering()), "Mundo: a fase 2 devia trazer trovoada");
		krakenHit(server, 450);
		context.waitTicks(6);
		aimAtKraken(context, server, origin);
		shot(context, "16_kraken_fase2_rachaduras");
		context.waitTicks(620);
		check(server.computeOnServer(s -> kraken(s).shield() == 600F), "Kraken: o escudo devia regenerar após 20 s sem dano");

		krakenHit(server, 600);
		krakenHit(server, 600);
		context.waitTicks(2);
		check(server.computeOnServer(s -> kraken(s).phase() == 3 && near(kraken(s).realHealth(), 500F) && kraken(s).shield() == 800F),
			"Kraken: fase 3 com 800 de escudo");
		krakenHit(server, 700);
		context.waitTicks(6);
		aimAtKraken(context, server, origin);
		shot(context, "17_kraken_fase3_rachaduras");

		context.waitTicks(70);
		check(context.computeOnClient(mc -> com.aquamarine.client.music.KrakenMusic.currentPhase() == 3), "Música: o Abismo devia tocar na fase 3");
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
		rewards(context, world, server, origin);
		falseWarning(context, world, server, origin);
		server.runCommand("kill @e[type=aquamarine:kraken]");
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
	}

	private static int count(MinecraftServer s, Item item) {
		var inventory = player(s).getInventory();
		int total = 0;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (inventory.getItem(i).is(item)) {
				total += inventory.getItem(i).getCount();
			}
		}
		return total;
	}

	private static void killKraken(ClientGameTestContext context, TestServerContext server, BlockPos origin) {
		server.runOnServer(s -> kraken(s).kill(s.overworld()));
		context.waitTicks(25);
		server.runOnServer(s -> check(s.overworld().getEntities(ModEntities.KRAKEN, e -> true).isEmpty(), "Kraken: devia ter sumido após a morte"));
	}

	private static void spawnKraken(TestServerContext server, BlockPos origin) {
		server.runOnServer(s -> KrakenSummoning.spawnAt(s.overworld(), origin.offset(0, 25, 0), origin).attacks().setEnabled(false));
	}

	/** Drops e primeira vitória: Olho, Tinta, Troféu, XP e Coração só na primeira vez; Dourado no modo abissal. */
	private static void rewards(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos origin) {
		server.runCommand("gamemode survival @a");
		server.runOnServer(s -> {
			// o Kraken do teste do Altar pode ter dado o Coração antes, com o jogador por perto
			com.aquamarine.event.KrakenWorldData.get(s).forgetHeart(player(s).getUUID());
			player(s).getInventory().clearContent();
			player(s).experienceLevel = 0;
			player(s).totalExperience = 0;
		});
		killKraken(context, server, origin);
		check(server.computeOnServer(s -> count(s, ModItems.KRAKEN_EYE) == 1 && count(s, ModItems.KRAKEN_TROPHY) == 1
			&& count(s, ModItems.KRAKEN_HEART) == 1 && count(s, ModItems.GOLDEN_ABYSS_TROPHY) == 0), "Recompensas: Olho, Troféu e Coração na primeira vitória");
		check(server.computeOnServer(s -> count(s, ModItems.ABYSSAL_INK) >= 2 && count(s, ModItems.ABYSSAL_INK) <= 4), "Recompensas: 2 a 4 de Tinta Abissal");
		check(server.computeOnServer(s -> player(s).totalExperience >= 1000), "Recompensas: 1.000 de XP");

		spawnKraken(server, origin);
		context.waitTicks(2);
		killKraken(context, server, origin);
		check(server.computeOnServer(s -> count(s, ModItems.KRAKEN_HEART) == 1 && count(s, ModItems.KRAKEN_EYE) == 2
			&& count(s, ModItems.KRAKEN_TROPHY) == 2), "Recompensas: o Coração só vem na primeira vitória");

		com.aquamarine.config.AquamarineConfig.get().difficulty = "abissal";
		spawnKraken(server, origin);
		context.waitTicks(2);
		killKraken(context, server, origin);
		com.aquamarine.config.AquamarineConfig.get().difficulty = "normal";
		check(server.computeOnServer(s -> count(s, ModItems.GOLDEN_ABYSS_TROPHY) == 1 && count(s, ModItems.KRAKEN_TROPHY) == 2),
			"Recompensas: Troféu Dourado só no modo abissal");

		// Troféu no mundo (modelo e textura).
		BlockPos spot = origin.offset(0, 28, 10);
		server.runOnServer(s -> {
			s.overworld().setBlockAndUpdate(spot, ModBlocks.KRAKEN_TROPHY.defaultBlockState());
			s.overworld().setBlockAndUpdate(spot.east(2), ModBlocks.GOLDEN_ABYSS_TROPHY.defaultBlockState());
		});
		tp(server, spot.getX() + 1.0, spot.getY() + 1.2, spot.getZ() + 3.5, 0, 15);
		context.waitTicks(10);
		shot(context, "16_trofeus");
		server.runCommand("gamemode creative @a");
	}

	/** Aviso falso: gatilho (tempestade à noite, 8%, 1 a cada 3 dias), sem Kraken, silêncio, tremor e escurecer no cliente. */
	private static void falseWarning(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos origin) {
		server.runCommand("weather clear");
		server.runOnServer(s -> check(com.aquamarine.event.FalseWarning.check(s.overworld(), p -> true, () -> 0.0)
			== com.aquamarine.event.FalseWarning.Outcome.NOT_ELIGIBLE, "Aviso falso: sem tempestade não dispara"));
		server.runCommand("time set midnight");
		server.runCommand("weather thunder");
		context.waitTicks(120);
		server.runOnServer(s -> check(com.aquamarine.event.FalseWarning.check(s.overworld(), p -> false, () -> 0.0)
			== com.aquamarine.event.FalseWarning.Outcome.NOT_ELIGIBLE, "Aviso falso: sem jogador em oceano profundo não dispara"));
		server.runOnServer(s -> check(com.aquamarine.event.FalseWarning.check(s.overworld(), p -> true, () -> 0.5)
			== com.aquamarine.event.FalseWarning.Outcome.MISSED, "Aviso falso: sorte alta perde"));
		server.runOnServer(s -> check(com.aquamarine.event.FalseWarning.check(s.overworld(), p -> true, () -> 0.0)
			== com.aquamarine.event.FalseWarning.Outcome.NOT_ELIGIBLE, "Aviso falso: só uma sorte por noite"));
		server.runCommand("time add 1d");
		server.runOnServer(s -> check(com.aquamarine.event.FalseWarning.check(s.overworld(), p -> true, () -> 0.0)
			== com.aquamarine.event.FalseWarning.Outcome.TRIGGERED, "Aviso falso: devia disparar na noite sorteada com sorte"));
		check(server.computeOnServer(s -> com.aquamarine.event.KrakenSummoning.isActiveNear(s.overworld(), player(s).blockPosition())),
			"Aviso falso: a sequência devia estar rodando");
		context.waitTicks(3);
		check(context.computeOnClient(mc -> com.aquamarine.client.SummonEffects.silenced()), "Aviso falso: oceano devia silenciar em 0 s");
		context.waitTicks(102);
		check(context.computeOnClient(mc -> com.aquamarine.client.SummonEffects.shaking()), "Aviso falso: tela devia tremer aos 5 s");
		context.waitTicks(100);
		check(context.computeOnClient(mc -> com.aquamarine.client.SummonEffects.darkening()), "Aviso falso: o céu devia escurecer");
		context.waitTicks(40);
		shot(context, "17_aviso_falso_escuro");
		check(server.computeOnServer(s -> s.overworld().getEntities(ModEntities.KRAKEN, e -> true).isEmpty()), "Aviso falso: não pode surgir Kraken");
		context.waitTicks(370);
		check(server.computeOnServer(s -> !com.aquamarine.event.KrakenSummoning.isActiveNear(s.overworld(), player(s).blockPosition())),
			"Aviso falso: a sequência devia ter terminado");

		server.runCommand("time add 1d");
		server.runOnServer(s -> check(com.aquamarine.event.FalseWarning.check(s.overworld(), p -> true, () -> 0.0)
			== com.aquamarine.event.FalseWarning.Outcome.COOLDOWN, "Aviso falso: 1 dia depois ainda esfria"));
		server.runCommand("time add 3d");
		server.runOnServer(s -> check(com.aquamarine.event.FalseWarning.check(s.overworld(), p -> true, () -> 0.0)
			== com.aquamarine.event.FalseWarning.Outcome.TRIGGERED, "Aviso falso: depois de 3 dias dispara de novo"));
		com.aquamarine.config.AquamarineConfig.get().falseWarning = false;
		server.runCommand("time add 4d");
		server.runOnServer(s -> check(com.aquamarine.event.FalseWarning.check(s.overworld(), p -> true, () -> 0.0)
			== com.aquamarine.event.FalseWarning.Outcome.NOT_ELIGIBLE, "Aviso falso: desligado na config não dispara"));
		com.aquamarine.config.AquamarineConfig.get().falseWarning = true;
		context.waitTicks(610);
		server.runCommand("weather clear");
		server.runCommand("time set noon");
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
		player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.ABYSSAL_CHESTPLATE));
		player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.ABYSSAL_LEGGINGS));
		player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.ABYSSAL_BOOTS));
	}

	/** Vitrine: blocos (com troféus e blocos de trabalho do Porto), suportes de armadura vestidos e a barra de itens. */
	private static void showcase(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			BlockState forge = ModBlocks.ABYSSAL_FORGE.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);
			level.setBlockAndUpdate(base.offset(-2, 0, 5), forge);
			level.setBlockAndUpdate(base.offset(0, 0, 5), ModBlocks.ABYSSAL_ORE.defaultBlockState());
			level.setBlockAndUpdate(base.offset(2, 0, 5), ModBlocks.ABYSSAL_BLOCK.defaultBlockState());
			// Troféus do Kraken (2E) e blocos de trabalho dos Mergulhadores (3E), para ver no mundo.
			level.setBlockAndUpdate(base.offset(-4, 0, 5), ModBlocks.KRAKEN_TROPHY.defaultBlockState());
			level.setBlockAndUpdate(base.offset(4, 0, 5), ModBlocks.GOLDEN_ABYSS_TROPHY.defaultBlockState());
			level.setBlockAndUpdate(base.offset(-2, 0, 7), ModBlocks.TIDE_ANVIL.defaultBlockState());
			level.setBlockAndUpdate(base.offset(0, 0, 7), ModBlocks.CHART_TABLE.defaultBlockState());
			level.setBlockAndUpdate(base.offset(2, 0, 7), ModBlocks.SHELL_ALTAR.defaultBlockState());

			spawnStand(level, base.offset(-1, 0, 3), ModItems.ABYSSAL_HELMET, ModItems.ABYSSAL_SWORD);
			spawnStand(level, base.offset(1, 0, 3), ModItems.DIVING_GOGGLES, ModItems.ABYSSAL_PICKAXE);

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
		server.runOnServer(s -> setArmor(player(s), ModItems.ABYSSAL_HELMET));
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() - 1.5, 180, 10);
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
		context.waitTicks(25);
		shot(context, "02_jogador_armadura");
		check(server.computeOnServer(s -> player(s).getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY)
			.hasModifier(Aquamarine.id("abyssal_set_swim"))), "bônus de nado do conjunto completo não aplicado");

		server.runOnServer(s -> setArmor(player(s), ModItems.DIVING_GOGGLES));
		context.waitTicks(25);
		shot(context, "03_jogador_oculos");
		check(!server.computeOnServer(s -> player(s).getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY)
			.hasModifier(Aquamarine.id("abyssal_set_swim"))), "bônus de nado continuou sem o capacete");
		context.runOnClient(mc -> mc.options.setCameraType(CameraType.FIRST_PERSON));
	}

	/** Vitrine dedicada aos dez blocos da revisão visual, com seis capturas para posterior recorte. */
	private static void blockVisuals(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		server.runCommand("time set noon");
		server.runCommand("weather clear");
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			BlockPos row = base.offset(-9, 0, 8);
			Block[] blocks = {ModBlocks.ABYSSAL_ORE, ModBlocks.ABYSSAL_BLOCK, ModBlocks.ABYSSAL_FORGE,
				ModBlocks.ABYSSAL_ALTAR, ModBlocks.ANCHOR_BUOY, ModBlocks.KRAKEN_TROPHY,
				ModBlocks.GOLDEN_ABYSS_TROPHY, ModBlocks.TIDE_ANVIL, ModBlocks.CHART_TABLE, ModBlocks.SHELL_ALTAR};
			for (int i = 0; i < blocks.length; i++) {
				BlockState state = blocks[i] == ModBlocks.ANCHOR_BUOY
					? AnchorBuoyBlock.placed(true).setValue(AnchorBuoyBlock.WATERLOGGED, false) : blocks[i].defaultBlockState();
				if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
					// A câmera olha do sul: os pares mostram a frente (sul) e o lado (leste).
					state = state.setValue(HorizontalDirectionalBlock.FACING, i % 2 == 0 ? Direction.SOUTH : Direction.EAST);
				}
				level.setBlockAndUpdate(row.east(i * 2), state);
			}
		});
		BlockPos row = base.offset(-9, 0, 8);
		tp(server, base.getX() + 0.5, base.getY() + 4.0, row.getZ() + 13.0, 180, 15);
		world.getConnection().waitForChunksRender();
		context.waitTicks(10);
		shot(context, "blocos_00_fileira");
		for (int i = 0; i < 10; i += 2) {
			tp(server, row.getX() + 2 * i + 1.0, row.getY() + 0.0, row.getZ() + 4.2, 180, 16);
			world.getConnection().waitForChunksRender();
			context.waitTicks(3);
			shot(context, "blocos_" + String.format(java.util.Locale.ROOT, "%02d", i / 2 + 1) + "_pares_" + i + "_" + (i + 1));
		}
	}

	/** Fase 4 (4E): blocos aquáticos, vidros e vidraças rachados, e a onda de choque do Kraken rachando vidro. */
	private static void buildingVisuals(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		server.runCommand("time set noon");
		server.runCommand("kill @e[type=!player]");
		BlockPos origin = base.offset(-12, 0, 16);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			List<Block> building = ModBuildingBlocks.building();
			for (int i = 0; i < building.size(); i++) {
				level.setBlockAndUpdate(origin.offset((i % 12) * 2, 0, (i / 12) * 2), building.get(i).defaultBlockState());
			}
			List<Block> glass = ModBuildingBlocks.glass();
			int blocks = 0, panes = 0;
			for (Block block : glass) {
				boolean pane = BuiltInRegistries.BLOCK.getKey(block).getPath().endsWith("_pane");
				BlockPos at = origin.offset(pane ? panes++ : blocks++, 0, pane ? 9 : 7);
				level.setBlockAndUpdate(at, block.defaultBlockState());
				level.setBlockAndUpdate(at.above(), block.defaultBlockState());
			}
			// Onda de choque: vidro comum e vermelho fora da cratera viram rachados (com as mesmas propriedades).
			BlockPos clear = origin.offset(0, 4, -6);
			level.setBlockAndUpdate(clear, Blocks.GLASS.defaultBlockState());
			level.setBlockAndUpdate(clear.east(), Blocks.STAINED_GLASS_PANE.pick(DyeColor.RED).defaultBlockState());
			int cracked = KrakenWorldEffects.crackGlass(level, Vec3.atCenterOf(clear).add(18, 0, 0), 14, 22);
			check(cracked >= 2 && level.getBlockState(clear).is(ModBuildingBlocks.cracked(Blocks.GLASS))
				&& level.getBlockState(clear.east()).is(ModBuildingBlocks.cracked(Blocks.STAINED_GLASS_PANE.pick(DyeColor.RED))),
				"a onda de choque não rachou os vidros (" + cracked + ")");
		});
		tp(server, origin.getX() + 11.5, origin.getY() + 5.0, origin.getZ() - 5.0, 0, 35);
		world.getConnection().waitForChunksRender();
		context.waitTicks(10);
		shot(context, "construcao_00_blocos");
		tp(server, origin.getX() + 9.0, origin.getY() + 1.6, origin.getZ() + 14.5, 180, 5);
		world.getConnection().waitForChunksRender();
		context.waitTicks(10);
		shot(context, "construcao_01_vidros");

		// Fase 6 (6D1 e 6H): família do Aurium, peças de navio, aquário e estátua; e o bônus do conjunto de Aurium na água.
		BlockPos row = origin.offset(0, 0, -4);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			List<Block> six = new java.util.ArrayList<>(com.aquamarine.block.ModDecorBlocks.building());
			six.addAll(com.aquamarine.block.ModDecorBlocks.decor());
			six.add(ModBlocks.AURIUM_ORE);
			six.add(ModBlocks.AURIUM_BLOCK);
			for (int i = 0; i < six.size(); i++) {
				level.setBlockAndUpdate(row.offset((i * 2) % 24, 0, -(i / 12) * 2), six.get(i).defaultBlockState());
			}
		});
		tp(server, row.getX() + 11.5, row.getY() + 3.0, row.getZ() - 7.5, 0, 25);
		world.getConnection().waitForChunksRender();
		context.waitTicks(10);
		shot(context, "construcao_02_fase6");
		BlockPos pool = row.offset(28, 0, 0);
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass", pool.getX() - 1, pool.getY() - 1, pool.getZ() - 1,
			pool.getX() + 1, pool.getY() + 3, pool.getZ() + 1));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", pool.getX(), pool.getY(), pool.getZ(),
			pool.getX(), pool.getY() + 2, pool.getZ()));
		server.runOnServer(s -> {
			ServerPlayer p = player(s);
			p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(ModItems.AURIUM_HELMET));
			p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, new ItemStack(ModItems.AURIUM_CHESTPLATE));
			p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, new ItemStack(ModItems.AURIUM_LEGGINGS));
			p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, new ItemStack(ModItems.AURIUM_BOOTS));
			p.removeEffect(net.minecraft.world.effect.MobEffects.CONDUIT_POWER);
			p.teleportTo(pool.getX() + 0.5, pool.getY(), pool.getZ() + 0.5);
		});
		context.waitTicks(30);
		check(server.computeOnServer(s -> player(s).hasEffect(net.minecraft.world.effect.MobEffects.CONDUIT_POWER)),
			"Aurium: o conjunto devia dar Poder do Conduíte debaixo da água");
		server.runOnServer(s -> {
			for (net.minecraft.world.entity.EquipmentSlot slot : new net.minecraft.world.entity.EquipmentSlot[] {
				net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST,
				net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET}) {
				player(s).setItemSlot(slot, ItemStack.EMPTY);
			}
		});
	}

	/** Ovo, Cria e tentáculo do Kraken lado a lado, de frente e de trás, para a revisão de UV. */
	private static void spawnlingVisuals(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		server.runCommand("time set noon");
		server.runCommand("kill @e[type=!player]");
		double z = base.getZ() + 8.5, y = base.getY();
		String[] ids = {"kraken_egg", "young_kraken", "tentacle"};
		for (int i = 0; i < ids.length; i++) {
			server.runCommand(String.format(java.util.Locale.ROOT, "summon aquamarine:%s %.1f %.1f %.1f {Rotation:[0f,0f]}", ids[i], base.getX() - 4.0 + i * 4.0 + 0.5, y, z));
		}
		tp(server, base.getX() + 0.5, y + 0.0, z + 10.0, 180, 8);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		shot(context, "spawnlings_00_frente");
		tp(server, base.getX() + 0.5, y + 0.0, z - 10.0, 0, 8);
		context.waitTicks(10);
		shot(context, "spawnlings_01_costas");
		for (int i = 0; i < ids.length; i++) {
			tp(server, base.getX() - 4.0 + i * 4.0 + 0.5, y + 1.0, z + 4.5, 180, 8);
			context.waitTicks(6);
			shot(context, "spawnlings_" + (i + 2) + "_" + ids[i]);
		}
	}

	private static void spawnStand(ServerLevel level, BlockPos pos, Item head, Item hand) {
		ArmorStand stand = new ArmorStand(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		stand.setYRot(180);
		stand.setYBodyRot(180);
		stand.setShowArms(true);
		stand.setNoBasePlate(true);
		stand.setItemSlot(EquipmentSlot.HEAD, new ItemStack(head));
		stand.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.ABYSSAL_CHESTPLATE));
		stand.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.ABYSSAL_LEGGINGS));
		stand.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.ABYSSAL_BOOTS));
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
			ItemStack ingot = new ItemStack(ModItems.ABYSSAL_INGOT);
			ItemStack stick = new ItemStack(Items.STICK);
			ItemStack[] grid = {ingot, ingot, ingot, ItemStack.EMPTY, stick, ItemStack.EMPTY, ItemStack.EMPTY, stick, ItemStack.EMPTY};
			for (int i = 0; i < grid.length; i++) {
				player.containerMenu.getSlot(1 + i).set(grid[i].copy());
			}
			player.containerMenu.broadcastChanges();
			check(player.containerMenu.getSlot(0).getItem().is(ModItems.ABYSSAL_PICKAXE), "a Forja não montou a picareta");

			CraftingInput input = CraftingInput.of(3, 3, List.of(grid));
			check(s.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).isEmpty(),
				"a picareta Abissal não deveria sair da bancada comum");
			// Mundos de antes da Fase 4: o item salvo como orichalcum_* abre como Abissal, e o bloco também.
			ItemStack old = ItemStack.CODEC.parse(JsonOps.INSTANCE,
				JsonParser.parseString("{\"id\":\"aquamarine:orichalcum_pickaxe\",\"count\":1}")).getOrThrow();
			check(old.is(ModItems.ABYSSAL_PICKAXE), "item antigo de Oricálcio não abriu como Abissal: " + old);
			check(BuiltInRegistries.BLOCK.getValue(Aquamarine.id("orichalcum_ore")) == ModBlocks.ABYSSAL_ORE,
				"o bloco antigo de Oricálcio não virou Abissal");
		});
		context.waitForScreen(AbyssalForgeScreen.class);
		context.waitTicks(5);
		shot(context, "05_forja");

		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.containerMenu.clicked(0, 0, ContainerInput.QUICK_MOVE, player);
			check(player.getInventory().contains(new ItemStack(ModItems.ABYSSAL_PICKAXE)), "a picareta não foi para o inventário");
			for (int i = 1; i <= 9; i++) {
				check(player.containerMenu.getSlot(i).getItem().isEmpty(), "ingrediente sobrou na grade (slot " + i + ")");
			}
			player.closeContainer();
		});
		context.waitTicks(5);
		// 6D2: a Forja em 3D (brasas, cadinho e chaminé), vista de frente e um pouco de lado.
		tp(server, forgePos.getX() - 1.1, forgePos.getY() + 0.3, forgePos.getZ() - 1.6, -37.0F, 24.0F);
		context.waitTicks(10);
		shot(context, "05b_forja_3d");
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
