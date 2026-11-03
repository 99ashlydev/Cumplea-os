/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.ashlydev.birthdays.R
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.domain.usecase.GetUpcomingBirthdaysUseCase
import cu.ashlydev.birthdays.domain.usecase.UpcomingBirthday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TodayUiState(
    val today: List<UpcomingBirthday> = emptyList(),
    val next7: List<UpcomingBirthday> = emptyList()
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    personRepository: PersonRepository,
    private val getUpcoming: GetUpcomingBirthdaysUseCase
) : ViewModel() {
    val uiState: StateFlow<TodayUiState> = personRepository.observeAll()
        .map { persons ->
            val all = getUpcoming(persons)
            TodayUiState(
                today = all.filter { it.isToday },
                next7 = all.filter { !it.isToday && it.daysUntil in 1..7 }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayUiState())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    onPersonClick: (Long) -> Unit,
    onGreetingClick: (Long) -> Unit,
    viewModel: TodayViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_today)) }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Hoy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }
            if (state.today.isEmpty()) {
                item {
                    Text(
                        "No hay cumpleaños hoy",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                items(state.today, key = { "today_${it.person.id}" }) { item ->
                    TodayCard(
                        item = item,
                        onClick = { onPersonClick(item.person.id) },
                        onGreeting = { onGreetingClick(item.person.id) }
                    )
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Próximos 7 días",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
            }
            if (state.next7.isEmpty()) {
                item { Text("Nada en los próximos 7 días", style = MaterialTheme.typography.bodyMedium) }
            } else {
                items(state.next7, key = { "next_${it.person.id}" }) { item ->
                    TodayCard(item = item, onClick = { onPersonClick(item.person.id) }, onGreeting = null)
                }
            }
        }
    }
}

@Composable
private fun TodayCard(item: UpcomingBirthday, onClick: () -> Unit, onGreeting: (() -> Unit)?) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isToday)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        item.person.nombre.firstOrNull()?.uppercase()?.toString() ?: "?",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.person.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (item.isToday) stringResource(R.string.today_is_birthday)
                        else stringResource(R.string.next_birthday_in_days, item.daysUntil.toInt()),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(stringResource(R.string.turns_years, item.ageTurning), style = MaterialTheme.typography.bodyMedium)
                }
                if (item.isToday) Icon(Icons.Filled.Cake, contentDescription = null)
            }
            if (onGreeting != null) {
                Spacer(Modifier.height(12.dp))
                Button(onClick = onGreeting, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.CardGiftcard, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.alarm_send_whatsapp))
                }
            }
        }
    }
}