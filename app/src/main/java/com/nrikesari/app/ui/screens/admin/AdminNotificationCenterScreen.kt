package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.Notification
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationCenterScreen(navController: NavController) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var alertsList by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") } // All, Registration, Message, Booking, Inquiry
    var showArchived by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    // Table sorting & pagination states
    var sortBy by remember { mutableStateOf("Newest") } // Newest, Oldest, Title
    var currentPage by remember { mutableStateOf(0) }
    val itemsPerPage = 8

    // Manual notification composer state
    var isCreateSheetOpen by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newMessage by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("general") }
    var newClickAction by remember { mutableStateOf("") }

    // Live Auditing Logs state
    val logsList = remember { mutableStateListOf<String>() }
    var showLogs by remember { mutableStateOf(false) }

    // Live Snapshot Listener for notifications
    DisposableEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val listener = firestore.collection("notifications")
            .whereEqualTo("adminAlert", true)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(Notification::class.java)
                    alertsList = list.sortedByDescending { it.timestamp }
                }
                isLoading = false
            }

        // Cleanup listener on dispose
        onDispose {
            listener.remove()
        }
    }

    // Reset pagination when filter/search/sort changes or notifications list size changes
    LaunchedEffect(searchQuery, filterType, showArchived, sortBy, alertsList.size) {
        currentPage = 0
    }

    val filteredAlerts = remember(alertsList, searchQuery, filterType, showArchived, sortBy) {
        var list = alertsList.filter {
            val isArchived = it.status == "Archived"
            val matchesArchive = if (showArchived) isArchived else !isArchived
            matchesArchive &&
            (filterType == "All" || it.type.equals(filterType, ignoreCase = true)) &&
            (it.title.contains(searchQuery, ignoreCase = true) || it.message.contains(searchQuery, ignoreCase = true))
        }

        list = when (sortBy) {
            "Oldest" -> list.sortedBy { it.timestamp }
            "Title" -> list.sortedBy { it.title.lowercase() }
            else -> list.sortedByDescending { it.timestamp } // Newest
        }

        list
    }

    // Pagination calculations
    val totalItems = filteredAlerts.size
    val totalPages = maxOf(1, (totalItems + itemsPerPage - 1) / itemsPerPage)
    val paginatedAlerts = remember(filteredAlerts, currentPage) {
        filteredAlerts.drop(currentPage * itemsPerPage).take(itemsPerPage)
    }

    AdminDrawerLayout(
        navController = navController,
        currentRoute = "admin_notifications",
        title = "Notification Center",
        actions = {
            IconButton(onClick = { 
                showLogs = false
                logsList.clear()
                isCreateSheetOpen = true 
            }) {
                Icon(Icons.Default.AddCircleOutline, "Send Notification")
            }
            if (alertsList.any { !it.isRead && it.status != "Archived" }) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            firebaseService.markAllNotificationsAsRead(null, isAdmin = true)
                        }
                    }
                ) {
                    Text("Mark All Read", fontWeight = FontWeight.Bold)
                }
            }
            if (alertsList.isNotEmpty()) {
                TextButton(
                    onClick = {
                        showClearAllDialog = true
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        if (showClearAllDialog) {
            AlertDialog(
                onDismissRequest = { showClearAllDialog = false },
                title = { Text("Clear All Alerts") },
                text = { Text("Are you sure you want to delete all admin alerts? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearAllDialog = false
                            coroutineScope.launch {
                                firebaseService.clearAllNotifications(null, isAdmin = true)
                            }
                        }
                    ) {
                        Text("Clear All", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearAllDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            /* SEARCH BAR */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search alerts...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(Modifier.height(12.dp))

            /* FILTER TABS ROW */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Registration", "Message", "Booking", "Inquiry").forEach { type ->
                    val isSel = filterType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { filterType = type }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            type,
                            color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            
            /* SHOW ARCHIVED & SORT ROW */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Show Archived Alerts", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.width(4.dp))
                    Switch(
                        checked = showArchived,
                        onCheckedChange = { showArchived = it },
                        modifier = Modifier.scale(0.8f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            sortBy = when (sortBy) {
                                "Newest" -> "Oldest"
                                "Oldest" -> "Title"
                                else -> "Newest"
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (sortBy) {
                                "Title" -> "Title A-Z"
                                "Oldest" -> "Oldest First"
                                else -> "Newest First"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = if (sortBy == "Newest") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* LIST AND PAGINATION */
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (paginatedAlerts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(54.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(10.dp))
                        Text("No notifications found", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(paginatedAlerts, key = { it.id }) { alert ->
                            AdminNotificationItem(
                                alert = alert,
                                onMarkRead = {
                                    coroutineScope.launch {
                                        firebaseService.markNotificationAsRead(alert.id)
                                    }
                                },
                                onArchive = {
                                    coroutineScope.launch {
                                        firebaseService.updateNotificationStatus(alert.id, "Archived")
                                    }
                                },
                                onDelete = {
                                    coroutineScope.launch {
                                        firebaseService.deleteNotification(alert.id)
                                    }
                                }
                            )
                        }
                    }

                    /* PAGINATION FOOTER */
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Page ${currentPage + 1} of $totalPages (${totalItems} items)",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledIconButton(
                                onClick = { if (currentPage > 0) currentPage-- },
                                enabled = currentPage > 0,
                                modifier = Modifier.size(36.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Icon(Icons.Default.ChevronLeft, null, modifier = Modifier.size(16.dp))
                            }
                            FilledIconButton(
                                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                                enabled = currentPage < totalPages - 1,
                                modifier = Modifier.size(36.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    /* MANUAL NOTIFICATION COMPOSER BOTTOM SHEET */
    if (isCreateSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isCreateSheetOpen = false },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 50.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Broadcast User Notification",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Notification Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    label = { Text("Message Body") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Alert Type", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("general", "project_update", "message", "booking", "inquiry").forEach { typeOpt ->
                        val isSel = newType == typeOpt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { newType = typeOpt }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = typeOpt.replace("_", " ").uppercase(),
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                              )
                        }
                    }
                }

                OutlinedTextField(
                    value = newClickAction,
                    onValueChange = { newClickAction = it },
                    label = { Text("Deep Link Route (Optional)") },
                    placeholder = { Text("e.g. chat/project_id or my_projects") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                /* AUDIT & DELIVERY LOGS CONSOLE */
                if (showLogs) {
                    Spacer(Modifier.height(4.dp))
                    Text("Broadcaster Delivery Audit Logs:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1E1E))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(logsList) { log ->
                                val color = when {
                                    log.contains("[SUCCESS]") -> Color(0xFF4CAF50)
                                    log.contains("[ERROR]") -> Color(0xFFF44336)
                                    log.contains("[INFO]") -> Color(0xFF2196F3)
                                    else -> Color(0xFFD4D4D4)
                                }
                                Text(
                                    text = log,
                                    color = color,
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (newTitle.isBlank() || newMessage.isBlank()) return@Button
                        coroutineScope.launch {
                            showLogs = true
                            logsList.clear()
                            firebaseService.broadcastCustomNotification(
                                context = context,
                                title = newTitle.trim(),
                                message = newMessage.trim(),
                                type = newType,
                                clickAction = newClickAction.trim(),
                                onLog = { logMsg ->
                                    logsList.add(logMsg)
                                }
                            )
                            newTitle = ""
                            newMessage = ""
                            newType = "general"
                            newClickAction = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Send, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Broadcast & Dispatch Push", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminNotificationItem(
    alert: Notification,
    onMarkRead: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isRead) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        alert.title,
                        fontWeight = if (alert.isRead) FontWeight.Medium else FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (!alert.isRead) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            Spacer(Modifier.width(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!alert.isRead) {
                    IconButton(onClick = onMarkRead) {
                        Icon(Icons.Default.Done, "Mark Read", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                if (alert.status != "Archived") {
                    IconButton(onClick = onArchive) {
                        Icon(Icons.Default.Archive, "Archive", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
