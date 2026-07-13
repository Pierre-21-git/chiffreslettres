package fr.pierre.chiffreslettres.data

import android.content.Context
import androidx.room.Room

/** Même pattern singleton que `DictionnaireProvider` côté :app. */
object AppDatabaseProvider {
    @Volatile private var instance: AppDatabase? = null

    fun obtenir(context: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "chiffreslettres.db")
                .build()
                .also { instance = it }
        }
}
