package com.aquamarine.item;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlocksAttacks;

/** Escudo de Concha: bloqueio vanilla, 672 usos e 60% da recarga de desativação. */
public final class ShellShield {
    public static final int DURABILITY = 672;
    public static final float DISABLE_SCALE = .6F;
    public static Item.Properties properties() {
        return new Item.Properties().durability(DURABILITY).repairable(ModItems.CRAB_SHELL)
            .equippableUnswappable(EquipmentSlot.OFFHAND)
            .delayedComponent(DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(.25F, DISABLE_SCALE,
                List.of(new BlocksAttacks.DamageReduction(90, Optional.empty(), 0, 1)),
                new BlocksAttacks.ItemDamageFunction(3, 1, 1), Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                Optional.of(SoundEvents.SHIELD_BLOCK), Optional.of(SoundEvents.SHIELD_BREAK)))
            .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK);
    }
    /** O bônus exige estar bloqueando; carregar o escudo no inventário não protege. */
    public static double whalePushScale(LivingEntity entity) {
        return entity.isBlocking() && entity.getUseItem().is(ModItems.SHELL_SHIELD) ? .5 : 1;
    }
    private ShellShield() {}
}
