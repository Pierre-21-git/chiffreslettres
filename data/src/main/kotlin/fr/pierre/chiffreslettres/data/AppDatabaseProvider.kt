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

/** Même pattern singleton que `DictionnaireProvider` côté :app. */
object AppDatabaseProvider {
    @Volatile private var instance: AppDatabase? = null

    fun obtenir(context: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "chiffreslettres.db")
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { instance = it }
        }
}
