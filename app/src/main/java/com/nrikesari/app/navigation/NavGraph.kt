package com.nrikesari.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

@Composable
fun NrikesariNavGraph(navController: NavHostController) {

    val viewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route   // ✅ Splash first
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
            ServicesScreen(
                navController = navController,
                viewModel = viewModel
            )
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

            serviceId?.let {
                viewModel.getServiceById(it)?.let { service ->
                    ServiceDetailScreen(
                        navController = navController,
                        service = service
                    )
                }
            }
        }

        composable(Screen.Portfolio.route) {
            PortfolioScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Screen.Contact.route) {
            ContactScreen(navController)
        }

        composable(Screen.Premium.route) {
            PremiumFeaturesScreen(navController)
        }
    }
}