/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.usecase

import cu.ashlydev.birthdays.domain.model.Person
import cu.ashlydev.birthdays.domain.util.BirthdayCalculator
import java.time.LocalDate
import javax.inject.Inject

data class UpcomingBirthday(
    val person: Person,
    val nextDate: LocalDate,
    val daysUntil: Long,
    val ageTurning: Int,
    val isToday: Boolean
)

class GetUpcomingBirthdaysUseCase @Inject constructor() {

    operator fun invoke(persons: List<Person>, hoy: LocalDate = LocalDate.now()): List<UpcomingBirthday> {
        return persons
            .filter { it.activo }
            .map { p ->
                UpcomingBirthday(
                    person = p,
                    nextDate = BirthdayCalculator.nextOccurrence(p.fechaNacimiento, hoy),
                    daysUntil = BirthdayCalculator.daysUntil(p.fechaNacimiento, hoy),
                    ageTurning = BirthdayCalculator.ageOnNextBirthday(p.fechaNacimiento, hoy),
                    isToday = BirthdayCalculator.isToday(p.fechaNacimiento, hoy)
                )
            }
            .sortedWith(
                compareBy<UpcomingBirthday> { it.daysUntil }
                    .thenBy { it.person.nombre.lowercase() }
            )
    }
}