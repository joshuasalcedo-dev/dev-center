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

$RemoteHost = "cmdcenter"
$RepoDir = "/home/joshuasalcedo/dev-center"

Write-Host "=== Deploying remote-server ($Environment) to $RemoteHost ===" -ForegroundColor Cyan

# --- Step 1: Pull latest on remote first ---
Write-Host "`n[1/2] Pulling latest code on remote..." -ForegroundColor Yellow
ssh $RemoteHost "cd $RepoDir && git pull origin main"

if ($LASTEXITCODE -ne 0) {
    Write-Error "git pull failed on remote"
    exit 1
}

# --- Step 2: Run deploy script ---
Write-Host "`n[2/2] Running deploy.sh on remote..." -ForegroundColor Yellow
ssh $RemoteHost "cd $RepoDir/backend && bash deploy.sh $Environment"

if ($LASTEXITCODE -ne 0) {
    Write-Error "Remote deployment failed"
    exit 1
}

Write-Host "`n=== Done ===" -ForegroundColor Cyan
Write-Host "  Logs: ssh ${RemoteHost} 'sudo docker compose -f /opt/commandcenter/docker-compose.yml logs -f app'"
