package com.xclub.sync.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val module: String,
    val lastSyncVersion: Long,
    val records: List<SyncRecord>
)

@Serializable
data class SyncRecord(
    val id: Long,
    val action: String,
    val data: String,
    val updatedAt: Long,
    val syncVersion: Long
)

@Serializable
data class SyncResponse(
    val currentVersion: Long,
    val records: List<SyncRecord>
)

@Serializable
data class AuthRequest(
    val action: String,
    val token: String? = null
)

@Serializable
data class AuthResponse(
    val token: String,
    val status: String
)

@Serializable
data class UpdateInfo(
    val versionCode: Long,
    val versionName: String,
    val changelog: String,
    val downloadUrl: String
)
