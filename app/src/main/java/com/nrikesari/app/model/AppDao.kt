package com.nrikesari.app.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM projects")
    fun getAllProjects(): Flow<List<PortfolioProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<PortfolioProject>)

    @Query("SELECT * FROM projects WHERE category = :category")
    fun getProjectsByCategory(category: String): Flow<List<PortfolioProject>>

    @Query("SELECT * FROM projects WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchProjects(query: String): Flow<List<PortfolioProject>>

    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<Service>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<Service>)

    @Query("SELECT * FROM skills")
    fun getAllSkills(): Flow<List<Skill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<Skill>)
    
    @Query("SELECT * FROM blog_posts")
    fun getAllBlogPosts(): Flow<List<BlogPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlogPosts(posts: List<BlogPost>)
}
