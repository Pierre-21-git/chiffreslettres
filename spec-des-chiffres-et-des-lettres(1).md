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

Le nombre de "grands nombres" (25/50/75/100) parmi les 6 tirés est **déterminé
automatiquement au hasard entre 0 et 2** (pas de choix du joueur), le reste
étant complété par des petits nombres (1-10). Tirage sans remise dans le
réservoir de la manche en cours.

### 3.2 Niveaux de difficulté

| Niveau | Cible | Opérations autorisées | Garantie de solution |
|---|---|---|---|
| 1. Facile ≤ 100 | aléatoire entre 10 et 100 | +, −, ×, ÷ | **Oui**, une solution exacte existe toujours |
| 2. Aléatoire ≤ 100 | aléatoire entre 10 et 100 | +, −, ×, ÷ | Non, peut ne pas avoir de solution exacte |
| 3. Facile ≤ 100 (+ / −) | aléatoire entre 10 et 100 | + et − uniquement | **Oui** |
| 4. Aléatoire ≤ 100 (+ / −) | aléatoire entre 10 et 100 | + et − uniquement | Non |
| 5. Facile ≤ 200 | aléatoire entre 10 et 200 | +, −, ×, ÷ | **Oui** |
| 6. Aléatoire ≤ 200 | aléatoire entre 10 et 200 | +, −, ×, ÷ | Non |
| 7. Normal (officiel) | aléatoire entre 100 et 999 | +, −, ×, ÷ | Non — comme le jeu télévisé |

Les niveaux 3 et 4 reprennent exactement les règles des niveaux 1 et 2
(même plage de cible, même mécanisme), mais avec le jeu d'opérations restreint
à l'addition et la soustraction — ils servent d'échauffement avant les niveaux
avec multiplication/division. Dans l'interface, cela se traduit simplement par
la désactivation des boutons × et ÷ sur la calculatrice (voir §3.4).

**Mécanisme de garantie** : tirer les 6 nombres et la cible selon les règles du
niveau, exécuter le solveur (recherche exhaustive avec mémoïsation des
combinaisons/opérations, en respectant le jeu d'opérations autorisé pour le
niveau, avec résultats intermédiaires entiers positifs uniquement). Si aucune
solution exacte n'existe, retirer une nouvelle cible (en conservant les 6
nombres) et recommencer, jusqu'à obtention d'un tirage solvable. Prévoir un
garde-fou (nombre max de tentatives) qui retire aussi les nombres en dernier
recours.

### 3.3 Barème de points (dégressif selon l'écart)

- Écart = |cible − résultat proposé par le joueur|
- Score = `max(0, 10 - écart)`, plafonné à 0 en dessous
- Compte exact (écart = 0) → 10 points
- Aucune proposition ou écart trop important → 0 point

*(Formule simple à ajuster facilement si besoin une fois testée en jeu.)*

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
- Le joueur peut valider son compte à tout moment (dès qu'un résultat
  intermédiaire correspond à la cible, ou pour proposer sa meilleure
  approche en fin de temps)
- Un bouton "annuler la dernière opération" permet de revenir en arrière si
  le joueur se trompe de chemin
- Chronomètre configurable (voir §5)
- À la fin du temps : affichage d'une solution trouvée par le solveur (au cas
  où le joueur n'a pas trouvé), et du score obtenu

---

## 4. Mode Lettres

### 4.1 Tirage des lettres

9 lettres toujours, choisies une à une par le joueur en cliquant sur
"Consonne" ou "Voyelle". Tirage sans remise dans un sac dont la répartition
suit (approximativement) les fréquences du français — base retenue : la
distribution Scrabble français (100 lettres) :

```
A:9 B:2 C:2 D:3 E:15 F:2 G:2 H:2 I:8 J:1 K:1 L:5 M:3 N:6
O:6 P:2 Q:1 R:6 S:6 T:6 U:6 V:2 W:1 X:1 Y:1 Z:1
```

**Règle du minimum 2 voyelles** : le Y compte comme voyelle pour cette règle
(comme dans le jeu original). L'interface doit empêcher un tirage qui
aboutirait à moins de 2 voyelles sur les 9 lettres : si le nombre de tirages
"voyelle" restants possibles ne permet plus d'atteindre 2, désactiver le
bouton "Consonne" pour forcer une voyelle.

### 4.2 Niveaux de difficulté

| Niveau | Lettres exclues du sac |
|---|---|
| Facile | X, Y, Z, W, K, Q |
| Moyen | X, Y, Z, W |
| Normal | Aucune exclusion (alphabet complet) |

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

Score = nombre de lettres du mot proposé et validé par le dictionnaire.

### 4.5 Écran de jeu

- Affichage des 9 lettres tirées (boutons Consonne / Voyelle avant tirage
  complet)
- Zone de saisie du mot trouvé par le joueur
- Chronomètre configurable
- À la fin du temps : validation du mot du joueur contre le dictionnaire,
  affichage du meilleur mot trouvé par le moteur de recherche, score

---

## 5. Chronomètre

Durée **entièrement configurable** par le joueur dans les réglages, pour le
mode chiffres et le mode lettres séparément. Valeurs par défaut suggérées :
45 secondes (chiffres), 40 secondes (lettres), modifiables librement (pas de
bornes imposées autres qu'un minimum technique raisonnable, ex. 10 secondes).

---

## 6. Structure des parties (solo)

Les deux modes suivants doivent être disponibles, sélectionnables par le
joueur au lancement :

### 6.1 Entraînement libre
- Le joueur enchaîne les manches (chiffres ou lettres, niveau au choix) à sa
  guise
- Score cumulé affiché en continu
- Peut s'arrêter à tout moment, la session est enregistrée dans l'historique

### 6.2 Partie structurée
- Séquence de manches configurable avant de démarrer (nombre de manches
  lettres / chiffres, ordre), avec une structure par défaut proposée
  (ex. 4 manches lettres + 3 manches chiffres)
- Score total calculé en fin de partie
- Résultat enregistré dans l'historique comme une partie complète

---

## 7. Historique et statistiques (solo)

### 7.1 Profils joueurs

L'app gère un ou plusieurs **profils joueurs locaux** (pratique pour un usage
familial où plusieurs personnes jouent sur le même téléphone) :
- Au premier lancement, création d'un profil par défaut (saisie d'un pseudo)
- Possibilité d'ajouter/supprimer des profils depuis les réglages
- Sélection du profil actif en début de session solo (le profil sélectionné
  reste actif jusqu'à changement explicite)
- Toutes les parties et tous les scores enregistrés sont rattachés au profil
  actif au moment de la partie

### 7.2 Données stockées

Stockage local via Room :
- Historique des sessions (date, joueur, mode, niveau joué, score total,
  détail des manches)
- **Meilleurs scores rattachés à la fois au nom du joueur et au niveau de
  difficulté joué** — un classement séparé par niveau (les 7 niveaux de
  chiffres, les 3 niveaux de lettres), et par joueur au sein de chaque niveau
- Plus long mot trouvé, par joueur
- Meilleur score en partie structurée, par joueur
- Écran dédié "Statistiques" accessible depuis le menu principal, avec un
  sélecteur de joueur et un sélecteur de niveau pour consulter les
  classements

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
 │    │    ├── Choix mode (Chiffres / Lettres)
 │    │    └── Choix niveau de difficulté
 │    └── Partie structurée
 │         ├── Configuration des manches
 │         └── Déroulé de la partie
 ├── Jouer à 2 (Bluetooth / Nearby)
 │    ├── Héberger une partie
 │    └── Rejoindre une partie
 ├── Statistiques
 │    ├── Sélecteur de joueur
 │    └── Sélecteur de niveau (chiffres / lettres)
 ├── Réglages
 │    ├── Gestion des profils joueurs (créer / supprimer / renommer)
 │    ├── Durée du chrono (chiffres / lettres)
 │    └── (autres préférences à définir en cours de dev)
 └── À propos (licences, dont mention LGPL du dictionnaire)
```

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
