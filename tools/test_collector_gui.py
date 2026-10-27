#!/usr/bin/env python3
"""Testa a grade, o encaixe conservador dos textos e a coincidência dos slots do Coletor."""
import json
import unittest

from PIL import Image

import gen_collector_gui as gui


# Avanços do bitmap vanilla; caracteres desconhecidos usam 6 px, o pior caso da fonte padrão.
NARROW = {
    " ": 4, "!": 2, "'": 3, "(": 4, ")": 4, ",": 2, ".": 2, ":": 2,
    ";": 2, "I": 4, "[": 4, "]": 4, "i": 2, "l": 3, "t": 4, "%": 6,
}


def font_width(text):
    return sum(NARROW.get(char, 6) for char in text)


class CollectorGuiTest(unittest.TestCase):
    def test_boxes_are_separate_and_inside_panel(self):
        boxes = (gui.HEADER, gui.ENERGY, gui.CORE, gui.STOCK, gui.STATUS, gui.INVENTORY)
        for x0, y0, x1, y1 in boxes:
            self.assertGreaterEqual(x0, 3)
            self.assertGreaterEqual(y0, 3)
            self.assertLess(x1, gui.W - 3)
            self.assertLess(y1, gui.H - 2)
            self.assertGreaterEqual(x1 - x0 + 1, 6)
            self.assertGreaterEqual(y1 - y0 + 1, 6)
        for index, first in enumerate(boxes):
            ax0, ay0, ax1, ay1 = first
            for bx0, by0, bx1, by1 in boxes[index + 1:]:
                overlaps = ax0 <= bx1 and bx0 <= ax1 and ay0 <= by1 and by0 <= ay1
                self.assertFalse(overlaps, f"caixas sobrepostas: {first} e {(bx0, by0, bx1, by1)}")

    def test_menu_is_the_slot_source_of_truth(self):
        self.assertEqual(174, gui.java_constant("OUTPUT_SLOT_X"))
        self.assertEqual(50, gui.java_constant("OUTPUT_SLOT_Y"))
        self.assertEqual(29, gui.java_constant("PLAYER_INVENTORY_X"))
        self.assertEqual(145, gui.java_constant("PLAYER_INVENTORY_Y"))
        self.assertLessEqual(29 + 8 * 18 + 16, gui.INVENTORY[2] - 3)
        self.assertLessEqual(145 + 58 + 16, gui.INVENTORY[3] - 1)

    def test_visible_text_fits_both_languages(self):
        limits = {
            "container.aquamarine.neutronium_collector": 198,
            "gui.aquamarine.collector.energy": 48,
            "gui.aquamarine.collector.progress": 64,
            "gui.aquamarine.collector.stock": 56,
        }
        status_limit = 179
        for locale in ("pt_br", "en_us"):
            path = gui.ROOT / f"src/main/resources/assets/aquamarine/lang/{locale}.json"
            language = json.loads(path.read_text(encoding="utf-8"))
            for key, limit in limits.items():
                self.assertLessEqual(font_width(language[key]), limit, f"{locale}: {key}")
            for state in ("running", "no_energy", "deep_ocean", "water", "depth", "full"):
                for line in ("title", "detail"):
                    key = f"gui.aquamarine.collector.status.{line}.{state}"
                    value = language[key].replace("%s", "999")
                    self.assertLessEqual(font_width(value), status_limit, f"{locale}: {key}")
            inventory = "Inventário" if locale == "pt_br" else "Inventory"
            self.assertLessEqual(font_width(inventory), 69, f"{locale}: inventário")
        for value, limit in (("999kE", 32), ("100%", 32), ("59:59", 35), ("16 / 16", 54)):
            self.assertLessEqual(font_width(value), limit, value)
        self.assertEqual("5:00", f"{300 // 60}:{300 % 60:02d}")

    def test_generated_texture_contains_eight_core_frames(self):
        image = Image.open(gui.OUT).convert("RGBA")
        self.assertEqual((gui.SHEET, gui.SHEET), image.size)
        for frame in range(8):
            box = (frame * gui.CORE_FRAME_SIZE, gui.CORE_FRAMES_Y,
                   (frame + 1) * gui.CORE_FRAME_SIZE, gui.CORE_FRAMES_Y + gui.CORE_FRAME_SIZE)
            self.assertIsNotNone(image.crop(box).getbbox(), f"quadro vazio: {frame}")


if __name__ == "__main__":
    unittest.main()
