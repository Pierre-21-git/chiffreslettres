package fr.pierre.chiffreslettres.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProfilEntity::class, SessionEntity::class, MancheEntity::class, DefiEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profilDao(): ProfilDao
    abstract fun historiqueDao(): HistoriqueDao
    abstract fun defiDao(): DefiDao
}
