import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

const data = window.ATLAS_DATA;
const $ = id => document.getElementById(id);
const labels = {idle:'Repouso',swim:'Nado',emerge:'Emergir',stunned:'Atordoado',phaseChange:'Mudança de fase',death:'Morte',slam:'Pancada',grab:'Agarrão',ink:'Nuvem de tinta',whirlpool:'Redemoinho',spawnYoung:'Invocar crias',waterJet:'Jato de água',roar:'Rugido abissal',bolts:'Raios guiados',ambush:'Emboscada',doubleGrab:'Agarrão duplo',strike:'Bote',chain:'Descarga em cadeia',coil:'Enrolar',sing:'Canto',surface:'Subir à superfície',sonic:'Onda sonora',pulse:'Pulsação',hatch:'Eclosão',attack:'Ataque',rise:'Erguer',release:'Soltar'};
const descriptions = {
 idle:'Massa suspensa, respiração lenta e uma onda que percorre os tentáculos até as pontas.',
 swim:'O manto contrai, os braços empurram e o corpo desliza. As pontas acompanham com atraso.',
 emerge:'O corpo sobe primeiro. Os tentáculos se abrem em sequência e assentam com peso.',
 stunned:'A cabeça pende, o manto oscila pouco e as pontas ficam soltas: um intervalo de vulnerabilidade.',
 phaseChange:'Contração, explosão e reverberação. Um momento frenético entre os ritmos da luta.',
 death:'Última contração, perda de sustentação e queda progressiva dos tentáculos.',
 slam:'O braço direito prepara a pancada. A massa do corpo reage ao golpe e a recuperação percorre as juntas.',
 grab:'O braço esquerdo serpenteia, avança e fecha a curva. Os braços livres fazem o contrapeso.',
 doubleGrab:'Dois braços abrem a silhueta e fecham como pinças, com as pontas seguindo o movimento.',
 ink:'O manto incha sob tensão e se comprime em um pulso rápido. A mandíbula acompanha a expulsão.',
 whirlpool:'Uma onda circular atravessa os oito braços. Cabeça e manto torcem juntos em um laço contínuo.',
 spawnYoung:'Os braços de trás sobem. Contrações do manto anunciam a liberação dos ovos.',
 waterJet:'Preparação curta, disparo rápido e recuo. O retorno é amortecido pelas pontas dos braços.',
 roar:'O corpo se fecha sob tensão. Manto, mandíbula e braços abrem juntos no rugido.',
 bolts:'Quatro braços formam antenas. As pontas tremem antes da descarga e relaxam em sequência.',
 ambush:'O corpo se comprime para baixo e os tentáculos mergulham em um golpe curto e violento.'
};
const machineLabels={idle:'Parada',start:'Ligando',run:'Funcionando',stop:'Desligando',overload:'Sobrecarga',full:'Cheia',empty:'Vazia'};
let machineMode=false, stateKey=null, pending=[], fadeFrom=null, machineMats=[];
const catalog=()=>machineMode?(data.machines||[]):data.models;
const isMachine=()=>model.kind==='machine';
const label=n=>(isMachine()?machineLabels[n]:labels[n])||n;
const FADE=.3;
const fmt = (n, digits=2) => Number(n).toLocaleString('pt-BR',{minimumFractionDigits:digits,maximumFractionDigits:digits});
const audioClock = seconds => {seconds=Number.isFinite(seconds)?Math.floor(seconds):0;return `${Math.floor(seconds/60)}:${String(seconds%60).padStart(2,'0')}`;};
const el = (tag, className, text) => { const node=document.createElement(tag);if(className)node.className=className;if(text!==undefined)node.textContent=text;return node; };
const dl = (target, rows) => {target.replaceChildren();for(const [key,value] of rows)target.append(el('dt','',key),el('dd','',String(value)));};
let model=data.models[0], clipName='idle', time=0, playing=!matchMedia('(prefers-reduced-motion: reduce)').matches;
let root, boneMap=new Map(), helpers, grid, renderer, scene, camera, controls, currentBounds, framePoints=[], lastFrame=0;
let baseRot=new Map(), basePos=new Map(), frameHandle, viewportObserver;
const textures=new Map();
const inGame = v => (v.label||'').startsWith('No jogo');
const selectedVersion = () => model.versions.find(v=>v.version===$('version').value)||model.versions.at(-1);
const clips = () => selectedVersion().clips;
const clip = () => clips()[clipName];
const geometry = () => selectedVersion();

// Minecraft usa a ordem de rotação ZYX. O grupo raiz inverte Y, preservando os pivôs Java.
function boxGeometry(cube, resolution) {
  const [w,h,d]=cube.size, f=cube.inflate||0;
  const [x,y,z]=cube.position;
  const v=[[x-f,y-f,z-f],[x+w+f,y-f,z-f],[x+w+f,y+h+f,z-f],[x-f,y+h+f,z-f],
    [x-f,y-f,z+d+f],[x+w+f,y-f,z+d+f],[x+w+f,y+h+f,z+d+f],[x-f,y+h+f,z+d+f]];
  const [u,q]=cube.uv||[0,0];
  const faces=cube.faces?[[[5,4,0,1],cube.faces.top],[[2,3,7,6],cube.faces.bottom],[[0,4,7,3],cube.faces.left],
    [[1,0,3,2],cube.faces.front],[[5,1,2,6],cube.faces.right],[[4,5,6,7],cube.faces.back]].filter(f=>f[1]):[[[5,4,0,1],[u+d,q,u+d+w,q+d]],[[2,3,7,6],[u+d+w,q+d,u+d+2*w,q]],
    [[0,4,7,3],[u,q+d,u+d,q+d+h]],[[1,0,3,2],[u+d,q+d,u+d+w,q+d+h]],
    [[5,1,2,6],[u+d+w,q+d,u+2*d+w,q+d+h]],[[4,5,6,7],[u+2*d+w,q+d,u+2*d+2*w,q+d+h]]];
  const pos=[],uv=[];
  for(const [indices,rect] of faces){
    const [u1,v1,u2,v2]=rect, coords=[[u2,v1],[u1,v1],[u1,v2],[u2,v2]];
    for(const k of [0,1,2,0,2,3]){pos.push(...v[indices[k]]);uv.push(coords[k][0]/resolution[0],1-coords[k][1]/resolution[1]);}
  }
  const g=new THREE.BufferGeometry();
  g.setAttribute('position',new THREE.Float32BufferAttribute(pos,3));
  g.setAttribute('uv',new THREE.Float32BufferAttribute(uv,2));g.computeVertexNormals();return g;
}

function texture(src){
  if(!textures.has(src)){
    const t=new THREE.TextureLoader().load(src,()=>renderOnce(),undefined,()=>showError('Não foi possível carregar a textura. Gere o catálogo novamente.'));
    t.magFilter=THREE.NearestFilter;t.minFilter=THREE.NearestFilter;t.generateMipmaps=false;t.colorSpace=THREE.SRGBColorSpace;textures.set(src,t);
  }
  return textures.get(src);
}
function material(glass=false){
  const mode=$('material').value;
  const v=selectedVersion(),glow=mode==='texture'&&v.glow;
  return new THREE.MeshStandardMaterial({map:mode==='texture'?texture(v.texture):mode==='uv'?texture(v.uv||model.uvTexture):null,
    ...(glow?{emissiveMap:texture(v.glow),emissive:0xffffff}:{}),
    color:mode==='clay'?0x97c1c3:mode==='wire'?0x6ee4cf:0xffffff,roughness:.88,metalness:0,side:THREE.DoubleSide,wireframe:mode==='wire',
    ...(glass&&mode==='texture'?{transparent:true,depthWrite:false,alphaTest:.01}:isMachine()?{alphaTest:.5}:model.translucent&&mode==='texture'?{transparent:true,alphaTest:.02}:{alphaTest:.15})});
}
function disposeGroup(group){
  if(!group)return;
  const materials=new Set();group.traverse(o=>{if(o.geometry)o.geometry.dispose();if(o.material)materials.add(o.material);});
  for(const m of materials)m.dispose();scene.remove(group);
}
function makeRig(){
  if(!scene)return;
  disposeGroup(root);disposeGroup(helpers);boneMap=new Map();baseRot=new Map();basePos=new Map();
  root=new THREE.Group();root.scale.set(isMachine()?-1:1,-1,1);boneMap.set('root',root);scene.add(root);
  for(const b of geometry().bones){
    const group=new THREE.Group();group.name=b.name;group.position.fromArray(b.position);
    group.rotation.set(...b.rotation.map(THREE.MathUtils.degToRad),'ZYX');
    boneMap.get(b.parent).add(group);boneMap.set(b.name,group);
    baseRot.set(b.name,group.rotation.clone());basePos.set(b.name,group.position.clone());
  }
  const mat=material(),glassMat=isMachine()?material(true):mat;machineMats=isMachine()?[mat,glassMat]:[];
  for(const c of geometry().cubes){
    const mesh=new THREE.Mesh(boxGeometry(c,selectedVersion().resolution),c.glass?glassMat:mat);mesh.name=c.name;if(c.glass)mesh.renderOrder=1;
    // Escudo intacto na prévia; as cascas transparentes são camadas de dano do jogo.
    mesh.visible=!c.name.startsWith('crack_');boneMap.get(c.bone).add(mesh);
  }
  helpers=new THREE.Group();scene.add(helpers);
  const lineMat=new THREE.LineBasicMaterial({color:0xf0c783,depthTest:false,transparent:true,opacity:.8});
  for(const b of geometry().bones){
    const g=new THREE.BufferGeometry().setFromPoints([new THREE.Vector3(),new THREE.Vector3()]);
    const line=new THREE.Line(g,lineMat);line.userData.bone=b;line.renderOrder=10;helpers.add(line);
  }
  applyPose();frameModel();updateInfo();
}

export function sample(keys,t){
  if(t<=keys[0][0])return keys[0][1];
  if(t>=keys.at(-1)[0])return keys.at(-1)[1];
  let low=0,high=keys.length-1;
  while(high-low>1){const mid=(low+high)>>1;if(keys[mid][0]<=t)low=mid;else high=mid;}
  const a=keys[low],b=keys[high],f=(t-a[0])/(b[0]-a[0]);
  if(b[2]==='linear')return a[1].map((v,i)=>v+(b[1][i]-v)*f);
  const p=keys[Math.max(0,low-1)][1],q=keys[Math.min(keys.length-1,high+1)][1];
  return a[1].map((v,i)=>.5*((2*v)+(-p[i]+b[1][i])*f+(2*p[i]-5*v+4*b[1][i]-q[i])*f*f+(-p[i]+3*v-3*b[1][i]+q[i])*f*f*f));
}

function applyPose(at=time){
  if(!root)return;
  for(const [name,b] of boneMap){if(name==='root')continue;b.position.copy(basePos.get(name));b.rotation.copy(baseRot.get(name));b.scale.set(1,1,1);}
  let red=0;
  for(const layer of layers(at)){
   const w=layer.weight;if(layer.name==='overload')red+=w;
   for(const tr of layer.clip.tracks){
    const b=boneMap.get(tr.bone);if(!b)continue;
    const v=sample(tr.keys,layer.time);
    if(tr.channel==='rotation'){b.rotation.x+=w*THREE.MathUtils.degToRad(v[0]);b.rotation.y+=w*THREE.MathUtils.degToRad(v[1]);b.rotation.z+=w*THREE.MathUtils.degToRad(v[2]);}
    else if(tr.channel==='position'){b.position.x+=w*v[0];b.position.y-=w*v[1];b.position.z+=w*v[2];}
    else if(tr.channel==='scale'){b.scale.x+=w*(v[0]-1);b.scale.y+=w*(v[1]-1);b.scale.z+=w*(v[2]-1);}
   }
  }
  machineTint(red);
  if(model.id==='kraken' && $('alive').checked)alive(at);
  if(boneMap.has('saddle'))boneMap.get('saddle').visible=$('saddle').checked;
  root.updateMatrixWorld(true);
  helpers.visible=$('rig').checked;
  if(helpers.visible){
    const a=new THREE.Vector3(),b=new THREE.Vector3();
    for(const line of helpers.children){const spec=line.userData.bone;boneMap.get(spec.name).getWorldPosition(a);boneMap.get(spec.parent).getWorldPosition(b);
      const attr=line.geometry.getAttribute('position');attr.setXYZ(0,a.x,a.y,a.z);attr.setXYZ(1,b.x,b.y,b.z);attr.needsUpdate=true;line.geometry.computeBoundingSphere();}
  }
}

function alive(seconds){
  // Camada contínua da fase 1, transcrita de KrakenModel.alive; não altera keyframes.
  const t=seconds*20,swim=clipName==='swim'?1:0,m=boneMap.get('mantle'),h=boneMap.get('head');
  const breath=Math.sin(t*.05);m.scale.x*=1+.05*breath;m.scale.z*=1+.05*breath;m.scale.y*=1-.035*breath;
  m.rotation.z+=.035*Math.sin(t*.031);m.rotation.x+=.03*Math.sin(t*.043)-.16*swim;
  h.rotation.x+=.05*Math.sin(t*.05+1)-.08*swim;h.rotation.y+=.06*Math.sin(t*.023);
  const freq=.07+.12*swim,amp=.08+.05*swim,stroke=swim*.4*Math.sin(t*.22);
  for(let i=0;i<8;i++)for(let j=0;j<5;j++){const s=boneMap.get(`arm_${i}_segment_${j}`),reach=.45+.3*j;
    s.rotation.x+=amp*reach*Math.sin(t*freq-j*.9+i*.785)+(j===0?stroke:0);
    s.rotation.y+=amp*.6*reach*Math.cos(t*freq*.7-j*.8+i*1.3);}
}

function bounds(){
  // Enquadra a animação inteira, inclusive as pontas em expansão, não só a pose inicial.
  const box=new THREE.Box3();framePoints=[];
  for(let i=0;i<=48;i++){applyPose(clip().length*i/48);root.traverse(o=>{if(o.isMesh&&o.visible&&o.parent.visible){
    if(!o.geometry.boundingBox)o.geometry.computeBoundingBox();
    const b=o.geometry.boundingBox;
    for(const x of [b.min.x,b.max.x])for(const y of [b.min.y,b.max.y])for(const z of [b.min.z,b.max.z]){
      const p=new THREE.Vector3(x,y,z).applyMatrix4(o.matrixWorld);framePoints.push(p);box.expandByPoint(p);
    }
  }});}
  applyPose();return box;
}
function frameModel(direction){
  if(!root)return;
  currentBounds=bounds();const size=currentBounds.getSize(new THREE.Vector3()),center=currentBounds.getCenter(new THREE.Vector3());
  const radius=size.length()/2;
  const dir=direction||(isMachine()?new THREE.Vector3(.85,.62,-1.15):(model.id==='whale'||model.id==='sperm_whale'||model.id==='moray')?new THREE.Vector3(1,.55,-1.1):new THREE.Vector3(.9,.65,1.3));
  dir.normalize();
  const right=new THREE.Vector3().crossVectors(new THREE.Vector3(0,1,0),dir).normalize();
  const up=new THREE.Vector3().crossVectors(dir,right).normalize();
  const tanY=Math.tan(THREE.MathUtils.degToRad(camera.fov/2))*.72,tanX=tanY*camera.aspect;
  let dist=radius;
  // Projeta os cantos reais dos cubos; a caixa global superestima braços diagonais.
  for(const point of framePoints){
    const corner=point.clone().sub(center);
    dist=Math.max(dist,Math.abs(corner.dot(right))/tanX+corner.dot(dir),Math.abs(corner.dot(up))/tanY+corner.dot(dir));
  }
  controls.target.copy(center);camera.position.copy(center).add(dir.normalize().multiplyScalar(dist));
  camera.near=Math.max(.01,radius/1000);camera.far=radius*100;camera.updateProjectionMatrix();
  controls.minDistance=radius*.15;controls.maxDistance=radius*12;controls.update();
  if(grid){scene.remove(grid);grid.geometry.dispose();grid.material.dispose();}
  const gridSize=Math.ceil(Math.max(size.x,size.z)*1.7/16)*16;
  grid=new THREE.GridHelper(gridSize,Math.min(80,Math.max(8,gridSize/8)),0x31535b,0x163039);
  grid.position.set(center.x,currentBounds.min.y-3,center.z);grid.visible=$('grid').checked;scene.add(grid);
  renderOnce();
}
function renderOnce(){if(renderer&&root){applyPose();renderer.render(scene,camera);}}
function resize(){
  if(!renderer)return;const rect=$('viewport').getBoundingClientRect();if(rect.width<1||rect.height<1)return;
  renderer.setSize(rect.width,rect.height,false);camera.aspect=rect.width/rect.height;camera.updateProjectionMatrix();
  frameModel();
}
function showError(message){$('viewer-error').hidden=false;$('viewer-error').textContent=message;}
function initScene(){
  try{
    renderer=new THREE.WebGLRenderer({canvas:$('scene'),antialias:true,alpha:false});renderer.setPixelRatio(Math.min(devicePixelRatio,2));
    renderer.setClearColor(0x09191f);renderer.outputColorSpace=THREE.SRGBColorSpace;renderer.toneMapping=THREE.ACESFilmicToneMapping;renderer.toneMappingExposure=1.35;
    scene=new THREE.Scene();camera=new THREE.PerspectiveCamera(36,1,.1,5000);
    scene.add(new THREE.HemisphereLight(0xdcf8ff,0x35616a,2.6));
    const key=new THREE.DirectionalLight(0xfff2d9,3);key.position.set(60,130,110);scene.add(key);
    const rim=new THREE.DirectionalLight(0x74e8de,2);rim.position.set(-100,50,-80);scene.add(rim);
    controls=new OrbitControls(camera,$('scene'));controls.enableDamping=true;controls.dampingFactor=.08;
    viewportObserver=new ResizeObserver(resize);viewportObserver.observe($('viewport'));
    $('scene').addEventListener('webglcontextlost',e=>{e.preventDefault();playing=false;syncTransport();showError('O navegador perdeu a sessão 3D. Recarregue esta página para reabrir o modelo.');});
    resize();makeRig();
  }catch(error){console.error(error);showError('Não foi possível iniciar o 3D. Abra no Chrome ou Edge com aceleração gráfica ativada. Áudio, texturas e documentação continuam disponíveis.');}
}

function mobList(){
  $('mob-count').textContent=catalog().length;
  $('mob-list').replaceChildren(...catalog().map(m=>{
    const b=el('button','mob');b.dataset.id=m.id;b.setAttribute('aria-pressed',String(m.id===model.id));
    const img=el('img','mob-icon');img.src=m.texture;img.alt='';const text=el('span');text.append(el('strong','',m.name),el('small','',`${Object.keys(m.clips).length} animações${m.versions.length>1?` · ${m.versions.length} versões`:''}`));
    b.append(img,text);b.onclick=()=>selectModel(m.id);return b;
  }));
}
function selectModel(id){
  model=catalog().find(m=>m.id===id)||catalog()[0];versionOptions();clipName=Object.keys(clips())[0];time=0;$('clip-search').value='';
  stateKey=null;pending=[];fadeFrom=null;
  $('alive-label').hidden=model.id!=='kraken';$('saddle-label').hidden=model.id!=='whale';
  $('model-name').textContent=model.name;
  $('model-kind').textContent=isMachine()?'Máquina animada · bloco':model.id==='kraken'?'O terror das profundezas':model.id==='kraken_egg'?'Entidade de invocação':'Criatura do oceano';
  stateButtons();if(isMachine()&&model.states?.idle)playState('idle',true);
  mobList();clipList();clipInfo();makeRig();
}
function versionOptions(){
  $('version').replaceChildren(...model.versions.map(v=>{const option=el('option','',v.label);option.value=v.version;return option;}));
  $('version').value=(model.versions.find(v=>inGame(v))||model.versions.at(-1)).version;
}
function clipList(){
  const query=$('clip-search').value.toLocaleLowerCase('pt-BR');
  const entries=Object.entries(clips()).filter(([n])=>`${label(n)} ${n}`.toLocaleLowerCase('pt-BR').includes(query));
  $('clip-count').textContent=Object.keys(clips()).length;
  $('clip-list').replaceChildren(...entries.map(([name,c])=>{
    const b=el('button','clip');b.dataset.clip=name;b.setAttribute('aria-pressed',String(name===clipName));
    b.append(el('span','',label(name)),el('small','',`${c.loop?'↻ ':''}${fmt(c.length,1)} s`));
    b.onclick=()=>{clipName=name;time=0;stateKey=null;pending=[];fadeFrom=null;stateButtons();clipList();clipInfo();frameModel();};return b;
  }));
  if(!entries.length)$('clip-list').append(el('p','empty','Nenhum movimento encontrado.'));
}
function clipInfo(){
  const c=clip(),warning=model.warnings[clipName],live=inGame(selectedVersion()),proposed=!live&&selectedVersion().version!=='v1';
  $('clip-title').textContent=label(clipName);$('stage-clip').textContent=label(clipName);
  $('stage-version').textContent=live?`NO JOGO · ${selectedVersion().version.toUpperCase()}`:proposed?`PROPOSTA ${selectedVersion().version.toUpperCase()} · SÓ NO SITE`:'REFERÊNCIA V1 DO MOD';
  $('clip-description').textContent=isMachine()?(model.clipDescriptions?.[clipName]||''):proposed?(model.id==='kraken'&&selectedVersion().version==='v2'?descriptions[clipName]:'Estudo do movimento; consulte o histórico da versão.'):`Keyframes preservados de ${model.name}. ${c.loop?'Este movimento é um laço.':'Este movimento é executado uma vez no jogo.'}`;
  dl($('clip-info'),[['Identificador',clipName],['Duração',`${fmt(c.length)} s`],['Canais',c.tracks.length],...(warning?[['Aviso preservado',`${fmt(warning)} s`]]:[])]);
  $('timeline').max=c.length;$('impact-marker').hidden=!warning;$('jump-impact').hidden=!warning;
  if(warning)$('impact-marker').style.left=`${100*warning/c.length}%`;
  syncTransport();updateInfo();
}
function updateInfo(){
  const g=geometry();const live=inGame(g),proposed=!live&&g.version!=='v1';
  dl($('model-info'),[['Rig',`${g.bones.length} ossos · ${g.cubes.length} cubos`],['Textura',`${g.resolution[0]} × ${g.resolution[1]} px`],['Arquivo preservado',`tools/atlas/versions/${model.id}-${g.version}.json`],['Status',live?'No jogo':proposed?'Estudo visual; não instalado no jogo':'Referência preservada do mod'],...(g.notes?[['Notas',g.notes]]:[])]);
  $('model-note').textContent=model.id==='kraken'?'V2: 7 juntas por braço (antes 5), mandíbula articulada e abas laterais do manto. UVs reaproveitam a textura existente. A opção de movimento contínuo acrescenta a camada procedural da fase 1.':'';
  $('version-history').textContent='Registro permanente · '+model.versions.map(v=>`${v.version}: ${v.label} (${v.date})`).join(' · ')+'. Cada versão preserva seu modelo, textura e animações.';
}
function syncTransport(){
  $('play').textContent=playing?'Pausar':'Reproduzir';$('play').setAttribute('aria-pressed',String(playing));
  $('timeline').value=time;$('time').textContent=`${fmt(time)} / ${fmt(clip().length)} s`;
  const w=model.warnings[clipName];
  $('motion-state').textContent=w?(time<w?'Antecipação · aviso':'Golpe / recuperação'):(clip().loop?'Laço contínuo':time>=clip().length?'Fim do movimento':'Movimento único');
}
function animate(now){
  const dt=Math.min((now-lastFrame)/1000,.1);lastFrame=now;
  if(!$('modelos').hidden&&renderer&&root){
    if(playing){time+=dt*Number($('speed').value);if(time>=clip().length){if(pending.length&&!clip().loop){time-=clip().length;clipName=pending.shift();clipList();clipInfo();}
      else if($('repeat').checked)time%=clip().length;else{time=clip().length;playing=false;}}}
    applyPose();controls.update();renderer.render(scene,camera);syncTransport();
  }
  frameHandle=requestAnimationFrame(animate);
}

function audioList(){
  const query=$('audio-search').value.toLocaleLowerCase('pt-BR'),kind=$('audio-kind').value;
  const entries=data.audio.filter(a=>(kind==='all'||a.kind===kind)&&`${a.name} ${a.description}`.toLocaleLowerCase('pt-BR').includes(query));
  $('audio-count').textContent=`${entries.length} de ${data.audio.length} arquivos`;
  document.querySelectorAll('audio').forEach(a=>a.pause());
  $('audio-list').replaceChildren(...entries.map(a=>{
    const article=el('article','audio-card');article.append(el('h2','',a.description||a.name),el('p','kind',({sfx:'Efeito sonoro',music:'Música',stem:'Camada de música adaptativa',ambience:'Ambiência',comparison:'Comparação · IA local'})[a.kind]||a.kind),el('code','path',a.file||a.name));
    if(a.available){
      const player=el('audio');player.controls=true;player.preload='metadata';player.src=a.src;player.setAttribute('aria-label',a.description||a.name);player.addEventListener('play',()=>document.querySelectorAll('audio').forEach(other=>{if(other!==player)other.pause();}));article.append(player);
      const seekControls=el('div','audio-seek-controls'),back=el('button','skip-audio','−15 s'),forward=el('button','skip-audio','+15 s'),seek=el('input','audio-seek');
      back.type=forward.type='button';seek.type='range';seek.min='0';seek.max='1000';seek.step='1';seek.value='0';seek.setAttribute('aria-label',`Buscar trecho: ${a.description||a.name}`);seek.disabled=true;
      const clock=el('output','audio-clock','0:00 / 0:00');
      const syncSeek=()=>{const duration=Number.isFinite(player.duration)?player.duration:0;seek.disabled=!duration;seek.value=duration?String(Math.round(player.currentTime/duration*1000)):'0';clock.textContent=`${audioClock(player.currentTime)} / ${audioClock(duration)}`;};
      for(const event of ['loadedmetadata','durationchange','timeupdate','seeked'])player.addEventListener(event,syncSeek);
      back.onclick=()=>{if(Number.isFinite(player.duration))player.currentTime=Math.max(0,player.currentTime-15);};
      forward.onclick=()=>{if(Number.isFinite(player.duration))player.currentTime=Math.min(player.duration,player.currentTime+15);};
      seek.oninput=()=>{if(Number.isFinite(player.duration)){player.currentTime=Number(seek.value)/1000*player.duration;syncSeek();}};
      seekControls.append(back,seek,forward,clock);article.append(seekControls);
      if(a.mixes?.length){const label=el('label','','Mixagem de audição'),select=el('select');for(const mix of a.mixes){const option=el('option','',({full:'Completa',respiro:'Respiro · sem breaks',sem_gloria:'Sem glória · base + breaks'})[mix.id]||mix.id);option.value=mix.src;select.append(option);}select.value=a.src;select.onchange=()=>{player.pause();player.src=select.value;player.load();};label.append(select);article.append(label);}}
    else article.append(el('p','','Arquivo de áudio não encontrado neste catálogo. Atualize após a entrega do áudio.'));
    if(a.listen_for)article.append(el('p','',`Ouça: ${a.listen_for}`));
    if(a.loop)article.append(el('p','',a.playback_note||(a.audition?'Prévia de 3 voltas com crossfade, como no player original.':`Arquivo com sobra de ${a.loop_overlap_s||0} s para crossfade. Este player não simula a emenda do jogo.`)));
    const stats=el('div','stats');for(const value of [`${fmt(a.seconds||0,1)} s`,a.lufs!=null?`${a.lufs} LUFS`:'LUFS não medido',a.true_peak!=null?`${a.true_peak} dBTP`:'Pico não medido'])stats.append(el('span','',value));article.append(stats);
    const details=el('details');details.append(el('summary','','Medidas, origem e espectrograma'));const list=el('dl');
    dl(list,[['Canais',a.channels===1?'Mono':a.channels===2?'Estéreo':'Não informado'],['LRA',a.lra!=null?`${a.lra} LU`:'Não medido'],['Saturação',a.clipping_samples??'Não medida'],['Silêncio',a.silence_s!=null?`${a.silence_s} s`:'Não medido'],['Tamanho',`${Math.round((a.bytes||0)/1024)} KB`],['Origem / licença',a.license||'Não informada'],['Gerador',a.script||a.model||'Não informado'],['Semente',a.seed??'Não informada'],...(a.seam_jump!=null?[['Emenda (salto / espectro)',`${a.seam_jump} / ${a.seam_spectral}`]]:[]),...(a.prompt?[['Prompt',a.prompt]]:[])]);details.append(list);
    if(a.spectrogram){const img=el('img');img.src=a.spectrogram;img.alt=`Espectrograma de ${a.name}`;img.loading='lazy';details.append(img);}article.append(details);
    if(a.available){const link=el('a','',a.mixes?'Baixar mixagem completa':'Baixar áudio original');link.href=a.download||a.src;link.download='';article.append(link);}return article;
  }));
  if(!entries.length)$('audio-list').append(el('p','empty','Nenhum áudio encontrado. Tente outro nome ou categoria.'));
}

function textureList(){
  const query=$('texture-search').value.toLocaleLowerCase('pt-BR'),group=$('texture-group').value;
  const entries=data.textures.filter(t=>(group==='all'||t.group===group)&&t.path.toLocaleLowerCase('pt-BR').includes(query));
  $('texture-count').textContent=`${entries.length} de ${data.textures.length} texturas`;
  $('texture-list').replaceChildren(...entries.map(t=>{
    const card=el('article','texture-card'),preview=el('div','texture-preview'),img=el('img');img.src=t.src;img.alt=t.name;img.loading='lazy';preview.append(img);
    const link=el('a','','Abrir / baixar PNG');link.href=t.src;link.target='_blank';link.rel='noopener';
    card.append(preview,el('h2','',t.name),el('p','',`${t.size[0]} × ${t.size[1]} px · ${t.path}`),link);return card;
  }));
  if(!entries.length)$('texture-list').append(el('p','empty','Nenhuma textura encontrada. Tente outro nome ou pasta.'));
}
function documentation(){
  const doc=data.docs[Number($('doc-select').value)||0];$('doc-content').replaceChildren(el('pre','',doc.text));
}
function route(){
  let name=location.hash.slice(1)||'modelos';if(!['modelos','maquinas','audio','texturas','documentacao'].includes(name))name='modelos';
  const page=name==='maquinas'?'modelos':name;
  document.querySelectorAll('.page').forEach(p=>p.hidden=p.id!==page);
  if((name==='maquinas')!==machineMode&&(name==='maquinas'||name==='modelos')){machineMode=name==='maquinas';headings();selectModel(catalog()[0].id);}
  document.querySelectorAll('nav a').forEach(a=>{if(a.dataset.page===name)a.setAttribute('aria-current','page');else a.removeAttribute('aria-current');});
  if(name!=='audio')document.querySelectorAll('audio').forEach(a=>a.pause());
  if(page==='modelos')requestAnimationFrame(resize);
}

$('play').onclick=()=>{if(time>=clip().length)time=0;playing=!playing;syncTransport();};
$('restart').onclick=()=>{time=0;syncTransport();renderOnce();};
$('timeline').oninput=()=>{time=Number($('timeline').value);playing=false;syncTransport();renderOnce();};
$('jump-impact').onclick=()=>{time=model.warnings[clipName];playing=false;syncTransport();renderOnce();};
$('fit').onclick=()=>frameModel();
$('front').onclick=()=>frameModel(new THREE.Vector3(0,.12,model.id.includes('kraken')||model.id==='tentacle'?1:-1));
$('side').onclick=()=>frameModel(new THREE.Vector3(1,.12,0));
$('top').onclick=()=>frameModel(new THREE.Vector3(0,1,.001));
$('grid').onchange=()=>{if(grid)grid.visible=$('grid').checked;renderOnce();};
$('rig').onchange=renderOnce;$('alive').onchange=()=>frameModel();$('saddle').onchange=()=>frameModel();
$('version').onchange=()=>{if(!clips()[clipName])clipName=Object.keys(clips())[0];time=0;clipList();clipInfo();makeRig();};
$('material').onchange=()=>{if(!root)return;if(isMachine()){makeRig();return;}const mat=material(),old=new Set();root.traverse(o=>{if(o.isMesh){old.add(o.material);o.material=mat;}});old.forEach(m=>m.dispose());renderOnce();};
$('clip-search').oninput=clipList;
$('audio-search').oninput=audioList;$('audio-kind').onchange=audioList;
$('texture-search').oninput=textureList;$('texture-group').onchange=textureList;
$('texture-size').onchange=()=>$('texture-list').style.setProperty('--tile',`${$('texture-size').value}px`);
$('doc-select').onchange=documentation;
$('download-model').onclick=()=>{
  const version=selectedVersion().version;
  const record={...selectedVersion(),format:'aquamarine-atlas-1',id:model.id,version,coordinates:'Java; Y down; rotations degrees ZYX; position animation Y up'};
  const url=URL.createObjectURL(new Blob([JSON.stringify(record,null,2)],{type:'application/json'}));const link=el('a');link.href=url;link.download=`${model.id}-${version}.json`;link.click();setTimeout(()=>URL.revokeObjectURL(url),1000);
};
$('refresh').onclick=async()=>{
  if(location.protocol==='file:'){$('audio-status').textContent='Para atualizar, execute tools/atlas/abrir.ps1 e recarregue esta página. O HTML aberto diretamente é um snapshot portátil.';return;}
  $('refresh').disabled=true;$('audio-status').textContent='Lendo as entregas mais recentes do projeto…';
  try{const response=await fetch('/api/refresh',{method:'POST'});if(!response.ok)throw new Error('refresh');location.reload();}
  catch{$('audio-status').textContent='Não foi possível atualizar. Execute tools/atlas/abrir.ps1 para gerar um catálogo novo.';$('refresh').disabled=false;}
};
$('scene').onkeydown=e=>{
  if(!controls)return;
  const offset=camera.position.clone().sub(controls.target),spherical=new THREE.Spherical().setFromVector3(offset);
  if(e.key==='ArrowLeft')spherical.theta-=.12;else if(e.key==='ArrowRight')spherical.theta+=.12;
  else if(e.key==='ArrowUp')spherical.phi-=.12;else if(e.key==='ArrowDown')spherical.phi+=.12;
  else if(e.key==='+'||e.key==='=')spherical.radius*=.9;else if(e.key==='-')spherical.radius*=1.1;
  else if(e.key==='f'||e.key==='F'){frameModel();e.preventDefault();return;}
  else if(e.key===' '){$('play').click();e.preventDefault();return;}else return;
  spherical.makeSafe();camera.position.copy(controls.target).add(new THREE.Vector3().setFromSpherical(spherical));controls.update();renderOnce();e.preventDefault();
};
for(const group of [...new Set(data.textures.map(t=>t.group))].sort()){const option=el('option','',group);option.value=group;$('texture-group').append(option);}
data.docs.forEach((doc,i)=>{const option=el('option','',doc.name);option.value=i;$('doc-select').append(option);});
$('build-info').textContent=`Catálogo gerado em ${new Date(data.generated).toLocaleString('pt-BR')}`;
window.addEventListener('hashchange',route);
versionOptions();mobList();clipList();clipInfo();audioList();textureList();documentation();route();initScene();
frameHandle=requestAnimationFrame(animate);
document.addEventListener('visibilitychange',()=>{if(document.hidden){cancelAnimationFrame(frameHandle);document.querySelectorAll('audio').forEach(a=>a.pause());}else{lastFrame=performance.now();frameHandle=requestAnimationFrame(animate);}});
// Máquinas (0.6.1, 7B): estados como no jogo. Parada = stop e idle; Funcionando = start e run; Sobrecarga = overload.
function layers(at){
  const out=[{name:clipName,clip:clip(),time:at,weight:1}];
  if(fadeFrom&&isMachine()){
    const age=(performance.now()-fadeFrom.at)/1000;
    if(age<FADE){const f=age/FADE,w=f*f*(3-2*f),c=clips()[fadeFrom.name];
      if(c){const t=c.loop?(fadeFrom.time+age)%c.length:Math.min(c.length,fadeFrom.time+age);out[0].weight=w;out.push({name:fadeFrom.name,clip:c,time:t,weight:1-w});}}
    else fadeFrom=null;
  }
  return out;
}
function playState(key,instant=false){
  const st=model.states?.[key];if(!st)return;
  if(!instant&&clipName)fadeFrom={name:clipName,time,at:performance.now()};
  stateKey=key;pending=[];
  const enter=st.enter&&clips()[st.enter]&&!instant?st.enter:null;
  clipName=enter||st.loop;if(enter)pending=[st.loop];time=0;playing=true;
  stateButtons();clipList();clipInfo();syncTransport();
}
function stateButtons(){
  const box=$('machine-states');box.hidden=!isMachine()||!model.states;
  if(box.hidden){box.replaceChildren();return;}
  box.replaceChildren(el('span','','Estado no jogo:'),...Object.entries(model.states).map(([key,st])=>{
    const b=el('button',st.tint?'hot':'',st.label);b.dataset.state=key;b.setAttribute('aria-pressed',String(key===stateKey));b.onclick=()=>playState(key);return b;}));
}
function machineTint(red){
  if(!machineMats.length)return;
  const beat=.5+.5*Math.sin(performance.now()/1000*Math.PI*8),flush=red*(.55+.45*beat);
  for(const m of machineMats){if(m.color)m.color.setRGB(1,1-.18*flush,1-.24*flush);if(m.emissiveMap)m.emissive.setRGB(1,1-.55*red,1-.7*red);}
}
function headings(){
  $('page-title').textContent=machineMode?'Sala de máquinas':'Observatório de criaturas';
  $('page-lead').textContent=machineMode?'Blocos com modelo e animações próprios. Troque o estado e veja a máquina ligar, trabalhar e sobrecarregar.':'Explore os modelos. Veja cada movimento de perto.';
  $('proposal-pill').hidden=machineMode;
  $('list-title').textContent=machineMode?'Máquinas':'Criaturas';
  $('list-note').textContent=machineMode?'Máquinas da 0.6.1 com modelo e animação próprios. Mesmo rig do jogo.':'Todos os rigs próprios do mod. O Kraken abre com a proposta v2.';
}
// Superfície de diagnóstico para os testes locais, sem controles extras na interface.
window.atlas={selectModel,selectClip(name){clipName=name;time=0;stateKey=null;pending=[];fadeFrom=null;clipList();clipInfo();frameModel();},sample,
  playState(key){playState(key);},machine:()=>isMachine()?{state:stateKey,clip:clipName,pending:[...pending],layers:layers(time).map(l=>[l.name,l.weight])}:null,
  view(x,y,z){frameModel(new THREE.Vector3(x,y,z));},
  inspect(){return {model:model.id,clip:clipName,time,playing,bones:boneMap.size,cubes:geometry().cubes.length,
    bounds:currentBounds?.getSize(new THREE.Vector3()).toArray(),renderer:!!renderer,textureLoaded:[...textures.values()].every(t=>!!t.image?.complete)};},
  pose(at){applyPose(at);return [...boneMap].map(([name,b])=>[name,...b.matrixWorld.elements]);}};
