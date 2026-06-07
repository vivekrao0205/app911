package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.Notification
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var stats by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var recentAlerts by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val statsResult = firebaseService.getAnalyticsCounts()
        if (statsResult.isSuccess) {
            stats = statsResult.getOrDefault(emptyMap())
        }
        val alertsResult = firebaseService.getAdminNotifications()
        if (alertsResult.isSuccess) {
            recentAlerts = alertsResult.getOrDefault(emptyList()).take(5)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("admin_notifications") }) {
                        Box {
                            Icon(Icons.Default.Notifications, null)
                            val unreadCount = recentAlerts.count { !it.isRead }
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.error)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                            )
                        )
                    )
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Welcome Admin",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Here is a summary of the agency's performance and requests.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                /* STATISTICS GRID */
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCardItem(
                                title = "Total Users",
                                value = stats["Total Users"]?.toString() ?: "0",
                                icon = Icons.Default.People,
                                modifier = Modifier.weight(1f)
                            )
                            StatCardItem(
                                title = "Total Projects",
                                value = stats["Total Projects"]?.toString() ?: "0",
                                icon = Icons.Default.Work,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCardItem(
                                title = "Bookings",
                                value = stats["Total Bookings"]?.toString() ?: "0",
                                icon = Icons.Default.CalendarToday,
                                modifier = Modifier.weight(1f)
                            )
                            StatCardItem(
                                title = "Inquiries",
                                value = stats["Total Inquiries"]?.toString() ?: "0",
                                icon = Icons.Default.SupportAgent,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                /* QUICK ACTIONS PANEL */
                item {
                    Text(
                        "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            QuickActionRow(
                                title = "User Management",
                                subtitle = "Manage registered accounts and suspension settings",
                                icon = Icons.Default.ManageAccounts
                            ) { navController.navigate("admin_users") }
                            
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            
                            QuickActionRow(
                                title = "Communications",
                                subtitle = "View conversations, inquiries, and ticket priorities",
                                icon = Icons.Default.ChatBubble
                            ) { navController.navigate("admin_communications") }
                            
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            
                            QuickActionRow(
                                title = "Portfolio Manager",
                                subtitle = "Create, edit, publish, and delete projects",
                                icon = Icons.Default.LibraryAdd
                            ) { navController.navigate("admin_projects") }
                            
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            
                            QuickActionRow(
                                title = "Analytics Reports",
                                subtitle = "View growth statistics and export data reports",
                                icon = Icons.Default.InsertChart
                            ) { navController.navigate("admin_analytics") }
                        }
                    }
                }

                /* RECENT ACTIVITIES ALERT FEED */
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Activities",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "View All",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate("admin_notifications") }
                        )
                    }
                }

                if (recentAlerts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No recent alerts received.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(recentAlerts) { alert ->
                        RecentActivityCard(alert)
                    }
                }

                item { Spacer(Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun StatCardItem(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuickActionRow(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun RecentActivityCard(alert: Notification) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val alertIcon = when (alert.type.lowercase()) {
                "registration" -> Icons.Default.PersonAdd
                "message" -> Icons.Default.ChatBubbleOutline
                "booking" -> Icons.Default.EventAvailable
                "inquiry" -> Icons.Default.AssignmentInd
                "ticket" -> Icons.Default.ErrorOutline
                else -> Icons.Default.Notifications
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (alert.isRead) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.errorContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    alertIcon,
                    null,
                    tint = if (alert.isRead) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    alert.title,
                    fontWeight = if (alert.isRead) FontWeight.Medium else FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
