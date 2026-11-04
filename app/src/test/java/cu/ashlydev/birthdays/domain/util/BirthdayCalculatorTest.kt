/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BirthdayCalculatorTest {

    @Test
    fun `next occurrence es hoy si es su cumple`() {
        val hoy = LocalDate.of(2026, 3, 15)
        val nacimiento = LocalDate.of(1990, 3, 15)
        assertEquals(hoy, BirthdayCalculator.nextOccurrence(nacimiento, hoy))
    }

    @Test
    fun `next occurrence es este año si aun no ha llegado`() {
        val hoy = LocalDate.of(2026, 1, 10)
        val nacimiento = LocalDate.of(1990, 5, 20)
        assertEquals(LocalDate.of(2026, 5, 20), BirthdayCalculator.nextOccurrence(nacimiento, hoy))
    }

    @Test
    fun `next occurrence es el proximo año si ya paso`() {
        val hoy = LocalDate.of(2026, 12, 30)
        val nacimiento = LocalDate.of(1990, 1, 5)
        assertEquals(LocalDate.of(2027, 1, 5), BirthdayCalculator.nextOccurrence(nacimiento, hoy))
    }

    @Test
    fun `days until es 0 si es hoy`() {
        val hoy = LocalDate.of(2026, 3, 15)
        val nacimiento = LocalDate.of(1990, 3, 15)
        assertEquals(0L, BirthdayCalculator.daysUntil(nacimiento, hoy))
    }

    @Test
    fun `days until calcula correctamente`() {
        val hoy = LocalDate.of(2026, 1, 1)
        val nacimiento = LocalDate.of(1990, 1, 10)
        assertEquals(9L, BirthdayCalculator.daysUntil(nacimiento, hoy))
    }

    @Test
    fun `age turning es correcta`() {
        val hoy = LocalDate.of(2026, 1, 1)
        val nacimiento = LocalDate.of(1990, 5, 20)
        assertEquals(36, BirthdayCalculator.ageOnNextBirthday(nacimiento, hoy))
    }

    @Test
    fun `isToday detecta cumple correctamente`() {
        val hoy = LocalDate.of(2026, 3, 15)
        assertTrue(BirthdayCalculator.isToday(LocalDate.of(1990, 3, 15), hoy))
        assertFalse(BirthdayCalculator.isToday(LocalDate.of(1990, 3, 16), hoy))
    }

    @Test
    fun `nacio un 29 de febrero no rompe en año no bisiesto`() {
        val hoy = LocalDate.of(2026, 1, 1)
        val nacimiento = LocalDate.of(2000, 2, 29)
        val next = BirthdayCalculator.nextOccurrence(nacimiento, hoy)
        assertEquals(2, next.monthValue)
        assertTrue(next.dayOfMonth in listOf(28, 29, 1))
    }
}