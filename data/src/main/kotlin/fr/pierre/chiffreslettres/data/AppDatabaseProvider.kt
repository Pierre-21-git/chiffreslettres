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

/** Même pattern singleton que `DictionnaireProvider` côté :app. */
object AppDatabaseProvider {
    @Volatile private var instance: AppDatabase? = null

    fun obtenir(context: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "chiffreslettres.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
                .also { instance = it }
        }
}
