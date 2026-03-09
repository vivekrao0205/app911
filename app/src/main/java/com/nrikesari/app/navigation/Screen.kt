package com.nrikesari.app.navigation

sealed class Screen(val route: String) {

    /* -------- Core -------- */

    object Splash : Screen("splash")
    object Home : Screen("home")

    /* -------- Main Pages -------- */

    object About : Screen("about")
    object Services : Screen("services")
    object Projects : Screen("projects")
    object Portfolio : Screen("portfolio")
    object Blog : Screen("blog")
    object Skills : Screen("skills")
    object Contact : Screen("contact")
    object Settings : Screen("settings")
    object Premium : Screen("premium")

    /* -------- Auth -------- */

    object Login : Screen("login")
    object Signup : Screen("signup")

    /* -------- Project -------- */

    object ProjectEnquiry : Screen("project_enquiry")
    object MyProjects : Screen("my_projects")
    object BookCall : Screen("book_call")
    object WriteReview : Screen("write_review")

    object ProjectDetail : Screen("project_detail/{projectId}") {

        const val ARG_PROJECT_ID = "projectId"

        fun createRoute(projectId: String): String {
            return "project_detail/$projectId"
        }
    }

    object Chat : Screen("chat/{projectId}") {

        const val ARG_PROJECT_ID = "projectId"

        fun createRoute(projectId: String): String {
            return "chat/$projectId"
        }
    }

    /* -------- Services -------- */

    object ServiceDetail : Screen("service_detail/{serviceId}") {

        const val ARG_SERVICE_ID = "serviceId"

        fun createRoute(serviceId: String): String {
            return "service_detail/$serviceId"
        }
    }
}