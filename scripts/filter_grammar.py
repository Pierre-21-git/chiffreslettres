#!/usr/bin/env python3
"""Filtrage grammatical du dictionnaire (spec §4.3, règles du jeu Lettres) : retire les
noms propres, les sigles/abréviations, et les formes de verbes purement conjuguées
(infinitifs et participes présents/passés restent acceptés).

Étape 1 (sans ressource externe) : un mot commençant par une majuscule est considéré
comme nom propre s'il n'existe pas de variante en minuscule dans la liste (le Hunspell
Dicollecte n'a pas d'entrée capitalisée pour les mots communs) ; un mot entièrement en
majuscules (plus d'une lettre) est considéré comme un sigle.

Étape 2 (via Morphalou3, lexique morphologique CNRS/ATILF, licence LGPL-LR) : un mot est
retiré seulement si TOUTES ses lectures connues dans Morphalou sont des formes de verbe
conjuguées (indicatif, subjonctif, conditionnel, impératif) — s'il existe une lecture nom
commun / adjectif / adverbe / interjection / mot grammatical, ou une lecture infinitif /
participe, le mot est conservé (homographes du type "porte", "marche", "ferme"...). Un mot
absent de Morphalou est conservé par défaut (le lexique ne couvre pas 100% du vocabulaire).
"""
from __future__ import annotations

import sys
from pathlib import Path

NONVERB_FILES = [
    "commonNoun_Morphalou3.1_CSV.csv",
    "adjective_Morphalou3.1_CSV.csv",
    "adverb_Morphalou3.1_CSV.csv",
    "interjection_Morphalou3.1_CSV.csv",
    "grammaticalWords_Morphalou3.1_CSV.csv",
    "noCategory_Morphalou3.1_CSV.csv",
]

EN_TETE_LIGNES = 16  # bandeau de licence + ligne d'en-tête sur deux niveaux (LEMME/FLEXION)


def est_nom_propre(mot: str, mots_connus: set[str]) -> bool:
    return mot[:1].isupper() and (mot[0].lower() + mot[1:]) not in mots_connus


def est_sigle(mot: str) -> bool:
    return len(mot) > 1 and mot == mot.upper()


def charger_formes_non_verbe(morphalou_dir: Path, cibles: set[str]) -> set[str]:
    formes: set[str] = set()
    for nom_fichier in NONVERB_FILES:
        chemin = morphalou_dir / nom_fichier
        if not chemin.exists():
            continue
        with open(chemin, encoding="utf-8") as f:
            for i, ligne in enumerate(f):
                if i < EN_TETE_LIGNES:
                    continue
                champs = ligne.rstrip("\n").split(";")
                if len(champs) < 10:
                    continue
                graphie_flexion = champs[9]
                if not graphie_flexion:
                    continue
                mot_l = graphie_flexion.lower()
                if mot_l in cibles:
                    formes.add(mot_l)
    return formes


def charger_verbes(morphalou_dir: Path, cibles: set[str]) -> tuple[set[str], set[str]]:
    finies: set[str] = set()
    non_finies: set[str] = set()
    chemin = morphalou_dir / "verb_Morphalou3.1_CSV.csv"
    with open(chemin, encoding="utf-8") as f:
        for i, ligne in enumerate(f):
            if i < EN_TETE_LIGNES:
                continue
            champs = ligne.rstrip("\n").split(";")
            if len(champs) < 13:
                continue
            graphie_flexion = champs[9]
            mode = champs[12]
            if not graphie_flexion:
                continue
            mot_l = graphie_flexion.lower()
            if mot_l not in cibles:
                continue
            if mode in ("infinitive", "participle"):
                non_finies.add(mot_l)
            else:
                finies.add(mot_l)
    return finies, non_finies


def main() -> None:
    if len(sys.argv) != 4:
        print("Usage: filter_grammar.py <input_file> <morphalou_csv_dir> <output_file>", file=sys.stderr)
        sys.exit(1)

    input_file, morphalou_dir, output_file = Path(sys.argv[1]), Path(sys.argv[2]), Path(sys.argv[3])

    mots = [l.strip() for l in input_file.read_text(encoding="utf-8").splitlines() if l.strip()]
    total_depart = len(mots)
    mots_connus = set(mots)

    print("Étape 1/2 : noms propres et sigles (par casse)...", file=sys.stderr)
    noms_propres = {m for m in mots if est_nom_propre(m, mots_connus)}
    sigles = {m for m in mots if m not in noms_propres and est_sigle(m)}
    retires_casse = noms_propres | sigles
    restants = [m for m in mots if m not in retires_casse]
    print(f"  Noms propres retirés : {len(noms_propres)}", file=sys.stderr)
    print(f"  Sigles retirés       : {len(sigles)}", file=sys.stderr)

    print("Étape 2/2 : verbes conjugués (via Morphalou3)...", file=sys.stderr)
    cibles = {m.lower() for m in restants}
    non_verbes = charger_formes_non_verbe(morphalou_dir, cibles)
    finies, non_finies = charger_verbes(morphalou_dir, cibles)

    mots_finaux = []
    nb_exclus_verbe = 0
    for m in restants:
        m_l = m.lower()
        est_uniquement_conjugue = (
            m_l in finies and m_l not in non_finies and m_l not in non_verbes
        )
        if est_uniquement_conjugue:
            nb_exclus_verbe += 1
        else:
            mots_finaux.append(m)
    print(f"  Formes conjuguées retirées : {nb_exclus_verbe}", file=sys.stderr)

    mots_finaux.sort(key=lambda m: (len(m), m))
    output_file.write_text("\n".join(mots_finaux) + "\n", encoding="utf-8")

    print(f"\nDictionnaire filtré : {output_file}", file=sys.stderr)
    print(f"Total avant filtrage grammatical : {total_depart}", file=sys.stderr)
    print(f"Total après filtrage grammatical : {len(mots_finaux)}", file=sys.stderr)
    par_longueur: dict[int, int] = {}
    for m in mots_finaux:
        par_longueur[len(m)] = par_longueur.get(len(m), 0) + 1
    print("Répartition par longueur :", file=sys.stderr)
    for longueur in sorted(par_longueur):
        print(f"  {longueur:2d} lettres : {par_longueur[longueur]} mots", file=sys.stderr)


if __name__ == "__main__":
    main()
