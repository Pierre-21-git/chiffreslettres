#!/usr/bin/env python3
"""
Nettoyage et vérification du dictionnaire brut produit par `unmunch` (spec §4.3).

`unmunch` (paquet hunspell-tools) ne résout pas correctement les classes de
continuation imbriquées utilisées par le dictionnaire Dicollecte pour les
conjugaisons : beaucoup de formes ressortent encore suivies de leurs
indicateurs bruts, ex. "aimerait/n'q'l'm't's'|" au lieu de "aimerait" seul.

Stratégie : on tronque chaque ligne à la première "/" (le texte avant est
déjà le mot correctement formé dans l'immense majorité des cas), on
dédoublonne, puis on **vérifie chaque candidat avec spylls** (réimplémentation
Python fidèle de Hunspell, qui gère bien les classes de continuation) pour
écarter les faux positifs restants (mots-valises générés par erreur lors du
compoundage des élisions, ex. "Puisqu'Chorem").
"""
from __future__ import annotations

import multiprocessing
import sys
from pathlib import Path

_dictionary = None


def _init_worker(dic_base: str) -> None:
    global _dictionary
    from spylls.hunspell import Dictionary

    _dictionary = Dictionary.from_files(dic_base)


def _garde_si_valide(mot: str) -> str | None:
    try:
        return mot if _dictionary.lookup(mot) else None
    except Exception:
        return None


def main() -> None:
    if len(sys.argv) != 4:
        print("Usage: clean_dictionary.py <raw_unmunch_file> <dic_base_path> <output_file>", file=sys.stderr)
        sys.exit(1)

    raw_file, dic_base, output_file = sys.argv[1], sys.argv[2], sys.argv[3]

    print("Extraction et dédoublonnage des candidats...", file=sys.stderr)
    candidats: set[str] = set()
    with open(raw_file, encoding="utf-8", errors="ignore") as f:
        for ligne in f:
            mot = ligne.split("/", 1)[0].strip()
            if mot and all(c.isalpha() for c in mot):
                candidats.add(mot)
    print(f"{len(candidats)} candidats uniques à vérifier.", file=sys.stderr)

    nb_workers = multiprocessing.cpu_count()
    print(f"Vérification avec spylls sur {nb_workers} processus (peut prendre plusieurs minutes)...", file=sys.stderr)
    with multiprocessing.Pool(nb_workers, initializer=_init_worker, initargs=(dic_base,)) as pool:
        resultats = pool.map(_garde_si_valide, candidats, chunksize=2000)

    mots_valides = sorted((m for m in resultats if m), key=lambda m: (len(m), m))

    Path(output_file).write_text("\n".join(mots_valides) + "\n", encoding="utf-8")

    print(f"Dictionnaire généré : {output_file}", file=sys.stderr)
    print(f"Nombre total de mots valides : {len(mots_valides)}", file=sys.stderr)
    par_longueur: dict[int, int] = {}
    for m in mots_valides:
        par_longueur[len(m)] = par_longueur.get(len(m), 0) + 1
    print("Répartition par longueur :", file=sys.stderr)
    for longueur in sorted(par_longueur):
        print(f"  {longueur:2d} lettres : {par_longueur[longueur]} mots", file=sys.stderr)


if __name__ == "__main__":
    main()
