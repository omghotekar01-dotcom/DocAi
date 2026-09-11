package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AuthSessionSheet
import com.example.ui.components.GlowingChip
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.GovIntelViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GovIntelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GovIntelTheme {
                MainAppLayout(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppLayout(viewModel: GovIntelViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    var showAuthSheet by remember { mutableStateOf(false) }
    var showPolicyDiffView by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GlassDarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBarGlassHeader(
                officerInitials = authState.currentUser.avatarInitials,
                officerName = authState.currentUser.name,
                isSyncing = isSyncing,
                isSubView = showPolicyDiffView,
                onBack = { showPolicyDiffView = false },
                onSyncClick = { viewModel.syncWorkspace() },
                onAvatarClick = { showAuthSheet = true }
            )
        },
        bottomBar = {
            if (!showPolicyDiffView) {
                LiquidGlassBottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = {
                        showPolicyDiffView = false
                        viewModel.setTab(it)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GlassDarkBackground,
                            Color(0xFF091124),
                            GlassDarkBackground
                        )
                    )
                )
        ) {
            if (showPolicyDiffView) {
                PolicyCompareScreen(viewModel = viewModel)
            } else {
                when (currentTab) {
                    0 -> AnalyticsDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToChat = { viewModel.setTab(1) },
                        onNavigateToCompare = { showPolicyDiffView = true },
                        onOpenIngest = { viewModel.setTab(2) }
                    )
                    1 -> AgentChatScreen(viewModel = viewModel)
                    2 -> VaultScreen(
                        viewModel = viewModel,
                        onAnalyzeDocInChat = { viewModel.setTab(1) }
                    )
                    3 -> CollabScreen(viewModel = viewModel)
                    else -> AnalyticsDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (showAuthSheet) {
        AuthSessionSheet(
            authState = authState,
            onDismiss = { showAuthSheet = false },
            onSwitchOfficer = {
                viewModel.switchOfficer(it)
                showAuthSheet = false
            },
            onRefreshSession = {
                viewModel.refreshSession()
            },
            onLogout = {
                viewModel.logout()
                showAuthSheet = false
            },
            onLogin = {
                viewModel.loginWithOAuth()
                showAuthSheet = false
            }
        )
    }
}

@Composable
fun TopAppBarGlassHeader(
    officerInitials: String,
    officerName: String,
    isSyncing: Boolean,
    isSubView: Boolean = false,
    onBack: () -> Unit = {},
    onSyncClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color(0xF2070D1E),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSubView) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Policy Comparison",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(listOf(ElectricBlue, DeepIndigo))
                            )
                            .border(1.dp, NeonCyan, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "GovDoc AI",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldTeal)
                            )
                            Text(
                                text = "Gemma 2B Edge INT4",
                                color = ElectricBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Right actions: Sync & Officer Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Officer Avatar & Auth Sheet Trigger
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable(onClick = onAvatarClick)
                        .testTag("auth_officer_button"),
                    color = Color(0x30111E38),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = officerInitials,
                                color = GlassDarkBackground,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "JWT Active",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiquidGlassBottomNavBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color(0xF2070D1E),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x2038BDF8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                title = "Dashboard",
                icon = Icons.Default.Dashboard,
                isSelected = currentTab == 0,
                tag = "nav_dashboard",
                onClick = { onTabSelected(0) }
            )
            NavBarItem(
                title = "Gemma RAG",
                icon = Icons.Default.AutoAwesome,
                isSelected = currentTab == 1,
                tag = "nav_gemma",
                onClick = { onTabSelected(1) }
            )
            NavBarItem(
                title = "Documents",
                icon = Icons.Default.FolderSpecial,
                isSelected = currentTab == 2,
                tag = "nav_vault",
                onClick = { onTabSelected(2) }
            )
            NavBarItem(
                title = "Collab",
                icon = Icons.Default.Group,
                isSelected = currentTab == 3,
                tag = "nav_collab",
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    val activeColor = ElectricBlue

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) activeColor else TextSecondary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            color = if (isSelected) Color.White else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun Greeting(name: String = "GovDoc", modifier: Modifier = Modifier) {
    Text(
        text = "GovDoc Intelligence AI ($name)",
        color = Color.White,
        modifier = modifier
    )
}
