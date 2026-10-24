package com.aquamarine.item;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.ModBlocks;
import com.aquamarine.effect.ModEffects;
import com.aquamarine.pressure.PressureState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;

/**
 * Itens do mod, na ordem em que aparecem na aba criativa "Aquamarine".
 * Item novo: registre aqui com {@link #register} e ele entra na aba sozinho.
 */
public final class ModItems {
	private static final List<Item> TAB_ORDER = new ArrayList<>();

	// Materiais
	public static final Item ORICHALCUM_ORE = registerBlock(ModBlocks.ORICHALCUM_ORE, new Item.Properties());
	public static final Item ORICHALCUM_INGOT = register("orichalcum_ingot", Item::new, new Item.Properties());
	public static final Item ORICHALCUM_BLOCK = registerBlock(ModBlocks.ORICHALCUM_BLOCK, new Item.Properties());
	public static final Item ABYSSAL_PEARL = register("abyssal_pearl", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item TIDE_CRYSTAL = register("tide_crystal", Item::new, new Item.Properties());
	public static final Item ANCIENT_SCALE = register("ancient_scale", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));

	// Bancada
	public static final Item ABYSSAL_FORGE = registerBlock(ModBlocks.ABYSSAL_FORGE, new Item.Properties());

	// Ferramentas e arma de Oricálcio (mesmas bases das de diamante; o material dá o resto)
	public static final Item ORICHALCUM_SWORD = register("orichalcum_sword", Item::new,
		new Item.Properties().sword(ModMaterials.ORICHALCUM_TOOL, 3.0F, -2.4F));
	public static final Item ORICHALCUM_PICKAXE = register("orichalcum_pickaxe", Item::new,
		new Item.Properties().pickaxe(ModMaterials.ORICHALCUM_TOOL, 1.0F, -2.8F));
	public static final Item ORICHALCUM_AXE = register("orichalcum_axe", Item::new,
		new Item.Properties().axe(ModMaterials.ORICHALCUM_TOOL, 5.0F, -3.0F));
	public static final Item ORICHALCUM_SHOVEL = register("orichalcum_shovel", Item::new,
		new Item.Properties().shovel(ModMaterials.ORICHALCUM_TOOL, 1.5F, -3.0F));
	public static final Item ORICHALCUM_HOE = register("orichalcum_hoe", Item::new,
		new Item.Properties().hoe(ModMaterials.ORICHALCUM_TOOL, -3.0F, 0.0F));

	// Armadura de Oricálcio
	public static final Item ORICHALCUM_HELMET = register("orichalcum_helmet", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.ORICHALCUM_ARMOR, ArmorType.HELMET));
	public static final Item ORICHALCUM_CHESTPLATE = register("orichalcum_chestplate", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.ORICHALCUM_ARMOR, ArmorType.CHESTPLATE));
	public static final Item ORICHALCUM_LEGGINGS = register("orichalcum_leggings", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.ORICHALCUM_ARMOR, ArmorType.LEGGINGS));
	public static final Item ORICHALCUM_BOOTS = register("orichalcum_boots", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.ORICHALCUM_ARMOR, ArmorType.BOOTS));

	// Exploração
	/** Óculos de Mergulho: slot da cabeça; o efeito (menos névoa na água) é só do cliente. */
	public static final Item DIVING_GOGGLES = register("diving_goggles", Item::new, new Item.Properties()
		.stacksTo(1)
		.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
			.setAsset(ModMaterials.DIVING_GOGGLES_ASSET)
			.setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
			.build()));

	/** Tônico de Pressão: bebida de 5 min que reduz o máximo da Pressão em 2 níveis. */
	public static final Item PRESSURE_TONIC = register("pressure_tonic", Item::new, new Item.Properties()
		.stacksTo(16).usingConvertsTo(Items.GLASS_BOTTLE)
		.component(DataComponents.CONSUMABLE, Consumable.builder().consumeSeconds(1.6F)
			.animation(ItemUseAnimation.DRINK).sound(SoundEvents.GENERIC_DRINK)
			.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.PRESSURE_TONIC, PressureState.TONIC_TICKS)))
			.build()));
	/** Boia de Âncora: forma uma plataforma 3x3 na superfície da água por 90 s (recarga de 30 s). */
	public static final Item ANCHOR_BUOY = register("anchor_buoy", AnchorBuoyItem::new,
		new Item.Properties().useBlockDescriptionPrefix().stacksTo(16));

	// Invocação
	/** Projeto do Chamariz: vem do Cofre do Templo (ainda não existe); por enquanto só criativo. */
	public static final Item LURE_BLUEPRINT = register("lure_blueprint", Item::new,
		new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	/** Chamariz Abissal: usado no Altar do Abismo, invoca o Kraken e é consumido. */
	public static final Item ABYSSAL_LURE = register("abyssal_lure", Item::new,
		new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	/** Altar do Abismo: indestrutível como a Cidade; aqui só para o criativo e testes. */
	public static final Item ABYSSAL_ALTAR = registerBlock(ModBlocks.ABYSSAL_ALTAR, new Item.Properties().rarity(Rarity.EPIC));

	// Recompensas do Kraken (entram nas mãos de quem lutou; docs/02-kraken.md, "Recompensas")
	public static final Item KRAKEN_EYE = register("kraken_eye", Item::new, new Item.Properties().rarity(Rarity.RARE));
	public static final Item ABYSSAL_INK = register("abyssal_ink", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item KRAKEN_HEART = register("kraken_heart", Item::new, new Item.Properties().rarity(Rarity.EPIC));
	public static final Item KRAKEN_TROPHY = registerBlock(ModBlocks.KRAKEN_TROPHY, new Item.Properties().rarity(Rarity.EPIC));
	public static final Item GOLDEN_ABYSS_TROPHY = registerBlock(ModBlocks.GOLDEN_ABYSS_TROPHY, new Item.Properties().rarity(Rarity.EPIC));

	public static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Aquamarine.id(name));
		Item item = factory.apply(properties.setId(key));
		if (item instanceof BlockItem blockItem) {
			blockItem.registerBlocks(Item.BY_BLOCK, item);
		}
		TAB_ORDER.add(item);
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static Item registerBlock(Block block, Item.Properties properties) {
		String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
		return register(name, p -> new BlockItem(block, p), properties.useBlockDescriptionPrefix());
	}

	/** Itens na ordem da aba criativa. */
	public static List<Item> tabOrder() {
		return Collections.unmodifiableList(TAB_ORDER);
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModItems() {}
}
