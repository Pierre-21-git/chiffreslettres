package fr.pierre.chiffreslettres.ui.lettres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.alphabet.ConfigurationAlphabetLettres
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.letters.SacLettres
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.letters.meilleurMot
import fr.pierre.chiffreslettres.ui.defi.seuilLongueurDefiLettres
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LettresRoundUiState(
    val niveau: NiveauLettres,
    val nombreLettres: Int,
    val lettresTirees: List<Char> = emptyList(),
    val tirageTermine: Boolean = false,
    /** Indices dans [lettresTirees] des tuiles cliquées, dans l'ordre (spec §4.5, retour utilisateur : mot construit au clic, pas au clavier). */
    val indicesUtilises: List<Int> = emptyList(),
    val motSaisi: String = "",
    /** Null = pas de limite de temps (entraînement libre, retour utilisateur). */
    val tempsRestantSecondes: Int?,
    val termine: Boolean = false,
    val meilleurMot: String? = null,
    val motJoueurValide: Boolean? = null,
    val scoreObtenu: Int? = null,
    /** Nombre de voyelles choisi pour ce tirage, null tant qu'il n'a pas été choisi — permet au mode Duo de forcer le même choix pour le second joueur d'une manche (retour utilisateur : mêmes lettres pour les deux). */
    val nombreVoyellesChoisi: Int? = null,
)

/** Tirage selon le nombre de voyelles choisi (§4.1) puis recherche du mot le plus long (§4.5). */
class LettresRoundViewModel(
    private val niveau: NiveauLettres,
    private val dictionnaire: DictionnaireIndex,
    private val configurationAlphabet: ConfigurationAlphabetLettres,
    dureeSecondes: Int? = null,
    private val nombreLettres: Int = TirageLettres.NOMBRE_LETTRES,
    /** Permet au mode Duo de rejouer exactement le même tirage pour les deux joueurs (même graine, nouvelle instance de Random par joueur ; combiné au même nombreVoyelles forcé côté second joueur). */
    private val random: Random = Random,
    /**
     * Défi série/sans faute niveau Monique ou Mathieu uniquement (retour utilisateur) : retire
     * le tirage jusqu'à ce qu'il contienne un mot d'au moins [seuilLongueurDefiLettres] lettres
     * pour ce niveau, pour garantir une vraie réussite plutôt que de dépendre de la tolérance
     * "meilleure approche" de `motEstReussiDefiLettres` (qui laisse un mot plus court compter
     * comme réussite quand le tirage ne permet objectivement pas d'atteindre le seuil).
     */
    private val garantieMotSeuil: Boolean = false,
) : ViewModel() {

    private fun sacNeuf() = SacLettres.creer(
        configurationAlphabet.distributionBase,
        configurationAlphabet.voyelles,
        configurationAlphabet.lettresExcluesParNiveau.getValue(niveau),
    )

    private var sac = sacNeuf()
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(
        LettresRoundUiState(
            niveau = niveau,
            nombreLettres = nombreLettres,
            tempsRestantSecondes = dureeSecondes,
        ),
    )
    val uiState: StateFlow<LettresRoundUiState> = _uiState.asStateFlow()

    fun choisirNombreVoyelles(nombreVoyelles: Int) {
        val etat = _uiState.value
        if (etat.tirageTermine || etat.termine) return
        val lettres = if (garantieMotSeuil) tirerAvecGarantie(nombreVoyelles) else TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
        _uiState.update { it.copy(lettresTirees = lettres, tirageTermine = true, nombreVoyellesChoisi = nombreVoyelles) }
        demarrerChrono()
    }

    /**
     * Chaque tentative recrée un sac neuf (le précédent tirage l'a vidé) jusqu'à trouver un
     * tirage contenant un mot d'au moins [seuilLongueurDefiLettres] lettres, ou abandonne après
     * [MAX_TENTATIVES_GARANTIE] essais (dictionnaire trop pauvre pour ce tirage de lettres) en
     * renvoyant le dernier tirage obtenu, pour ne jamais bloquer la partie.
     */
    private fun tirerAvecGarantie(nombreVoyelles: Int): List<Char> {
        val seuil = seuilLongueurDefiLettres(niveau)
        repeat(MAX_TENTATIVES_GARANTIE - 1) {
            val tirage = TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
            if (dictionnaire.rechercherAuMoins(tirage, seuil).isNotEmpty()) return tirage
            sac = sacNeuf()
        }
        return TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
    }

    private fun demarrerChrono() {
        if (_uiState.value.tempsRestantSecondes == null) return
        timerJob = viewModelScope.launch {
            while ((_uiState.value.tempsRestantSecondes ?: 0) > 0) {
                delay(1000)
                if (_uiState.value.termine) return@launch
                _uiState.update { it.copy(tempsRestantSecondes = it.tempsRestantSecondes?.minus(1)) }
            }
            terminer()
        }
    }

    fun cliquerLettre(index: Int) {
        val etat = _uiState.value
        if (etat.termine || !etat.tirageTermine) return
        if (index !in etat.lettresTirees.indices || index in etat.indicesUtilises) return
        mettreAJourMot(etat.indicesUtilises + index)
    }

    fun annulerLettre() {
        val etat = _uiState.value
        if (etat.termine) return
        mettreAJourMot(etat.indicesUtilises.dropLast(1))
    }

    fun effacerMot() {
        if (_uiState.value.termine) return
        mettreAJourMot(emptyList())
    }

    private fun mettreAJourMot(indicesUtilises: List<Int>) {
        val etat = _uiState.value
        val mot = indicesUtilises.map { etat.lettresTirees[it] }.joinToString("")
        _uiState.update { it.copy(indicesUtilises = indicesUtilises, motSaisi = mot) }
    }

    fun valider() = terminer()

    private fun terminer() {
        if (_uiState.value.termine) return
        timerJob?.cancel()
        val etat = _uiState.value
        val motValide = etat.motSaisi.isNotBlank() && dictionnaire.estJouable(etat.motSaisi)
        val meilleur = meilleurMot(etat.lettresTirees, dictionnaire)
        val score = if (motValide) etat.motSaisi.trim().length else 0
        _uiState.update {
            it.copy(termine = true, meilleurMot = meilleur, motJoueurValide = motValide, scoreObtenu = score)
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }

    private companion object {
        const val MAX_TENTATIVES_GARANTIE = 300
    }
}
