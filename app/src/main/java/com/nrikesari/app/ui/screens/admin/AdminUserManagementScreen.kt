package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.draw.scale
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

    var editingBooking by remember { mutableStateOf<Booking?>(null) }
    var selectedBookingStatus by remember { mutableStateOf("Pending") }

    // Table sorting & filtering states
    var selectedStatusFilter by remember { mutableStateOf("All") } // All, Active, Suspended
    var sortBy by remember { mutableStateOf("Newest") } // Newest, Oldest, Name
    var currentPage by remember { mutableStateOf(0) }
    val itemsPerPage = 8

    fun loadUserDetails(userId: String) {
        isDetailLoading = true
        coroutineScope.launch {
            val inqRes = firebaseService.getUserProjects(userId)
            val bookRes = firebaseService.getUserBookings(userId)
            userInquiries = inqRes.getOrDefault(emptyList())
            userBookings = bookRes.getOrDefault(emptyList())
            isDetailLoading = false
        }
    }

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

    // Reset page index on filter change
    LaunchedEffect(searchQuery, selectedStatusFilter, sortBy) {
        currentPage = 0
    }

    val filteredUsers = remember(usersList, searchQuery, selectedStatusFilter, sortBy) {
        var list = usersList.filter {
            val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery, ignoreCase = true)
            val matchesStatus = when (selectedStatusFilter) {
                "Active" -> it.accountStatus == "active"
                "Suspended" -> it.accountStatus == "suspended"
                else -> true
            }
            matchesSearch && matchesStatus
        }

        list = when (sortBy) {
            "Oldest" -> list.sortedBy { it.joinedAt }
            "Name" -> list.sortedBy { it.name.lowercase() }
            else -> list.sortedByDescending { it.joinedAt } // Newest
        }

        list
    }

    // Pagination calculations
    val totalItems = filteredUsers.size
    val totalPages = maxOf(1, (totalItems + itemsPerPage - 1) / itemsPerPage)
    val paginatedUsers = remember(filteredUsers, currentPage) {
        filteredUsers.drop(currentPage * itemsPerPage).take(itemsPerPage)
    }

    AdminDrawerLayout(
        navController = navController,
        currentRoute = "admin_users",
        title = "User Management"
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

            Spacer(Modifier.height(10.dp))

            /* FILTER CHIPS ROW */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Active", "Suspended").forEach { statusOpt ->
                        val isSel = selectedStatusFilter == statusOpt
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedStatusFilter = statusOpt },
                            label = { Text(statusOpt) }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            sortBy = when (sortBy) {
                                "Newest" -> "Oldest"
                                "Oldest" -> "Name"
                                else -> "Newest"
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (sortBy) {
                                "Name" -> "Name A-Z"
                                "Oldest" -> "Oldest Join"
                                else -> "Newest Join"
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

            /* USERS LIST AND PAGINATION */
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (paginatedUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No users found matching filters.")
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(paginatedUsers, key = { it.uid }) { user ->
                            UserListItem(
                                user = user,
                                onClick = {
                                    selectedUser = user
                                    loadUserDetails(user.uid)
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

                    /* PAGINATION CONTROLS FOOTER */
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Page ${currentPage + 1} of $totalPages (${totalItems} users)",
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

    /* PREMIUM USER DETAILS MODAL SHEET */
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
                // Profile header block
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(Locale.getDefault()),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(
                                    text = user.email,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Profile detail info card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Account Info",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Phone Number", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                Text(if(user.phone.isBlank()) "Not Provided" else user.phone, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Member Since", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                                Text(sdf.format(Date(user.joinedAt)), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Status", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                val isSusp = user.accountStatus == "suspended"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSusp) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = user.accountStatus.uppercase(),
                                        color = if (isSusp) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                /* ACTIVITY SUMMARY CARDS */
                item {
                    Text(
                        text = "Activity Overview",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (isDetailLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Assignment, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.height(4.dp))
                                    Text("${userInquiries.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text("Inquiries", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Event, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.height(4.dp))
                                    Text("${userBookings.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text("Bookings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    if (userInquiries.isNotEmpty()) {
                        item {
                            Text("Project Inquiries List", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        items(userInquiries) { inquiry ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(inquiry.service, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(inquiry.status, color = MaterialTheme.colorScheme.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(inquiry.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                }
                            }
                        }
                    }

                    if (userBookings.isNotEmpty()) {
                        item {
                            Text("Consultation Bookings List", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        items(userBookings) { booking ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        editingBooking = booking
                                        selectedBookingStatus = booking.status ?: "Pending"
                                    },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Date: ${booking.date}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(booking.status ?: "Pending", color = MaterialTheme.colorScheme.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text("Time Slot: ${booking.timeSlot}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editingBooking?.let { booking ->
        AlertDialog(
            onDismissRequest = { editingBooking = null },
            title = { Text("Update Booking Status") },
            text = {
                Column {
                    Text("Select new status for this booking:")
                    Spacer(Modifier.height(14.dp))
                    listOf("Pending", "Confirmed", "Rescheduled", "Completed", "Cancelled").forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedBookingStatus = status }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedBookingStatus == status,
                                onClick = { selectedBookingStatus = status }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(status)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            firebaseService.updateBookingStatus(booking.id, selectedBookingStatus)
                            if (booking.userId.isNotEmpty()) {
                                val notifId = java.util.UUID.randomUUID().toString()
                                val notification = com.nrikesari.app.model.Notification(
                                    id = notifId,
                                    userId = booking.userId,
                                    title = "Booking Status Updated",
                                    message = "Your booking for consultation on ${booking.date} at ${booking.timeSlot} is now '$selectedBookingStatus'.",
                                    type = "booking",
                                    clickAction = "notification_history",
                                    isAdminAlert = false
                                )
                                firebaseService.saveNotification(notification)
                            }
                            editingBooking = null
                            loadUserDetails(booking.userId)
                        }
                    }
                ) {
                    Text("Update", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBooking = null }) {
                    Text("Cancel")
                }
            }
        )
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
