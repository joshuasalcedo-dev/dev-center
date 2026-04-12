#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="/home/joshuasalcedo/dev-center"
DEPLOY_DIR="/opt/commandcenter"
ENVIRONMENT="${1:-prod}"
COMPOSE_FILE="docker-compose.${ENVIRONMENT}.yml"

echo "=== Deploying remote-server (${ENVIRONMENT}) ==="

# --- Step 1: Set up deploy directory ---
echo -e "\n[1/3] Setting up ${DEPLOY_DIR}..."
sudo mkdir -p "$DEPLOY_DIR/backend"

# Symlink backend into deploy dir so docker-compose context works
sudo ln -sfn "$REPO_DIR/backend" "$DEPLOY_DIR/backend"

# Copy compose file
sudo cp "$REPO_DIR/backend/remote-server/$COMPOSE_FILE" "$DEPLOY_DIR/docker-compose.yml"

# --- Step 2: Build and start ---
echo -e "\n[2/3] Building and starting services..."
cd "$DEPLOY_DIR"
sudo docker builder prune -af 2>/dev/null || true
sudo docker compose -f docker-compose.yml build --no-cache app
sudo docker compose -f docker-compose.yml up -d

# --- Step 3: Health check ---
echo -e "\n[3/3] Waiting for health check..."
max_retries=15
retry=0
while [ $retry -lt $max_retries ]; do
    sleep 5
    retry=$((retry + 1))
    if curl -sf http://localhost:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
        echo "  Server is healthy!"
        break
    fi
    echo "  Waiting... ($retry/$max_retries)"
done

if [ $retry -ge $max_retries ]; then
    echo "WARNING: Health check timed out. Check logs:"
    echo "  sudo docker compose -f $DEPLOY_DIR/docker-compose.yml logs app"
fi

echo -e "\n=== Deployment complete ==="
echo "  Logs: sudo docker compose -f $DEPLOY_DIR/docker-compose.yml logs -f app"
