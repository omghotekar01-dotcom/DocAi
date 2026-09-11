package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuthState
import com.example.model.UserProfile
import com.example.security.AuthManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthSessionSheet(
    authState: AuthState,
    onDismiss: () -> Unit,
    onSwitchOfficer: (UserProfile) -> Unit,
    onRefreshSession: () -> Unit,
    onLogout: () -> Unit,
    onLogin: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var copiedToken by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0C1427),
        dragHandle = { BottomSheetDefaults.DragHandle(color = ElectricBlue) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue.copy(alpha = 0.2f))
                            .border(1.dp, ElectricBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = authState.currentUser.avatarInitials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Column {
                        Text(
                            text = authState.currentUser.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = authState.currentUser.email,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                GlowingChip(
                    text = if (authState.isAuthenticated) "JWT Active" else "Unauthenticated",
                    color = if (authState.isAuthenticated) EmeraldTeal else CrimsonAlert,
                    icon = if (authState.isAuthenticated) Icons.Default.VerifiedUser else Icons.Default.Lock
                )
            }

            // Role & Security Clearance
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x30111F38)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ROLE & JURISDICTION",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = authState.currentUser.role,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = authState.currentUser.department,
                        color = ElectricBlue,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Clearance Tier:",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = authState.currentUser.clearance.label,
                            color = AmberWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // OAuth 2.0 & JWT Claims Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x250F172A)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OAUTH 2.0 & JWT TOKEN",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Expires in ${authState.session.remainingMinutes}m",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Token snippet
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x50050B14))
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(8.dp))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(authState.session.token))
                                copiedToken = true
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = authState.session.token.take(36) + "...",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Icon(
                                imageVector = if (copiedToken) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy Token",
                                tint = if (copiedToken) EmeraldTeal else ElectricBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Provider: ${authState.currentUser.authProvider}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Alg: ${authState.session.algorithm.take(5)}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Switch Officer Persona
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SWITCH OFFICER PERSONA (SIMULATION)",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                AuthManager.AVAILABLE_OFFICERS.forEach { officer ->
                    val isSelected = officer.id == authState.currentUser.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricBlue.copy(alpha = 0.15f) else Color(0x18FFFFFF))
                            .border(
                                1.dp,
                                if (isSelected) ElectricBlue else GlassBorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSwitchOfficer(officer) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = officer.name,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "${officer.role.take(28)} • ${officer.clearance.label.take(12)}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRefreshSession,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refresh JWT", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (authState.isAuthenticated) onLogout() else onLogin()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (authState.isAuthenticated) CrimsonAlert.copy(alpha = 0.8f) else EmeraldTeal
                    )
                ) {
                    Icon(
                        imageVector = if (authState.isAuthenticated) Icons.AutoMirrored.Filled.Logout else Icons.AutoMirrored.Filled.Login,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (authState.isAuthenticated) "Log Out" else "Log In", fontSize = 12.sp)
                }
            }
        }
    }
}
