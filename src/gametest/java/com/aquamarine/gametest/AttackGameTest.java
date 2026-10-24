package com.aquamarine.gametest;

import com.aquamarine.block.ModBlocks;
import com.aquamarine.command.KrakenCommand;
import com.aquamarine.effect.ModEffects;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.TentacleEntity;
import com.aquamarine.entity.boss.KrakenAttackType;
import com.aquamarine.event.KrakenSummoning;
import com.aquamarine.item.ModItems;

import java.util.Locale;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * Teste da faixa 2D-C: os 8 tentáculos, os 10 ataques (aviso e golpe, por comando ou código),
 * o dano de cada um e as contramedidas (cortar o tentáculo, Tônico, armadura de Oricálcio,
 * submergir). Chamado por {@link AquamarineClientGameTest}; 3 screenshots.
 */
final class AttackGameTest {
	private static BlockPos origin;

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		origin = base.offset(600, 0, 0);
		server.runCommand("gamemode survival @a");
		server.runCommand("difficulty normal");
		server.runCommand(String.format("forceload add %d %d %d %d", origin.getX() - 30, origin.getZ() - 30, origin.getX() + 30, origin.getZ() + 30));
		// Krakens de testes anteriores falsificariam "o mais próximo" dos comandos.
		server.runOnServer(s -> {
			s.overworld().getEntities(ModEntities.KRAKEN, e -> true).forEach(k -> k.discard());
			s.overworld().getEntities(ModEntities.TENTACLE, e -> true).forEach(t -> t.discard());
		});
		// A invocação do Altar do teste anterior ainda pode surgir com um Kraken atrasado: espera e limpa de novo.
		context.waitTicks(200);
		server.runOnServer(s -> {
			s.overworld().getEntities(ModEntities.KRAKEN, e -> true).forEach(k -> k.discard());
			s.overworld().getEntities(ModEntities.TENTACLE, e -> true).forEach(t -> t.discard());
		});
		context.waitTicks(20);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			KrakenCommand.buildArena(level, origin);
			KrakenEntity kraken = KrakenSummoning.spawnAt(level, origin.offset(0, 25, 0), origin);
			check(kraken != null, "Ataques: o Kraken não surgiu");
		});
		server.runOnServer(s -> {
			check(s.overworld().getEntities(ModEntities.KRAKEN, e -> true).size() == 1, "Ataques: só o Kraken da arena devia existir: "
				+ s.overworld().getEntities(ModEntities.KRAKEN, e -> true).stream().map(k -> k.blockPosition() + " vida " + k.isAlive()).toList());
			kraken(s).attacks().setEnabled(false);
		});
		reset(server);
		world.getConnection().waitForChunksRender();
		context.waitTicks(30);
		check(server.computeOnServer(s -> kraken(s).attacks().aliveTentacles(s.overworld())) == 8, "Tentáculos: devem ser 8");
		check(server.computeOnServer(s -> s.overworld().getEntities(ModEntities.TENTACLE, e -> true).size()) == 8, "Tentáculos: 8 entidades");

		// Pancada (pelo comando): aviso na água, depois 12 de dano.
		server.runCommand(String.format("execute positioned %d %d %d run aquamarine kraken attack slam", origin.getX(), origin.getY() + 25, origin.getZ() + 12));
		context.waitTicks(15);
		check(health(server) == 20.0F, "Pancada: não deve ferir durante o aviso");
		shot(context, "18_ataque_pancada_aviso");
		context.waitTicks(20);
		check(health(server) == 8.0F, "Pancada: 12 de dano, vida " + health(server));

		// Agarrão: prende, 6 de dano por segundo, cortar o tentáculo solta.
		reset(server);
		attack(server, KrakenAttackType.GRAB);
		context.waitTicks(36);
		check(server.computeOnServer(s -> kraken(s).attacks().grabbedCount()) == 1, "Agarrão: devia prender o jogador");
		context.waitTicks(25);
		check(health(server) <= 14.0F, "Agarrão: 6 de dano por segundo, vida " + health(server));
		server.runOnServer(s -> busyTentacles(s).forEach(t -> t.kill(s.overworld())));
		context.waitTicks(2);
		check(server.computeOnServer(s -> kraken(s).attacks().grabbedCount()) == 0, "Agarrão: cortar o tentáculo devia soltar");

		// Agarrão com Tônico: solta 50% mais rápido (5 s viram 2,5 s).
		reset(server);
		server.runOnServer(s -> {
			player(s).addEffect(new MobEffectInstance(ModEffects.PRESSURE_TONIC, 6000));
			check(ModEffects.hasTonic(player(s)), "Tônico: efeito ausente");
		});
		attack(server, KrakenAttackType.GRAB);
		context.waitTicks(36);
		check(server.computeOnServer(s -> kraken(s).attacks().grabbedCount()) == 1, "Agarrão com Tônico: devia prender");
		context.waitTicks(60);
		check(server.computeOnServer(s -> kraken(s).attacks().grabbedCount()) == 0, "Agarrão com Tônico: devia soltar em ~2,5 s");

		// Nuvem de Tinta: cegueira, exceto com a armadura de Oricálcio completa (óculos não bastam).
		reset(server);
		attack(server, KrakenAttackType.INK);
		context.waitTicks(45);
		check(server.computeOnServer(s -> player(s).hasEffect(MobEffects.BLINDNESS)), "Tinta: devia cegar");
		reset(server);
		server.runOnServer(s -> {
			player(s).setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.ORICHALCUM_HELMET));
			player(s).setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.ORICHALCUM_CHESTPLATE));
			player(s).setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.ORICHALCUM_LEGGINGS));
			player(s).setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.ORICHALCUM_BOOTS));
		});
		attack(server, KrakenAttackType.INK);
		context.waitTicks(45);
		check(server.computeOnServer(s -> !player(s).hasEffect(MobEffects.BLINDNESS)), "Tinta: a armadura completa devia proteger");

		// Redemoinho: puxa quem está no raio de 16 blocos.
		reset(server);
		double before = server.computeOnServer(s -> player(s).distanceTo(kraken(s)));
		attack(server, KrakenAttackType.WHIRLPOOL);
		context.waitTicks(50);
		check(server.computeOnServer(s -> kraken(s).attacks().isWhirlpoolActive()), "Redemoinho: devia estar ativo");
		context.waitTicks(40);
		double after = server.computeOnServer(s -> player(s).distanceTo(kraken(s)));
		check(after < before - 1.0, "Redemoinho: devia puxar o jogador (" + before + " -> " + after + ")");

		// Crias: 3 + n/2 com 1 jogador.
		reset(server);
		attack(server, KrakenAttackType.SPAWN_YOUNG);
		context.waitTicks(30);
		shot(context, "19_ataque_ovos_aviso");
		context.waitTicks(15);
		check(server.computeOnServer(s -> kraken(s).attacks().youngCount(s.overworld())) == 3, "Crias: devem ser 3");
		server.runCommand("kill @e[type=aquamarine:young_kraken]");

		// Jato de Água: 10 de dano em linha (o alvo é travado no aviso).
		reset(server);
		attack(server, KrakenAttackType.WATER_JET);
		StringBuilder trace = new StringBuilder();
		for (int i = 0; i < 14; i++) {
			context.waitTick();
			trace.append(health(server)).append(' ');
		}
		shot(context, "20_ataque_jato_aviso");
		for (int i = 0; i < 16; i++) {
			context.waitTick();
			trace.append(health(server)).append(' ');
		}
		check(health(server) == 10.0F, "Jato: 10 de dano, vida " + health(server) + " trace " + trace);

		// Rugido: quebra vidro, derruba a Boia de Âncora.
		reset(server);
		BlockPos glass = origin.offset(5, 25, 5);
		BlockPos buoy = origin.offset(-5, 25, -5);
		server.runOnServer(s -> {
			s.overworld().setBlockAndUpdate(glass, Blocks.GLASS.defaultBlockState());
			s.overworld().setBlockAndUpdate(buoy, ModBlocks.ANCHOR_BUOY.defaultBlockState());
		});
		attack(server, KrakenAttackType.ROAR);
		context.waitTicks(40);
		check(server.computeOnServer(s -> !s.overworld().getBlockState(glass).is(Blocks.GLASS)), "Rugido: devia quebrar o vidro");
		check(server.computeOnServer(s -> !s.overworld().getBlockState(buoy).is(ModBlocks.ANCHOR_BUOY)), "Rugido: devia derrubar a Boia");

		// Raios Guiados: submerso não leva dano; fora da água leva 14.
		reset(server);
		attack(server, KrakenAttackType.BOLTS);
		context.waitTicks(45);
		check(health(server) == 20.0F, "Raios: submerso não deve levar dano, vida " + health(server));
		reset(server);
		tp(server, origin.getX() + 0.5, origin.getY() + 65.0, origin.getZ() + 12.5, 180.0F, 0.0F);
		server.runOnServer(s -> {
			player(s).setNoGravity(true);
			player(s).setDeltaMovement(0, 0, 0);
		});
		attack(server, KrakenAttackType.BOLTS);
		context.waitTicks(45);
		check(health(server) == 6.0F, "Raios: 14 de dano fora da água, vida " + health(server));
		server.runOnServer(s -> player(s).setNoGravity(false));

		// Emboscada por Baixo: 10 de dano e o jogador é lançado para cima.
		reset(server);
		attack(server, KrakenAttackType.AMBUSH);
		context.waitTicks(45);
		check(health(server) == 10.0F, "Emboscada: 10 de dano, vida " + health(server));

		// Agarrão Duplo: cortar um tentáculo solta parcialmente, cortar os dois solta de vez.
		reset(server);
		attack(server, KrakenAttackType.DOUBLE_GRAB);
		context.waitTicks(36);
		check(server.computeOnServer(s -> kraken(s).attacks().grabbedCount()) == 1, "Agarrão Duplo: devia prender");
		server.runOnServer(s -> busyTentacles(s).stream().findFirst().ifPresent(t -> t.kill(s.overworld())));
		context.waitTicks(3);
		check(server.computeOnServer(s -> kraken(s).attacks().grabbedCount()) == 1, "Agarrão Duplo: com um cortado ainda prende, parcialmente");
		server.runOnServer(s -> busyTentacles(s).forEach(t -> t.kill(s.overworld())));
		context.waitTicks(3);
		check(server.computeOnServer(s -> kraken(s).attacks().grabbedCount()) == 0, "Agarrão Duplo: sem tentáculos devia soltar");

		// Tentáculos cortados: com os 8 fora do ciclo o Kraken fica exposto; voltam em 30 s.
		reset(server);
		server.runCommand("kill @e[type=aquamarine:tentacle]");
		context.waitTicks(3);
		check(server.computeOnServer(s -> kraken(s).isExposed()), "Tentáculos: os 8 cortados deviam expor o Kraken");
		check(server.computeOnServer(s -> kraken(s).attacks().aliveTentacles(s.overworld())) == 0, "Tentáculos: nenhum devia estar vivo");
		context.waitTicks(620);
		check(server.computeOnServer(s -> kraken(s).attacks().aliveTentacles(s.overworld())) == 8, "Tentáculos: devem voltar em 30 s");

		// Criaturas do Codex (2D-M) por comando: tentáculo, Cria e ovo com seus renderers e animações.
		reset(server);
		server.runOnServer(s -> kraken(s).attacks().setEnabled(false));
		int tentacles = server.computeOnServer(s -> s.overworld().getEntities(ModEntities.TENTACLE, e -> true).size());
		server.runCommand("execute as @p at @p rotated ~ 0 positioned ^-3 ^ ^6 run aquamarine kraken egg");
		server.runCommand("execute as @p at @p rotated ~ 0 positioned ^ ^ ^6 run aquamarine kraken young");
		server.runCommand("execute as @p at @p rotated ~ 0 positioned ^3 ^ ^6 run aquamarine kraken tentacle");
		context.waitTicks(5);
		check(server.computeOnServer(s -> s.overworld().getEntities(ModEntities.TENTACLE, e -> true).size()) == tentacles + 1, "Comando: tentáculo");
		check(server.computeOnServer(s -> s.overworld().getEntities(ModEntities.YOUNG_KRAKEN, e -> true).size()) == 1, "Comando: Cria");
		check(server.computeOnServer(s -> s.overworld().getEntities(ModEntities.KRAKEN_EGG, e -> true).size()) == 1, "Comando: ovo");
		server.runCommand("aquamarine kraken tentacle anim rise");
		server.runCommand("aquamarine kraken young anim attack");
		context.waitTicks(10);
		shot(context, "21_criaturas_comando");
		server.runCommand("aquamarine kraken tentacle anim slam");
		server.runCommand("aquamarine kraken egg anim hatch");
		context.waitTicks(6);
		// Texturas (2D-T): uma criatura de cada vez, de perto.
		String[][] close = {{"egg", "^ ^ ^3", "23_textura_ovo"}, {"young", "^ ^ ^3.5", "24_textura_cria"}, {"tentacle", "^ ^ ^7", "25_textura_tentaculo"}};
		for (String[] c : close) {
			server.runCommand("kill @e[type=aquamarine:young_kraken]");
			server.runOnServer(s -> {
				s.overworld().getEntities(ModEntities.KRAKEN_EGG, e -> true).forEach(e -> e.discard());
				s.overworld().getEntities(ModEntities.TENTACLE, t -> t.owner() == null).forEach(t -> t.discard());
			});
			reset(server);
			server.runCommand("execute as @p at @p rotated ~ 0 positioned " + c[1] + " run aquamarine kraken " + c[0]);
			context.waitTicks(8);
			shot(context, c[2]);
		}
		server.runCommand("kill @e[type=aquamarine:young_kraken]");
		server.runOnServer(s -> s.overworld().getEntities(ModEntities.KRAKEN_EGG, e -> true).forEach(e -> e.discard()));
		server.runOnServer(s -> s.overworld().getEntities(ModEntities.TENTACLE, t -> t.owner() == null).forEach(t -> t.discard()));

		// Sorteio automático: com a IA ligada, o Kraken ataca sozinho dentro do limite de simultâneos.
		reset(server);
		// Vida alta: o Kraken final (24 x 16) cobre a arena e mataria o jogador antes dos 20 s.
		server.runOnServer(s -> {
			player(s).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(2000.0);
			player(s).setHealth(2000.0F);
			kraken(s).attacks().setEnabled(true);
		});
		context.waitTicks(400);
		check(server.computeOnServer(s -> kraken(s).attacks().activeCount() <= KrakenAttackType.maxSimultaneous(1)), "IA: simultâneos acima do limite");
		check(server.computeOnServer(s -> player(s).getHealth() < 2000.0F || player(s).hasEffect(MobEffects.BLINDNESS)
			|| kraken(s).attacks().youngCount(s.overworld()) > 0 || kraken(s).attacks().grabbedCount() > 0
			|| kraken(s).attacks().isWhirlpoolActive() || !player(s).isAlive()), "IA: nenhum ataque automático em 20 s");

		server.runOnServer(s -> kraken(s).attacks().setEnabled(false));
		server.runCommand("kill @e[type=aquamarine:kraken]");
		server.runCommand("kill @e[type=aquamarine:young_kraken]");
		context.waitTicks(5);
		check(server.computeOnServer(s -> s.overworld().getEntities(ModEntities.TENTACLE, e -> true).isEmpty()), "Tentáculos: devem sumir com o Kraken");
		server.runOnServer(s -> {
			ServerPlayer p = player(s);
			p.removeAllEffects();
			p.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(20.0);
			p.setHealth(20.0F);
			p.snapTo(base.getX() + 0.5, base.getY(), base.getZ() + 0.5);
		});
		context.waitTicks(10);
	}

	/** Cura o jogador, tira efeitos e armadura e o põe de volta a 12,5 blocos do Kraken, olhando para ele. */
	private static void reset(TestServerContext server) {
		server.runOnServer(s -> {
			ServerPlayer p = player(s);
			// a Pressão (2B) sobe sozinha perto do Kraken e atrapalharia as medidas
			com.aquamarine.pressure.PressureManager.reset(p);
			p.removeAllEffects();
			p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20000));
			for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
				p.setItemSlot(slot, ItemStack.EMPTY);
			}
			p.setHealth(20.0F);
			p.setDeltaMovement(0, 0, 0);
		});
		tp(server, origin.getX() + 0.5, origin.getY() + 25.0, origin.getZ() + 12.5, 180.0F, 0.0F);
	}

	private static void attack(TestServerContext server, KrakenAttackType type) {
		check(server.computeOnServer(s -> kraken(s).attacks().force(s.overworld(), type, player(s))), "Ataque " + type + " não começou");
	}

	private static java.util.List<? extends TentacleEntity> busyTentacles(MinecraftServer s) {
		return s.overworld().getEntities(ModEntities.TENTACLE, t -> t.isBusy());
	}

	private static float health(TestServerContext server) {
		return server.computeOnServer(s -> player(s).getHealth());
	}

	private static KrakenEntity kraken(MinecraftServer server) {
		return server.overworld().getEntities(ModEntities.KRAKEN, e -> true).getFirst();
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

	private static String fmt(double v) {
		return String.format(Locale.ROOT, "%.1f", v);
	}

	private AttackGameTest() {}
}
