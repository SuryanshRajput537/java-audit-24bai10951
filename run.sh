#!/bin/bash
# ─────────────────────────────────────────────────────────────
#  FitTrack CLI - Build & Run (Linux / macOS)
#  Usage: ./run.sh
# ─────────────────────────────────────────────────────────────

MAIN_CLASS="com.fittrack.Main"
SRC_DIR="src/main/java"
OUT_DIR="out"
LIB_DIR="lib"
SQLITE_JAR="$LIB_DIR/sqlite-jdbc.jar"
SLF4J_API="$LIB_DIR/slf4j-api.jar"
SLF4J_SIMPLE="$LIB_DIR/slf4j-simple.jar"
CP="$OUT_DIR:$SQLITE_JAR:$SLF4J_API:$SLF4J_SIMPLE"

GREEN='\033[0;32m'; RED='\033[0;31m'; CYAN='\033[0;36m'; NC='\033[0m'

echo -e "${CYAN}======================================"
echo -e "  FitTrack CLI - Build Script"
echo -e "======================================${NC}"

if ! command -v javac &> /dev/null; then
    echo -e "${RED}ERROR: javac not found. Please install JDK 17+${NC}"
    exit 1
fi
echo -e "${GREEN}Java found.${NC}"

download_jar() {
    local url=$1; local dest=$2; local name=$3
    if [ ! -f "$dest" ]; then
        echo "Downloading $name..."
        curl -fL -o "$dest" "$url" 2>/dev/null || wget -q -O "$dest" "$url" 2>/dev/null
        [ $? -ne 0 ] && echo -e "${RED}Failed to download $name${NC}" && exit 1
        echo -e "${GREEN}Downloaded $name${NC}"
    fi
}

mkdir -p "$LIB_DIR"
download_jar "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar" "$SQLITE_JAR" "sqlite-jdbc.jar"
download_jar "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar" "$SLF4J_API" "slf4j-api.jar"
download_jar "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar" "$SLF4J_SIMPLE" "slf4j-simple.jar"

echo "Building..."
mkdir -p "$OUT_DIR"
javac -cp "$SQLITE_JAR" -d "$OUT_DIR" $(find "$SRC_DIR" -name "*.java")
[ $? -ne 0 ] && echo -e "${RED}BUILD FAILED.${NC}" && exit 1
echo -e "${GREEN}Build successful!${NC}"

mkdir -p reports
echo ""
java -cp "$CP" "$MAIN_CLASS"
