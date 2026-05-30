package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.TodoEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY priority ASC, dueDate ASC")
    fun getActive(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompleted(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE dueDate = :date AND isCompleted = 0")
    fun getByDate(date: LocalDate): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getById(id: Long): TodoEntity?

    @Insert
    suspend fun insert(todo: TodoEntity): Long

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}
