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

    fun valeursAtteignables(nombres: List<Int>, operations: Set<Operation>): Map<Int, Expression> {
        val initial = nombres.map { Expression.Valeur(it) as Expression }
        val cache = HashMap<List<Int>, Map<Int, Expression>>()
        return explorer(initial, operations, cache)
    }

    fun estAtteignable(nombres: List<Int>, cible: Int, operations: Set<Operation>): Boolean =
        valeursAtteignables(nombres, operations).containsKey(cible)

    private fun explorer(
        expressions: List<Expression>,
        operations: Set<Operation>,
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
                    combiner(a, op, b)?.let { combo ->
                        explorer(reste + combo, operations, cache).forEach { (v, e) -> resultat.putIfAbsent(v, e) }
                    }
                    if (op == Operation.MOINS || op == Operation.DIVISE) {
                        combiner(b, op, a)?.let { combo ->
                            explorer(reste + combo, operations, cache).forEach { (v, e) -> resultat.putIfAbsent(v, e) }
                        }
                    }
                }
            }
        }

        cache[cle] = resultat
        return resultat
    }

    /**
     * Combine deux expressions avec une opération, ou `null` si le résultat
     * n'est pas un entier positif (résultat interdit, cf. §3.2). Public : c'est
     * aussi ce que l'écran de jeu utilise pour exécuter un pas de calcul du
     * joueur, afin de garantir exactement les mêmes règles que le solveur.
     */
    fun combiner(gauche: Expression, operation: Operation, droite: Expression): Expression? {
        val x = gauche.resultat
        val y = droite.resultat
        val valeur = when (operation) {
            Operation.PLUS -> x + y
            Operation.MOINS -> if (x > y) x - y else return null
            Operation.FOIS -> x * y
            Operation.DIVISE -> if (y != 0 && x % y == 0) x / y else return null
        }
        return Expression.Calcul(gauche, operation, droite, valeur)
    }
}
