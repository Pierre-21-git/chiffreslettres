package fr.pierre.chiffreslettres.ui.partieduo

import androidx.lifecycle.ViewModel
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.ui.partie.ManchePlanifiee
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Qui joue actuellement une manche donnée du duo. */
enum class TourDuo { JOUEUR1, JOUEUR2 }

/**
 * Résultat d'un joueur sur une manche, avec l'écart à la cible pour les manches chiffres (null
 * en lettres — la comparaison s'y fait sur la longueur du mot), et [detail] à afficher sur
 * l'écran de transition (le calcul effectué en chiffres, le mot joué en lettres).
 */
data class ResultatDuoManche(val resultat: ResultatManche, val ecartCible: Int? = null, val detail: String = "")

/** Qui commence une manche donnée (retour utilisateur : alterne à chaque manche, chiffres et lettres confondus, pour que le choix du nombre de voyelles ne revienne pas toujours au même joueur). */
fun premierJoueurManche(index: Int): TourDuo = if (index % 2 == 0) TourDuo.JOUEUR1 else TourDuo.JOUEUR2

/**
 * Partagé par le sous-graphe "partieDuo", même principe que [fr.pierre.chiffreslettres.ui.partie.PartieStructureeViewModel]
 * mais pour deux joueurs sur le même tirage à chaque manche (retour utilisateur). Un seul
 * `index` de manche partagé (contrairement au solo, les deux joueurs jouent la même manche
 * avant de passer à la suivante) ; [tour] indique lequel des deux joue actuellement.
 */
class PartieDuoViewModel : ViewModel() {

    private val _sequence = MutableStateFlow<List<ManchePlanifiee>>(emptyList())
    val sequence: StateFlow<List<ManchePlanifiee>> = _sequence.asStateFlow()

    /** Une graine par manche (générée une fois au démarrage) pour que les deux joueurs affrontent exactement le même tirage. */
    private val _seeds = MutableStateFlow<List<Long>>(emptyList())
    val seeds: StateFlow<List<Long>> = _seeds.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _tour = MutableStateFlow(TourDuo.JOUEUR1)
    val tour: StateFlow<TourDuo> = _tour.asStateFlow()

    private val _resultatsJoueur1 = MutableStateFlow<List<ResultatDuoManche>>(emptyList())
    val resultatsJoueur1: StateFlow<List<ResultatDuoManche>> = _resultatsJoueur1.asStateFlow()

    private val _resultatsJoueur2 = MutableStateFlow<List<ResultatDuoManche>>(emptyList())
    val resultatsJoueur2: StateFlow<List<ResultatDuoManche>> = _resultatsJoueur2.asStateFlow()

    /** Vrai dès qu'un joueur vient de terminer son tour : écran de transition affiché avant de continuer (retour utilisateur, systématique même si le joueur ne change pas). */
    private val _enTransition = MutableStateFlow(false)
    val enTransition: StateFlow<Boolean> = _enTransition.asStateFlow()

    var profil2Id: Long = -1L
        private set
    var mode: ModeScoreDuo = ModeScoreDuo.DUO
        private set

    fun demarrer(profil2Id: Long, sequence: List<ManchePlanifiee>, mode: ModeScoreDuo) {
        this.profil2Id = profil2Id
        this.mode = mode
        _sequence.value = sequence
        _seeds.value = List(sequence.size) { Random.nextLong() }
        _index.value = 0
        _tour.value = premierJoueurManche(0)
        _resultatsJoueur1.value = emptyList()
        _resultatsJoueur2.value = emptyList()
        _enTransition.value = false
    }

    /** Appelé dès qu'un joueur termine sa manche : enregistre son résultat et bascule sur l'écran de transition/révélation. */
    fun enregistrerResultat(resultat: ResultatDuoManche) {
        if (_tour.value == TourDuo.JOUEUR1) {
            _resultatsJoueur1.value = _resultatsJoueur1.value + resultat
        } else {
            _resultatsJoueur2.value = _resultatsJoueur2.value + resultat
        }
        _enTransition.value = true
    }

    /** Clic sur "Prêt" de l'écran de transition : passe au second joueur de la manche, ou à la manche suivante si les deux ont fini. */
    fun confirmerTransition() {
        _enTransition.value = false
        val idx = _index.value
        val premier = premierJoueurManche(idx)
        if (_tour.value == premier) {
            _tour.value = if (premier == TourDuo.JOUEUR1) TourDuo.JOUEUR2 else TourDuo.JOUEUR1
        } else {
            val nouvelIndex = idx + 1
            _index.value = nouvelIndex
            _tour.value = premierJoueurManche(nouvelIndex)
        }
    }

    /**
     * Résultats finaux prêts à être enregistrés : en mode Confrontation, le score de chaque
     * manche est écrasé à 0 pour le perdant (le gagnant garde le sien ; égalité → chacun garde
     * le sien). En mode Duo, les scores individuels ne sont jamais modifiés.
     */
    fun resultatsFinaux(): Pair<List<ResultatManche>, List<ResultatManche>> {
        val r1 = _resultatsJoueur1.value
        val r2 = _resultatsJoueur2.value
        if (mode != ModeScoreDuo.CONFRONTATION) {
            return r1.map { it.resultat } to r2.map { it.resultat }
        }
        val finaux1 = mutableListOf<ResultatManche>()
        val finaux2 = mutableListOf<ResultatManche>()
        for (i in r1.indices) {
            val a = r1[i]
            val b = r2.getOrNull(i) ?: continue
            val vainqueur = when (a.resultat.mode) {
                ModeJeu.CHIFFRES -> vainqueurMancheChiffres(a.ecartCible, b.ecartCible)
                ModeJeu.LETTRES -> vainqueurMancheLettres(a.resultat.motJoue, b.resultat.motJoue)
            }
            when (vainqueur) {
                VainqueurManche.JOUEUR1 -> {
                    finaux1 += a.resultat
                    finaux2 += b.resultat.copy(score = 0)
                }
                VainqueurManche.JOUEUR2 -> {
                    finaux1 += a.resultat.copy(score = 0)
                    finaux2 += b.resultat
                }
                VainqueurManche.EGALITE -> {
                    finaux1 += a.resultat
                    finaux2 += b.resultat
                }
            }
        }
        return finaux1 to finaux2
    }
}
