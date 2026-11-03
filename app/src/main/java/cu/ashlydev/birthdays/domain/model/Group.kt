/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.model

data class Group(
    val id: Long = 0L,
    val nombre: String,
    val colorArgb: Int = 0xFFE91E63.toInt()
)