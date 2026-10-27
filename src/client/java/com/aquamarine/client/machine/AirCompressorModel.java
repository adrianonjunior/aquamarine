package com.aquamarine.client.machine;

import com.aquamarine.Aquamarine;
import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

/**
 * Compressor de Ar: Motor de aço com aletas, volante de cobre, cilindro de latão com cruzeta e reservatório de ar com válvula de alívio. Latão, vidro e vapor; 32 px por face.
 * GERADO por tools/models/machines/air_compressor/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class AirCompressorModel {
	public static final Identifier TEXTURE = Aquamarine.id("textures/block/machine/air_compressor.png");
	public static final Identifier GLOW = Aquamarine.id("textures/block/machine/air_compressor_glow.png");
	/** Partes de vidro: vão só na passada translúcida. */
	public static final Set<String> GLASS = Set.of();

	public static ModelPart create() {
		MachineParts p = new MachineParts(128, 128);
		p.part("root", null, 0F, 24F, 0F, 0F, 0F, 0F);
		p.part("works", "root", 0F, -3F, 0F, 0F, 0F, 0F);
		p.part("flywheel", "works", -3.5F, -5F, -5F, 0F, 0F, 0F);
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
		p.part("piston", "works", 4F, -7F, -2F, 0F, 0F, 0F);
		p.part("rod_link", "works", -3.5F, -5F, -6F, 0F, 0F, 0F);
		p.part("relief_cap", "works", 5.5F, -12.5F, 4F, 0F, 0F, 0F);
		p.part("needle", "works", 4F, -3F, -5.125F, 0F, 0F, 0F);
		p.part("energy_in", "works", -6.5F, -5F, 3F, 0F, -1.5708F, 0F);
		p.part("air_out", "works", 3.5F, -9.25F, 7.5F, 0F, 3.1416F, 0F);
		p.box("root", -7F, -2F, -7F, 14F, 1F, 14F, new int[] {0, 0, 0, 28, 28, 1, 0, 0, 28, 28, 2, 69, 51, 28, 2, 3, 69, 51, 28, 2, 4, 40, 51, 28, 2, 5, 40, 51, 28, 2}); // deck
		p.box("root", -7F, -1F, -7F, 3F, 1F, 3F, new int[] {0, 61, 42, 6, 6, 1, 61, 42, 6, 6, 2, 46, 56, 6, 2, 3, 46, 56, 6, 2, 4, 46, 56, 6, 2, 5, 46, 56, 6, 2}); // foot_0
		p.box("root", 4F, -1F, -7F, 3F, 1F, 3F, new int[] {0, 61, 42, 6, 6, 1, 61, 42, 6, 6, 2, 46, 56, 6, 2, 3, 46, 56, 6, 2, 4, 46, 56, 6, 2, 5, 46, 56, 6, 2}); // foot_1
		p.box("root", -7F, -1F, 4F, 3F, 1F, 3F, new int[] {0, 61, 42, 6, 6, 1, 61, 42, 6, 6, 2, 46, 56, 6, 2, 3, 46, 56, 6, 2, 4, 46, 56, 6, 2, 5, 46, 56, 6, 2}); // foot_2
		p.box("root", 4F, -1F, 4F, 3F, 1F, 3F, new int[] {0, 61, 42, 6, 6, 1, 61, 42, 6, 6, 2, 46, 56, 6, 2, 3, 46, 56, 6, 2, 4, 46, 56, 6, 2, 5, 46, 56, 6, 2}); // foot_3
		p.box("works", -6F, -8F, 0F, 5F, 6F, 6F, new int[] {0, 0, 29, 10, 12, 1, 0, 29, 10, 12, 2, 88, 0, 12, 12, 3, 101, 0, 12, 12, 4, 114, 0, 10, 12, 5, 114, 0, 10, 12}); // motor
		p.box("works", -6.25F, -8.25F, 0.5F, 5.5F, 6.5F, 0.5F, new int[] {0, 38, 59, 11, 1, 1, 38, 59, 11, 1, 2, 73, 0, 1, 13, 3, 73, 0, 1, 13, 4, 61, 0, 11, 13, 5, 61, 0, 11, 13}); // motor_band_0
		p.box("works", -6.25F, -8.25F, 4.5F, 5.5F, 6.5F, 0.5F, new int[] {0, 38, 59, 11, 1, 1, 38, 59, 11, 1, 2, 73, 0, 1, 13, 3, 73, 0, 1, 13, 4, 61, 0, 11, 13, 5, 61, 0, 11, 13}); // motor_band_1
		p.box("works", -5.5F, -2F, 1F, 4F, 3F, 4F, new int[] {0, 9, 42, 8, 8, 1, 0, 42, 8, 8, 2, 38, 42, 8, 6, 3, 38, 42, 8, 6, 4, 29, 42, 8, 6, 5, 29, 42, 8, 6}); // motor_foot
		p.box("works", -5.5F, -7.5F, 6F, 4F, 5F, 0.5F, new int[] {0, 61, 59, 8, 1, 1, 61, 59, 8, 1, 2, 89, 29, 1, 10, 3, 89, 29, 1, 10, 4, 75, 29, 8, 10, 5, 75, 29, 8, 10}); // motor_cap
		p.box("works", -4F, -5.5F, -4.5F, 1F, 1F, 4.5F, new int[] {0, 118, 29, 2, 9, 1, 118, 29, 2, 9, 2, 0, 56, 9, 2, 3, 0, 56, 9, 2, 4, 90, 56, 2, 2, 5, 90, 56, 2, 2}); // axle
		p.box("flywheel_rim_0", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 59, 56, 5, 2, 1, 59, 56, 5, 2, 2, 96, 56, 2, 2, 3, 96, 56, 2, 2, 4, 53, 56, 5, 2, 5, 53, 56, 5, 2}); // flywheel_rim_0
		p.box("flywheel_rim_1", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 59, 56, 5, 2, 1, 59, 56, 5, 2, 2, 96, 56, 2, 2, 3, 96, 56, 2, 2, 4, 53, 56, 5, 2, 5, 53, 56, 5, 2}); // flywheel_rim_1
		p.box("flywheel_rim_2", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 59, 56, 5, 2, 1, 59, 56, 5, 2, 2, 96, 56, 2, 2, 3, 96, 56, 2, 2, 4, 53, 56, 5, 2, 5, 53, 56, 5, 2}); // flywheel_rim_2
		p.box("flywheel_rim_3", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 59, 56, 5, 2, 1, 59, 56, 5, 2, 2, 96, 56, 2, 2, 3, 96, 56, 2, 2, 4, 53, 56, 5, 2, 5, 53, 56, 5, 2}); // flywheel_rim_3
		p.box("flywheel_rim_4", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 59, 56, 5, 2, 1, 59, 56, 5, 2, 2, 96, 56, 2, 2, 3, 96, 56, 2, 2, 4, 53, 56, 5, 2, 5, 53, 56, 5, 2}); // flywheel_rim_4
		p.box("flywheel_rim_5", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 59, 56, 5, 2, 1, 59, 56, 5, 2, 2, 96, 56, 2, 2, 3, 96, 56, 2, 2, 4, 53, 56, 5, 2, 5, 53, 56, 5, 2}); // flywheel_rim_5
		p.box("flywheel_rim_6", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 59, 56, 5, 2, 1, 59, 56, 5, 2, 2, 96, 56, 2, 2, 3, 96, 56, 2, 2, 4, 53, 56, 5, 2, 5, 53, 56, 5, 2}); // flywheel_rim_6
		p.box("flywheel_rim_7", -1.25F, -4F, -0.5F, 2.5F, 1F, 1F, new int[] {0, 59, 56, 5, 2, 1, 59, 56, 5, 2, 2, 96, 56, 2, 2, 3, 96, 56, 2, 2, 4, 53, 56, 5, 2, 5, 53, 56, 5, 2}); // flywheel_rim_7
		p.box("flywheel_spoke_0", -0.25F, -3F, -0.25F, 0.5F, 6F, 0.5F, new int[] {0, 111, 59, 1, 1, 1, 111, 59, 1, 1, 2, 11, 29, 1, 12, 3, 11, 29, 1, 12, 4, 11, 29, 1, 12, 5, 11, 29, 1, 12}); // flywheel_spoke_0
		p.box("flywheel_spoke_1", -0.25F, -3F, -0.25F, 0.5F, 6F, 0.5F, new int[] {0, 111, 59, 1, 1, 1, 111, 59, 1, 1, 2, 11, 29, 1, 12, 3, 11, 29, 1, 12, 4, 11, 29, 1, 12, 5, 11, 29, 1, 12}); // flywheel_spoke_1
		p.box("flywheel_spoke_2", -0.25F, -3F, -0.25F, 0.5F, 6F, 0.5F, new int[] {0, 111, 59, 1, 1, 1, 111, 59, 1, 1, 2, 11, 29, 1, 12, 3, 11, 29, 1, 12, 4, 11, 29, 1, 12, 5, 11, 29, 1, 12}); // flywheel_spoke_2
		p.box("flywheel_spoke_3", -0.25F, -3F, -0.25F, 0.5F, 6F, 0.5F, new int[] {0, 111, 59, 1, 1, 1, 111, 59, 1, 1, 2, 11, 29, 1, 12, 3, 11, 29, 1, 12, 4, 11, 29, 1, 12, 5, 11, 29, 1, 12}); // flywheel_spoke_3
		p.box("flywheel", -0.5F, -0.5F, -1F, 1F, 1F, 2F, new int[] {0, 0, 51, 2, 4, 1, 0, 51, 2, 4, 2, 80, 56, 4, 2, 3, 80, 56, 4, 2, 4, 93, 56, 2, 2, 5, 93, 56, 2, 2}); // flywheel_hub
		p.box("flywheel", 1F, 1.75F, -0.75F, 1.5F, 1.5F, 0.5F, new int[] {0, 93, 59, 3, 1, 1, 93, 59, 3, 1, 2, 34, 51, 1, 3, 3, 34, 51, 1, 3, 4, 14, 51, 3, 3, 5, 14, 51, 3, 3}); // counterweight
		p.box("works", 1.5F, -6F, -4.5F, 5F, 7F, 5F, new int[] {0, 64, 29, 10, 10, 1, 53, 29, 10, 10, 2, 50, 0, 10, 14, 3, 50, 0, 10, 14, 4, 39, 0, 10, 14, 5, 39, 0, 10, 14}); // cylinder
		p.box("works", 1.25F, -0.5F, -4.75F, 5.5F, 0.5F, 5.5F, new int[] {0, 28, 29, 11, 11, 1, 28, 29, 11, 11, 2, 26, 59, 11, 1, 3, 26, 59, 11, 1, 4, 14, 59, 11, 1, 5, 14, 59, 11, 1}); // cylinder_band_0
		p.box("works", 1.25F, -5F, -4.75F, 5.5F, 0.5F, 5.5F, new int[] {0, 28, 29, 11, 11, 1, 28, 29, 11, 11, 2, 26, 59, 11, 1, 3, 26, 59, 11, 1, 4, 14, 59, 11, 1, 5, 14, 59, 11, 1}); // cylinder_band_1
		p.box("works", 1F, -7F, -5F, 6F, 1F, 6F, new int[] {0, 75, 0, 12, 12, 1, 75, 0, 12, 12, 2, 111, 51, 12, 2, 3, 111, 51, 12, 2, 4, 98, 51, 12, 2, 5, 98, 51, 12, 2}); // cylinder_head
		p.box("piston", -0.5F, -3F, -0.5F, 1F, 3F, 1F, new int[] {0, 99, 56, 2, 2, 1, 99, 56, 2, 2, 2, 90, 42, 2, 6, 3, 90, 42, 2, 6, 4, 87, 42, 2, 6, 5, 87, 42, 2, 6}); // piston_rod
		p.box("piston", -1.75F, -4F, -1F, 3.5F, 1F, 2F, new int[] {0, 93, 42, 7, 4, 1, 93, 42, 7, 4, 2, 70, 56, 4, 2, 3, 70, 56, 4, 2, 4, 10, 56, 7, 2, 5, 10, 56, 7, 2}); // crosshead
		p.box("works", 1.5F, -12F, -2.5F, 0.5F, 5F, 1F, new int[] {0, 111, 56, 1, 2, 1, 111, 56, 1, 2, 2, 84, 29, 2, 10, 3, 84, 29, 2, 10, 4, 87, 29, 1, 10, 5, 87, 29, 1, 10}); // guide_0
		p.box("works", 6F, -12F, -2.5F, 0.5F, 5F, 1F, new int[] {0, 111, 56, 1, 2, 1, 111, 56, 1, 2, 2, 84, 29, 2, 10, 3, 84, 29, 2, 10, 4, 87, 29, 1, 10, 5, 87, 29, 1, 10}); // guide_1
		p.box("works", 1.5F, -12.5F, -2.75F, 5F, 0.5F, 1.5F, new int[] {0, 3, 51, 10, 3, 1, 3, 51, 10, 3, 2, 97, 59, 3, 1, 3, 97, 59, 3, 1, 4, 50, 59, 10, 1, 5, 50, 59, 10, 1}); // guide_cap
		p.box("rod_link", 0F, -0.25F, -0.25F, 6.5F, 0.5F, 0.5F, new int[] {0, 0, 59, 13, 1, 1, 0, 59, 13, 1, 2, 109, 59, 1, 1, 3, 109, 59, 1, 1, 4, 115, 56, 13, 1, 5, 115, 56, 13, 1}); // connecting_rod
		p.box("works", 0.5F, -11.5F, 1.5F, 6F, 4.5F, 5.5F, new int[] {0, 15, 29, 12, 11, 1, 15, 29, 12, 11, 2, 106, 29, 11, 9, 3, 106, 29, 11, 9, 4, 93, 29, 12, 9, 5, 93, 29, 12, 9}); // receiver
		p.box("works", 1F, -11.75F, 1.25F, 0.5F, 5F, 6F, new int[] {0, 13, 29, 1, 12, 1, 13, 29, 1, 12, 2, 40, 29, 12, 10, 3, 40, 29, 12, 10, 4, 91, 29, 1, 10, 5, 91, 29, 1, 10}); // receiver_hoop_0
		p.box("works", 5.5F, -11.75F, 1.25F, 0.5F, 5F, 6F, new int[] {0, 13, 29, 1, 12, 1, 13, 29, 1, 12, 2, 40, 29, 12, 10, 3, 40, 29, 12, 10, 4, 91, 29, 1, 10, 5, 91, 29, 1, 10}); // receiver_hoop_1
		p.box("works", 2.5F, -7F, 2.5F, 2F, 8F, 2F, new int[] {0, 116, 42, 4, 4, 1, 111, 42, 4, 4, 2, 34, 0, 4, 16, 3, 34, 0, 4, 16, 4, 29, 0, 4, 16, 5, 29, 0, 4, 16}); // receiver_leg
		p.box("works", 3.25F, -7F, 0.5F, 1.5F, 1.5F, 1.5F, new int[] {0, 26, 51, 3, 3, 1, 26, 51, 3, 3, 2, 22, 51, 3, 3, 3, 22, 51, 3, 3, 4, 18, 51, 3, 3, 5, 18, 51, 3, 3}); // delivery
		p.box("works", 5F, -12.5F, 4F, 1F, 1F, 1F, new int[] {0, 108, 56, 2, 2, 1, 108, 56, 2, 2, 2, 105, 56, 2, 2, 3, 105, 56, 2, 2, 4, 102, 56, 2, 2, 5, 102, 56, 2, 2}); // relief_body
		p.box("relief_cap", -0.75F, -0.5F, -0.25F, 1.5F, 0.5F, 1.5F, new int[] {0, 30, 51, 3, 3, 1, 30, 51, 3, 3, 2, 105, 59, 3, 1, 3, 105, 59, 3, 1, 4, 101, 59, 3, 1, 5, 101, 59, 3, 1}); // relief_lid
		p.box("works", 6.5F, -4F, -3.5F, 1F, 3F, 3F, new int[] {0, 84, 42, 2, 6, 1, 84, 42, 2, 6, 2, 68, 42, 6, 6, 3, 68, 42, 6, 6, 4, 81, 42, 2, 6, 5, 81, 42, 2, 6}); // intake
		p.box("works", 2F, -5F, -5F, 4F, 4F, 0.5F, new int[] {0, 79, 59, 8, 1, 1, 70, 59, 8, 1, 2, 27, 42, 1, 8, 3, 27, 42, 1, 8, 4, 18, 42, 8, 8, 5, 18, 42, 8, 8}); // needle_dial
		p.box("needle", -0.25F, -1.25F, -0.375F, 0.5F, 1.5F, 0.5F, new int[] {0, 113, 59, 1, 1, 1, 113, 59, 1, 1, 2, 38, 51, 1, 3, 3, 38, 51, 1, 3, 4, 36, 51, 1, 3, 5, 36, 51, 1, 3}); // needle_pointer
		p.box("works", -1F, 0F, -7F, 2F, 1F, 0.5F, new int[] {0, 88, 59, 4, 1, 1, 88, 59, 4, 1, 2, 113, 56, 1, 2, 3, 113, 56, 1, 2, 4, 85, 56, 4, 2, 5, 85, 56, 4, 2}); // status_lamp
		p.box("energy_in", -1.5F, -1.5F, -0.5F, 3F, 3F, 1F, new int[] {0, 39, 56, 6, 2, 1, 32, 56, 6, 2, 2, 78, 42, 2, 6, 3, 78, 42, 2, 6, 4, 54, 42, 6, 6, 5, 54, 42, 6, 6}); // energy_in_ring
		p.box("energy_in", -1F, -1F, 0.5F, 2F, 2F, 1F, new int[] {0, 75, 56, 4, 2, 1, 75, 56, 4, 2, 2, 124, 42, 2, 4, 3, 124, 42, 2, 4, 4, 106, 42, 4, 4, 5, 106, 42, 4, 4}); // energy_in_neck
		p.box("air_out", -1.5F, -1.5F, -0.5F, 3F, 3F, 1F, new int[] {0, 25, 56, 6, 2, 1, 18, 56, 6, 2, 2, 75, 42, 2, 6, 3, 75, 42, 2, 6, 4, 47, 42, 6, 6, 5, 47, 42, 6, 6}); // air_out_ring
		p.box("air_out", -1F, -1F, 0.5F, 2F, 2F, 1F, new int[] {0, 65, 56, 4, 2, 1, 65, 56, 4, 2, 2, 121, 42, 2, 4, 3, 121, 42, 2, 4, 4, 101, 42, 4, 4, 5, 101, 42, 4, 4}); // air_out_neck
		return p.bake();
	}

	private AirCompressorModel() {}
}
