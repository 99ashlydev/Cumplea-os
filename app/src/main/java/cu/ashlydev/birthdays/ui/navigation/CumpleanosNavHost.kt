/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cu.ashlydev.birthdays.ui.components.CumpleanosBottomBar
import cu.ashlydev.birthdays.ui.screens.birthdays.BirthdaysScreen
import cu.ashlydev.birthdays.ui.screens.calendar.CalendarScreen
import cu.ashlydev.birthdays.ui.screens.detail.PersonDetailScreen
import cu.ashlydev.birthdays.ui.screens.edit.PersonEditScreen
import cu.ashlydev.birthdays.ui.screens.greeting.GreetingCardScreen
import cu.ashlydev.birthdays.ui.screens.settings.SettingsScreen
import cu.ashlydev.birthdays.ui.screens.stats.StatsScreen
import cu.ashlydev.birthdays.ui.screens.today.TodayScreen

@Composable
fun CumpleanosNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = BottomTab.all.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                CumpleanosBottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.BIRTHDAYS,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ---- Tabs principales ----
            composable(Routes.BIRTHDAYS) {
                BirthdaysScreen(
                    onAddClick = { navController.navigate(Routes.PERSON_NEW) },
                    onPersonClick = { id -> navController.navigate(Routes.personDetail(id)) }
                )
            }
            composable(Routes.TODAY) {
                TodayScreen(
                    onPersonClick = { id -> navController.navigate(Routes.personDetail(id)) },
                    onGreetingClick = { id -> navController.navigate(Routes.greetingCard(id)) }
                )
            }
            composable(Routes.CALENDAR) {
                CalendarScreen(
                    onPersonClick = { id -> navController.navigate(Routes.personDetail(id)) }
                )
            }
            composable(Routes.STATS) {
                StatsScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }

            // ---- Detalle ----
            composable(
                route = Routes.PERSON_DETAIL,
                arguments = listOf(navArgument("personId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("personId") ?: 0L
                PersonDetailScreen(
                    personId = id,
                    onBack = { navController.popBackStack() },
                    onEdit = { pid -> navController.navigate(Routes.personEdit(pid)) },
                    onGreeting = { pid -> navController.navigate(Routes.greetingCard(pid)) }
                )
            }

            // ---- Nueva persona ----
            composable(Routes.PERSON_NEW) {
                PersonEditScreen(
                    personId = null,
                    onBack = { navController.popBackStack() }
                )
            }

            // ---- Editar persona ----
            composable(
                route = Routes.PERSON_EDIT,
                arguments = listOf(navArgument("personId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("personId") ?: 0L
                PersonEditScreen(
                    personId = id,
                    onBack = { navController.popBackStack() }
                )
            }

            // ---- Tarjeta de felicitación ----
            composable(
                route = Routes.GREETING_CARD,
                arguments = listOf(navArgument("personId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("personId") ?: 0L
                GreetingCardScreen(
                    personId = id,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}