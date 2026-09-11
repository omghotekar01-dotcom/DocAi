package com.example.security

import android.util.Base64
import com.example.model.AuthState
import com.example.model.JwtSession
import com.example.model.SecurityClearance
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.charset.StandardCharsets

object AuthManager {

    val AVAILABLE_OFFICERS = listOf(
        UserProfile(
            id = "officer_01",
            name = "Om Ghotekar",
            email = "omghotekar01@gmail.com",
            role = "Chief Information Security Officer (CISO)",
            department = "Ministry of Electronics & IT (MeitY)",
            clearance = SecurityClearance.TOP_SECRET,
            avatarInitials = "OG",
            authProvider = "Google Workspace OAuth 2.0"
        ),
        UserProfile(
            id = "officer_02",
            name = "Dr. Priya Nair",
            email = "priya.nair@meity.gov.in",
            role = "Director General (Cyber Regulation)",
            department = "National Informatics Centre (NIC)",
            clearance = SecurityClearance.CONFIDENTIAL,
            avatarInitials = "PN",
            authProvider = "GovPass NIC OAuth 2.0"
        ),
        UserProfile(
            id = "officer_03",
            name = "Vikramaditya Sen",
            email = "v.sen@audit.gov.in",
            role = "Principal Statutory Compliance Auditor",
            department = "Comptroller & Auditor General (CAG)",
            clearance = SecurityClearance.TOP_SECRET,
            avatarInitials = "VS",
            authProvider = "GovPass NIC OAuth 2.0"
        ),
        UserProfile(
            id = "officer_04",
            name = "Ananya Roy",
            email = "ananya.roy@dpiit.gov.in",
            role = "Senior Procurement Policy Analyst",
            department = "DPIIT / Ministry of Commerce",
            clearance = SecurityClearance.RESTRICTED,
            avatarInitials = "AR",
            authProvider = "Google Workspace OAuth 2.0"
        )
    )

    private val _authState = MutableStateFlow(
        AuthState(
            isAuthenticated = true,
            currentUser = AVAILABLE_OFFICERS[0],
            session = generateJwtForUser(AVAILABLE_OFFICERS[0])
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun switchOfficer(user: UserProfile) {
        val newJwt = generateJwtForUser(user)
        _authState.value = AuthState(
            isAuthenticated = true,
            currentUser = user,
            session = newJwt
        )
    }

    fun refreshSession(): JwtSession {
        val currentUser = _authState.value.currentUser
        val newJwt = generateJwtForUser(currentUser)
        _authState.value = _authState.value.copy(session = newJwt)
        return newJwt
    }

    fun logout() {
        val guest = UserProfile(
            id = "guest_00",
            name = "Guest Reviewer",
            email = "guest@govdoc.nic.in",
            role = "Public Inquirer",
            department = "Public Administration",
            clearance = SecurityClearance.PUBLIC,
            avatarInitials = "GR",
            authProvider = "Guest Session"
        )
        _authState.value = AuthState(
            isAuthenticated = false,
            currentUser = guest,
            session = generateJwtForUser(guest, isGuest = true)
        )
    }

    fun loginWithOAuth(user: UserProfile = AVAILABLE_OFFICERS[0]) {
        switchOfficer(user)
    }

    fun generateJwtForUser(user: UserProfile, isGuest: Boolean = false): JwtSession {
        val issuedAt = System.currentTimeMillis()
        val expiresAt = issuedAt + (8 * 3600 * 1000) // 8 hour valid session

        val headerJson = """{"alg":"HS256","typ":"JWT"}"""
        val payloadJson = """{"sub":"${user.id}","name":"${user.name}","email":"${user.email}","role":"${user.role}","dept":"${user.department}","clearance":"${user.clearance.name}","iat":${issuedAt / 1000},"exp":${expiresAt / 1000},"iss":"https://govdoc.nic.in/oauth2","auth_provider":"${user.authProvider}"}"""

        val b64Header = Base64.encodeToString(headerJson.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val b64Payload = Base64.encodeToString(payloadJson.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

        val signaturePayload = "$b64Header.$b64Payload"
        val rawHash = CryptoVault.computeSha256(signaturePayload + "_hmac_salt_govdoc_2025")
        val b64Signature = Base64.encodeToString(rawHash.take(32).toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

        val fullJwt = "$b64Header.$b64Payload.$b64Signature"

        return JwtSession(
            token = fullJwt,
            subject = user.id,
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            isValid = !isGuest
        )
    }
}
