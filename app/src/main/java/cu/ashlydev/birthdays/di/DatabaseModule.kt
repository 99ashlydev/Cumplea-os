/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.di

import android.content.Context
import androidx.room.Room
import cu.ashlydev.birthdays.data.local.CumpleanosDatabase
import cu.ashlydev.birthdays.data.local.dao.AchievementDao
import cu.ashlydev.birthdays.data.local.dao.GroupDao
import cu.ashlydev.birthdays.data.local.dao.PersonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): CumpleanosDatabase =
        Room.databaseBuilder(ctx, CumpleanosDatabase::class.java, CumpleanosDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun providePersonDao(db: CumpleanosDatabase): PersonDao = db.personDao()
    @Provides fun provideGroupDao(db: CumpleanosDatabase): GroupDao = db.groupDao()
    @Provides fun provideAchievementDao(db: CumpleanosDatabase): AchievementDao = db.achievementDao()
}