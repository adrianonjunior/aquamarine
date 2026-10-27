import { chromium } from '@playwright/test';
import assert from 'node:assert/strict';
import path from 'node:path';
import { fileURLToPath,pathToFileURL } from 'node:url';
import { mkdir } from 'node:fs/promises';

const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../../..');
const url=process.env.ATLAS_URL||pathToFileURL(path.join(root,'build/atlas/index.html')).href;
const shots=path.join(root,'build/atlas-qa');await mkdir(shots,{recursive:true});
const browser=await chromium.launch({channel:'msedge',headless:true,args:['--allow-file-access-from-files']});
const page=await browser.newPage({viewport:{width:1440,height:1050},deviceScaleFactor:1});
const errors=[];page.on('pageerror',e=>errors.push(e.message));
await page.goto(url);await page.waitForFunction(()=>window.atlas?.inspect().textureLoaded);
assert.equal((await page.evaluate(()=>atlas.inspect())).renderer,true);
await page.getByRole('button',{name:'Pausar',exact:true}).click();
await page.locator('[data-clip="roar"]').click();await page.getByRole('button',{name:'Ir ao fim do aviso'}).click();
await page.screenshot({path:path.join(shots,'desktop.png'),fullPage:true});
const inventory=await page.evaluate(()=>ATLAS_DATA.models.map(m=>({id:m.id,versions:m.versions.map(v=>({id:v.version,clips:Object.entries(v.clips).map(([name,c])=>({name,duration:c.length}))}))})));
let verifiedClips=0;
for(const m of inventory){
  await page.evaluate(id=>atlas.selectModel(id),m.id);
  for(const version of m.versions){
   await page.locator('#version').selectOption(version.id);
   for(const clip of version.clips){
    const result=await page.evaluate(({name,duration})=>{atlas.selectClip(name);
      const a=atlas.pose(0),b=atlas.pose(duration*.58);return {state:atlas.inspect(),finite:b.every(row=>row.slice(1).every(Number.isFinite)),changed:JSON.stringify(a)!==JSON.stringify(b)};},clip);
    assert(result.finite,`${m.id}/${clip}: matriz não finita`);assert(result.changed,`${m.id}/${clip}: pose não muda`);
    assert(result.state.bounds.every(v=>v>0&&Number.isFinite(v)),`${m.id}/${clip}: bounds`);
    verifiedClips++;
   }
  }
}
// 0.6.1 (7B): aba "Máquinas": mesmo visualizador, clipes e botões de estado como no jogo.
await page.getByRole('link',{name:'Máquinas',exact:true}).click();
const machines=await page.evaluate(()=>ATLAS_DATA.machines.map(m=>({id:m.id,states:Object.keys(m.states),clips:Object.entries(m.clips).map(([name,c])=>({name,duration:c.length}))})));
assert(machines.length>=1,'Aba Máquinas vazia');
let machineClips=0;
for(const m of machines){
  await page.evaluate(id=>atlas.selectModel(id),m.id);
  for(const clip of m.clips){
    const result=await page.evaluate(({name,duration})=>{atlas.selectClip(name);
      const a=atlas.pose(0),b=atlas.pose(duration*.58);return {state:atlas.inspect(),finite:b.every(row=>row.slice(1).every(Number.isFinite)),changed:JSON.stringify(a)!==JSON.stringify(b)};},clip);
    assert(result.finite&&result.changed,`${m.id}/${clip.name}: pose`);assert(result.state.bounds.every(v=>v>0),`${m.id}/${clip.name}: bounds`);machineClips++;
  }
  for(const state of m.states){await page.locator(`[data-state="${state}"]`).click();await page.waitForTimeout(150);
    assert.equal((await page.evaluate(()=>atlas.machine())).state,state,`${m.id}: estado ${state}`);}
}
await page.evaluate(()=>atlas.selectModel('vent_generator'));
await page.locator('[data-state="run"]').click();
assert.equal((await page.evaluate(()=>atlas.machine())).clip,'start','Ligar devia tocar o start');
await page.waitForTimeout(2300);
assert.equal((await page.evaluate(()=>atlas.machine())).clip,'run','Depois do start vem o run');
await page.locator('#scene').screenshot({path:path.join(shots,'maquinas-funcionando.png')});
await page.locator('[data-state="overload"]').click();await page.waitForTimeout(500);
await page.screenshot({path:path.join(shots,'maquinas.png'),fullPage:false});
await page.getByRole('link',{name:'Modelos & animações'}).click();
await page.evaluate(()=>atlas.selectModel('kraken'));
const v2=(await page.evaluate(()=>atlas.inspect())).bones;
await page.locator('#version').selectOption('v1');assert((await page.evaluate(()=>atlas.inspect())).bones<v2);
await page.locator('#version').selectOption('v2');
await page.locator('#clip-search').fill('impossível');assert.equal(await page.locator('#clip-list button').count(),0);await page.locator('#clip-search').fill('');
await page.locator('#material').selectOption('uv');await page.locator('#material').selectOption('texture');
await page.locator('#timeline').fill('1');assert.equal((await page.evaluate(()=>atlas.inspect())).playing,false);
await page.locator('#repeat').uncheck();await page.locator('#timeline').fill('3.99');await page.locator('#play').click();await page.waitForTimeout(150);assert.equal((await page.evaluate(()=>atlas.inspect())).playing,false);
await page.locator('#repeat').check();
await page.getByRole('link',{name:'Áudio',exact:true}).click();
const audioCount=await page.locator('audio').count();assert(audioCount>=6);
const audioChecks=await page.evaluate(async()=>{
  const players=[...document.querySelectorAll('audio')];return await Promise.all(players.map(a=>new Promise(resolve=>{a.onloadedmetadata=()=>resolve(Number.isFinite(a.duration)&&a.duration>0);a.onerror=()=>resolve(false);a.preload='metadata';a.load();setTimeout(()=>resolve(false),15000);})));});
assert(audioChecks.every(Boolean),'Áudio faltando ou ilegível');
await page.screenshot({path:path.join(shots,'audio.png'),fullPage:false});
await page.locator('#audio-kind').selectOption('comparison');assert.equal(await page.locator('.audio-card').count(),3);
await page.getByRole('link',{name:'Texturas',exact:true}).click();await page.locator('#texture-search').fill('kraken');assert(await page.locator('.texture-card').count()>0);
await page.getByRole('link',{name:'Documentação',exact:true}).click();assert((await page.locator('#doc-content').innerText()).length>100);
await page.getByRole('link',{name:'Modelos & animações'}).click();
await page.setViewportSize({width:390,height:844});await page.waitForTimeout(400);
await page.locator('#timeline').fill('1.5');await page.screenshot({path:path.join(shots,'mobile.png'),fullPage:true});
assert(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth),'Overflow horizontal mobile');
assert.deepEqual(errors,[]);
console.log(JSON.stringify({machines:machines.length,machineClips,models:inventory.length,versions:inventory.reduce((n,m)=>n+m.versions.length,0),verifiedClips,audio:audioCount,errors,screenshots:shots}));
await browser.close();
