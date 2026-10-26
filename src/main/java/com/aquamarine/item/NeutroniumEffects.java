package com.aquamarine.item;

import com.aquamarine.Aquamarine;
import java.util.List;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Neutrônio na água (docs/10-fase4.md, §8):
 * <ul>
 *   <li>Ferramentas: mineram debaixo d'água sem perder velocidade; a espada ganha +3 de dano submersa.</li>
 *   <li>Capacete: respiração aquática e visão noturna submerso. Peitoral: imune à Pressão da profundidade, e a do
 *       Kraken sobe {@link #PRESSURE_REDUCTION} mais devagar. Calças: Graça do Golfinho. Botas: Passos Profundos III.</li>
 *   <li>Conjunto completo (Singularidade Abissal): submerso, regeneração I e +20% de dano; imune à Nuvem de Tinta e
 *       ao Redemoinho do Kraken; aura de estrelas.</li>
 * </ul>
 */
public final class NeutroniumEffects {
	private static final Identifier TIDE_MINING_ID = Aquamarine.id("neutronium_tide_mining");
	private static final Identifier DEPTH_STRIDER_ID = Aquamarine.id("neutronium_depth_strider");
	private static final Identifier SET_DAMAGE_ID = Aquamarine.id("neutronium_set_damage");
	private static final Identifier SWORD_DAMAGE_ID = Aquamarine.id("neutronium_sword_underwater");
	/** O peitoral deixa a subida da Pressão do Kraken 75% mais lenta (o conjunto Abissal inteiro dá 60%). */
	public static final float PRESSURE_REDUCTION = 0.75F;
	private static final double SET_DAMAGE_BONUS = 0.2;
	private static final double SWORD_UNDERWATER_DAMAGE = 3.0;
	private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static void register() {
		List<Item> tools = List.of(ModItems.NEUTRONIUM_SWORD, ModItems.NEUTRONIUM_PICKAXE, ModItems.NEUTRONIUM_AXE,
			ModItems.NEUTRONIUM_SHOVEL, ModItems.NEUTRONIUM_HOE);
		DefaultItemComponentEvents.MODIFY.register(context -> {
			context.modify(tools, (builder, item) -> {
				ItemAttributeModifiers current = builder.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
				builder.set(DataComponents.ATTRIBUTE_MODIFIERS, current.withModifierAdded(Attributes.SUBMERGED_MINING_SPEED,
					new AttributeModifier(TIDE_MINING_ID, 0.8, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
			});
			// Botas: Passos Profundos III embutido (o atributo do encantamento no máximo).
			context.modify(ModItems.NEUTRONIUM_BOOTS, builder -> {
				ItemAttributeModifiers current = builder.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
				builder.set(DataComponents.ATTRIBUTE_MODIFIERS, current.withModifierAdded(Attributes.WATER_MOVEMENT_EFFICIENCY,
					new AttributeModifier(DEPTH_STRIDER_ID, 1.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET));
			});
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 10 != 0) {
				return;
			}
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tick(player);
			}
		});
	}

	/** Molde de ferraria: peça Abissal + Lingote de Neutrônio dá a peça de Neutrônio. */
	public static SmithingTemplateItem template(Item.Properties properties) {
		String key = "item.aquamarine.smithing_template.neutronium_upgrade.";
		return new SmithingTemplateItem(
			Component.translatable(key + "applies_to").withStyle(ChatFormatting.BLUE),
			Component.translatable(key + "ingredients").withStyle(ChatFormatting.BLUE),
			Component.translatable(key + "base_slot_description"),
			Component.translatable(key + "additions_slot_description"),
			List.of(slot("helmet"), slot("sword"), slot("chestplate"), slot("pickaxe"), slot("leggings"), slot("axe"),
				slot("boots"), slot("hoe"), slot("shovel")),
			List.of(slot("ingot")), properties);
	}

	private static Identifier slot(String name) {
		return Identifier.withDefaultNamespace("container/slot/" + name);
	}

	/** Quantas peças de Neutrônio a entidade veste (0 a 4). */
	public static int armorPieces(LivingEntity entity) {
		int pieces = 0;
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			Item item = entity.getItemBySlot(slot).getItem();
			if (item == ModItems.NEUTRONIUM_HELMET || item == ModItems.NEUTRONIUM_CHESTPLATE
				|| item == ModItems.NEUTRONIUM_LEGGINGS || item == ModItems.NEUTRONIUM_BOOTS) {
				pieces++;
			}
		}
		return pieces;
	}

	public static boolean hasFullSet(LivingEntity entity) {
		return armorPieces(entity) == ARMOR_SLOTS.length;
	}

	/** O peitoral anula a Pressão da profundidade. */
	public static boolean blocksAmbientPressure(LivingEntity entity) {
		return entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.NEUTRONIUM_CHESTPLATE);
	}

	/** Redução da subida da Pressão do Kraken dada pelo peitoral. */
	public static float pressureReduction(LivingEntity entity) {
		return blocksAmbientPressure(entity) ? PRESSURE_REDUCTION : 0.0F;
	}

	private static void tick(ServerPlayer player) {
		boolean wet = player.isUnderWater();
		boolean full = hasFullSet(player);
		if (wet && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.NEUTRONIUM_HELMET)) {
			refresh(player, MobEffects.WATER_BREATHING, 300, 0);
			refresh(player, MobEffects.NIGHT_VISION, 300, 0);
		}
		if (wet && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.NEUTRONIUM_LEGGINGS)) {
			refresh(player, MobEffects.DOLPHINS_GRACE, 60, 0);
		}
		if (wet && full) {
			refresh(player, MobEffects.REGENERATION, 200, 0);
			if (player.level() instanceof ServerLevel level && player.getRandom().nextInt(3) == 0) {
				level.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 1.0, player.getZ(), 3, 0.5, 0.7, 0.5, 0.02);
				level.sendParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 1.0, player.getZ(), 1, 0.6, 0.8, 0.6, 0.0);
			}
		}
		AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
		if (attack != null) {
			toggle(attack, SET_DAMAGE_ID, wet && full, SET_DAMAGE_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
			toggle(attack, SWORD_DAMAGE_ID, wet && player.getMainHandItem().is(ModItems.NEUTRONIUM_SWORD), SWORD_UNDERWATER_DAMAGE,
				AttributeModifier.Operation.ADD_VALUE);
		}
	}

	/** Renova o efeito só quando falta pouco: renovar todo tique zeraria o relógio da regeneração. */
	private static void refresh(ServerPlayer player, Holder<MobEffect> effect, int duration, int amplifier) {
		MobEffectInstance current = player.getEffect(effect);
		if (current == null || current.getDuration() < duration / 2) {
			player.addEffect(new MobEffectInstance(effect, duration, amplifier, true, false, true));
		}
	}

	private static void toggle(AttributeInstance attribute, Identifier id, boolean on, double amount, AttributeModifier.Operation operation) {
		if (on && !attribute.hasModifier(id)) {
			attribute.addTransientModifier(new AttributeModifier(id, amount, operation));
		} else if (!on && attribute.hasModifier(id)) {
			attribute.removeModifier(id);
		}
	}

	private NeutroniumEffects() {}
}
