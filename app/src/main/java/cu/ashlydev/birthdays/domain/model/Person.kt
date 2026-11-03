/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.model

import java.time.LocalDate

/**
 * Modelo de dominio de una persona con cumpleaños.
 *
 * @param id Identificador único (0 si aún no se ha guardado).
 * @param nombre Nombre completo.
 * @param fechaNacimiento Fecha completa (día, mes, año).
 * @param telefono Teléfono opcional para llamar o WhatsApp.
 * @param notas Notas opcionales.
 * @param fotoUri URI persistente de la foto (o null).
 * @param grupoId FK al grupo, opcional.
 * @param avisoAnticipadoDias Días de anticipación personalizados (null = usar default global).
 * @param horaNotificacionMinutos Hora personalizada en minutos desde medianoche (null = usar default global).
 * @param activo Si está o no activo (para "archivar" sin borrar).
 */
data class Person(
    val id: Long = 0L,
    val nombre: String,
    val fechaNacimiento: LocalDate,
    val telefono: String? = null,
    val notas: String? = null,
    val fotoUri: String? = null,
    val grupoId: Long? = null,
    val avisoAnticipadoDias: Int? = null,
    val horaNotificacionMinutos: Int? = null,
    val activo: Boolean = true
)