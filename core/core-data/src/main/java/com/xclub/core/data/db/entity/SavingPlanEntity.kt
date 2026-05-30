package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "saving_plans")
data class SavingPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Long,
    val currentAmount: Long,
    val deadline: LocalDate? = null,
    val autoDeductAmount: Long? = null,
    val autoDeductPeriod: String? = null,
    val autoDeductCustomDays: Int? = null,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now(),
    val syncVersion: Long = 0
)
