package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val remark: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now(),
    val syncVersion: Long = 0
)
