package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdTimestamp DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: Long): DocumentEntity?

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    fun observeDocumentById(id: Long): Flow<DocumentEntity?>

    @Query("SELECT * FROM documents WHERE title LIKE '%' || :query || '%' OR circularNumber LIKE '%' || :query || '%' OR department LIKE '%' || :query || '%'")
    fun searchDocuments(query: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity): Long

    @Update
    suspend fun updateDocument(doc: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocument(id: Long)

    @Query("SELECT COUNT(*) FROM documents")
    fun getDocumentCount(): Flow<Int>
}

@Dao
interface ChunkDao {
    @Query("SELECT * FROM document_chunks WHERE documentId = :docId ORDER BY chunkIndex ASC")
    suspend fun getChunksForDocument(docId: Long): List<ChunkEntity>

    @Query("SELECT * FROM document_chunks")
    suspend fun getAllChunks(): List<ChunkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<ChunkEntity>)

    @Query("DELETE FROM document_chunks WHERE documentId = :docId")
    suspend fun deleteChunksForDocument(docId: Long)

    @Query("SELECT COUNT(*) FROM document_chunks")
    fun getChunkCount(): Flow<Int>
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 50")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity): Long
}

@Dao
interface CollaboratorDao {
    @Query("SELECT * FROM collaborators")
    fun getAllCollaborators(): Flow<List<CollaboratorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollaborators(collabs: List<CollaboratorEntity>)
}

@Dao
interface CollaborativeNoteDao {
    @Query("SELECT * FROM collaborative_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<CollaborativeNoteEntity>>

    @Query("SELECT * FROM collaborative_notes WHERE documentId = :docId ORDER BY timestamp DESC")
    fun getNotesForDocument(docId: Long): Flow<List<CollaborativeNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: CollaborativeNoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<CollaborativeNoteEntity>)

    @Query("DELETE FROM collaborative_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}

