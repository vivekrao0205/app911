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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.ChatMessage
import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.navigation.Screen
import kotlinx.coroutines.launch

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

    val filteredInquiries = remember(inquiriesList, searchQuery) {
        inquiriesList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.service.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        }
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
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

            Spacer(Modifier.height(14.dp))

            /* SEARCH */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search chats or inquiries...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (activeTab == 0) {
                    /* CHATS LIST - ADMIN CAN CHAT ABOUT SPECIFIC INQUIRIES */
                    if (filteredInquiries.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text("No active project chats found.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredInquiries, key = { it.id }) { inquiry ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        navController.navigate("chat/${inquiry.id}")
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Chat, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(inquiry.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("Project: ${inquiry.service}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(Modifier.height(4.dp))
                                            Text(inquiry.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1)
                                        }
                                        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    /* INQUIRIES & CONTACT FORMS LIST */
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (filteredInquiries.isNotEmpty()) {
                            item {
                                Text("Project Enquiries", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }
                            items(filteredInquiries, key = { "inq_${it.id}" }) { inquiry ->
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

                        if (contactInquiriesList.isNotEmpty()) {
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
