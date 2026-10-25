# Servidor do Aquamarine {{VERSAO}} (Minecraft {{MC}}, Fabric Loader {{LOADER}}).
# Acha um Java 25 ou mais novo (o que o próprio Minecraft Launcher baixou também serve), pede o aceite da EULA
# na primeira vez e abre o servidor. Na primeira vez o Fabric baixa o servidor do Minecraft (precisa de internet).

$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
# Memória do servidor: 4 GB atendem uns 5 jogadores lutando com o Kraken. Mude aqui se precisar.
$Memoria = '4G'

function Falar([string]$texto, [string]$cor = 'White') { Write-Host $texto -ForegroundColor $cor }
function Parar([string]$texto) { Falar ''; Falar "ERRO: $texto" 'Red'; Falar ''; exit 1 }

function VersaoJava([string]$exe) {
    try {
        $saida = (& $exe -version 2>&1 | Out-String)
        if ($saida -match 'version "(\d+)') { return [int]$Matches[1] }
    } catch { }
    return 0
}

Falar '=== Servidor Aquamarine {{VERSAO}} ===' 'Cyan'
Falar ''

# 1. Java: o do sistema, o do JAVA_HOME ou os que o Minecraft Launcher instala (fica com o mais novo).
$candidatos = New-Object System.Collections.Generic.List[string]
$naPath = Get-Command java -ErrorAction SilentlyContinue
if ($naPath) { $candidatos.Add($naPath.Source) }
if ($env:JAVA_HOME) { $candidatos.Add((Join-Path $env:JAVA_HOME 'bin\java.exe')) }
$padroes = @(
    "$env:APPDATA\.minecraft\runtime\*\windows-x64\*\bin\java.exe",
    "$env:LOCALAPPDATA\Packages\Microsoft.4297127D64EC6_8wekyb3d8bbwe\LocalCache\Local\runtime\*\windows-x64\*\bin\java.exe",
    "${env:ProgramFiles(x86)}\Minecraft Launcher\runtime\*\windows-x64\*\bin\java.exe",
    "$env:ProgramFiles\Java\*\bin\java.exe",
    "$env:ProgramFiles\Eclipse Adoptium\*\bin\java.exe",
    "$env:ProgramFiles\Microsoft\jdk-*\bin\java.exe",
    "$env:ProgramFiles\Zulu\*\bin\java.exe"
)
foreach ($padrao in $padroes) {
    Get-Item -Path $padrao -ErrorAction SilentlyContinue | ForEach-Object { $candidatos.Add($_.FullName) }
}
$java = $null
$melhor = 0
foreach ($c in ($candidatos | Select-Object -Unique)) {
    if (Test-Path -LiteralPath $c) {
        $v = VersaoJava $c
        if ($v -gt $melhor) { $melhor = $v; $java = $c }
    }
}
if ($melhor -lt 25) {
    Parar 'O servidor precisa do Java 25 ou mais novo. Jogue o Minecraft {{MC}} uma vez pelo launcher (ele baixa o Java certo) ou instale o Java 25 em https://adoptium.net e rode de novo.'
}
Falar "Java $melhor encontrado: $java" 'Green'

# 2. EULA da Mojang: só uma vez.
$eula = Join-Path $PSScriptRoot 'eula.txt'
$aceita = (Test-Path $eula) -and ((Get-Content -LiteralPath $eula -Raw) -match 'eula\s*=\s*true')
if (-not $aceita) {
    Falar ''
    Falar 'Para abrir um servidor, a Mojang pede que você aceite a EULA do Minecraft:' 'Yellow'
    Falar '    https://aka.ms/MinecraftEULA' 'Yellow'
    $r = Read-Host 'Você aceita a EULA? (S/N)'
    if ($r -notmatch '^[sSyY]') { Parar 'Sem aceitar a EULA o servidor não abre.' }
    Set-Content -LiteralPath $eula -Value 'eula=true' -Encoding ASCII
}

# 3. Mods: os do pacote (pasta mods ao lado), trocando versões antigas.
$modsPacote = Join-Path $PSScriptRoot '..\mods'
$modsServidor = Join-Path $PSScriptRoot 'mods'
New-Item -ItemType Directory -Force -Path $modsServidor | Out-Null
if (Test-Path $modsPacote) {
    Get-ChildItem -Path $modsServidor -Filter '*.jar' | Where-Object { $_.Name -match '^(aquamarine|fabric-api)-' } | Remove-Item -Force
    Copy-Item -Force (Join-Path $modsPacote '*.jar') $modsServidor
}
if (-not (Get-ChildItem -Path $modsServidor -Filter 'aquamarine-*.jar')) { Parar 'Não achei o mod na pasta mods. Use a pasta Servidor dentro do pacote do Aquamarine, sem tirar ela de lá.' }

# 4. Como os amigos entram.
$ips = @(Get-NetIPAddress -AddressFamily IPv4 -ErrorAction SilentlyContinue |
    Where-Object { $_.IPAddress -notmatch '^(127\.|169\.254\.)' } | Select-Object -ExpandProperty IPAddress)
Falar ''
if ($ips.Count -gt 0) { Falar "Amigos na mesma rede (Wi-Fi/cabo) entram pelo Multijogador em: $($ips -join '  ou  ')" 'Cyan' }
Falar 'Amigos pela internet: veja "Jogar pela internet" no LEIA-ME-SERVIDOR.txt.' 'Cyan'
Falar 'Quando aparecer "Done", o servidor está pronto. Para usar os comandos do mod, digite nesta janela:  op SEU_NOME' 'Cyan'
Falar 'Para desligar com segurança, digite:  stop' 'Cyan'
Falar ''

& $java "-Xms1G" "-Xmx$Memoria" -jar fabric-server-launch.jar nogui
