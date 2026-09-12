package fr.pierre.chiffreslettres.numbers

/**
 * Solveur "compte est bon" : recherche exhaustive de toutes les valeurs
 * atteignables en combinant les nombres deux à deux (comme sur la calculatrice
 * du jeu, §3.4), résultats intermédiaires entiers positifs uniquement.
 *
 * Une valeur est "atteignable" dès qu'elle apparaît à une étape quelconque du
 * calcul, pas seulement en réduisant les 6 nombres à un seul résultat final :
 * le joueur peut valider son compte à tout moment.
 */
object Solveur {

    fun valeursAtteignables(
        nombres: List<Int>,
        operations: Set<Operation>,
        tableMultiplicationMax: Int? = null,
    ): Map<Int, Expression> {
        val initial = nombres.map { Expression.Valeur(it) as Expression }
        val cache = HashMap<List<Int>, Map<Int, Expression>>()
        return explorer(initial, operations, tableMultiplicationMax, cache)
    }

    fun estAtteignable(
        nombres: List<Int>,
        cible: Int,
        operations: Set<Operation>,
        tableMultiplicationMax: Int? = null,
    ): Boolean = valeursAtteignables(nombres, operations, tableMultiplicationMax).containsKey(cible)

    private fun explorer(
        expressions: List<Expression>,
        operations: Set<Operation>,
        tableMultiplicationMax: Int?,
        cache: MutableMap<List<Int>, Map<Int, Expression>>,
    ): Map<Int, Expression> {
        val cle = expressions.map { it.resultat }.sorted()
        cache[cle]?.let { return it }

        val resultat = HashMap<Int, Expression>()
        // Chaque valeur actuellement visible peut être validée telle quelle.
        for (expr in expressions) resultat.putIfAbsent(expr.resultat, expr)

        for (i in expressions.indices) {
            for (j in expressions.indices) {
                if (i >= j) continue
                val a = expressions[i]
                val b = expressions[j]
                val reste = expressions.filterIndexed { idx, _ -> idx != i && idx != j }

                for (op in operations) {
                    combiner(a, op, b, tableMultiplicationMax)?.let { combo ->
                        explorer(reste + combo, operations, tableMultiplicationMax, cache).forEach { (v, e) -> resultat.putIfAbsent(v, e) }
                    }
                    if (op == Operation.MOINS || op == Operation.DIVISE) {
                        combiner(b, op, a, tableMultiplicationMax)?.let { combo ->
                            explorer(reste + combo, operations, tableMultiplicationMax, cache).forEach { (v, e) -> resultat.putIfAbsent(v, e) }
                        }
                    }
                }
            }
        }

        cache[cle] = resultat
        return resultat
    }

    /**
     * Combine deux expressions avec une opération, ou `null` si le résultat n'est pas un entier
     * positif (résultat interdit, cf. §3.2) ou si la multiplication ne respecte pas
     * [tableMultiplicationMax] (retour utilisateur : niveau Odile, cf. `Niveau.tableMultiplicationMax`).
     * Public : c'est aussi ce que l'écran de jeu utilise pour exécuter un pas de calcul du
     * joueur — mais sans passer [tableMultiplicationMax] (retour utilisateur : cette
     * restriction ne s'applique qu'à la recherche du tirage garanti, pas aux calculs du
     * joueur, qui reste libre de multiplier comme il veut en partie).
     */
    fun combiner(
        gauche: Expression,
        operation: Operation,
        droite: Expression,
        tableMultiplicationMax: Int? = null,
    ): Expression? {
        val x = gauche.resultat
        val y = droite.resultat
        if (operation == Operation.FOIS && tableMultiplicationMax != null && !dansTableMultiplication(x, y, tableMultiplicationMax)) return null
        val valeur = when (operation) {
            Operation.PLUS -> x + y
            Operation.MOINS -> if (x > y) x - y else return null
            Operation.FOIS -> x * y
            Operation.DIVISE -> if (y != 0 && x % y == 0) x / y else return null
        }
        return Expression.Calcul(gauche, operation, droite, valeur)
    }

    /** Vrai si un des deux facteurs est dans `1..max` et l'autre dans `1..10` (table de multiplication scolaire). */
    private fun dansTableMultiplication(x: Int, y: Int, max: Int): Boolean =
        (x in 1..max && y in 1..10) || (y in 1..max && x in 1..10)
}
