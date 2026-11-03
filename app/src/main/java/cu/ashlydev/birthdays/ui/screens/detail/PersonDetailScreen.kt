/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.ashlydev.birthdays.R
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.domain.model.Person
import cu.ashlydev.birthdays.domain.util.BirthdayCalculator
import cu.ashlydev.birthdays.whatsapp.WhatsAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val repository: PersonRepository
) : ViewModel() {
    private val _person = MutableStateFlow<Person?>(null)
    val person: StateFlow<Person?> = _person.asStateFlow()

    fun load(id: Long) = viewModelScope.launch { _person.value = repository.getById(id) }
    fun delete(p: Person) = viewModelScope.launch { repository.delete(p) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onGreeting: (Long) -> Unit,
    viewModel: PersonDetailViewModel = hiltViewModel()
) {
    val person by viewModel.person.collectAsState()
    val context = LocalContext.current
    androidx.compose.runtime.LaunchedEffect(personId) { viewModel.load(personId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person?.nombre ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    person?.let { p ->
                        IconButton(onClick = { onEdit(p.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { viewModel.delete(p); onBack() }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            )
        }
    ) { padding ->
        person?.let { p ->
            Column(
                Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
            ) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        val days = BirthdayCalculator.daysUntil(p.fechaNacimiento)
                        val age = BirthdayCalculator.ageOnNextBirthday(p.fechaNacimiento)
                        val isToday = BirthdayCalculator.isToday(p.fechaNacimiento)
                        Text(
                            if (isToday) stringResource(R.string.today_is_birthday)
                            else stringResource(R.string.next_birthday_in_days, days.toInt()),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Fecha: ${p.fechaNacimiento}")
                        Text(stringResource(R.string.turns_years, age))
                        p.telefono?.let { Text("Tel: $it") }
                        p.notas?.let { Text("Notas: $it") }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onGreeting(p.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(Modifier.height(0.dp))
                    Text("  ${stringResource(R.string.alarm_send_whatsapp)}")
                }
                Spacer(Modifier.height(8.dp))
                p.telefono?.let { tel ->
                    Button(
                        onClick = { WhatsAppHelper.openWhatsApp(context, p) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Phone, contentDescription = null)
                        Text("  WhatsApp directo")
                    }
                }
            }
        }
    }
}