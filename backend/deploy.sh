#/bin/bash
set -euo pipefail

mvn clean install -U -DskipTests

docker compose up -d --build


