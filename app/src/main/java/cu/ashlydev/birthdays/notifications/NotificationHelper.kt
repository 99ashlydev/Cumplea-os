/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cu.ashlydev.birthdays.R
import cu.ashlydev.birthdays.domain.model.Person
import cu.ashlydev.birthdays.ui.alarm.AlarmActivity
import cu.ashlydev.birthdays.whatsapp.WhatsAppHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun showBirthdayNotification(
        person: Person,
        daysUntil: Int,
        ageTurning: Int,
        tonoUri: String?,
        vibrar: Boolean
    ) {
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra(AlarmActivity.EXTRA_PERSON_ID, person.id)
            putExtra(AlarmActivity.EXTRA_PERSON_NAME, person.nombre)
            putExtra(AlarmActivity.EXTRA_PERSON_AGE, ageTurning)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPending = PendingIntent.getActivity(
            context,
            person.id.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openAppIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra(AlarmActivity.EXTRA_PERSON_ID, person.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPending = PendingIntent.getActivity(
            context,
            person.id.toInt() + 1,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val whatsappPending = WhatsAppHelper.buildWhatsAppPendingIntent(context, person)

        val texto = if (daysUntil == 0) {
            context.getString(R.string.today_is_birthday)
        } else {
            context.getString(R.string.next_birthday_in_days, daysUntil)
        }

        val builder = NotificationCompat.Builder(context, NotificationChannels.ALARM)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🎂 ${person.nombre}")
            .setContentText("$texto — ${context.getString(R.string.turns_years, ageTurning)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(openAppPending)
            .setFullScreenIntent(fullScreenPending, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(0, context.getString(R.string.alarm_open_app), openAppPending)
            .addAction(0, context.getString(R.string.alarm_send_whatsapp), whatsappPending)
            .addAction(0, context.getString(R.string.alarm_dismiss), buildDismissPending(person.id.toInt()))

        if (vibrar) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE)
        }

        tonoUri?.let {
            if (it.isNotBlank()) builder.setSound(Uri.parse(it))
        }

        NotificationManagerCompat.from(context)
            .notify(person.id.toInt() + NOTIFICATION_ID_OFFSET, builder.build())
    }

    private fun buildDismissPending(requestCode: Int): PendingIntent {
        val intent = Intent(context, AlarmActivity::class.java).apply {
            action = AlarmActivity.ACTION_DISMISS
            putExtra(AlarmActivity.EXTRA_DISMISS_ID, requestCode)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            requestCode + 2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancelFor(personId: Long) {
        NotificationManagerCompat.from(context).cancel(personId.toInt() + NOTIFICATION_ID_OFFSET)
    }

    companion object {
        const val NOTIFICATION_ID_OFFSET = 1000
    }
}