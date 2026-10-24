package com.aquamarine.item;

import com.aquamarine.Aquamarine;

import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Efeitos do Oricálcio (docs/05-itens.md):
 * <ul>
 *   <li>Ferramentas: "Maré", mineram debaixo d'água sem a penalidade da água.</li>
 *   <li>Conjunto completo de armadura: nado mais rápido. Os outros bônus (Nuvem de Tinta e
 *       Pressão) dependem do Kraken e leem {@link #armorPieces} quando existirem.</li>
 * </ul>
 */
public final class OrichalcumEffects {
	private static final Identifier TIDE_MINING_ID = Aquamarine.id("tide_mining");
	private static final Identifier SET_SWIM_ID = Aquamarine.id("orichalcum_set_swim");
	/**
	 * Eficiência de movimento na água do conjunto completo. Calibrada para cerca de 25% a mais
	 * de velocidade nadando (a física de nado do vanilla não é linear nesse atributo).
	 */
	private static final double SET_SWIM_BONUS = 0.33;
	/** Redução da subida da Pressão por peça de Oricálcio vestida (60% com as 4). */
	public static final float PRESSURE_REDUCTION_PER_PIECE = 0.15F;

	private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static void register() {
		List<Item> tools = List.of(ModItems.ORICHALCUM_SWORD, ModItems.ORICHALCUM_PICKAXE, ModItems.ORICHALCUM_AXE,
			ModItems.ORICHALCUM_SHOVEL, ModItems.ORICHALCUM_HOE);
		// Maré: o atributo vale 0,2 por padrão debaixo d'água; +0,8 deixa igual a minerar no seco.
		DefaultItemComponentEvents.MODIFY.register(context -> context.modify(tools, (builder, item) -> {
			ItemAttributeModifiers current = builder.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
			builder.set(DataComponents.ATTRIBUTE_MODIFIERS, current.withModifierAdded(
				Attributes.SUBMERGED_MINING_SPEED,
				new AttributeModifier(TIDE_MINING_ID, 0.8, AttributeModifier.Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND));
		}));

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 10 != 0) {
				return;
			}
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				updateSetBonus(player);
			}
		});
	}

	/** Quantas peças da armadura de Oricálcio a entidade está vestindo (0 a 4). */
	public static int armorPieces(LivingEntity entity) {
		int pieces = 0;
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			Item item = entity.getItemBySlot(slot).getItem();
			if (item == ModItems.ORICHALCUM_HELMET || item == ModItems.ORICHALCUM_CHESTPLATE
				|| item == ModItems.ORICHALCUM_LEGGINGS || item == ModItems.ORICHALCUM_BOOTS) {
				pieces++;
			}
		}
		return pieces;
	}

	public static boolean hasFullSet(LivingEntity entity) {
		return armorPieces(entity) == ARMOR_SLOTS.length;
	}

	private static void updateSetBonus(ServerPlayer player) {
		AttributeInstance swim = player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY);
		if (swim == null) {
			return;
		}
		boolean full = hasFullSet(player);
		if (full && !swim.hasModifier(SET_SWIM_ID)) {
			swim.addTransientModifier(new AttributeModifier(SET_SWIM_ID, SET_SWIM_BONUS, AttributeModifier.Operation.ADD_VALUE));
		} else if (!full && swim.hasModifier(SET_SWIM_ID)) {
			swim.removeModifier(SET_SWIM_ID);
		}
	}

	private OrichalcumEffects() {}
}
