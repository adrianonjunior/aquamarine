package com.aquamarine.client.entity.kraken;

import net.minecraft.client.model.geom.ModelPart;

/**
 * Movimento contínuo dos Krakens da Fase 6 por cima dos clipes (nada aqui é keyframe), no molde do da v2: o manto
 * respira, a cabeça oscila e uma onda corre da raiz à ponta de cada braço. Tudo fica mais forte a cada fase e quando o
 * Kraken nada depressa. Os modelos gerados ({@code KrakenV3aModel} e irmãos) só passam as partes.
 */
public final class KrakenV3Motion {
	/** Partes que o movimento usa: cabeça, o que respira e as juntas de cada braço (raiz primeiro). */
	public record Parts(ModelPart head, ModelPart[] breathe, ModelPart[][] arms) {}

	/** Desce do {@code root} pelos nomes até a parte. */
	public static ModelPart part(ModelPart root, String... path) {
		ModelPart p = root;
		for (String name : path) {
			p = p.getChild(name);
		}
		return p;
	}

	public static void alive(KrakenRenderState state, Parts parts) {
		float t = state.ageInTicks;
		float agitation = 1.0F + 0.4F * (Math.max(1, state.phase) - 1);
		float swim = Math.min(1.0F, state.speed / 0.08F);
		float breath = (float) Math.sin(t * 0.05F * agitation);
		for (ModelPart m : parts.breathe()) {
			m.xScale *= 1.0F + 0.05F * agitation * breath;
			m.zScale *= 1.0F + 0.05F * agitation * breath;
			m.yScale *= 1.0F - 0.035F * agitation * breath;
		}
		parts.head().xRot += 0.04F * (float) Math.sin(t * 0.05F + 1.0F) - 0.06F * swim;
		parts.head().yRot += 0.05F * (float) Math.sin(t * 0.023F * agitation);
		float freq = 0.07F * agitation + 0.12F * swim;
		float amp = (0.05F + 0.04F * swim) * agitation;
		float stroke = swim * 0.35F * (float) Math.sin(t * 0.22F);
		ModelPart[][] arms = parts.arms();
		for (int i = 0; i < arms.length; i++) {
			for (int joint = 0; joint < arms[i].length; joint++) {
				ModelPart segment = arms[i][joint];
				float reach = 0.45F + 0.3F * Math.min(joint, 4) + 0.15F * Math.max(0, joint - 4);
				segment.xRot += amp * reach * (float) Math.sin(t * freq - joint * 0.9F + i * 0.785F);
				segment.zRot += amp * 0.6F * reach * (float) Math.cos(t * freq * 0.7F - joint * 0.8F + i * 1.3F);
				if (joint == 0) {
					segment.xRot += stroke;
				}
			}
		}
	}

	private KrakenV3Motion() {}
}
