package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val amount: Long,
    val categoryId: Long,
    val note: String? = null,
    val date: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now(),
    val syncVersion: Long = 0
)
