/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.repository

import cu.ashlydev.birthdays.data.local.dao.PersonDao
import cu.ashlydev.birthdays.data.mapper.toDomain
import cu.ashlydev.birthdays.data.mapper.toEntity
import cu.ashlydev.birthdays.domain.model.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonRepository @Inject constructor(
    private val dao: PersonDao
) {
    fun observeAll(): Flow<List<Person>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: Long): Person? = dao.getById(id)?.toDomain()

    suspend fun getAllOnce(): List<Person> = dao.getAllOnce().map { it.toDomain() }

    suspend fun upsert(person: Person): Long {
        return if (person.id == 0L) dao.insert(person.toEntity())
        else { dao.update(person.toEntity()); person.id }
    }

    suspend fun delete(person: Person) = dao.delete(person.toEntity())

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun archive(id: Long) = dao.archive(id)

    suspend fun count(): Int = dao.count()

    suspend fun replaceAll(persons: List<Person>) {
        dao.clearAll()
        persons.forEach { dao.insert(it.toEntity()) }
    }
}