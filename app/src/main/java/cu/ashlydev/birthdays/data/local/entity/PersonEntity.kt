/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "persons",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["grupoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("grupoId"), Index("fechaNacimiento")]
)
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nombre: String,
    val fechaNacimiento: LocalDate,
    val telefono: String? = null,
    val notas: String? = null,
    val fotoUri: String? = null,
    val grupoId: Long? = null,
    val avisoAnticipadoDias: Int? = null,
    val horaNotificacionMinutos: Int? = null,
    val activo: Boolean = true,
    val creadoEn: Long = System.currentTimeMillis()
)