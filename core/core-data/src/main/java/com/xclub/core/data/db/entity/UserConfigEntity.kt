package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_configs")
data class UserConfigEntity(
    @PrimaryKey val key: String,
    val value: String
)
