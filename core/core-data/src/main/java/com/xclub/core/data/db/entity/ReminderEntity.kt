package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val todoId: Long,
    val remindAt: Instant,
    val isTriggered: Boolean = false
)
