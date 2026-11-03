/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.di

import cu.ashlydev.birthdays.data.local.dao.AchievementDao
import cu.ashlydev.birthdays.data.local.dao.GroupDao
import cu.ashlydev.birthdays.data.local.dao.PersonDao
import cu.ashlydev.birthdays.data.preferences.SettingsDataStore
import cu.ashlydev.birthdays.data.repository.AchievementRepository
import cu.ashlydev.birthdays.data.repository.GroupRepository
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides @Singleton
    fun providePersonRepository(dao: PersonDao) = PersonRepository(dao)

    @Provides @Singleton
    fun provideGroupRepository(dao: GroupDao) = GroupRepository(dao)

    @Provides @Singleton
    fun provideAchievementRepository(dao: AchievementDao) = AchievementRepository(dao)

    @Provides @Singleton
    fun provideSettingsRepository(ds: SettingsDataStore) = SettingsRepository(ds)
}