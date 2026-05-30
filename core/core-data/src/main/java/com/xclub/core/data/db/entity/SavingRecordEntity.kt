package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "saving_records")
data class SavingRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val amount: Long,
    val isAuto: Boolean,
    val date: LocalDate,
    val createdAt: Instant
)
