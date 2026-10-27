package com.aquamarine.client.machine;

import com.aquamarine.Aquamarine;
import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

/**
 * Tanque d'Água: Tanque de vidro octogonal empilhável com aro de latão, escada, escala de 64 baldes e tampa selada. O líquido vem do conteúdo real do bloco.
 * GERADO por tools/models/machines/water_tank/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class WaterTankModel {
	public static final Identifier TEXTURE = Aquamarine.id("textures/block/machine/water_tank.png");
	public static final Identifier GLOW = Aquamarine.id("textures/block/machine/water_tank_glow.png");
	/** Partes de vidro: vão só na passada translúcida. */
	public static final Set<String> GLASS = Set.of("corner_0_glass", "corner_1_glass", "corner_2_glass", "corner_3_glass", "panel_0_glass", "panel_1_glass", "panel_2_glass", "panel_3_glass");

	public static ModelPart create() {
		MachineParts p = new MachineParts(128, 128);
		p.part("root", null, 0F, 24F, 0F, 0F, 0F, 0F);
		p.part("works", "root", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("chamber", "works", 0F, -8F, 0F, 0F, 0F, 0F);
		p.part("panel_0", "chamber", 0F, 0F, -6.25F, 0F, 0F, 0F);
		p.part("panel_0_glass", "panel_0", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("panel_1", "chamber", 6.25F, 0F, 0F, 0F, 1.5708F, 0F);
		p.part("panel_1_glass", "panel_1", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("panel_2", "chamber", 0F, 0F, 6.25F, 0F, 3.1416F, 0F);
		p.part("panel_2_glass", "panel_2", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("panel_3", "chamber", -6.25F, 0F, 0F, 0F, 4.7124F, 0F);
		p.part("panel_3_glass", "panel_3", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("corner_0", "chamber", 4.5F, 0F, -4.5F, 0F, -0.7854F, 0F);
		p.part("corner_0_glass", "corner_0", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("corner_1", "chamber", 4.5F, 0F, 4.5F, 0F, -2.3562F, 0F);
		p.part("corner_1_glass", "corner_1", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("corner_2", "chamber", -4.5F, 0F, 4.5F, 0F, -3.927F, 0F);
		p.part("corner_2_glass", "corner_2", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("corner_3", "chamber", -4.5F, 0F, -4.5F, 0F, -5.4978F, 0F);
		p.part("corner_3_glass", "corner_3", 0F, 0F, 0F, 0F, 0F, 0F);
		p.part("water_port", "works", 0F, -3F, -7.5F, 0F, 0F, 0F);
		p.part("water_back", "works", 0F, -3F, 7.5F, 0F, 3.1416F, 0F);
		p.part("needle", "works", 0F, -12F, -7.125F, 0F, 0F, 0F);
		p.part("valve", "works", 0F, -15.5F, 0F, 0F, 0F, 0F);
		p.box("works", -4F, -1F, -7F, 8F, 1F, 2F, new int[] {0, 95, 0, 16, 4, 1, 95, 0, 16, 4, 2, 12, 34, 4, 2, 3, 12, 34, 4, 2, 4, 72, 29, 16, 2, 5, 72, 29, 16, 2}); // rim_0_0
		p.box("works", -7F, -1F, -5F, 14F, 1F, 10F, new int[] {0, 33, 0, 28, 20, 1, 33, 0, 28, 20, 2, 51, 29, 20, 2, 3, 51, 29, 20, 2, 4, 22, 29, 28, 2, 5, 22, 29, 28, 2}); // rim_0_1
		p.box("works", -4F, -1F, 5F, 8F, 1F, 2F, new int[] {0, 112, 0, 16, 4, 1, 112, 0, 16, 4, 2, 17, 34, 4, 2, 3, 17, 34, 4, 2, 4, 89, 29, 16, 2, 5, 89, 29, 16, 2}); // rim_0_2
		p.box("works", -4F, -16F, -7F, 8F, 1F, 2F, new int[] {0, 95, 0, 16, 4, 1, 95, 0, 16, 4, 2, 12, 34, 4, 2, 3, 12, 34, 4, 2, 4, 72, 29, 16, 2, 5, 72, 29, 16, 2}); // rim_1_0
		p.box("works", -7F, -16F, -5F, 14F, 1F, 10F, new int[] {0, 33, 0, 28, 20, 1, 33, 0, 28, 20, 2, 51, 29, 20, 2, 3, 51, 29, 20, 2, 4, 22, 29, 28, 2, 5, 22, 29, 28, 2}); // rim_1_1
		p.box("works", -4F, -16F, 5F, 8F, 1F, 2F, new int[] {0, 112, 0, 16, 4, 1, 112, 0, 16, 4, 2, 17, 34, 4, 2, 3, 17, 34, 4, 2, 4, 89, 29, 16, 2, 5, 89, 29, 16, 2}); // rim_1_2
		p.box("panel_0_glass", -4F, -7F, -0.25F, 8F, 14F, 0.5F, new int[] {4, 0, 0, 16, 28, 5, 0, 0, 16, 28}); // panel_0
		p.box("panel_1_glass", -4F, -7F, -0.25F, 8F, 14F, 0.5F, new int[] {4, 0, 0, 16, 28, 5, 0, 0, 16, 28}); // panel_1
		p.box("panel_2_glass", -4F, -7F, -0.25F, 8F, 14F, 0.5F, new int[] {4, 0, 0, 16, 28, 5, 0, 0, 16, 28}); // panel_2
		p.box("panel_3_glass", -4F, -7F, -0.25F, 8F, 14F, 0.5F, new int[] {4, 0, 0, 16, 28, 5, 0, 0, 16, 28}); // panel_3
		p.box("corner_0_glass", -1.75F, -7F, -0.25F, 3.5F, 14F, 0.5F, new int[] {4, 17, 0, 7, 28, 5, 17, 0, 7, 28}); // corner_0
		p.box("corner_1_glass", -1.75F, -7F, -0.25F, 3.5F, 14F, 0.5F, new int[] {4, 17, 0, 7, 28, 5, 17, 0, 7, 28}); // corner_1
		p.box("corner_2_glass", -1.75F, -7F, -0.25F, 3.5F, 14F, 0.5F, new int[] {4, 17, 0, 7, 28, 5, 17, 0, 7, 28}); // corner_2
		p.box("corner_3_glass", -1.75F, -7F, -0.25F, 3.5F, 14F, 0.5F, new int[] {4, 17, 0, 7, 28, 5, 17, 0, 7, 28}); // corner_3
		p.box("works", -4.5F, -15F, -6.5F, 0.5F, 14F, 0.5F, new int[] {0, 98, 34, 1, 1, 1, 98, 34, 1, 1, 2, 25, 0, 1, 28, 3, 25, 0, 1, 28, 4, 25, 0, 1, 28, 5, 25, 0, 1, 28}); // brace_0
		p.box("works", 4F, -15F, -6.5F, 0.5F, 14F, 0.5F, new int[] {0, 98, 34, 1, 1, 1, 98, 34, 1, 1, 2, 25, 0, 1, 28, 3, 25, 0, 1, 28, 4, 25, 0, 1, 28, 5, 25, 0, 1, 28}); // brace_1
		p.box("works", -4.5F, -15F, 6F, 0.5F, 14F, 0.5F, new int[] {0, 98, 34, 1, 1, 1, 98, 34, 1, 1, 2, 25, 0, 1, 28, 3, 25, 0, 1, 28, 4, 25, 0, 1, 28, 5, 25, 0, 1, 28}); // brace_2
		p.box("works", 4F, -15F, 6F, 0.5F, 14F, 0.5F, new int[] {0, 98, 34, 1, 1, 1, 98, 34, 1, 1, 2, 25, 0, 1, 28, 3, 25, 0, 1, 28, 4, 25, 0, 1, 28, 5, 25, 0, 1, 28}); // brace_3
		p.box("works", 7F, -15F, -3F, 0.5F, 14F, 0.5F, new int[] {0, 98, 34, 1, 1, 1, 98, 34, 1, 1, 2, 25, 0, 1, 28, 3, 25, 0, 1, 28, 4, 25, 0, 1, 28, 5, 25, 0, 1, 28}); // ladder_rail_0
		p.box("works", 7F, -15F, 2F, 0.5F, 14F, 0.5F, new int[] {0, 98, 34, 1, 1, 1, 98, 34, 1, 1, 2, 25, 0, 1, 28, 3, 25, 0, 1, 28, 4, 25, 0, 1, 28, 5, 25, 0, 1, 28}); // ladder_rail_1
		p.box("works", 7F, -2.5F, -3F, 0.5F, 0.5F, 5.5F, new int[] {0, 62, 0, 1, 11, 1, 62, 0, 1, 11, 2, 39, 34, 11, 1, 3, 39, 34, 11, 1, 4, 104, 34, 1, 1, 5, 104, 34, 1, 1}); // rung_0
		p.box("works", 7F, -5.5F, -3F, 0.5F, 0.5F, 5.5F, new int[] {0, 62, 0, 1, 11, 1, 62, 0, 1, 11, 2, 39, 34, 11, 1, 3, 39, 34, 11, 1, 4, 104, 34, 1, 1, 5, 104, 34, 1, 1}); // rung_1
		p.box("works", 7F, -8.5F, -3F, 0.5F, 0.5F, 5.5F, new int[] {0, 62, 0, 1, 11, 1, 62, 0, 1, 11, 2, 39, 34, 11, 1, 3, 39, 34, 11, 1, 4, 104, 34, 1, 1, 5, 104, 34, 1, 1}); // rung_2
		p.box("works", 7F, -11.5F, -3F, 0.5F, 0.5F, 5.5F, new int[] {0, 62, 0, 1, 11, 1, 62, 0, 1, 11, 2, 39, 34, 11, 1, 3, 39, 34, 11, 1, 4, 104, 34, 1, 1, 5, 104, 34, 1, 1}); // rung_3
		p.box("works", 7F, -14.5F, -3F, 0.5F, 0.5F, 5.5F, new int[] {0, 62, 0, 1, 11, 1, 62, 0, 1, 11, 2, 39, 34, 11, 1, 3, 39, 34, 11, 1, 4, 104, 34, 1, 1, 5, 104, 34, 1, 1}); // rung_4
		p.box("works", -5.5F, -14F, -7F, 1.5F, 12F, 0.5F, new int[] {0, 91, 34, 3, 1, 1, 87, 34, 3, 1, 2, 31, 0, 1, 24, 3, 31, 0, 1, 24, 4, 27, 0, 3, 24, 5, 27, 0, 3, 24}); // capacity_scale
		p.box("water_port", -1.5F, -1.5F, -0.5F, 3F, 3F, 1F, new int[] {0, 0, 34, 6, 2, 1, 120, 29, 6, 2, 2, 92, 0, 2, 6, 3, 92, 0, 2, 6, 4, 82, 0, 6, 6, 5, 82, 0, 6, 6}); // water_port_ring
		p.box("water_port", -1F, -1F, 0.5F, 2F, 2F, 1F, new int[] {0, 27, 34, 4, 2, 1, 27, 34, 4, 2, 2, 13, 29, 2, 4, 3, 13, 29, 2, 4, 4, 5, 29, 4, 4, 5, 5, 29, 4, 4}); // water_port_neck
		p.box("water_back", -1.5F, -1.5F, -0.5F, 3F, 3F, 1F, new int[] {0, 113, 29, 6, 2, 1, 106, 29, 6, 2, 2, 89, 0, 2, 6, 3, 89, 0, 2, 6, 4, 75, 0, 6, 6, 5, 75, 0, 6, 6}); // water_back_ring
		p.box("water_back", -1F, -1F, 0.5F, 2F, 2F, 1F, new int[] {0, 22, 34, 4, 2, 1, 22, 34, 4, 2, 2, 10, 29, 2, 4, 3, 10, 29, 2, 4, 4, 0, 29, 4, 4, 5, 0, 29, 4, 4}); // water_back_neck
		p.box("works", -2F, -14F, -7F, 4F, 4F, 0.5F, new int[] {0, 78, 34, 8, 1, 1, 69, 34, 8, 1, 2, 73, 0, 1, 8, 3, 73, 0, 1, 8, 4, 64, 0, 8, 8, 5, 64, 0, 8, 8}); // needle_dial
		p.box("needle", -0.25F, -1.25F, -0.375F, 0.5F, 1.5F, 0.5F, new int[] {0, 102, 34, 1, 1, 1, 102, 34, 1, 1, 2, 20, 29, 1, 3, 3, 20, 29, 1, 3, 4, 18, 29, 1, 3, 5, 18, 29, 1, 3}); // needle_pointer
		p.box("works", -2F, -16F, -1F, 0.5F, 1F, 2F, new int[] {0, 16, 29, 1, 4, 1, 16, 29, 1, 4, 2, 7, 34, 4, 2, 3, 7, 34, 4, 2, 4, 37, 34, 1, 2, 5, 37, 34, 1, 2}); // lid_handle_l
		p.box("works", 1.5F, -16F, -1F, 0.5F, 1F, 2F, new int[] {0, 16, 29, 1, 4, 1, 16, 29, 1, 4, 2, 7, 34, 4, 2, 3, 7, 34, 4, 2, 4, 37, 34, 1, 2, 5, 37, 34, 1, 2}); // lid_handle_r
		p.box("valve", -2F, -0.5F, -1F, 4F, 0.5F, 0.5F, new int[] {0, 60, 34, 8, 1, 1, 60, 34, 8, 1, 2, 100, 34, 1, 1, 3, 100, 34, 1, 1, 4, 51, 34, 8, 1, 5, 51, 34, 8, 1}); // lid_handle
		p.box("works", 3F, -14F, -7F, 1F, 1F, 0.5F, new int[] {0, 95, 34, 2, 1, 1, 95, 34, 2, 1, 2, 35, 34, 1, 2, 3, 35, 34, 1, 2, 4, 32, 34, 2, 2, 5, 32, 34, 2, 2}); // level_lamp
		return p.bake();
	}

	private WaterTankModel() {}
}
