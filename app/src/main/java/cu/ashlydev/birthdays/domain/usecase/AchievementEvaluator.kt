/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.usecase

import cu.ashlydev.birthdays.data.local.entity.UserStatsEntity
import cu.ashlydev.birthdays.data.repository.AchievementRepository
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.data.repository.SettingsRepository
import cu.ashlydev.birthdays.domain.model.AchievementKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementEvaluator @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val personRepository: PersonRepository,
    private val settingsRepository: SettingsRepository
) {

    /** Evalúa logros de "estado" (cantidad de personas, foto, grupo, etc.). */
    suspend fun evaluateStateAchievements(): List<AchievementKey> {
        val unlocked = mutableListOf<AchievementKey>()
        val persons = personRepository.getAllOnce()
        val count = persons.size

        if (count >= 1) unlocked += tryUnlock(AchievementKey.FIRST_PERSON)
        if (count >= 5) unlocked += tryUnlock(AchievementKey.FIVE_PEOPLE)
        if (count >= 10) unlocked += tryUnlock(AchievementKey.TEN_PEOPLE)
        if (count >= 25) unlocked += tryUnlock(AchievementKey.TWENTY_FIVE_PEOPLE)
        if (persons.any { !it.fotoUri.isNullOrBlank() }) unlocked += tryUnlock(AchievementKey.HAS_PHOTO)
        if (persons.any { it.grupoId != null }) unlocked += tryUnlock(AchievementKey.HAS_GROUP)

        val settings = settingsRepository.settings.first()
        if (settings.avisoAnticipadoDias > 0) unlocked += tryUnlock(AchievementKey.SET_ADVANCE)

        // Edad de la app: 30 días / 1 año
        val stats = achievementRepository.getStats()
        val elapsed = System.currentTimeMillis() - stats.primeraVezEnMillis
        val days = elapsed / (1000L * 60 * 60 * 24)
        if (days >= 30) unlocked += tryUnlock(AchievementKey.THIRTY_DAYS)
        if (days >= 365) unlocked += tryUnlock(AchievementKey.ONE_YEAR)

        return unlocked
    }

    /** Se llama cuando el usuario felicita por WhatsApp. */
    suspend fun onWhatsAppSent(): List<AchievementKey> {
        val unlocked = mutableListOf<AchievementKey>()
        achievementRepository.updateStats { s ->
            s.copy(felicitacionesEnviadas = s.felicitacionesEnviadas + 1)
        }
        val stats = achievementRepository.getStats()
        if (stats.felicitacionesEnviadas >= 1) unlocked += tryUnlock(AchievementKey.WHATSAPP_ONE)
        if (stats.felicitacionesEnviadas >= 10) unlocked += tryUnlock(AchievementKey.WHATSAPP_TEN)
        return unlocked
    }

    /** Se llama cuando el usuario descarta una notificación (equivale a "felicitó a tiempo"). */
    suspend fun onNotificationDismissed() {
        achievementRepository.updateStats { s ->
            val nueva = s.rachaActual + 1
            s.copy(
                rachaActual = nueva,
                mejorRacha = maxOf(s.mejorRacha, nueva)
            )
        }
        val stats = achievementRepository.getStats()
        if (stats.rachaActual >= 3) tryUnlock(AchievementKey.STREAK_3)
        if (stats.rachaActual >= 5) tryUnlock(AchievementKey.STREAK_5)
        if (stats.rachaActual >= 10) tryUnlock(AchievementKey.STREAK_10)
        tryUnlock(AchievementKey.RECEIVED_ONE_NOTIFICATION)
    }

    /** Rompe la racha (si pasó un cumple sin descartar). */
    suspend fun resetStreak() {
        achievementRepository.updateStats { it.copy(rachaActual = 0) }
    }

    private suspend fun tryUnlock(key: AchievementKey): AchievementKey? {
        val wasNew = achievementRepository.unlock(key)
        return if (wasNew) key else null
    }

    /** Texto y emoji por logro (para UI). */
    fun metadata(key: AchievementKey): Triple<String, String, String> = when (key) {
        AchievementKey.FIRST_PERSON -> Triple("Primer paso", "Añade tu primera persona", "🎂")
        AchievementKey.FIVE_PEOPLE -> Triple("Círculo íntimo", "Añade 5 personas", "👥")
        AchievementKey.TEN_PEOPLE -> Triple("Popular", "Añade 10 personas", "🎉")
        AchievementKey.TWENTY_FIVE_PEOPLE -> Triple("Coleccionista", "Añade 25 personas", "🏆")
        AchievementKey.HAS_PHOTO -> Triple("Cara a cara", "Añade una foto", "🖼️")
        AchievementKey.HAS_GROUP -> Triple("Organizado", "Crea un grupo", "🏷️")
        AchievementKey.SET_ADVANCE -> Triple("Planificador", "Configura un aviso anticipado", "📅")
        AchievementKey.RECEIVED_ONE_NOTIFICATION -> Triple("A tiempo", "Recibe y descarta una notificación", "🔔")
        AchievementKey.STREAK_3 -> Triple("Racha 3", "Felicita 3 cumples seguidos", "🔥")
        AchievementKey.STREAK_5 -> Triple("Racha 5", "Felicita 5 cumples seguidos", "🔥🔥")
        AchievementKey.STREAK_10 -> Triple("Racha 10", "Felicita 10 cumples seguidos", "🔥🔥🔥")
        AchievementKey.WHATSAPP_ONE -> Triple("Buen amigo", "Envía 1 felicitación por WhatsApp", "💬")
        AchievementKey.WHATSAPP_TEN -> Triple("Mensajero", "Envía 10 felicitaciones", "💌")
        AchievementKey.THIRTY_DAYS -> Triple("Veterano", "Usa la app 30 días", "🗓️")
        AchievementKey.ONE_YEAR -> Triple("Año completo", "Cumple 1 año con la app", "🎊")
    }
}