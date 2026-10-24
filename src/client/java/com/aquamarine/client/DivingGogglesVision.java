package com.aquamarine.client;

import com.aquamarine.item.ModItems;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EquipmentSlot;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Efeito dos Óculos de Mergulho (docs/05-itens.md): só visual e só no cliente. Sem visão
 * noturna. A Nuvem de Tinta do Kraken, quando existir, deve ter névoa própria que este efeito
 * não toca (só o conjunto de Oricálcio enxerga através dela).
 */
public final class DivingGogglesVision {
	/**
	 * Quanto o fim da névoa se afasta em relação à água totalmente "acostumada" do vanilla.
	 * Com 2,0 a água parecia ar (screenshot do teste de cliente); 1,5 mantém o clima.
	 */
	private static final float FOG_END_MULTIPLIER = 1.5F;
	/** Quanto a cor da névoa se aproxima de um verde-água claro (0 = nada, 1 = tudo). */
	private static final float COLOR_CLEARING = 0.18F;
	private static final Vector3fc CLEAR_WATER = new Vector3f(0.55F, 0.85F, 0.88F);

	public static boolean isActive(Camera camera) {
		return camera.entity() instanceof LocalPlayer player
			&& player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.DIVING_GOGGLES);
	}

	/**
	 * O vanilla começa a água com 25% do alcance e leva cerca de 30 s para "acostumar" a vista.
	 * Com os óculos a vista já começa acostumada e enxerga 50% mais longe.
	 */
	public static void applyFog(FogData fog, Camera camera, DeltaTracker deltaTracker) {
		if (!isActive(camera)) {
			return;
		}
		float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
		float end = camera.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_END_DISTANCE, partialTicks) * FOG_END_MULTIPLIER;
		fog.environmentalEnd = end;
		fog.skyEnd = end;
		fog.cloudEnd = end;
	}

	public static Vector3fc clearerColor(Vector3fc color) {
		return new Vector3f(color).lerp(CLEAR_WATER, COLOR_CLEARING);
	}

	private DivingGogglesVision() {}
}
