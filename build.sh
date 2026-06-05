#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

echo "Cleaning previous build..."
rm -rf build
mkdir -p build

echo "Compiling sources..."
javac -d build src/objectville/*.java

echo "Packaging ObjectVilleGame.jar..."
jar cfe ObjectVilleGame.jar objectville.Main -C build .

echo "Done. Run with:"
echo "  java -jar ObjectVilleGame.jar sample_map.txt 10"
