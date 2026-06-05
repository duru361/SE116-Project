#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

MAP="${1:-sample_map.txt}"
TICKS="${2:-10}"

if [ ! -f ObjectVilleGame.jar ]; then
  echo "Jar not found; building first..."
  ./build.sh
fi

java -jar ObjectVilleGame.jar "$MAP" "$TICKS"
