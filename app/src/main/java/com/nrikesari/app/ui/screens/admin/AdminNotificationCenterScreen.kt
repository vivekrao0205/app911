package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.Notification
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationCenterScreen(navController: NavController) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var alertsList by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") } // All, Registration, Message, Booking, Inquiry
    var showArchived by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

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

    val filteredAlerts = remember(alertsList, searchQuery, filterType, showArchived) {
        alertsList.filter {
            val isArchived = it.status == "Archived"
            val matchesArchive = if (showArchived) isArchived else !isArchived
            matchesArchive &&
            (filterType == "All" || it.type.equals(filterType, ignoreCase = true)) &&
            (it.title.contains(searchQuery, ignoreCase = true) || it.message.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
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
                }
            )
        }
    ) { padding ->
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

            /* FILTER TABS */
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            
            /* SHOW ARCHIVED TOGGLE */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Archived Alerts", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = showArchived,
                    onCheckedChange = { showArchived = it }
                )
            }

            Spacer(Modifier.height(12.dp))

            /* LIST */
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredAlerts.isEmpty()) {
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
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredAlerts, key = { it.id }) { alert ->
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
        )
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
