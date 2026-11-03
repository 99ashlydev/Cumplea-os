/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.repository

import cu.ashlydev.birthdays.data.local.dao.GroupDao
import cu.ashlydev.birthdays.data.mapper.toDomain
import cu.ashlydev.birthdays.data.mapper.toEntity
import cu.ashlydev.birthdays.domain.model.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
    private val dao: GroupDao
) {
    fun observeAll(): Flow<List<Group>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getAllOnce(): List<Group> = dao.getAllOnce().map { it.toDomain() }

    suspend fun upsert(group: Group): Long {
        val id = dao.insert(group.toEntity())
        return if (group.id == 0L) id else group.id
    }

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun replaceAll(groups: List<Group>) {
        dao.clearAll()
        groups.forEach { dao.insert(it.toEntity()) }
    }
}