package com.aquamarine.client;

import com.aquamarine.recipe.RecipeSyncPayload;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Receitas do mod que o servidor mandou ao entrar ({@link RecipeSyncPayload}): o JEI dos convidados de um servidor ou de
 * um mundo em LAN lê daqui, porque só o dono do mundo tem o servidor integrado. Fica em JSON até o JEI pedir, quando
 * os registros e as tags do mundo já chegaram.
 */
public final class SyncedRecipes {
	private static volatile List<String> raw = List.of();

	public static void register() {
		ClientConfigurationNetworking.registerGlobalReceiver(RecipeSyncPayload.TYPE, (payload, context) -> raw = List.copyOf(payload.recipes()));
	}

	public static List<RecipeHolder<?>> decode(RegistryAccess access) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);
		List<RecipeHolder<?>> out = new ArrayList<>();
		for (String line : raw) {
			JsonObject entry = JsonParser.parseString(line).getAsJsonObject();
			ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.parse(entry.get("id").getAsString()));
			Recipe.DIRECT_CODEC.parse(ops, entry.get("recipe")).result().ifPresent(recipe -> out.add(new RecipeHolder<>(key, recipe)));
		}
		return out;
	}

	private SyncedRecipes() {}
}
