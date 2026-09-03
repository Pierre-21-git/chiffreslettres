# Plan de fusion `refonte-trophees` → `master`

État au 2026-09-03.

## Contexte

Depuis la v1.94 (commit `31766a3`), tout le développement de fond (refonte des trophées, Défi
Points, Duel points, etc.) se fait sur la branche `refonte-trophees` plutôt que sur `master`, le
temps que la merge request F-Droid !45293 (https://gitlab.com/fdroid/fdroiddata/-/merge_requests/45293,
basée sur le tag `1.86`) soit validée par un mainteneur — objectif : ne pas perturber sa relecture
avec des changements ultérieurs non liés.

`master` n'a aucun commit que `refonte-trophees` n'a pas : la fusion sera un simple
fast-forward une fois déclenchée.

- `master` : v1.94 (`31766a3`)
- `refonte-trophees` : v1.120 (`7b58186`), poussée sur GitHub, à jour avec `origin/refonte-trophees`

## Point bloquant : tags manquants

F-Droid utilise `UpdateCheckMode: Tags` + `AutoUpdateMode: Version` (voir `CLAUDE.md`) : seuls les
tags Git poussés déclenchent la détection d'une nouvelle version côté F-Droid. Or les tags
s'arrêtent à `1.98` alors que le code de `refonte-trophees` est à `1.120` — les versions 1.99 à
1.120 ont été développées sur la branche dédiée sans jamais être taguées.

## Décision (utilisateur, 2026-09-01)

- Retagger **rétroactivement toutes** les versions manquantes (1.99 à la version finale au moment
  de la fusion), pas seulement la dernière — l'utilisateur préfère un historique de tags complet.
- **Supprimer la branche `refonte-trophees`** (locale et distante) une fois la fusion faite dans
  `master`.

## Check-list à exécuter le jour de la fusion

1. Vérifier l'état de la MR !45293 (validée par un mainteneur F-Droid).
2. Tester la version finale sur téléphone physique.
3. Créer et pousser un tag Git pour chaque version de 1.99 à la version finale (aux commits
   `Version X.YY : ...` correspondants sur `refonte-trophees`).
4. Fusionner `refonte-trophees` dans `master` (fast-forward).
5. Pousser `master`.
6. Vérifier que `versionCode`/`versionName` (`app/build.gradle.kts`) sont cohérents avec le
   dernier tag poussé.
7. Supprimer la branche `refonte-trophees` (locale et distante), après confirmation explicite de
   l'utilisateur.

Rien de cette check-list n'a été exécuté à ce jour — la MR !45293 est toujours en attente de
relecture.
