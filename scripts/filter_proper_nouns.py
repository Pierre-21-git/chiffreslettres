#!/usr/bin/env python3
"""Filtrage grammatical allégé pour les langues sans lexique type Morphalou (ex. anglais) :
retire les noms propres et les sigles/abréviations (étape 1 de filter_grammar.py, sans
ressource externe), mais ne filtre PAS les formes de verbes purement conjuguées faute
d'un lexique morphologique équivalent pour cette langue (limite connue, cf. mémoire projet
"reference_dictionnaires_langues" — à revoir si un lexique adapté est trouvé plus tard).

Heuristique noms propres ("majuscule sans forme minuscule connue") invalide pour les langues
où TOUS les noms communs sont capitalisés par convention orthographique (ex. allemand) : dans
ce cas, utiliser --pas-de-detection-noms-propres pour ne retirer que les sigles. Les vrais noms
propres présents dans le dictionnaire source restent alors acceptés (limite connue).
"""
from __future__ import annotations

import sys
from pathlib import Path


def est_nom_propre(mot: str, mots_connus: set[str]) -> bool:
    return mot[:1].isupper() and (mot[0].lower() + mot[1:]) not in mots_connus


def est_sigle(mot: str) -> bool:
    return len(mot) > 1 and mot == mot.upper()


def main() -> None:
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    detecter_noms_propres = "--pas-de-detection-noms-propres" not in sys.argv
    if len(args) != 2:
        print("Usage: filter_proper_nouns.py <input_file> <output_file> [--pas-de-detection-noms-propres]", file=sys.stderr)
        sys.exit(1)

    input_file, output_file = Path(args[0]), Path(args[1])

    mots = [l.strip() for l in input_file.read_text(encoding="utf-8").splitlines() if l.strip()]
    total_depart = len(mots)
    mots_connus = set(mots)

    print("Noms propres et sigles (par casse)...", file=sys.stderr)
    noms_propres = {m for m in mots if detecter_noms_propres and est_nom_propre(m, mots_connus)}
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
