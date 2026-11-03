/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTab(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
) {
    data object Birthdays : BottomTab("birthdays", cu.ashlydev.birthdays.R.string.tab_birthdays, Icons.Filled.Cake)
    data object Today : BottomTab("today", cu.ashlydev.birthdays.R.string.tab_today, Icons.Filled.Today)
    data object Calendar : BottomTab("calendar", cu.ashlydev.birthdays.R.string.tab_calendar, Icons.Filled.CalendarMonth)
    data object Stats : BottomTab("stats", cu.ashlydev.birthdays.R.string.tab_stats, Icons.Filled.Insights)
    data object Settings : BottomTab("settings", cu.ashlydev.birthdays.R.string.tab_settings, Icons.Filled.Settings)

    companion object {
        val all = listOf(Birthdays, Today, Calendar, Stats, Settings)
    }
}

object Routes {
    const val BIRTHDAYS = "birthdays"
    const val TODAY = "today"
    const val CALENDAR = "calendar"
    const val STATS = "stats"
    const val SETTINGS = "settings"

    const val PERSON_DETAIL = "person/{personId}"
    fun personDetail(id: Long) = "person/$id"

    const val PERSON_EDIT = "person/{personId}/edit"
    fun personEdit(id: Long) = "person/$id/edit"

    const val PERSON_NEW = "person/new"
    const val GREETING_CARD = "greeting/{personId}"
    fun greetingCard(id: Long) = "greeting/$id"
}