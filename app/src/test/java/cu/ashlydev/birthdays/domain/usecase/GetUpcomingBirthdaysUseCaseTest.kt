/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.usecase

import cu.ashlydev.birthdays.domain.model.Person
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetUpcomingBirthdaysUseCaseTest {

    private val useCase = GetUpcomingBirthdaysUseCase()
    private val hoy = LocalDate.of(2026, 3, 15)

    @Test
    fun `ordena por dias hasta el cumple`() {
        val persons = listOf(
            Person(id = 1, nombre = "Ana", fechaNacimiento = LocalDate.of(1990, 12, 1)),
            Person(id = 2, nombre = "Luis", fechaNacimiento = LocalDate.of(1985, 3, 20)),
            Person(id = 3, nombre = "Marta", fechaNacimiento = LocalDate.of(1992, 3, 15))
        )
        val result = useCase(persons, hoy)
        assertEquals(listOf("Marta", "Luis", "Ana"), result.map { it.person.nombre })
    }

    @Test
    fun `marca isToday correctamente`() {
        val persons = listOf(
            Person(id = 1, nombre = "Marta", fechaNacimiento = LocalDate.of(1992, 3, 15))
        )
        val result = useCase(persons, hoy)
        assertEquals(true, result.first().isToday)
    }

    @Test
    fun `ignora personas inactivas`() {
        val persons = listOf(
            Person(id = 1, nombre = "Inactivo", fechaNacimiento = LocalDate.of(1990, 3, 15), activo = false)
        )
        val result = useCase(persons, hoy)
        assertEquals(0, result.size)
    }
}