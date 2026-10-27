param([string]$Only = 'industry3', [switch]$Build)
$ErrorActionPreference = 'Continue'
$env:JAVA_HOME = 'C:/Program Files/Microsoft/jdk-25.0.4.101-hotspot'
$env:GRADLE_USER_HOME = 'C:/Users/adria/.gradle'
$env:AQUAMARINE_GAMETEST_ONLY = $Only
$taskLog = Join-Path $PSScriptRoot ('../build/check-' + ($Only -replace '[^a-zA-Z0-9_-]', '_') + '.log')
[string[]]$tasks = if ($Build) { 'build'; 'compileGametestJava' } else { 'runClientGameTest' }
& "$env:JAVA_HOME/bin/java.exe" -classpath gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain @tasks --console=plain *> $taskLog
$taskExit = $LASTEXITCODE
Get-Content -LiteralPath $taskLog -Tail 35
exit $taskExit
