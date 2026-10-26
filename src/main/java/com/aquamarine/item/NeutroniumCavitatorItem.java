package com.aquamarine.item;

import com.aquamarine.entity.CavitationPulseEntity;
import com.aquamarine.particle.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Cavitador de Neutrônio (docs/10-fase4.md, §9). Segurar carrega e soltar dispara:
 * <ul>
 *   <li>debaixo d'água, um pulso de cavitação ({@link CavitationPulseEntity}): carga cheia em {@link #FULL_CHARGE}
 *       tiques dá 10 de dano, carga curta dá 5;</li>
 *   <li>fora d'água, um arpão pesado com a ponta escolhida na Aljava (o pulso não existe sem água).</li>
 * </ul>
 */
public class NeutroniumCavitatorItem extends Item {
	public static final int FULL_CHARGE = 12;
	public static final int MIN_CHARGE = 4;
	public static final int HARPOON_CHARGE = 6;
	public static final float HARPOON_SPEED = 3.2F;

	public NeutroniumCavitatorItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BOW;
	}

	/** Pontos de luz convergem para a boca do cano enquanto carrega; um estalo avisa a carga cheia. */
	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
		int charge = getUseDuration(stack, entity) - remaining;
		Vec3 muzzle = muzzle(entity);
		if (level.isClientSide()) {
			int motes = charge >= FULL_CHARGE ? 1 : 2;
			for (int i = 0; i < motes; i++) {
				Vec3 from = muzzle.add((entity.getRandom().nextDouble() - 0.5) * 1.6, (entity.getRandom().nextDouble() - 0.5) * 1.2,
					(entity.getRandom().nextDouble() - 0.5) * 1.6);
				Vec3 v = muzzle.subtract(from).scale(1.0 / 8.0);
				level.addParticle(ModParticles.CHARGE_MOTE, from.x, from.y, from.z, v.x, v.y, v.z);
			}
		} else if (charge == FULL_CHARGE) {
			level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 0.8F, 1.6F);
		}
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) {
		if (!(entity instanceof Player player)) {
			return false;
		}
		int charge = getUseDuration(stack, entity) - remaining;
		boolean underwater = player.isUnderWater();
		if (charge < (underwater ? MIN_CHARGE : HARPOON_CHARGE)) {
			return false;
		}
		if (!(level instanceof ServerLevel server)) {
			return true;
		}
		EquipmentSlot slot = player.getUsedItemHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
		if (underwater) {
			CavitationPulseEntity.shoot(server, player, charge >= FULL_CHARGE, stack);
			player.getCooldowns().addCooldown(stack, 10);
			stack.hurtAndBreak(1, player, slot);
		} else {
			ThrownTrident harpoon = new ThrownTrident(server, player, new ItemStack(ModItems.ABYSSAL_HARPOON));
			harpoon.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, HARPOON_SPEED, 0.4F);
			harpoon.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			HarpoonEffects.loadTip(harpoon, player);
			((HarpoonStyleHolder) harpoon).aquamarine$setHarpoonStyle(HarpoonStyleHolder.CAVITATOR);
			server.addFreshEntity(harpoon);
			Vec3 muzzle = muzzle(player);
			server.sendParticles(ModParticles.NEUTRON_SPARK, muzzle.x, muzzle.y, muzzle.z, 6, 0.1, 0.1, 0.1, 0.06);
			server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 0.7F);
			player.getCooldowns().addCooldown(stack, 20);
			stack.hurtAndBreak(2, player, slot);
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		return true;
	}

	/** Boca do cano: um pouco à frente do olho e à direita, onde o modelo segura a arma. */
	private static Vec3 muzzle(LivingEntity entity) {
		Vec3 look = entity.getLookAngle();
		double yaw = Math.toRadians(entity.getYRot());
		Vec3 right = new Vec3(-Math.cos(yaw), 0, -Math.sin(yaw));
		return entity.getEyePosition().add(look.scale(1.1)).add(right.scale(0.3)).add(0, -0.25, 0);
	}
}
