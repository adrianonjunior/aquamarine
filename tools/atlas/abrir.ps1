param([string]$AudioRoot = '', [int]$Port = 8766, [switch]$SemAbrir)
$ErrorActionPreference = 'Stop'
$atlasScriptDirectory = $PSScriptRoot
$atlasRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$atlasPython = if (Test-Path 'C:/Python314/python.exe') { 'C:/Python314/python.exe' } else { (Get-Command python.exe).Source }
if (!(Test-Path (Join-Path $PSScriptRoot 'node_modules/three/package.json'))) {
    & npm ci --prefix $atlasScriptDirectory --no-audit --no-fund
    if ($LASTEXITCODE -ne 0) { throw 'Não foi possível instalar as dependências do Atlas.' }
}
# Importação explícita da worktree de áudio conhecida, quando ela ainda está em produção.
if (!$AudioRoot) {
    $audioCandidate = Join-Path (Split-Path $atlasRoot) 'aquamarine-claude-audio'
    if (Test-Path (Join-Path $audioCandidate 'tools/audio/manifest.json')) { $AudioRoot = $audioCandidate }
}
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
$atlasUrl = "http://127.0.0.1:$Port"
Write-Output $atlasUrl
if (!$SemAbrir) { Start-Process $atlasUrl }
