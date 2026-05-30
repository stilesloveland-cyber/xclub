package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "billing_cycles")
data class BillingCycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val payday: Int? = null,
    val startOffset: Int = 0,
    val endOffset: Int = 0,
    val isDefault: Boolean = false
)
