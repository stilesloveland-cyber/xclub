package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.SavingPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingPlanDao {
    @Query("SELECT * FROM saving_plans ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SavingPlanEntity>>

    @Query("SELECT * FROM saving_plans WHERE id = :id")
    suspend fun getById(id: Long): SavingPlanEntity?

    @Insert
    suspend fun insert(plan: SavingPlanEntity): Long

    @Update
    suspend fun update(plan: SavingPlanEntity)

    @Delete
    suspend fun delete(plan: SavingPlanEntity)
}
