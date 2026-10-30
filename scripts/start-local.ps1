param([Parameter(Mandatory = $true)][ValidateSet('backend', 'frontend')][string]$Component)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$localDirectory = Join-Path $projectRoot '.local'
New-Item -ItemType Directory -Force -Path $localDirectory | Out-Null

try {
    $port = if ($Component -eq 'backend') { 8080 } else { 5173 }
    $listener = Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($listener) {
        Write-Host "A porta $port ja esta em uso pelo processo $($listener.OwningProcess)."
        Write-Host 'Nenhuma segunda instancia foi iniciada, para evitar conflito de porta ou banco.'
        Write-Host 'Se o iStock ja esta rodando, acesse http://127.0.0.1:5173'
        Write-Host 'Para reiniciar, encerre a instancia anterior antes de abrir este arquivo novamente.'
        exit 0
    }
    if ($Component -eq 'frontend') {
        if (-not (Get-Command npm.cmd -ErrorAction SilentlyContinue)) {
            throw 'Instale Node.js 24 e abra um novo terminal.'
        }
        Set-Location (Join-Path $projectRoot 'frontend')
        # Usa as autoridades certificadoras do Windows, sem desabilitar TLS.
        $env:NODE_OPTIONS = "$env:NODE_OPTIONS --use-system-ca".Trim()
        if (-not (Test-Path 'node_modules/.bin/vite.cmd')) {
            & npm.cmd ci --no-audit --no-fund
            if ($LASTEXITCODE -ne 0) { throw 'Falha ao instalar dependencias do frontend.' }
        }
        Write-Host 'Frontend: http://127.0.0.1:5173 (mantenha esta janela aberta)'
        & npm.cmd run dev -- --host 127.0.0.1 --port 5173 --strictPort
    } else {
        $javaHomeFile = Join-Path $localDirectory 'java-home.txt'
        $candidates = @($env:JAVA_HOME)
        if (Test-Path $javaHomeFile) { $candidates += (Get-Content $javaHomeFile -Raw).Trim() }
        $compiler = Get-Command javac.exe -ErrorAction SilentlyContinue
        if ($compiler) { $candidates += Split-Path -Parent (Split-Path -Parent $compiler.Source) }
        $jdk = $null
        foreach ($candidate in $candidates) {
            if (-not $candidate -or -not (Test-Path (Join-Path $candidate 'bin/javac.exe'))) { continue }
            $version = & (Join-Path $candidate 'bin/javac.exe') -version 2>&1
            if ("$version" -match 'javac (\d+)' -and [int]$Matches[1] -ge 21) { $jdk = $candidate; break }
        }
        if (-not $jdk) {
            throw 'JDK 21 nao encontrado. Configure JAVA_HOME ou salve o caminho do JDK em .local/java-home.txt.'
        }
        $env:JAVA_HOME = $jdk
        $env:Path = "$(Join-Path $jdk 'bin');$env:Path"
        $env:MAVEN_OPTS = "$env:MAVEN_OPTS -Djavax.net.ssl.trustStoreType=Windows-ROOT -Djavax.net.ssl.trustStore=NUL".Trim()
        $secretFile = Join-Path $localDirectory 'jwt-secret.txt'
        if (-not (Test-Path $secretFile)) {
            $bytes = New-Object byte[] 32
            $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
            try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
            [System.IO.File]::WriteAllText($secretFile, [Convert]::ToBase64String($bytes))
        }
        $env:JWT_SECRET = (Get-Content $secretFile -Raw).Trim()
        Set-Location (Join-Path $projectRoot 'backend')
        Write-Host "JDK: $jdk"
        Write-Host 'Backend: http://127.0.0.1:8080 - banco local persistente (mantenha esta janela aberta)'
        # Usa diretorio local para sockets, evitando o caminho temporario abreviado do Windows.
        & .\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=standalone' '-Dspring-boot.run.arguments=--seed' '-Dspring-boot.run.jvmArguments=-Djdk.net.unixdomain.tmpdir=.local -Dspring.devtools.restart.enabled=false'
    }
    if ($LASTEXITCODE -ne 0) { throw "Inicializacao falhou (codigo $LASTEXITCODE). Veja o erro acima." }
} catch {
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
