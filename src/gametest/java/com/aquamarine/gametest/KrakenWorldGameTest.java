package com.aquamarine.gametest;

import com.aquamarine.entity.boss.KrakenWorldEffects;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * A luta do Kraken marca o mundo (docs/02-kraken.md, "A luta muda o mundo"), parte que dá para conferir
 * sem sorteio: o coral vivo vira o morto com os mesmos estados, a cratera quebra o que não é duro e
 * poupa obsidiana e baús, e com {@code mob_griefing} desligado nada quebra. A tempestade, o céu e a música
 * por fase são conferidos na parte {@code kraken}. Sem screenshots.
 */
final class KrakenWorldGameTest {
	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		server.runOnServer(s -> {
			check(KrakenWorldEffects.deadCoral(Blocks.TUBE_CORAL_BLOCK.defaultBlockState()).is(Blocks.DEAD_TUBE_CORAL_BLOCK),
				"Recife: o bloco de coral devia morrer");
			BlockState fan = Blocks.BRAIN_CORAL_WALL_FAN.defaultBlockState().setValue(BaseCoralWallFanBlock.FACING, Direction.EAST);
			BlockState dead = KrakenWorldEffects.deadCoral(fan);
			check(dead != null && dead.is(Blocks.DEAD_BRAIN_CORAL_WALL_FAN) && dead.getValue(BaseCoralWallFanBlock.FACING) == Direction.EAST,
				"Recife: o leque de parede morto devia manter a direção");
			check(KrakenWorldEffects.deadCoral(Blocks.DEAD_TUBE_CORAL_BLOCK.defaultBlockState()) == null, "Recife: coral morto não muda");
			check(KrakenWorldEffects.deadCoral(Blocks.SAND.defaultBlockState()) == null, "Recife: areia não é coral");
		});
		BlockPos c = base.offset(-60, 12, 60);
		server.runOnServer(s -> fillCrater(s.overworld(), c));
		server.runCommand("gamerule mob_griefing false");
		int none = server.computeOnServer(s -> KrakenWorldEffects.crater(s.overworld(), Vec3.atCenterOf(c), 1.6, KrakenWorldEffects.CRATER_MAX_HARDNESS));
		check(none == 0, "Cratera: com mob_griefing desligado nada devia quebrar");
		server.runCommand("gamerule mob_griefing true");
		int broken = server.computeOnServer(s -> KrakenWorldEffects.crater(s.overworld(), Vec3.atCenterOf(c), 1.6, KrakenWorldEffects.CRATER_MAX_HARDNESS));
		check(broken > 10, "Cratera: a areia devia quebrar (quebrou " + broken + ")");
		check(server.computeOnServer(s -> {
			BlockState center = s.overworld().getBlockState(c);
			return center.isAir() || center.is(Blocks.WATER);
		}), "Cratera: o centro devia abrir");
		check(server.computeOnServer(s -> s.overworld().getBlockState(c.east()).is(Blocks.OBSIDIAN)), "Cratera: obsidiana fica");
		check(server.computeOnServer(s -> s.overworld().getBlockState(c.west()).is(Blocks.CHEST)), "Cratera: baú fica");
		server.runOnServer(s -> {
			for (BlockPos p : BlockPos.betweenClosed(c.offset(-3, -3, -3), c.offset(3, 3, 3))) {
				s.overworld().setBlock(p, Blocks.AIR.defaultBlockState(), 3);
			}
		});
	}

	private static void fillCrater(ServerLevel level, BlockPos c) {
		level.getChunkAt(c);
		for (BlockPos p : BlockPos.betweenClosed(c.offset(-2, -2, -2), c.offset(2, 2, 2))) {
			level.setBlock(p, Blocks.SAND.defaultBlockState(), 2);
		}
		level.setBlock(c.east(), Blocks.OBSIDIAN.defaultBlockState(), 2);
		level.setBlock(c.west(), Blocks.CHEST.defaultBlockState(), 2);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private KrakenWorldGameTest() {}
}
