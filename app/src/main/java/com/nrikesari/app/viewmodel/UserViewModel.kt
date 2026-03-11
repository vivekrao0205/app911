package com.nrikesari.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.model.Testimonial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ---------------- REVIEW SUBMISSION STATE ----------------

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Submitting : SubmissionState()
    object Success : SubmissionState()
    data class Error(val message: String) : SubmissionState()
}

// ---------------- USER PROJECTS STATE ----------------

sealed class UserProjectsState {
    object Idle : UserProjectsState()
    object Loading : UserProjectsState()
    data class Success(val projects: List<ProjectInquiry>) : UserProjectsState()
    data class Error(val message: String) : UserProjectsState()
}

class UserViewModel : ViewModel() {

    private val firebaseService = FirebaseService()

    // ---------------- REVIEW STATE ----------------

    private val _submissionState =
        MutableStateFlow<SubmissionState>(SubmissionState.Idle)

    val submissionState: StateFlow<SubmissionState> = _submissionState

    // ---------------- REVIEWS LIST ----------------

    private val _reviews =
        MutableStateFlow<List<Testimonial>>(emptyList())

    val reviews: StateFlow<List<Testimonial>> = _reviews

    // ---------------- PROJECTS STATE ----------------

    private val _projectsState =
        MutableStateFlow<UserProjectsState>(UserProjectsState.Idle)

    val projectsState: StateFlow<UserProjectsState> = _projectsState

    // ---------------- SUBMIT REVIEW ----------------

    fun submitTestimonial(testimonial: Testimonial) {

        viewModelScope.launch {

            _submissionState.value = SubmissionState.Submitting

            val result = firebaseService.submitTestimonial(testimonial)

            result.onSuccess {

                _submissionState.value = SubmissionState.Success
                loadTestimonials()

            }.onFailure {

                _submissionState.value =
                    SubmissionState.Error(
                        it.message ?: "Failed to submit review"
                    )
            }
        }
    }

    // ---------------- LOAD REVIEWS ----------------

    fun loadTestimonials() {

        viewModelScope.launch {

            val result = firebaseService.getTestimonials()

            result.onSuccess {

                _reviews.value = it

            }.onFailure {

                _submissionState.value =
                    SubmissionState.Error(
                        it.message ?: "Failed to load reviews"
                    )
            }
        }
    }

    // ---------------- FETCH USER PROJECTS ----------------

    fun fetchUserProjects(userId: String) {

        viewModelScope.launch {

            _projectsState.value = UserProjectsState.Loading

            val result = firebaseService.getUserProjects(userId)

            result.onSuccess {

                _projectsState.value =
                    UserProjectsState.Success(it)

            }.onFailure {

                _projectsState.value =
                    UserProjectsState.Error(
                        it.message ?: "Failed to load projects"
                    )
            }
        }
    }

    // ---------------- SUBMIT PROJECT ENQUIRY ----------------

    fun submitProjectEnquiry(inquiry: ProjectInquiry) {

        viewModelScope.launch {

            val result = firebaseService.submitProjectInquiry(inquiry)

            result.onFailure {

                _submissionState.value =
                    SubmissionState.Error(
                        it.message ?: "Failed to submit enquiry"
                    )
            }
        }
    }
}