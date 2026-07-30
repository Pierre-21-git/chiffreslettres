#!/usr/bin/env python3
"""Filtrage grammatical allégé pour les langues sans lexique type Morphalou (ex. anglais) :
retire les noms propres et les sigles/abréviations (étape 1 de filter_grammar.py, sans
ressource externe), mais ne filtre PAS les formes de verbes purement conjuguées faute
d'un lexique morphologique équivalent pour cette langue (limite connue, cf. mémoire projet
"reference_dictionnaires_langues" — à revoir si un lexique adapté est trouvé plus tard).
"""
from __future__ import annotations

import sys
from pathlib import Path


def est_nom_propre(mot: str, mots_connus: set[str]) -> bool:
    return mot[:1].isupper() and (mot[0].lower() + mot[1:]) not in mots_connus


def est_sigle(mot: str) -> bool:
    return len(mot) > 1 and mot == mot.upper()


def main() -> None:
    if len(sys.argv) != 3:
        print("Usage: filter_proper_nouns.py <input_file> <output_file>", file=sys.stderr)
        sys.exit(1)

    input_file, output_file = Path(sys.argv[1]), Path(sys.argv[2])

    mots = [l.strip() for l in input_file.read_text(encoding="utf-8").splitlines() if l.strip()]
    total_depart = len(mots)
    mots_connus = set(mots)

    print("Noms propres et sigles (par casse)...", file=sys.stderr)
    noms_propres = {m for m in mots if est_nom_propre(m, mots_connus)}
    sigles = {m for m in mots if m not in noms_propres and est_sigle(m)}
    retires = noms_propres | sigles
    mots_finaux = [m for m in mots if m not in retires]
    print(f"  Noms propres retirés : {len(noms_propres)}", file=sys.stderr)
    print(f"  Sigles retirés       : {len(sigles)}", file=sys.stderr)

    mots_finaux.sort(key=lambda m: (len(m), m))
    output_file.write_text("\n".join(mots_finaux) + "\n", encoding="utf-8")

    print(f"\nDictionnaire filtré : {output_file}", file=sys.stderr)
    print(f"Total avant filtrage : {total_depart}", file=sys.stderr)
    print(f"Total après filtrage : {len(mots_finaux)}", file=sys.stderr)
    par_longueur: dict[int, int] = {}
    for m in mots_finaux:
        par_longueur[len(m)] = par_longueur.get(len(m), 0) + 1
    print("Répartition par longueur :", file=sys.stderr)
    for longueur in sorted(par_longueur):
        print(f"  {longueur:2d} lettres : {par_longueur[longueur]} mots", file=sys.stderr)


if __name__ == "__main__":
    main()
