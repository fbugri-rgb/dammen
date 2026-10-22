#!/usr/bin/env bash
# ---------------------------------------------------------------
#  Maakt een kant-en-klaar programma van het damspel voor het
#  besturingssysteem waarop dit script draait, met Java erin.
#  Spelers hoeven dus zelf niets te installeren.
#
#    Windows -> target/dist/Dammen-windows.zip  (map met Dammen.exe)
#    macOS   -> target/dist/Dammen-macos.dmg    (Dammen.app)
#    Linux   -> target/dist/Dammen-linux.tar.gz (map met bin/Dammen)
#
#  Nodig: JDK 21 (met jpackage). Gebruik: bash scripts/package.sh
#  GitHub Actions voert dit uit op alle drie, zie .github/workflows/builds.yml
# ---------------------------------------------------------------
set -euo pipefail
cd "$(dirname "$0")/.."

case "$(uname -s)" in
    MINGW* | MSYS* | CYGWIN*) os=windows; mvn=./mvnw.cmd ;;
    Darwin) os=macos; mvn=./mvnw ;;
    *) os=linux; mvn=./mvnw ;;
esac

jdk_bin="${JAVA_HOME:+$JAVA_HOME/bin/}"
version=1.0.0

echo "Jar bouwen met JavaFX voor $os..."
"$mvn" -B -q -DskipTests clean package

input=target/jpackage-input
dist=target/dist
rm -rf "$input" "$dist"
mkdir -p "$input" "$dist"
cp target/dammen.jar "$input/"

# Enkel de Java-modules meenemen die het spel echt gebruikt, dat houdt het klein.
modules=$("${jdk_bin}jdeps" --print-module-deps --ignore-missing-deps \
    --multi-release 21 target/dammen.jar)
echo "Java-modules: $modules"

type=app-image
[ "$os" = macos ] && type=dmg

"${jdk_bin}jpackage" \
    --type "$type" \
    --name Dammen \
    --app-version "$version" \
    --vendor "Farok Bugri" \
    --description "Damspel op een bord van 10 op 10" \
    --input "$input" \
    --main-jar dammen.jar \
    --main-class be.kdg.dammen.Launcher \
    --add-modules "$modules" \
    --java-options "-Ddammen.data.dir=~/Dammen" \
    --dest "$dist"

case "$os" in
    windows)
        powershell -NoProfile -Command \
            "Compress-Archive -Path '$dist/Dammen' -DestinationPath '$dist/Dammen-windows.zip' -Force"
        ;;
    macos)
        mv "$dist"/Dammen-*.dmg "$dist/Dammen-macos.dmg"
        ;;
    linux)
        tar -czf "$dist/Dammen-linux.tar.gz" -C "$dist" Dammen
        ;;
esac

echo "Klaar:"
ls -lh "$dist"
