package com.nrikesari.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object About : Screen("about_screen")
    object Services : Screen("services_screen")
    object ServiceDetail : Screen("service_detail_screen/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail_screen/$serviceId"
    }
    object Portfolio : Screen("portfolio_screen")
    object Contact : Screen("contact_screen")
    object Premium : Screen("premium_screen")
}
