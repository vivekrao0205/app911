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

import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var stats by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var recentAlerts by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var latestInquiries by remember { mutableStateOf<List<ProjectInquiry>>(emptyList()) }
    var latestMessages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var selectedSectionTab by remember { mutableStateOf(0) } // 0 = Alerts, 1 = Latest Inquiries, 2 = Recent Chats

    DisposableEffect(Unit) {
        val statsListeners = firebaseService.listenToAnalyticsCounts { updatedStats ->
            stats = updatedStats
            isLoading = false
        }
        val alertsListener = firebaseService.listenToAdminNotifications { updatedAlerts ->
            recentAlerts = updatedAlerts.take(5)
        }
        
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val inquiriesListener = db.collection("inquiries")
            .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    latestInquiries = snapshot.toObjects(ProjectInquiry::class.java)
                }
            }
            
        val messagesListener = db.collectionGroup("chats")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    latestMessages = snapshot.toObjects(ChatMessage::class.java)
                }
            }
            
        onDispose {
            statsListeners.forEach { it.remove() }
            alertsListener.remove()
            inquiriesListener.remove()
            messagesListener.remove()
        }
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
                        "Here is a summary of the agency's performance and requests in real-time.",
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
                                title = "Active Users",
                                value = stats["Active Users"]?.toString() ?: "0",
                                icon = Icons.Default.Person,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCardItem(
                                title = "Total Projects",
                                value = stats["Total Projects"]?.toString() ?: "0",
                                icon = Icons.Default.Work,
                                modifier = Modifier.weight(1f)
                            )
                            StatCardItem(
                                title = "Total Messages",
                                value = stats["Total Messages"]?.toString() ?: "0",
                                icon = Icons.Default.ChatBubble,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCardItem(
                                title = "Total Meetings",
                                value = stats["Total Bookings"]?.toString() ?: "0",
                                icon = Icons.Default.CalendarToday,
                                modifier = Modifier.weight(1f)
                            )
                            StatCardItem(
                                title = "Total Inquiries",
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

                /* TAB OVERVIEW SECTION */
                item {
                    Column {
                        TabRow(
                            selectedTabIndex = selectedSectionTab,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        ) {
                            Tab(selected = selectedSectionTab == 0, onClick = { selectedSectionTab = 0 }) {
                                Box(modifier = Modifier.padding(10.dp)) { Text("Alerts", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            }
                            Tab(selected = selectedSectionTab == 1, onClick = { selectedSectionTab = 1 }) {
                                Box(modifier = Modifier.padding(10.dp)) { Text("Latest Inquiries", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            }
                            Tab(selected = selectedSectionTab == 2, onClick = { selectedSectionTab = 2 }) {
                                Box(modifier = Modifier.padding(10.dp)) { Text("Recent Chats", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        when (selectedSectionTab) {
                            0 -> {
                                if (recentAlerts.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                        Text("No recent alerts received.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        recentAlerts.forEach { alert ->
                                            RecentActivityCard(alert)
                                        }
                                    }
                                }
                            }
                            1 -> {
                                if (latestInquiries.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                        Text("No inquiries submitted yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        latestInquiries.forEach { inquiry ->
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                                modifier = Modifier.fillMaxWidth().clickable {
                                                    navController.navigate("chat/${inquiry.id}")
                                                }
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(inquiry.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                        Text(inquiry.status, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(Modifier.height(4.dp))
                                                    Text("Requested: ${inquiry.service}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                                    Text(inquiry.description, style = MaterialTheme.typography.bodySmall, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                if (latestMessages.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                        Text("No recent chat messages found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        latestMessages.forEach { msg ->
                                            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                            val timeString = sdf.format(Date(msg.timestamp))
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                                modifier = Modifier.fillMaxWidth().clickable {
                                                    navController.navigate("chat/${msg.projectId}")
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(msg.text, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1)
                                                        Text("Inquiry ID: ${msg.projectId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                    Text(timeString, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(30.dp)) }
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
