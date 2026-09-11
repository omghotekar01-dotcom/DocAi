package com.example.security

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoVault {

    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12

    // Master seed derivation for device-bound sovereign enclave key
    private val MASTER_ENCLAVE_KEY = generateKeyFromPassphrase("Sovereign-Gov-Enclave-Key-2025")

    fun encryptPayload(plainText: String): EncryptedResult {
        val iv = ByteArray(IV_LENGTH_BYTE)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.ENCRYPT_MODE, MASTER_ENCLAVE_KEY, spec)

        val cipherTextBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val cipherBase64 = Base64.encodeToString(cipherTextBytes, Base64.NO_WRAP)
        val sha256 = computeSha256(plainText)

        return EncryptedResult(
            cipherText = "$ivBase64:$cipherBase64",
            sha256Hash = sha256,
            algorithm = "AES-256-GCM"
        )
    }

    fun decryptPayload(combined: String): String {
        return try {
            val parts = combined.split(":")
            if (parts.size != 2) return combined
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val cipherText = Base64.decode(parts[1], Base64.NO_WRAP)

            val cipher = Cipher.getInstance(ALGORITHM)
            val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, MASTER_ENCLAVE_KEY, spec)

            val decrypted = cipher.doFinal(cipherText)
            String(decrypted, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            "Decryption Error: ${e.message}"
        }
    }

    fun computeSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun generateKeyFromPassphrase(passphrase: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(passphrase.toByteArray(StandardCharsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }
}

data class EncryptedResult(
    val cipherText: String,
    val sha256Hash: String,
    val algorithm: String
)
