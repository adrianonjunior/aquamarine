package com.aquamarine.gametest;

import com.aquamarine.block.Waterlogging;
import com.aquamarine.client.machine.VentGeneratorAnimations;
import com.aquamarine.energy.ModEnergy;
import com.aquamarine.energy.WaterGeneratorBlockEntity;
import com.aquamarine.machine.MachineState;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 0.6.1, 7B (docs/12-industria.md): o Gerador de Fenda animado. Confere o contrato dos clipes, monta um aquário com três
 * Fendas de verdade (funcionando, em sobrecarga sem cabo e parada sem magma) e uma vitrine em terra firme com a produção
 * forçada só no teste, para as capturas da arte em cada estado; por fim, o item na mão.
 */
final class MachineGameTest {
	private MachineGameTest() {}

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		var clips = VentGeneratorAnimations.CLIPS;
		for (String name : List.of("idle", "start", "run", "stop", "overload")) {
			check(clips.containsKey(name), "Fenda: falta o clipe " + name);
		}
		check(close(clips.get("start").lengthInSeconds(), WaterGeneratorBlockEntity.VENT_START), "Fenda: VENT_START difere do clipe start");
		check(close(clips.get("run").lengthInSeconds() / 2, WaterGeneratorBlockEntity.VENT_STROKE), "Fenda: VENT_STROKE difere do run");
		check(clips.get("run").looping() && clips.get("idle").looping() && clips.get("overload").looping(), "Fenda: laços sem looping");

		BlockPos o = base.offset(90, 0, -90);
		int x = o.getX(), y = o.getY(), z = o.getZ();
		server.runCommand(String.format("forceload add %d %d %d %d", x - 4, z - 12, x + 12, z + 6));
		server.runCommand("time set noon");
		server.runCommand("weather clear");
		// Aquário (modo mundo): magma embaixo, água em cima e em volta. 0 funciona, 1 fica cheio sem cabo, 2 não tem magma.
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow", x - 1, y, z - 3, x + 5, y + 4, z + 1));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", x, y + 1, z - 2, x + 4, y + 3, z));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:sand", x, y + 1, z - 2, x + 4, y + 1, z - 1));
		BlockPos[] tank = {new BlockPos(x, y + 2, z), new BlockPos(x + 2, y + 2, z), new BlockPos(x + 4, y + 2, z)};
		// Vitrine em terra (o modo fluido chega com o Cano): parada, funcionando e em sobrecarga, de frente para a câmera.
		BlockPos[] show = {new BlockPos(x + 1, y, z - 8), new BlockPos(x + 3, y, z - 8), new BlockPos(x + 5, y, z - 8)};
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			for (int i = 0; i < 3; i++) {
				level.setBlockAndUpdate(tank[i].below(), i < 2 ? Blocks.MAGMA_BLOCK.defaultBlockState() : Blocks.STONE.defaultBlockState());
				level.setBlockAndUpdate(tank[i], vent(Direction.NORTH).setValue(Waterlogging.WATERLOGGED, true));
				level.setBlockAndUpdate(show[i].below(), Blocks.POLISHED_DEEPSLATE.defaultBlockState());
				level.setBlockAndUpdate(show[i], vent(Direction.NORTH));
				if (i > 0) {
					WaterGeneratorBlockEntity.FORCED.add(show[i]);
				}
			}
			gen(level, tank[1]).energy.amount = gen(level, tank[1]).energy.capacity;
			gen(level, show[2]).energy.amount = gen(level, show[2]).energy.capacity;
		});
		// O cliente só roda os block entities perto do jogador: a câmera vai para lá antes de esperar.
		tp(server, x + 2.5, y + 2.6, z - 3.4, 0, 18);
		context.waitTicks(60);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			check(gen(level, tank[0]).machineState() == MachineState.RUNNING, "Fenda no aquário: devia funcionar, está " + gen(level, tank[0]).machineState());
			check(gen(level, tank[1]).machineState() == MachineState.OVERLOAD, "Fenda cheia: devia entrar em sobrecarga");
			check(gen(level, tank[2]).machineState() == MachineState.IDLE, "Fenda sem magma: devia ficar parada");
			check(level.getFluidState(tank[0]).isSource(), "Fenda: devia guardar a água dentro");
		});
		check(context.computeOnClient(mc -> mc.level.getBlockEntity(tank[1]) instanceof WaterGeneratorBlockEntity be
			&& be.motion().state() == MachineState.OVERLOAD), "Fenda: o cliente não recebeu a sobrecarga");
		check(context.computeOnClient(mc -> mc.level.getBlockEntity(show[1]) instanceof WaterGeneratorBlockEntity be
			&& be.motion().state() == MachineState.RUNNING), "Fenda: o cliente não recebeu o funcionando");

		// Capturas sem HUD, em espectador (a câmera atravessa blocos e não mostra a mão), apontada para o centro da máquina.
		server.runCommand("gamemode spectator @p");
		context.runOnClient(mc -> {
			if (!mc.gui.hud.isHidden()) {
				mc.gui.hud.toggle();
			}
		});
		look(server, x + 2.5, y + 3.4, z - 4.6, x + 2.5, y + 2.45, z + 0.5);
		shot(context, world, "maquinas_00_aquario");
		look(server, x + 1.35, y + 3.05, z - 1.35, x + 0.5, y + 2.42, z + 0.5);
		shot(context, world, "maquinas_01_aquario_dentro");
		look(server, x + 3.5, y + 1.75, z - 11.2, x + 3.5, y + 0.35, z - 7.5);
		shot(context, world, "maquinas_02_vitrine");
		// Close de cada estado na vitrine: parada (0), funcionando (1) e sobrecarga (2), pela frente e à esquerda.
		String[] names = {"parada", "funcionando", "sobrecarga"};
		for (int i = 0; i < 3; i++) {
			BlockPos p = show[i];
			look(server, p.getX() + 1.35, p.getY() + 0.98, p.getZ() - 0.35, p.getX() + 0.5, p.getY() + 0.42, p.getZ() + 0.5);
			shot(context, world, "maquinas_0" + (3 + i) + "_" + names[i]);
		}
		BlockPos run = show[1];
		look(server, run.getX() - 0.35, run.getY() + 0.98, run.getZ() + 1.35, run.getX() + 0.5, run.getY() + 0.42, run.getZ() + 0.5);
		shot(context, world, "maquinas_06_funcionando_tras");
		look(server, run.getX() + 0.5, run.getY() + 1.75, run.getZ() - 0.35, run.getX() + 0.5, run.getY() + 0.5, run.getZ() + 0.55);
		shot(context, world, "maquinas_07_funcionando_cima");
		context.runOnClient(mc -> {
			if (mc.gui.hud.isHidden()) {
				mc.gui.hud.toggle();
			}
		});
		// Desligar no meio do funcionando: a Fenda passa por stop e volta a parada.
		server.runOnServer(s -> WaterGeneratorBlockEntity.FORCED.remove(show[1]));
		context.waitTicks(10);
		check(server.computeOnServer(s -> gen(s.overworld(), show[1]).machineState()) == MachineState.IDLE, "Fenda: devia parar sem produção");
		// O item na mão usa o modelo gerado na pose parada.
		server.runCommand("gamemode creative @p");
		server.runOnServer(s -> s.getPlayerList().getPlayers().get(0).setItemInHand(InteractionHand.MAIN_HAND,
			new ItemStack(ModEnergy.VENT_GENERATOR)));
		look(server, x + 3.5, y + 1.62, z - 12.0, x + 3.5, y + 1.2, z - 4.0);
		shot(context, world, "maquinas_08_item");
		server.runOnServer(s -> {
			WaterGeneratorBlockEntity.FORCED.clear();
			var player = s.getPlayerList().getPlayers().get(0);
			player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		});
	}

	private static BlockState vent(Direction facing) {
		return ModEnergy.VENT_GENERATOR.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing);
	}

	private static WaterGeneratorBlockEntity gen(ServerLevel level, BlockPos pos) {
		check(level.getBlockEntity(pos) instanceof WaterGeneratorBlockEntity, "Fenda: sem block entity em " + pos);
		return (WaterGeneratorBlockEntity) level.getBlockEntity(pos);
	}

	private static void shot(ClientGameTestContext context, TestSingleplayerContext world, String name) {
		world.getConnection().waitForChunksRender();
		context.waitTicks(12);
		context.runOnClient(mc -> {
			mc.gui.toastManager().clear();
			mc.gui.hud.getChat().clearMessages(false);
		});
		context.waitTick();
		context.takeScreenshot(name);
	}

	private static boolean close(float a, double b) {
		return Math.abs(a - b) < 1.0E-4;
	}

	/** Põe o olho do jogador em (ex, ey, ez) olhando para o alvo (o tp usa os pés: o olho fica 1,62 acima). */
	private static void look(TestServerContext server, double ex, double ey, double ez, double tx, double ty, double tz) {
		double dx = tx - ex, dy = ty - ey, dz = tz - ez;
		float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
		float pitch = (float) Math.toDegrees(-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
		tp(server, ex, ey - 1.62, ez, yaw, pitch);
	}

	private static void tp(TestServerContext server, double x, double y, double z, float yaw, float pitch) {
		server.runCommand(String.format(Locale.ROOT, "tp @p %.2f %.2f %.2f %.1f %.1f", x, y, z, yaw, pitch));
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
