package com.aquamarine.item;

import com.aquamarine.Aquamarine;
import com.aquamarine.ModTags;
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
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.Weapon;
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
	public static final Item ABYSSAL_ORE = registerBlock(ModBlocks.ABYSSAL_ORE, new Item.Properties());
	public static final Item ABYSSAL_INGOT = register("abyssal_ingot", Item::new, new Item.Properties());
	public static final Item ABYSSAL_BLOCK = registerBlock(ModBlocks.ABYSSAL_BLOCK, new Item.Properties());
	public static final Item ABYSSAL_PEARL = register("abyssal_pearl", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item TIDE_CRYSTAL = register("tide_crystal", Item::new, new Item.Properties());
	public static final Item ANCIENT_SCALE = register("ancient_scale", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));

	// Bancada
	public static final Item ABYSSAL_FORGE = registerBlock(ModBlocks.ABYSSAL_FORGE, new Item.Properties());

	// Ferramentas e arma Abissal (mesmas bases das de diamante; o material dá o resto)
	public static final Item ABYSSAL_SWORD = register("abyssal_sword", Item::new,
		new Item.Properties().sword(ModMaterials.ABYSSAL_TOOL, 3.0F, -2.4F));
	public static final Item ABYSSAL_PICKAXE = register("abyssal_pickaxe", Item::new,
		new Item.Properties().pickaxe(ModMaterials.ABYSSAL_TOOL, 1.0F, -2.8F));
	public static final Item ABYSSAL_AXE = register("abyssal_axe", Item::new,
		new Item.Properties().axe(ModMaterials.ABYSSAL_TOOL, 5.0F, -3.0F));
	public static final Item ABYSSAL_SHOVEL = register("abyssal_shovel", Item::new,
		new Item.Properties().shovel(ModMaterials.ABYSSAL_TOOL, 1.5F, -3.0F));
	public static final Item ABYSSAL_HOE = register("abyssal_hoe", Item::new,
		new Item.Properties().hoe(ModMaterials.ABYSSAL_TOOL, -3.0F, 0.0F));

	// Armadura Abissal
	public static final Item ABYSSAL_HELMET = register("abyssal_helmet", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.ABYSSAL_ARMOR, ArmorType.HELMET));
	public static final Item ABYSSAL_CHESTPLATE = register("abyssal_chestplate", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.ABYSSAL_ARMOR, ArmorType.CHESTPLATE));
	public static final Item ABYSSAL_LEGGINGS = register("abyssal_leggings", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.ABYSSAL_ARMOR, ArmorType.LEGGINGS));
	public static final Item ABYSSAL_BOOTS = register("abyssal_boots", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.ABYSSAL_ARMOR, ArmorType.BOOTS));

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

	// Fase 3A: Pressão por profundidade
	/** Medidor de Profundidade: na mão, o HUD mostra a profundidade e o nível de Pressão. */
	public static final Item DEPTH_GAUGE = register("depth_gauge", Item::new, new Item.Properties().stacksTo(1));
	/** Regulador de Pressão: na mão secundária, tira 2 níveis da Pressão ambiental; gasta 1 de durabilidade a cada 10 s ativo. */
	public static final Item PRESSURE_REGULATOR = register("pressure_regulator", Item::new,
		new Item.Properties().stacksTo(1).durability(DiveGear.REGULATOR_DURABILITY));
	// Traje de Mergulho Profundo
	public static final Item DEEP_DIVING_HELMET = register("deep_diving_helmet", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.DEEP_DIVING_ARMOR, ArmorType.HELMET));
	public static final Item DEEP_DIVING_CHESTPLATE = register("deep_diving_chestplate", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.DEEP_DIVING_ARMOR, ArmorType.CHESTPLATE));
	public static final Item DEEP_DIVING_LEGGINGS = register("deep_diving_leggings", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.DEEP_DIVING_ARMOR, ArmorType.LEGGINGS));
	public static final Item DEEP_DIVING_BOOTS = register("deep_diving_boots", Item::new,
		new Item.Properties().humanoidArmor(ModMaterials.DEEP_DIVING_ARMOR, ArmorType.BOOTS));

	// Fase 3B: Moréia Tempestuosa
	/** Escama Elétrica: cai da Moréia; cria a Bobina Voltaica e o Chicote de Moréia. */
	public static final Item ELECTRIC_SCALE = register("electric_scale", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	/** Bobina Voltaica: bateria consumível (6 cargas do Propulsor; vira munição de Choque). */
	public static final Item VOLTAIC_COIL = register("voltaic_coil", Item::new, new Item.Properties());
	/** Chicote de Moréia: 7 de dano, rápido, alcance 3,5 e corrente nos alvos molhados (Forja Abissal). */
	public static final Item MORAY_WHIP = register("moray_whip", MorayWhipItem::new, MorayWhipItem.properties());

	// Fase 3B: Baleias
	/** Sela de Baleia: sem ela não se monta a Baleia-de-Nereia domada. */
	public static final Item WHALE_SADDLE = register("whale_saddle", Item::new, new Item.Properties().stacksTo(1));

	// Fase 3C: Arpão e munições
	/** Arpão Abissal: arremesso do tridente, dano triplo no escudo do Kraken, prende o tentáculo 3 s. */
	public static final Item ABYSSAL_HARPOON = register("abyssal_harpoon", TridentItem::new, new Item.Properties()
		.rarity(Rarity.RARE)
		.durability(400)
		.attributes(TridentItem.createAttributes())
		.component(DataComponents.TOOL, TridentItem.createToolProperties())
		.enchantable(1)
		.component(DataComponents.WEAPON, new Weapon(1)));
	/** Aljava de Arpões: guarda até 4 tipos de ponta; R troca a escolhida. */
	public static final Item HARPOON_QUIVER = register("harpoon_quiver", HarpoonQuiverItem::new, new Item.Properties());
	/** Ponta Abissal: base das pontas especiais. */
	public static final Item ABYSSAL_HARPOON_TIP = register("abyssal_harpoon_tip", Item::new, new Item.Properties());
	public static final Item POISON_HARPOON_TIP = register("poison_harpoon_tip", Item::new, new Item.Properties());
	public static final Item FIRE_HARPOON_TIP = register("fire_harpoon_tip", Item::new, new Item.Properties());
	public static final Item WITHER_HARPOON_TIP = register("wither_harpoon_tip", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item SHOCK_HARPOON_TIP = register("shock_harpoon_tip", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));

	// Fase 3D
	/** Propulsor de Maré: mão secundária, tecla V; 1 Bobina = 6 cargas. */
	public static final Item TIDE_THRUSTER = register("tide_thruster", TideThrusterItem::new, new Item.Properties());

	// Fase 3E: blocos de trabalho da Vila
	public static final Item TIDE_ANVIL = registerBlock(ModBlocks.TIDE_ANVIL, new Item.Properties());
	public static final Item CHART_TABLE = registerBlock(ModBlocks.CHART_TABLE, new Item.Properties());
	public static final Item SHELL_ALTAR = registerBlock(ModBlocks.SHELL_ALTAR, new Item.Properties());

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

	// Fase 4F: Neutrônio (docs/10-fase4.md, §8). Não queima, não explode e afunda na água (NeutroniumTempering).
	public static final Item NEUTRONIUM_ORE = registerBlock(ModBlocks.NEUTRONIUM_ORE, neutronium());
	public static final Item RAW_NEUTRONIUM = register("raw_neutronium", Item::new, neutronium());
	public static final Item NEUTRONIUM_DUST = register("neutronium_dust", Item::new, neutronium());
	public static final Item UNSTABLE_NEUTRONIUM_INGOT = register("unstable_neutronium_ingot", Item::new, neutronium().stacksTo(16));
	public static final Item NEUTRONIUM_INGOT = register("neutronium_ingot", Item::new, neutronium());
	public static final Item NEUTRONIUM_BLOCK = registerBlock(ModBlocks.NEUTRONIUM_BLOCK, neutronium());
	public static final Item NEUTRONIUM_UPGRADE_SMITHING_TEMPLATE = register("neutronium_upgrade_smithing_template",
		NeutroniumEffects::template, neutronium());
	public static final Item NEUTRONIUM_SWORD = register("neutronium_sword", Item::new,
		neutronium().sword(ModMaterials.NEUTRONIUM_TOOL, 3.0F, -2.4F));
	public static final Item NEUTRONIUM_PICKAXE = register("neutronium_pickaxe", Item::new,
		neutronium().pickaxe(ModMaterials.NEUTRONIUM_TOOL, 1.0F, -2.8F));
	public static final Item NEUTRONIUM_AXE = register("neutronium_axe", Item::new,
		neutronium().axe(ModMaterials.NEUTRONIUM_TOOL, 5.0F, -3.0F));
	public static final Item NEUTRONIUM_SHOVEL = register("neutronium_shovel", Item::new,
		neutronium().shovel(ModMaterials.NEUTRONIUM_TOOL, 1.5F, -3.0F));
	public static final Item NEUTRONIUM_HOE = register("neutronium_hoe", Item::new,
		neutronium().hoe(ModMaterials.NEUTRONIUM_TOOL, -5.0F, 0.0F));
	public static final Item NEUTRONIUM_HELMET = register("neutronium_helmet", Item::new,
		neutronium().humanoidArmor(ModMaterials.NEUTRONIUM_ARMOR, ArmorType.HELMET));
	public static final Item NEUTRONIUM_CHESTPLATE = register("neutronium_chestplate", Item::new,
		neutronium().humanoidArmor(ModMaterials.NEUTRONIUM_ARMOR, ArmorType.CHESTPLATE));
	public static final Item NEUTRONIUM_LEGGINGS = register("neutronium_leggings", Item::new,
		neutronium().humanoidArmor(ModMaterials.NEUTRONIUM_ARMOR, ArmorType.LEGGINGS));
	public static final Item NEUTRONIUM_BOOTS = register("neutronium_boots", Item::new,
		neutronium().humanoidArmor(ModMaterials.NEUTRONIUM_ARMOR, ArmorType.BOOTS));

	/** Cavitador de Neutrônio (Fase 4G): pulso de cavitação debaixo d'água, lançador de arpões fora dela. */
	public static final Item NEUTRONIUM_CAVITATOR = register("neutronium_cavitator", NeutroniumCavitatorItem::new,
		neutronium().durability(1200));

	/** Propriedades da família do Neutrônio: épica e imune a fogo e explosão. */
	private static Item.Properties neutronium() {
		return new Item.Properties().rarity(Rarity.EPIC)
			.delayedComponent(DataComponents.DAMAGE_RESISTANT,
				registries -> new DamageResistant(registries.getOrThrow(ModTags.DamageTypes.NEUTRONIUM_IMMUNE)));
	}

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

	/** Itens que se chamavam {@code orichalcum_*} até a Fase 4 (o Oricálcio virou Abissal). */
	private static final String[] RENAMED = {"ore", "ingot", "block", "sword", "pickaxe", "axe", "shovel", "hoe", "helmet",
		"chestplate", "leggings", "boots", "harpoon", "harpoon_tip"};

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro. O ID antigo vira apelido do novo: os mundos
		// salvos antes da Fase 4 abrem com os itens já Abissais.
		for (String name : RENAMED) {
			BuiltInRegistries.ITEM.addAlias(Aquamarine.id("orichalcum_" + name), Aquamarine.id("abyssal_" + name));
		}
	}

	private ModItems() {}
}
