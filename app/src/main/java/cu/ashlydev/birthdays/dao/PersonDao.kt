/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cu.ashlydev.birthdays.data.local.entity.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Query("SELECT * FROM persons WHERE activo = 1 ORDER BY nombre COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PersonEntity?

    @Query("SELECT * FROM persons WHERE activo = 1")
    suspend fun getAllOnce(): List<PersonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: PersonEntity): Long

    @Update
    suspend fun update(person: PersonEntity)

    @Delete
    suspend fun delete(person: PersonEntity)

    @Query("DELETE FROM persons WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE persons SET activo = 0 WHERE id = :id")
    suspend fun archive(id: Long)

    @Query("SELECT COUNT(*) FROM persons WHERE activo = 1")
    suspend fun count(): Int

    @Query("DELETE FROM persons")
    suspend fun clearAll()
}