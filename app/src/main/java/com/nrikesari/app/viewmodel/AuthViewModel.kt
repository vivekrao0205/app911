package com.nrikesari.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val firebaseService = FirebaseService()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val fbUser = firebaseService.currentUser
        if (fbUser != null) {
            _authState.value = AuthState.Authenticated(null)
            fetchUserProfile(fbUser.uid)
        } else {
            _authState.value = AuthState.Idle
            _currentUserProfile.value = null
        }
    }

    private fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            val result = firebaseService.getUserProfile(uid)
            if (result.isSuccess) {
                val profile = result.getOrNull()
                _currentUserProfile.value = profile
                _authState.value = AuthState.Authenticated(profile)
            }
        }
    }

    fun login(email: String, pass: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = firebaseService.login(email.trim(), pass.trim())
            if (result.isSuccess) {
                checkAuthStatus()
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun signup(email: String, pass: String, name: String, phone: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = firebaseService.signup(email.trim(), pass.trim(), name.trim(), phone.trim())
            if (result.isSuccess) {
                checkAuthStatus()
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Signup failed")
            }
        }
    }

    fun logout() {
        firebaseService.logout()
        _authState.value = AuthState.Idle
        _currentUserProfile.value = null
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
