package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.DynamicProject
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProjectManagementScreen(navController: NavController) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var projectsList by remember { mutableStateOf<List<DynamicProject>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Form editing state
    var isFormOpen by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<DynamicProject?>(null) }

    // Form fields
    var title by remember { mutableStateOf("") }
    var shortDesc by remember { mutableStateOf("") }
    var fullDesc by remember { mutableStateOf("") }
    var coverImg by remember { mutableStateOf("") }
    var galleryStr by remember { mutableStateOf("") } // comma separated
    var techStr by remember { mutableStateOf("") } // comma separated
    var category by remember { mutableStateOf("") }
    var completionDate by remember { mutableStateOf("") }
    var projectUrl by remember { mutableStateOf("") }
    var githubUrl by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Completed") }
    var isPublished by remember { mutableStateOf(true) }

    fun loadProjects() {
        isLoading = true
        coroutineScope.launch {
            val result = firebaseService.getDynamicProjects(includeUnpublished = true)
            if (result.isSuccess) {
                projectsList = result.getOrDefault(emptyList())
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadProjects()
    }

    fun openForm(project: DynamicProject?) {
        editingProject = project
        title = project?.title ?: ""
        shortDesc = project?.shortDescription ?: ""
        fullDesc = project?.fullDescription ?: ""
        coverImg = project?.coverImage ?: ""
        galleryStr = project?.galleryImages?.joinToString(", ") ?: ""
        techStr = project?.technologiesUsed?.joinToString(", ") ?: ""
        category = project?.category ?: ""
        completionDate = project?.completionDate ?: ""
        projectUrl = project?.projectUrl ?: ""
        githubUrl = project?.gitHubUrl ?: ""
        clientName = project?.clientName ?: ""
        status = project?.status ?: "Completed"
        isPublished = project?.isPublished ?: true
        isFormOpen = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portfolio Manager", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { openForm(null) }) {
                        Icon(Icons.Default.Add, "Add Project")
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Manage Portfolio Items",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "Admins can create, edit, delete and publish dynamic portfolio projects.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                if (projectsList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No projects in database. Click '+' to add one.")
                        }
                    }
                } else {
                    items(projectsList, key = { it.id }) { project ->
                        AdminProjectItem(
                            project = project,
                            onEdit = { openForm(project) },
                            onTogglePublish = {
                                coroutineScope.launch {
                                    val updated = project.copy(isPublished = !project.isPublished)
                                    firebaseService.saveDynamicProject(updated)
                                    if (updated.isPublished) {
                                        firebaseService.broadcastProjectNotification(updated)
                                    }
                                    loadProjects()
                                }
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    firebaseService.deleteDynamicProject(project.id)
                                    loadProjects()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    /* PROJECT FORM SHEET */
    if (isFormOpen) {
        ModalBottomSheet(
            onDismissRequest = { isFormOpen = false },
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
                    text = if (editingProject == null) "Create Project" else "Edit Project",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (e.g. Web Dev, App Dev)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = shortDesc, onValueChange = { shortDesc = it }, label = { Text("Short Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fullDesc, onValueChange = { fullDesc = it }, label = { Text("Full Description (Optional)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(value = coverImg, onValueChange = { coverImg = it }, label = { Text("Cover Image URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = galleryStr, onValueChange = { galleryStr = it }, label = { Text("Gallery Image URLs (comma separated)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = techStr, onValueChange = { techStr = it }, label = { Text("Technologies Used (comma separated)") }, modifier = Modifier.fillMaxWidth())
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = completionDate, onValueChange = { completionDate = it }, label = { Text("Completion Date") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Client Name") }, modifier = Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = projectUrl, onValueChange = { projectUrl = it }, label = { Text("Project Live URL") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = githubUrl, onValueChange = { githubUrl = it }, label = { Text("GitHub Repo URL") }, modifier = Modifier.weight(1f))
                }

                /* STATUS dropdown */
                Text("Project Status", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Ongoing", "Completed", "Upcoming").forEach { stat ->
                        val isSel = status == stat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { status = stat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(stat, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                /* PUBLISHED TOGGLE */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Publish Project?", fontWeight = FontWeight.SemiBold)
                    Switch(checked = isPublished, onCheckedChange = { isPublished = it })
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (title.isBlank()) return@Button
                        
                        val listGallery = galleryStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val listTech = techStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        
                        val newProj = DynamicProject(
                            id = editingProject?.id ?: UUID.randomUUID().toString(),
                            title = title.trim(),
                            shortDescription = shortDesc.trim(),
                            fullDescription = fullDesc.trim(),
                            coverImage = coverImg.trim(),
                            galleryImages = listGallery,
                            technologiesUsed = listTech,
                            category = category.trim(),
                            completionDate = completionDate.trim(),
                            projectUrl = projectUrl.trim(),
                            gitHubUrl = githubUrl.trim(),
                            clientName = clientName.trim(),
                            status = status,
                            isPublished = isPublished,
                            createdAt = editingProject?.createdAt ?: System.currentTimeMillis()
                        )

                        coroutineScope.launch {
                            firebaseService.saveDynamicProject(newProj)
                            if (newProj.isPublished) {
                                firebaseService.broadcastProjectNotification(newProj)
                            }
                            isFormOpen = false
                            loadProjects()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Project", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminProjectItem(
    project: DynamicProject,
    onEdit: () -> Unit,
    onTogglePublish: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (project.isPublished) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.errorContainer
                        )
                        .clickable { onTogglePublish() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (project.isPublished) "PUBLISHED" else "UNPUBLISHED",
                        color = if (project.isPublished) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(6.dp))
            Text(project.category, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(project.shortDescription, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
