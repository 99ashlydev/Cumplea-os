/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.data.repository.SettingsRepository
import cu.ashlydev.birthdays.domain.util.BirthdayCalculator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class BirthdayAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var personRepository: PersonRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AlarmScheduler.ACTION_ALARM) return
        val personId = intent.getLongExtra(AlarmScheduler.EXTRA_PERSON_ID, -1L)
        val isAdvance = intent.getBooleanExtra(AlarmScheduler.EXTRA_IS_ADVANCE, false)
        if (personId <= 0) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val person = personRepository.getById(personId) ?: return@launch
                val settings = settingsRepository.settings.first()
                if (!settings.notificacionesActivas) return@launch

                val daysUntil = BirthdayCalculator.daysUntil(person.fechaNacimiento)
                val age = BirthdayCalculator.ageOnNextBirthday(person.fechaNacimiento)

                notificationHelper.showBirthdayNotification(
                    person = person,
                    daysUntil = if (isAdvance) daysUntil.toInt() else 0,
                    ageTurning = age,
                    tonoUri = settings.tonoNotificacionUri,
                    vibrar = settings.vibracion
                )

                // Programa re-notificación a las 2 horas (si sigue sin descartarse)
                val work = OneTimeWorkRequestBuilder<RescheduleWorker>()
                    .setInitialDelay(2, TimeUnit.HOURS)
                    .setInputData(
                        androidx.work.workDataOf(RescheduleWorker.KEY_PERSON_ID to personId)
                    )
                    .build()
                WorkManager.getInstance(context).enqueue(work)

                // Re-agenda para el próximo año (así no depende solo de BOOT)
                alarmScheduler.schedule(person, settings)

                // Desbloquea logro de recibir notificación
                achievementHook(personId)
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun achievementHook(personId: Long) {
        // Se rellena en AchievementEvaluator (Bloque 4) cuando el usuario descarte.
        // Aquí no evaluamos para no inflar; el evaluador se llama desde el ViewModel.
    }
}