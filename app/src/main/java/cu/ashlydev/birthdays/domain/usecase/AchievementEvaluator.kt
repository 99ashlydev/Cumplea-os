/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.domain.usecase

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

    suspend fun evaluateStateAchievements(): List<AchievementKey> {
        val unlocked = mutableListOf<AchievementKey>()
        val persons = personRepository.getAllOnce()
        val count = persons.size

        if (count >= 1) tryUnlock(AchievementKey.FIRST_PERSON)?.let { unlocked += it }
        if (count >= 5) tryUnlock(AchievementKey.FIVE_PEOPLE)?.let { unlocked += it }
        if (count >= 10) tryUnlock(AchievementKey.TEN_PEOPLE)?.let { unlocked += it }
        if (count >= 25) tryUnlock(AchievementKey.TWENTY_FIVE_PEOPLE)?.let { unlocked += it }
        if (persons.any { !it.fotoUri.isNullOrBlank() }) tryUnlock(AchievementKey.HAS_PHOTO)?.let { unlocked += it }
        if (persons.any { it.grupoId != null }) tryUnlock(AchievementKey.HAS_GROUP)?.let { unlocked += it }

        val settings = settingsRepository.settings.first()
        if (settings.avisoAnticipadoDias > 0) tryUnlock(AchievementKey.SET_ADVANCE)?.let { unlocked += it }

        val stats = achievementRepository.getStats()
        val elapsed = System.currentTimeMillis() - stats.primeraVezEnMillis
        val days = elapsed / (1000L * 60 * 60 * 24)
        if (days >= 30) tryUnlock(AchievementKey.THIRTY_DAYS)?.let { unlocked += it }
        if (days >= 365) tryUnlock(AchievementKey.ONE_YEAR)?.let { unlocked += it }

        return unlocked
    }

    suspend fun onWhatsAppSent(): List<AchievementKey> {
        val unlocked = mutableListOf<AchievementKey>()
        achievementRepository.updateStats { s ->
            s.copy(felicitacionesEnviadas = s.felicitacionesEnviadas + 1)
        }
        val stats = achievementRepository.getStats()
        if (stats.felicitacionesEnviadas >= 1) tryUnlock(AchievementKey.WHATSAPP_ONE)?.let { unlocked += it }
        if (stats.felicitacionesEnviadas >= 10) tryUnlock(AchievementKey.WHATSAPP_TEN)?.let { unlocked += it }
        return unlocked
    }

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

    suspend fun resetStreak() {
        achievementRepository.updateStats { it.copy(rachaActual = 0) }
    }

    private suspend fun tryUnlock(key: AchievementKey): AchievementKey? {
        val wasNew = achievementRepository.unlock(key)
        return if (wasNew) key else null
    }

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