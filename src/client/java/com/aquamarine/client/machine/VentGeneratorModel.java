package com.aquamarine.client.machine;

import com.aquamarine.Aquamarine;
import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

/**
 * Gerador de Fenda: caixa de pedra rachada com magma nas fendas, câmara de vidro com magma borbulhante, pistões em contratempo num balancim, volante da válvula, mostrador e chaminé; 32 E/t com magma embaixo e água em cima.
 * GERADO por tools/models/machines/vent_generator/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class VentGeneratorModel {
	public static final Identifier TEXTURE = Aquamarine.id("textures/block/machine/vent_generator.png");
	public static final Identifier GLOW = Aquamarine.id("textures/block/machine/vent_generator_glow.png");
	/** Partes de vidro: vão só na passada translúcida. */
	public static final Set<String> GLASS = Set.of("chamber_glass");

	public static ModelPart create() {
		MachineParts p = new MachineParts(128, 128);
		p.part("frame", null, 0F, 24F, 0F, 0F, 0F, 0F);
		p.part("works", "frame", 0F, -6F, 0F, 0F, 0F, 0F);
		p.part("chamber", "works", 0F, 0F, -1F, 0F, 0F, 0F);
		p.part("chamber_glass", "chamber", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("magma", "chamber", 0F, -1F, 0F, 0F, 0F, 0F);
		p.part("blob_1", "magma", -1.5F, -2F, -1.5F, 0F, 0F, 0F);
		p.part("blob_2", "magma", 1F, -2F, 1F, 0F, 0F, 0F);
		p.part("blob_3", "magma", -0.5F, -2F, 1.5F, 0F, 0F, 0F);
		p.part("piston_l", "works", -5.75F, -4.5F, -1.25F, 0F, 0F, 0F);
		p.part("piston_r", "works", 5.75F, -4.5F, -1.25F, 0F, 0F, 0F);
		p.part("beam", "works", 0F, -8F, -1.25F, 0F, 0F, 0F);
		p.part("chimney", "works", -5.75F, 0F, 4.75F, 0F, 0F, 0F);
		p.part("chimney_cap", "chimney", 0F, -9F, 1.75F, 0F, 0F, 0F);
		p.part("valve", "works", 6F, -1.75F, -6F, 0F, 0F, 0F);
		p.part("gauge", "works", -5.25F, 0F, -6.75F, 0F, 0F, 0F);
		p.part("needle", "gauge", 0F, -3.5F, -0.5F, 0F, 0F, -1.9199F);
		p.box("frame", -7.5F, -1F, -7.5F, 15F, 1F, 15F, new int[] {0, 31, 0, 30, 30, 2, 93, 58, 30, 2, 3, 93, 58, 30, 2, 4, 62, 58, 30, 2, 5, 62, 58, 30, 2}); // plinth
		p.box("frame", -7F, -5F, -7F, 14F, 4F, 14F, new int[] {2, 75, 31, 28, 8, 3, 0, 42, 28, 8, 4, 46, 31, 28, 8, 5, 17, 31, 28, 8}); // stone
		p.box("frame", -7.5F, -6F, -7.5F, 15F, 1F, 15F, new int[] {0, 0, 0, 30, 30, 1, 0, 0, 30, 30, 2, 31, 58, 30, 2, 3, 31, 58, 30, 2, 4, 0, 58, 30, 2, 5, 0, 58, 30, 2}); // deck
		p.box("frame", -4F, -4F, -7.5F, 8F, 2.5F, 0.5F, new int[] {0, 17, 64, 16, 1, 1, 0, 64, 16, 1, 2, 51, 51, 1, 5, 3, 53, 51, 1, 5, 4, 34, 51, 16, 5}); // grate
		p.box("frame", -7.25F, -5F, -7.25F, 1.5F, 4F, 1.5F, new int[] {2, 53, 42, 3, 8, 3, 53, 42, 3, 8, 4, 53, 42, 3, 8, 5, 53, 42, 3, 8}); // guard_0
		p.box("frame", 5.75F, -5F, -7.25F, 1.5F, 4F, 1.5F, new int[] {2, 53, 42, 3, 8, 3, 53, 42, 3, 8, 4, 53, 42, 3, 8, 5, 53, 42, 3, 8}); // guard_1
		p.box("frame", -7.25F, -5F, 5.75F, 1.5F, 4F, 1.5F, new int[] {2, 53, 42, 3, 8, 3, 53, 42, 3, 8, 4, 53, 42, 3, 8, 5, 53, 42, 3, 8}); // guard_2
		p.box("frame", 5.75F, -5F, 5.75F, 1.5F, 4F, 1.5F, new int[] {2, 53, 42, 3, 8, 3, 53, 42, 3, 8, 4, 53, 42, 3, 8, 5, 53, 42, 3, 8}); // guard_3
		p.box("frame", -1.5F, -4.5F, 7F, 3F, 3F, 0.5F, new int[] {0, 118, 64, 6, 1, 1, 111, 64, 6, 1, 2, 14, 51, 1, 6, 3, 16, 51, 1, 6, 5, 116, 42, 6, 6}); // ring_energy
		p.box("frame", -7.5F, -4.5F, -1.5F, 0.5F, 3F, 3F, new int[] {0, 32, 51, 1, 6, 1, 28, 51, 1, 6, 2, 7, 51, 6, 6, 4, 30, 51, 1, 6, 5, 26, 51, 1, 6}); // ring_water
		p.box("frame", 7F, -4.5F, -1.5F, 0.5F, 3F, 3F, new int[] {0, 24, 51, 1, 6, 1, 20, 51, 1, 6, 3, 0, 51, 6, 6, 4, 22, 51, 1, 6, 5, 18, 51, 1, 6}); // ring_magma
		p.box("chamber", -4F, -1F, -4F, 8F, 1F, 8F, new int[] {0, 81, 0, 16, 16, 2, 46, 61, 16, 2, 3, 46, 61, 16, 2, 4, 46, 61, 16, 2, 5, 46, 61, 16, 2}); // collar
		p.box("chamber_glass", -3.5F, -5F, -3.5F, 7F, 4F, 7F, new int[] {2, 29, 42, 14, 8, 3, 29, 42, 14, 8, 4, 29, 42, 14, 8, 5, 29, 42, 14, 8}); // glass
		p.box("chamber", -3.75F, -5.5F, -3.75F, 7.5F, 0.5F, 7.5F, new int[] {2, 34, 64, 15, 1, 3, 34, 64, 15, 1, 4, 34, 64, 15, 1, 5, 34, 64, 15, 1}); // gasket
		p.box("chamber", -4.5F, -6.5F, -4.5F, 9F, 1F, 9F, new int[] {0, 62, 0, 18, 18, 1, 62, 0, 18, 18, 2, 27, 61, 18, 2, 3, 27, 61, 18, 2, 4, 27, 61, 18, 2, 5, 27, 61, 18, 2}); // lid
		p.box("chamber", -1.5F, -7F, -3.5F, 0.5F, 0.5F, 0.5F, new int[] {2, 11, 66, 1, 1, 3, 13, 66, 1, 1, 4, 9, 66, 1, 1, 5, 7, 66, 1, 1}); // handle_l
		p.box("chamber", 1F, -7F, -3.5F, 0.5F, 0.5F, 0.5F, new int[] {2, 11, 66, 1, 1, 3, 13, 66, 1, 1, 4, 9, 66, 1, 1, 5, 7, 66, 1, 1}); // handle_r
		p.box("chamber", -1.5F, -7.5F, -3.5F, 3F, 0.5F, 0.5F, new int[] {0, 104, 64, 6, 1, 1, 90, 64, 6, 1, 2, 3, 66, 1, 1, 3, 5, 66, 1, 1, 4, 97, 64, 6, 1, 5, 97, 64, 6, 1}); // handle_bar
		p.box("chamber", -4F, -5F, -4F, 1F, 4F, 1F, new int[] {2, 57, 42, 2, 8, 3, 57, 42, 2, 8, 4, 57, 42, 2, 8, 5, 57, 42, 2, 8}); // post_0
		p.box("chamber", 3F, -5F, -4F, 1F, 4F, 1F, new int[] {2, 57, 42, 2, 8, 3, 57, 42, 2, 8, 4, 57, 42, 2, 8, 5, 57, 42, 2, 8}); // post_1
		p.box("chamber", -4F, -5F, 3F, 1F, 4F, 1F, new int[] {2, 57, 42, 2, 8, 3, 57, 42, 2, 8, 4, 57, 42, 2, 8, 5, 57, 42, 2, 8}); // post_2
		p.box("chamber", 3F, -5F, 3F, 1F, 4F, 1F, new int[] {2, 57, 42, 2, 8, 3, 57, 42, 2, 8, 4, 57, 42, 2, 8, 5, 57, 42, 2, 8}); // post_3
		p.box("magma", -3F, -2F, -3F, 6F, 2F, 6F, new int[] {0, 104, 0, 12, 12, 2, 55, 51, 12, 4, 3, 55, 51, 12, 4, 4, 55, 51, 12, 4, 5, 55, 51, 12, 4}); // magma
		p.box("blob_1", -0.5F, -1F, -0.5F, 1F, 1F, 1F, new int[] {0, 114, 61, 2, 2, 2, 111, 61, 2, 2, 3, 111, 61, 2, 2, 4, 111, 61, 2, 2, 5, 111, 61, 2, 2}); // blob_1
		p.box("blob_2", -0.5F, -1F, -0.5F, 1F, 1F, 1F, new int[] {0, 114, 61, 2, 2, 2, 111, 61, 2, 2, 3, 111, 61, 2, 2, 4, 111, 61, 2, 2, 5, 111, 61, 2, 2}); // blob_2
		p.box("blob_3", -0.5F, -1F, -0.5F, 1F, 1F, 1F, new int[] {0, 114, 61, 2, 2, 2, 111, 61, 2, 2, 3, 111, 61, 2, 2, 4, 111, 61, 2, 2, 5, 111, 61, 2, 2}); // blob_3
		p.box("works", -7.5F, -0.5F, -3F, 3.5F, 0.5F, 3.5F, new int[] {0, 60, 42, 7, 7, 2, 59, 64, 7, 1, 3, 59, 64, 7, 1, 4, 59, 64, 7, 1, 5, 59, 64, 7, 1}); // band_low_l
		p.box("works", -7.25F, -4F, -2.75F, 3F, 3.5F, 3F, new int[] {2, 92, 42, 6, 7, 3, 92, 42, 6, 7, 4, 92, 42, 6, 7, 5, 92, 42, 6, 7}); // housing_l
		p.box("works", -7.5F, -4.5F, -3F, 3.5F, 0.5F, 3.5F, new int[] {0, 68, 42, 7, 7, 1, 68, 42, 7, 7, 2, 67, 64, 7, 1, 3, 67, 64, 7, 1, 4, 67, 64, 7, 1, 5, 67, 64, 7, 1}); // band_top_l
		p.box("piston_l", -0.5F, -2F, -0.5F, 1F, 3.5F, 1F, new int[] {2, 99, 42, 2, 7, 3, 99, 42, 2, 7, 4, 99, 42, 2, 7, 5, 99, 42, 2, 7}); // rod_l
		p.box("piston_l", -1F, -3F, -1F, 2F, 1F, 2F, new int[] {0, 68, 51, 4, 4, 1, 68, 51, 4, 4, 2, 95, 61, 4, 2, 3, 95, 61, 4, 2, 4, 95, 61, 4, 2, 5, 95, 61, 4, 2}); // head_l
		p.box("works", 4F, -0.5F, -3F, 3.5F, 0.5F, 3.5F, new int[] {0, 60, 42, 7, 7, 2, 59, 64, 7, 1, 3, 59, 64, 7, 1, 4, 59, 64, 7, 1, 5, 59, 64, 7, 1}); // band_low_r
		p.box("works", 4.25F, -4F, -2.75F, 3F, 3.5F, 3F, new int[] {2, 92, 42, 6, 7, 3, 92, 42, 6, 7, 4, 92, 42, 6, 7, 5, 92, 42, 6, 7}); // housing_r
		p.box("works", 4F, -4.5F, -3F, 3.5F, 0.5F, 3.5F, new int[] {0, 68, 42, 7, 7, 1, 68, 42, 7, 7, 2, 67, 64, 7, 1, 3, 67, 64, 7, 1, 4, 67, 64, 7, 1, 5, 67, 64, 7, 1}); // band_top_r
		p.box("piston_r", -0.5F, -2F, -0.5F, 1F, 3.5F, 1F, new int[] {2, 99, 42, 2, 7, 3, 99, 42, 2, 7, 4, 99, 42, 2, 7, 5, 99, 42, 2, 7}); // rod_r
		p.box("piston_r", -1F, -3F, -1F, 2F, 1F, 2F, new int[] {0, 68, 51, 4, 4, 1, 68, 51, 4, 4, 2, 95, 61, 4, 2, 3, 95, 61, 4, 2, 4, 95, 61, 4, 2, 5, 95, 61, 4, 2}); // head_r
		p.box("works", -0.75F, -7.5F, -2F, 1.5F, 1F, 1.5F, new int[] {2, 104, 61, 3, 2, 3, 104, 61, 3, 2, 4, 104, 61, 3, 2, 5, 104, 61, 3, 2}); // beam_post
		p.box("beam", -6.5F, -0.5F, -0.75F, 13F, 1F, 1.5F, new int[] {0, 77, 51, 26, 3, 1, 77, 51, 26, 3, 2, 100, 61, 3, 2, 3, 100, 61, 3, 2, 4, 0, 61, 26, 2, 5, 0, 61, 26, 2}); // beam
		p.box("beam", -0.5F, -0.5F, -1.25F, 1F, 1F, 0.5F, new int[] {0, 0, 66, 2, 1, 1, 125, 64, 2, 1, 2, 120, 61, 1, 2, 3, 120, 61, 1, 2, 4, 108, 61, 2, 2}); // beam_pin
		p.box("chimney", -1.25F, -8F, -1.25F, 2.5F, 8F, 2.5F, new int[] {2, 98, 0, 5, 16, 3, 98, 0, 5, 16, 4, 98, 0, 5, 16, 5, 98, 0, 5, 16}); // chimney
		p.box("chimney", -1.5F, -3.5F, -1.5F, 3F, 0.5F, 3F, new int[] {0, 102, 42, 6, 6, 1, 102, 42, 6, 6, 2, 83, 64, 6, 1, 3, 83, 64, 6, 1, 4, 83, 64, 6, 1, 5, 83, 64, 6, 1}); // chimney_band
		p.box("chimney", -1.5F, -9F, -1.5F, 3F, 1F, 3F, new int[] {0, 109, 42, 6, 6, 2, 74, 61, 6, 2, 3, 74, 61, 6, 2, 4, 74, 61, 6, 2, 5, 74, 61, 6, 2}); // chimney_mouth
		p.box("chimney_cap", -1.75F, -0.5F, -3.5F, 3.5F, 0.5F, 3.5F, new int[] {0, 84, 42, 7, 7, 1, 76, 42, 7, 7, 2, 75, 64, 7, 1, 3, 75, 64, 7, 1, 4, 75, 64, 7, 1, 5, 75, 64, 7, 1}); // chimney_cap
		p.box("works", -4.5F, -6.5F, 3.5F, 3F, 1F, 1F, new int[] {0, 88, 61, 6, 2, 1, 88, 61, 6, 2, 5, 81, 61, 6, 2}); // pipe
		p.box("works", -2F, -6.75F, 3.5F, 1F, 1.5F, 1.5F, new int[] {0, 118, 51, 2, 3, 1, 115, 51, 2, 3, 2, 104, 51, 3, 3, 3, 104, 51, 3, 3, 5, 112, 51, 2, 3}); // flange
		p.box("works", 5.25F, -1.5F, -6.75F, 1.5F, 1.5F, 1.5F, new int[] {2, 108, 51, 3, 3, 3, 108, 51, 3, 3, 4, 108, 51, 3, 3, 5, 108, 51, 3, 3}); // valve_stub
		p.box("valve", -2F, -0.25F, -2F, 4F, 0.5F, 4F, new int[] {0, 44, 42, 8, 8, 1, 44, 42, 8, 8, 2, 50, 64, 8, 1, 3, 50, 64, 8, 1, 4, 50, 64, 8, 1, 5, 50, 64, 8, 1}); // wheel
		p.box("gauge", -0.5F, -1F, -0.5F, 1F, 1F, 1F, new int[] {2, 117, 61, 2, 2, 3, 117, 61, 2, 2, 4, 117, 61, 2, 2, 5, 117, 61, 2, 2}); // gauge_post
		p.box("gauge", -2.5F, -6F, -0.5F, 5F, 5F, 1F, new int[] {0, 63, 61, 10, 2, 1, 63, 61, 10, 2, 2, 11, 31, 2, 10, 3, 14, 31, 2, 10, 4, 0, 31, 10, 10, 5, 117, 0, 10, 10}); // dial
		p.box("needle", -0.25F, -2F, -0.5F, 0.5F, 2F, 0.5F, new int[] {0, 15, 66, 1, 1, 2, 75, 51, 1, 4, 3, 75, 51, 1, 4, 4, 73, 51, 1, 4}); // needle
		return p.bake();
	}

	private VentGeneratorModel() {}
}
