package com.nrikesari.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val firebaseService = FirebaseService()
    private val firestore = FirebaseFirestore.getInstance()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    val currentUserId: String
        get() = firebaseService.currentUser?.uid ?: ""

    fun startListening(projectId: String) {
        listenerRegistration?.remove()
        
        listenerRegistration = firestore.collection("projects")
            .document(projectId)
            .collection("chats")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val newMessages = snapshot.toObjects(ChatMessage::class.java)
                    _messages.value = newMessages
                }
            }
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    fun sendMessage(projectId: String, text: String, attachmentUrl: String = "") {
        val message = ChatMessage(
            projectId = projectId,
            senderId = currentUserId,
            text = text,
            attachmentUrl = attachmentUrl
        )

        viewModelScope.launch {
            // Using firestore directly to match snapshot listener path
            val id = if (message.id.isBlank()) java.util.UUID.randomUUID().toString() else message.id
            firestore.collection("projects")
                .document(projectId)
                .collection("chats")
                .document(id)
                .set(message.copy(id = id))
        }
    }

    fun uploadAttachment(projectId: String, uri: Uri) {
        _isUploading.value = true
        viewModelScope.launch {
            val uploadResult = firebaseService.uploadFile(uri, "chat_attachments/$projectId")
            if (uploadResult.isSuccess) {
                val downloadUrl = uploadResult.getOrThrow()
                sendMessage(projectId, "Sent an attachment", downloadUrl)
            }
            _isUploading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
