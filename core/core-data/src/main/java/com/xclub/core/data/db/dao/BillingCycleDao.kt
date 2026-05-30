package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.BillingCycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillingCycleDao {
    @Query("SELECT * FROM billing_cycles WHERE isDefault = 1 LIMIT 1")
    fun getDefault(): Flow<BillingCycleEntity?>

    @Query("SELECT * FROM billing_cycles ORDER BY id ASC")
    fun getAll(): Flow<List<BillingCycleEntity>>

    @Insert
    suspend fun insert(cycle: BillingCycleEntity): Long

    @Update
    suspend fun update(cycle: BillingCycleEntity)

    @Delete
    suspend fun delete(cycle: BillingCycleEntity)
}
