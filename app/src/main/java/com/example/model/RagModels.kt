package com.example.model

data class ScoredChunk(
    val chunkId: Long,
    val documentId: Long,
    val documentTitle: String,
    val circularNumber: String,
    val clauseNumber: String,
    val content: String,
    val similarityScore: Float,
    val matchedKeywords: List<String>
)

data class Citation(
    val clauseNumber: String,
    val circularNumber: String,
    val documentTitle: String,
    val excerpt: String,
    val confidenceScore: Float
)

data class AgentStepTrace(
    val agentName: String,
    val status: String, // "ANALYZING", "COMPLETED", "ROUTED"
    val description: String,
    val latencyMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ChangeType {
    MODIFIED, ADDED, REMOVED, TIGHTENED, UNCHANGED
}

data class ClauseComparison(
    val clauseTitle: String,
    val oldProvision: String,
    val newProvision: String,
    val changeType: ChangeType,
    val impactAssessment: String
)

data class PolicyComparisonReport(
    val docATitle: String,
    val docACircular: String,
    val docBTitle: String,
    val docBCircular: String,
    val executiveSummary: String,
    val clauseComparisons: List<ClauseComparison>,
    val keyTakeaways: List<String>,
    val complianceActionItems: List<String>,
    val generatedLatencyMs: Long
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val citations: List<Citation> = emptyList(),
    val agentTraces: List<AgentStepTrace> = emptyList(),
    val modelUsed: String = "Edge Gemma RAG (Sub-15ms)",
    val latencyMs: Long = 0
)

enum class SecurityClearance(val label: String, val levelCode: Int) {
    PUBLIC("Public Access (L1)", 1),
    RESTRICTED("Department Officer (L2)", 2),
    CONFIDENTIAL("Senior Directorate (L3)", 3),
    TOP_SECRET("Cabinet & Auditor (L4)", 4)
}
