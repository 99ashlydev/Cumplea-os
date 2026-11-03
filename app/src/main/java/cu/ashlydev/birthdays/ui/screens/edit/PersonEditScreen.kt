/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.screens.edit

import android.app.DatePickerDialog
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PersonEditViewModel @Inject constructor(
    private val repository: PersonRepository
) : ViewModel() {
    fun load(id: Long): Person? = null // se rellena con estado en pantalla
    suspend fun get(id: Long): Person? = repository.getById(id)
    fun save(person: Person, onDone: () -> Unit) = viewModelScope.launch {
        repository.upsert(person); onDone()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonEditScreen(
    personId: Long?,
    onBack: () -> Unit,
    viewModel: PersonEditViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf<LocalDate?>(null) }
    var telefono by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var existing by remember { mutableStateOf<Person?>(null) }

    androidx.compose.runtime.LaunchedEffect(personId) {
        if (personId != null && personId > 0) {
            viewModel.get(personId)?.let {
                existing = it
                nombre = it.nombre
                fecha = it.fechaNacimiento
                telefono = it.telefono ?: ""
                notas = it.notas ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (personId == null || personId == 0L) stringResource(R.string.action_add) else stringResource(R.string.action_edit)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(stringResource(R.string.field_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = fecha?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.field_birthdate)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val init = fecha ?: LocalDate.now().minusYears(20)
                DatePickerDialog(
                    context,
                    { _, y, m, d -> fecha = LocalDate.of(y, m + 1, d) },
                    init.year, init.monthValue - 1, init.dayOfMonth
                ).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Elegir fecha")
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text(stringResource(R.string.field_phone)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text(stringResource(R.string.field_notes)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val f = fecha ?: return@Button
                    val p = (existing ?: Person(nombre = nombre, fechaNacimiento = f)).copy(
                        nombre = nombre,
                        fechaNacimiento = f,
                        telefono = telefono.takeIf { it.isNotBlank() },
                        notas = notas.takeIf { it.isNotBlank() }
                    )
                    viewModel.save(p, onBack)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && fecha != null
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}