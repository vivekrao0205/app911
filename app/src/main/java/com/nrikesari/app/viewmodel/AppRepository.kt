package com.nrikesari.app.viewmodel

import com.nrikesari.app.model.AppDao
import com.nrikesari.app.model.BlogPost
import com.nrikesari.app.model.PortfolioProject
import com.nrikesari.app.model.Service
import com.nrikesari.app.model.Skill
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val allProjects: Flow<List<PortfolioProject>> = appDao.getAllProjects()
    val allServices: Flow<List<Service>> = appDao.getAllServices()
    val allSkills: Flow<List<Skill>> = appDao.getAllSkills()
    val allBlogPosts: Flow<List<BlogPost>> = appDao.getAllBlogPosts()

    fun searchProjects(query: String): Flow<List<PortfolioProject>> {
        return appDao.searchProjects(query)
    }

    suspend fun insertInitialData(
        projects: List<PortfolioProject>,
        services: List<Service>,
        skills: List<Skill>,
        posts: List<BlogPost>
    ) {
        appDao.insertProjects(projects)
        appDao.insertServices(services)
        appDao.insertSkills(skills)
        appDao.insertBlogPosts(posts)
    }
}
