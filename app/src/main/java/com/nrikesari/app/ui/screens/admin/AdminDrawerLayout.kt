package com.nrikesari.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

data class DrawerItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun AdminDrawerLayout(
    navController: NavController,
    currentRoute: String,
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        DrawerItem("Dashboard", Icons.Default.Dashboard, "admin_dashboard"),
        DrawerItem("Projects", Icons.Default.Work, "admin_projects"),
        DrawerItem("Project Inquiries", Icons.Default.Assignment, "admin_communications?tab=inquiries&filter=all"),
        DrawerItem("Start Project Requests", Icons.Default.TrendingUp, "admin_communications?tab=inquiries&filter=project"),
        DrawerItem("Users", Icons.Default.People, "admin_users"),
        DrawerItem("Notifications", Icons.Default.Notifications, "admin_notifications"),
        DrawerItem("Messages", Icons.Default.Chat, "admin_communications?tab=chats"),
        DrawerItem("Analytics", Icons.Default.InsertChart, "admin_analytics"),
        DrawerItem("Settings", Icons.Default.Settings, "settings"),
        DrawerItem("Client App", Icons.Default.Home, "home")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                // Sidebar Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "N",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "NRIKESARI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Admin Portal",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                // Navigation Items
                drawerItems.forEach { item ->
                    val isSelected = currentRoute == item.route || 
                                     (currentRoute.startsWith("admin_communications") && item.route.startsWith("admin_communications") && currentRoute == item.route)
                    
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        selected = isSelected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                if (item.route == "home") {
                                    popUpTo(0) { inclusive = true }
                                } else if (item.route == "settings") {
                                    launchSingleTop = true
                                } else {
                                    popUpTo("admin_dashboard") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Open Menu")
                        }
                    },
                    actions = actions
                )
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}
