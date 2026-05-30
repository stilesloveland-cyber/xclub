package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_tags")
data class NoteTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
