package com.nrikesari.app.ui.screens.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

    val currentUserProfile by authViewModel.currentUserProfile.collectAsState()
    val projectsState by userViewModel.projectsState.collectAsState()

    /* Fetch user inquiries from Firebase */

    LaunchedEffect(currentUserProfile?.uid) {
        currentUserProfile?.uid?.let {
            userViewModel.fetchUserProjects(it)
        }
    }

    val isLoading = projectsState is UserProjectsState.Loading
    val projects = (projectsState as? UserProjectsState.Success)?.projects ?: emptyList()

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text(
                        "My Discussions",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(onClick = { navController.popBackStack() }) {

                        Icon(Icons.Default.ArrowBack, null)
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {

            when {

                /* Loading */

                isLoading -> {

                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                /* Empty state */

                projects.isEmpty() -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "No discussions yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }

                /* Show inquiries */

                else -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        item { Spacer(Modifier.height(8.dp)) }

                        items(projects) { project ->

                            ProjectStatusCard(
                                project = project,
                                onChatClick = {

                                    navController.navigate("chat/${project.id}")
                                }
                            )
                        }

                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

/* ---------------- PROJECT CARD ---------------- */

@Composable
fun ProjectStatusCard(
    project: ProjectInquiry,
    onChatClick: () -> Unit
) {

    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(project.submittedAt))

    Surface(

        shape = RoundedCornerShape(16.dp),

        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),

        color = MaterialTheme.colorScheme.surface,

        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            /* Header */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = project.service,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {

                    Text(
                        text = project.status,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            /* Description */

            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(14.dp))

            /* Footer */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Submitted",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = onChatClick,
                    shape = RoundedCornerShape(22.dp)
                ) {

                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text("Discussion")
                }
            }
        }
    }
}