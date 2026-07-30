package fr.pierre.chiffreslettres.network

import java.util.UUID

/** Label court (respecte la limite conventionnelle de 15 caractères pour un type de service NSD). */
const val TYPE_SERVICE_NSD = "_clettres._tcp."
const val PREFIXE_NOM_SERVICE_NSD = "Partie de "
const val TIMEOUT_CONNEXION_MS = 8_000
const val TIMEOUT_HANDSHAKE_MS = 5_000L

/** UUID fixe du service RFCOMM : doit être strictement identique côté hôte et invité. */
val UUID_SERVICE_BLUETOOTH: UUID = UUID.fromString("e43bf1f7-78a9-4f41-8029-62841f0c3c75")
const val NOM_SERVICE_BLUETOOTH = "ChiffresLettres"
