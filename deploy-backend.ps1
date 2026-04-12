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

Write-Host "=== Deploying remote-server ($Environment) to $RemoteHost ===" -ForegroundColor Cyan
Write-Host "Running deploy.sh on remote..." -ForegroundColor Yellow

ssh $RemoteHost "bash /home/joshuasalcedo/dev-center/backend/deploy.sh $Environment"

if ($LASTEXITCODE -ne 0) {
    Write-Error "Remote deployment failed"
    exit 1
}

Write-Host "`n=== Done ===" -ForegroundColor Cyan
Write-Host "  Logs: ssh ${RemoteHost} 'sudo docker compose -f /opt/commandcenter/docker-compose.yml logs -f app'"
