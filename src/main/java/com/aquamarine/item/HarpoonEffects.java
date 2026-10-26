package com.aquamarine.item;

import com.aquamarine.Aquamarine;
import com.aquamarine.ModTags;
import com.aquamarine.effect.ModEffects;
import com.aquamarine.entity.ElectricDamage;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.TentacleEntity;
import com.aquamarine.entity.boss.ShieldDamage;

import java.util.Map;
import java.util.WeakHashMap;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Arpão Abissal no servidor (docs/08-fase3.md, seção 5; docs/02-kraken.md): o arremesso é o do
 * tridente do jogo com o nosso item; aqui a ponta escolhida na Aljava é gasta ao arremessar e aplicada
 * no acerto, junto com Fisgada, Perfurante e a trava de 3 s no tentáculo. O acerto chega pelo
 * {@code ThrownTridentMixin}.
 */
public final class HarpoonEffects {
	/** Dano base do arremesso (o do tridente). */
	public static final float BASE_DAMAGE = 8.0F;
	/** O Arpão prende o tentáculo por 3 s. */
	public static final int TENTACLE_PIN_TICKS = 3 * 20;
	/** Fisgada: força do puxão por nível e retorno 40% mais rápido por nível. */
	public static final double HOOK_PULL_PER_LEVEL = 0.6;
	public static final double HOOK_RETURN_PER_LEVEL = 0.4;
	/** Perfurante: alcance atrás do primeiro alvo para achar os seguintes. */
	public static final double PIERCE_RANGE = 8.0;

	public static final ResourceKey<Enchantment> HOOK = key("hook");
	public static final ResourceKey<Enchantment> PIERCING = key("piercing_harpoon");

	/** Ponta carregada em cada arpão em voo (só no servidor; um arpão salvo no chão perde a ponta). */
	private static final Map<ThrownTrident, HarpoonTip> TIPS = new WeakHashMap<>();

	/** Tecla da Aljava (R): troca a ponta escolhida. Sem dados; o servidor acha a Aljava. */
	public record CyclePayload() implements CustomPacketPayload {
		public static final Type<CyclePayload> TYPE = new Type<>(Aquamarine.id("quiver_cycle"));
		public static final StreamCodec<RegistryFriendlyByteBuf, CyclePayload> CODEC = StreamCodec.unit(new CyclePayload());

		@Override
		public Type<CyclePayload> type() {
			return TYPE;
		}
	}

	public static void register() {
		PayloadTypeRegistry.serverboundPlay().register(CyclePayload.TYPE, CyclePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(CyclePayload.TYPE, (payload, context) -> HarpoonQuiverItem.cycle(context.player()));
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (entity instanceof ThrownTrident trident && trident.tickCount == 0 && isHarpoon(trident.getWeaponItem())
				&& trident.getOwner() instanceof Player player) {
				loadTip(trident, player);
			}
		});
	}

	private static ResourceKey<Enchantment> key(String name) {
		return ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id(name));
	}

	public static boolean isHarpoon(ItemStack stack) {
		return stack != null && stack.is(ModTags.Items.HARPOONS);
	}

	public static HarpoonTip tip(ThrownTrident trident) {
		return TIPS.get(trident);
	}

	/** Gasta 1 ponta da escolhida na Aljava do jogador (criativo não gasta) e a põe no arpão. */
	public static HarpoonTip loadTip(ThrownTrident trident, Player player) {
		ItemStack quiver = HarpoonQuiverItem.find(player);
		if (quiver.isEmpty()) {
			return null;
		}
		QuiverContents contents = HarpoonQuiverItem.contents(quiver);
		HarpoonTip tip = contents.selectedTip();
		if (contents.count(tip) <= 0) {
			return null;
		}
		if (!player.getAbilities().instabuild) {
			quiver.set(ModComponents.QUIVER_CONTENTS, contents.with(tip, contents.count(tip) - 1));
		}
		TIPS.put(trident, tip);
		return tip;
	}

	/** Define a ponta direto (testes). */
	public static void setTip(ThrownTrident trident, HarpoonTip tip) {
		TIPS.put(trident, tip);
	}

	private static int level(ServerLevel level, ResourceKey<Enchantment> key, ItemStack stack) {
		return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(key)
			.map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, stack)).orElse(0);
	}

	/** Depois do dano normal do arremesso: ponta, trava do tentáculo, Fisgada e Perfurante. */
	public static void onHit(ThrownTrident trident, Entity target) {
		if (!(trident.level() instanceof ServerLevel level) || !isHarpoon(trident.getWeaponItem())) {
			return;
		}
		Entity owner = trident.getOwner();
		ItemStack weapon = trident.getWeaponItem();
		HarpoonTip tip = TIPS.remove(trident);
		if (target instanceof TentacleEntity tentacle) {
			tentacle.pin(TENTACLE_PIN_TICKS);
		}
		if (tip != null) {
			applyTip(level, tip, target, owner == null ? trident : owner);
		}
		int hook = level(level, HOOK, weapon);
		if (hook > 0 && owner != null && target instanceof LivingEntity living && !(target instanceof KrakenEntity)) {
			Vec3 pull = owner.position().subtract(living.position()).normalize().scale(HOOK_PULL_PER_LEVEL * hook);
			living.push(pull.x, pull.y + 0.15, pull.z);
			living.needsSync = true;
		}
		int pierce = level(level, PIERCING, weapon);
		if (pierce > 0) {
			pierce(level, trident, target, pierce);
		}
	}

	/** Efeito da ponta no alvo. No Kraken: veneno e wither não pegam; fogo e choque somam no escudo. */
	public static void applyTip(ServerLevel level, HarpoonTip tip, Entity target, Entity attacker) {
		if (target instanceof KrakenEntity kraken) {
			if (tip.affectsKraken && kraken.shield() > 0) {
				kraken.hitShield(level, ShieldDamage.Source.HARPOON, BASE_DAMAGE * tip.krakenBonus, attacker, false, 0);
			}
			return;
		}
		if (!(target instanceof LivingEntity living)) {
			return;
		}
		switch (tip) {
			case POISON -> living.addEffect(new MobEffectInstance(MobEffects.POISON, HarpoonTip.POISON_TICKS, 0), attacker);
			case FIRE -> living.addEffect(new MobEffectInstance(ModEffects.ABYSSAL_FLAME, HarpoonTip.FIRE_TICKS, 0), attacker);
			case WITHER -> living.addEffect(new MobEffectInstance(MobEffects.WITHER, HarpoonTip.WITHER_TICKS, 0), attacker);
			case SHOCK -> {
				Vec3 at = living.position().add(0, living.getBbHeight() * 0.5, 0);
				ElectricDamage.chain(level, attacker, at, HarpoonTip.SHOCK_RADIUS, BASE_DAMAGE * HarpoonTip.SHOCK_FRACTION, living);
				level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y, at.z, 20, 0.5, 0.5, 0.5, 0.2);
			}
		}
	}

	/** Perfurante: os próximos alvos na linha do arremesso, atrás do primeiro, levam o mesmo dano. */
	private static void pierce(ServerLevel level, ThrownTrident trident, Entity first, int extra) {
		Vec3 dir = trident.getDeltaMovement().lengthSqr() > 1.0E-4 ? trident.getDeltaMovement().normalize()
			: first.position().subtract(trident.position()).normalize();
		Vec3 from = first.position();
		Entity owner = trident.getOwner();
		level.getEntitiesOfClass(LivingEntity.class, new AABB(from, from).inflate(PIERCE_RANGE),
				e -> e != first && e != owner && e.isAlive() && e.position().subtract(from).normalize().dot(dir) > 0.6)
			.stream()
			.sorted(java.util.Comparator.comparingDouble(e -> e.distanceToSqr(from)))
			.limit(extra)
			.forEach(e -> e.hurtServer(level, level.damageSources().trident(trident, owner == null ? trident : owner), BASE_DAMAGE));
	}

	/** Fisgada: o arpão com Lealdade volta 40% mais rápido por nível. */
	public static void tick(ThrownTrident trident) {
		if (!(trident.level() instanceof ServerLevel level) || !trident.isNoPhysics() || !isHarpoon(trident.getWeaponItem())) {
			return;
		}
		int hook = level(level, HOOK, trident.getWeaponItem());
		Entity owner = trident.getOwner();
		if (hook > 0 && owner != null) {
			Vec3 to = owner.getEyePosition().subtract(trident.position()).normalize();
			trident.setDeltaMovement(trident.getDeltaMovement().add(to.scale(0.05 * HOOK_RETURN_PER_LEVEL * hook)));
		}
	}

	private HarpoonEffects() {}
}
