package com.aquamarine.client.machine;

import com.aquamarine.Aquamarine;
import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

/**
 * Pulmão de Ferro: Vaso de ferro deitado com cintas de latão, visor de nível com boia acesa, mostrador de pressão, válvula de alívio e um fole de couro na ponta que enche com o ar guardado. Latão, vidro e vapor; 32 px por face.
 * GERADO por tools/models/machines/iron_lung/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class IronLungModel {
	public static final Identifier TEXTURE = Aquamarine.id("textures/block/machine/iron_lung.png");
	public static final Identifier GLOW = Aquamarine.id("textures/block/machine/iron_lung_glow.png");
	/** Partes de vidro: vão só na passada translúcida. */
	public static final Set<String> GLASS = Set.of("works_glass");

	public static ModelPart create() {
		MachineParts p = new MachineParts(128, 128);
		p.part("root", null, 0F, 24F, 0F, 0F, 0F, 0F);
		p.part("works", "root", 0F, -3F, 0F, 0F, 0F, 0F);
		p.part("works_glass", "works", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("float", "works", 0.5F, -2.5F, -4.75F, 0F, 0F, 0F);
		p.part("needle", "works", -4F, -5F, -5.125F, 0F, 0F, 0F);
		p.part("relief_cap", "works", -2F, -11F, 0F, 0F, 0F, 0F);
		p.part("bellows", "works", 3F, -5F, 0F, 0F, 0F, 0F);
		p.part("bellows_plate", "works", 7.25F, -5F, 0F, 0F, 0F, 0F);
		p.part("air_in", "works", -7F, -5F, 0F, 0F, -1.5708F, 0F);
		p.part("air_out", "works", -2F, -5F, 5F, 0F, 3.1416F, 0F);
		p.box("root", -7F, -2F, -7F, 14F, 1F, 14F, new int[] {0, 0, 0, 28, 28, 1, 0, 0, 28, 28, 2, 0, 65, 28, 2, 3, 0, 65, 28, 2, 4, 74, 60, 28, 2, 5, 74, 60, 28, 2}); // deck
		p.box("root", -7F, -1F, -7F, 3F, 1F, 3F, new int[] {0, 106, 47, 6, 6, 1, 106, 47, 6, 6, 2, 107, 65, 6, 2, 3, 107, 65, 6, 2, 4, 107, 65, 6, 2, 5, 107, 65, 6, 2}); // foot_0
		p.box("root", 4F, -1F, -7F, 3F, 1F, 3F, new int[] {0, 106, 47, 6, 6, 1, 106, 47, 6, 6, 2, 107, 65, 6, 2, 3, 107, 65, 6, 2, 4, 107, 65, 6, 2, 5, 107, 65, 6, 2}); // foot_1
		p.box("root", -7F, -1F, 4F, 3F, 1F, 3F, new int[] {0, 106, 47, 6, 6, 1, 106, 47, 6, 6, 2, 107, 65, 6, 2, 3, 107, 65, 6, 2, 4, 107, 65, 6, 2, 5, 107, 65, 6, 2}); // foot_2
		p.box("root", 4F, -1F, 4F, 3F, 1F, 3F, new int[] {0, 106, 47, 6, 6, 1, 106, 47, 6, 6, 2, 107, 65, 6, 2, 3, 107, 65, 6, 2, 4, 107, 65, 6, 2, 5, 107, 65, 6, 2}); // foot_3
		p.box("works", -6F, -1F, -4F, 2.5F, 2F, 8F, new int[] {0, 46, 29, 5, 16, 1, 40, 29, 5, 16, 2, 0, 60, 16, 4, 3, 0, 60, 16, 4, 4, 26, 60, 5, 4, 5, 26, 60, 5, 4}); // saddle_0
		p.box("works", 0F, -1F, -4F, 2.5F, 2F, 8F, new int[] {0, 46, 29, 5, 16, 1, 40, 29, 5, 16, 2, 0, 60, 16, 4, 3, 0, 60, 16, 4, 4, 26, 60, 5, 4, 5, 26, 60, 5, 4}); // saddle_1
		p.box("works", -6.5F, -8F, -4.5F, 9.5F, 6F, 9F, new int[] {0, 51, 0, 19, 18, 1, 31, 0, 19, 18, 2, 0, 47, 18, 12, 3, 19, 47, 18, 12, 4, 96, 29, 19, 12, 5, 96, 29, 19, 12}); // vessel
		p.box("works", -6.5F, -9F, -3.5F, 9.5F, 1F, 7F, new int[] {0, 54, 29, 19, 14, 1, 54, 29, 19, 14, 2, 49, 65, 14, 2, 3, 64, 65, 14, 2, 4, 29, 65, 19, 2, 5, 29, 65, 19, 2}); // vessel_top
		p.box("works", -6.5F, -2F, -3.5F, 9.5F, 1F, 7F, new int[] {0, 54, 29, 19, 14, 1, 54, 29, 19, 14, 2, 49, 65, 14, 2, 3, 64, 65, 14, 2, 4, 29, 65, 19, 2, 5, 29, 65, 19, 2}); // vessel_bottom
		p.box("works", -7F, -8.25F, -4.75F, 0.5F, 6.5F, 9.5F, new int[] {0, 29, 0, 1, 19, 1, 29, 0, 1, 19, 2, 74, 29, 19, 13, 3, 74, 29, 19, 13, 4, 94, 29, 1, 13, 5, 94, 29, 1, 13}); // hoop_0
		p.box("works", -7F, -9.25F, -3.75F, 0.5F, 8.5F, 7.5F, new int[] {0, 52, 29, 1, 15, 1, 52, 29, 1, 15, 2, 18, 29, 15, 17, 3, 18, 29, 15, 17, 4, 38, 29, 1, 17, 5, 38, 29, 1, 17}); // hoop_cap_0
		p.box("works", -2.5F, -8.25F, -4.75F, 0.5F, 6.5F, 9.5F, new int[] {0, 29, 0, 1, 19, 1, 29, 0, 1, 19, 2, 74, 29, 19, 13, 3, 74, 29, 19, 13, 4, 94, 29, 1, 13, 5, 94, 29, 1, 13}); // hoop_1
		p.box("works", -2.5F, -9.25F, -3.75F, 0.5F, 8.5F, 7.5F, new int[] {0, 52, 29, 1, 15, 1, 52, 29, 1, 15, 2, 18, 29, 15, 17, 3, 18, 29, 15, 17, 4, 38, 29, 1, 17, 5, 38, 29, 1, 17}); // hoop_cap_1
		p.box("works", 2.5F, -8.25F, -4.75F, 0.5F, 6.5F, 9.5F, new int[] {0, 29, 0, 1, 19, 1, 29, 0, 1, 19, 2, 74, 29, 19, 13, 3, 74, 29, 19, 13, 4, 94, 29, 1, 13, 5, 94, 29, 1, 13}); // hoop_2
		p.box("works", 2.5F, -9.25F, -3.75F, 0.5F, 8.5F, 7.5F, new int[] {0, 52, 29, 1, 15, 1, 52, 29, 1, 15, 2, 18, 29, 15, 17, 3, 18, 29, 15, 17, 4, 38, 29, 1, 17, 5, 38, 29, 1, 17}); // hoop_cap_2
		p.box("works", -7.5F, -7.5F, -3F, 0.5F, 5F, 6F, new int[] {0, 62, 47, 1, 12, 1, 62, 47, 1, 12, 2, 64, 47, 12, 10, 3, 64, 47, 12, 10, 4, 77, 47, 1, 10, 5, 77, 47, 1, 10}); // end_cap
		p.box("works", -0.5F, -8.5F, -5.5F, 2F, 0.5F, 1.5F, new int[] {0, 65, 60, 4, 3, 1, 65, 60, 4, 3, 2, 58, 68, 3, 1, 3, 58, 68, 3, 1, 4, 48, 68, 4, 1, 5, 48, 68, 4, 1}); // sight_top
		p.box("works", -0.5F, -2F, -5.5F, 2F, 0.5F, 1.5F, new int[] {0, 65, 60, 4, 3, 1, 65, 60, 4, 3, 2, 58, 68, 3, 1, 3, 58, 68, 3, 1, 4, 48, 68, 4, 1, 5, 48, 68, 4, 1}); // sight_bottom
		p.box("works_glass", -0.25F, -8F, -5.25F, 1.5F, 6F, 1F, new int[] {0, 4, 68, 3, 2, 1, 0, 68, 3, 2, 2, 55, 47, 2, 12, 3, 55, 47, 2, 12, 4, 51, 47, 3, 12, 5, 51, 47, 3, 12}); // sight_glass
		p.box("float", -0.5F, -0.5F, -0.25F, 1F, 1F, 0.5F, new int[] {0, 62, 68, 2, 1, 1, 62, 68, 2, 1, 2, 14, 68, 1, 2, 3, 14, 68, 1, 2, 4, 8, 68, 2, 2, 5, 8, 68, 2, 2}); // float_mark
		p.box("works", -6F, -7F, -5F, 4F, 4F, 0.5F, new int[] {0, 27, 68, 8, 1, 1, 18, 68, 8, 1, 2, 90, 47, 1, 8, 3, 90, 47, 1, 8, 4, 79, 47, 8, 8, 5, 79, 47, 8, 8}); // needle_dial
		p.box("needle", -0.25F, -1.25F, -0.375F, 0.5F, 1.5F, 0.5F, new int[] {0, 71, 68, 1, 1, 1, 71, 68, 1, 1, 2, 72, 60, 1, 3, 3, 72, 60, 1, 3, 4, 70, 60, 1, 3, 5, 70, 60, 1, 3}); // needle_pointer
		p.box("works", -3F, -10.5F, -1F, 2F, 1.5F, 2F, new int[] {0, 42, 60, 4, 4, 1, 42, 60, 4, 4, 2, 60, 60, 4, 3, 3, 60, 60, 4, 3, 4, 55, 60, 4, 3, 5, 55, 60, 4, 3}); // relief_body
		p.box("works", -2.5F, -11F, -0.5F, 1F, 0.5F, 1F, new int[] {0, 11, 68, 2, 2, 1, 11, 68, 2, 2, 2, 68, 68, 2, 1, 3, 68, 68, 2, 1, 4, 65, 68, 2, 1, 5, 65, 68, 2, 1}); // relief_spring
		p.box("relief_cap", -1.25F, -0.5F, -1.25F, 2.5F, 0.5F, 2.5F, new int[] {0, 119, 47, 5, 5, 1, 119, 47, 5, 5, 2, 42, 68, 5, 1, 3, 42, 68, 5, 1, 4, 36, 68, 5, 1, 5, 36, 68, 5, 1}); // relief_lid
		p.box("relief_cap", -0.25F, -1F, -0.25F, 0.5F, 0.5F, 0.5F, new int[] {0, 77, 68, 1, 1, 1, 77, 68, 1, 1, 2, 75, 68, 1, 1, 3, 75, 68, 1, 1, 4, 73, 68, 1, 1, 5, 73, 68, 1, 1}); // relief_stem
		p.box("bellows", 0F, -4.25F, -4.25F, 0.5F, 8.5F, 8.5F, new int[] {0, 36, 29, 1, 17, 1, 36, 29, 1, 17, 2, 0, 29, 17, 17, 3, 0, 29, 17, 17, 4, 34, 29, 1, 17, 5, 34, 29, 1, 17}); // bellows_fold_0
		p.box("bellows", 0.5F, -3F, -3F, 0.5F, 6F, 6F, new int[] {0, 60, 47, 1, 12, 1, 60, 47, 1, 12, 2, 38, 47, 12, 12, 3, 38, 47, 12, 12, 4, 58, 47, 1, 12, 5, 58, 47, 1, 12}); // bellows_pleat_0
		p.box("bellows", 1F, -4.25F, -4.25F, 0.5F, 8.5F, 8.5F, new int[] {0, 36, 29, 1, 17, 1, 36, 29, 1, 17, 2, 0, 29, 17, 17, 3, 0, 29, 17, 17, 4, 34, 29, 1, 17, 5, 34, 29, 1, 17}); // bellows_fold_1
		p.box("bellows", 1.5F, -3F, -3F, 0.5F, 6F, 6F, new int[] {0, 60, 47, 1, 12, 1, 60, 47, 1, 12, 2, 38, 47, 12, 12, 3, 38, 47, 12, 12, 4, 58, 47, 1, 12, 5, 58, 47, 1, 12}); // bellows_pleat_1
		p.box("bellows", 2F, -4.25F, -4.25F, 0.5F, 8.5F, 8.5F, new int[] {0, 36, 29, 1, 17, 1, 36, 29, 1, 17, 2, 0, 29, 17, 17, 3, 0, 29, 17, 17, 4, 34, 29, 1, 17, 5, 34, 29, 1, 17}); // bellows_fold_2
		p.box("bellows", 2.5F, -3F, -3F, 0.5F, 6F, 6F, new int[] {0, 60, 47, 1, 12, 1, 60, 47, 1, 12, 2, 38, 47, 12, 12, 3, 38, 47, 12, 12, 4, 58, 47, 1, 12, 5, 58, 47, 1, 12}); // bellows_pleat_2
		p.box("bellows", 3F, -4.25F, -4.25F, 0.5F, 8.5F, 8.5F, new int[] {0, 36, 29, 1, 17, 1, 36, 29, 1, 17, 2, 0, 29, 17, 17, 3, 0, 29, 17, 17, 4, 34, 29, 1, 17, 5, 34, 29, 1, 17}); // bellows_fold_3
		p.box("bellows", 3.5F, -3F, -3F, 0.5F, 6F, 6F, new int[] {0, 60, 47, 1, 12, 1, 60, 47, 1, 12, 2, 38, 47, 12, 12, 3, 38, 47, 12, 12, 4, 58, 47, 1, 12, 5, 58, 47, 1, 12}); // bellows_pleat_3
		p.box("bellows", 2F, -4.5F, -4.5F, 0.5F, 9F, 9F, new int[] {0, 115, 0, 1, 18, 1, 115, 0, 1, 18, 2, 90, 0, 18, 18, 3, 90, 0, 18, 18, 4, 113, 0, 1, 18, 5, 113, 0, 1, 18}); // bellows_wire
		p.box("bellows_plate", -0.25F, -4.5F, -4.5F, 0.5F, 9F, 9F, new int[] {0, 111, 0, 1, 18, 1, 111, 0, 1, 18, 2, 71, 0, 18, 18, 3, 71, 0, 18, 18, 4, 109, 0, 1, 18, 5, 109, 0, 1, 18}); // bellows_board
		p.box("bellows_plate", 0.25F, -1F, -2F, 0.5F, 2F, 4F, new int[] {0, 88, 47, 1, 8, 1, 88, 47, 1, 8, 2, 17, 60, 8, 4, 3, 17, 60, 8, 4, 4, 53, 60, 1, 4, 5, 53, 60, 1, 4}); // bellows_handle
		p.box("works", 3.5F, 0F, -7F, 2F, 1F, 0.5F, new int[] {0, 53, 68, 4, 1, 1, 53, 68, 4, 1, 2, 16, 68, 1, 2, 3, 16, 68, 1, 2, 4, 124, 65, 4, 2, 5, 124, 65, 4, 2}); // status_lamp
		p.box("air_in", -1.5F, -1.5F, -0.5F, 3F, 3F, 1F, new int[] {0, 86, 65, 6, 2, 1, 79, 65, 6, 2, 2, 113, 47, 2, 6, 3, 113, 47, 2, 6, 4, 92, 47, 6, 6, 5, 92, 47, 6, 6}); // air_in_ring
		p.box("air_in", -1F, -1F, 0.5F, 2F, 2F, 1F, new int[] {0, 114, 65, 4, 2, 1, 114, 65, 4, 2, 2, 47, 60, 2, 4, 3, 47, 60, 2, 4, 4, 32, 60, 4, 4, 5, 32, 60, 4, 4}); // air_in_neck
		p.box("air_out", -1.5F, -1.5F, -0.5F, 3F, 3F, 1F, new int[] {0, 100, 65, 6, 2, 1, 93, 65, 6, 2, 2, 116, 47, 2, 6, 3, 116, 47, 2, 6, 4, 99, 47, 6, 6, 5, 99, 47, 6, 6}); // air_out_ring
		p.box("air_out", -1F, -1F, 0.5F, 2F, 2F, 1F, new int[] {0, 119, 65, 4, 2, 1, 119, 65, 4, 2, 2, 50, 60, 2, 4, 3, 50, 60, 2, 4, 4, 37, 60, 4, 4, 5, 37, 60, 4, 4}); // air_out_neck
		return p.bake();
	}

	private IronLungModel() {}
}
