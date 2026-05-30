package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String? = null,
    val priority: String,
    val dueDate: LocalDate? = null,
    val isCompleted: Boolean = false,
    val repeatRule: String? = null,
    val linkedNoteId: Long? = null,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now(),
    val syncVersion: Long = 0
)
