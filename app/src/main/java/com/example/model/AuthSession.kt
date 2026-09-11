package com.example.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val department: String,
    val clearance: SecurityClearance,
    val avatarInitials: String,
    val authProvider: String = "Google Workspace OAuth 2.0"
)

data class JwtSession(
    val token: String,
    val subject: String,
    val issuer: String = "https://govdoc.nic.in/oauth2",
    val issuedAt: Long,
    val expiresAt: Long,
    val keyId: String = "govdoc_sec_key_2025",
    val algorithm: String = "HS256 (HMAC-SHA256)",
    val isValid: Boolean = true
) {
    val remainingMinutes: Long
        get() = ((expiresAt - System.currentTimeMillis()) / (60 * 1000)).coerceAtLeast(0)
}

data class AuthState(
    val isAuthenticated: Boolean = true,
    val currentUser: UserProfile,
    val session: JwtSession,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)
