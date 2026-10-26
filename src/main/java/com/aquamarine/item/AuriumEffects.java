package com.aquamarine.item;

import com.aquamarine.Aquamarine;
import java.util.List;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Efeitos do Aurium (Fase 6, 6D1; docs/11-fase6.md), entre o Abissal e o Neutrônio:
 * <ul>
 *   <li>Ferramentas: mineram debaixo d'água sem a penalidade da água, como as Abissais.</li>
 *   <li>Conjunto completo, "Brilho Perolado": submerso, Poder do Conduíte (respiração, visão e pressa na água) e o mesmo
 *       nado rápido do conjunto Abissal. Menor que os bônus do Neutrônio, que valem por peça e somam a Singularidade.</li>
 * </ul>
 */
public final class AuriumEffects {
	private static final Identifier TIDE_MINING_ID = Aquamarine.id("aurium_tide_mining");
	private static final Identifier SET_SWIM_ID = Aquamarine.id("aurium_set_swim");
	private static final double SET_SWIM_BONUS = 0.33;
	/** Duração do Poder do Conduíte, renovado a cada meio segundo (sobra para o ícone não piscar). */
	private static final int CONDUIT_TICKS = 220;
	private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static void register() {
		List<Item> tools = List.of(ModItems.AURIUM_SWORD, ModItems.AURIUM_PICKAXE, ModItems.AURIUM_AXE, ModItems.AURIUM_SHOVEL,
			ModItems.AURIUM_HOE);
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

	/** Quantas peças da armadura de Aurium a entidade está vestindo (0 a 4). */
	public static int armorPieces(LivingEntity entity) {
		int pieces = 0;
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			Item item = entity.getItemBySlot(slot).getItem();
			if (item == ModItems.AURIUM_HELMET || item == ModItems.AURIUM_CHESTPLATE || item == ModItems.AURIUM_LEGGINGS
				|| item == ModItems.AURIUM_BOOTS) {
				pieces++;
			}
		}
		return pieces;
	}

	public static boolean hasFullSet(LivingEntity entity) {
		return armorPieces(entity) == ARMOR_SLOTS.length;
	}

	private static void updateSetBonus(ServerPlayer player) {
		boolean full = hasFullSet(player);
		if (full && player.isUnderWater()) {
			player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, CONDUIT_TICKS, 0, true, false, true));
		}
		AttributeInstance swim = player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY);
		if (swim == null) {
			return;
		}
		if (full && !swim.hasModifier(SET_SWIM_ID)) {
			swim.addTransientModifier(new AttributeModifier(SET_SWIM_ID, SET_SWIM_BONUS, AttributeModifier.Operation.ADD_VALUE));
		} else if (!full && swim.hasModifier(SET_SWIM_ID)) {
			swim.removeModifier(SET_SWIM_ID);
		}
	}

	private AuriumEffects() {}
}
