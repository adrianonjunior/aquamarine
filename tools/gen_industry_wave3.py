"""Recursos da onda 3. Arte em pixel art determinística; receitas, loot e nomes nos dois idiomas."""
from pathlib import Path
import json, math
from PIL import Image, ImageDraw
import gen_phase4_building as b
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/aquamarine'
D=ROOT/'src/main/resources/data/aquamarine'
def write(path,data):
    path.parent.mkdir(parents=True,exist_ok=True); path.write_text(json.dumps(data,indent=2,ensure_ascii=False)+'\n',encoding='utf-8')
def cube(a,z,tex='all'):
    return {'from':a,'to':z,'faces':{s:{'texture':'#'+tex,'uv':[0,0,16,16]} for s in ['up','down','north','south','east','west']}}
def icon(name):
    im=Image.new('RGBA',(16,16)); d=ImageDraw.Draw(im)
    if name=='oyster_pearl':
        for radius,color in [(6,'#395564'),(5,'#699baf'),(4,'#a9cdd0'),(3,'#e4eee0')]:
            d.ellipse((8-radius,8-radius,8+radius,8+radius),fill=color)
        d.rectangle((5,4,7,5),fill='#fffde0'); d.point((4,6),fill='#fffde0'); d.line((8,12,11,10),fill='#bfd2d8')
    else:
        d.polygon([(2,5),(4,3),(8,2),(12,3),(14,5),(13,10),(9,14),(7,14),(3,10)],fill='#503b3b')
        d.polygon([(3,5),(5,4),(8,3),(11,4),(13,5),(12,9),(8,13),(4,9)],fill='#dcb987')
        for x in [4,6,8,10,12]: d.line((8,12,x,5),fill='#9c7658'); d.point((x,4),fill='#fff0c5')
        d.line((5,10,11,10),fill='#eed5a9')
    return im
def main():
    names={'tide_pump':('Bomba de Maré','Tide Pump'),'water_tank':("Tanque d’Água",'Water Tank'),
      'cultivation_jar':('Garrafão de Cultivo','Cultivation Jar'),'cultivation_rope':('Corda de Cultivo','Cultivation Rope'),
      'cultivation_buoy':('Boia de Cultivo','Cultivation Buoy')}
    tips={'tide_pump':('160 E por balde. Capta fonte de água encostada; sai pelos lados ligados.','160 E per bucket. Draws from adjacent water sources; outputs through enabled sides.'),
      'water_tank':('64 baldes por bloco. Empilhe para ampliar. Balde: enche ou esvazia; Chave: escolhe o lado de saída.','64 buckets per block. Stack to expand. Bucket: fill or drain; Wrench: choose an output side.'),
      'cultivation_jar':('Ligue à Boia por Corda, mergulhado em água limpa. Uma pérola a cada 8 min; bônus até 50%.','Connect to a Buoy with Rope in clean water. One pearl every 8 min; bonus up to 50%.'),
      'cultivation_rope':('Linha reta entre Garrafões e Boia. Coloque todos virados para o mesmo lado.','Straight line between Jars and Buoy. Place all facing the same way.'),
      'cultivation_buoy':('Na ponta da linha. Clique para conferir a produção e recolher as pérolas.','At the end of the line. Click to check production and collect pearls.')}
    lang={}
    for name,pair in names.items():
        lang['block.aquamarine.'+name]=pair; lang['tooltip.aquamarine.'+name]=tips[name]
        animated=True
        if animated:
            write(A/f'models/block/{name}.json',{'textures':{'particle':f'aquamarine:block/machine/{name}'}})
        variants={f'facing={f}':{'model':f'aquamarine:block/{name}','y':angle} for f,angle in [('north',0),('east',90),('south',180),('west',270)]}
        write(A/f'blockstates/{name}.json',{'variants':variants})
        write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':f'aquamarine:block/{name}'+('_item' if animated else '')}})
        write(D/f'loot_table/blocks/{name}.json',b.self_drop(name))
    for name,pair in {'oyster_pearl':('Pérola de Ostra','Oyster Pearl'),'crab_shell':('Concha de Caranguejo','Crab Shell'),'shell_shield':('Escudo de Concha','Shell Shield')}.items():
        im=icon('crab_shell' if name=='shell_shield' else name); im.save(A/f'textures/item/{name}.png')
        write(A/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':f'aquamarine:item/{name}'}})
        write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':f'aquamarine:item/{name}'}})
        lang['item.aquamarine.'+name]=pair
    # Escudo com formato vanilla, pintura de concha e poses de empunhar/bloquear.
    shield={'textures':{'all':'aquamarine:item/shell_shield','particle':'aquamarine:item/shell_shield'},
        'elements':[cube([2,-3,7],[14,17,9]),cube([6,3,9],[10,11,11])],
        'display':{'gui':{'rotation':[15,-25,0],'scale':[.65,.65,.65]},'ground':{'translation':[0,2,0],'scale':[.3,.3,.3]},
        'thirdperson_righthand':{'rotation':[0,90,0],'translation':[0,3,2],'scale':[1,1,1]},
        'thirdperson_lefthand':{'rotation':[0,-90,0],'translation':[0,3,2],'scale':[1,1,1]},
        'firstperson_righthand':{'rotation':[0,180,5],'translation':[-5,0,-5],'scale':[1,1,1]},
        'firstperson_lefthand':{'rotation':[0,180,-5],'translation':[-5,0,-5],'scale':[1,1,1]}}}
    write(A/'models/item/shell_shield.json',shield)
    blocking=json.loads(json.dumps(shield)); blocking['display']['firstperson_righthand']={'rotation':[0,180,-5],'translation':[1,2,-3]}; blocking['display']['firstperson_lefthand']={'rotation':[0,180,5],'translation':[1,2,-3]}
    write(A/'models/item/shell_shield_blocking.json',blocking)
    write(A/'items/shell_shield.json',{'model':{'type':'minecraft:condition','property':'minecraft:using_item','on_true':{'type':'minecraft:model','model':'aquamarine:item/shell_shield_blocking'},'on_false':{'type':'minecraft:model','model':'aquamarine:item/shell_shield'}}})
    brass='aquamarine:brass_ingot'; pearl='aquamarine:oyster_pearl'
    recipes={
      'tide_pump':b.shaped('tide_pump',[' B ','CPC','BRB'],{'B':brass,'P':pearl,'C':'aquamarine:fluid_pipe','R':'minecraft:piston'}),
      'water_tank':b.shaped('water_tank',['BBB','GPG','BBB'],{'B':brass,'P':pearl,'G':'minecraft:glass'}),
      'cultivation_jar':b.shaped('cultivation_jar',[' B ','GPG','GGG'],{'B':brass,'P':pearl,'G':'minecraft:glass'}),
      'cultivation_rope':b.shaped('cultivation_rope',['SSS'],{'S':'minecraft:string'},8),
      'cultivation_buoy':b.shaped('cultivation_buoy',[' T ','BCB',' R '],{'T':'minecraft:torch','B':brass,'C':'minecraft:copper_ingot','R':'aquamarine:cultivation_rope'}),
      'shell_shield':b.shapeless('shell_shield',['aquamarine:crab_shell',pearl,'minecraft:shield']),
      'aurium_refining':b.shaped('aurium_ingot',['RP'],{'R':'aquamarine:raw_aurium','P':pearl},2)}
    recipes['aurium_refining']['type']='aquamarine:abyssal_forging'
    for name,recipe in recipes.items(): write(D/f'recipe/{name}.json',recipe)
    write(D/'loot_table/entities/crab.json',{'type':'minecraft:entity','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':'aquamarine:crab_shell'}]}]})
    write(D/'loot_table/entities/oyster.json',{'type':'minecraft:entity','pools':[]})
    lang.update({
      'entity.aquamarine.oyster':('Ostra','Oyster'),'entity.aquamarine.crab':('Caranguejo','Crab'),
      'tooltip.aquamarine.oyster_pearl':('Núcleo de máquinas. Com Aurium Bruto na Forja: 2 lingotes.','Machine core. With Raw Aurium in the Forge: 2 ingots.'),
      'tooltip.aquamarine.crab_shell':('Concha resistente encontrada com caranguejos de praia.','Sturdy shell carried by beach crabs.'),
      'tooltip.aquamarine.shell_shield':('672 de durabilidade; recarga 40% menor e metade do empurrão de baleia ao bloquear.','672 durability; 40% shorter disable cooldown and half whale push while blocking.'),
      'message.aquamarine.hydraulic.contents':('%s / %s baldes · %s E','%s / %s buckets · %s E'),
      'message.aquamarine.cultivation':('%s garrafões · %s produzindo · %s pérolas · %s%% · %s','%s jars · %s growing · %s pearls · %s%% · %s'),
      'message.aquamarine.cultivation.connected':('linha ligada','connected line'),
      'message.aquamarine.cultivation.disconnected':('falta ligar à Boia','connect to a Buoy'),
      'jei.aquamarine.obtain.crab.1':('Caranguejos raros nas praias arenosas carregam a Concha.','Rare crabs on sandy beaches carry the Shell.'),
      'jei.aquamarine.obtain.crab.2':('Aceitam peixe cru; fogem para a areia quando ameaçados.','Accept raw fish; burrow in sand when threatened.'),
      'jei.aquamarine.obtain.oyster.1':('Ostras vivem no fundo de oceanos mornos, até 20 blocos de profundidade. 15% carregam uma pérola.','Oysters live on warm ocean floors, up to 20 blocks deep. 15% carry a pearl.'),
      'jei.aquamarine.obtain.oyster.2':('Garrafões ligados por Corda à Boia, em água limpa: 8 min por pérola, até +50% por vizinhos.','Jars linked by Rope to a Buoy in clean water: 8 min per pearl, up to +50% from neighbors.')})
    b.merge_lang(lang)
    b.merge_tag('minecraft/tags/block/mineable/pickaxe.json',['aquamarine:'+n for n in names])
    print('Onda 3: recursos, 7 receitas, pt/en e loot gerados')
if __name__=='__main__': main()
