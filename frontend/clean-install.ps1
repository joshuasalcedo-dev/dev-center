# Clean install script for the frontend monorepo
Write-Host "Cleaning node_modules and lockfile..." -ForegroundColor Cyan

# Remove all node_modules
Get-ChildItem -Path . -Filter "node_modules" -Recurse -Directory -Force | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "bun.lock" -Force -ErrorAction SilentlyContinue

Write-Host "Installing dependencies..." -ForegroundColor Cyan
bun install

Write-Host "Done." -ForegroundColor Green
