package com.aquamarine.client.machine;

import com.aquamarine.Aquamarine;
import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

/**
 * Garrafão de Cultivo: Vidro verde-água em gaiola de latão, corda trançada, ostra articulada e pérola pronta com brilho próprio.
 * GERADO por tools/models/machines/cultivation_jar/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class CultivationJarModel {
	public static final Identifier TEXTURE = Aquamarine.id("textures/block/machine/cultivation_jar.png");
	public static final Identifier GLOW = Aquamarine.id("textures/block/machine/cultivation_jar_glow.png");
	/** Partes de vidro: vão só na passada translúcida. */
	public static final Set<String> GLASS = Set.of("bubble_0_glass", "bubble_1_glass", "bubble_2_glass", "frame_glass");

	public static ModelPart create() {
		MachineParts p = new MachineParts(128, 128);
		p.part("frame", null, 0F, 24F, 0F, 0F, 0F, 0F);
		p.part("frame_glass", "frame", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("oyster", "frame", 0F, -2F, 0F, 0F, 0F, 0F);
		p.part("lid", "oyster", 0F, -2F, 2.5F, 0F, 0F, 0F);
		p.part("pearl", "oyster", 0F, -2.5F, -1F, 0F, 0F, 0F);
		p.part("bubble_0", "frame", -3F, -7F, -1F, 0F, 0F, 0F);
		p.part("bubble_0_glass", "bubble_0", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("bubble_1", "frame", 2F, -7F, 1F, 0F, 0F, 0F);
		p.part("bubble_1_glass", "bubble_1", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("bubble_2", "frame", -1F, -7F, 2F, 0F, 0F, 0F);
		p.part("bubble_2_glass", "bubble_2", 0F, 0F, 0F, 0F, 0F, 0F);
		p.box("frame", -5F, -1F, -5F, 10F, 1F, 10F, new int[] {1, 0, 0, 20, 20, 2, 84, 99, 20, 2, 3, 105, 99, 20, 2, 4, 63, 99, 20, 2, 5, 42, 99, 20, 2}); // foot
		p.box("frame", -5.5F, -2F, -5.5F, 11F, 1F, 11F, new int[] {2, 71, 93, 22, 2, 3, 94, 93, 22, 2, 4, 48, 93, 22, 2, 5, 25, 93, 22, 2}); // bottom
		p.box("frame", -6F, -3F, -6F, 12F, 1F, 1F, new int[] {0, 25, 90, 24, 2, 1, 100, 87, 24, 2, 2, 95, 111, 2, 2, 3, 98, 111, 2, 2, 4, 0, 90, 24, 2, 5, 75, 87, 24, 2}); // band_2_0
		p.box("frame", -6F, -3F, 5F, 12F, 1F, 1F, new int[] {0, 0, 93, 24, 2, 1, 75, 90, 24, 2, 2, 101, 111, 2, 2, 3, 104, 111, 2, 2, 4, 100, 90, 24, 2, 5, 50, 90, 24, 2}); // band_2_1
		p.box("frame", -6F, -3F, -5F, 1F, 1F, 10F, new int[] {0, 36, 0, 2, 20, 1, 33, 0, 2, 20, 2, 84, 96, 20, 2, 3, 105, 96, 20, 2, 4, 110, 111, 2, 2, 5, 107, 111, 2, 2}); // band_2_2
		p.box("frame", 5F, -3F, -5F, 1F, 1F, 10F, new int[] {0, 42, 0, 2, 20, 1, 39, 0, 2, 20, 2, 0, 99, 20, 2, 3, 21, 99, 20, 2, 4, 116, 111, 2, 2, 5, 113, 111, 2, 2}); // band_2_3
		p.box("frame", -6F, -11F, -6F, 12F, 1F, 1F, new int[] {0, 75, 84, 24, 2, 1, 25, 84, 24, 2, 2, 71, 111, 2, 2, 3, 74, 111, 2, 2, 4, 50, 84, 24, 2, 5, 0, 84, 24, 2}); // band_10_0
		p.box("frame", -6F, -11F, 5F, 12F, 1F, 1F, new int[] {0, 50, 87, 24, 2, 1, 0, 87, 24, 2, 2, 77, 111, 2, 2, 3, 80, 111, 2, 2, 4, 25, 87, 24, 2, 5, 100, 84, 24, 2}); // band_10_1
		p.box("frame", -6F, -11F, -5F, 1F, 1F, 10F, new int[] {0, 24, 0, 2, 20, 1, 21, 0, 2, 20, 2, 0, 96, 20, 2, 3, 21, 96, 20, 2, 4, 86, 111, 2, 2, 5, 83, 111, 2, 2}); // band_10_2
		p.box("frame", 5F, -11F, -5F, 1F, 1F, 10F, new int[] {0, 30, 0, 2, 20, 1, 27, 0, 2, 20, 2, 42, 96, 20, 2, 3, 63, 96, 20, 2, 4, 92, 111, 2, 2, 5, 89, 111, 2, 2}); // band_10_3
		p.box("frame", -5.5F, -10F, -5.5F, 1F, 7F, 1F, new int[] {0, 12, 114, 2, 2, 1, 9, 114, 2, 2, 2, 3, 51, 2, 14, 3, 6, 51, 2, 14, 4, 0, 51, 2, 14, 5, 126, 36, 2, 14}); // brace_2.5_2.5
		p.box("frame", -5.5F, -10F, 4.5F, 1F, 7F, 1F, new int[] {0, 6, 114, 2, 2, 1, 3, 114, 2, 2, 2, 120, 36, 2, 14, 3, 123, 36, 2, 14, 4, 117, 36, 2, 14, 5, 114, 36, 2, 14}); // brace_2.5_12.5
		p.box("frame", 4.5F, -10F, -5.5F, 1F, 7F, 1F, new int[] {0, 0, 114, 2, 2, 1, 125, 111, 2, 2, 2, 108, 36, 2, 14, 3, 111, 36, 2, 14, 4, 105, 36, 2, 14, 5, 102, 36, 2, 14}); // brace_12.5_2.5
		p.box("frame", 4.5F, -10F, 4.5F, 1F, 7F, 1F, new int[] {0, 122, 111, 2, 2, 1, 119, 111, 2, 2, 2, 96, 36, 2, 14, 3, 99, 36, 2, 14, 4, 93, 36, 2, 14, 5, 90, 36, 2, 14}); // brace_12.5_12.5
		p.box("frame_glass", -5F, -10F, -5.5F, 10F, 7F, 0.5F, new int[] {0, 78, 114, 20, 1, 1, 57, 114, 20, 1, 2, 13, 51, 1, 14, 3, 15, 51, 1, 14, 4, 21, 21, 20, 14, 5, 0, 21, 20, 14}); // glass_front
		p.box("frame_glass", -5F, -10F, 5F, 10F, 7F, 0.5F, new int[] {0, 36, 114, 20, 1, 1, 15, 114, 20, 1, 2, 9, 51, 1, 14, 3, 11, 51, 1, 14, 4, 108, 0, 20, 14, 5, 87, 0, 20, 14}); // glass_back
		p.box("frame_glass", -5.5F, -10F, -5F, 0.5F, 7F, 10F, new int[] {0, 47, 0, 1, 20, 1, 45, 0, 1, 20, 2, 42, 21, 20, 14, 3, 63, 21, 20, 14, 4, 19, 51, 1, 14, 5, 17, 51, 1, 14}); // glass_left
		p.box("frame_glass", 5F, -10F, -5F, 0.5F, 7F, 10F, new int[] {0, 51, 0, 1, 20, 1, 49, 0, 1, 20, 2, 84, 21, 20, 14, 3, 105, 21, 20, 14, 4, 23, 51, 1, 14, 5, 21, 51, 1, 14}); // glass_right
		p.box("frame_glass", -4F, -11.5F, -4F, 8F, 1F, 8F, new int[] {0, 70, 0, 16, 16, 1, 53, 0, 16, 16, 2, 34, 102, 16, 2, 3, 51, 102, 16, 2, 4, 17, 102, 16, 2, 5, 0, 102, 16, 2}); // glass_shoulder
		p.box("frame_glass", -3F, -13.5F, -3F, 6F, 2F, 6F, new int[] {0, 94, 51, 12, 12, 1, 81, 51, 12, 12, 2, 97, 66, 12, 4, 3, 110, 66, 12, 4, 4, 84, 66, 12, 4, 5, 71, 66, 12, 4}); // neck
		p.box("frame", -3.5F, -14F, -3.5F, 7F, 1F, 7F, new int[] {0, 75, 36, 14, 14, 1, 60, 36, 14, 14, 2, 30, 105, 14, 2, 3, 45, 105, 14, 2, 4, 15, 105, 14, 2, 5, 0, 105, 14, 2}); // neck_band
		p.box("frame", -3F, -15F, -3F, 6F, 1F, 6F, new int[] {0, 0, 66, 12, 12, 1, 107, 51, 12, 12, 2, 39, 108, 12, 2, 3, 52, 108, 12, 2, 4, 26, 108, 12, 2, 5, 13, 108, 12, 2}); // stopper
		p.box("frame", -3.5F, -15.5F, -3.5F, 7F, 0.5F, 7F, new int[] {0, 15, 36, 14, 14, 1, 0, 36, 14, 14, 2, 0, 117, 14, 1, 3, 15, 117, 14, 1, 4, 114, 114, 14, 1, 5, 99, 114, 14, 1}); // cap
		p.box("frame", -2F, -16F, -1F, 1F, 0.5F, 2F, new int[] {0, 111, 79, 2, 4, 1, 108, 79, 2, 4, 2, 76, 117, 4, 1, 3, 81, 117, 4, 1, 4, 99, 117, 2, 1, 5, 96, 117, 2, 1}); // handle_l
		p.box("frame", 1F, -16F, -1F, 1F, 0.5F, 2F, new int[] {0, 117, 79, 2, 4, 1, 114, 79, 2, 4, 2, 86, 117, 4, 1, 3, 91, 117, 4, 1, 4, 105, 117, 2, 1, 5, 102, 117, 2, 1}); // handle_r
		p.box("frame", -1F, -16F, -1F, 2F, 0.5F, 2F, new int[] {0, 63, 79, 4, 4, 1, 58, 79, 4, 4, 2, 66, 117, 4, 1, 3, 71, 117, 4, 1, 4, 61, 117, 4, 1, 5, 56, 117, 4, 1}); // handle
		p.box("frame", -8F, -12F, -1F, 5F, 1F, 2F, new int[] {0, 11, 79, 10, 4, 1, 0, 79, 10, 4, 2, 51, 111, 4, 2, 3, 56, 111, 4, 2, 4, 102, 108, 10, 2, 5, 91, 108, 10, 2}); // rope_left
		p.box("frame", 3F, -12F, -1F, 5F, 1F, 2F, new int[] {0, 33, 79, 10, 4, 1, 22, 79, 10, 4, 2, 61, 111, 4, 2, 3, 66, 111, 4, 2, 4, 0, 111, 10, 2, 5, 113, 108, 10, 2}); // rope_right
		p.box("frame", -4.5F, -12.5F, -1.5F, 2F, 2F, 3F, new int[] {0, 40, 66, 4, 6, 1, 35, 66, 4, 6, 2, 44, 79, 6, 4, 3, 51, 79, 6, 4, 4, 73, 79, 4, 4, 5, 68, 79, 4, 4}); // knot
		p.box("frame", -3F, -3.2F, -6.5F, 6F, 2F, 0.5F, new int[] {0, 43, 117, 12, 1, 1, 30, 117, 12, 1, 2, 120, 79, 1, 4, 3, 122, 79, 1, 4, 4, 58, 66, 12, 4, 5, 45, 66, 12, 4}); // label
		p.box("oyster", -3.5F, -1F, -3.5F, 7F, 1F, 7F, new int[] {0, 45, 36, 14, 14, 1, 30, 36, 14, 14, 2, 98, 102, 14, 2, 3, 113, 102, 14, 2, 4, 83, 102, 14, 2, 5, 68, 102, 14, 2}); // lower_rim
		p.box("oyster", -3F, -2F, -3F, 6F, 1F, 6F, new int[] {0, 68, 51, 12, 12, 1, 55, 51, 12, 12, 2, 116, 105, 12, 2, 3, 0, 108, 12, 2, 4, 103, 105, 12, 2, 5, 90, 105, 12, 2}); // lower_cup
		p.box("lid", -3.5F, -1F, -6F, 7F, 1F, 6F, new int[] {0, 40, 51, 14, 12, 1, 25, 51, 14, 12, 2, 65, 108, 12, 2, 3, 78, 108, 12, 2, 4, 75, 105, 14, 2, 5, 60, 105, 14, 2}); // upper_shell
		p.box("lid", -2.5F, -2F, -5F, 5F, 1F, 4F, new int[] {0, 24, 66, 10, 8, 1, 13, 66, 10, 8, 2, 33, 111, 8, 2, 3, 42, 111, 8, 2, 4, 22, 111, 10, 2, 5, 11, 111, 10, 2}); // upper_crown
		p.box("pearl", -1F, -1.5F, -1F, 2F, 2F, 2F, new int[] {0, 103, 79, 4, 4, 1, 83, 79, 4, 4, 2, 93, 79, 4, 4, 3, 98, 79, 4, 4, 4, 88, 79, 4, 4, 5, 78, 79, 4, 4}); // pearl
		p.box("bubble_0_glass", 0F, -0.5F, 0F, 0.5F, 0.5F, 0.5F, new int[] {0, 118, 117, 1, 1, 1, 110, 117, 1, 1, 2, 114, 117, 1, 1, 3, 116, 117, 1, 1, 4, 112, 117, 1, 1, 5, 108, 117, 1, 1}); // bubble_0
		p.box("bubble_1_glass", 0F, -0.5F, 0F, 0.5F, 0.5F, 0.5F, new int[] {0, 2, 119, 1, 1, 1, 122, 117, 1, 1, 2, 126, 117, 1, 1, 3, 0, 119, 1, 1, 4, 124, 117, 1, 1, 5, 120, 117, 1, 1}); // bubble_1
		p.box("bubble_2_glass", 0F, -0.5F, 0F, 0.5F, 0.5F, 0.5F, new int[] {0, 14, 119, 1, 1, 1, 6, 119, 1, 1, 2, 10, 119, 1, 1, 3, 12, 119, 1, 1, 4, 8, 119, 1, 1, 5, 4, 119, 1, 1}); // bubble_2
		return p.bake();
	}

	private CultivationJarModel() {}
}
