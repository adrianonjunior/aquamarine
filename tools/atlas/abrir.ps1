param([string]$AudioRoot = '', [int]$Port = 8766, [switch]$SemAbrir)
$ErrorActionPreference = 'Stop'
$atlasScriptDirectory = $PSScriptRoot
$atlasRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$atlasPython = if (Test-Path 'C:/Python314/python.exe') { 'C:/Python314/python.exe' } else { (Get-Command python.exe).Source }
if (!(Test-Path (Join-Path $PSScriptRoot 'node_modules/three/package.json'))) {
    & npm ci --prefix $atlasScriptDirectory --no-audit --no-fund
    if ($LASTEXITCODE -ne 0) { throw 'Não foi possível instalar as dependências do Atlas.' }
}
# O áudio gerado (build/audio) mora na própria pasta do projeto desde 26/10/2026; -AudioRoot lê outra pasta de áudio.
$atlasBuildArgs = @((Join-Path $PSScriptRoot 'build.py'))
if ($AudioRoot) { $atlasBuildArgs += @('--audio-root', (Resolve-Path $AudioRoot).Path) }
& $atlasPython @atlasBuildArgs
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar o Atlas.' }
$atlasRunning = $false
$atlasStatus = $null
try {
    $atlasStatus = Invoke-RestMethod "http://127.0.0.1:$Port/api/status" -TimeoutSec 2
} catch { }
if ($atlasStatus) {
    $atlasRunning = $atlasStatus.app -eq 'aquamarine-atlas' -and $atlasStatus.root -eq $atlasRoot
    if (!$atlasRunning) { throw 'A porta está sendo usada por outro servidor. Use -Port 8767.' }
}
if (!$atlasRunning) {
    $atlasServerArgs = @(('"' + (Join-Path $atlasScriptDirectory 'serve.py') + '"'), '--port', "$Port")
    if ($AudioRoot) { $atlasServerArgs += @('--audio-root', ('"' + (Resolve-Path $AudioRoot).Path + '"')) }
    $atlasProcess = Start-Process -FilePath $atlasPython -ArgumentList $atlasServerArgs -WindowStyle Hidden -PassThru -WorkingDirectory $atlasRoot -RedirectStandardOutput (Join-Path $atlasRoot 'build/atlas-server.log') -RedirectStandardError (Join-Path $atlasRoot 'build/atlas-server.err.log')
    $atlasProcess.Id | Set-Content (Join-Path $atlasRoot 'build/atlas-server.pid')
    $atlasReady = $false
    for ($attempt=0; $attempt -lt 20; $attempt++) {
        try { $null = Invoke-RestMethod "http://127.0.0.1:$Port/api/status" -TimeoutSec 1; $atlasReady=$true; break } catch { Start-Sleep -Milliseconds 200 }
    }
    if (!$atlasReady) { throw 'O servidor não iniciou; veja build/atlas-server.err.log.' }
}
$atlasPublicRunning = $false
try {
    $atlasPublicResponse = Invoke-WebRequest "http://127.0.0.1:8767" -TimeoutSec 2
    $atlasPublicRunning = $atlasPublicResponse.Content.Contains('Observatório de criaturas')
    if (!$atlasPublicRunning) { throw 'A porta 8767 está sendo usada por outro site.' }
} catch {
    if ($_.Exception.Message -like '*está sendo usada*') { throw }
}
if (!$atlasPublicRunning) {
    $atlasPublicArgs = @(('"' + (Join-Path $atlasScriptDirectory 'serve_public.py') + '"'), '--port', '8767', '--directory', ('"' + (Join-Path $atlasRoot 'build/atlas') + '"'))
    $atlasPublicProcess = Start-Process -FilePath $atlasPython -ArgumentList $atlasPublicArgs -WindowStyle Hidden -PassThru -WorkingDirectory $atlasRoot -RedirectStandardOutput (Join-Path $atlasRoot 'build/atlas-public.log') -RedirectStandardError (Join-Path $atlasRoot 'build/atlas-public.err.log')
    $atlasPublicProcess.Id | Set-Content (Join-Path $atlasRoot 'build/atlas-public.pid')
    $atlasPublicReady = $false
    for ($attempt=0; $attempt -lt 20; $attempt++) {
        try { $publicCheck = Invoke-WebRequest "http://127.0.0.1:8767" -TimeoutSec 1; if ($publicCheck.Content.Contains('Observatório de criaturas')) { $atlasPublicReady=$true; break } } catch { Start-Sleep -Milliseconds 200 }
    }
    if (!$atlasPublicReady) { throw 'O servidor público estático não iniciou; veja build/atlas-public.err.log.' }
}
$atlasUrl = "http://127.0.0.1:$Port"
Write-Output $atlasUrl
if (!$SemAbrir) { Start-Process $atlasUrl }
