package com.nrikesari.app.ui.screens.projects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.Booking
import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.viewmodel.AuthViewModel
import com.nrikesari.app.viewmodel.UserProjectsState
import com.nrikesari.app.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProjectsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {


    val firebaseService = remember { FirebaseService() }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val projectsState by userViewModel.projectsState.collectAsState()

    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }

    LaunchedEffect(currentUser?.uid) {

        currentUser?.uid?.let { uid ->

            userViewModel.fetchUserProjects(uid)

            val bookingResult = firebaseService.getUserBookings(uid)

            if (bookingResult.isSuccess) {
                bookings = bookingResult.getOrDefault(emptyList())
            }
        }
    }

    val isLoading = projectsState is UserProjectsState.Loading
    val projects = (projectsState as? UserProjectsState.Success)?.projects ?: emptyList()

    Scaffold(

        topBar = {

            TopAppBar(
                title = { Text("My Dashboard", fontWeight = FontWeight.Bold) },

                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {

            when {

                isLoading -> {

                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                projects.isEmpty() && bookings.isEmpty() -> {

                    EmptyState()
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {

                        item { Spacer(Modifier.height(10.dp)) }

                        /* -------- HEADER CARD -------- */

                        item {

                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
                                )
                            ) {

                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {

                                    Text(
                                        "Welcome back",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        "Track your projects and consultations here",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        /* -------- QUICK STATS -------- */

                        item {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                StatCard(
                                    icon = Icons.Default.Work,
                                    label = "Projects",
                                    value = projects.size.toString()
                                )

                                StatCard(
                                    icon = Icons.Default.Schedule,
                                    label = "Calls",
                                    value = bookings.size.toString()
                                )
                            }
                        }

                        /* -------- PROJECT DISCUSSIONS -------- */

                        if (projects.isNotEmpty()) {

                            item {
                                SectionHeader(
                                    icon = Icons.Default.Chat,
                                    title = "Project Discussions"
                                )
                            }

                            items(projects) { project ->

                                AnimatedVisibility(visible = true, enter = fadeIn()) {

                                    ProjectStatusCard(
                                        project = project,
                                        onChatClick = {
                                            navController.navigate("chat/${project.id}")
                                        }
                                    )
                                }
                            }
                        }

                        /* -------- CONSULTATION CALLS -------- */

                        if (bookings.isNotEmpty()) {

                            item {
                                SectionHeader(
                                    icon = Icons.Default.Schedule,
                                    title = "Consultation Calls"
                                )
                            }

                            items(bookings) { booking ->
                                BookingCard(booking)
                            }
                        }

                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }


}

/* -------- STAT CARD -------- */

@Composable
fun StatCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {


    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(6.dp))

            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }


}

/* -------- SECTION HEADER -------- */

@Composable
fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {


    Row(verticalAlignment = Alignment.CenterVertically) {

        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.width(8.dp))

        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }


}

/* -------- EMPTY STATE -------- */

@Composable
fun EmptyState() {


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            Icons.Default.Info,
            null,
            modifier = Modifier.size(60.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )

        Spacer(Modifier.height(14.dp))

        Text(
            "No activity yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            "Your project discussions and bookings will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }


}

/* -------- PROJECT CARD -------- */

@Composable
fun ProjectStatusCard(project: ProjectInquiry, onChatClick: () -> Unit) {


    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(project.submittedAt))

    Card(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {

        Column(modifier = Modifier.padding(18.dp)) {

            Text(project.service, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))

            Text(project.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                AssistChip(onClick = {}, label = { Text(dateStr) })

                Button(onClick = onChatClick, shape = RoundedCornerShape(22.dp)) {

                    Icon(Icons.Default.Chat, null)

                    Spacer(Modifier.width(6.dp))

                    Text("Discussion")
                }
            }
        }
    }


}

/* -------- BOOKING CARD -------- */

@Composable
fun BookingCard(
    booking: Booking
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Consultation Call",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Date: ${booking.date}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Time: ${booking.timeSlot}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (booking.notes.isNotEmpty()) {

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    booking.notes,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AssistChip(
                onClick = {},
                label = {
                    Text(booking.status ?: "Pending")
                }
            )
        }
    }


}
