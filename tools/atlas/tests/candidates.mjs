// Capturas das versões do Kraken no Atlas já gerado (build/atlas), para comparar candidatos lado a lado.
// Uso: node tools/atlas/tests/candidates.mjs [v2 v3a v3b v3c]   →   build/atlas-qa/kraken-<versão>-<vista>.png
import { chromium } from '@playwright/test';
import path from 'node:path';
import { fileURLToPath,pathToFileURL } from 'node:url';
import { mkdir } from 'node:fs/promises';

const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../../..');
const url=process.env.ATLAS_URL||pathToFileURL(path.join(root,'build/atlas/index.html')).href;
const shots=path.join(root,'build/atlas-qa');await mkdir(shots,{recursive:true});
const versions=process.argv.slice(2).length?process.argv.slice(2):['v2','v3a','v3b','v3c'];
const model=process.env.ATLAS_MODEL||'kraken';
// Direção da câmera a partir do centro do modelo; a frente do bicho é -z.
const allViews={front:[0.45,0.3,-1],side:[1,0.18,0],top:[0.7,1.4,-0.8],back:[-0.8,0.55,1]};
const wantedViews=(process.env.ATLAS_VIEWS||Object.keys(allViews).join(',')).split(',');
const views=Object.fromEntries(wantedViews.map(name=>[name,allViews[name]]).filter(([,dir])=>dir));
const poses=(process.env.ATLAS_POSES||'idle:0').split(',').map(p=>p.split(':'));
const browser=await chromium.launch({channel:'msedge',headless:true,args:['--allow-file-access-from-files']});
const page=await browser.newPage({viewport:{width:1200,height:900},deviceScaleFactor:1});
const errors=[];page.on('pageerror',e=>errors.push(e.message));
await page.goto(url);await page.waitForFunction(()=>window.atlas?.inspect().textureLoaded);
await page.getByRole('button',{name:'Pausar',exact:true}).click();
if(process.env.ATLAS_SECTION==='machines')await page.getByRole('link',{name:'Máquinas',exact:true}).click();
await page.evaluate(id=>atlas.selectModel(id),model);
for(const version of versions){
  await page.locator('#version').selectOption(version);
  for(const [clip,at] of poses){
    await page.evaluate(name=>atlas.selectClip(name),clip);
    await page.locator('#timeline').fill(at);
    for(const [name,dir] of Object.entries(views)){
      await page.evaluate(d=>atlas.view(...d),dir);await page.waitForTimeout(120);
      await page.locator('#scene').screenshot({path:path.join(shots,`${model}-${version}-${clip}${at}-${name}.png`)});
    }
  }
}
await browser.close();
if(errors.length){console.error(errors.join('\n'));process.exit(1);}
console.log(`capturas em ${shots}`);
