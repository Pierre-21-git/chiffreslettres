#!/usr/bin/env bash
# Préparation hors-application du dictionnaire français pour le mode Lettres (spec §4.3).
#
# Source : dictionnaire Hunspell/Dicollecte français ("classique" v7.7), licence MPL-2.0
# (voir /usr/share/doc/hunspell-fr/copyright). Génère une liste plate de formes fléchies
# à partir des fichiers .dic/.aff, à embarquer plus tard comme asset Android.
#
# Deux étapes : `unmunch` développe grossièrement les formes, puis un script Python
# (clean_dictionary.py) s'appuyant sur `spylls` (réimplémentation Python fidèle de
# Hunspell) revérifie chaque candidat, car `unmunch` ne résout pas correctement les
# classes de continuation imbriquées utilisées par ce dictionnaire pour les
# conjugaisons (voir commentaire en tête de clean_dictionary.py).
#
# Usage: ./scripts/prepare_dictionary.sh [dic_file] [aff_file]

set -euo pipefail

DIC_FILE="${1:-/usr/share/hunspell/fr_FR.dic}"
AFF_FILE="${2:-/usr/share/hunspell/fr_FR.aff}"
DIC_BASE="${DIC_FILE%.dic}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOLS_DIR="$SCRIPT_DIR/.tools"
OUTPUT_DIR="$SCRIPT_DIR/output"
mkdir -p "$TOOLS_DIR" "$OUTPUT_DIR"

if [ ! -f "$DIC_FILE" ] || [ ! -f "$AFF_FILE" ]; then
    echo "Fichiers dictionnaire introuvables : $DIC_FILE / $AFF_FILE" >&2
    echo "Installez le paquet 'hunspell-fr' ou passez les chemins en argument." >&2
    exit 1
fi

# --- 1. Localiser (ou récupérer sans sudo) le binaire unmunch ---
if command -v unmunch >/dev/null 2>&1; then
    UNMUNCH="$(command -v unmunch)"
elif [ -x "$TOOLS_DIR/usr/bin/unmunch" ]; then
    UNMUNCH="$TOOLS_DIR/usr/bin/unmunch"
else
    echo "unmunch introuvable : téléchargement du paquet hunspell-tools (sans sudo)..." >&2
    ( cd "$TOOLS_DIR" && apt-get download hunspell-tools ) || {
        echo "Échec du téléchargement. Installez manuellement : sudo apt install hunspell-tools" >&2
        exit 1
    }
    dpkg-deb -x "$TOOLS_DIR"/hunspell-tools_*.deb "$TOOLS_DIR"
    rm -f "$TOOLS_DIR"/hunspell-tools_*.deb
    UNMUNCH="$TOOLS_DIR/usr/bin/unmunch"
fi

# --- 2. Localiser (ou récupérer sans sudo) pip puis spylls ---
PYLIBS_DIR="$TOOLS_DIR/pylibs"
if ! PYTHONPATH="$PYLIBS_DIR" python3 -c "import spylls" >/dev/null 2>&1; then
    echo "spylls introuvable : mise en place d'un pip local (sans sudo)..." >&2
    PIP_EXTRACT_DIR="$TOOLS_DIR/pip-extract"
    if ! PYTHONPATH="$PIP_EXTRACT_DIR/usr/lib/python3/dist-packages" python3 -m pip --version >/dev/null 2>&1; then
        mkdir -p "$PIP_EXTRACT_DIR"
        ( cd "$PIP_EXTRACT_DIR" && apt-get download python3-pip )
        dpkg-deb -x "$PIP_EXTRACT_DIR"/python3-pip_*.deb "$PIP_EXTRACT_DIR"
        rm -f "$PIP_EXTRACT_DIR"/python3-pip_*.deb
    fi
    mkdir -p "$PYLIBS_DIR"
    PYTHONPATH="$PIP_EXTRACT_DIR/usr/lib/python3/dist-packages" python3 -m pip install --target "$PYLIBS_DIR" spylls
fi

# --- 3. Générer les formes fléchies (grossièrement, cf. commentaire ci-dessus) ---
RAW_FILE="$OUTPUT_DIR/dictionnaire_fr.raw.txt"
"$UNMUNCH" "$DIC_FILE" "$AFF_FILE" > "$RAW_FILE" 2>/dev/null

# --- 4. Nettoyage + vérification avec spylls ---
FINAL_FILE="$OUTPUT_DIR/dictionnaire_fr.txt"
PYTHONPATH="$PYLIBS_DIR" python3 "$SCRIPT_DIR/clean_dictionary.py" "$RAW_FILE" "$DIC_BASE" "$FINAL_FILE"

rm -f "$RAW_FILE"
