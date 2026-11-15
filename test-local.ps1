param([string]$JavaHome = 'C:\Program Files\Java\jdk-24')
$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'build-local.ps1') -Project 'build.xml'
$jar = Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot './dist') -Filter '*.jar' | Select-Object -First 1 -ExpandProperty FullName
if (-not $jar) { throw 'No se encontró el JAR compilado' }
$out = Join-Path $PSScriptRoot ('.local-build/test-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $out -Force | Out-Null
$classpath = $jar + ';' + (Join-Path (Split-Path $jar) 'lib/*')
& (Join-Path $JavaHome 'bin/javac.exe') -encoding UTF-8 -cp $classpath -d $out (Join-Path $PSScriptRoot 'tests/SmokeBiblioteca.java')
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
& (Join-Path $JavaHome 'bin/java.exe') -ea -cp ($out + ';' + $classpath) SmokeBiblioteca (Join-Path $out 'data')
exit $LASTEXITCODE
