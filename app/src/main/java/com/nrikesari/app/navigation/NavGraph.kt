package com.nrikesari.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.nrikesari.app.ui.screens.about.AboutScreen
import com.nrikesari.app.ui.screens.contact.ContactScreen
import com.nrikesari.app.ui.screens.home.HomeScreen
import com.nrikesari.app.ui.screens.portfolio.PortfolioScreen
import com.nrikesari.app.ui.screens.premium.PremiumFeaturesScreen
import com.nrikesari.app.ui.screens.services.ServiceDetailScreen
import com.nrikesari.app.ui.screens.services.ServicesScreen
import com.nrikesari.app.ui.screens.splash.SplashScreen
import com.nrikesari.app.viewmodel.MainViewModel
import com.nrikesari.app.ui.screens.projects.ProjectEnquiryScreen
import com.nrikesari.app.ui.screens.projects.ProjectsScreen


@Composable
fun NrikesariNavGraph(navController: NavHostController) {

    val viewModel: MainViewModel = viewModel()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.Splash.route

    Scaffold(

        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController)
            }
        },

        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.ProjectEnquiry.route)
                    },
                    containerColor = Color(0xFF8B2C2C),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = 65.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
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
                    navArgument("serviceId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val serviceId =
                    backStackEntry.arguments?.getString("serviceId")

                val service = serviceId?.let {
                    viewModel.getServiceById(it)
                }

                if (service != null) {
                    ServiceDetailScreen(navController, service)
                }
            }

            composable(Screen.Portfolio.route) {
                PortfolioScreen(navController, viewModel)
            }

            composable(Screen.Contact.route) {
                ContactScreen(navController)
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
fun BottomBar(navController: NavHostController) {

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                    }
                }
            ) {
                Icon(Icons.Default.Home, null)
            }

            IconButton(
                onClick = {
                    navController.navigate(Screen.Projects.route) {
                        launchSingleTop = true
                    }
                }
            ) {
                Icon(Icons.Default.Work, null)
            }

            Spacer(modifier = Modifier.width(56.dp)) // space for FAB

            IconButton(
                onClick = {
                    navController.navigate(Screen.Contact.route) {
                        launchSingleTop = true
                    }
                }
            ) {
                Icon(Icons.Default.Mail, null)
            }

            IconButton(
                onClick = {
                    navController.navigate(Screen.About.route) {
                        launchSingleTop = true
                    }
                }
            ) {
                Icon(Icons.Default.Person, null)
            }
        }
    }
}


