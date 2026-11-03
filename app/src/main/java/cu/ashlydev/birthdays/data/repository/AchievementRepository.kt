/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.repository

import cu.ashlydev.birthdays.data.local.dao.AchievementDao
import cu.ashlydev.birthdays.data.local.entity.AchievementEntity
import cu.ashlydev.birthdays.data.local.entity.UserStatsEntity
import cu.ashlydev.birthdays.domain.model.AchievementKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepository @Inject constructor(
    private val dao: AchievementDao
) {
    fun observeAchievements(): Flow<List<AchievementEntity>> = dao.observeAll()
    fun observeStats(): Flow<UserStatsEntity?> = dao.observeStats()

    suspend fun unlock(key: AchievementKey): Boolean {
        val existing = dao.getByKey(key.name)
        if (existing?.desbloqueado == true) return false
        dao.upsert(
            AchievementEntity(
                key = key,
                desbloqueado = true,
                fechaDesbloqueo = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun getStats(): UserStatsEntity =
        dao.getStats() ?: UserStatsEntity().also { dao.upsertStats(it) }

    suspend fun updateStats(transform: (UserStatsEntity) -> UserStatsEntity) {
        val current = getStats()
        dao.upsertStats(transform(current))
    }

    suspend fun clearAll() {
        dao.clearAchievements()
        dao.clearStats()
    }
}