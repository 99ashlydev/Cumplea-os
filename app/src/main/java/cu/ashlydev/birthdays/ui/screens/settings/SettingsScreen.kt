/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.screens.settings

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.ashlydev.birthdays.BuildConfig
import cu.ashlydev.birthdays.R
import cu.ashlydev.birthdays.backup.BackupManager
import cu.ashlydev.birthdays.data.repository.SettingsRepository
import cu.ashlydev.birthdays.domain.model.AppSettings
import cu.ashlydev.birthdays.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backupManager: BackupManager
) : ViewModel() {
    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setHora(m: Int) = viewModelScope.launch { settingsRepository.setHora(m) }
    fun setAviso(d: Int) = viewModelScope.launch { settingsRepository.setAviso(d) }
    fun setTono(u: String?) = viewModelScope.launch { settingsRepository.setTono(u) }
    fun setVibracion(b: Boolean) = viewModelScope.launch { settingsRepository.setVibracion(b) }
    fun setTheme(m: ThemeMode) = viewModelScope.launch { settingsRepository.setTheme(m) }
    fun setIdioma(l: String) = viewModelScope.launch { settingsRepository.setIdioma(l) }
    fun setNotifActivas(b: Boolean) = viewModelScope.launch { settingsRepository.setNotificacionesActivas(b) }

    fun export(uri: Uri) = viewModelScope.launch { backupManager.exportTo(uri) }
    fun import(uri: Uri) = viewModelScope.launch { backupManager.importFrom(uri) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    var showTheme by remember { mutableStateOf(false) }
    var showLang by remember { mutableStateOf(false) }
    var showAdvance by remember { mutableStateOf(false) }

    val ringtoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            viewModel.setTono(uri?.toString())
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { viewModel.export(it) } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.import(it) } }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_settings)) }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionTitle(stringResource(R.string.settings_notifications))

            SwitchRow(
                title = "Notificaciones activas",
                checked = settings.notificacionesActivas,
                onCheckedChange = viewModel::setNotifActivas
            )
            SettingRow(
                title = stringResource(R.string.settings_default_time),
                value = formatHora(settings.horaNotificacionMinutos)
            ) {
                TimePickerDialog(
                    context,
                    { _, h, m -> viewModel.setHora(h * 60 + m) },
                    settings.horaNotificacionMinutos / 60,
                    settings.horaNotificacionMinutos % 60,
                    true
                ).show()
            }
            SettingRow(
                title = stringResource(R.string.settings_ringtone),
                value = settings.tonoNotificacionUri?.let { "Personalizado" } ?: "Sistema"
            ) {
                val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                    settings.tonoNotificacionUri?.let {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(it))
                    }
                }
                ringtoneLauncher.launch(intent)
            }
            SettingRow(
                title = stringResource(R.string.settings_advance_days),
                value = "${settings.avisoAnticipadoDias} días"
            ) { showAdvance = true }
            SwitchRow(
                title = "Vibración",
                checked = settings.vibracion,
                onCheckedChange = viewModel::setVibracion
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!am.canScheduleExactAlarms()) {
                    Spacer(Modifier.height(8.dp))
                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text(stringResource(R.string.permission_exact_alarm_rationale))
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                val i = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    .setData(Uri.parse("package:${context.packageName}"))
                                context.startActivity(i)
                            }) { Text("Permitir alarmas exactas") }
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Spacer(Modifier.height(8.dp))
                Card {
                    Column(Modifier.padding(12.dp)) {
                        Text(stringResource(R.string.permission_notifications_rationale))
                    }
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            SectionTitle("Apariencia")
            SettingRow(
                title = stringResource(R.string.settings_theme),
                value = when (settings.themeMode) {
                    ThemeMode.SYSTEM -> "Sistema"
                    ThemeMode.LIGHT -> "Claro"
                    ThemeMode.DARK -> "Oscuro"
                }
            ) { showTheme = true }
            SettingRow(
                title = stringResource(R.string.settings_language),
                value = when (settings.idioma) {
                    "es" -> "Español"
                    "en" -> "English"
                    else -> "Sistema"
                }
            ) { showLang = true }

            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            SectionTitle(stringResource(R.string.settings_backup))
            Button(onClick = {
                exportLauncher.launch("cumpleanos-backup-${System.currentTimeMillis()}.json")
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_backup_export))
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { importLauncher.launch(arrayOf("application/json")) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_backup_import))
            }

            Spacer(Modifier.height(24.dp))

            // Versión centrada al pie
            Text(
                text = stringResource(
                    R.string.settings_version,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "© 2026+ Ashly Dev",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showTheme) {
        AlertDialog(
            onDismissRequest = { showTheme = false },
            title = { Text("Tema") },
            text = {
                Column {
                    listOf(
                        ThemeMode.SYSTEM to "Sistema",
                        ThemeMode.LIGHT to "Claro",
                        ThemeMode.DARK to "Oscuro"
                    ).forEach { (mode, label) ->
                        Text(label, Modifier.fillMaxWidth().clickable {
                            viewModel.setTheme(mode); showTheme = false
                        }.padding(12.dp))
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showTheme = false }) { Text("Cerrar") } }
        )
    }
    if (showLang) {
        AlertDialog(
            onDismissRequest = { showLang = false },
            title = { Text("Idioma") },
            text = {
                Column {
                    listOf("system" to "Sistema", "es" to "Español", "en" to "English")
                        .forEach { (code, label) ->
                            Text(label, Modifier.fillMaxWidth().clickable {
                                viewModel.setIdioma(code); showLang = false
                            }.padding(12.dp))
                        }
                }
            },
            confirmButton = { TextButton(onClick = { showLang = false }) { Text("Cerrar") } }
        )
    }
    if (showAdvance) {
        AlertDialog(
            onDismissRequest = { showAdvance = false },
            title = { Text("Aviso anticipado") },
            text = {
                Column {
                    listOf(0, 1, 3, 7, 14).forEach { d ->
                        Text("$d días", Modifier.fillMaxWidth().clickable {
                            viewModel.setAviso(d); showAdvance = false
                        }.padding(12.dp))
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAdvance = false }) { Text("Cerrar") } }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SettingRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun SwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private fun formatHora(min: Int): String = "%02d:%02d".format(min / 60, min % 60)