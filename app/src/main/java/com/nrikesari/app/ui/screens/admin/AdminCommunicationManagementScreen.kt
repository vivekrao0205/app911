package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.ChatMessage
import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.model.User
import com.nrikesari.app.navigation.Screen
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCommunicationManagementScreen(navController: NavController) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(0) } // 0 = Chat Messages, 1 = Queries/Inquiries

    var inquiriesList by remember { mutableStateOf<List<ProjectInquiry>>(emptyList()) }
    var contactInquiriesList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Chat filter states
    var chatFilterType by remember { mutableStateOf("All") } // All, Unread, In Progress
    var chatSortBy by remember { mutableStateOf("Newest") } // Newest, Name
    var chatCurrentPage by remember { mutableStateOf(0) }
    val chatsPerPage = 6

    // Inquiry filter states
    var inquiryFilterStatus by remember { mutableStateOf("All") } // All, Pending, In Progress, Resolved, Closed
    var inquirySortBy by remember { mutableStateOf("Newest") } // Newest, Name
    var inquiryCurrentPage by remember { mutableStateOf(0) }
    val inquiriesPerPage = 6

    // Unread count map to enable parent filtering
    val unreadCounts = remember { mutableStateMapOf<String, Int>() }
    val lastMessageTimes = remember { mutableStateMapOf<String, Long>() }

    // Update statuses state
    var editingInquiry by remember { mutableStateOf<ProjectInquiry?>(null) }
    var selectedStatus by remember { mutableStateOf("New") }

    fun loadData() {
        isLoading = true
        coroutineScope.launch {
            val inquiriesRes = firebaseService.getAllInquiries()
            val contactsRes = firebaseService.getAllContactInquiries()
            
            inquiriesList = inquiriesRes.getOrDefault(emptyList())
            contactInquiriesList = contactsRes.getOrDefault(emptyList())
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    // Reset pagination
    LaunchedEffect(searchQuery, chatFilterType, chatSortBy) {
        chatCurrentPage = 0
    }
    LaunchedEffect(searchQuery, inquiryFilterStatus, inquirySortBy) {
        inquiryCurrentPage = 0
    }

    /* CHAT FILTERING AND SORTING */
    val filteredChats = remember(inquiriesList, searchQuery, chatFilterType, chatSortBy, unreadCounts.toMap(), lastMessageTimes.toMap()) {
        var list = inquiriesList.filter {
            val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) ||
                    it.service.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (chatFilterType) {
                "Unread" -> (unreadCounts[it.id] ?: 0) > 0
                "In Progress" -> it.status.equals("In Progress", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesFilter
        }

        list = when (chatSortBy) {
            "Name" -> list.sortedBy { it.name.lowercase() }
            else -> list.sortedByDescending { lastMessageTimes[it.id] ?: it.submittedAt } // Newest message or submission
        }

        list
    }

    val totalChats = filteredChats.size
    val totalChatPages = maxOf(1, (totalChats + chatsPerPage - 1) / chatsPerPage)
    val paginatedChats = remember(filteredChats, chatCurrentPage) {
        filteredChats.drop(chatCurrentPage * chatsPerPage).take(chatsPerPage)
    }

    /* INQUIRIES FILTERING AND SORTING */
    val filteredInquiries = remember(inquiriesList, searchQuery, inquiryFilterStatus, inquirySortBy) {
        var list = inquiriesList.filter {
            val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) ||
                    it.service.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (inquiryFilterStatus) {
                "All" -> true
                else -> it.status.equals(inquiryFilterStatus, ignoreCase = true)
            }

            matchesSearch && matchesStatus
        }

        list = when (inquirySortBy) {
            "Name" -> list.sortedBy { it.name.lowercase() }
            else -> list.sortedByDescending { it.submittedAt }
        }

        list
    }

    val totalInquiries = filteredInquiries.size
    val totalInquiryPages = maxOf(1, (totalInquiries + inquiriesPerPage - 1) / inquiriesPerPage)
    val paginatedInquiries = remember(filteredInquiries, inquiryCurrentPage) {
        filteredInquiries.drop(inquiryCurrentPage * inquiriesPerPage).take(inquiriesPerPage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Communications Manager", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            /* TABS */
            TabRow(selectedTabIndex = activeTab) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Box(modifier = Modifier.padding(14.dp)) {
                        Text("Active Chats", fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Box(modifier = Modifier.padding(14.dp)) {
                        Text("Inquiries & Forms", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* SEARCH */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (activeTab == 0) "Search active chats..." else "Search inquiries...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(Modifier.height(10.dp))

            /* RENDER CORRESPONDING TAB SECTION */
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (activeTab == 0) {
                    /* ACTIVE CHATS LIST */
                    // Filters row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("All", "Unread", "In Progress").forEach { filterOpt ->
                                val isSel = chatFilterType == filterOpt
                                FilterChip(
                                    selected = isSel,
                                    onClick = { chatFilterType = filterOpt },
                                    label = { Text(filterOpt) }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    chatSortBy = if (chatSortBy == "Newest") "Name" else "Newest"
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (chatSortBy == "Name") "Name A-Z" else "Recent Chat",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (chatSortBy == "Newest") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    if (paginatedChats.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text("No active chats match filters.")
                        }
                    } else {
                        Column(modifier = Modifier.weight(1f)) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(paginatedChats, key = { it.id }) { inquiry ->
                                    ChatListItem(
                                        inquiry = inquiry,
                                        navController = navController,
                                        onUnreadUpdate = { count ->
                                            unreadCounts[inquiry.id] = count
                                        },
                                        onLastMessageTimeUpdate = { timestamp ->
                                            lastMessageTimes[inquiry.id] = timestamp
                                        }
                                    )
                                }
                            }

                            /* CHATS PAGINATION FOOTER */
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Page ${chatCurrentPage + 1} of $totalChatPages (${totalChats} chats)",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilledIconButton(
                                        onClick = { if (chatCurrentPage > 0) chatCurrentPage-- },
                                        enabled = chatCurrentPage > 0,
                                        modifier = Modifier.size(36.dp),
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Icon(Icons.Default.ChevronLeft, null, modifier = Modifier.size(16.dp))
                                    }
                                    FilledIconButton(
                                        onClick = { if (chatCurrentPage < totalChatPages - 1) chatCurrentPage++ },
                                        enabled = chatCurrentPage < totalChatPages - 1,
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
                } else {
                    /* INQUIRIES & CONTACT FORMS LIST */
                    // Filters row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("All", "Pending", "In Progress", "Resolved", "Closed").forEach { statusOpt ->
                            val isSel = inquiryFilterStatus == statusOpt
                            FilterChip(
                                selected = isSel,
                                onClick = { inquiryFilterStatus = statusOpt },
                                label = { Text(statusOpt) }
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    inquirySortBy = if (inquirySortBy == "Newest") "Name" else "Newest"
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (inquirySortBy == "Name") "Name A-Z" else "Recent",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (inquirySortBy == "Newest") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    if (paginatedInquiries.isEmpty() && contactInquiriesList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text("No inquiries match the filters.")
                        }
                    } else {
                        Column(modifier = Modifier.weight(1f)) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (paginatedInquiries.isNotEmpty()) {
                                    item {
                                        Text("Project Inquiries", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    }
                                    items(paginatedInquiries, key = { "inq_${it.id}" }) { inquiry ->
                                        Card(
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(inquiry.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                                            .clickable {
                                                                editingInquiry = inquiry
                                                                selectedStatus = inquiry.status
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                                    ) {
                                                        Text(inquiry.status, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                Spacer(Modifier.height(6.dp))
                                                Text("Service: ${inquiry.service}", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                                Text("Contact: ${inquiry.contact}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Spacer(Modifier.height(4.dp))
                                                Text(inquiry.description, style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    }
                                }

                                if (contactInquiriesList.isNotEmpty() && inquiryFilterStatus == "All") {
                                    item {
                                        Spacer(Modifier.height(16.dp))
                                        Text("General Contacts / Quotes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    }
                                    items(contactInquiriesList) { contact ->
                                        Card(
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(contact["name"]?.toString() ?: "N/A", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                Spacer(Modifier.height(4.dp))
                                                Text("Email: ${contact["email"]?.toString() ?: "N/A"}", fontSize = 13.sp)
                                                Text("Phone: ${contact["phone"]?.toString() ?: "N/A"}", fontSize = 13.sp)
                                                if (contact["company"]?.toString()?.isNotEmpty() == true) {
                                                    Text("Company: ${contact["company"]?.toString()}", fontSize = 13.sp)
                                                }
                                                Spacer(Modifier.height(8.dp))
                                                Text(contact["description"]?.toString() ?: "", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    }
                                }
                            }

                            /* INQUIRIES PAGINATION FOOTER */
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Page ${inquiryCurrentPage + 1} of $totalInquiryPages (${totalInquiries} inquiries)",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilledIconButton(
                                        onClick = { if (inquiryCurrentPage > 0) inquiryCurrentPage-- },
                                        enabled = inquiryCurrentPage > 0,
                                        modifier = Modifier.size(36.dp),
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Icon(Icons.Default.ChevronLeft, null, modifier = Modifier.size(16.dp))
                                    }
                                    FilledIconButton(
                                        onClick = { if (inquiryCurrentPage < totalInquiryPages - 1) inquiryCurrentPage++ },
                                        enabled = inquiryCurrentPage < totalInquiryPages - 1,
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
        }
    }

    /* EDIT INQUIRY STATUS DIALOG */
    editingInquiry?.let { inquiry ->
        AlertDialog(
            onDismissRequest = { editingInquiry = null },
            title = { Text("Update Enquiry Status") },
            text = {
                Column {
                    Text("Select new status for this inquiry:")
                    Spacer(Modifier.height(14.dp))
                    listOf("Pending", "In Progress", "Resolved", "Closed").forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStatus = status }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedStatus == status, onClick = { selectedStatus = status })
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
                            firebaseService.updateInquiryStatus(inquiry.id, selectedStatus)
                            if (inquiry.userId.isNotEmpty()) {
                                val notifId = java.util.UUID.randomUUID().toString()
                                val notification = com.nrikesari.app.model.Notification(
                                    id = notifId,
                                    userId = inquiry.userId,
                                    title = "Inquiry Status Updated",
                                    message = "Your inquiry for '${inquiry.service}' has been updated to '$selectedStatus'.",
                                    type = "inquiry",
                                    clickAction = "my_projects",
                                    isAdminAlert = false
                                )
                                firebaseService.saveNotification(notification)
                            }
                            editingInquiry = null
                            loadData()
                        }
                    }
                ) {
                    Text("Update", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingInquiry = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatListItem(
    inquiry: ProjectInquiry,
    navController: NavController,
    onUnreadUpdate: (Int) -> Unit,
    onLastMessageTimeUpdate: (Long) -> Unit
) {
    val firestore = remember { FirebaseFirestore.getInstance() }
    var lastMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var unreadCount by remember { mutableStateOf(0) }
    var userProfile by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(inquiry.id, inquiry.userId) {
        // Retrieve User profile details
        if (inquiry.userId.isNotEmpty()) {
            firestore.collection("users").document(inquiry.userId).get()
                .addOnSuccessListener { snap ->
                    if (snap.exists()) {
                        userProfile = snap.toObject(User::class.java)
                    }
                }
        }

        // Live Chat Messages updates for Last message & Unread Count
        firestore.collection("projects")
            .document(inquiry.id)
            .collection("chats")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    val messagesList = snap.toObjects(ChatMessage::class.java)
                    lastMessage = messagesList.lastOrNull()
                    lastMessage?.let {
                        onLastMessageTimeUpdate(it.timestamp)
                    }
                    
                    // Count unread: messages from sender (which is user) that are not read
                    // Wait, admin needs to read user's messages.
                    // If senderId == userId, it's a message from user.
                    val userUnread = messagesList.count { it.senderId == inquiry.userId && !it.isRead }
                    unreadCount = userUnread
                    onUnreadUpdate(userUnread)
                }
            }
    }

    val displayMessage = when {
        lastMessage == null -> inquiry.description
        lastMessage?.attachmentUrl?.isNotEmpty() == true -> "Sent an attachment"
        else -> lastMessage?.text ?: ""
    }

    val displayTime = if (lastMessage != null) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(lastMessage!!.timestamp))
    } else {
        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
        sdf.format(Date(inquiry.submittedAt))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = if (unreadCount > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("chat/${inquiry.id}")
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image or initials avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                if (userProfile?.profileImageUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = userProfile?.profileImageUrl,
                        contentDescription = "Profile Pic",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = (userProfile?.name ?: inquiry.name).take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userProfile?.displayName ?: inquiry.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = displayTime,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Project: ${inquiry.service}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = displayMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Unread Count Badge
            if (unreadCount > 0) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
