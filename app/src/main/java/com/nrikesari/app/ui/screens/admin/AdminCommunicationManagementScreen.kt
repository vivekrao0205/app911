package com.nrikesari.app.ui.screens.admin

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.ChatMessage
import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.model.User
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Unified Inquiry model representing both detailed Start Project requests and General Contact forms
data class UnifiedInquiry(
    val id: String,
    val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val company: String,
    val service: String,
    val projectType: String,
    val budget: String,
    val timeline: String,
    val description: String,
    val status: String, // "New", "Contacted", "In Progress", "Completed", "Archived"
    val submittedAt: Long,
    val isProjectInquiry: Boolean,
    val fileUrl: String = "",
    val additionalNotes: String = "",
    val goals: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCommunicationManagementScreen(
    navController: NavController,
    initialTab: String = "chats",
    initialFilter: String = "all"
) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(if (initialTab == "inquiries") 1 else 0) } // 0 = Chat Messages, 1 = CRM Inquiries

    var inquiriesList by remember { mutableStateOf<List<ProjectInquiry>>(emptyList()) }
    var contactInquiriesList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Chat filter states
    var chatFilterType by remember { mutableStateOf("All") } // All, Unread, In Progress
    var chatSortBy by remember { mutableStateOf("Newest") } // Newest, Name
    var chatCurrentPage by remember { mutableStateOf(0) }
    val chatsPerPage = 6

    // Inquiry filter states (CRM Statuses)
    var inquiryFilterStatus by remember { mutableStateOf("All") } // All, New, Contacted, In Progress, Completed, Archived
    var inquirySortBy by remember { mutableStateOf("Newest") } // Newest, Oldest, Name
    var inquiryTypeFilter by remember { mutableStateOf(if (initialFilter == "project") "Start Project Wizard" else "All") } // All, Start Project Wizard, General Contacts
    var inquiryCurrentPage by remember { mutableStateOf(0) }
    val inquiriesPerPage = 8

    // Selected Inquiry for details panel
    var selectedInquiryForDetails by remember { mutableStateOf<UnifiedInquiry?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Unread count map to enable parent filtering
    val unreadCounts = remember { mutableStateMapOf<String, Int>() }
    val lastMessageTimes = remember { mutableStateMapOf<String, Long>() }

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

    // Reset pagination on filter updates
    LaunchedEffect(searchQuery, chatFilterType, chatSortBy) {
        chatCurrentPage = 0
    }
    LaunchedEffect(searchQuery, inquiryFilterStatus, inquirySortBy, inquiryTypeFilter) {
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
            "Unread First" -> list.sortedWith(
                compareByDescending<ProjectInquiry> { unreadCounts[it.id] ?: 0 }
                    .thenByDescending { lastMessageTimes[it.id] ?: it.submittedAt }
            )
            else -> list.sortedByDescending { lastMessageTimes[it.id] ?: it.submittedAt }
        }

        list
    }

    val totalChats = filteredChats.size
    val totalChatPages = maxOf(1, (totalChats + chatsPerPage - 1) / chatsPerPage)
    val paginatedChats = remember(filteredChats, chatCurrentPage) {
        filteredChats.drop(chatCurrentPage * chatsPerPage).take(chatsPerPage)
    }

    /* CRM UNIFIED INQUIRIES LIST (Combining ProjectInquiries & General Contacts) */
    val unifiedInquiriesList = remember(inquiriesList, contactInquiriesList) {
        val list = mutableListOf<UnifiedInquiry>()
        
        // Map detailed inquiries
        inquiriesList.forEach {
            // Map old/legacy statuses into clean CRM status
            val cleanStatus = when (it.status) {
                "Inquiry Received", "Pending", "New" -> "New"
                "In Progress" -> "In Progress"
                "Resolved", "Completed" -> "Completed"
                "Closed", "Archived" -> "Archived"
                "Contacted" -> "Contacted"
                else -> "New"
            }
            list.add(
                UnifiedInquiry(
                    id = it.id,
                    userId = it.userId,
                    name = it.name,
                    email = it.email.ifBlank { it.contact },
                    phone = it.phone,
                    company = it.companyName,
                    service = it.service,
                    projectType = it.projectType.ifBlank { "N/A" },
                    budget = it.budgetRange.ifBlank { "Flexible" },
                    timeline = it.timeline.ifBlank { "Flexible" },
                    description = it.description,
                    status = cleanStatus,
                    submittedAt = it.submittedAt,
                    isProjectInquiry = true,
                    fileUrl = it.fileUrl,
                    additionalNotes = it.additionalNotes,
                    goals = it.goals
                )
            )
        }

        // Map general contact inquiries
        contactInquiriesList.forEach {
            val rawStatus = it["status"]?.toString() ?: "New"
            val cleanStatus = when (rawStatus) {
                "Inquiry Received", "Pending", "New" -> "New"
                "In Progress" -> "In Progress"
                "Resolved", "Completed" -> "Completed"
                "Closed", "Archived" -> "Archived"
                "Contacted" -> "Contacted"
                else -> "New"
            }
            val timestamp = (it["timestamp"] as? com.google.firebase.Timestamp)?.toDate()?.time
                ?: it["timestamp"] as? Long 
                ?: System.currentTimeMillis()
                
            list.add(
                UnifiedInquiry(
                    id = it["id"]?.toString() ?: "",
                    userId = it["userId"]?.toString() ?: "",
                    name = it["name"]?.toString() ?: "General Contact",
                    email = it["email"]?.toString() ?: "",
                    phone = it["phone"]?.toString() ?: "",
                    company = it["company"]?.toString() ?: "",
                    service = "General Contact",
                    projectType = "N/A",
                    budget = "N/A",
                    timeline = "N/A",
                    description = it["description"]?.toString() ?: "",
                    status = cleanStatus,
                    submittedAt = timestamp,
                    isProjectInquiry = false
                )
            )
        }

        list
    }

    val filteredUnifiedInquiries = remember(unifiedInquiriesList, searchQuery, inquiryFilterStatus, inquirySortBy, inquiryTypeFilter) {
        var list = unifiedInquiriesList.filter {
            val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery, ignoreCase = true) ||
                    it.service.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.company.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (inquiryFilterStatus) {
                "All" -> true
                else -> it.status.equals(inquiryFilterStatus, ignoreCase = true)
            }

            val matchesType = when (inquiryTypeFilter) {
                "Start Project Wizard" -> it.isProjectInquiry
                "General Contacts" -> !it.isProjectInquiry
                else -> true
            }

            matchesSearch && matchesStatus && matchesType
        }

        list = when (inquirySortBy) {
            "Name" -> list.sortedBy { it.name.lowercase() }
            "Oldest" -> list.sortedBy { it.submittedAt }
            else -> list.sortedByDescending { it.submittedAt }
        }

        list
    }

    val totalInquiries = filteredUnifiedInquiries.size
    val totalInquiryPages = maxOf(1, (totalInquiries + inquiriesPerPage - 1) / inquiriesPerPage)
    val paginatedInquiries = remember(filteredUnifiedInquiries, inquiryCurrentPage) {
        filteredUnifiedInquiries.drop(inquiryCurrentPage * inquiriesPerPage).take(inquiriesPerPage)
    }

    AdminDrawerLayout(
        navController = navController,
        currentRoute = "admin_communications",
        title = "CRM & Communications"
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
                        Text("Inquiry CRM", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* SEARCH */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (activeTab == 0) "Search active chats..." else "Search CRM by name, email, company, content...") },
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
                                    chatSortBy = when (chatSortBy) {
                                        "Newest" -> "Unread First"
                                        "Unread First" -> "Name"
                                        else -> "Newest"
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (chatSortBy) {
                                        "Name" -> "Name A-Z"
                                        "Unread First" -> "Unread First"
                                        else -> "Recent Chat"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (chatSortBy == "Name") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
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
                    /* INQUIRIES CRM LIST */
                    // Type filter selector (Project Wizard vs General Contacts)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Start Project Wizard", "General Contacts").forEach { typeOpt ->
                            val isSel = inquiryTypeFilter == typeOpt
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { inquiryTypeFilter = typeOpt }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    typeOpt,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Status Filters chip row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("All", "New", "Contacted", "In Progress", "Completed", "Archived").forEach { statusOpt ->
                            val isSel = inquiryFilterStatus == statusOpt
                            FilterChip(
                                selected = isSel,
                                onClick = { inquiryFilterStatus = statusOpt },
                                label = { Text(statusOpt) }
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    inquirySortBy = when (inquirySortBy) {
                                        "Newest" -> "Oldest"
                                        "Oldest" -> "Name"
                                        else -> "Newest"
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (inquirySortBy) {
                                        "Name" -> "Name A-Z"
                                        "Oldest" -> "Oldest First"
                                        else -> "Newest First"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (inquirySortBy == "Oldest") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (paginatedInquiries.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text("No CRM inquiries match search or filters.")
                        }
                    } else {
                        Column(modifier = Modifier.weight(1f)) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(paginatedInquiries, key = { "crm_${it.id}" }) { inquiry ->
                                    CRMInquiryListItem(
                                        inquiry = inquiry,
                                        onClick = { selectedInquiryForDetails = inquiry }
                                    )
                                }
                            }

                            /* CRM PAGINATION FOOTER */
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

    /* FULL-SCREEN CRM INQUIRY DETAILS VIEW DIALOG */
    selectedInquiryForDetails?.let { inquiry ->
        Dialog(
            onDismissRequest = { selectedInquiryForDetails = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    // Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { selectedInquiryForDetails = null }) {
                                Icon(Icons.Default.Close, "Close details")
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "CRM Inquiry Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        // Current CRM Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(getStatusColor(inquiry.status).copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = inquiry.status.uppercase(),
                                color = getStatusColor(inquiry.status),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider()

                    // Scrollable CRM Detail Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Client Info Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Client Contact Info", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                HorizontalDivider()
                                
                                DetailFieldRow("Full Name", inquiry.name)
                                DetailFieldRow("Email Address", inquiry.email.ifBlank { "Not Provided" })
                                DetailFieldRow("Phone Number", inquiry.phone.ifBlank { "Not Provided" })
                                DetailFieldRow("Company Name", inquiry.company.ifBlank { "N/A" })
                            }
                        }

                        // Project Specs Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Assignment, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Project Specifications", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                HorizontalDivider()
                                
                                DetailFieldRow("Selected Service", inquiry.service)
                                DetailFieldRow("Project Type", inquiry.projectType)
                                DetailFieldRow("Budget Range", inquiry.budget)
                                DetailFieldRow("Timeline Request", inquiry.timeline)
                                
                                val sdf = SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault())
                                DetailFieldRow("Submission Date", sdf.format(Date(inquiry.submittedAt)))
                            }
                        }

                        // Project Goals Card (if present)
                        if (inquiry.goals.isNotEmpty()) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Project Goals", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    HorizontalDivider()
                                    Text(inquiry.goals, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        // Requirements (EXACT ORIGINAL MESSAGE - NO TRUNCATION)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Exact Original Message (Requirements)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                HorizontalDivider()
                                Text(
                                    text = inquiry.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        // Additional Notes Card (if present)
                        if (inquiry.additionalNotes.isNotEmpty()) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Additional Notes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    HorizontalDivider()
                                    Text(inquiry.additionalNotes, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        // Attachments Card (if present)
                        if (inquiry.fileUrl.isNotEmpty()) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Attachments", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    HorizontalDivider()
                                    
                                    val isImage = inquiry.fileUrl.contains(".jpg", true) ||
                                                  inquiry.fileUrl.contains(".jpeg", true) ||
                                                  inquiry.fileUrl.contains(".png", true) ||
                                                  inquiry.fileUrl.contains(".webp", true)
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Icon(Icons.Default.AttachFile, null, tint = MaterialTheme.colorScheme.primary)
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = "Attached Asset Reference",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        val context = LocalContext.current
                                        Button(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquiry.fileUrl))
                                                context.startActivity(intent)
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Download")
                                        }
                                    }

                                    if (isImage) {
                                        Spacer(Modifier.height(10.dp))
                                        AsyncImage(
                                            model = inquiry.fileUrl,
                                            contentDescription = "Preview Image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sticky Bottom CRM Actions Row
                    Surface(
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("CRM Status Actions", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("New", "Contacted", "In Progress", "Completed", "Archived").forEach { statusName ->
                                    val isCurrent = inquiry.status == statusName
                                    FilterChip(
                                        selected = isCurrent,
                                        onClick = {
                                            coroutineScope.launch {
                                                val result = if (inquiry.isProjectInquiry) {
                                                    firebaseService.updateInquiryStatus(inquiry.id, statusName)
                                                } else {
                                                    firebaseService.updateContactInquiryStatus(inquiry.id, statusName)
                                                }
                                                
                                                if (result.isSuccess) {
                                                    if (inquiry.userId.isNotEmpty()) {
                                                        val notifId = java.util.UUID.randomUUID().toString()
                                                        val notification = com.nrikesari.app.model.Notification(
                                                            id = notifId,
                                                            userId = inquiry.userId,
                                                            title = "Inquiry Status Updated",
                                                            message = "Your inquiry for '${inquiry.service}' has been updated to '$statusName'.",
                                                            type = "inquiry",
                                                            clickAction = "my_projects",
                                                            isAdminAlert = false
                                                        )
                                                        firebaseService.saveNotification(notification)
                                                    }
                                                    
                                                    // Refresh detail overlay & CRM list
                                                    selectedInquiryForDetails = inquiry.copy(status = statusName)
                                                    loadData()
                                                }
                                            }
                                        },
                                        label = { Text("Mark $statusName") }
                                    )
                                }
                            }
                            
                            Button(
                                onClick = { showDeleteConfirm = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Delete Inquiry", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    /* DELETE INQUIRY CONFIRMATION DIALOG */
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Inquiry Profile?") },
            text = { Text("Are you sure you want to permanently delete this inquiry record? This action is irreversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        val targetInq = selectedInquiryForDetails
                        if (targetInq != null) {
                            coroutineScope.launch {
                                val res = if (targetInq.isProjectInquiry) {
                                    firebaseService.deleteInquiry(targetInq.id)
                                } else {
                                    firebaseService.deleteContactInquiry(targetInq.id)
                                }
                                if (res.isSuccess) {
                                    selectedInquiryForDetails = null
                                    loadData()
                                }
                            }
                        }
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

@Composable
fun CRMInquiryListItem(
    inquiry: UnifiedInquiry,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    inquiry.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Color-coded CRM status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(getStatusColor(inquiry.status).copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = inquiry.status,
                        color = getStatusColor(inquiry.status),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Service Styled Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = inquiry.service,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            // Client Info Fields
            if (inquiry.company.isNotEmpty()) {
                Text(
                    text = "Company: ${inquiry.company}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (inquiry.email.isNotEmpty() || inquiry.phone.isNotEmpty()) {
                Text(
                    text = "Email: ${inquiry.email.ifBlank { "N/A" }} | Phone: ${inquiry.phone.ifBlank { "N/A" }}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Specs Fields
            if (inquiry.isProjectInquiry) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Type: ${inquiry.projectType} | Budget: ${inquiry.budget} | Timeline: ${inquiry.timeline}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // Truncated preview of description
            Text(
                text = inquiry.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (inquiry.fileUrl.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AttachFile,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Attachment Uploaded",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun DetailFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "new", "pending" -> Color(0xFF0066CC) // Professional Blue
        "contacted" -> Color(0xFFE28B00) // Amber / Orange
        "in progress" -> Color(0xFF8E24AA) // Purple
        "completed", "resolved" -> Color(0xFF2E7D32) // Forest Green
        else -> Color(0xFF757575) // Dark Gray (Archived/Closed)
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
                        text = userProfile?.name ?: inquiry.name,
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
