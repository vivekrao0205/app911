package com.nrikesari.app.model

// User Model
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val joinedAt: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = if (name.isNotBlank()) name else email
}


// Project Inquiry Model
data class ProjectInquiry(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val contact: String = "",
    val service: String = "",
    val description: String = "",
    val status: String = "Inquiry Received", // Inquiry Received, Project Discussion, In Progress, Review, Completed
    val submittedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


// Chat Message Model
data class ChatMessage(
    val id: String = "",
    val projectId: String = "",
    val senderId: String = "", // userId or "team"
    val text: String = "",
    val attachmentUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)


// Booking Model
data class Booking(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val date: String = "",
    val timeSlot: String = "",
    val notes: String = "",
    val status: String = "Pending",
    val bookedAt: Long = System.currentTimeMillis()
)


// Testimonial Model
data class Testimonial(
    val id: String = "",
    val clientName: String = "",
    val serviceType: String = "",
    val feedback: String = "",
    val rating: Float = 5f,
    val avatarUrl: String = ""
)