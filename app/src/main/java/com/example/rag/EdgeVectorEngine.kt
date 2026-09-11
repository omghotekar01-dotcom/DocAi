package com.example.rag

import com.example.data.local.ChunkEntity
import com.example.data.local.DocumentEntity
import java.util.Locale
import kotlin.math.sqrt

object EdgeVectorEngine {

    private val STOPWORDS = setOf(
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and",
        "any", "are", "as", "at", "be", "because", "been", "before", "being", "below",
        "between", "both", "but", "by", "could", "did", "do", "does", "doing", "down",
        "during", "each", "few", "for", "from", "further", "had", "has", "have",
        "having", "he", "her", "here", "hers", "herself", "him", "himself", "his",
        "how", "i", "if", "in", "into", "is", "it", "its", "itself", "me", "more",
        "most", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only",
        "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own",
        "same", "she", "should", "so", "some", "such", "than", "that", "the", "their",
        "theirs", "them", "themselves", "then", "there", "these", "they", "this",
        "those", "through", "to", "too", "under", "until", "up", "very", "was", "we",
        "were", "what", "when", "where", "which", "while", "who", "whom", "why", "with",
        "would", "you", "your", "yours", "yourself", "yourselves"
    )

    fun tokenize(text: String): List<String> {
        return text.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9\\s\\-]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length > 1 && it !in STOPWORDS }
    }

    fun buildTermFrequency(tokens: List<String>): Map<String, Float> {
        if (tokens.isEmpty()) return emptyMap()
        val freq = mutableMapOf<String, Float>()
        for (token in tokens) {
            freq[token] = (freq[token] ?: 0f) + 1f
        }
        val total = tokens.size.toFloat()
        return freq.mapValues { it.value / total }
    }

    fun calculateCosineSimilarity(vectorA: Map<String, Float>, vectorB: Map<String, Float>): Float {
        if (vectorA.isEmpty() || vectorB.isEmpty()) return 0f
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f

        for ((term, weightA) in vectorA) {
            normA += weightA * weightA
            val weightB = vectorB[term] ?: 0f
            dotProduct += weightA * weightB
        }

        for ((_, weightB) in vectorB) {
            normB += weightB * weightB
        }

        val denominator = (sqrt(normA) * sqrt(normB))
        return if (denominator > 0f) dotProduct / denominator else 0f
    }

    fun chunkDocument(doc: DocumentEntity): List<ChunkEntity> {
        val lines = doc.fullText.lines()
        val chunks = mutableListOf<ChunkEntity>()
        var currentClause = "General Section"
        var currentBuffer = StringBuilder()
        var chunkIdx = 0

        val clausePattern = Regex("^((\\d+(\\.\\d+)*)|Clause|Section|Article)\\s+.*", RegexOption.IGNORE_CASE)

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            if (clausePattern.matches(trimmed) && currentBuffer.length > 120) {
                // Flush previous chunk
                val text = currentBuffer.toString().trim()
                val tokens = tokenize(text)
                val tf = buildTermFrequency(tokens)
                val topKeywords = tf.entries.sortedByDescending { it.value }.take(6).map { it.key }
                chunks.add(
                    ChunkEntity(
                        documentId = doc.id,
                        clauseNumber = currentClause,
                        chunkIndex = chunkIdx++,
                        content = text,
                        keywords = topKeywords.joinToString(","),
                        vectorSummary = serializeVector(tf)
                    )
                )
                currentBuffer = StringBuilder()
                currentClause = trimmed.take(35)
            }

            if (currentBuffer.isEmpty() && clausePattern.matches(trimmed)) {
                currentClause = trimmed.take(35)
            }
            currentBuffer.append(trimmed).append("\n")
        }

        if (currentBuffer.isNotEmpty()) {
            val text = currentBuffer.toString().trim()
            val tokens = tokenize(text)
            val tf = buildTermFrequency(tokens)
            val topKeywords = tf.entries.sortedByDescending { it.value }.take(6).map { it.key }
            chunks.add(
                ChunkEntity(
                    documentId = doc.id,
                    clauseNumber = currentClause,
                    chunkIndex = chunkIdx,
                    content = text,
                    keywords = topKeywords.joinToString(","),
                    vectorSummary = serializeVector(tf)
                )
            )
        }

        return chunks
    }

    private fun serializeVector(vector: Map<String, Float>): String {
        return vector.entries.joinToString(";") { "${it.key}:${"%.4f".format(Locale.ROOT, it.value)}" }
    }

    fun deserializeVector(data: String): Map<String, Float> {
        if (data.isBlank()) return emptyMap()
        val result = mutableMapOf<String, Float>()
        for (pair in data.split(";")) {
            val parts = pair.split(":")
            if (parts.size == 2) {
                val weight = parts[1].toFloatOrNull() ?: 0f
                result[parts[0]] = weight
            }
        }
        return result
    }
}
