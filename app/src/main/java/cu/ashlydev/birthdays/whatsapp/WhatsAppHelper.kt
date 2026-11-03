/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.whatsapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import cu.ashlydev.birthdays.domain.model.Person

object WhatsAppHelper {

    private val MENSAJES = listOf(
        "¡Feliz cumpleaños, %s! 🎂 Que tengas un día increíble y un año lleno de cosas buenas. 🎉",
        "¡Muchas felicidades, %s! 🥳 Hoy es tu día, disfrútalo al máximo. 🎈",
        "¡Feliz vuelta al sol, %s! 🌟 Que se cumplan todos tus deseos. 🎁",
        "¡Happy birthday, %s! 🎂 Que este año venga cargado de alegrías. 🎊"
    )

    fun buildMessage(nombre: String): String {
        val base = MENSAJES.random().format(nombre)
        return base
    }

    /**
     * Construye un PendingIntent que abre WhatsApp (o WhatsApp Business) con el texto prellenado.
     * Si el teléfono existe, intenta abrir el chat directo; si no, deja elegir contacto.
     */
    fun buildWhatsAppPendingIntent(context: Context, person: Person): PendingIntent {
        val phone = person.telefono?.filter { it.isDigit() }
        val uri = if (!phone.isNullOrEmpty()) {
            Uri.parse("https://wa.me/$phone?text=${Uri.encode(buildMessage(person.nombre))}")
        } else {
            Uri.parse("https://wa.me/?text=${Uri.encode(buildMessage(person.nombre))}")
        }

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val fallback = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val chooser = Intent.createChooser(fallback, "WhatsApp").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return PendingIntent.getActivity(
            context,
            person.id.toInt() + 5000,
            chooser,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Abre WhatsApp directamente desde UI (no PendingIntent). */
    fun openWhatsApp(context: Context, person: Person) {
        val phone = person.telefono?.filter { it.isDigit() }
        val uri = if (!phone.isNullOrEmpty()) {
            Uri.parse("https://wa.me/$phone?text=${Uri.encode(buildMessage(person.nombre))}")
        } else {
            Uri.parse("https://wa.me/?text=${Uri.encode(buildMessage(person.nombre))}")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        runCatching { context.startActivity(intent) }
    }
}