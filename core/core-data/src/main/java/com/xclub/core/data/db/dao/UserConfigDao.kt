package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.UserConfigEntity

@Dao
interface UserConfigDao {
    @Query("SELECT value FROM user_configs WHERE `key` = :key")
    suspend fun get(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(config: UserConfigEntity)

    @Query("DELETE FROM user_configs WHERE `key` = :key")
    suspend fun delete(key: String)
}
