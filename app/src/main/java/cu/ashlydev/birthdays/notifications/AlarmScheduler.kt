/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import cu.ashlydev.birthdays.domain.model.AppSettings
import cu.ashlydev.birthdays.domain.model.Person
import cu.ashlydev.birthdays.domain.util.BirthdayCalculator
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun canScheduleExact(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    /**
     * Programa la alarma principal (día del cumple) y la de aviso anticipado si aplica.
     * Cancela las anteriores para esa persona.
     */
    fun schedule(person: Person, settings: AppSettings) {
        cancel(person.id)

        val hora = person.horaNotificacionMinutos ?: settings.horaNotificacionMinutos
        val aviso = person.avisoAnticipadoDias ?: settings.avisoAnticipadoDias

        // Alarma principal
        val mainDateTime = BirthdayCalculator.alarmDateTime(person.fechaNacimiento, hora)
        val mainMillis = mainDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        setAlarm(person.id, mainMillis, isAdvance = false)

        // Aviso anticipado
        if (aviso > 0) {
            val advance = BirthdayCalculator.advanceAlarmDateTime(
                person.fechaNacimiento, aviso, hora
            )
            if (advance != null) {
                val advMillis = advance.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                setAlarm(person.id, advMillis, isAdvance = true)
            }
        }
    }

    fun cancel(personId: Long) {
        alarmManager.cancel(mainPending(personId, isAdvance = false))
        alarmManager.cancel(mainPending(personId, isAdvance = true))
    }

    fun rescheduleAll(persons: List<Person>, settings: AppSettings) {
        persons.filter { it.activo }.forEach { schedule(it, settings) }
    }

    private fun setAlarm(personId: Long, triggerAtMillis: Long, isAdvance: Boolean) {
        val pending = mainPending(personId, isAdvance)
        try {
            if (canScheduleExact()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerAtMillis, pending
                    )
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
                }
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pending
                )
            }
        } catch (se: SecurityException) {
            // Sin permiso exacto: fallback no-exacto
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
        }
    }

    private fun mainPending(personId: Long, isAdvance: Boolean): PendingIntent {
        val intent = Intent(context, BirthdayAlarmReceiver::class.java).apply {
            action = ACTION_ALARM
            putExtra(EXTRA_PERSON_ID, personId)
            putExtra(EXTRA_IS_ADVANCE, isAdvance)
        }
        val requestCode = personId.toInt() + if (isAdvance) ADVANCE_OFFSET else 0
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val ACTION_ALARM = "cu.ashlydev.birthdays.ACTION_ALARM"
        const val EXTRA_PERSON_ID = "extra_person_id"
        const val EXTRA_IS_ADVANCE = "extra_is_advance"
        const val ADVANCE_OFFSET = 100_000
    }
}