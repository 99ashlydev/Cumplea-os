/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cu.ashlydev.birthdays.domain.model.AchievementKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val key: AchievementKey,
    val desbloqueado: Boolean = false,
    val fechaDesbloqueo: Long? = null
)