"""Verifica contratos que podem se perder na conversão Java → navegador."""
import json
import math
import re
import sys
import unittest
import tempfile
from unittest.mock import patch
from pathlib import Path

BASE = Path(__file__).resolve().parents[1]
sys.path.insert(0,str(BASE))
from catalog import models, MODELS


class CatalogTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.models = models()
        cls.kraken = cls.models[0]

    def test_complete_java_inventory(self):
        expected = {"kraken":16,"tentacle":5,"young_kraken":6,"kraken_egg":2,"moray":7,"whale":5,"sperm_whale":4,"crab":8,"oyster":4}
        self.assertEqual({m["id"]:len(m["clips"]) for m in self.models},expected)

    def test_java_timing_and_proposal_do_not_change_contract(self):
        for name, old in self.kraken["baseline"].items():
            new = self.kraken["clips"][name]
            self.assertEqual((old["length"],old["loop"]),(new["length"],new["loop"]),name)
            for t in new["tracks"]:
                keys = t["keys"]
                times = [k[0] for k in keys]
                self.assertEqual(times,sorted(set(times)),(name,t["bone"]))
                self.assertEqual(times[0],0)
                self.assertEqual(times[-1],new["length"])
                self.assertTrue(all(math.isfinite(v) for k in keys for v in k[1]))
                if new["loop"]:
                    for a,b in zip(keys[0][1],keys[-1][1]):
                        self.assertAlmostEqual(a,b,places=5,msg=(name,t["bone"]))

    def test_v2_integrated_and_all_bones_resolve(self):
        # A v2 entrou no jogo em 25/10/2026: o rig de produção já tem as partes dela, sem proposta separada.
        names={b["name"] for b in self.kraken["bones"]}
        self.assertTrue({"jaw_hinge","mantle_fin_left","mantle_fin_right"}<=names)
        self.assertTrue(all(f"arm_{i}_segment_6" in names for i in range(8)))
        self.assertIsNone(self.kraken["proposal"])
        for m in self.models:
            for rig in [m]+([m["proposal"]] if m["proposal"] else []):
                names={"root"}
                for b in rig["bones"]:
                    self.assertIn(b["parent"],names)
                    self.assertNotIn(b["name"],names)
                    names.add(b["name"])
                for c in rig["cubes"]:
                    self.assertIn(c["bone"],names)
                    self.assertTrue(all(v>0 for v in c["size"]))
        generator=(MODELS/"kraken/build_model.py").read_text(encoding="utf-8")
        self.assertNotIn("kraken_preview_v2",generator)

    def test_spawnlings_match_generated_java(self):
        # O gerador grava o Java e o rig.json juntos: caixas e UV do site são as do jogo.
        for kind,cls in (("tentacle","Tentacle"),("young_kraken","YoungKraken"),("kraken_egg","KrakenEgg")):
            m=next(m for m in self.models if m["id"]==kind)
            java=(BASE.parents[1]/"src/client/java/com/aquamarine/client/entity"/kind/f"{cls}Model.java").read_text(encoding="utf-8")
            offsets=sorted([int(u),int(v)] for u,v in re.findall(r"texOffs\((\d+), (\d+)\)",java))
            self.assertEqual(offsets,sorted(c["uv"] for c in m["cubes"]),kind)
        egg=next(m for m in self.models if m["id"]=="kraken_egg")
        self.assertEqual(next(b for b in egg["bones"] if b["name"]=="embryo")["parent"],"anchor")
        self.assertTrue(egg["translucent"])

    def test_registry_preserves_originals_and_proposal(self):
        registry=json.loads((BASE/"versions/registry.json").read_text(encoding="utf-8"))
        self.assertEqual(len(registry),32)
        # 0.6.1 (7F): Compressor e Pulmão com o clipe de nível do Pulmão.
        lung=json.loads((BASE/"versions/iron_lung-v1.json").read_text(encoding="utf-8"))
        self.assertTrue({"idle","run","full","level"}<=set(lung["clips"]))
        self.assertTrue((BASE/"versions/air_compressor-v1.json").exists())
        # 0.6.1 (7B): a primeira máquina animada, com a camada de brilho e o gabarito de UV.
        vent=json.loads((BASE/"versions/vent_generator-v1.json").read_text(encoding="utf-8"))
        self.assertTrue(vent["glow"].startswith("data:image/png") and vent["uv"].startswith("data:image/png"))
        self.assertTrue({"idle","start","run","stop","overload"}<=set(vent["clips"]))
        # Fase 6 (6A1): três candidatos do Kraken só no site, com camada de brilho e gabarito de UV próprios.
        for v in ("v3a","v3b","v3c"):
            cand=json.loads((BASE/"versions"/f"kraken-{v}.json").read_text(encoding="utf-8"))
            self.assertTrue(cand["glow"].startswith("data:image/png") and cand["uv"].startswith("data:image/png"))
            self.assertTrue({"idle","swim","slam","roar","grab","death"}<=set(cand["clips"]))
            # Fase 6: as três foram para o jogo com os 16 clipes do contrato (tools/models/kraken_v3/build_game.py).
            game=json.loads((BASE/"versions"/f"kraken-{v}-jogo.json").read_text(encoding="utf-8"))
            self.assertEqual(len(game["clips"]),16)
            self.assertEqual(set(game["clips"]),{n for n in self.kraken["clips"]})
        latest={r["id"]:r["file"] for r in registry}  # última versão registrada de cada modelo
        for m in self.models:
            old=json.loads((BASE/"versions"/f"{m['id']}-v1.json").read_text(encoding="utf-8"))
            if m["id"]=="kraken":
                # A v1 segue preservada no registro; o rig atual é a v2 integrada (contém todos os ossos da v1).
                self.assertEqual(old["clips"],m["baseline"])
                self.assertTrue({b["name"] for b in old["bones"]}<{b["name"] for b in m["bones"]})
                continue
            current=json.loads((BASE/"versions"/latest[m["id"]]).read_text(encoding="utf-8"))
            self.assertEqual(current["bones"],m["bones"])
            self.assertEqual(current["cubes"],m["cubes"])
            self.assertEqual(current["clips"],m["clips"])
        new=json.loads((BASE/"versions/kraken-v2.json").read_text(encoding="utf-8"))
        self.assertEqual({b["name"] for b in new["bones"]},{b["name"] for b in self.kraken["bones"]})
        self.assertEqual({n:(c["length"],c["loop"]) for n,c in new["clips"].items()},
                         {n:(c["length"],c["loop"]) for n,c in self.kraken["clips"].items()})

    def test_machines_tab(self):
        # 0.6.1 (7B): aba "Máquinas" com o Gerador de Fenda; rig, UV por face e clipes iguais aos do Java gerado.
        from catalog import machines
        vent = {m["id"]: m for m in machines()}["vent_generator"]
        self.assertEqual(vent["kind"], "machine")
        self.assertTrue({"idle", "start", "run", "stop", "overload"} <= set(vent["clips"]))
        self.assertEqual(set(vent["states"]), {"idle", "run", "overload"})
        width, height = vent["resolution"]
        names = {"root"} | {b["name"] for b in vent["bones"]}
        dims = {"front": (0, 1), "back": (0, 1), "left": (2, 1), "right": (2, 1), "top": (0, 2), "bottom": (0, 2)}
        for c in vent["cubes"]:
            self.assertIn(c["bone"], names)
            self.assertEqual(c["glass"], c["bone"].endswith("_glass"), c["name"])
            for face, (u1, v1, u2, v2) in c["faces"].items():
                self.assertTrue(0 <= u1 < u2 <= width and 0 <= v1 < v2 <= height, (c["name"], face))
                a, b = dims[face]
                self.assertEqual((u2 - u1, v2 - v1), (2 * c["size"][a], 2 * c["size"][b]), (c["name"], face))  # 32 px por face
        for name, clip in vent["clips"].items():
            for track in clip["tracks"]:
                times = [k[0] for k in track["keys"]]
                self.assertEqual(times, sorted(set(times)), (name, track["bone"]))
                self.assertEqual((times[0], times[-1]), (0, clip["length"]), (name, track["bone"]))
                if clip["loop"]:
                    for x, y in zip(track["keys"][0][1], track["keys"][-1][1]):
                        self.assertAlmostEqual(x, y, places=5, msg=(name, track["bone"]))
        java = BASE.parents[1] / "src/client/java/com/aquamarine/client/machine"
        self.assertEqual((java / "VentGeneratorModel.java").read_text(encoding="utf-8").count("p.box("), len(vent["cubes"]))
        anims = (java / "VentGeneratorAnimations.java").read_text(encoding="utf-8")
        self.assertTrue(all(f'clips.put("{n}"' in anims for n in vent["clips"]))

        # Rework do Coletor: primeiro rig multibloco do Atlas, folha maior e leitura viva de progresso/estoque.
        collector = {m["id"]: m for m in machines()}["neutronium_collector"]
        self.assertEqual(collector["resolution"], [512, 512])
        self.assertEqual(len(collector["cubes"]), 200)
        self.assertTrue({"idle", "start", "run", "stop", "full", "collect", "progress", "stock", "invalid"}
                        <= set(collector["clips"]))
        self.assertEqual(set(collector["states"]), {"idle", "run", "full"})
        self.assertTrue(any(min(c["position"]) < -16 for c in collector["cubes"]))
        self.assertTrue((BASE / "versions/neutronium_collector-v1.json").exists())
        self.assertTrue((BASE / "versions/neutronium_collector-v2.json").exists())
        saved = json.loads((BASE / "versions/neutronium_collector-v3.json").read_text(encoding="utf-8"))
        self.assertEqual(saved["cubes"], collector["cubes"])
        self.assertTrue(saved["glow"].startswith("data:image/png") and saved["uv"].startswith("data:image/png"))

    def test_adaptive_audio_manifest_without_single_file(self):
        import build
        with tempfile.TemporaryDirectory() as tmp:
            root=Path(tmp);(root/"tools/audio").mkdir(parents=True)
            (root/"mix.ogg").write_bytes(b"OggS")
            (root/"stem.ogg").write_bytes(b"OggS")
            manifest={"theme":{"name":"theme","kind":"music","layers":{"base":"stem.ogg"},"audition":{"full":"mix.ogg"}},
                      "theme_base":{"name":"theme_base","kind":"stem","file":"stem.ogg","loop":True,"loop_overlap_s":0}}
            (root/"tools/audio/manifest.json").write_text(json.dumps(manifest),encoding="utf-8")
            with patch.object(build,"ROOT",root),patch.object(build,"OUT",root/"out"):
                catalog=build.audio_catalog()
            self.assertTrue(all(a["available"] for a in catalog))
            self.assertEqual(catalog[0]["mixes"][0]["id"],"full")
            self.assertIn("2 voltas",catalog[0]["playback_note"])
            self.assertIn("circular",catalog[1]["playback_note"])


if __name__ == "__main__":
    unittest.main()
