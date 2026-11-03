/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cu.ashlydev.birthdays.data.local.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Query("SELECT * FROM groups ORDER BY nombre COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups")
    suspend fun getAllOnce(): List<GroupEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: GroupEntity): Long

    @Query("DELETE FROM groups WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM groups")
    suspend fun clearAll()
}