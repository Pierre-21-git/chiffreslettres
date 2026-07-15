package fr.pierre.chiffreslettres.numbers

/**
 * Arbre de calcul menant à [resultat], utilisé comme témoin qu'une valeur est
 * atteignable (et pour l'afficher comme solution en fin de manche).
 */
sealed class Expression {
    abstract val resultat: Int

    data class Valeur(override val resultat: Int) : Expression()

    data class Calcul(
        val gauche: Expression,
        val operation: Operation,
        val droite: Expression,
        override val resultat: Int,
    ) : Expression()

    fun texte(): String = when (this) {
        is Valeur -> resultat.toString()
        is Calcul -> "(${gauche.texte()} ${operation.symbole} ${droite.texte()})"
    }

    /** Décompose le calcul en étapes "x op y = résultat", même format que les opérations du joueur. */
    fun etapes(): List<String> = when (this) {
        is Valeur -> emptyList()
        is Calcul -> gauche.etapes() + droite.etapes() +
            "${gauche.resultat} ${operation.symbole} ${droite.resultat} = $resultat"
    }
}
