package com.nrikesari.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class Service(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconRes: Int? = null,
    val benefits: List<String> = emptyList(),
    val whyChooseUs: String = ""
)

@Entity(tableName = "projects")
data class PortfolioProject(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val shortDescription: String,
    val resultsAchieved: String,
    val imageUrl: String = "",
    val techStack: List<String> = emptyList() // Added for detailed view
)

@Entity(tableName = "team_members")
data class TeamMember(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val imageUrl: String = ""
)

@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey val id: String,
    val name: String,
    val proficiency: Int, // 0 to 100
    val iconUrl: String = ""
)

@Entity(tableName = "blog_posts")
data class BlogPost(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val author: String,
    val date: String,
    val content: String,
    val imageUrl: String = ""
)
