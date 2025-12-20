set -e

# Build internal mod
echo "Building internal mod..."
cd mod

chmod +x ./gradlew
./gradlew clean runDatagen build

# Build pack
echo "Building pack..."
cd ..

rm -rf build
mkdir -p build
cp -r config mods index.toml pack.toml build
cp mod/build/libs/* build/mods

cd build
mkdir -p ../out
packwiz modrinth export -o ../out/holiday-server-pack.mrpack
packwiz refresh --build
