package com.nrikesari.app.firebase

import android.net.Uri
import com.nrikesari.app.model.*
import kotlinx.coroutines.delay
import java.util.UUID

class FirebaseService {

    // --- Mock User Data ---
    private var mockCurrentUser: User? = null
    
    // Check if we have a mocked user
    val currentUser: User? get() = mockCurrentUser

    // Mock Login
    suspend fun login(email: String, pass: String): Result<Unit> {
        delay(1000) // Simulate network delay
        return try {
            if (email.isNotBlank() && pass.isNotBlank()) {
                mockCurrentUser = User(
                    uid = UUID.randomUUID().toString(),
                    name = "Mock User",
                    email = email
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Mock Signup
    suspend fun signup(email: String, pass: String, name: String, phone: String): Result<Unit> {
        delay(1000)
        return try {
            if (email.isNotBlank() && pass.isNotBlank() && name.isNotBlank()) {
                 mockCurrentUser = User(
                    uid = UUID.randomUUID().toString(),
                    name = name,
                    email = email,
                    phone = phone
                )
                Result.success(Unit)
            } else {
                 Result.failure(Exception("Registration failed: missing fields"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        mockCurrentUser = null
    }

    // --- User Profile ---
    suspend fun getUserProfile(uid: String): Result<User?> {
        delay(500)
        return Result.success(mockCurrentUser)
    }

    // --- Projects ---
    suspend fun submitProjectInquiry(inquiry: ProjectInquiry): Result<Unit> {
        delay(800)
        return Result.success(Unit)
    }

    suspend fun getUserProjects(userId: String): Result<List<ProjectInquiry>> {
        delay(800)
        // Mocking a few past projects for the user
        val mockProjects = listOf(
            ProjectInquiry(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "My Awesome App",
                contact = "hello@example.com",
                service = "App Dev",
                description = "Need a fitness app built from scratch.",
                status = "In Progress"
            ),
             ProjectInquiry(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "Corporate Website",
                contact = "hello@example.com",
                service = "Web Dev",
                description = "Redesigning our company portfolio.",
                status = "Review"
            )
        )
        return Result.success(mockProjects)
    }

    // --- Booking ---
    suspend fun submitBooking(booking: Booking): Result<Unit> {
        delay(800)
        return Result.success(Unit)
    }

    // --- Testimonials ---
    suspend fun getTestimonials(): Result<List<Testimonial>> {
        delay(800)
        // Mock a rich set of testimonials
        val mockTestimonials = listOf(
            Testimonial(
                id = "1",
                clientName = "Rajeshwari Agencies",
                serviceType = "App Development",
                feedback = "Nrikesari completely transformed our digital presence. Their design team is top-notch and the new app works flawlessly.",
                rating = 5f
            ),
            Testimonial(
                id = "2",
                clientName = "Global Tech",
                serviceType = "UI/UX Design",
                feedback = "The attention to detail and user-centric approach is unparalleled. Our engagement metrics doubled after the redesign.",
                rating = 5f
            ),
             Testimonial(
                id = "3",
                clientName = "Creative Studios",
                serviceType = "Video Editing",
                feedback = "Fast, reliable, and incredibly creative. They delivered exactly what we envisioned for our marketing campaign.",
                rating = 4.5f
            ),
             Testimonial(
                id = "4",
                clientName = "Vertex Solutions",
                serviceType = "Web Development",
                feedback = "A seamless experience from start to finish. The team is professional, and the end product is extremely robust.",
                rating = 5f
            )
        )
        return Result.success(mockTestimonials)
    }

    // --- Chat ---
    suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }

    suspend fun getChatMessages(projectId: String): Result<List<ChatMessage>> {
        delay(800)
        // Return some basic mock messages
        val msgs = listOf(
            ChatMessage(
                id = "1",
                projectId = projectId,
                senderId = "team",
                text = "Hello! We've reviewed your inquiry. Can we schedule a quick call?",
                timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
            ),
             ChatMessage(
                id = "2",
                projectId = projectId,
                senderId = "user", // simulating the user
                text = "Yes, absolutely. Does tomorrow at 10 AM work?",
                timestamp = System.currentTimeMillis() - 3600000 // 1 hour ago
            )
        )
        return Result.success(msgs)
    }
    
    suspend fun uploadFile(uri: Uri, folder: String): Result<String> {
        delay(1000)
        return Result.success("https://mock-storage.com/${UUID.randomUUID()}")
    }
}
