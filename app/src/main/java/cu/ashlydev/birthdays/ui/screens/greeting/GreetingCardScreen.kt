/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.screens.greeting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.domain.model.Person
import cu.ashlydev.birthdays.ui.theme.ConfettiColors
import cu.ashlydev.birthdays.whatsapp.WhatsAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GreetingViewModel @Inject constructor(
    private val repository: PersonRepository
) : ViewModel() {
    private val _person = MutableStateFlow<Person?>(null)
    val person: StateFlow<Person?> = _person.asStateFlow()
    fun load(id: Long) = viewModelScope.launch { _person.value = repository.getById(id) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreetingCardScreen(
    personId: Long,
    onBack: () -> Unit,
    viewModel: GreetingViewModel = hiltViewModel()
) {
    val person by viewModel.person.collectAsState()
    val context = LocalContext.current
    androidx.compose.runtime.LaunchedEffect(personId) { viewModel.load(personId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarjeta de felicitación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        person?.let { p ->
            Column(
                Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(ConfettiColors.take(3)),
                                shape = MaterialTheme.shapes.extraLarge
                            )
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("🎂", style = MaterialTheme.typography.displayLarge)
                            Text(
                                "¡Feliz cumpleaños, ${p.nombre}!",
                                style = MaterialTheme.typography.headlineMedium,
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                WhatsAppHelper.buildMessage(p.nombre),
                                style = MaterialTheme.typography.bodyLarge,
                                color = androidx.compose.ui.graphics.Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { WhatsAppHelper.openWhatsApp(context, p) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enviar por WhatsApp")
                }
            }
        }
    }
}