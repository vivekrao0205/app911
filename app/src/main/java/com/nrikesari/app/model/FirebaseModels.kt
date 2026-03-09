package com.nrikesari.app.model


// ---------------- USER MODEL ----------------

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


// ---------------- PROJECT INQUIRY MODEL ----------------

data class ProjectInquiry(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val contact: String = "",
    val service: String = "",
    val description: String = "",
    val status: String = "Inquiry Received",
    val submittedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


// ---------------- CHAT MESSAGE MODEL ----------------

data class ChatMessage(
    val id: String = "",
    val projectId: String = "",
    val senderId: String = "",
    val text: String = "",
    val attachmentUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)


// ---------------- BOOKING MODEL ----------------

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


// ---------------- TESTIMONIAL / REVIEW MODEL ----------------

data class Testimonial(
    val id: String = "",
    val clientName: String = "",
    val serviceType: String = "",
    val feedback: String = "",
    val rating: Float = 5f,
    val avatarUrl: String = "",
    val timestamp: Long = System.currentTimeMillis() // important for realtime sorting
)