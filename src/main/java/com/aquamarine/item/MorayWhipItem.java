package com.aquamarine.item;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.ElectricDamage;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Weapon;

/**
 * Chicote de Moréia (docs/08-fase3.md, seção 3): 7 de dano, ataque rápido, alcance 3,5 e corrente
 * elétrica nos alvos molhados a 4 blocos do golpe (50% do dano). Durabilidade 450.
 */
public class MorayWhipItem extends Item {
	public static final float DAMAGE = 7.0F;
	public static final float CURRENT_FRACTION = 0.5F;
	public static final double CURRENT_RADIUS = 4.0;
	public static final int DURABILITY = 450;

	public MorayWhipItem(Properties properties) {
		super(properties);
	}

	public static Properties properties() {
		return new Properties()
			.durability(DURABILITY)
			.enchantable(15)
			.rarity(Rarity.RARE)
			.attributes(ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, DAMAGE - 1.0F, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.0, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Aquamarine.id("moray_whip_reach"), 0.5,
					AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build())
			.component(DataComponents.WEAPON, new Weapon(1));
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level() instanceof ServerLevel level) {
			ElectricDamage.chain(level, attacker, target.position().add(0, target.getBbHeight() * 0.5, 0), CURRENT_RADIUS,
				DAMAGE * CURRENT_FRACTION, target);
		}
	}
}
