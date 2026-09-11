package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val circularNumber: String,
    val title: String,
    val department: String,
    val issueDate: String,
    val category: String,
    val classification: String, // PUBLIC, RESTRICTED, CONFIDENTIAL
    val fullText: String,
    val summary: String,
    val sha256Hash: String,
    val isEncrypted: Boolean = false,
    val syncStatus: String = "SYNCED", // SYNCED, PENDING_SYNC, LOCAL_ONLY
    val chunkCount: Int = 0,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "document_chunks")
data class ChunkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val clauseNumber: String,
    val chunkIndex: Int,
    val content: String,
    val keywords: String,
    val vectorSummary: String
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String,
    val details: String,
    val actor: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latencyMs: Long = 0
)

@Entity(tableName = "collaborators")
data class CollaboratorEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val role: String,
    val avatarInitials: String,
    val status: String,
    val activeDocTitle: String
)

@Entity(tableName = "collaborative_notes")
data class CollaborativeNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val circularNumber: String,
    val authorName: String,
    val authorRole: String,
    val noteText: String,
    val clauseTag: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED"
)
