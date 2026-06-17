package com.nrikesari.app.model

import com.google.firebase.firestore.PropertyName



// ---------------- USER MODEL ----------------

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val joinedAt: Long = System.currentTimeMillis(),
    val accountStatus: String = "active",
    val fcmToken: String = ""
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
    val updatedAt: Long = System.currentTimeMillis(),
    val email: String = "",
    val phone: String = "",
    val companyName: String = "",
    val projectType: String = "",
    val budgetRange: String = "",
    val timeline: String = "",
    val goals: String = "",
    val fileUrl: String = "",
    val additionalNotes: String = ""
)


// ---------------- CHAT MESSAGE MODEL ----------------

data class ChatMessage(
    val id: String = "",
    val projectId: String = "",
    val senderId: String = "",
    val text: String = "",
    val attachmentUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @get:PropertyName("read") @field:PropertyName("read") val isRead: Boolean = false
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


// ---------------- DYNAMIC PROJECT MODEL ----------------

data class DynamicProject(
    val id: String = "",
    val title: String = "",
    val shortDescription: String = "",
    val fullDescription: String = "",
    val coverImage: String = "",
    val galleryImages: List<String> = emptyList(),
    val technologiesUsed: List<String> = emptyList(),
    val category: String = "",
    val completionDate: String = "",
    val projectUrl: String = "",
    val gitHubUrl: String = "",
    val clientName: String = "",
    val status: String = "Completed", // Ongoing, Completed, Upcoming
    @get:PropertyName("published") @field:PropertyName("published") val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)


// ---------------- NOTIFICATION MODEL ----------------

data class Notification(
    val id: String = "",
    val userId: String = "", // sender or target user UID
    val title: String = "",
    val message: String = "",
    val type: String = "", // registration, message, booking, inquiry, ticket, file_upload, profile_update
    val status: String = "Sent", // Sent, Delivered, Seen, Accepted, Rejected, In Progress, Completed
    val clickAction: String = "", // Deep link route to navigate to
    val timestamp: Long = System.currentTimeMillis(),
    @get:PropertyName("read") @field:PropertyName("read") val isRead: Boolean = false,
    @get:PropertyName("adminAlert") @field:PropertyName("adminAlert") val isAdminAlert: Boolean = false
)