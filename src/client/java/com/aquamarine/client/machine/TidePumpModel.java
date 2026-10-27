package com.aquamarine.client.machine;

import com.aquamarine.Aquamarine;
import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

/**
 * Bomba de Maré: Bomba de pistão com volante exposto de cobre, corpo de aço, sucção gradeada e mostrador de vazão. Latão, vidro e vapor; 32 px por face.
 * GERADO por tools/models/machines/tide_pump/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class TidePumpModel {
	public static final Identifier TEXTURE = Aquamarine.id("textures/block/machine/tide_pump.png");
	public static final Identifier GLOW = Aquamarine.id("textures/block/machine/tide_pump_glow.png");
	/** Partes de vidro: vão só na passada translúcida. */
	public static final Set<String> GLASS = Set.of();

	public static ModelPart create() {
		MachineParts p = new MachineParts(128, 128);
		p.part("root", null, 0F, 24F, 0F, 0F, 0F, 0F);
		p.part("works", "root", 0F, -3F, 0F, 0F, 0F, 0F);
		p.part("piston", "works", -3.5F, -5F, 0.5F, 0F, 0F, 0F);
		p.part("flywheel", "works", 2.5F, -3.5F, -4F, 0F, 0F, 0F);
		p.part("flywheel_rim_0", "flywheel", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("flywheel_rim_1", "flywheel", 0F, 0F, 0F, 0F, 0F, 0.7854F);
		p.part("flywheel_rim_2", "flywheel", 0F, 0F, 0F, 0F, 0F, 1.5708F);
		p.part("flywheel_rim_3", "flywheel", 0F, 0F, 0F, 0F, 0F, 2.3562F);
		p.part("flywheel_rim_4", "flywheel", 0F, 0F, 0F, 0F, 0F, 3.1416F);
		p.part("flywheel_rim_5", "flywheel", 0F, 0F, 0F, 0F, 0F, 3.927F);
		p.part("flywheel_rim_6", "flywheel", 0F, 0F, 0F, 0F, 0F, 4.7124F);
		p.part("flywheel_rim_7", "flywheel", 0F, 0F, 0F, 0F, 0F, 5.4978F);
		p.part("flywheel_spoke_0", "flywheel", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("flywheel_spoke_1", "flywheel", 0F, 0F, 0F, 0F, 0F, 0.7854F);
		p.part("flywheel_spoke_2", "flywheel", 0F, 0F, 0F, 0F, 0F, 1.5708F);
		p.part("flywheel_spoke_3", "flywheel", 0F, 0F, 0F, 0F, 0F, 2.3562F);
		p.part("rod_link", "works", 2.5F, -3.5F, -2F, 0F, 0F, 0F);
		p.part("water_out", "works", -3F, -4F, 7.5F, 0F, 3.1416F, 0F);
		p.part("energy_in", "works", 6.5F, -0.5F, 2F, 0F, 1.5708F, 0F);
		p.part("needle", "works", 2.5F, -8F, 1.875F, 0F, 0F, 0F);
		p.box("root", -7F, -2F, -7F, 14F, 1F, 14F, new int[] {0, 0, 0, 28, 28, 1, 0, 0, 28, 28, 2, 77, 36, 28, 2, 3, 77, 36, 28, 2, 4, 48, 36, 28, 2, 5, 48, 36, 28, 2}); // deck
		p.box("root", -7F, -1F, -7F, 3F, 1F, 3F, new int[] {0, 59, 29, 6, 6, 1, 59, 29, 6, 6, 2, 62, 41, 6, 2, 3, 62, 41, 6, 2, 4, 62, 41, 6, 2, 5, 62, 41, 6, 2}); // foot_0
		p.box("root", 4F, -1F, -7F, 3F, 1F, 3F, new int[] {0, 59, 29, 6, 6, 1, 59, 29, 6, 6, 2, 62, 41, 6, 2, 3, 62, 41, 6, 2, 4, 62, 41, 6, 2, 5, 62, 41, 6, 2}); // foot_1
		p.box("root", -7F, -1F, 4F, 3F, 1F, 3F, new int[] {0, 59, 29, 6, 6, 1, 59, 29, 6, 6, 2, 62, 41, 6, 2, 3, 62, 41, 6, 2, 4, 62, 41, 6, 2, 5, 62, 41, 6, 2}); // foot_2
		p.box("root", 4F, -1F, 4F, 3F, 1F, 3F, new int[] {0, 59, 29, 6, 6, 1, 59, 29, 6, 6, 2, 62, 41, 6, 2, 3, 62, 41, 6, 2, 4, 62, 41, 6, 2, 5, 62, 41, 6, 2}); // foot_3
		p.box("works", -6F, 0F, -2F, 5F, 1F, 5F, new int[] {0, 50, 0, 10, 10, 1, 50, 0, 10, 10, 2, 37, 41, 10, 2, 3, 37, 41, 10, 2, 4, 37, 41, 10, 2, 5, 37, 41, 10, 2}); // pump_shoe
		p.box("works", -5.5F, -5F, -1.5F, 4F, 5F, 4F, new int[] {0, 96, 0, 8, 8, 1, 87, 0, 8, 8, 2, 61, 0, 8, 10, 3, 61, 0, 8, 10, 4, 61, 0, 8, 10, 5, 61, 0, 8, 10}); // barrel
		p.box("works", -6F, -0.5F, -2F, 5F, 0.5F, 5F, new int[] {0, 39, 0, 10, 10, 1, 39, 0, 10, 10, 2, 34, 44, 10, 1, 3, 34, 44, 10, 1, 4, 34, 44, 10, 1, 5, 34, 44, 10, 1}); // barrel_band_0
		p.box("works", -6F, -5F, -2F, 5F, 0.5F, 5F, new int[] {0, 39, 0, 10, 10, 1, 39, 0, 10, 10, 2, 34, 44, 10, 1, 3, 34, 44, 10, 1, 4, 34, 44, 10, 1, 5, 34, 44, 10, 1}); // barrel_band_1
		p.box("piston", -0.5F, -4.5F, -0.5F, 1F, 5F, 1F, new int[] {0, 12, 44, 2, 2, 1, 12, 44, 2, 2, 2, 84, 0, 2, 10, 3, 84, 0, 2, 10, 4, 84, 0, 2, 10, 5, 84, 0, 2, 10}); // piston_rod
		p.box("piston", -1.5F, -5F, -1.5F, 3F, 1F, 3F, new int[] {0, 66, 29, 6, 6, 1, 66, 29, 6, 6, 2, 69, 41, 6, 2, 3, 69, 41, 6, 2, 4, 69, 41, 6, 2, 5, 69, 41, 6, 2}); // piston_head
		p.box("works", -6.5F, -11F, 0F, 0.5F, 11F, 1F, new int[] {0, 17, 44, 1, 2, 1, 17, 44, 1, 2, 2, 29, 0, 2, 22, 3, 29, 0, 2, 22, 4, 32, 0, 1, 22, 5, 32, 0, 1, 22}); // guide_0
		p.box("works", -1F, -11F, 0F, 0.5F, 11F, 1F, new int[] {0, 17, 44, 1, 2, 1, 17, 44, 1, 2, 2, 29, 0, 2, 22, 3, 29, 0, 2, 22, 4, 32, 0, 1, 22, 5, 32, 0, 1, 22}); // guide_1
		p.box("works", -7F, -12F, -0.5F, 7F, 1F, 2F, new int[] {0, 99, 29, 14, 4, 1, 99, 29, 14, 4, 2, 102, 41, 4, 2, 3, 102, 41, 4, 2, 4, 106, 36, 14, 2, 5, 106, 36, 14, 2}); // crosshead
		p.box("works", 1F, -4F, -0.5F, 3F, 5F, 3F, new int[] {0, 45, 29, 6, 6, 1, 38, 29, 6, 6, 2, 77, 0, 6, 10, 3, 77, 0, 6, 10, 4, 70, 0, 6, 10, 5, 70, 0, 6, 10}); // bearing
		p.box("works", 2F, -4F, -5F, 1F, 1F, 6F, new int[] {0, 34, 0, 2, 12, 1, 34, 0, 2, 12, 2, 0, 41, 12, 2, 3, 0, 41, 12, 2, 4, 0, 44, 2, 2, 5, 0, 44, 2, 2}); // axle
		p.box("flywheel_rim_0", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 96, 41, 5, 2, 1, 96, 41, 5, 2, 2, 9, 44, 2, 2, 3, 9, 44, 2, 2, 4, 90, 41, 5, 2, 5, 90, 41, 5, 2}); // flywheel_rim_0
		p.box("flywheel_rim_1", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 96, 41, 5, 2, 1, 96, 41, 5, 2, 2, 9, 44, 2, 2, 3, 9, 44, 2, 2, 4, 90, 41, 5, 2, 5, 90, 41, 5, 2}); // flywheel_rim_1
		p.box("flywheel_rim_2", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 96, 41, 5, 2, 1, 96, 41, 5, 2, 2, 9, 44, 2, 2, 3, 9, 44, 2, 2, 4, 90, 41, 5, 2, 5, 90, 41, 5, 2}); // flywheel_rim_2
		p.box("flywheel_rim_3", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 96, 41, 5, 2, 1, 96, 41, 5, 2, 2, 9, 44, 2, 2, 3, 9, 44, 2, 2, 4, 90, 41, 5, 2, 5, 90, 41, 5, 2}); // flywheel_rim_3
		p.box("flywheel_rim_4", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 96, 41, 5, 2, 1, 96, 41, 5, 2, 2, 9, 44, 2, 2, 3, 9, 44, 2, 2, 4, 90, 41, 5, 2, 5, 90, 41, 5, 2}); // flywheel_rim_4
		p.box("flywheel_rim_5", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 96, 41, 5, 2, 1, 96, 41, 5, 2, 2, 9, 44, 2, 2, 3, 9, 44, 2, 2, 4, 90, 41, 5, 2, 5, 90, 41, 5, 2}); // flywheel_rim_5
		p.box("flywheel_rim_6", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 96, 41, 5, 2, 1, 96, 41, 5, 2, 2, 9, 44, 2, 2, 3, 9, 44, 2, 2, 4, 90, 41, 5, 2, 5, 90, 41, 5, 2}); // flywheel_rim_6
		p.box("flywheel_rim_7", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 96, 41, 5, 2, 1, 96, 41, 5, 2, 2, 9, 44, 2, 2, 3, 9, 44, 2, 2, 4, 90, 41, 5, 2, 5, 90, 41, 5, 2}); // flywheel_rim_7
		p.box("flywheel_spoke_0", -0.25F, -3F, -0.25F, 0.5F, 6F, 0.5F, new int[] {0, 72, 44, 1, 1, 1, 72, 44, 1, 1, 2, 37, 0, 1, 12, 3, 37, 0, 1, 12, 4, 37, 0, 1, 12, 5, 37, 0, 1, 12}); // flywheel_spoke_0
		p.box("flywheel_spoke_1", -0.25F, -3F, -0.25F, 0.5F, 6F, 0.5F, new int[] {0, 72, 44, 1, 1, 1, 72, 44, 1, 1, 2, 37, 0, 1, 12, 3, 37, 0, 1, 12, 4, 37, 0, 1, 12, 5, 37, 0, 1, 12}); // flywheel_spoke_1
		p.box("flywheel_spoke_2", -0.25F, -3F, -0.25F, 0.5F, 6F, 0.5F, new int[] {0, 72, 44, 1, 1, 1, 72, 44, 1, 1, 2, 37, 0, 1, 12, 3, 37, 0, 1, 12, 4, 37, 0, 1, 12, 5, 37, 0, 1, 12}); // flywheel_spoke_2
		p.box("flywheel_spoke_3", -0.25F, -3F, -0.25F, 0.5F, 6F, 0.5F, new int[] {0, 72, 44, 1, 1, 1, 72, 44, 1, 1, 2, 37, 0, 1, 12, 3, 37, 0, 1, 12, 4, 37, 0, 1, 12, 5, 37, 0, 1, 12}); // flywheel_spoke_3
		p.box("flywheel", -0.5F, -0.5F, -1F, 1F, 1F, 2F, new int[] {0, 32, 36, 2, 4, 1, 32, 36, 2, 4, 2, 112, 41, 4, 2, 3, 112, 41, 4, 2, 4, 6, 44, 2, 2, 5, 6, 44, 2, 2}); // flywheel_hub
		p.box("flywheel", 1F, 0F, -0.75F, 1.5F, 1.5F, 0.5F, new int[] {0, 68, 44, 3, 1, 1, 68, 44, 3, 1, 2, 42, 36, 1, 3, 3, 42, 36, 1, 3, 4, 38, 36, 3, 3, 5, 38, 36, 3, 3}); // counterweight
		p.box("rod_link", -6F, -0.5F, -0.25F, 6F, 1F, 0.5F, new int[] {0, 21, 44, 12, 1, 1, 21, 44, 12, 1, 2, 15, 44, 1, 2, 3, 15, 44, 1, 2, 4, 13, 41, 12, 2, 5, 13, 41, 12, 2}); // connecting_rod
		p.box("works", -6F, -2F, -6F, 4F, 3F, 3F, new int[] {0, 29, 29, 8, 6, 1, 11, 29, 8, 6, 2, 73, 29, 6, 6, 3, 73, 29, 6, 6, 4, 20, 29, 8, 6, 5, 20, 29, 8, 6}); // suction_elbow
		p.box("works", -6.5F, -2F, -7F, 5F, 3F, 1F, new int[] {0, 26, 41, 10, 2, 1, 26, 41, 10, 2, 2, 93, 29, 2, 6, 3, 93, 29, 2, 6, 4, 0, 29, 10, 6, 5, 0, 29, 10, 6}); // intake_grate
		p.box("works", -5F, -3F, -5F, 2F, 2F, 4F, new int[] {0, 119, 0, 4, 8, 1, 119, 0, 4, 8, 2, 0, 36, 8, 4, 3, 0, 36, 8, 4, 4, 19, 36, 4, 4, 5, 19, 36, 4, 4}); // suction_riser
		p.box("works", -4F, -5F, 3F, 2F, 2F, 4F, new int[] {0, 114, 0, 4, 8, 1, 114, 0, 4, 8, 2, 114, 29, 8, 4, 3, 114, 29, 8, 4, 4, 9, 36, 4, 4, 5, 9, 36, 4, 4}); // delivery_riser
		p.box("water_out", -1.5F, -1.5F, -0.5F, 3F, 3F, 1F, new int[] {0, 83, 41, 6, 2, 1, 76, 41, 6, 2, 2, 96, 29, 2, 6, 3, 96, 29, 2, 6, 4, 80, 29, 6, 6, 5, 80, 29, 6, 6}); // water_out_ring
		p.box("water_out", -1F, -1F, 0.5F, 2F, 2F, 1F, new int[] {0, 122, 41, 4, 2, 1, 122, 41, 4, 2, 2, 35, 36, 2, 4, 3, 35, 36, 2, 4, 4, 24, 36, 4, 4, 5, 24, 36, 4, 4}); // water_out_neck
		p.box("energy_in", -1.5F, -1.5F, -0.5F, 3F, 3F, 1F, new int[] {0, 55, 41, 6, 2, 1, 48, 41, 6, 2, 2, 90, 29, 2, 6, 3, 90, 29, 2, 6, 4, 52, 29, 6, 6, 5, 52, 29, 6, 6}); // energy_in_ring
		p.box("energy_in", -1F, -1F, 0.5F, 2F, 2F, 1F, new int[] {0, 107, 41, 4, 2, 1, 107, 41, 4, 2, 2, 29, 36, 2, 4, 3, 29, 36, 2, 4, 4, 14, 36, 4, 4, 5, 14, 36, 4, 4}); // energy_in_neck
		p.box("works", 0.5F, -10F, 2F, 4F, 4F, 0.5F, new int[] {0, 54, 44, 8, 1, 1, 45, 44, 8, 1, 2, 124, 0, 1, 8, 3, 124, 0, 1, 8, 4, 105, 0, 8, 8, 5, 105, 0, 8, 8}); // needle_dial
		p.box("needle", -0.25F, -1.25F, -0.375F, 0.5F, 1.5F, 0.5F, new int[] {0, 74, 44, 1, 1, 1, 74, 44, 1, 1, 2, 46, 36, 1, 3, 3, 46, 36, 1, 3, 4, 44, 36, 1, 3, 5, 44, 36, 1, 3}); // needle_pointer
		p.box("works", 2F, -7F, 3F, 1F, 3F, 1F, new int[] {0, 3, 44, 2, 2, 1, 3, 44, 2, 2, 2, 87, 29, 2, 6, 3, 87, 29, 2, 6, 4, 87, 29, 2, 6, 5, 87, 29, 2, 6}); // dial_stem
		p.box("works", -0.5F, 0F, -7F, 2F, 1F, 0.5F, new int[] {0, 63, 44, 4, 1, 1, 63, 44, 4, 1, 2, 19, 44, 1, 2, 3, 19, 44, 1, 2, 4, 117, 41, 4, 2, 5, 117, 41, 4, 2}); // status_lamp
		return p.bake();
	}

	private TidePumpModel() {}
}
