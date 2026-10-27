package com.aquamarine.recipe;

import com.aquamarine.Aquamarine;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Receitas do mod para quem entra num servidor ou num mundo aberto em LAN (pedido do Adriano em 26/10/2026: "deixar o
 * craft de todos os itens visíveis"). Desde o 1.21.2 o cliente não recebe as receitas, e o JEI só lê as do servidor
 * integrado: os convidados não viam nenhuma receita da Forja (todo o caminho do Neutrônio). O servidor manda, na
 * configuração da conexão (antes do JEI carregar), cada receita {@code aquamarine:} em JSON; o cliente guarda e o plugin do
 * JEI lê ({@code com.aquamarine.client.compat.jei.SyncedRecipes}). Cliente sem o mod não recebe nada.
 */
public record RecipeSyncPayload(List<String> recipes) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<RecipeSyncPayload> TYPE = new CustomPacketPayload.Type<>(Aquamarine.id("recipes"));
	public static final StreamCodec<FriendlyByteBuf, RecipeSyncPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.stringUtf8(1 << 16).apply(ByteBufCodecs.list()), RecipeSyncPayload::recipes, RecipeSyncPayload::new);

	public static void register() {
		PayloadTypeRegistry.clientboundConfiguration().register(TYPE, CODEC);
		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
			if (ServerConfigurationNetworking.canSend(handler, TYPE)) {
				ServerConfigurationNetworking.send(handler, of(server));
			}
		});
	}

	/** Cada receita do mod como {"id": ..., "recipe": {...}}. */
	public static RecipeSyncPayload of(MinecraftServer server) {
		RegistryOps<com.google.gson.JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
		List<String> out = new ArrayList<>();
		for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
			if (!holder.id().identifier().getNamespace().equals(Aquamarine.MOD_ID)) {
				continue;
			}
			Recipe.DIRECT_CODEC.encodeStart(ops, holder.value()).result().ifPresent(json -> {
				JsonObject entry = new JsonObject();
				entry.addProperty("id", holder.id().identifier().toString());
				entry.add("recipe", json);
				out.add(entry.toString());
			});
		}
		return new RecipeSyncPayload(out);
	}

	@Override
	public CustomPacketPayload.Type<RecipeSyncPayload> type() {
		return TYPE;
	}
}
