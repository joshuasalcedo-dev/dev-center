param(
    [Parameter(Position = 0)]
    [ValidateSet("dev", "prod")]
    [string]$Mode = "dev"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
$SidecarDir = "$ProjectRoot\src-tauri\binaries"
$OpenApiSpec = "$SidecarDir\openapi.json"

# --- Ensure binaries dir ---
if (!(Test-Path $SidecarDir)) {
    New-Item -ItemType Directory -Path $SidecarDir | Out-Null
}

if ($Mode -eq "dev") {
    # =====================
    #  DEV: build JAR, start with maven, run tauri
    # =====================

    # 1. Build + start backend (non-blocking via spring-boot:start)
    Write-Host "`n=== [dev] Building + starting Spring Boot ===" -ForegroundColor Cyan
    & mvn package spring-boot:start -Pdev -DskipTests -f "$ProjectRoot\pom.xml"
    if ($LASTEXITCODE -ne 0) { Write-Host "Maven build/start failed!" -ForegroundColor Red; exit 1 }
    Write-Host "Backend running on http://localhost:5050" -ForegroundColor Green

    try {
        # 2. Generate OpenAPI spec (backend is already running on :5050)
        Write-Host "`n=== [dev] Generating OpenAPI spec ===" -ForegroundColor Cyan
        $timeout = 30; $elapsed = 0
        while (-not (Test-Path $OpenApiSpec) -and $elapsed -lt $timeout) {
            Start-Sleep -Seconds 1
            $elapsed++
        }
        if (!(Test-Path $OpenApiSpec)) {
            Write-Host "Timed out waiting for OpenAPI spec!" -ForegroundColor Red; exit 1
        }
        Write-Host "OpenAPI spec written to $OpenApiSpec" -ForegroundColor Green

        # 3. Generate TypeScript API client
        Write-Host "`n=== [dev] Generating TypeScript API client ===" -ForegroundColor Cyan
        Set-Location $ProjectRoot
        & bun run generate-api
        if ($LASTEXITCODE -ne 0) { Write-Host "API codegen failed!" -ForegroundColor Red; exit 1 }
        Write-Host "TypeScript client generated at src/lib/api" -ForegroundColor Green

        # 4. Run Tauri dev
        Write-Host "`n=== [dev] Starting Tauri dev ===" -ForegroundColor Cyan
        & bun tauri dev
    } finally {
        # Stop backend via spring-boot:stop
        Write-Host "`nStopping backend..." -ForegroundColor Yellow
        & mvn spring-boot:stop -f "$ProjectRoot\pom.xml" 2>$null
    }

} else {
    # =====================
    #  PROD: native build + Tauri bundle
    # =====================

    # 1. Native compile + copy to sidecar binaries
    Write-Host "`n=== [prod] Native compiling Spring Boot ===" -ForegroundColor Cyan
    & mvn "-Pnative,tauri-sidecar" native:compile package -DskipTests -f "$ProjectRoot\pom.xml"
    if ($LASTEXITCODE -ne 0) { Write-Host "Native build failed!" -ForegroundColor Red; exit 1 }
    Write-Host "Native binary copied to $SidecarDir" -ForegroundColor Green

    # 2. Boot native binary briefly to generate OpenAPI spec
    Write-Host "`n=== [prod] Generating OpenAPI spec ===" -ForegroundColor Cyan
    if (Test-Path $OpenApiSpec) { Remove-Item $OpenApiSpec }
    $Triple = (rustc -vV | Select-String "host:").ToString().Split(": ")[1].Trim()
    $Ext = if ($Triple -match "windows") { ".exe" } else { "" }
    $NativeBin = "$SidecarDir\devcom-server-$Triple$Ext"
    $BackendProc = Start-Process -FilePath $NativeBin -ArgumentList "--spring.profiles.active=prod" -PassThru -NoNewWindow
    $timeout = 30; $elapsed = 0
    while (-not (Test-Path $OpenApiSpec) -and $elapsed -lt $timeout) {
        Start-Sleep -Seconds 1
        $elapsed++
    }
    Stop-Process -Id $BackendProc.Id -Force -ErrorAction SilentlyContinue
    if (!(Test-Path $OpenApiSpec)) {
        Write-Host "Timed out waiting for OpenAPI spec!" -ForegroundColor Red; exit 1
    }
    Write-Host "OpenAPI spec written to $OpenApiSpec" -ForegroundColor Green

    # 3. Generate TypeScript API client
    Write-Host "`n=== [prod] Generating TypeScript API client ===" -ForegroundColor Cyan
    Set-Location $ProjectRoot
    & bun run generate-api
    if ($LASTEXITCODE -ne 0) { Write-Host "API codegen failed!" -ForegroundColor Red; exit 1 }
    Write-Host "TypeScript client generated at src/lib/api" -ForegroundColor Green

    # 4. Build Tauri app bundle
    Write-Host "`n=== [prod] Building Tauri bundle ===" -ForegroundColor Cyan
    & bun tauri build
    if ($LASTEXITCODE -ne 0) { Write-Host "Tauri build failed!" -ForegroundColor Red; exit 1 }
    Write-Host "`n=== DONE ===" -ForegroundColor Green
}
