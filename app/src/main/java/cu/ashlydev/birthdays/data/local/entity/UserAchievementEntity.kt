/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Estado global de gamificación del usuario (una sola fila con id = 1).
 */
@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val rachaActual: Int = 0,
    val mejorRacha: Int = 0,
    val felicitacionesEnviadas: Int = 0,
    val primeraVezEnMillis: Long = System.currentTimeMillis(),
    val ultimaAperturaEnMillis: Long = System.currentTimeMillis()
)