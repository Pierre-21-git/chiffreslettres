package fr.pierre.chiffreslettres.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProfilEntity::class,
        SessionEntity::class,
        MancheEntity::class,
        DefiEntity::class,
        TropheeEntity::class,
        DefiQuotidienEntity::class,
    ],
    version = 7,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profilDao(): ProfilDao
    abstract fun historiqueDao(): HistoriqueDao
    abstract fun defiDao(): DefiDao
    abstract fun tropheeDao(): TropheeDao
    abstract fun defiQuotidienDao(): DefiQuotidienDao
}
