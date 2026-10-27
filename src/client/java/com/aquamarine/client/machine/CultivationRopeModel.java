package com.aquamarine.client.machine;

import com.aquamarine.Aquamarine;
import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

/**
 * Corda de Cultivo: Três fios de couro em meia-volta, presilhas modulares de latão e uma ampola central atrasada pela correnteza.
 * GERADO por tools/models/machines/cultivation_rope/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class CultivationRopeModel {
	public static final Identifier TEXTURE = Aquamarine.id("textures/block/machine/cultivation_rope.png");
	public static final Identifier GLOW = Aquamarine.id("textures/block/machine/cultivation_rope_glow.png");
	/** Partes de vidro: vão só na passada translúcida. */
	public static final Set<String> GLASS = Set.of("float_glass");

	public static ModelPart create() {
		MachineParts p = new MachineParts(128, 128);
		p.part("root", null, 0F, 12.5F, 0F, 0F, 0F, 0F);
		p.part("wave_0", "root", -7F, 0F, 0F, 0F, 0F, 0F);
		p.part("wave_1", "root", -5F, 0.25F, 0F, 0F, 0F, 0F);
		p.part("wave_2", "root", -3F, 0.55F, 0F, 0F, 0F, 0F);
		p.part("wave_3", "root", -1F, 0.85F, 0F, 0F, 0F, 0F);
		p.part("wave_4", "root", 1F, 0.85F, 0F, 0F, 0F, 0F);
		p.part("wave_5", "root", 3F, 0.55F, 0F, 0F, 0F, 0F);
		p.part("wave_6", "root", 5F, 0.25F, 0F, 0F, 0F, 0F);
		p.part("wave_7", "root", 7F, 0F, 0F, 0F, 0F, 0F);
		p.part("float", "root", 0F, 1.1F, 0F, 0F, 0F, 0F);
		p.part("float_glass", "float", 0F, 0F, 0F, 0F, 0F, 0F);
		p.box("wave_0", -1F, -0.5F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 109, 0, 4, 1, 1, 109, 0, 4, 1, 2, 29, 8, 1, 1, 3, 29, 8, 1, 1, 4, 104, 0, 4, 1, 5, 104, 0, 4, 1}); // strand_0_0
		p.box("wave_0", -1F, 0F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 119, 0, 4, 1, 1, 119, 0, 4, 1, 2, 31, 8, 1, 1, 3, 31, 8, 1, 1, 4, 114, 0, 4, 1, 5, 114, 0, 4, 1}); // strand_0_1
		p.box("wave_0", -1F, 0F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 0, 8, 4, 1, 1, 0, 8, 4, 1, 2, 33, 8, 1, 1, 3, 33, 8, 1, 1, 4, 124, 0, 4, 1, 5, 124, 0, 4, 1}); // strand_0_2
		p.box("wave_1", -1F, -0.25F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 109, 0, 4, 1, 1, 109, 0, 4, 1, 2, 29, 8, 1, 1, 3, 29, 8, 1, 1, 4, 104, 0, 4, 1, 5, 104, 0, 4, 1}); // strand_1_0
		p.box("wave_1", -1F, 0.25F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 119, 0, 4, 1, 1, 119, 0, 4, 1, 2, 31, 8, 1, 1, 3, 31, 8, 1, 1, 4, 114, 0, 4, 1, 5, 114, 0, 4, 1}); // strand_1_1
		p.box("wave_1", -1F, -0.75F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 0, 8, 4, 1, 1, 0, 8, 4, 1, 2, 33, 8, 1, 1, 3, 33, 8, 1, 1, 4, 124, 0, 4, 1, 5, 124, 0, 4, 1}); // strand_1_2
		p.box("wave_2", -1F, -0.05F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 109, 0, 4, 1, 1, 109, 0, 4, 1, 2, 29, 8, 1, 1, 3, 29, 8, 1, 1, 4, 104, 0, 4, 1, 5, 104, 0, 4, 1}); // strand_2_0
		p.box("wave_2", -1F, -0.55F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 119, 0, 4, 1, 1, 119, 0, 4, 1, 2, 31, 8, 1, 1, 3, 31, 8, 1, 1, 4, 114, 0, 4, 1, 5, 114, 0, 4, 1}); // strand_2_1
		p.box("wave_2", -1F, -0.55F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 0, 8, 4, 1, 1, 0, 8, 4, 1, 2, 33, 8, 1, 1, 3, 33, 8, 1, 1, 4, 124, 0, 4, 1, 5, 124, 0, 4, 1}); // strand_2_2
		p.box("wave_3", -1F, -0.35F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 109, 0, 4, 1, 1, 109, 0, 4, 1, 2, 29, 8, 1, 1, 3, 29, 8, 1, 1, 4, 104, 0, 4, 1, 5, 104, 0, 4, 1}); // strand_3_0
		p.box("wave_3", -1F, -0.35F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 119, 0, 4, 1, 1, 119, 0, 4, 1, 2, 31, 8, 1, 1, 3, 31, 8, 1, 1, 4, 114, 0, 4, 1, 5, 114, 0, 4, 1}); // strand_3_1
		p.box("wave_3", -1F, 0.15F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 0, 8, 4, 1, 1, 0, 8, 4, 1, 2, 33, 8, 1, 1, 3, 33, 8, 1, 1, 4, 124, 0, 4, 1, 5, 124, 0, 4, 1}); // strand_3_2
		p.box("wave_4", -1F, -0.35F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 109, 0, 4, 1, 1, 109, 0, 4, 1, 2, 29, 8, 1, 1, 3, 29, 8, 1, 1, 4, 104, 0, 4, 1, 5, 104, 0, 4, 1}); // strand_4_0
		p.box("wave_4", -1F, 0.15F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 119, 0, 4, 1, 1, 119, 0, 4, 1, 2, 31, 8, 1, 1, 3, 31, 8, 1, 1, 4, 114, 0, 4, 1, 5, 114, 0, 4, 1}); // strand_4_1
		p.box("wave_4", -1F, 0.15F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 0, 8, 4, 1, 1, 0, 8, 4, 1, 2, 33, 8, 1, 1, 3, 33, 8, 1, 1, 4, 124, 0, 4, 1, 5, 124, 0, 4, 1}); // strand_4_2
		p.box("wave_5", -1F, -0.05F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 109, 0, 4, 1, 1, 109, 0, 4, 1, 2, 29, 8, 1, 1, 3, 29, 8, 1, 1, 4, 104, 0, 4, 1, 5, 104, 0, 4, 1}); // strand_5_0
		p.box("wave_5", -1F, -0.05F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 119, 0, 4, 1, 1, 119, 0, 4, 1, 2, 31, 8, 1, 1, 3, 31, 8, 1, 1, 4, 114, 0, 4, 1, 5, 114, 0, 4, 1}); // strand_5_1
		p.box("wave_5", -1F, -0.55F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 0, 8, 4, 1, 1, 0, 8, 4, 1, 2, 33, 8, 1, 1, 3, 33, 8, 1, 1, 4, 124, 0, 4, 1, 5, 124, 0, 4, 1}); // strand_5_2
		p.box("wave_6", -1F, 0.25F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 109, 0, 4, 1, 1, 109, 0, 4, 1, 2, 29, 8, 1, 1, 3, 29, 8, 1, 1, 4, 104, 0, 4, 1, 5, 104, 0, 4, 1}); // strand_6_0
		p.box("wave_6", -1F, -0.25F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 119, 0, 4, 1, 1, 119, 0, 4, 1, 2, 31, 8, 1, 1, 3, 31, 8, 1, 1, 4, 114, 0, 4, 1, 5, 114, 0, 4, 1}); // strand_6_1
		p.box("wave_6", -1F, -0.25F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 0, 8, 4, 1, 1, 0, 8, 4, 1, 2, 33, 8, 1, 1, 3, 33, 8, 1, 1, 4, 124, 0, 4, 1, 5, 124, 0, 4, 1}); // strand_6_2
		p.box("wave_7", -1F, 0F, -0.5F, 2F, 0.5F, 0.5F, new int[] {0, 109, 0, 4, 1, 1, 109, 0, 4, 1, 2, 29, 8, 1, 1, 3, 29, 8, 1, 1, 4, 104, 0, 4, 1, 5, 104, 0, 4, 1}); // strand_7_0
		p.box("wave_7", -1F, -0.5F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 119, 0, 4, 1, 1, 119, 0, 4, 1, 2, 31, 8, 1, 1, 3, 31, 8, 1, 1, 4, 114, 0, 4, 1, 5, 114, 0, 4, 1}); // strand_7_1
		p.box("wave_7", -1F, 0F, 0F, 2F, 0.5F, 0.5F, new int[] {0, 0, 8, 4, 1, 1, 0, 8, 4, 1, 2, 33, 8, 1, 1, 3, 33, 8, 1, 1, 4, 124, 0, 4, 1, 5, 124, 0, 4, 1}); // strand_7_2
		p.box("root", -8F, -1F, -1F, 0.5F, 0.5F, 2F, new int[] {0, 36, 0, 1, 4, 1, 36, 0, 1, 4, 2, 84, 0, 4, 1, 3, 89, 0, 4, 1, 4, 23, 8, 1, 1, 5, 23, 8, 1, 1}); // cuff_l_top
		p.box("root", -8F, 0.5F, -1F, 0.5F, 0.5F, 2F, new int[] {0, 34, 0, 1, 4, 1, 34, 0, 1, 4, 2, 74, 0, 4, 1, 3, 79, 0, 4, 1, 4, 17, 8, 1, 1, 5, 17, 8, 1, 1}); // cuff_l_bottom
		p.box("root", -8F, -0.5F, -1.25F, 0.5F, 1F, 0.5F, new int[] {0, 21, 8, 1, 1, 1, 19, 8, 1, 1, 2, 52, 0, 1, 2, 3, 52, 0, 1, 2, 4, 50, 0, 1, 2, 5, 48, 0, 1, 2}); // cuff_l_front
		p.box("root", -8F, -0.5F, 0.75F, 0.5F, 1F, 0.5F, new int[] {0, 15, 8, 1, 1, 1, 13, 8, 1, 1, 2, 46, 0, 1, 2, 3, 46, 0, 1, 2, 4, 44, 0, 1, 2, 5, 42, 0, 1, 2}); // cuff_l_back
		p.box("root", 7.5F, -1F, -1F, 0.5F, 0.5F, 2F, new int[] {0, 36, 0, 1, 4, 1, 36, 0, 1, 4, 2, 84, 0, 4, 1, 3, 89, 0, 4, 1, 4, 23, 8, 1, 1, 5, 23, 8, 1, 1}); // cuff_r_top
		p.box("root", 7.5F, 0.5F, -1F, 0.5F, 0.5F, 2F, new int[] {0, 34, 0, 1, 4, 1, 34, 0, 1, 4, 2, 74, 0, 4, 1, 3, 79, 0, 4, 1, 4, 17, 8, 1, 1, 5, 17, 8, 1, 1}); // cuff_r_bottom
		p.box("root", 7.5F, -0.5F, -1.25F, 0.5F, 1F, 0.5F, new int[] {0, 21, 8, 1, 1, 1, 19, 8, 1, 1, 2, 52, 0, 1, 2, 3, 52, 0, 1, 2, 4, 50, 0, 1, 2, 5, 48, 0, 1, 2}); // cuff_r_front
		p.box("root", 7.5F, -0.5F, 0.75F, 0.5F, 1F, 0.5F, new int[] {0, 15, 8, 1, 1, 1, 13, 8, 1, 1, 2, 46, 0, 1, 2, 3, 46, 0, 1, 2, 4, 44, 0, 1, 2, 5, 42, 0, 1, 2}); // cuff_r_back
		p.box("float", -0.75F, -0.1F, -0.75F, 1.5F, 0.5F, 1.5F, new int[] {0, 38, 0, 3, 3, 1, 38, 0, 3, 3, 2, 9, 8, 3, 1, 3, 9, 8, 3, 1, 4, 5, 8, 3, 1, 5, 5, 8, 3, 1}); // float_cap
		p.box("float_glass", -1.5F, 0.4F, -1.5F, 3F, 2F, 3F, new int[] {0, 15, 0, 6, 6, 1, 8, 0, 6, 6, 2, 22, 0, 6, 4, 3, 22, 0, 6, 4, 4, 22, 0, 6, 4, 5, 22, 0, 6, 4}); // float_glass
		p.box("float", -1F, 2.4F, -1F, 2F, 0.5F, 2F, new int[] {0, 29, 0, 4, 4, 1, 29, 0, 4, 4, 2, 99, 0, 4, 1, 3, 99, 0, 4, 1, 4, 94, 0, 4, 1, 5, 94, 0, 4, 1}); // float_tip
		p.box("float", -1.75F, 1.15F, -1.75F, 3.5F, 0.5F, 3.5F, new int[] {0, 0, 0, 7, 7, 1, 0, 0, 7, 7, 2, 66, 0, 7, 1, 3, 66, 0, 7, 1, 4, 58, 0, 7, 1, 5, 58, 0, 7, 1}); // float_band
		p.box("float", -1.25F, -0.3F, -0.75F, 0.5F, 1F, 0.5F, new int[] {0, 27, 8, 1, 1, 1, 25, 8, 1, 1, 2, 56, 0, 1, 2, 3, 56, 0, 1, 2, 4, 54, 0, 1, 2, 5, 54, 0, 1, 2}); // fibre_l
		p.box("float", 0.75F, -0.3F, 0.25F, 0.5F, 1F, 0.5F, new int[] {0, 27, 8, 1, 1, 1, 25, 8, 1, 1, 2, 56, 0, 1, 2, 3, 56, 0, 1, 2, 4, 54, 0, 1, 2, 5, 54, 0, 1, 2}); // fibre_r
		return p.bake();
	}

	private CultivationRopeModel() {}
}
