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
                "name" to (firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User"),
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

            // Trigger notification for admins
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                userId = inquiry.userId,
                title = "New Project Inquiry",
                message = "${inquiry.name} requested service: ${inquiry.service}",
                type = "inquiry",
                clickAction = "admin_communications",
                isAdminAlert = true
            )
            saveNotification(notification)

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

            val newBooking = booking.copy(
                id = id,
                status = booking.status ?: "Pending",
                bookedAt = System.currentTimeMillis()
            )

            bookings.document(id).set(newBooking).await()

            // Trigger notification for admins
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                userId = booking.userId,
                title = "New Booking Request",
                message = "${booking.name} requested consultation on ${booking.date} at ${booking.timeSlot}",
                type = "booking",
                clickAction = "admin_dashboard",
                isAdminAlert = true
            )
            saveNotification(notification)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserBookings(userId: String): Result<List<Booking>> {
        return try {

            val snapshot = bookings
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val list = snapshot.documents
                .mapNotNull { it.toObject(Booking::class.java) }
                .sortedByDescending { it.bookedAt }

            Result.success(list)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> {
        return try {
            bookings.document(bookingId).update("status", status).await()
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

    // ---------------- DYNAMIC PROJECTS CRUD ----------------

    suspend fun saveDynamicProject(project: DynamicProject): Result<Unit> {
        return try {
            val id = if (project.id.isBlank()) UUID.randomUUID().toString() else project.id
            val newProject = project.copy(id = id)
            firestore.collection("projects").document(id).set(newProject).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun broadcastProjectNotification(project: DynamicProject): Result<Unit> {
        return try {
            val usersResult = getAllUsers()
            if (usersResult.isSuccess) {
                val allUsers = usersResult.getOrDefault(emptyList())
                for (user in allUsers) {
                    if (user.uid.isNotEmpty()) {
                        val notifId = UUID.randomUUID().toString()
                        val notification = Notification(
                            id = notifId,
                            userId = user.uid,
                            title = "New Project Showcase",
                            message = "Check out our latest project: ${project.title}",
                            type = "project_update",
                            clickAction = "project_detail/${project.id}",
                            isAdminAlert = false
                        )
                        saveNotification(notification)
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun broadcastCustomNotification(title: String, message: String, type: String = "general", clickAction: String = ""): Result<Unit> {
        return try {
            val usersResult = getAllUsers()
            if (usersResult.isSuccess) {
                val allUsers = usersResult.getOrDefault(emptyList())
                for (user in allUsers) {
                    if (user.uid.isNotEmpty()) {
                        val notifId = UUID.randomUUID().toString()
                        val notification = Notification(
                            id = notifId,
                            userId = user.uid,
                            title = title,
                            message = message,
                            type = type,
                            clickAction = clickAction,
                            isAdminAlert = false
                        )
                        saveNotification(notification)
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteDynamicProject(projectId: String): Result<Unit> {
        return try {
            firestore.collection("projects").document(projectId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDynamicProjects(includeUnpublished: Boolean = false): Result<List<DynamicProject>> {
        return try {
            val query = if (includeUnpublished) {
                firestore.collection("projects")
            } else {
                firestore.collection("projects").whereEqualTo("published", true)
            }
            val snapshot = query.get().await()
            val list = snapshot.documents.mapNotNull { it.toObject(DynamicProject::class.java) }
            Result.success(list.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- NOTIFICATIONS CRUD ----------------

    suspend fun saveNotification(notification: Notification): Result<Unit> {
        return try {
            val id = if (notification.id.isBlank()) UUID.randomUUID().toString() else notification.id
            val newNotif = notification.copy(id = id)
            firestore.collection("notifications").document(id).set(newNotif).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserNotifications(userId: String): Result<List<Notification>> {
        return try {
            val snapshot = firestore.collection("notifications")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
            Result.success(list.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAdminNotifications(): Result<List<Notification>> {
        return try {
            val snapshot = firestore.collection("notifications")
                .whereEqualTo("adminAlert", true)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
            Result.success(list.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        return try {
            firestore.collection("notifications").document(notificationId).update("read", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNotificationStatus(notificationId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("notifications").document(notificationId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllNotificationsAsRead(userId: String?, isAdmin: Boolean): Result<Unit> {
        return try {
            val query = if (isAdmin) {
                firestore.collection("notifications").whereEqualTo("adminAlert", true).whereEqualTo("read", false)
            } else {
                firestore.collection("notifications").whereEqualTo("userId", userId).whereEqualTo("read", false)
            }
            val snapshot = query.get().await()
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.update(doc.reference, "read", true)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            firestore.collection("notifications").document(notificationId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- USER MANAGEMENT ----------------

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val snapshot = users.get().await()
            val list = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
            Result.success(list.sortedByDescending { it.joinedAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun suspendUser(uid: String): Result<Unit> {
        return try {
            users.document(uid).update("accountStatus", "suspended").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reactivateUser(uid: String): Result<Unit> {
        return try {
            users.document(uid).update("accountStatus", "active").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(uid: String): Result<Unit> {
        return try {
            users.document(uid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- COMMUNICATIONS & INQUIRIES ----------------

    suspend fun getAllInquiries(): Result<List<ProjectInquiry>> {
        return try {
            val snapshot = inquiries.get().await()
            val list = snapshot.documents.mapNotNull { it.toObject(ProjectInquiry::class.java) }
            Result.success(list.sortedByDescending { it.submittedAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllContactInquiries(): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = firestore.collection("contact_inquiries").get().await()
            val list = snapshot.documents.map { doc ->
                val map = doc.data ?: emptyMap<String, Any>()
                map + ("id" to doc.id)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInquiryStatus(inquiryId: String, status: String): Result<Unit> {
        return try {
            inquiries.document(inquiryId).update("status", status, "updatedAt", System.currentTimeMillis()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- ANALYTICS & COUNTS ----------------

    suspend fun getAnalyticsCounts(): Result<Map<String, Int>> {
        return try {
            val usersCount = users.get().await().size()
            val projectsCount = firestore.collection("projects").get().await().size()
            val notificationsCount = firestore.collection("notifications").get().await().size()
            val bookingsCount = bookings.get().await().size()
            val inquiriesCount = inquiries.get().await().size()
            val contactInquiriesCount = firestore.collection("contact_inquiries").get().await().size()
            val messagesCount = firestore.collectionGroup("chats").get().await().size()

            val map = mapOf(
                "Total Users" to usersCount,
                "Active Users" to usersCount, // Simplified
                "Total Projects" to projectsCount,
                "Total Bookings" to bookingsCount,
                "Total Inquiries" to (inquiriesCount + contactInquiriesCount),
                "Total Notifications" to notificationsCount,
                "Total Messages" to messagesCount
            )
            Result.success(map)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- REALTIME SNAPSHOT LISTENERS ----------------

    fun listenToDynamicProjects(
        includeUnpublished: Boolean = false,
        onUpdate: (List<DynamicProject>) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration {
        val query = if (includeUnpublished) {
            firestore.collection("projects")
        } else {
            firestore.collection("projects").whereEqualTo("published", true)
        }
        return query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { it.toObject(DynamicProject::class.java) }
                onUpdate(list.sortedByDescending { it.createdAt })
            }
        }
    }

    fun listenToUserNotifications(
        userId: String,
        onUpdate: (List<Notification>) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration {
        return firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
                    onUpdate(list.sortedByDescending { it.timestamp })
                }
            }
    }

    fun listenToAdminNotifications(
        onUpdate: (List<Notification>) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration {
        return firestore.collection("notifications")
            .whereEqualTo("adminAlert", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
                    onUpdate(list.sortedByDescending { it.timestamp })
                }
            }
    }

    fun listenToAnalyticsCounts(
        onUpdate: (Map<String, Int>) -> Unit
    ): List<com.google.firebase.firestore.ListenerRegistration> {
        val regs = mutableListOf<com.google.firebase.firestore.ListenerRegistration>()
        
        val counts = mutableMapOf(
            "Total Users" to 0,
            "Active Users" to 0,
            "Total Projects" to 0,
            "Total Bookings" to 0,
            "Total Inquiries" to 0,
            "Total Notifications" to 0,
            "Total Messages" to 0
        )
        
        fun triggerUpdate() {
            onUpdate(counts.toMap())
        }
        
        regs.add(users.addSnapshotListener { snap, _ ->
            if (snap != null) {
                counts["Total Users"] = snap.size()
                // Fetch active user count specifically
                val activeCount = snap.documents.count { (it.getString("accountStatus") ?: "active") == "active" }
                counts["Active Users"] = activeCount
                triggerUpdate()
            }
        })
        
        regs.add(firestore.collection("projects").addSnapshotListener { snap, _ ->
            if (snap != null) {
                counts["Total Projects"] = snap.size()
                triggerUpdate()
            }
        })
        
        regs.add(bookings.addSnapshotListener { snap, _ ->
            if (snap != null) {
                counts["Total Bookings"] = snap.size()
                triggerUpdate()
            }
        })
        
        var inqCount = 0
        var contactInqCount = 0
        regs.add(inquiries.addSnapshotListener { snap, _ ->
            if (snap != null) {
                inqCount = snap.size()
                counts["Total Inquiries"] = inqCount + contactInqCount
                triggerUpdate()
            }
        })
        
        regs.add(firestore.collection("contact_inquiries").addSnapshotListener { snap, _ ->
            if (snap != null) {
                contactInqCount = snap.size()
                counts["Total Inquiries"] = inqCount + contactInqCount
                triggerUpdate()
            }
        })
        
        regs.add(firestore.collection("notifications").addSnapshotListener { snap, _ ->
            if (snap != null) {
                counts["Total Notifications"] = snap.size()
                triggerUpdate()
            }
        })

        regs.add(firestore.collectionGroup("chats").addSnapshotListener { snap, _ ->
            if (snap != null) {
                counts["Total Messages"] = snap.size()
                triggerUpdate()
            }
        })
        
        return regs
    }
}