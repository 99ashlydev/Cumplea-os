/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.model

enum class AchievementKey {
    FIRST_PERSON,
    FIVE_PEOPLE,
    TEN_PEOPLE,
    TWENTY_FIVE_PEOPLE,
    HAS_PHOTO,
    HAS_GROUP,
    SET_ADVANCE,
    RECEIVED_ONE_NOTIFICATION,
    STREAK_3,
    STREAK_5,
    STREAK_10,
    WHATSAPP_ONE,
    WHATSAPP_TEN,
    THIRTY_DAYS,
    ONE_YEAR
}

data class Achievement(
    val key: AchievementKey,
    val titulo: String,
    val descripcion: String,
    val emoji: String,
    val desbloqueado: Boolean = false,
    val fechaDesbloqueo: Long? = null
)