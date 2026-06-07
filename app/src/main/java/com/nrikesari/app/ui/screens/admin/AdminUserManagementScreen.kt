package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.Booking
import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.model.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementScreen(navController: NavController) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var usersList by remember { mutableStateOf<List<User>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // User Details data state
    var userInquiries by remember { mutableStateOf<List<ProjectInquiry>>(emptyList()) }
    var userBookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isDetailLoading by remember { mutableStateOf(false) }

    fun loadUsers() {
        isLoading = true
        coroutineScope.launch {
            val result = firebaseService.getAllUsers()
            if (result.isSuccess) {
                usersList = result.getOrDefault(emptyList())
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadUsers()
    }

    val filteredUsers = remember(usersList, searchQuery) {
        usersList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.email.contains(searchQuery, ignoreCase = true) ||
            it.phone.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
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
            /* SEARCH USER */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search users by name, email or phone...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(Modifier.height(16.dp))

            /* USERS LIST */
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No users found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredUsers, key = { it.uid }) { user ->
                        UserListItem(
                            user = user,
                            onClick = {
                                selectedUser = user
                                isDetailLoading = true
                                coroutineScope.launch {
                                    val inqRes = firebaseService.getUserProjects(user.uid)
                                    val bookRes = firebaseService.getUserBookings(user.uid)
                                    userInquiries = inqRes.getOrDefault(emptyList())
                                    userBookings = bookRes.getOrDefault(emptyList())
                                    isDetailLoading = false
                                }
                            },
                            onSuspendToggle = {
                                coroutineScope.launch {
                                    if (user.accountStatus == "suspended") {
                                        firebaseService.reactivateUser(user.uid)
                                    } else {
                                        firebaseService.suspendUser(user.uid)
                                    }
                                    loadUsers()
                                }
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    firebaseService.deleteUser(user.uid)
                                    loadUsers()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    /* USER DETAILS MODAL SHEET */
    selectedUser?.let { user ->
        ModalBottomSheet(
            onDismissRequest = { selectedUser = null },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1).uppercase(Locale.getDefault()),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                item {
                    HorizontalDivider()
                }

                item {
                    Text("Profile Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text("Phone: ${if(user.phone.isBlank()) "N/A" else user.phone}", fontSize = 14.sp)
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    Text("Registration Date: ${sdf.format(Date(user.joinedAt))}", fontSize = 14.sp)
                    Text("Status: ${user.accountStatus.uppercase(Locale.getDefault())}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }

                item {
                    HorizontalDivider()
                }

                /* ACTIVITY LOGS */
                item {
                    Text("Activity History", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                if (isDetailLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    item {
                        Text("Project Inquiries: ${userInquiries.size}", fontSize = 14.sp)
                        Text("Consultation Bookings: ${userBookings.size}", fontSize = 14.sp)
                    }

                    if (userInquiries.isNotEmpty()) {
                        item {
                            Text("Inquiries Submitted:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        items(userInquiries) { inquiry ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(inquiry.service, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(inquiry.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Status: ${inquiry.status}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    if (userBookings.isNotEmpty()) {
                        item {
                            Text("Meetings Booked:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        items(userBookings) { booking ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Consultation Date: ${booking.date} @ ${booking.timeSlot}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Status: ${booking.status}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserListItem(
    user: User,
    onClick: () -> Unit,
    onSuspendToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(1).uppercase(Locale.getDefault()),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(Modifier.height(4.dp))
                // Account Status Chip
                val statusBg = if (user.accountStatus == "suspended") MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer
                val statusText = if (user.accountStatus == "suspended") MaterialTheme.colorScheme.onErrorContainer
                else MaterialTheme.colorScheme.onPrimaryContainer
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        user.accountStatus.uppercase(Locale.getDefault()),
                        color = statusText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onSuspendToggle) {
                    Icon(
                        if (user.accountStatus == "suspended") Icons.Default.Refresh else Icons.Default.Block,
                        contentDescription = "Suspend / Reactivate",
                        tint = if (user.accountStatus == "suspended") MaterialTheme.colorScheme.primary else Color(0xFFE28B00)
                    )
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, "Delete User", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete User Account?") },
            text = { Text("Are you sure you want to delete this user profile? This action is irreversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
