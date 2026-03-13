package com.nrikesari.app.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nrikesari.app.model.*
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseService {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val users = firestore.collection("users")
    private val inquiries = firestore.collection("inquiries")
    private val bookings = firestore.collection("bookings")
    private val testimonials = firestore.collection("testimonials")
    private val messages = firestore.collection("messages")

    // ---------------- AUTH ----------------

    val currentUser get() = auth.currentUser

    suspend fun login(email: String, pass: String): Result<Unit> {
        return try {

            auth.signInWithEmailAndPassword(email, pass).await()

            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not found"))

            ensureUserProfile(uid)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(
        email: String,
        pass: String,
        name: String,
        phone: String
    ): Result<Unit> {

        return try {

            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid
                ?: return Result.failure(Exception("User ID null"))

            val userData = hashMapOf(
                "uid" to uid,
                "name" to name,
                "email" to email,
                "phone" to phone,
                "createdAt" to FieldValue.serverTimestamp()
            )

            users.document(uid).set(userData).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return try {

            val credential = GoogleAuthProvider.getCredential(idToken, null)

            auth.signInWithCredential(credential).await()

            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not found"))

            ensureUserProfile(uid)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    // ---------------- USER PROFILE ----------------

    suspend fun getUserProfile(uid: String): Result<User?> {
        return try {

            val doc = users.document(uid).get().await()

            val user = doc.toObject(User::class.java)

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun ensureUserProfile(uid: String) {

        val doc = users.document(uid).get().await()

        if (!doc.exists()) {

            val firebaseUser = auth.currentUser ?: return

            val userData = hashMapOf(
                "uid" to uid,
                "name" to (firebaseUser.displayName ?: "User"),
                "email" to (firebaseUser.email ?: ""),
                "phone" to "",
                "createdAt" to FieldValue.serverTimestamp()
            )

            users.document(uid).set(userData).await()
        }
    }

    // ---------------- PROJECT INQUIRY ----------------

    suspend fun submitProjectInquiry(inquiry: ProjectInquiry): Result<Unit> {
        return try {

            val id = if (inquiry.id.isBlank())
                UUID.randomUUID().toString()
            else inquiry.id

            val newInquiry = inquiry.copy(id = id)

            inquiries.document(id).set(newInquiry).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProjects(userId: String): Result<List<ProjectInquiry>> {
        return try {

            val snapshot = inquiries
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val list = snapshot.documents.mapNotNull {
                it.toObject(ProjectInquiry::class.java)
            }

            Result.success(list)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- BOOKINGS ----------------

    suspend fun submitBooking(booking: Booking): Result<Unit> {
        return try {

            val id = if (booking.id.isBlank())
                UUID.randomUUID().toString()
            else booking.id

            val newBooking = booking.copy(id = id)

            bookings.document(id).set(newBooking).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- TESTIMONIALS ----------------

    suspend fun submitTestimonial(testimonial: Testimonial): Result<Unit> {
        return try {

            val id = if (testimonial.id.isBlank())
                UUID.randomUUID().toString()
            else testimonial.id

            val newTestimonial = testimonial.copy(
                id = id,
                timestamp = System.currentTimeMillis()
            )

            testimonials.document(id).set(newTestimonial).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTestimonials(): Result<List<Testimonial>> {
        return try {

            val snapshot = testimonials
                .orderBy("timestamp")
                .get()
                .await()

            val list = snapshot.documents.mapNotNull {
                it.toObject(Testimonial::class.java)
            }

            Result.success(list)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- CHAT ----------------

    suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        return try {

            val id = if (message.id.isBlank())
                UUID.randomUUID().toString()
            else message.id

            val newMessage = message.copy(id = id)

            messages.document(id).set(newMessage).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChatMessages(projectId: String): Result<List<ChatMessage>> {
        return try {

            val snapshot = messages
                .whereEqualTo("projectId", projectId)
                .get()
                .await()

            val list = snapshot.documents
                .mapNotNull { it.toObject(ChatMessage::class.java) }
                .sortedBy { it.timestamp }

            Result.success(list)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- FILE UPLOAD ----------------

    suspend fun uploadFile(uri: Uri, folder: String): Result<String> {
        return try {

            val fileName = UUID.randomUUID().toString()

            val ref = storage.reference.child("$folder/$fileName")

            ref.putFile(uri).await()

            val url = ref.downloadUrl.await()

            Result.success(url.toString())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

