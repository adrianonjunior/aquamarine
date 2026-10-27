"""Recursos da onda 4 (0.6.1, 7E e 7F): blocos do Compressor de Ar e do Pulmão de Ferro, receitas com a Pérola de Ostra
como núcleo, loot, nomes e dicas em pt/en (as do gerador explicam o modo fluido e a regulação). Os modelos e texturas vêm
de tools/models/machines/air_compressor e iron_lung (machine_kit)."""
from pathlib import Path
import json
import gen_phase4_building as b

ROOT = Path(__file__).resolve().parents[1]
A = ROOT / 'src/main/resources/assets/aquamarine'
D = ROOT / 'src/main/resources/data/aquamarine'


def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + '\n', encoding='utf-8', newline='\n')


def main():
    names = {'air_compressor': ('Compressor de Ar', 'Air Compressor'), 'iron_lung': ('Pulmão de Ferro', 'Iron Lung')}
    lang = {}
    for name, pair in names.items():
        lang['block.aquamarine.' + name] = pair
        write(A / f'models/block/{name}.json', {'textures': {'particle': f'aquamarine:block/machine/{name}'}})
        variants = {f'facing={f}': {'model': f'aquamarine:block/{name}', 'y': angle}
                    for f, angle in [('north', 0), ('east', 90), ('south', 180), ('west', 270)]}
        write(A / f'blockstates/{name}.json', {'variants': variants})
        write(A / f'items/{name}.json', {'model': {'type': 'minecraft:model', 'model': f'aquamarine:block/{name}_item'}})
        write(D / f'loot_table/blocks/{name}.json', b.self_drop(name))
    brass, pearl, tube = 'aquamarine:brass_ingot', 'aquamarine:oyster_pearl', 'aquamarine:air_tube'
    recipes = {
        'air_compressor': b.shaped('air_compressor', ['BTB', 'RPR', 'BCB'],
                                   {'B': brass, 'T': tube, 'R': 'minecraft:piston', 'P': pearl, 'C': 'minecraft:copper_block'}),
        'iron_lung': b.shaped('iron_lung', ['ITI', 'LPL', 'IBI'],
                              {'I': 'minecraft:iron_ingot', 'T': tube, 'L': 'minecraft:leather', 'P': pearl, 'B': 'minecraft:iron_block'}),
    }
    for name, recipe in recipes.items():
        write(D / f'recipe/{name}.json', recipe)
    lang.update({
        'tooltip.aquamarine.air_compressor': ('8 E/t: um balde de Ar Comprimido a cada 10 s, guarda 8.',
                                              '8 E/t: one bucket of Compressed Air every 10 s, holds 8.'),
        'tooltip.aquamarine.air_compressor.2': ('Empurra o ar pelos lados ligados ao Tubo de Ar (Chave: escolhe os lados).',
                                                'Pushes air into connected Air Tubes (Wrench: choose sides).'),
        'tooltip.aquamarine.iron_lung': ('Guarda 256 baldes de ar. Recebe por todos os lados e solta por trás, 1 balde/s.',
                                         'Holds 256 buckets of air. Takes from every side, releases from the back, 1 bucket/s.'),
        'tooltip.aquamarine.iron_lung.2': ('Gerador que recebe ar pelo Tubo rende 25% a mais. Cheio, só abre a válvula.',
                                           'Generators fed air by Tube make 25% more. When full it only vents.'),
        'tooltip.aquamarine.vent_generator.2': ('Ou, em qualquer lugar, com água e lava pelo Cano (fluido).',
                                                'Or anywhere, with water and lava through Pipes (fluid mode).'),
        'tooltip.aquamarine.vent_generator.3': ('Com Ar Comprimido pelo Tubo de Ar: +25%.', 'With Compressed Air through an Air Tube: +25%.'),
        'tooltip.aquamarine.current_turbine.2': ('Com Ar Comprimido pelo Tubo de Ar: +25%.', 'With Compressed Air through an Air Tube: +25%.'),
        'tooltip.aquamarine.wave_buoy.2': ('Com Ar Comprimido pelo Tubo de Ar: +25%.', 'With Compressed Air through an Air Tube: +25%.'),
        'message.aquamarine.pneumatic.compressor': ('Ar: %s / %s baldes · %s E', 'Air: %s / %s buckets · %s E'),
        'message.aquamarine.pneumatic.lung': ('Ar: %s / %s baldes', 'Air: %s / %s buckets'),
        'message.aquamarine.energy.generator.tanks': ('%s · água %s · lava %s · ar %s baldes',
                                                      '%s · water %s · lava %s · air %s buckets'),
    })
    b.merge_lang(lang)
    b.merge_tag('minecraft/tags/block/mineable/pickaxe.json', ['aquamarine:' + n for n in names])
    print('Onda 4: 2 blocos, 2 receitas, pt/en e loot gerados')


if __name__ == '__main__':
    main()
