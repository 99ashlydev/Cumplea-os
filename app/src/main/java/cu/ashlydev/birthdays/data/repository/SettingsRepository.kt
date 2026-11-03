/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.repository

import cu.ashlydev.birthdays.data.preferences.SettingsDataStore
import cu.ashlydev.birthdays.domain.model.AppSettings
import cu.ashlydev.birthdays.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: SettingsDataStore
) {
    val settings: Flow<AppSettings> = dataStore.settings

    suspend fun setHora(minutos: Int) = dataStore.setHora(minutos)
    suspend fun setAviso(dias: Int) = dataStore.setAviso(dias)
    suspend fun setTono(uri: String?) = dataStore.setTono(uri)
    suspend fun setVibracion(v: Boolean) = dataStore.setVibracion(v)
    suspend fun setTheme(mode: ThemeMode) = dataStore.setTheme(mode)
    suspend fun setIdioma(lang: String) = dataStore.setIdioma(lang)
    suspend fun setNotificacionesActivas(b: Boolean) = dataStore.setNotificacionesActivas(b)
}