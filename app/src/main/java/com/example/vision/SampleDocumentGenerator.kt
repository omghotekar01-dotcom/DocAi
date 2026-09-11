package com.example.vision

import android.graphics.*

object SampleDocumentGenerator {

    enum class SampleType(val title: String, val circularNo: String, val dept: String) {
        MEITY_EDGE_AI(
            title = "Directive on Edge RAG & Sovereign AI Deployments",
            circularNo = "MeitY/CYBER/2025/ORD-781",
            dept = "Ministry of Electronics and Information Technology"
        ),
        RBI_CLOUD_SECURITY(
            title = "Prudential Norms for Cloud & LLM Data Residency",
            circularNo = "RBI/2025-26/108/DOR.STR.REC.42",
            dept = "Reserve Bank of India (FinTech & Cyber Department)"
        ),
        CERT_INCIDENT_REPORTING(
            title = "Cyber Incident Reporting & Provenance Hashing Protocol",
            circularNo = "CERT-In/DIR/2025/SEC-994",
            dept = "Indian Computer Emergency Response Team (CERT-In)"
        )
    }

    fun generateSampleBitmap(type: SampleType): Bitmap {
        val width = 800
        val height = 1100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Paper Background with slight warmth
        val bgPaint = Paint().apply {
            color = android.graphics.Color.rgb(248, 249, 250)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Outer Document Border
        val borderPaint = Paint().apply {
            color = android.graphics.Color.rgb(203, 213, 225)
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRect(24f, 24f, (width - 24).toFloat(), (height - 24).toFloat(), borderPaint)

        // 3. Official Header / Letterhead
        val emblemPaint = Paint().apply {
            color = android.graphics.Color.rgb(30, 41, 59)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle((width / 2).toFloat(), 65f, 22f, emblemPaint)

        val headerPaint = Paint().apply {
            color = android.graphics.Color.rgb(15, 23, 42)
            textSize = 20f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("GOVERNMENT OF INDIA", (width / 2).toFloat(), 115f, headerPaint)

        val subHeaderPaint = Paint().apply {
            color = android.graphics.Color.rgb(51, 65, 85)
            textSize = 14f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(type.dept.uppercase(), (width / 2).toFloat(), 138f, subHeaderPaint)
        canvas.drawText("ELECTRONICS NIKETAN, CGO COMPLEX, NEW DELHI - 110003", (width / 2).toFloat(), 158f, subHeaderPaint)

        // Horizontal Separator
        val linePaint = Paint().apply {
            color = android.graphics.Color.rgb(100, 116, 139)
            strokeWidth = 2f
        }
        canvas.drawLine(50f, 175f, (width - 50).toFloat(), 175f, linePaint)

        // Metadata Fields
        val metaPaint = Paint().apply {
            color = android.graphics.Color.rgb(30, 41, 59)
            textSize = 13f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CIRCULAR REF: ${type.circularNo}", 55f, 205f, metaPaint)
        canvas.drawText("DATE: 12th January 2025", 55f, 225f, metaPaint)
        canvas.drawText("CLASSIFICATION: CONFIDENTIAL // COMPLIANCE MANDATE", 55f, 245f, metaPaint)

        // Subject
        val subjPaint = Paint().apply {
            color = android.graphics.Color.rgb(15, 23, 42)
            textSize = 15f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("SUBJECT: ${type.title.uppercase()}", 55f, 280f, subjPaint)

        // Statutory Clauses Content
        val bodyPaint = Paint().apply {
            color = android.graphics.Color.rgb(30, 41, 59)
            textSize = 12f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        var yOffset = 315f
        val clauses = when (type) {
            SampleType.MEITY_EDGE_AI -> listOf(
                "Clause 1: All public sector document synthesis must leverage quantized local models (Gemma 2B INT4 or equivalent) to prevent sensitive legislative drafts from exiting air-gapped perimeters.",
                "Clause 2: Document comparison audits must preserve timestamped SHA-256 provenance hashes for every statutory delta.",
                "Clause 3: Multi-officer review records must adhere to HMAC-SHA256 verified session tokens with mandatory zero-trust verification.",
                "Clause 4: Any detected legislative discrepancy must trigger automated compliance checklists within 6 hours of publication."
            )
            SampleType.RBI_CLOUD_SECURITY -> listOf(
                "Clause 1: Regulated entities shall maintain cryptographic keys within domestic Sovereign Key Vaults (HSM Level 3 compliant).",
                "Clause 2: Financial transaction summaries generated by artificial intelligence must retain full algorithmic lineage and audit logs for 7 years.",
                "Clause 3: Cross-border transmission of raw bank customer data via unvetted cloud endpoints is strictly prohibited.",
                "Clause 4: Edge inference devices must be hardened against memory injection and side-channel extraction attacks."
            )
            SampleType.CERT_INCIDENT_REPORTING -> listOf(
                "Clause 1: Mandated reporting window of 6 hours for critical cyber incidents across government infrastructure.",
                "Clause 2: System event logs and NTP synchronization must maintain microsecond accuracy for digital forensics.",
                "Clause 3: Multi-cloud deployments must implement automated drift detection against baseline compliance profiles.",
                "Clause 4: Failure to report breach artifacts shall invoke statutory penalties under IT Act Section 70B."
            )
        }

        clauses.forEach { clause ->
            val words = clause.split(" ")
            var line = ""
            words.forEach { word ->
                if (bodyPaint.measureText("$line $word") < (width - 120)) {
                    line = if (line.isEmpty()) word else "$line $word"
                } else {
                    canvas.drawText(line, 55f, yOffset, bodyPaint)
                    yOffset += 18f
                    line = word
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, 55f, yOffset, bodyPaint)
                yOffset += 28f
            }
        }

        // 4. Official Seal Stamp (Red Circular Stamp)
        val stampPaint = Paint().apply {
            color = android.graphics.Color.argb(190, 220, 38, 38)
            style = Paint.Style.STROKE
            strokeWidth = 3.5f
            isAntiAlias = true
        }
        val sealX = (width * 0.76f)
        val sealY = (height * 0.74f)
        canvas.drawCircle(sealX, sealY, 52f, stampPaint)
        val stampInner = Paint().apply {
            color = android.graphics.Color.argb(170, 220, 38, 38)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
        }
        canvas.drawCircle(sealX, sealY, 44f, stampInner)

        val stampTextPaint = Paint().apply {
            color = android.graphics.Color.argb(210, 220, 38, 38)
            textSize = 9f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("GOVT OF INDIA", sealX, sealY - 14f, stampTextPaint)
        canvas.drawText("VERIFIED", sealX, sealY + 2f, stampTextPaint)
        canvas.drawText("CERT-IN SEAL", sealX, sealY + 18f, stampTextPaint)

        // 5. Authorized Signatory (Blue Signature)
        val sigLinePaint = Paint().apply {
            color = android.graphics.Color.rgb(100, 116, 139)
            strokeWidth = 1.5f
        }
        val sigX = (width * 0.58f)
        val sigY = (height * 0.88f)
        canvas.drawLine(sigX, sigY, (width - 55).toFloat(), sigY, sigLinePaint)

        val sigTextPaint = Paint().apply {
            color = android.graphics.Color.rgb(30, 58, 138)
            textSize = 11f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("Dr. A. K. Sharma (IAS)", sigX + 10f, sigY - 8f, sigTextPaint)

        val titlePaint = Paint().apply {
            color = android.graphics.Color.rgb(71, 85, 105)
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText("Joint Secretary to the Government of India", sigX, sigY + 16f, titlePaint)

        // 6. Security Barcode Lines (Bottom Left)
        val barcodePaint = Paint().apply {
            color = android.graphics.Color.rgb(15, 23, 42)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        var barX = 55f
        val barY = (height - 85).toFloat()
        val barH = 32f
        val randomPatterns = listOf(2f, 4f, 1f, 3f, 5f, 2f, 4f, 1f, 3f, 2f, 5f, 3f, 1f, 4f, 2f)
        randomPatterns.forEach { w ->
            barcodePaint.strokeWidth = w
            canvas.drawLine(barX, barY, barX, barY + barH, barcodePaint)
            barX += w + 3f
        }
        val codeText = Paint().apply {
            color = android.graphics.Color.rgb(71, 85, 105)
            textSize = 9f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }
        canvas.drawText("*SEC-891-2025-GAZETTE*", 55f, barY + barH + 14f, codeText)

        return bitmap
    }
}
