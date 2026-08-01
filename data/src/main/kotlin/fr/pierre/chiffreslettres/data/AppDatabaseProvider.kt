package fr.pierre.chiffreslettres.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 → v2 : ajout de la table `DefiEntity` (mode Défi). Migration réelle plutôt qu'une
 * réinitialisation destructrice, pour ne pas effacer les profils/historique déjà en base sur
 * le téléphone des joueurs.
 */
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `DefiEntity` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `profilId` INTEGER NOT NULL,
                `mode` TEXT NOT NULL,
                `niveauCode` TEXT NOT NULL,
                `serie` INTEGER NOT NULL,
                `date` INTEGER NOT NULL,
                FOREIGN KEY(`profilId`) REFERENCES `ProfilEntity`(`id`) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DefiEntity_profilId` ON `DefiEntity` (`profilId`)")
    }
}

/** v2 → v3 : ajout de la table `TropheeEntity` (trophées/succès). */
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `TropheeEntity` (
                `profilId` INTEGER NOT NULL,
                `trophyId` TEXT NOT NULL,
                `dateDebloque` INTEGER NOT NULL,
                PRIMARY KEY(`profilId`, `trophyId`),
                FOREIGN KEY(`profilId`) REFERENCES `ProfilEntity`(`id`) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TropheeEntity_profilId` ON `TropheeEntity` (`profilId`)")
    }
}

/**
 * v3 → v4 : ajout de la colonne `type` sur `DefiEntity` (défi série vs défi chrono, retour
 * utilisateur). `DEFAULT 'SERIE'` pour que les défis déjà enregistrés restent classés comme
 * aujourd'hui.
 */
private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `DefiEntity` ADD COLUMN `type` TEXT NOT NULL DEFAULT 'SERIE'")
    }
}

/** v4 → v5 : ajout de la colonne `avatar` sur `ProfilEntity` (retour utilisateur). */
private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `ProfilEntity` ADD COLUMN `avatar` TEXT NOT NULL DEFAULT '$AVATAR_PAR_DEFAUT'")
    }
}

/** v5 → v6 : ajout de la table `DefiQuotidienEntity` (défi quotidien, retour utilisateur). */
private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `DefiQuotidienEntity` (
                `profilId` INTEGER NOT NULL,
                `jour` TEXT NOT NULL,
                `dateReussite` INTEGER NOT NULL,
                PRIMARY KEY(`profilId`, `jour`),
                FOREIGN KEY(`profilId`) REFERENCES `ProfilEntity`(`id`) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DefiQuotidienEntity_profilId` ON `DefiQuotidienEntity` (`profilId`)")
    }
}

/**
 * v6 → v7 : ajout de `victoireDuel` sur `SessionEntity` (mode duo/confrontation, retour
 * utilisateur) — nullable, sans défaut, laissé `NULL` pour toutes les sessions déjà en base
 * (types autres que DUO/DUO_CONFRONTATION, pour qui ce champ n'a pas de sens).
 */
private val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `SessionEntity` ADD COLUMN `victoireDuel` INTEGER")
    }
}

/**
 * v7 → v8 : ajout de la colonne `langue` sur `ProfilEntity` (retour utilisateur : choix de
 * langue par profil). `DEFAULT '$LANGUE_PAR_DEFAUT'` pour que les profils déjà en base restent
 * en français, comme avant cette fonctionnalité.
 */
private val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `ProfilEntity` ADD COLUMN `langue` TEXT NOT NULL DEFAULT '$LANGUE_PAR_DEFAUT'")
    }
}

/**
 * v8 → v9 : les trophées "Duo à distance"/"Confrontation à distance" sont fusionnés avec
 * "Duo"/"Confrontation" (retour utilisateur : une partie compte pour le même trophée qu'elle
 * soit jouée sur un seul téléphone ou à distance). Purge les trophées déjà débloqués sous les
 * anciens ids, devenus orphelins (absents du catalogue), pour ne pas fausser le compteur
 * "X / total" affiché à l'écran Trophées.
 */
private val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            DELETE FROM `TropheeEntity` WHERE `trophyId` IN (
                'duo_reseau_1', 'duo_reseau_gagnee_1', 'duo_reseau_gagnee_10',
                'confrontation_reseau_1', 'confrontation_reseau_gagnee_1', 'confrontation_reseau_gagnee_10'
            )
            """.trimIndent(),
        )
    }
}

/** Même pattern singleton que `DictionnaireProvider` côté :app. */
object AppDatabaseProvider {
    @Volatile private var instance: AppDatabase? = null

    fun obtenir(context: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "chiffreslettres.db")
                .addMigrations(
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,
                    MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9,
                )
                .build()
                .also { instance = it }
        }
}
