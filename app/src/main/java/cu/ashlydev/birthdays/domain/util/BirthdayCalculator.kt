/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.util

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

/**
 * Utilidades puras para cálculo de cumpleaños.
 * Esta clase es 100% testeable y no depende de Android.
 */
object BirthdayCalculator {

    /** Próxima ocurrencia del cumpleaños a partir de [hoy]. */
    fun nextOccurrence(fechaNacimiento: LocalDate, hoy: LocalDate = LocalDate.now()): LocalDate {
        val esteAnio = fechaNacimiento.withYear(hoy.year)
        return if (esteAnio.isBefore(hoy) || esteAnio.isEqual(hoy)) {
            // Si ya pasó o es hoy, y el cumple de hoy aún no se ha consumido,
            // devolvemos hoy; si ya pasó, el año que viene.
            if (esteAnio.isEqual(hoy)) hoy else esteAnio.plusYears(1)
        } else {
            esteAnio
        }
    }

    /** Días que faltan para el próximo cumpleaños (0 si es hoy). */
    fun daysUntil(fechaNacimiento: LocalDate, hoy: LocalDate = LocalDate.now()): Long {
        val next = nextOccurrence(fechaNacimiento, hoy)
        return ChronoUnit.DAYS.between(hoy, next)
    }

    /** Edad que cumple en la próxima ocurrencia (o la que cumple hoy). */
    fun ageOnNextBirthday(fechaNacimiento: LocalDate, hoy: LocalDate = LocalDate.now()): Int {
        val next = nextOccurrence(fechaNacimiento, hoy)
        return Period.between(fechaNacimiento, next).years
    }

    /** ¿Es hoy su cumpleaños? */
    fun isToday(fechaNacimiento: LocalDate, hoy: LocalDate = LocalDate.now()): Boolean {
        return fechaNacimiento.monthValue == hoy.monthValue &&
            fechaNacimiento.dayOfMonth == hoy.dayOfMonth
    }

    /** Fecha y hora exacta en la que debe sonar la alarma (día del cumple a la hora indicada). */
    fun alarmDateTime(
        fechaNacimiento: LocalDate,
        horaMinutos: Int,
        hoy: LocalDate = LocalDate.now()
    ): java.time.LocalDateTime {
        val next = nextOccurrence(fechaNacimiento, hoy)
        return next.atStartOfDay().plusMinutes(horaMinutos.toLong())
    }

    /** Fecha y hora del aviso anticipado (N días antes a la hora indicada). */
    fun advanceAlarmDateTime(
        fechaNacimiento: LocalDate,
        diasAntes: Int,
        horaMinutos: Int,
        hoy: LocalDate = LocalDate.now()
    ): java.time.LocalDateTime? {
        if (diasAntes <= 0) return null
        val next = nextOccurrence(fechaNacimiento, hoy)
        val aviso = next.minusDays(diasAntes.toLong())
        val dateTime = aviso.atStartOfDay().plusMinutes(horaMinutos.toLong())
        return if (dateTime.isAfter(java.time.LocalDateTime.now())) dateTime else null
    }
}