package com.nrikesari.app.model

data class Service(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int? = null,
    val benefits: List<String> = emptyList(),
    val whyChooseUs: String = ""
)

data class PortfolioProject(
    val id: String,
    val title: String,
    val category: String,
    val shortDescription: String,
    val resultsAchieved: String,
    val imageUrl: String = ""
)

data class TeamMember(
    val id: String,
    val name: String,
    val role: String,
    val imageUrl: String = ""
)
