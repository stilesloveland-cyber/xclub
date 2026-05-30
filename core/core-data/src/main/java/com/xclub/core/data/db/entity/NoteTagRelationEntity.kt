package com.xclub.core.data.db.entity

import androidx.room.Entity

@Entity(tableName = "note_tag_relations", primaryKeys = ["noteId", "tagId"])
data class NoteTagRelationEntity(
    val noteId: Long,
    val tagId: Long
)
