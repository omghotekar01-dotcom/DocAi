package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CollaborativeNoteEntity
import com.example.data.local.CollaboratorEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingChip
import com.example.ui.theme.*
import com.example.viewmodel.GovIntelViewModel

@Composable
fun CollabScreen(viewModel: GovIntelViewModel) {
    val collaborators by viewModel.collaborators.collectAsState()
    val collaborativeNotes by viewModel.collaborativeNotes.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var selectedDocId by remember { mutableStateOf(2L) }
    var selectedClauseTag by remember { mutableStateOf("Section 5.1 (Incident)") }
    var noteInputText by remember { mutableStateOf("") }
    var showNoteAddedFeedback by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("collab_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Sync & Real-Time Presence Header
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x300C162E)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Real-Time Team Mesh",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${collaborators.size} Officers Co-Present • Multi-User Sync",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.syncWorkspace() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSyncing) AmberWarning else ElectricBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = null,
                            tint = GlassDarkBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSyncing) "Syncing..." else "Sync Now",
                            color = GlassDarkBackground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Active Officers Co-Presence Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ACTIVE OFFICERS IN WORKSPACE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(collaborators, key = { it.id }) { collab ->
                        CollaboratorPill(collab = collab)
                    }
                }
            }
        }

        // 3. Post a Collaborative Annotation / Note
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x280D1830),
                borderColor = ElectricBlue.copy(alpha = 0.4f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "POST SHARED ANNOTATION",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Posting as: ${authState.currentUser.name.take(15)}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("collab_note_input"),
                        placeholder = {
                            Text(
                                "Add compliance guidance, clause review remarks, or audit notes...",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = GlassBorderSubtle,
                            focusedContainerColor = Color(0x20050B14),
                            unfocusedContainerColor = Color(0x20050B14),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick tag chip
                        GlowingChip(
                            text = selectedClauseTag,
                            color = VibrantPurple
                        )

                        Button(
                            onClick = {
                                if (noteInputText.isNotBlank()) {
                                    val targetDoc = documents.find { it.id == selectedDocId }
                                    viewModel.postCollaborativeNote(
                                        documentId = selectedDocId,
                                        circularNumber = targetDoc?.circularNumber ?: "MeitY/eGov/2025/12",
                                        clauseTag = selectedClauseTag,
                                        noteText = noteInputText.trim()
                                    )
                                    noteInputText = ""
                                    showNoteAddedFeedback = true
                                }
                            },
                            enabled = noteInputText.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldTeal,
                                contentColor = GlassDarkBackground
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Post & Sync", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (showNoteAddedFeedback) {
                        Text(
                            text = "✓ Note synchronized with team mesh across 4 devices.",
                            color = EmeraldTeal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 4. Shared Real-Time Notes Feed
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SYNCHRONIZED TEAM NOTES (${collaborativeNotes.size})",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (collaborativeNotes.isEmpty()) {
                    Text(
                        text = "No notes posted yet. Add the first shared note above.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                } else {
                    collaborativeNotes.forEach { note ->
                        CollaborativeNoteCard(note = note)
                    }
                }
            }
        }
    }
}

@Composable
private fun CollaboratorPill(collab: CollaboratorEntity) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp)),
        color = Color(0x350A1428),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue.copy(alpha = 0.2f))
                        .border(1.dp, ElectricBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = collab.avatarInitials,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(EmeraldTeal)
                        .border(1.5.dp, GlassDarkBackground, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Column {
                Text(
                    text = collab.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = collab.activeDocTitle.take(18) + "...",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun CollaborativeNoteCard(note: CollaborativeNoteEntity) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x200E1A33)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = note.authorName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• ${note.authorRole.take(18)}",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                GlowingChip(
                    text = "SYNCED",
                    color = EmeraldTeal,
                    icon = Icons.Default.Check
                )
            }

            // Tag & Circular
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = note.circularNumber,
                    color = ElectricBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "| ${note.clauseTag}",
                    color = NeonCyan,
                    fontSize = 11.sp
                )
            }

            Text(
                text = note.noteText,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
