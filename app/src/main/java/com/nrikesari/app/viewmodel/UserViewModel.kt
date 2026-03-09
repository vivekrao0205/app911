package com.nrikesari.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.ProjectInquiry
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserProjectsState {
    object Idle : UserProjectsState()
    object Loading : UserProjectsState()
    data class Success(val projects: List<ProjectInquiry>) : UserProjectsState()
    data class Error(val message: String) : UserProjectsState()
}

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Submitting : SubmissionState()
    object Success : SubmissionState()
    data class Error(val message: String) : SubmissionState()
}

class UserViewModel : ViewModel() {

    private val firebaseService = FirebaseService()

    private val _projectsState = MutableStateFlow<UserProjectsState>(UserProjectsState.Idle)
    val projectsState: StateFlow<UserProjectsState> = _projectsState.asStateFlow()

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState.asStateFlow()

    fun fetchUserProjects(userId: String) {
        _projectsState.value = UserProjectsState.Loading
        viewModelScope.launch {
            val result = firebaseService.getUserProjects(userId)
            if (result.isSuccess) {
                _projectsState.value = UserProjectsState.Success(result.getOrDefault(emptyList()))
            } else {
                _projectsState.value = UserProjectsState.Error(result.exceptionOrNull()?.message ?: "Failed to fetch projects")
            }
        }
    }

    fun submitProjectEnquiry(inquiry: ProjectInquiry) {
        _submissionState.value = SubmissionState.Submitting
        viewModelScope.launch {
            val result = firebaseService.submitProjectInquiry(inquiry)
            if (result.isSuccess) {
                _submissionState.value = SubmissionState.Success
                // optionally re-fetch projects if needed, or rely on snapshot
                fetchUserProjects(inquiry.userId) 
            } else {
                _submissionState.value = SubmissionState.Error(result.exceptionOrNull()?.message ?: "Failed to submit enquiry")
            }
        }
    }
    
    fun submitTestimonial(testimonial: com.nrikesari.app.model.Testimonial) {
        _submissionState.value = SubmissionState.Submitting
        viewModelScope.launch {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            try {
                db.collection("testimonials")
                    .document(testimonial.id)
                    .set(testimonial)
                    .await()
                _submissionState.value = SubmissionState.Success
            } catch (e: Exception) {
                _submissionState.value = SubmissionState.Error(e.message ?: "Failed to submit review")
            }
        }
    }
    
    fun resetSubmissionState() {
        _submissionState.value = SubmissionState.Idle
    }
}
