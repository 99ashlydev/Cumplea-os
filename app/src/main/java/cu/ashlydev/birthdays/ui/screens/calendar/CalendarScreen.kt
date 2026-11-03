/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.ashlydev.birthdays.R
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.domain.model.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    personRepository: PersonRepository
) : ViewModel() {
    val persons: StateFlow<List<Person>> = personRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onPersonClick: (Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val persons by viewModel.persons.collectAsState()
    var month by remember { mutableStateOf(YearMonth.now()) }

    val birthdaysByDay = remember(persons, month) {
        persons.filter { it.activo }.groupBy { p ->
            if (p.fechaNacimiento.monthValue == month.monthValue) p.fechaNacimiento.dayOfMonth else -1
        }.filterKeys { it > 0 }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_calendar)) }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { month = month.minusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                }
                Text(
                    text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() }} ${month.year}",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { month = month.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }

            val firstDay = month.atDay(1)
            val offset = (firstDay.dayOfWeek.value - 1)
            val daysInMonth = month.lengthOfMonth()

            Column(Modifier.padding(horizontal = 8.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    listOf("L", "M", "X", "J", "V", "S", "D").forEach { d ->
                        Text(
                            d,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))

                var dayCounter = 1
                val totalCells = offset + daysInMonth
                val rows = (totalCells + 6) / 7

                repeat(rows) { row ->
                    Row(Modifier.fillMaxWidth()) {
                        repeat(7) { col ->
                            val cellIndex = row * 7 + col
                            if (cellIndex < offset || dayCounter > daysInMonth) {
                                Box(Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                val day = dayCounter
                                val dayPersons = birthdaysByDay[day].orEmpty()
                                Box(
                                    modifier = Modifier.weight(1f).aspectRatio(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (dayPersons.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer
                                                else androidx.compose.ui.graphics.Color.Transparent
                                            )
                                            .clickable(enabled = dayPersons.isNotEmpty()) {
                                                dayPersons.firstOrNull()?.let { onPersonClick(it.id) }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            day.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (dayPersons.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                                dayCounter++
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Cumpleaños del mes",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                items(birthdaysByDay.entries.sortedBy { it.key }, key = { it.key }) { (day, list) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable {
                            list.firstOrNull()?.let { onPersonClick(it.id) }
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$day", style = MaterialTheme.typography.titleMedium, modifier = Modifier.size(32.dp))
                        Text(
                            list.joinToString { it.nombre },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}