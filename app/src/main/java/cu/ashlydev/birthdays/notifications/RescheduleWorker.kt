/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.data.repository.SettingsRepository
import cu.ashlydev.birthdays.domain.util.BirthdayCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RescheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val personRepository: PersonRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val personId = inputData.getLong(KEY_PERSON_ID, -1L)
        if (personId <= 0) return Result.success()

        val person = personRepository.getById(personId) ?: return Result.success()
        val settings = settingsRepository.settings.first()
        if (!settings.notificacionesActivas) return Result.success()

        val days = BirthdayCalculator.daysUntil(person.fechaNacimiento)
        val age = BirthdayCalculator.ageOnNextBirthday(person.fechaNacimiento)

        // Solo re-notifica si sigue siendo hoy y no fue descartada (el descarte cancela el work)
        if (days == 0L) {
            notificationHelper.showBirthdayNotification(
                person = person,
                daysUntil = 0,
                ageTurning = age,
                tonoUri = settings.tonoNotificacionUri,
                vibrar = settings.vibracion
            )
        }
        return Result.success()
    }

    companion object {
        const val KEY_PERSON_ID = "person_id"
    }
}