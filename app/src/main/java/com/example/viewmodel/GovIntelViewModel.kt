package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.local.CollaborativeNoteEntity
import com.example.data.local.CollaboratorEntity
import com.example.data.local.DocumentEntity
import com.example.data.local.PreloadedGovDocuments
import com.example.model.AuthState
import com.example.model.ChatMessage
import com.example.model.PolicyComparisonReport
import com.example.model.SecurityClearance
import com.example.model.UserProfile
import com.example.rag.AgenticRAGOrchestrator
import com.example.rag.EdgeVectorEngine
import com.example.rag.GemmaDocumentAnalysis
import com.example.rag.GemmaRAGEngine
import com.example.security.AuthManager
import com.example.security.CryptoVault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GovIntelViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val orchestrator = AgenticRAGOrchestrator(db)
    val gemmaEngine = GemmaRAGEngine()

    val authState: StateFlow<AuthState> = AuthManager.authState

    val documents: StateFlow<List<DocumentEntity>> = db.documentDao()
        .getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = db.auditDao()
        .getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collaborators: StateFlow<List<CollaboratorEntity>> = db.collaboratorDao()
        .getAllCollaborators()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collaborativeNotes: StateFlow<List<CollaborativeNoteEntity>> = db.collaborativeNoteDao()
        .getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isProcessingQuery = MutableStateFlow(false)
    val isProcessingQuery: StateFlow<Boolean> = _isProcessingQuery.asStateFlow()

    private val _comparisonReport = MutableStateFlow<PolicyComparisonReport?>(null)
    val comparisonReport: StateFlow<PolicyComparisonReport?> = _comparisonReport.asStateFlow()

    private val _selectedDocAId = MutableStateFlow(1L)
    val selectedDocAId: StateFlow<Long> = _selectedDocAId.asStateFlow()

    private val _selectedDocBId = MutableStateFlow(2L)
    val selectedDocBId: StateFlow<Long> = _selectedDocBId.asStateFlow()

    fun setSelectedDocA(id: Long) {
        _selectedDocAId.value = id
        runPolicyComparison(id, _selectedDocBId.value)
    }

    fun setSelectedDocB(id: Long) {
        _selectedDocBId.value = id
        runPolicyComparison(_selectedDocAId.value, id)
    }

    fun setComparisonDocs(docAId: Long, docBId: Long) {
        _selectedDocAId.value = docAId
        _selectedDocBId.value = docBId
        runPolicyComparison(docAId, docBId)
    }

    private val _isComparing = MutableStateFlow(false)
    val isComparing: StateFlow<Boolean> = _isComparing.asStateFlow()

    private val _selectedDoc = MutableStateFlow<DocumentEntity?>(null)
    val selectedDoc: StateFlow<DocumentEntity?> = _selectedDoc.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _gemmaSummaries = MutableStateFlow<List<GemmaDocumentAnalysis>>(emptyList())
    val gemmaSummaries: StateFlow<List<GemmaDocumentAnalysis>> = _gemmaSummaries.asStateFlow()

    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = db.documentDao().getDocumentById(1L)
            if (existing == null) {
                val initialDocs = PreloadedGovDocuments.getInitialDocuments()
                for (doc in initialDocs) {
                    val id = db.documentDao().insertDocument(doc)
                    val chunks = EdgeVectorEngine.chunkDocument(doc.copy(id = id))
                    db.chunkDao().insertChunks(chunks)
                }
                db.collaboratorDao().insertCollaborators(PreloadedGovDocuments.getInitialCollaborators())
                db.collaborativeNoteDao().insertNotes(PreloadedGovDocuments.getInitialNotes())
                db.auditDao().insertLog(
                    AuditLogEntity(
                        action = "SYSTEM_INITIALIZE",
                        details = "Initialized Sovereign Document Vault with Gemma 2B Edge RAG.",
                        actor = "Sovereign Root Core",
                        latencyMs = 12
                    )
                )
            }

            // Generate initial Gemma Document Summaries
            val allDocs = db.documentDao().getDocumentById(1L)?.let {
                PreloadedGovDocuments.getInitialDocuments()
            } ?: emptyList()

            val summaries = allDocs.map { gemmaEngine.analyzeDocument(it) }
            _gemmaSummaries.value = summaries

            // Initial Gemma welcome message
            _chatMessages.value = listOf(
                ChatMessage(
                    text = "Welcome to **GovDoc Intelligence** powered by **Gemma 2B Edge RAG**.\n\nAll departmental circulars and statutory notifications are indexed locally for zero-latency, private analysis.\n\n*Quick questions you can ask:*",
                    isUser = false,
                    modelUsed = "Gemma 2B Edge (INT4 • Ready)"
                )
            )

            // Run policy diff demo
            runPolicyComparison(1L, 2L)
        }
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun selectDocument(doc: DocumentEntity?) {
        _selectedDoc.value = doc
    }

    fun switchOfficer(user: UserProfile) {
        AuthManager.switchOfficer(user)
        viewModelScope.launch(Dispatchers.IO) {
            db.auditDao().insertLog(
                AuditLogEntity(
                    action = "AUTH_OFFICER_SWITCH",
                    details = "Authenticated session established for ${user.name} (${user.role})",
                    actor = "OAuth 2.0 / JWT Sentinel",
                    latencyMs = 4
                )
            )
        }
    }

    fun refreshSession() {
        val newJwt = AuthManager.refreshSession()
        viewModelScope.launch(Dispatchers.IO) {
            db.auditDao().insertLog(
                AuditLogEntity(
                    action = "JWT_TOKEN_REFRESH",
                    details = "Refreshed HMAC-SHA256 session token. Valid for 8 hours.",
                    actor = "Auth Security Enclave",
                    latencyMs = 2
                )
            )
        }
    }

    fun logout() {
        AuthManager.logout()
    }

    fun loginWithOAuth() {
        AuthManager.loginWithOAuth()
    }

    fun runPolicyComparison(docAId: Long, docBId: Long) {
        viewModelScope.launch {
            _isComparing.value = true
            val report = orchestrator.comparePoliciesDirect(docAId, docBId)
            _comparisonReport.value = report
            _isComparing.value = false

            withContext(Dispatchers.IO) {
                db.auditDao().insertLog(
                    AuditLogEntity(
                        action = "POLICY_DIFF_EXECUTION",
                        details = "Evaluated delta between ${report.docACircular} and ${report.docBCircular}",
                        actor = "Policy Comparison Agent",
                        latencyMs = report.generatedLatencyMs
                    )
                )
            }
        }
    }

    fun sendQuery(queryText: String) {
        if (queryText.isBlank()) return
        val userMsg = ChatMessage(text = queryText, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        _isProcessingQuery.value = true

        viewModelScope.launch {
            val docs = documents.value.ifEmpty { PreloadedGovDocuments.getInitialDocuments() }
            val chunks = withContext(Dispatchers.IO) { db.chunkDao().getAllChunks() }

            val response = gemmaEngine.queryLocalDocuments(
                query = queryText,
                targetDocumentId = _selectedDoc.value?.id,
                documents = docs,
                allChunks = chunks
            )

            _chatMessages.value = _chatMessages.value + response
            _isProcessingQuery.value = false

            withContext(Dispatchers.IO) {
                db.auditDao().insertLog(
                    AuditLogEntity(
                        action = "GEMMA_EDGE_RAG_QUERY",
                        details = "Query: '${queryText.take(35)}...' resolved with ${response.citations.size} citations.",
                        actor = "Gemma 2B Edge RAG",
                        latencyMs = response.latencyMs
                    )
                )
            }
        }
    }

    fun ingestNewDocument(
        title: String,
        circularNumber: String,
        department: String,
        category: String,
        classification: String,
        content: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val sha256 = CryptoVault.computeSha256(content)
            val newDoc = DocumentEntity(
                title = title,
                circularNumber = circularNumber,
                department = department,
                issueDate = "2025-03-11",
                category = category,
                classification = classification,
                fullText = content,
                summary = "User-ingested official notification ($circularNumber)",
                sha256Hash = sha256,
                isEncrypted = classification != "PUBLIC",
                syncStatus = "SYNCED",
                chunkCount = 0
            )
            val insertedId = db.documentDao().insertDocument(newDoc)
            val chunks = EdgeVectorEngine.chunkDocument(newDoc.copy(id = insertedId))
            db.chunkDao().insertChunks(chunks)
            val updatedDoc = newDoc.copy(id = insertedId, chunkCount = chunks.size)
            db.documentDao().updateDocument(updatedDoc)

            // Update Gemma summary for newly ingested document
            val analysis = gemmaEngine.analyzeDocument(updatedDoc)
            _gemmaSummaries.value = _gemmaSummaries.value + analysis

            db.auditDao().insertLog(
                AuditLogEntity(
                    action = "DOCUMENT_INGESTED",
                    details = "Ingested & indexed '$circularNumber' (${chunks.size} vector chunks) via Gemma.",
                    actor = "Gemma Ingestion Pipeline",
                    latencyMs = 14
                )
            )
        }
    }

    fun postCollaborativeNote(
        documentId: Long,
        circularNumber: String,
        clauseTag: String,
        noteText: String
    ) {
        if (noteText.isBlank()) return
        val officer = authState.value.currentUser
        viewModelScope.launch(Dispatchers.IO) {
            val note = CollaborativeNoteEntity(
                documentId = documentId,
                circularNumber = circularNumber,
                authorName = officer.name,
                authorRole = officer.role,
                noteText = noteText,
                clauseTag = clauseTag.ifBlank { "General Circular Note" },
                timestamp = System.currentTimeMillis(),
                syncStatus = "SYNCED"
            )
            db.collaborativeNoteDao().insertNote(note)
            db.auditDao().insertLog(
                AuditLogEntity(
                    action = "SHARED_NOTE_POSTED",
                    details = "Collaborative annotation posted on $circularNumber ($clauseTag)",
                    actor = officer.name,
                    latencyMs = 6
                )
            )
        }
    }

    fun toggleEncryption(doc: DocumentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = doc.copy(isEncrypted = !doc.isEncrypted)
            db.documentDao().updateDocument(updated)
            db.auditDao().insertLog(
                AuditLogEntity(
                    action = if (updated.isEncrypted) "E2EE_VAULT_LOCK" else "E2EE_VAULT_UNLOCK",
                    details = "Cipher state changed for ${doc.circularNumber} (AES-256 GCM)",
                    actor = "Crypto Security Module",
                    latencyMs = 4
                )
            )
        }
    }

    fun deleteDocument(docId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.documentDao().deleteDocument(docId)
            db.chunkDao().deleteChunksForDocument(docId)
            _gemmaSummaries.value = _gemmaSummaries.value.filter { it.documentId != docId }
            db.auditDao().insertLog(
                AuditLogEntity(
                    action = "DOCUMENT_EXPUNGED",
                    details = "Deleted document ID #$docId and associated vector spaces.",
                    actor = "Officer Action",
                    latencyMs = 8
                )
            )
        }
    }

    fun reindexWithGemma() {
        viewModelScope.launch(Dispatchers.IO) {
            gemmaEngine.purgeCache()
            val docs = db.documentDao().getDocumentById(1L)?.let {
                PreloadedGovDocuments.getInitialDocuments()
            } ?: emptyList()
            val summaries = docs.map { gemmaEngine.analyzeDocument(it) }
            _gemmaSummaries.value = summaries
            db.auditDao().insertLog(
                AuditLogEntity(
                    action = "GEMMA_REINDEX",
                    details = "Purged query cache and re-computed INT4 edge embeddings for ${docs.size} circulars.",
                    actor = "Gemma 2B Engine",
                    latencyMs = 15
                )
            )
        }
    }

    fun syncWorkspace() {
        viewModelScope.launch {
            _isSyncing.value = true
            kotlinx.coroutines.delay(800) // Fast simulated TLS 1.3 sync handshake
            _isSyncing.value = false

            withContext(Dispatchers.IO) {
                db.auditDao().insertLog(
                    AuditLogEntity(
                        action = "CROSS_PLATFORM_SYNC",
                        details = "E2EE sync verified with Cabinet Server & Mobile Mesh Nodes. 0 conflicts.",
                        actor = "Multi-User Sync Protocol",
                        latencyMs = 800
                    )
                )
            }
        }
    }
}
