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
import com.example.data.local.DocumentEntity
import com.example.ui.components.DocumentScannerSheet
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingChip
import com.example.ui.theme.*
import com.example.viewmodel.GovIntelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    viewModel: GovIntelViewModel,
    onAnalyzeDocInChat: (DocumentEntity) -> Unit = {}
) {
    val documents by viewModel.documents.collectAsState()
    val selectedDoc by viewModel.selectedDoc.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showIngestDialog by remember { mutableStateOf(false) }
    var showScannerDialog by remember { mutableStateOf(false) }

    val filteredDocs = remember(documents, searchQuery, selectedFilter) {
        documents.filter { doc ->
            val matchesQuery = searchQuery.isBlank() ||
                    doc.title.contains(searchQuery, ignoreCase = true) ||
                    doc.circularNumber.contains(searchQuery, ignoreCase = true) ||
                    doc.department.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "ELECTRONICS" -> doc.department.contains("Electronics", ignoreCase = true)
                "COMMERCE" -> doc.department.contains("Commerce", ignoreCase = true)
                "RESTRICTED" -> doc.classification != "PUBLIC"
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("vault_screen"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header & Ingest Action
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Policy Documents Vault",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${documents.size} Circulars Ingested • Edge Vector Indexed",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showScannerDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ElectricBlue
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("camera_scan_button_top")
                        ) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { showIngestDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlue,
                                contentColor = GlassDarkBackground
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("ingest_button_top")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ingest", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Search Field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vault_search_input"),
                    placeholder = { Text("Search circulars, ministries, clauses...", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = GlassBorderSubtle,
                        focusedContainerColor = Color(0x300C162E),
                        unfocusedContainerColor = Color(0x300C162E),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            // 3. Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterPill(title = "All Policies", isSelected = selectedFilter == "ALL", onClick = { selectedFilter = "ALL" })
                    }
                    item {
                        FilterPill(title = "MeitY Directives", isSelected = selectedFilter == "ELECTRONICS", onClick = { selectedFilter = "ELECTRONICS" })
                    }
                    item {
                        FilterPill(title = "Procurement / DPIIT", isSelected = selectedFilter == "COMMERCE", onClick = { selectedFilter = "COMMERCE" })
                    }
                    item {
                        FilterPill(title = "Encrypted Vault", isSelected = selectedFilter == "RESTRICTED", onClick = { selectedFilter = "RESTRICTED" })
                    }
                }
            }

            // 4. Document Cards List
            items(filteredDocs, key = { it.id }) { doc ->
                DocumentItemCard(
                    doc = doc,
                    onInspect = { viewModel.selectDocument(doc) },
                    onAnalyze = {
                        viewModel.selectDocument(doc)
                        onAnalyzeDocInChat(doc)
                    },
                    onToggleEncryption = { viewModel.toggleEncryption(doc) },
                    onDelete = { viewModel.deleteDocument(doc.id) }
                )
            }
        }
    }

    // Modal Inspection Sheet
    if (selectedDoc != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectDocument(null) },
            containerColor = Color(0xFF0C1427),
            dragHandle = { BottomSheetDefaults.DragHandle(color = ElectricBlue) }
        ) {
            DocumentDetailSheet(doc = selectedDoc!!, onClose = { viewModel.selectDocument(null) })
        }
    }

    // Ingestion Dialog
    if (showIngestDialog) {
        IngestDocumentDialog(
            onDismiss = { showIngestDialog = false },
            onIngest = { title, circularNo, dept, cat, classif, content ->
                viewModel.ingestNewDocument(title, circularNo, dept, cat, classif, content)
                showIngestDialog = false
            }
        )
    }

    // Camera Document Scanner Sheet
    if (showScannerDialog) {
        ModalBottomSheet(
            onDismissRequest = { showScannerDialog = false },
            containerColor = Color(0xFF070B14),
            dragHandle = { BottomSheetDefaults.DragHandle(color = ElectricBlue) }
        ) {
            DocumentScannerSheet(
                onDismiss = { showScannerDialog = false },
                onDocumentScannedAndIngest = { title, circularNo, dept, cat, classif, content ->
                    viewModel.ingestNewDocument(title, circularNo, dept, cat, classif, content)
                    showScannerDialog = false
                }
            )
        }
    }
}

@Composable
private fun FilterPill(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (isSelected) ElectricBlue else GlassBorderSubtle, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) ElectricBlue.copy(alpha = 0.2f) else Color(0x20FFFFFF),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DocumentItemCard(
    doc: DocumentEntity,
    onInspect: () -> Unit,
    onAnalyze: () -> Unit,
    onToggleEncryption: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x250C162E)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doc.circularNumber,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = doc.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = "${doc.department} • ${doc.issueDate}",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                IconButton(
                    onClick = onToggleEncryption,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (doc.isEncrypted) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Toggle E2EE",
                        tint = if (doc.isEncrypted) AmberWarning else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = doc.summary,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${doc.chunkCount} Vector Chunks",
                    color = EmeraldTeal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = onInspect,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Clauses", color = TextSecondary, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onAnalyze,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue.copy(alpha = 0.25f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Query", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentDetailSheet(doc: DocumentEntity, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = doc.circularNumber, color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = doc.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            GlowingChip(text = doc.classification, color = if (doc.classification == "PUBLIC") EmeraldTeal else AmberWarning)
        }

        Text(text = "STATUTORY CONTENT", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x30050B14))
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = doc.fullText,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }

        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = GlassDarkBackground)
        ) {
            Text("Done", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun IngestDocumentDialog(
    onDismiss: () -> Unit,
    onIngest: (title: String, circularNo: String, dept: String, cat: String, classif: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var circularNo by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("MeitY") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0C1427),
        title = { Text("Ingest Official Circular", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = circularNo,
                    onValueChange = { circularNo = it },
                    placeholder = { Text("Circular No (e.g. MeitY/2025/44)", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Document Title", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Paste statutory clauses or full circular text...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && circularNo.isNotBlank() && content.isNotBlank()) {
                        onIngest(title, circularNo, dept, "POLICY", "CONFIDENTIAL", content)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = GlassDarkBackground)
            ) {
                Text("Vectorize & Ingest", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
