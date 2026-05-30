package com.xclub.core.data.db.entity

import androidx.room.Entity

@Entity(tableName = "note_links", primaryKeys = ["sourceNoteId", "targetNoteId"])
data class NoteLinkEntity(
    val sourceNoteId: Long,
    val targetNoteId: Long
)
