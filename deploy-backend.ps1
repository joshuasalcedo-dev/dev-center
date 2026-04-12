# ============================================================
# deploy-backend.ps1
# Deploys commandcenter remote-server stack to the VM
# Run from repo root: .\deploy-backend.ps1
# Optional: .\deploy-backend.ps1 -Environment dev
# ============================================================

param(
    [ValidateSet("prod", "dev")]
    [string]$Environment = "prod"
)

# --- Configuration ---
$RemoteHost  = "cmdcenter"
$RemoteDir   = "/opt/commandcenter"
$ComposeFile = "docker-compose.$Environment.yml"
$LocalBackendDir = "backend"
$LocalComposeFile = "$LocalBackendDir/remote-server/$ComposeFile"
$LocalDockerfile  = "$LocalBackendDir/remote-server/Dockerfile"
$LocalEnvFile     = "$LocalBackendDir/remote-server/.env"

# --- Preflight checks ---
Write-Host "=== Deploying remote-server ($Environment) to $RemoteHost ===" -ForegroundColor Cyan

if (-not (Test-Path $LocalComposeFile)) {
    Write-Error "Compose file not found: $LocalComposeFile"
    exit 1
}
if (-not (Test-Path $LocalEnvFile)) {
    Write-Error ".env file not found: $LocalEnvFile"
    exit 1
}

# --- Step 1: Build the JAR locally ---
Write-Host "`n[1/5] Building backend JAR..." -ForegroundColor Yellow
Push-Location $LocalBackendDir
try {
    mvn package -pl remote-server -am -DskipTests -q
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Maven build failed"
        exit 1
    }
} finally {
    Pop-Location
}
Write-Host "  JAR built successfully" -ForegroundColor Green

# --- Step 2: Ensure remote directory structure ---
Write-Host "`n[2/5] Preparing remote directory..." -ForegroundColor Yellow
ssh $RemoteHost "mkdir -p $RemoteDir/backend/common $RemoteDir/backend/local-server $RemoteDir/backend/remote-server"

# --- Step 3: Sync files to remote ---
Write-Host "`n[3/5] Syncing files to $RemoteHost..." -ForegroundColor Yellow

# Copy compose file, Dockerfile, .env
scp $LocalComposeFile "${RemoteHost}:${RemoteDir}/docker-compose.yml"
scp $LocalDockerfile "${RemoteHost}:${RemoteDir}/backend/remote-server/Dockerfile"
scp $LocalEnvFile "${RemoteHost}:${RemoteDir}/.env"

# Copy pom files (needed for Docker build context)
scp "$LocalBackendDir/pom.xml" "${RemoteHost}:${RemoteDir}/backend/pom.xml"
scp "$LocalBackendDir/common/pom.xml" "${RemoteHost}:${RemoteDir}/backend/common/pom.xml"
scp "$LocalBackendDir/local-server/pom.xml" "${RemoteHost}:${RemoteDir}/backend/local-server/pom.xml"
scp "$LocalBackendDir/remote-server/pom.xml" "${RemoteHost}:${RemoteDir}/backend/remote-server/pom.xml"

# Sync source code and Maven wrapper
scp -r "$LocalBackendDir/common/src" "${RemoteHost}:${RemoteDir}/backend/common/"
scp -r "$LocalBackendDir/remote-server/src" "${RemoteHost}:${RemoteDir}/backend/remote-server/"
scp -r "$LocalBackendDir/.mvn" "${RemoteHost}:${RemoteDir}/backend/"
scp "$LocalBackendDir/mvnw" "${RemoteHost}:${RemoteDir}/backend/"

Write-Host "  Files synced" -ForegroundColor Green

# --- Step 4: Build and deploy on remote ---
Write-Host "`n[4/5] Building Docker image and starting services..." -ForegroundColor Yellow
ssh $RemoteHost @"
cd $RemoteDir
docker compose --env-file .env -f docker-compose.yml build --no-cache app
docker compose --env-file .env -f docker-compose.yml up -d
"@

if ($LASTEXITCODE -ne 0) {
    Write-Error "Remote deployment failed"
    exit 1
}
Write-Host "  Services started" -ForegroundColor Green

# --- Step 5: Health check ---
Write-Host "`n[5/5] Waiting for health check..." -ForegroundColor Yellow
$maxRetries = 15
$retryCount = 0
do {
    Start-Sleep -Seconds 5
    $retryCount++
    $health = ssh $RemoteHost "curl -sf http://localhost:8080/actuator/health 2>/dev/null"
    if ($health -match '"status":"UP"') {
        Write-Host "  Server is healthy!" -ForegroundColor Green
        break
    }
    Write-Host "  Waiting... ($retryCount/$maxRetries)" -ForegroundColor Gray
} while ($retryCount -lt $maxRetries)

if ($retryCount -ge $maxRetries) {
    Write-Warning "Health check timed out. Check logs with: ssh $RemoteHost 'docker compose -f $RemoteDir/docker-compose.yml logs app'"
}

# --- Done ---
Write-Host "`n=== Deployment complete ===" -ForegroundColor Cyan
Write-Host "  Remote: $RemoteHost:$RemoteDir"
Write-Host "  Compose: $ComposeFile"
Write-Host "  Logs:    ssh $RemoteHost 'docker compose -f $RemoteDir/docker-compose.yml logs -f app'"
