package com.aquamarine.gametest;

import com.aquamarine.entity.ModVillagers;
import com.aquamarine.harbor.HarborAlert;
import com.aquamarine.harbor.HarborTrades;
import com.aquamarine.pressure.PressureManager;

import java.util.List;
import java.util.Locale;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;

/**
 * QA da Vila num mundo normal gerado de verdade (parte da 3Q): {@code /locate structure #aquamarine:harbor}
 * acha um Porto; ele surge no fundo do oceano com a Cúpula seca, a Torre de Vigia na superfície acima
 * dela, os elevadores de bolhas e os Mergulhadores; a Eclusa zera a Pressão de quem entra; e o Cartógrafo
 * vende o Mapa de Fossas de Moréia. Abre um segundo mundo (semente fixa), depois do mundo plano da suíte.
 * Chamado por {@link AquamarineClientGameTest}; 2 screenshots.
 */
final class WorldgenGameTest {
	private static final String SEED = "aquamarine";

	static void run(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().adjustSettings(WorldgenGameTest::normalWorld).create()) {
			TestServerContext server = world.getServer();
			world.getConnection().waitForChunksRender();
			server.runCommand("time set noon");
			server.runCommand("weather clear");
			server.runCommand("gamemode spectator @a");

			int found = server.computeOnServer(s -> {
				try {
					return s.getCommands().getDispatcher().execute("locate structure #aquamarine:harbor", s.createCommandSourceStack()
						.withPosition(player(s).position()).withSuppressedOutput());
				} catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
					throw new AssertionError("Porto: /locate não achou nenhum Porto: " + e.getMessage());
				}
			});
			check(found > 0, "Porto: /locate não achou nenhum Porto");
			BoundingBox box = server.computeOnServer(WorldgenGameTest::harborBox);
			int seaLevel = server.computeOnServer(s -> s.overworld().getSeaLevel());
			BlockPos center = new BlockPos(box.getCenter().getX(), box.minY(), box.getCenter().getZ());
			server.runCommand(String.format("forceload add %d %d %d %d", box.minX() - 8, box.minZ() - 8, box.maxX() + 8, box.maxZ() + 8));
			tp(server, center.getX() + 0.5, seaLevel + 12, center.getZ() + 0.5, 0, 90);
			world.getConnection().waitForChunksRender();
			context.waitTicks(100);

			BlockPos bell = server.computeOnServer(s -> HarborAlert.findBell(s.overworld(), center, 4));
			check(bell != null, "Porto: sem Sino no centro da peça " + box);
			check(seaLevel - bell.getY() >= 20, "Porto: devia estar no fundo do oceano, o Sino está a " + (seaLevel - bell.getY()) + " m");
			check(server.computeOnServer(s -> ModVillagers.isInHarbor(s.overworld(), bell.above())), "Porto: a estrutura não ficou registrada no mundo");
			int wet = server.computeOnServer(s -> waterInsideDome(s.overworld(), bell));
			check(wet == 0, "Porto: entrou água na Cúpula (" + wet + " blocos)");
			BlockPos rod = server.computeOnServer(s -> find(s.overworld(), bell.atY(seaLevel - 2), 8, 16, st -> st.getBlock() instanceof LightningRodBlock));
			check(rod != null, "Porto: a Torre de Vigia não surgiu na superfície acima da Cúpula (Sino em " + bell + ")");
			List<Villager> divers = server.computeOnServer(s -> s.overworld().getEntitiesOfClass(Villager.class,
				new AABB(bell).inflate(48), HarborTrades::isDiver));
			check(divers.size() >= 9, "Porto: devia ter 9 Mergulhadores no mundo gerado, achou " + divers.size());
			boolean trenchMap = server.computeOnServer(s -> divers.stream()
				.filter(v -> v.getVillagerData().profession().is(ModVillagers.NAUTICAL_CARTOGRAPHER))
				.flatMap(v -> v.getOffers().stream()).map(offer -> offer.getResult()).anyMatch(WorldgenGameTest::isTrenchMap));
			check(trenchMap, "Cartógrafo Náutico: devia vender o Mapa de Fossas de Moréia");

			airlock(context, server, bell);
			BlockPos up = server.computeOnServer(s -> find(s.overworld(), bell, 40, 24, st -> st.is(Blocks.BUBBLE_COLUMN)
				&& !st.getValue(BubbleColumnBlock.DRAG_DOWN)));
			BlockPos down = server.computeOnServer(s -> find(s.overworld(), bell, 40, 24, st -> st.is(Blocks.BUBBLE_COLUMN)
				&& st.getValue(BubbleColumnBlock.DRAG_DOWN)));
			// A Eclusa deixou o jogador uns 15 s no Porto: o zelador dos elevadores já passou.
			String sand = server.computeOnServer(s -> {
				StringBuilder list = new StringBuilder();
				for (BlockPos pos : BlockPos.betweenClosed(bell.offset(-40, -2, -40), bell.offset(40, 0, 40))) {
					if (s.overworld().getBlockState(pos).is(Blocks.SOUL_SAND) || s.overworld().getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
						list.append(pos.toShortString()).append(' ').append(s.overworld().getBlockState(pos).getBlock().getDescriptionId())
							.append(" -> ").append(s.overworld().getBlockState(pos.above())).append("; ");
					}
				}
				return list.toString();
			});
			check(up != null && down != null, "Porto: os elevadores de bolhas do Cais não se formaram (sobe " + up + ", desce " + down + "): " + sand);
			BlockPos top = server.computeOnServer(s -> find(s.overworld(), bell.atY(seaLevel - 3), 40, 0, st -> st.is(Blocks.BUBBLE_COLUMN)
				&& !st.getValue(BubbleColumnBlock.DRAG_DOWN)));
			check(top != null, "Porto: o elevador que sobe não chega à superfície");

			tp(server, bell.getX() + 0.5, bell.getY() + 2.0, bell.getZ() + 9.5, 180, 10);
			world.getConnection().waitForChunksRender();
			context.waitTicks(20);
			shot(context, "37_porto_mundo_gerado");
			tp(server, rod.getX() + 10.5, seaLevel + 4, rod.getZ() + 10.5, 135, 10);
			world.getConnection().waitForChunksRender();
			context.waitTicks(20);
			shot(context, "38_torre_de_vigia");
		}
	}

	/** Mundo normal com estruturas (as configurações "consistentes" do teste usam mundo plano sem estruturas). */
	private static void normalWorld(WorldCreationUiState creator) {
		creator.setSeed(SEED);
		creator.setGenerateStructures(true);
		creator.getNormalPresetList().stream().filter(entry -> entry.preset() != null && entry.preset().is(WorldPresets.NORMAL))
			.findFirst().ifPresent(creator::setWorldType);
	}

	/** Caixa da peça principal do Porto mais próximo do jogador. */
	private static BoundingBox harborBox(MinecraftServer s) {
		ServerLevel level = s.overworld();
		BlockPos located = level.findNearestMapStructure(ModVillagers.HARBORS, player(s).blockPosition(), 100, false);
		check(located != null, "Porto: nenhum Porto em 100 chunks");
		Structure harbor = s.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValueOrThrow(ModVillagers.HARBOR);
		StructureStart start = level.getChunk(located.getX() >> 4, located.getZ() >> 4, ChunkStatus.STRUCTURE_STARTS).getStartForStructure(harbor);
		check(start != null && start.isValid() && !start.getPieces().isEmpty(), "Porto: o /locate apontou um chunk sem o início do Porto");
		return start.getPieces().getFirst().getBoundingBox();
	}

	private static int waterInsideDome(ServerLevel level, BlockPos bell) {
		int wet = 0;
		for (BlockPos pos : BlockPos.betweenClosed(bell.offset(-11, 0, -11), bell.offset(11, 10, 11))) {
			if (pos.distSqr(bell) <= 11 * 11 && level.getFluidState(pos).is(FluidTags.WATER)) {
				wet++;
			}
		}
		return wet;
	}

	private static BlockPos find(ServerLevel level, BlockPos from, int radius, int height, java.util.function.Predicate<BlockState> test) {
		for (BlockPos pos : BlockPos.betweenClosed(from.offset(-radius, 0, -radius), from.offset(radius, height, radius))) {
			if (test.test(level.getBlockState(pos))) {
				return pos.immutable();
			}
		}
		return null;
	}

	private static boolean isTrenchMap(ItemStack stack) {
		Component name = stack.get(DataComponents.ITEM_NAME);
		return stack.has(DataComponents.MAP_ID) && name != null && name.getContents() instanceof TranslatableContents t
			&& t.getKey().equals("item.aquamarine.trench_map");
	}

	/** Eclusa: sob a água fora da Cúpula a Pressão sobe; ao entrar no ar do Porto ela zera. */
	private static void airlock(ClientGameTestContext context, TestServerContext server, BlockPos bell) {
		// Finge um mar mais fundo (60 m acima do Sino) para a Pressão ambiental subir em poucos segundos.
		PressureManager.setTestSeaLevel(bell.getY() + 60);
		server.runCommand("gamemode survival @a");
		server.runOnServer(s -> {
			ServerPlayer player = player(s);
			player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 2000));
			PressureManager.reset(player);
		});
		// 45 graus nunca cai numa doca nem num elevador do Cais, qualquer que seja o giro da peça.
		tp(server, bell.getX() + 24.5, bell.getY() + 3, bell.getZ() + 24.5, 0, 0);
		context.waitTicks(260);
		int outside = server.computeOnServer(s -> PressureManager.level(player(s)));
		check(outside >= 1, "Eclusa: fora da Cúpula a Pressão devia subir, ficou " + outside);
		tp(server, bell.getX() + 0.5, bell.getY(), bell.getZ() + 3.5, 0, 0);
		context.waitTicks(40);
		int inside = server.computeOnServer(s -> PressureManager.level(player(s)));
		check(inside == 0, "Eclusa: no ar do Porto a Pressão devia zerar, ficou " + inside);
		PressureManager.setTestSeaLevel(null);
		server.runCommand("gamemode spectator @a");
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

	private WorldgenGameTest() {}
}
