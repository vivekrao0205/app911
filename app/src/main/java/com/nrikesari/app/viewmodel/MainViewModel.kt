package com.nrikesari.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nrikesari.app.model.PortfolioProject
import com.nrikesari.app.model.Service
import com.nrikesari.app.model.TeamMember
import com.nrikesari.app.model.Skill
import com.nrikesari.app.model.BlogPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    // Using StateFlow to expose data from Room to the UI
    val services: StateFlow<List<Service>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolio: StateFlow<List<PortfolioProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val skills: StateFlow<List<Skill>> = repository.allSkills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blogPosts: StateFlow<List<BlogPost>> = repository.allBlogPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _team = MutableStateFlow<List<TeamMember>>(emptyList())
    val team: StateFlow<List<TeamMember>> = _team.asStateFlow()

    init {
        loadTeamData()
        initializeDatabase()
    }

    private fun loadTeamData() {
        _team.value = MockDataRepository.getTeam()
    }

    private fun initializeDatabase() {
        viewModelScope.launch {
            // Check if DB is empty, then insert mock data as initial data
            val currentProjects = repository.allProjects.firstOrNull()
            if (currentProjects.isNullOrEmpty()) {
                repository.insertInitialData(
                    projects = MockDataRepository.getPortfolio(),
                    services = MockDataRepository.getServices(),
                    skills = listOf(
                        Skill("1", "Kotlin", 90, ""),
                        Skill("2", "Jetpack Compose", 85, ""),
                        Skill("3", "Room Database", 80, ""),
                        Skill("4", "Retrofit", 85, "")
                    ),
                    posts = listOf(
                        BlogPost("1", "Modern Android Architecture", "Exploring MVVM and Compose", "Venkata", "Mar 10, 2026", "Full article content...", "")
                    )
                )
            }
        }
    }

    fun getServiceById(id: String): Service? {
        // Find in current flow value
        return services.value.find { it.id == id }
    }
    
    fun getProjectById(id: String): PortfolioProject? {
        return portfolio.value.find { it.id == id }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
