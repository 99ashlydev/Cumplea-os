/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.model

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class AppSettings(
    val horaNotificacionMinutos: Int = 9 * 60,       // 09:00 por defecto
    val avisoAnticipadoDias: Int = 0,                // 0 = solo el día
    val tonoNotificacionUri: String? = null,         // null = tono por defecto del sistema
    val vibracion: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val idioma: String = "system",                   // "system" | "es" | "en"
    val notificacionesActivas: Boolean = true
)