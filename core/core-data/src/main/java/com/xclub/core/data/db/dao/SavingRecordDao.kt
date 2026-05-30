package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.SavingRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingRecordDao {
    @Query("SELECT * FROM saving_records WHERE planId = :planId ORDER BY date DESC")
    fun getByPlanId(planId: Long): Flow<List<SavingRecordEntity>>

    @Insert
    suspend fun insert(record: SavingRecordEntity): Long

    @Delete
    suspend fun delete(record: SavingRecordEntity)
}
