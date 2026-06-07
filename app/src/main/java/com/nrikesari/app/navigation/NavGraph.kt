package com.nrikesari.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.*

import com.nrikesari.app.ui.screens.about.AboutScreen
import com.nrikesari.app.ui.screens.blog.BlogScreen
import com.nrikesari.app.ui.screens.chat.ChatScreen
import com.nrikesari.app.ui.screens.contact.*
import com.nrikesari.app.ui.screens.home.HomeScreen
import com.nrikesari.app.ui.screens.portfolio.*
import com.nrikesari.app.ui.screens.projects.*
import com.nrikesari.app.ui.screens.reviews.*
import com.nrikesari.app.ui.screens.premium.PremiumFeaturesScreen
import com.nrikesari.app.ui.screens.services.*
import com.nrikesari.app.ui.screens.settings.SettingsScreen
import com.nrikesari.app.ui.screens.skills.SkillsScreen
import com.nrikesari.app.ui.screens.splash.SplashScreen
import com.nrikesari.app.ui.screens.auth.*
import com.nrikesari.app.ui.screens.admin.*
import com.nrikesari.app.ui.screens.notifications.NotificationHistoryScreen
import com.google.firebase.auth.FirebaseAuth

import com.nrikesari.app.viewmodel.MainViewModel
import com.nrikesari.app.viewmodel.AuthViewModel
import com.nrikesari.app.viewmodel.UserViewModel

@Composable
fun NrikesariNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val hideBarsRoutes = listOf(
        Screen.Splash.route,
        Screen.Login.route,
        Screen.Signup.route,
        Screen.ProjectEnquiry.route,
        Screen.Chat.route,
        Screen.NotificationHistory.route,
        "admin_dashboard",
        "admin_notifications",
        "admin_users",
        "admin_communications",
        "admin_projects",
        "admin_analytics"
    )

    val showBottomBar = currentRoute !in hideBarsRoutes

    Scaffold(

        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController, currentRoute)
            }
        },

        floatingActionButton = {

            if (showBottomBar) {

                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.ProjectEnquiry.route) {
                            launchSingleTop = true
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = 60.dp)
                ) {

                    Icon(
                        Icons.Default.Add,
                        contentDescription = "New Project"
                    )
                }
            }
        },

        floatingActionButtonPosition = FabPosition.Center

    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Screen.Splash.route) {
                SplashScreen(navController)
            }

            composable(Screen.Home.route) {
                HomeScreen(navController, userViewModel)
            }

            composable(Screen.About.route) {
                AboutScreen(navController)
            }

            composable(Screen.Services.route) {
                ServicesScreen(navController, viewModel)
            }

            composable(
                route = Screen.ServiceDetail.route,
                arguments = listOf(
                    navArgument("serviceId") { type = NavType.StringType }
                )
            ) { entry ->

                val serviceId = entry.arguments?.getString("serviceId")
                val service = serviceId?.let { viewModel.getServiceById(it) }

                if (service != null) {
                    ServiceDetailScreen(navController, service)
                }
            }

            composable(Screen.Portfolio.route) {
                PortfolioScreen(navController, viewModel)
            }

            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(
                    navArgument("projectId") { type = NavType.StringType }
                )
            ) { entry ->
                val projectId = entry.arguments?.getString("projectId") ?: ""
                val projects by viewModel.dynamicProjects.collectAsState()
                val project = projects.find { it.id == projectId }

                if (project != null) {
                    ProjectDetailScreen(navController, project, authViewModel)
                } else if (projects.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Project not found")
                    }
                }
            }

            composable(Screen.Contact.route) {
                ContactScreen(navController, authViewModel)
            }

            composable(Screen.Skills.route) {
                SkillsScreen(navController)
            }

            composable(Screen.Blog.route) {
                BlogScreen(navController, viewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(navController, authViewModel)
            }

            composable(Screen.Premium.route) {
                PremiumFeaturesScreen(navController)
            }

            composable(Screen.NotificationHistory.route) {
                NotificationHistoryScreen(navController)
            }

            composable(Screen.Projects.route) {
                ProjectsScreen(navController)
            }

            composable(Screen.ProjectEnquiry.route) {
                ProjectEnquiryScreen(authViewModel, userViewModel)
            }

            composable(Screen.MyProjects.route) {
                MyProjectsScreen(navController, authViewModel, userViewModel)
            }

            composable(Screen.WriteReview.route) {
                WriteReviewScreen(navController, authViewModel, userViewModel)
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(
                    navArgument("projectId") { type = NavType.StringType }
                )
            ) { entry ->

                val projectId = entry.arguments?.getString("projectId") ?: ""

                ChatScreen(
                    navController = navController,
                    projectId = projectId
                )
            }

            composable(Screen.BookCall.route) {
                BookCallScreen(navController)
            }

            composable(Screen.Login.route) {
                LoginScreen(navController, authViewModel)
            }

            composable(Screen.Signup.route) {
                SignupScreen(navController)
            }

            /* -------- ADMIN DASHBOARD & SECURE PANELS -------- */

            composable("admin_dashboard") {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email == "vivekrao9505@gmail.com" || email == "anileshwar7@gmail.com") {
                    AdminDashboardScreen(navController)
                } else {
                    AccessDeniedScreen(navController)
                }
            }

            composable("admin_notifications") {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email == "vivekrao9505@gmail.com" || email == "anileshwar7@gmail.com") {
                    AdminNotificationCenterScreen(navController)
                } else {
                    AccessDeniedScreen(navController)
                }
            }

            composable("admin_users") {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email == "vivekrao9505@gmail.com" || email == "anileshwar7@gmail.com") {
                    AdminUserManagementScreen(navController)
                } else {
                    AccessDeniedScreen(navController)
                }
            }

            composable("admin_communications") {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email == "vivekrao9505@gmail.com" || email == "anileshwar7@gmail.com") {
                    AdminCommunicationManagementScreen(navController)
                } else {
                    AccessDeniedScreen(navController)
                }
            }

            composable("admin_projects") {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email == "vivekrao9505@gmail.com" || email == "anileshwar7@gmail.com") {
                    AdminProjectManagementScreen(navController)
                } else {
                    AccessDeniedScreen(navController)
                }
            }

            composable("admin_analytics") {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email == "vivekrao9505@gmail.com" || email == "anileshwar7@gmail.com") {
                    AdminAnalyticsScreen(navController)
                } else {
                    AccessDeniedScreen(navController)
                }
            }
        }
    }


}

/* ---------------- BOTTOM BAR ---------------- */

@Composable
fun BottomBar(
    navController: NavHostController,
    currentRoute: String?
) {


    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("Projects", Icons.Default.Workspaces, Screen.Projects.route),
        BottomNavItem("Contact", Icons.Default.SupportAgent, Screen.Contact.route),
        BottomNavItem("About", Icons.Default.AccountCircle, Screen.About.route)
    )

    BottomAppBar {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEachIndexed { index, item ->

                if (index == 2) {
                    Spacer(modifier = Modifier.width(60.dp))
                }

                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {

                        navController.navigate(item.route) {

                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(item.icon, contentDescription = item.label)
                    },
                    label = {
                        Text(item.label)
                    }
                )
            }
        }
    }


}

/* ---------------- MODEL ---------------- */

data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
