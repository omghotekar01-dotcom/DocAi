package com.example.vision

import android.graphics.Bitmap
import android.graphics.Rect

data class ScannedObjectItem(
    val id: Int,
    val label: String,
    val category: String, // "OFFICIAL_SEAL", "SIGNATURE", "TABLE_DATA", "LETTERHEAD", "BARCODE_QR", "DOCUMENT_BOUNDARY"
    val confidence: Float,
    val bounds: Rect,
    val colorHex: Long = 0xFF38BDF8
)

data class ScannedTextBlock(
    val text: String,
    val bounds: Rect,
    val lineCount: Int,
    val confidence: Float = 0.95f
)

data class ExtractedClauseItem(
    val clauseId: String,
    val title: String,
    val text: String
)

data class ScannedDocumentAnalysis(
    val rawText: String,
    val detectedTitle: String,
    val detectedCircularNo: String,
    val detectedDepartment: String,
    val detectedClassification: String,
    val extractedClauses: List<ExtractedClauseItem>,
    val textBlocks: List<ScannedTextBlock>,
    val detectedObjects: List<ScannedObjectItem>,
    val sourceBitmap: Bitmap?,
    val processingTimeMs: Long,
    val characterCount: Int,
    val confidenceScore: Float
)
