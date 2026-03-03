package com.nrikesari.app.viewmodel

import androidx.lifecycle.ViewModel
import com.nrikesari.app.model.PortfolioProject
import com.nrikesari.app.model.Service
import com.nrikesari.app.model.TeamMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()

    private val _portfolio = MutableStateFlow<List<PortfolioProject>>(emptyList())
    val portfolio: StateFlow<List<PortfolioProject>> = _portfolio.asStateFlow()

    private val _team = MutableStateFlow<List<TeamMember>>(emptyList())
    val team: StateFlow<List<TeamMember>> = _team.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _services.value = MockDataRepository.getServices()
        _portfolio.value = MockDataRepository.getPortfolio()
        _team.value = MockDataRepository.getTeam()
    }

    fun getServiceById(id: String): Service? {
        return _services.value.find { it.id == id }
    }
}
