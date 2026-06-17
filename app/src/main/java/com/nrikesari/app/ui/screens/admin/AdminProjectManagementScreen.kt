package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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

    // Search, Filter, Sort, Pagination States
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") } // All, Published, Unpublished
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var sortBy by remember { mutableStateOf("Newest") } // Newest, Oldest, Title
    var currentPage by remember { mutableStateOf(0) }
    val itemsPerPage = 6

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

    // Reset pagination when search/filters/sorting changes
    LaunchedEffect(searchQuery, selectedStatusFilter, selectedCategoryFilter, sortBy) {
        currentPage = 0
    }

    val categoriesList = remember(projectsList) {
        listOf("All") + projectsList.map { it.category }.distinct().filter { it.isNotBlank() }
    }

    val filteredProjects = remember(projectsList, searchQuery, selectedStatusFilter, selectedCategoryFilter, sortBy) {
        var list = projectsList.filter { proj ->
            val matchesSearch = proj.title.contains(searchQuery, ignoreCase = true) ||
                    proj.category.contains(searchQuery, ignoreCase = true) ||
                    proj.shortDescription.contains(searchQuery, ignoreCase = true)
            
            val matchesStatus = when (selectedStatusFilter) {
                "Published" -> proj.isPublished
                "Unpublished" -> !proj.isPublished
                else -> true
            }

            val matchesCategory = selectedCategoryFilter == "All" || proj.category.equals(selectedCategoryFilter, ignoreCase = true)

            matchesSearch && matchesStatus && matchesCategory
        }

        list = when (sortBy) {
            "Oldest" -> list.sortedBy { it.createdAt }
            "Title" -> list.sortedBy { it.title.lowercase() }
            else -> list.sortedByDescending { it.createdAt } // Newest
        }

        list
    }

    // Pagination calculations
    val totalItems = filteredProjects.size
    val totalPages = maxOf(1, (totalItems + itemsPerPage - 1) / itemsPerPage)
    val paginatedProjects = remember(filteredProjects, currentPage) {
        filteredProjects.drop(currentPage * itemsPerPage).take(itemsPerPage)
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
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
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
                        "Admins can create, edit, delete and publish dynamic portfolio projects. User notifications are no longer automatically sent upon modifications.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                /* SEARCH & FILTER CONTROLS TABLE-LIKE INTERFACE */
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Search field
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search projects...") },
                                leadingIcon = { Icon(Icons.Default.Search, null) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, null)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )

                            Spacer(Modifier.height(12.dp))

                            // Status Filters Row
                            Text("Publication Status", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("All", "Published", "Unpublished").forEach { statusOpt ->
                                    val isSel = selectedStatusFilter == statusOpt
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { selectedStatusFilter = statusOpt },
                                        label = { Text(statusOpt) }
                                    )
                                }
                            }

                            // Category Filters Row
                            if (categoriesList.size > 1) {
                                Text("Category Filter", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    categoriesList.forEach { catOpt ->
                                        val isSel = selectedCategoryFilter == catOpt
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedCategoryFilter = catOpt },
                                            label = { Text(catOpt) }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Sorting Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Sort Order", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            imageVector = when (sortBy) {
                                                "Title" -> Icons.Default.SortByAlpha
                                                "Oldest" -> Icons.Default.ArrowUpward
                                                else -> Icons.Default.ArrowDownward
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (paginatedProjects.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No projects match the criteria. Click '+' to add one.")
                        }
                    }
                } else {
                    items(paginatedProjects, key = { it.id }) { project ->
                        AdminProjectItem(
                            project = project,
                            onEdit = { openForm(project) },
                            onTogglePublish = {
                                coroutineScope.launch {
                                    val updated = project.copy(isPublished = !project.isPublished)
                                    firebaseService.saveDynamicProject(updated)
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

                    /* PAGINATION FOOTER CONTROL */
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Page ${currentPage + 1} of $totalPages (${totalItems} items total)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledIconButton(
                                    onClick = { if (currentPage > 0) currentPage-- },
                                    enabled = currentPage > 0,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                ) {
                                    Icon(Icons.Default.ChevronLeft, "Previous Page")
                                }

                                FilledIconButton(
                                    onClick = { if (currentPage < totalPages - 1) currentPage++ },
                                    enabled = currentPage < totalPages - 1,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                ) {
                                    Icon(Icons.Default.ChevronRight, "Next Page")
                                }
                            }
                        }
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
