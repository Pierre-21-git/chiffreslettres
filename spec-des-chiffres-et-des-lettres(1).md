# Spécification — Application Android "Des Chiffres et des Lettres"

## 1. Vue d'ensemble

Application Android reproduisant les deux épreuves emblématiques du jeu télévisé
"Des Chiffres et des Lettres" : le compte est bon (chiffres) et le mot le plus
long (lettres). Deux modes de session (entraînement libre / partie structurée),
des statistiques locales, et un mode 2 joueurs en local via Nearby Connections.

## 2. Stack technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose
- **Architecture** : MVVM (ViewModel + StateFlow), modules séparés par domaine
- **Persistance locale** : Room (historique, statistiques, réglages)
- **Connexion 2 joueurs** : Nearby Connections API (Google Play services)
- **Cible** : Android natif, pas de backend serveur — tout tourne en local /
  pair-à-pair

### Découpage modules proposé
```
app/                    -> UI Compose, navigation, écrans
core-numbers/           -> logique tirage chiffres, solveur "compte est bon"
core-letters/           -> logique tirage lettres, recherche du meilleur mot
core-dictionary/        -> chargement et interrogation du dictionnaire
data/                   -> Room (historique, stats, réglages), DataStore
multiplayer/            -> wrapper Nearby Connections, protocole de partie
```

---

## 3. Mode Chiffres

### 3.1 Plaques et tirage

Réservoir de 24 plaques, identique au jeu original :
- Nombres de **1 à 10**, chacun en **double exemplaire**
- **25, 50, 75, 100**, chacun en **un seul exemplaire**

6 nombres tirés. Le nombre de "grands nombres" (25/50/75/100) parmi eux est
**déterminé automatiquement au hasard entre 0 et 2** (pas de choix du
joueur), le reste étant complété par des petits nombres (1-10). Tirage sans
remise dans le réservoir de la manche en cours.

### 3.2 Niveaux de difficulté

4 niveaux, avec des noms mnémotechniques partagés avec le mode Lettres (§4.2) —
même palier de difficulté, même nom, pour permettre un classement combiné
(§7.2) :

| Niveau | Cible | Opérations autorisées | Garantie de solution |
|---|---|---|---|
| Assez facile, Émile | aléatoire entre 10 et 100 | + et − uniquement | **Oui**, une solution exacte existe toujours |
| Ça va encore, Nestor | aléatoire entre 10 et 100 | +, −, ×, ÷ | **Oui** |
| Ça se complique, Monique | aléatoire entre 10 et 200 | +, −, ×, ÷ | Non, peut ne pas avoir de solution exacte |
| Là c'est sérieux, Mathieu | aléatoire entre 100 et 999 | +, −, ×, ÷ | Non — comme le jeu télévisé (mode officiel) |

Dans l'interface, le jeu d'opérations restreint (Émile) se traduit simplement
par la désactivation des boutons × et ÷ sur la calculatrice (voir §3.4).

**Mécanisme de garantie** : tirer les 6 nombres et la cible selon les règles du
niveau, exécuter le solveur (recherche exhaustive avec mémoïsation des
combinaisons/opérations, en respectant le jeu d'opérations autorisé pour le
niveau, avec résultats intermédiaires entiers positifs uniquement). Si aucune
solution exacte n'existe, retirer une nouvelle cible (en conservant les 6
nombres) et recommencer, jusqu'à obtention d'un tirage solvable. Prévoir un
garde-fou (nombre max de tentatives) qui retire aussi les nombres en dernier
recours.

### 3.3 Barème de points

Le barème dépend du niveau (retour utilisateur après testing) :

- **Émile, Nestor** (les deux niveaux les plus faciles) : barème simplifié —
  10 points si le compte est bon (résultat exact), 5 points pour toute
  proposition non exacte, 0 point si rien n'a été proposé.
- **Monique, Mathieu** : barème dégressif selon l'écart, comme le jeu
  télévisé — écart = |cible − résultat proposé|, score = `max(0, 10 - écart)`,
  compte exact → 10 points, aucune proposition ou écart trop important → 0
  point.

### 3.4 Écran de jeu — interface type calculatrice

Le joueur ne saisit pas une expression textuelle : il construit son calcul pas
à pas, comme sur une calculatrice, en combinant deux nombres disponibles à la
fois.

- Les 6 nombres tirés sont affichés comme des "jetons" cliquables, ainsi que
  la cible à atteindre
- Déroulé d'une opération : le joueur clique sur un premier nombre disponible,
  clique sur un opérateur (+, −, ×, ÷ — seuls les opérateurs autorisés par le
  niveau sont actifs, voir §3.2), puis clique sur un second nombre disponible
- Le résultat de l'opération **remplace les deux opérandes utilisés** dans la
  liste des nombres disponibles : il devient à son tour un jeton cliquable,
  utilisable dans une opération suivante (exactement comme dans le jeu
  original, où chaque plaque n'est utilisée qu'une fois mais un résultat
  intermédiaire peut resservir)
- Le joueur peut valider à tout moment ; la proposition retenue est le
  **dernier résultat de la liste des comptes** (le résultat de sa dernière
  opération, ou l'un des nombres tirés si aucune opération n'a été faite),
  pas un jeton qu'il faudrait sélectionner explicitement au moment de valider
  (retour utilisateur)
- Un bouton "annuler la dernière opération" permet de revenir en arrière si
  le joueur se trompe de chemin
- Chronomètre en partie structurée uniquement, configurable (voir §5) — pas
  de limite de temps en entraînement libre
- À la fin (validation, ou fin du temps en partie structurée) : affichage
  d'une solution trouvée par le solveur (au cas où le joueur n'a pas trouvé),
  et du score obtenu

---

## 4. Mode Lettres

### 4.1 Tirage des lettres

10 lettres. Le joueur choisit d'abord le nombre de voyelles souhaité (2, 3, 4
ou 5 ; le Y compte comme voyelle), puis les 10 lettres sont tirées d'un coup
(le nombre choisi de voyelles + le complément en consonnes, mélangées).
Tirage sans remise dans un sac dont la répartition suit (approximativement)
les fréquences du français — base retenue : la distribution Scrabble
français (100 lettres) :

```
A:9 B:2 C:2 D:3 E:15 F:2 G:2 H:2 I:8 J:1 K:1 L:5 M:3 N:6
O:6 P:2 Q:1 R:6 S:6 T:6 U:6 V:2 W:1 X:1 Y:1 Z:1
```

### 4.2 Niveaux de difficulté

4 niveaux, mêmes noms que le mode Chiffres (§3.2) pour permettre un
classement combiné (§7.2) :

| Niveau | Lettres exclues du sac |
|---|---|
| Assez facile, Émile | X, Y, Z, W, K, Q, H, J |
| Ça va encore, Nestor | X, Y, Z, W, K, Q |
| Ça se complique, Monique | X, Y, Z, W |
| Là c'est sérieux, Mathieu | Aucune exclusion (alphabet complet) |

Lorsque des lettres sont exclues, retirer leurs occurrences du sac de 100 et
conserver les proportions relatives des lettres restantes (pas besoin de
renormaliser à 100, le tirage sans remise fonctionne sur les quantités
brutes restantes).

### 4.3 Dictionnaire

- Source retenue : dictionnaire **Hunspell / LibreOffice français** (licence
  LGPL)
- **Étape de préparation hors application** (à faire une fois, en amont, pas
  dans le code Android) :
  1. Récupérer les fichiers `.dic` / `.aff` du dictionnaire français LibreOffice
  2. Utiliser un outil de type `unmunch` (fourni avec Hunspell) pour générer
     la liste complète des formes fléchies à partir des lemmes + règles
     d'affixes
  3. Nettoyer la liste : dédoublonnage, suppression des entrées non
     pertinentes, normalisation (majuscules/accents selon les besoins de
     comparaison)
  4. Exporter en fichier texte plat, groupé par longueur de mot, à embarquer
     comme asset Android (pas de dépendance Hunspell dans l'app elle-même)
  5. Ajouter la mention de licence LGPL dans un écran "À propos"
- **Structure de recherche runtime** : pour chaque mot du dictionnaire,
  précalculer un vecteur de comptage des 26 lettres. Pour un tirage donné,
  parcourir les mots par longueur décroissante (9 → 2) et ne garder que ceux
  dont le vecteur de lettres est un sous-ensemble du tirage (comparaison
  terme à terme, pas de permutation à calculer). S'arrêter dès qu'un mot de
  longueur maximale est trouvé, ou constituer la liste des meilleurs mots.

### 4.4 Barème de points

Le barème dépend du niveau (retour utilisateur après testing) :

- **Émile, Nestor** : 5 points pour un mot de 2 à 4 lettres, 10 points à
  partir de 5 lettres.
- **Monique, Mathieu** : score = nombre de lettres du mot proposé et validé
  par le dictionnaire (comme le jeu télévisé).

### 4.5 Écran de jeu

- Affichage des lettres tirées (choix du nombre de voyelles avant tirage)
- Une fois le tirage terminé, le joueur construit son mot en touchant les
  lettres tirées dans l'ordre voulu (chaque lettre n'est utilisable qu'une
  fois) — pas de saisie clavier. Boutons "Annuler" (retire la dernière
  lettre) et "Effacer" (vide le mot en cours) pour revenir en arrière.
- Chronomètre configurable
- À la fin du temps : validation du mot du joueur contre le dictionnaire,
  affichage du meilleur mot trouvé par le moteur de recherche, score

---

## 5. Chronomètre

**L'entraînement libre est sans limite de temps** (retour utilisateur) : le
joueur valide quand il veut, et un bouton "Quitter l'entraînement" (affiché
pendant la manche, pas seulement à la fin) permet d'arrêter la session à tout
moment — elle est alors enregistrée dans l'historique comme en pressant
"Arrêter" en fin de manche.

Le chronomètre s'applique uniquement en **partie structurée**, avec une durée
**fixe par niveau** (pas d'écran Réglages dans l'app — retour utilisateur :
plus rien à configurer une fois durée et nombre de manches fixés par niveau,
la page a été supprimée) :

| Niveau | Chiffres | Lettres |
|---|---|---|
| Assez facile, Émile | 120s | 110s |
| Ça va encore, Nestor | 100s | 90s |
| Ça se complique, Monique | 60s | 50s |
| Là c'est sérieux, Mathieu | 45s | 40s |

Le nombre de jetons tirés (chiffres, 6) et le nombre de lettres tirées
(lettres, 10) sont fixes eux aussi, non réglables par le joueur.

---

## 6. Structure des parties (solo)

Les deux modes suivants doivent être disponibles, sélectionnables par le
joueur au lancement :

### 6.1 Entraînement libre
- Un seul écran liste directement les 4 niveaux Chiffres puis les 4 niveaux
  Lettres (pas d'étape intermédiaire de choix du mode) ; le joueur enchaîne
  les manches à sa guise
- Score cumulé affiché en continu
- Peut s'arrêter à tout moment, la session est enregistrée dans l'historique

### 6.2 Partie structurée
- Un seul choix à faire avant de démarrer : le niveau (parmi les 4), appliqué
  aux manches chiffres et lettres de la partie
- Le nombre de manches par mode est **fixe par niveau** (`manchesParMode`,
  retour utilisateur), pas configuré à chaque partie :

  | Niveau | Manches par mode | Total |
  |---|---|---|
  | Assez facile, Émile | 2 | 4 |
  | Ça va encore, Nestor | 3 | 6 |
  | Ça se complique, Monique | 5 (comme le jeu télé) | 10 |
  | Là c'est sérieux, Mathieu | 5 (comme le jeu télé) | 10 |

- Les manches alternent chiffres et lettres (retour utilisateur) plutôt que
  d'être regroupées par mode
- Score total calculé en fin de partie
- Résultat enregistré dans l'historique comme une partie complète

---

## 7. Historique et statistiques (solo)

### 7.1 Profils joueurs

L'app gère un ou plusieurs **profils joueurs locaux** (pratique pour un usage
familial où plusieurs personnes jouent sur le même téléphone) :
- Au premier lancement, création d'un profil par défaut (saisie d'un pseudo)
- Gestion des profils (créer / renommer / supprimer) centralisée sur l'écran
  "Changer de profil" (retour utilisateur) — il n'y a pas d'écran Réglages
  séparé (supprimé, plus rien à y configurer, voir §5 et §6.2)
- Sélection du profil actif en début de session solo (le profil sélectionné
  reste actif jusqu'à changement explicite)
- Toutes les parties et tous les scores enregistrés sont rattachés au profil
  actif au moment de la partie

### 7.2 Données stockées

Stockage local via Room :
- Historique des sessions (date, joueur, mode, niveau joué, score total,
  détail des manches)
- **Classement combiné par niveau** : les 4 niveaux Chiffres et Lettres
  partagent les mêmes noms (§3.2, §4.2), donc un seul classement par niveau,
  sans distinction chiffres/lettres — top 5 scores bruts (pas de regroupement
  par joueur : un même joueur peut apparaître plusieurs fois s'il a les
  meilleurs scores, retour utilisateur), uniquement sur les parties
  structurées (l'entraînement libre n'y est pas compté)
- Écran dédié "Statistiques" accessible depuis le menu principal : les 4
  classements par niveau sont affichés directement (pas de sélecteur). Pas de
  section "stats par joueur" (retour utilisateur : supprimée). Un bouton
  "Réinitialiser les statistiques" (avec confirmation) vide tout l'historique.

---

## 8. Mode 2 joueurs (Nearby Connections)

### 8.1 Principe
Tirage unique partagé en temps réel : les deux joueurs voient exactement le
même tirage au même moment et cherchent chacun de leur côté, comme à la
télévision. Le premier appareil qui lance/héberge la manche est autoritaire
sur le tirage (il le génère et l'envoie à l'autre appareil).

### 8.2 Flux de connexion
1. Écran "Jouer à 2" : un joueur choisit "Héberger", l'autre "Rejoindre"
2. L'hôte lance `startAdvertising`, le second appareil lance `startDiscovery`
   via Nearby Connections
3. Une fois la connexion établie et acceptée des deux côtés, l'hôte envoie un
   signal de démarrage et le tirage de la première manche
4. Les deux appareils démarrent leur chronomètre de façon synchronisée à la
   réception du signal
5. Chaque joueur saisit sa réponse localement ; à la fin du temps, chaque
   appareil envoie sa réponse à l'autre (ou remonte à l'hôte qui redistribue)
6. Les scores sont calculés indépendamment sur chaque appareil avec la même
   logique (même solveur, même dictionnaire) pour éviter toute divergence,
   puis affichés aux deux joueurs
7. L'hôte tire la manche suivante et répète jusqu'à la fin de la partie
   (structure configurée avant le lancement, comme en solo §6.2)

### 8.3 Gestion des erreurs
- Timeout de connexion avec message clair si aucun appareil trouvé
- Gestion de la déconnexion en cours de partie (proposer reconnexion ou
  fin de partie avec sauvegarde du score déjà acquis)
- Permissions runtime nécessaires (Bluetooth / localisation selon version
  Android) demandées explicitement avec explication à l'utilisateur

---

## 9. Écrans / Navigation (proposition)

```
Menu principal (affiche le profil joueur actif, changement rapide possible)
 ├── Jouer en solo
 │    ├── Entraînement libre
 │    │    └── Choix niveau (liste unique : 4 niveaux Chiffres puis 4 niveaux Lettres)
 │    └── Partie structurée
 │         ├── Choix du niveau (un seul, appliqué aux manches chiffres et lettres)
 │         └── Déroulé de la partie
 ├── Jouer à 2 (Bluetooth / Nearby)
 │    ├── Héberger une partie
 │    └── Rejoindre une partie
 ├── Statistiques
 │    ├── Classement par niveau (4 niveaux, combiné chiffres/lettres, top 5 scores bruts)
 │    └── Réinitialiser les statistiques (avec confirmation)
 ├── Changer de profil (créer / renommer / supprimer les profils)
 └── À propos (licences, dont mention LGPL du dictionnaire)
```

Pas d'écran Réglages : durée du chrono et nombre de manches sont fixes par
niveau (§5, §6.2), rien à configurer côté joueur.

---

## 10. Phases de développement suggérées

1. **Cœur logique** : solveur chiffres (avec garantie de solvabilité),
   moteur de tirage lettres + recherche du meilleur mot, préparation hors-app
   du dictionnaire
2. **Solo — entraînement libre** : écrans chiffres et lettres jouables,
   tous niveaux de difficulté, chronomètre configurable
3. **Solo — partie structurée + historique/statistiques** (Room)
4. **Réglages** et écran "À propos"
5. **Mode 2 joueurs** : intégration Nearby Connections, protocole de partie
   synchronisée, gestion des erreurs de connexion

---

## 11. Points laissés ouverts pour Claude Code

- Format exact de saisie du calcul en mode chiffres (expression libre avec
  parenthèses vs saisie guidée pas à pas) — à trancher en design d'écran
- Détail visuel/UX (thème, animations) — non spécifié ici, à définir en
  parallèle avec un skill de design frontend si besoin
- Nombre exact de manches par défaut en partie structurée
