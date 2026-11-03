/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cu.ashlydev.birthdays.domain.model.AppSettings
import cu.ashlydev.birthdays.domain.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val HORA = intPreferencesKey("hora_notif_min")
        val AVISO = intPreferencesKey("aviso_dias")
        val TONO = stringPreferencesKey("tono_uri")
        val VIBRACION = booleanPreferencesKey("vibracion")
        val THEME = stringPreferencesKey("theme_mode")
        val IDIOMA = stringPreferencesKey("idioma")
        val NOTIF_ACTIVAS = booleanPreferencesKey("notif_activas")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            horaNotificacionMinutos = p[Keys.HORA] ?: (9 * 60),
            avisoAnticipadoDias = p[Keys.AVISO] ?: 0,
            tonoNotificacionUri = p[Keys.TONO],
            vibracion = p[Keys.VIBRACION] ?: true,
            themeMode = runCatching {
                ThemeMode.valueOf(p[Keys.THEME] ?: ThemeMode.SYSTEM.name)
            }.getOrDefault(ThemeMode.SYSTEM),
            idioma = p[Keys.IDIOMA] ?: "system",
            notificacionesActivas = p[Keys.NOTIF_ACTIVAS] ?: true
        )
    }

    suspend fun setHora(minutos: Int) = context.dataStore.edit { it[Keys.HORA] = minutos }
    suspend fun setAviso(dias: Int) = context.dataStore.edit { it[Keys.AVISO] = dias }
    suspend fun setTono(uri: String?) = context.dataStore.edit {
        if (uri == null) it.remove(Keys.TONO) else it[Keys.TONO] = uri
    }
    suspend fun setVibracion(v: Boolean) = context.dataStore.edit { it[Keys.VIBRACION] = v }
    suspend fun setTheme(mode: ThemeMode) = context.dataStore.edit { it[Keys.THEME] = mode.name }
    suspend fun setIdioma(lang: String) = context.dataStore.edit { it[Keys.IDIOMA] = lang }
    suspend fun setNotificacionesActivas(b: Boolean) =
        context.dataStore.edit { it[Keys.NOTIF_ACTIVAS] = b }
}