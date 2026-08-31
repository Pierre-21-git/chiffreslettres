package fr.pierre.chiffreslettres.ui.defi

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.DefiQuotidienRepository
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.Trophee
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.data.alphabet.ConfigurationAlphabetLettres
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.BaremeLettres
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.letters.ObjectifPoints
import fr.pierre.chiffreslettres.letters.SacLettres
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.letters.genererObjectifs
import fr.pierre.chiffreslettres.widget.DefiQuotidienWidgetProvider
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Raison de fin du défi Points (retour utilisateur) : affichée à l'écran pour expliquer l'arrêt. */
enum class RaisonFinDefiObjectifsPoints { VOLONTAIRE, TEMPS_ECOULE, TOUS_OBJECTIFS_ATTEINTS }

/** Raison du rejet d'un mot qui ne met pas fin au défi (retour utilisateur : signalé, sans pénalité). */
enum class RaisonRejetMotDefiObjectifsPoints { INVALIDE, SCORE_SANS_OBJECTIF }

data class DefiObjectifsPointsUiState(
    val niveau: NiveauLettres,
    val nombreLettres: Int,
    val lettresTirees: List<Char> = emptyList(),
    val tirageTermine: Boolean = false,
    val indicesUtilises: List<Int> = emptyList(),
    val motSaisi: String = "",
    val tempsRestantSecondes: Int,
    val termine: Boolean = false,
    val objectifs: List<ObjectifPoints> = emptyList(),
    /** Mots validés, parallèles aux objectifs atteints (retour utilisateur : affichés comme en défi mots max). */
    val motsTrouves: List<String> = emptyList(),
    val motRejeteTransitoire: String? = null,
    val raisonRejetTransitoire: RaisonRejetMotDefiObjectifsPoints? = null,
    val nombreVoyellesChoisi: Int? = null,
    val raisonFin: RaisonFinDefiObjectifsPoints? = null,
    /** Barème de points par lettre de la langue courante (retour utilisateur : dépend de la langue). */
    val bareme: Map<Char, Int> = BaremeLettres.FRANCAIS,
    /** Tous les mots jouables sur ce tirage (retour utilisateur : révélés en fin de défi, comme en défi mots max), triés du plus long au plus court, limités à [MAX_MOTS_POSSIBLES_AFFICHES]. */
    val motsPossibles: List<String> = emptyList(),
) {
    /** Score courant du mot en cours de saisie (retour utilisateur : affiché en direct sous "Votre mot"). */
    val scoreMotSaisi: Int get() = BaremeLettres.scoreMot(motSaisi, bareme)
}

/**
 * Défi "Points" (retour utilisateur) : un seul tirage de lettres, chronométré (durée par niveau
 * comme le défi chrono, cf. [budgetSecondesDefiChrono]), avec des objectifs de points à atteindre
 * exactement (`BaremeLettres`/[genererObjectifs]). Comme le défi mots max,
 * les lettres utilisées sont "dégrisées" après chaque mot validé ; un mot dont le score ne
 * correspond à aucun objectif restant est signalé mais ne met pas fin au défi. Un seul ViewModel
 * couvre tout le défi, qui enregistre lui-même sa performance finale.
 */
class DefiObjectifsPointsViewModel(
    private val niveau: NiveauLettres,
    private val dictionnaire: DictionnaireIndex,
    private val configurationAlphabet: ConfigurationAlphabetLettres,
    private val defiRepository: DefiRepository,
    private val tropheeRepository: TropheeRepository,
    private val profilId: Long,
    private val nombreLettres: Int = TirageLettres.NOMBRE_LETTRES,
    private val random: Random = Random,
    /** Non nuls seulement quand ce défi fait partie du défi quotidien (retour utilisateur). */
    private val defiQuotidienRepository: DefiQuotidienRepository? = null,
    private val jourQuotidien: String? = null,
    context: Context? = null,
) : ViewModel() {

    private val appContext = context?.applicationContext

    private fun sacNeuf() = SacLettres.creer(
        configurationAlphabet.distributionBase,
        configurationAlphabet.voyelles,
        configurationAlphabet.lettresExcluesParNiveau.getValue(niveau),
    )

    private var sac = sacNeuf()
    private val niveauCode = niveau.name
    private val nombreObjectifsCible = nombreObjectifsDefiPoints(niveau)
    private var timerJob: Job? = null
    private var enregistre = false
    /** Calculés une fois le tirage connu (retour utilisateur : révélés en fin de défi, comme en défi mots max). */
    private var motsPossiblesCalcules: List<String> = emptyList()

    private val _uiState = MutableStateFlow(
        DefiObjectifsPointsUiState(
            niveau = niveau,
            nombreLettres = nombreLettres,
            tempsRestantSecondes = budgetSecondesDefiChrono(niveau),
            bareme = configurationAlphabet.baremeLettres,
        ),
    )
    val uiState: StateFlow<DefiObjectifsPointsUiState> = _uiState.asStateFlow()

    private val _tropheesDebloques = MutableStateFlow<List<Trophee>>(emptyList())
    /** Trophées fraîchement débloqués à la fin de ce défi (retour utilisateur : écran dédié). */
    val tropheesDebloques: StateFlow<List<Trophee>> = _tropheesDebloques.asStateFlow()

    fun effacerTropheesDebloques() {
        _tropheesDebloques.value = emptyList()
    }

    fun choisirNombreVoyelles(nombreVoyelles: Int) {
        val etat = _uiState.value
        if (etat.tirageTermine || etat.termine) return
        val (lettres, objectifs) = tirerAvecGarantie(nombreVoyelles)
        _uiState.update {
            it.copy(lettresTirees = lettres, tirageTermine = true, nombreVoyellesChoisi = nombreVoyelles, objectifs = objectifs)
        }
        motsPossiblesCalcules = dictionnaire.rechercherAuMoins(lettres, LONGUEUR_MIN_MOT_POSSIBLE)
            .distinct()
            .sortedWith(compareByDescending<String> { it.length }.then(DictionnaireIndex.comparateurAlphabetiqueFrancais()))
        demarrerChrono()
    }

    /**
     * Chaque tentative recrée un sac neuf jusqu'à trouver un tirage offrant au moins
     * [nombreObjectifsCible] valeurs de points distinctes atteignables, ou abandonne après
     * [MAX_TENTATIVES_GARANTIE] essais (dictionnaire trop pauvre pour ce tirage) en renvoyant le
     * dernier tirage obtenu avec les objectifs qu'il permet, pour ne jamais bloquer la partie
     * (même principe que `DefiMotsMaxViewModel.tirerAvecGarantie`).
     */
    private fun tirerAvecGarantie(nombreVoyelles: Int): Pair<List<Char>, List<ObjectifPoints>> {
        repeat(MAX_TENTATIVES_GARANTIE - 1) {
            val tirage = TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
            val objectifs = genererObjectifs(tirage, dictionnaire, nombreObjectifsCible, configurationAlphabet.baremeLettres)
            if (objectifs.size >= nombreObjectifsCible) return tirage to objectifs
            sac = sacNeuf()
        }
        val tirage = TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
        return tirage to genererObjectifs(tirage, dictionnaire, nombreObjectifsCible, configurationAlphabet.baremeLettres)
    }

    private fun demarrerChrono() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.tempsRestantSecondes > 0) {
                delay(1000)
                if (_uiState.value.termine) return@launch
                _uiState.update { it.copy(tempsRestantSecondes = it.tempsRestantSecondes - 1) }
            }
            terminer(RaisonFinDefiObjectifsPoints.TEMPS_ECOULE)
        }
    }

    fun cliquerLettre(index: Int) {
        val etat = _uiState.value
        if (etat.termine || !etat.tirageTermine) return
        if (index !in etat.lettresTirees.indices || index in etat.indicesUtilises) return
        mettreAJourMot(etat.indicesUtilises + index)
    }

    fun annulerLettre() {
        if (_uiState.value.termine) return
        mettreAJourMot(_uiState.value.indicesUtilises.dropLast(1))
    }

    fun effacerMot() {
        if (_uiState.value.termine) return
        mettreAJourMot(emptyList())
    }

    private fun mettreAJourMot(indicesUtilises: List<Int>) {
        val etat = _uiState.value
        val mot = indicesUtilises.map { etat.lettresTirees[it] }.joinToString("")
        _uiState.update { it.copy(indicesUtilises = indicesUtilises, motSaisi = mot, motRejeteTransitoire = null, raisonRejetTransitoire = null) }
    }

    /**
     * Valide le mot en cours (retour utilisateur) : vide → arrêt volontaire ; hors dictionnaire →
     * signalé (INVALIDE) ; valide mais dont le score ne correspond à aucun objectif restant →
     * signalé (SCORE_SANS_OBJECTIF), aucune pénalité dans les deux cas ; sinon l'objectif
     * correspondant est marqué atteint, les lettres dégrisées. Si tous les objectifs sont
     * atteints, le défi se termine en succès complet.
     */
    fun valider() {
        val etat = _uiState.value
        if (etat.termine || !etat.tirageTermine) return
        val mot = etat.motSaisi
        if (mot.isBlank()) {
            terminer(RaisonFinDefiObjectifsPoints.VOLONTAIRE)
            return
        }
        if (!dictionnaire.estJouable(mot)) {
            rejeterMot(mot, RaisonRejetMotDefiObjectifsPoints.INVALIDE)
            return
        }
        val score = BaremeLettres.scoreMot(mot, configurationAlphabet.baremeLettres)
        val indexObjectif = etat.objectifs.indexOfFirst { it.points == score && !it.atteint }
        if (indexObjectif == -1) {
            rejeterMot(mot, RaisonRejetMotDefiObjectifsPoints.SCORE_SANS_OBJECTIF)
            return
        }
        _uiState.update {
            it.copy(
                objectifs = it.objectifs.mapIndexed { index, objectif -> if (index == indexObjectif) objectif.copy(atteint = true) else objectif },
                motsTrouves = it.motsTrouves + mot,
                indicesUtilises = emptyList(),
                motSaisi = "",
                motRejeteTransitoire = null,
                raisonRejetTransitoire = null,
            )
        }
        if (_uiState.value.objectifs.all { it.atteint }) {
            terminer(RaisonFinDefiObjectifsPoints.TOUS_OBJECTIFS_ATTEINTS)
        }
    }

    private fun rejeterMot(mot: String, raison: RaisonRejetMotDefiObjectifsPoints) {
        _uiState.update {
            it.copy(indicesUtilises = emptyList(), motSaisi = "", motRejeteTransitoire = mot, raisonRejetTransitoire = raison)
        }
    }

    private fun terminer(raison: RaisonFinDefiObjectifsPoints) {
        if (_uiState.value.termine) return
        timerJob?.cancel()
        _uiState.update { it.copy(termine = true, raisonFin = raison, motsPossibles = motsPossiblesCalcules.take(MAX_MOTS_POSSIBLES_AFFICHES)) }
        if (enregistre) return
        enregistre = true
        val score = _uiState.value.objectifs.count { it.atteint }
        val dureeSecondes = budgetSecondesDefiChrono(niveau) - _uiState.value.tempsRestantSecondes
        viewModelScope.launch {
            defiRepository.enregistrer(profilId, ModeJeu.LETTRES, niveauCode, TypeDefi.OBJECTIFS_POINTS, score, dureeSecondes)
            // Réussite du jour uniquement sur un succès complet (retour utilisateur : pas de
            // défi quotidien "partiel"), même principe que DefiViewModel.objectifQuotidienAtteint().
            if (raison == RaisonFinDefiObjectifsPoints.TOUS_OBJECTIFS_ATTEINTS && defiQuotidienRepository != null && jourQuotidien != null) {
                defiQuotidienRepository.enregistrerReussite(profilId, jourQuotidien, niveauCode)
                appContext?.let { DefiQuotidienWidgetProvider.demanderMiseAJour(it) }
            }
            _tropheesDebloques.value = tropheeRepository.reevaluer(profilId)
        }
    }

    fun recommencer() {
        timerJob?.cancel()
        enregistre = false
        motsPossiblesCalcules = emptyList()
        sac = sacNeuf()
        _uiState.update {
            DefiObjectifsPointsUiState(
                niveau = it.niveau,
                nombreLettres = nombreLettres,
                tempsRestantSecondes = budgetSecondesDefiChrono(niveau),
                bareme = configurationAlphabet.baremeLettres,
            )
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }

    private companion object {
        const val MAX_TENTATIVES_GARANTIE = 300
        /** Pas de longueur minimale propre au défi Points (contrairement au défi mots max) : tous les mots jouables du dictionnaire comptent, à partir de sa longueur minimale. */
        const val LONGUEUR_MIN_MOT_POSSIBLE = 2
    }
}
