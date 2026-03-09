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
import com.nrikesari.app.ui.screens.contact.ContactScreen
import com.nrikesari.app.ui.screens.home.HomeScreen
import com.nrikesari.app.ui.screens.portfolio.PortfolioScreen
import com.nrikesari.app.ui.screens.portfolio.ProjectDetailScreen
import com.nrikesari.app.ui.screens.projects.ProjectEnquiryScreen
import com.nrikesari.app.ui.screens.projects.ProjectsScreen
import com.nrikesari.app.ui.screens.premium.PremiumFeaturesScreen
import com.nrikesari.app.ui.screens.services.ServiceDetailScreen
import com.nrikesari.app.ui.screens.services.ServicesScreen
import com.nrikesari.app.ui.screens.settings.SettingsScreen
import com.nrikesari.app.ui.screens.skills.SkillsScreen
import com.nrikesari.app.ui.screens.splash.SplashScreen
import com.nrikesari.app.viewmodel.MainViewModel

@Composable
fun NrikesariNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val hideBarsRoutes = listOf(
        Screen.Splash.route,
        Screen.ProjectEnquiry.route,
        Screen.ServiceDetail.route,
        Screen.ProjectDetail.route
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
                    onClick = { navController.navigate(Screen.ProjectEnquiry.route) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(60.dp)
                        .offset(y = 65.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Project")
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
                HomeScreen(navController)
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

                val id = entry.arguments?.getString("serviceId")
                val service = id?.let { viewModel.getServiceById(it) }

                service?.let {
                    ServiceDetailScreen(navController, it)
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

                val id = entry.arguments?.getString("projectId")
                val project = id?.let { viewModel.getProjectById(it) }

                project?.let {
                    ProjectDetailScreen(navController, it)
                }
            }

            composable(Screen.Contact.route) {
                ContactScreen(navController)
            }

            composable(Screen.Skills.route) {
                SkillsScreen(navController)
            }

            composable(Screen.Blog.route) {
                BlogScreen(navController, viewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }

            composable(Screen.Premium.route) {
                PremiumFeaturesScreen(navController)
            }

            composable(Screen.Projects.route) {
                ProjectsScreen()
            }

            composable(Screen.ProjectEnquiry.route) {
                ProjectEnquiryScreen()
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    currentRoute: String?
) {

    val items = remember {
        listOf(
            BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
            BottomNavItem("Projects", Icons.Default.Workspaces, Screen.Projects.route),
            BottomNavItem("Contact", Icons.Default.SupportAgent, Screen.Contact.route),
            BottomNavItem("About", Icons.Default.AccountCircle, Screen.About.route)
        )
    }

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEachIndexed { index, item ->

                if (index == 2) {
                    Spacer(modifier = Modifier.width(56.dp))
                }

                val selected = currentRoute == item.route

                NavigationBarItem(
                    selected = selected,
                    onClick = {

                        if (currentRoute != item.route) {

                            navController.navigate(item.route) {

                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
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

data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)