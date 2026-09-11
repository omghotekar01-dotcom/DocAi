package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.rag.EdgeVectorEngine
import com.example.security.CryptoVault
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GovDoc Intelligence", appName)
  }

  @Test
  fun `verify edge vector engine tokenizer and cosine similarity`() {
    val tokens = EdgeVectorEngine.tokenize("Mandatory cyber incident reporting within 6 hours to CERT-In")
    assertTrue(tokens.contains("cyber"))
    assertTrue(tokens.contains("incident"))
    assertTrue(tokens.contains("reporting"))

    val tf1 = EdgeVectorEngine.buildTermFrequency(tokens)
    val queryTokens = EdgeVectorEngine.tokenize("incident reporting hours")
    val tfQuery = EdgeVectorEngine.buildTermFrequency(queryTokens)

    val similarity = EdgeVectorEngine.calculateCosineSimilarity(tf1, tfQuery)
    assertTrue("Similarity should be positive", similarity > 0.3f)
  }

  @Test
  fun `verify crypto vault AES-256 GCM encryption and decryption`() {
    val sensitivePayload = "CONFIDENTIAL: Cabinet note on quantum cryptography 2025"
    val encrypted = CryptoVault.encryptPayload(sensitivePayload)

    assertNotNull(encrypted.cipherText)
    assertTrue(encrypted.cipherText.contains(":"))
    assertEquals("AES-256-GCM", encrypted.algorithm)

    val decrypted = CryptoVault.decryptPayload(encrypted.cipherText)
    assertEquals(sensitivePayload, decrypted)

    val hash = CryptoVault.computeSha256(sensitivePayload)
    assertEquals(64, hash.length)
  }

  @Test
  fun `verify auth manager JWT generation and officer switching`() {
    val officer = com.example.security.AuthManager.AVAILABLE_OFFICERS.first()
    val session = com.example.security.AuthManager.generateJwtForUser(officer)

    assertNotNull(session.token)
    assertTrue(session.isValid)
    assertTrue(session.token.contains("."))
    assertEquals(3, session.token.split(".").size)
    assertEquals(officer.id, session.subject)
  }

  @Test
  fun `verify gemma rag engine document analysis`() = kotlinx.coroutines.runBlocking {
    val gemma = com.example.rag.GemmaRAGEngine()
    val testDoc = com.example.data.local.PreloadedGovDocuments.getInitialDocuments()[1] // Notification 12/2025

    val analysis = gemma.analyzeDocument(testDoc)
    assertEquals(testDoc.id, analysis.documentId)
    assertEquals("HIGH", analysis.riskLevel)
    assertTrue(analysis.keyTakeaways.isNotEmpty())
    assertTrue(analysis.mandatoryChecklist.isNotEmpty())
    assertTrue(analysis.complianceHealthScore > 80)
  }
}


