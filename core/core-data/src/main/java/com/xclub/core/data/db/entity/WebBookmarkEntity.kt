package com.xclub.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "web_bookmarks")
data class WebBookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val faviconUrl: String? = null,
    val group: String? = null,
    val sortOrder: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant
)
