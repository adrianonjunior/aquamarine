# Instalador do Aquamarine {{VERSAO}} (Minecraft {{MC}}, Fabric Loader {{LOADER}}).
# Faz o mesmo que o instalador oficial do Fabric, sem precisar de Java: grava o perfil do Fabric na pasta
# versions do Minecraft, cria o perfil "Aquamarine" no Minecraft Launcher com uma pasta própria e copia os mods.
# Não mexe nos seus mundos nem nos outros perfis. Desinstalar: apague o perfil no launcher e a pasta
# .minecraft\aquamarine.
param([string]$MinecraftDir = (Join-Path $env:APPDATA '.minecraft'))

$ErrorActionPreference = 'Stop'
$Versao = '{{VERSAO}}'
$Mc = '{{MC}}'
$Loader = '{{LOADER}}'
$LoaderId = "fabric-loader-$Loader-$Mc"
$Aqui = $PSScriptRoot

function Falar([string]$texto, [string]$cor = 'White') { Write-Host $texto -ForegroundColor $cor }
function Parar([string]$texto) { Falar ''; Falar "ERRO: $texto" 'Red'; Falar ''; exit 1 }

Falar "=== Aquamarine $Versao - instalação ===" 'Cyan'
Falar ''
if (-not (Test-Path $MinecraftDir)) {
    Parar "Não achei a pasta do Minecraft ($MinecraftDir). Instale o Minecraft Java Edition, abra o launcher uma vez e rode este instalador de novo."
}

# O launcher regrava a lista de perfis quando fecha: com ele aberto, o perfil novo podia sumir.
$aberto = Get-Process -ErrorAction SilentlyContinue | Where-Object { $_.ProcessName -match '^(MinecraftLauncher|Minecraft)$' }
if ($aberto) {
    Falar 'O Minecraft Launcher está aberto. Feche ele (também no ícone perto do relógio) e aperte ENTER.' 'Yellow'
    Read-Host | Out-Null
}

try {
    # 1. Fabric Loader: o perfil de versão (o launcher baixa as bibliotecas dele sozinho) e um .jar vazio.
    $versaoDir = Join-Path $MinecraftDir "versions\$LoaderId"
    New-Item -ItemType Directory -Force -Path $versaoDir | Out-Null
    $perfilJson = Join-Path $versaoDir "$LoaderId.json"
    $baixou = $false
    try {
        [Net.ServicePointManager]::SecurityProtocol = [Net.ServicePointManager]::SecurityProtocol -bor [Net.SecurityProtocolType]::Tls12
        $resposta = Invoke-WebRequest -UseBasicParsing -TimeoutSec 30 -Uri "https://meta.fabricmc.net/v2/versions/loader/$Mc/$Loader/profile/json"
        $null = $resposta.Content | ConvertFrom-Json
        [IO.File]::WriteAllText($perfilJson, $resposta.Content, (New-Object Text.UTF8Encoding $false))
        $baixou = $true
    } catch {
        # Sem internet agora: usa a cópia que veio no pacote (é o mesmo arquivo).
    }
    if (-not $baixou) { Copy-Item -Force (Join-Path $Aqui 'fabric-perfil.json') $perfilJson }
    $jarVazio = Join-Path $versaoDir "$LoaderId.jar"
    if (-not (Test-Path $jarVazio)) { New-Item -ItemType File -Path $jarVazio | Out-Null }
    Falar "[1/3] Fabric Loader $Loader para o Minecraft $Mc ... ok" 'Green'

    # 2. Pasta própria do perfil, com o mod e a Fabric API (versões antigas saem).
    $jogoDir = Join-Path $MinecraftDir 'aquamarine'
    $modsDir = Join-Path $jogoDir 'mods'
    New-Item -ItemType Directory -Force -Path $modsDir | Out-Null
    Get-ChildItem -Path $modsDir -Filter '*.jar' | Where-Object { $_.Name -match '^(aquamarine|fabric-api)-' } | Remove-Item -Force
    Copy-Item -Force (Join-Path $Aqui '..\mods\*.jar') $modsDir
    Falar "[2/3] Mods copiados para $modsDir ... ok" 'Green'

    # 3. Perfil no Minecraft Launcher (cópia de segurança da lista antes de mexer).
    $perfisPath = Join-Path $MinecraftDir 'launcher_profiles.json'
    if (Test-Path $perfisPath) {
        $copia = "$perfisPath.antes-do-aquamarine"
        if (-not (Test-Path $copia)) { Copy-Item $perfisPath $copia }
        $perfis = [IO.File]::ReadAllText($perfisPath) | ConvertFrom-Json
    } else {
        $perfis = [pscustomobject]@{ profiles = [pscustomobject]@{}; version = 3 }
    }
    if (-not ($perfis.PSObject.Properties.Name -contains 'profiles') -or $null -eq $perfis.profiles) {
        $perfis | Add-Member -Force -NotePropertyName 'profiles' -NotePropertyValue ([pscustomobject]@{})
    }
    $agora = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ss.fffZ')
    $perfil = [pscustomobject][ordered]@{
        name          = "Aquamarine $Versao"
        type          = 'custom'
        created       = $agora
        lastUsed      = $agora
        lastVersionId = $LoaderId
        gameDir       = $jogoDir
        javaArgs      = '-Xmx4G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M'
        icon          = '{{ICONE}}'
    }
    $perfis.profiles | Add-Member -Force -NotePropertyName 'aquamarine' -NotePropertyValue $perfil
    [IO.File]::WriteAllText($perfisPath, ($perfis | ConvertTo-Json -Depth 64), (New-Object Text.UTF8Encoding $false))
    Falar "[3/3] Perfil 'Aquamarine $Versao' no Minecraft Launcher ... ok" 'Green'
} catch {
    Parar "A instalação parou: $($_.Exception.Message)"
}

Falar ''
Falar 'Pronto! Abra o Minecraft Launcher, escolha o perfil "Aquamarine" (ao lado do botão JOGAR) e jogue.' 'Cyan'
Falar 'Na primeira vez o launcher baixa o Fabric sozinho (precisa de internet).'
Falar ''
