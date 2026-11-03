/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cu.ashlydev.birthdays.data.local.dao.AchievementDao
import cu.ashlydev.birthdays.data.local.dao.GroupDao
import cu.ashlydev.birthdays.data.local.dao.PersonDao
import cu.ashlydev.birthdays.data.local.entity.AchievementEntity
import cu.ashlydev.birthdays.data.local.entity.GroupEntity
import cu.ashlydev.birthdays.data.local.entity.PersonEntity
import cu.ashlydev.birthdays.data.local.entity.UserStatsEntity

@Database(
    entities = [
        PersonEntity::class,
        GroupEntity::class,
        AchievementEntity::class,
        UserStatsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class CumpleanosDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun groupDao(): GroupDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val NAME = "cumpleanos.db"
    }
}