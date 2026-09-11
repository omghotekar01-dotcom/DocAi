package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChangeType
import com.example.model.ClauseComparison
import com.example.model.PolicyComparisonReport
import com.example.ui.components.DocumentScannerSheet
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingChip
import com.example.ui.theme.*
import com.example.viewmodel.GovIntelViewModel

enum class CompareViewMode {
    CARD,
    SPLIT,
    GRID,
    COMPACT_LIST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyCompareScreen(viewModel: GovIntelViewModel) {
    val documents by viewModel.documents.collectAsState()
    val report by viewModel.comparisonReport.collectAsState()
    val isComparing by viewModel.isComparing.collectAsState()
    val docAId by viewModel.selectedDocAId.collectAsState()
    val docBId by viewModel.selectedDocBId.collectAsState()

    var showDocAMenu by remember { mutableStateOf(false) }
    var showDocBMenu by remember { mutableStateOf(false) }
    var showScannerSheet by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(CompareViewMode.CARD) }
    val clipboardManager = LocalClipboardManager.current
    var copiedSuccess by remember { mutableStateOf(false) }

    val docA = documents.find { it.id == docAId }
    val docB = documents.find { it.id == docBId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Policy Comparison Engine",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PS01 Core Demo • Cross-Circular Clause Differential Analysis",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                GlowingChip(
                    text = "Clause Diff",
                    color = VibrantPurple,
                    icon = Icons.AutoMirrored.Filled.CompareArrows
                )
            }
        }

        // Circular Selection Controls
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x221E293B),
                borderColor = GlassBorderStroke
            ) {
                Text(
                    text = "Select Policy Circulars for Differential Reasoning:",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Document A Selector
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { showDocAMenu = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_doc_a_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0x280284C7))
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Prior Base Policy (2024)", color = ElectricBlue, fontSize = 9.sp)
                                Text(
                                    text = docA?.circularNumber ?: "Select Doc A",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showDocAMenu,
                            onDismissRequest = { showDocAMenu = false }
                        ) {
                            documents.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text("${d.circularNumber}: ${d.title.take(30)}...") },
                                    onClick = {
                                        viewModel.setComparisonDocs(d.id, docBId)
                                        showDocAMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )

                    // Document B Selector
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { showDocBMenu = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_doc_b_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantPurple.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0x286366F1))
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Revised Superseding (2025)", color = VibrantPurple, fontSize = 9.sp)
                                Text(
                                    text = docB?.circularNumber ?: "Select Doc B",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showDocBMenu,
                            onDismissRequest = { showDocBMenu = false }
                        ) {
                            documents.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text("${d.circularNumber}: ${d.title.take(30)}...") },
                                    onClick = {
                                        viewModel.setComparisonDocs(docAId, d.id)
                                        showDocBMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showScannerSheet = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("compare_scan_doc_btn"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.7f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan / Upload Doc", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.runPolicyComparison(docAId, docBId) },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("run_comparison_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        enabled = !isComparing,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        if (isComparing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = GlassDarkBackground,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Diffing...", color = GlassDarkBackground, fontSize = 11.sp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GlassDarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run Policy Diff", color = GlassDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Executive Synthesis Banner
        if (report != null) {
            val r = report!!
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x300F172A),
                    borderColor = NeonCyan.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Executive Synthesis & Regulatory Delta",
                                color = NeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = {
                                val fullReport = buildString {
                                    append("CIVICLENS POLICY COMPARISON REPORT\n")
                                    append("Prior: ${r.docACircular} | Revised: ${r.docBCircular}\n\n")
                                    append("EXECUTIVE SUMMARY:\n${r.executiveSummary}\n\n")
                                    append("KEY CLAUSE DELTAS:\n")
                                    r.clauseComparisons.forEach { c ->
                                        append("[${c.changeType}] ${c.clauseTitle}\n")
                                        append("Old: ${c.oldProvision}\nNew: ${c.newProvision}\nImpact: ${c.impactAssessment}\n\n")
                                    }
                                }
                                clipboardManager.setText(AnnotatedString(fullReport))
                                copiedSuccess = true
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (copiedSuccess) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy Report",
                                tint = if (copiedSuccess) EmeraldTeal else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = r.executiveSummary,
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Key Takeaways:",
                        color = ElectricBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    r.keyTakeaways.forEach { takeaway ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = ElectricBlue, fontSize = 12.sp)
                            Text(
                                text = takeaway,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Section Header & View Mode Switcher
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Differential Matrix (${r.clauseComparisons.size} provisions)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // View Mode Switcher Pill
                    Surface(
                        color = Color(0x350F172A),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier.padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            ViewModeIconButton(
                                icon = Icons.Default.Dashboard,
                                description = "Card View",
                                isSelected = viewMode == CompareViewMode.CARD,
                                onClick = { viewMode = CompareViewMode.CARD }
                            )
                            ViewModeIconButton(
                                icon = Icons.Default.ViewColumn,
                                description = "Side-by-Side Split View",
                                isSelected = viewMode == CompareViewMode.SPLIT,
                                onClick = { viewMode = CompareViewMode.SPLIT }
                            )
                            ViewModeIconButton(
                                icon = Icons.Default.GridView,
                                description = "Compact Grid View",
                                isSelected = viewMode == CompareViewMode.GRID,
                                onClick = { viewMode = CompareViewMode.GRID }
                            )
                            ViewModeIconButton(
                                icon = Icons.AutoMirrored.Filled.List,
                                description = "Dense List View",
                                isSelected = viewMode == CompareViewMode.COMPACT_LIST,
                                onClick = { viewMode = CompareViewMode.COMPACT_LIST }
                            )
                        }
                    }
                }
            }

            // Clause items according to selected view mode
            when (viewMode) {
                CompareViewMode.CARD -> {
                    items(r.clauseComparisons) { item ->
                        ClauseComparisonCard(item = item)
                    }
                }
                CompareViewMode.SPLIT -> {
                    items(r.clauseComparisons) { item ->
                        ClauseComparisonSplitView(item = item)
                    }
                }
                CompareViewMode.GRID -> {
                    // Chunk comparisons in pairs for a responsive 2-column grid layout
                    val chunkedList = r.clauseComparisons.chunked(2)
                    items(chunkedList) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ClauseComparisonGridCard(item = pair[0])
                            }
                            if (pair.size > 1) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ClauseComparisonGridCard(item = pair[1])
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                CompareViewMode.COMPACT_LIST -> {
                    items(r.clauseComparisons) { item ->
                        ClauseComparisonDenseListItem(item = item)
                    }
                }
            }

            // Compliance Action Items Checklist
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x28064E3B),
                    borderColor = EmeraldTeal.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = EmeraldTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Statutory Compliance Checklist for Departments",
                            color = EmeraldTeal,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    r.complianceActionItems.forEach { actionItem ->
                        var isChecked by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isChecked = !isChecked }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { isChecked = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmeraldTeal,
                                    uncheckedColor = TextSecondary
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = actionItem,
                                color = if (isChecked) TextMuted else Color.White,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showScannerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showScannerSheet = false },
            containerColor = Color(0xFF070B14),
            dragHandle = { BottomSheetDefaults.DragHandle(color = ElectricBlue) }
        ) {
            DocumentScannerSheet(
                onDismiss = { showScannerSheet = false },
                onDocumentScannedAndIngest = { title, circularNo, dept, cat, classif, content ->
                    viewModel.ingestNewDocument(title, circularNo, dept, cat, classif, content)
                    showScannerSheet = false
                }
            )
        }
    }
}

@Composable
fun ClauseComparisonCard(item: ClauseComparison) {
    val (badgeColor, badgeText) = when (item.changeType) {
        ChangeType.TIGHTENED -> CrimsonAlert to "TIGHTENED"
        ChangeType.MODIFIED -> AmberWarning to "MODIFIED"
        ChangeType.ADDED -> EmeraldTeal to "NEW PROVISION"
        ChangeType.REMOVED -> TextMuted to "SUPERSEDED"
        ChangeType.UNCHANGED -> ElectricBlue to "UNCHANGED"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x221E293B),
        borderColor = badgeColor.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.clauseTitle,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.2f))
                    .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Previous Policy
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x22EF4444))
                .padding(8.dp)
        ) {
            Text(
                text = "Previous (2024 Circular):",
                color = Color(0xFFF87171),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.oldProvision,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Revised Policy
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x2210B981))
                .padding(8.dp)
        ) {
            Text(
                text = "Revised (2025 Mandate):",
                color = EmeraldTeal,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.newProvision,
                color = Color.White,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Impact: ${item.impactAssessment}",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun ViewModeIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) ElectricBlue.copy(alpha = 0.25f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = if (isSelected) ElectricBlue else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ClauseComparisonSplitView(item: ClauseComparison) {
    val (badgeColor, badgeText) = when (item.changeType) {
        ChangeType.TIGHTENED -> CrimsonAlert to "TIGHTENED"
        ChangeType.MODIFIED -> AmberWarning to "MODIFIED"
        ChangeType.ADDED -> EmeraldTeal to "NEW"
        ChangeType.REMOVED -> TextMuted to "SUPERSEDED"
        ChangeType.UNCHANGED -> ElectricBlue to "UNCHANGED"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x221E293B),
        borderColor = badgeColor.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.clauseTitle,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.2f))
                    .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Side-by-Side Dual Pane Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Prior Policy Pane (Left)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x22EF4444))
                    .border(1.dp, Color(0x40EF4444), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "PRIOR (2024)",
                    color = Color(0xFFF87171),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.oldProvision,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }

            // Revised Policy Pane (Right)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x2210B981))
                    .border(1.dp, Color(0x4010B981), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "REVISED (2025)",
                    color = EmeraldTeal,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.newProvision,
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Impact: ${item.impactAssessment}",
            color = TextSecondary,
            fontSize = 10.sp,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun ClauseComparisonGridCard(item: ClauseComparison) {
    val (badgeColor, badgeText) = when (item.changeType) {
        ChangeType.TIGHTENED -> CrimsonAlert to "TIGHTENED"
        ChangeType.MODIFIED -> AmberWarning to "MODIFIED"
        ChangeType.ADDED -> EmeraldTeal to "NEW"
        ChangeType.REMOVED -> TextMuted to "SUPERSEDED"
        ChangeType.UNCHANGED -> ElectricBlue to "UNCHANGED"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x25121D36),
        borderColor = badgeColor.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.clauseTitle,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor.copy(alpha = 0.2f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(badgeText, color = badgeColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "New Mandate:",
            color = EmeraldTeal,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.newProvision,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 10.sp,
            lineHeight = 13.sp,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Delta Impact:",
            color = ElectricBlue,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.impactAssessment,
            color = TextSecondary,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            maxLines = 2
        )
    }
}

@Composable
fun ClauseComparisonDenseListItem(item: ClauseComparison) {
    val (badgeColor, badgeText) = when (item.changeType) {
        ChangeType.TIGHTENED -> CrimsonAlert to "TIGHTENED"
        ChangeType.MODIFIED -> AmberWarning to "MODIFIED"
        ChangeType.ADDED -> EmeraldTeal to "NEW"
        ChangeType.REMOVED -> TextMuted to "SUPERSEDED"
        ChangeType.UNCHANGED -> ElectricBlue to "UNCHANGED"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(10.dp)),
        color = Color(0x300D162C),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.clauseTitle,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• $badgeText",
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "Revised: ${item.newProvision}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 2,
                    lineHeight = 15.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
