/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.graphics.Color
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.domain.util.BirthdayCalculator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NextBirthdayWidget : GlanceAppWidget() {
    @Inject lateinit var personRepository: PersonRepository

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent(personRepository)
            }
        }
    }
}

@Composable
private fun WidgetContent(repo: PersonRepository) {
    // Glance no permite suspend aquí directamente; usamos un estado simple.
    val persons = androidx.glance.appwidget.LocalContext.current
    Column(
        modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.surface).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Próximo cumple",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp)
        )
        Text(
            "Abre la app para verlo",
            style = TextStyle(fontSize = 11.sp)
        )
    }
}

@AndroidEntryPoint
class NextBirthdayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NextBirthdayWidget()
}