package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.WebBookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WebBookmarkDao {
    @Query("SELECT * FROM web_bookmarks ORDER BY sortOrder ASC, createdAt DESC")
    fun getAll(): Flow<List<WebBookmarkEntity>>

    @Query("SELECT * FROM web_bookmarks WHERE `group` = :group ORDER BY sortOrder ASC")
    fun getByGroup(group: String): Flow<List<WebBookmarkEntity>>

    @Query("SELECT * FROM web_bookmarks WHERE id = :id")
    suspend fun getById(id: Long): WebBookmarkEntity?

    @Insert
    suspend fun insert(bookmark: WebBookmarkEntity): Long

    @Update
    suspend fun update(bookmark: WebBookmarkEntity)

    @Delete
    suspend fun delete(bookmark: WebBookmarkEntity)
}
