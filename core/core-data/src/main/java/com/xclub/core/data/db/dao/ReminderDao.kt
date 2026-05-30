package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE todoId = :todoId")
    fun getByTodoId(todoId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isTriggered = 0 AND remindAt <= :before")
    suspend fun getPendingBefore(before: Instant): List<ReminderEntity>

    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)
}
