package com.nrikesari.app.ui.screens.projects

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.ProjectInquiry
import kotlinx.coroutines.launch

@Composable
fun ProjectEnquiryScreen() {

    val context = LocalContext.current
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("Video Editing") }

    val services = listOf(
        "Video Editing",
        "3D / VFX",
        "Graphic Design",
        "UI/UX",
        "Web Dev",
        "App Dev"
    )
    
    var isSubmitting by remember { mutableStateOf(false) }
    var submitSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    /* -------- Animated Background -------- */

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing)
        ),
        label = ""
    )

    val background = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            "Start a Project",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (submitSuccess) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Text(
                    "Project inquiry submitted successfully! We'll be in touch soon.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 12.dp))
        }

        /* -------- FORM -------- */

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text("Phone / Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Project Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        /* -------- SERVICE SELECT -------- */

        Text(
            "Service",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        services.forEach { serviceName ->

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { service = serviceName }
                    .padding(vertical = 8.dp)
            ) {

                RadioButton(
                    selected = service == serviceName,
                    onClick = { service = serviceName }
                )
                
                Spacer(modifier = Modifier.width(8.dp))

                Text(serviceName)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        /* -------- SEND -------- */

        Button(
            onClick = {
                val userId = firebaseService.currentUser?.uid
                if (userId == null) {
                    errorMessage = "Please login from Settings to submit a project."
                    return@Button
                }
                
                if (name.isBlank() || contact.isBlank() || description.isBlank()) {
                    errorMessage = "Please fill out all fields."
                    return@Button
                }
                
                isSubmitting = true
                errorMessage = null
                
                val inquiry = ProjectInquiry(
                    userId = userId,
                    name = name.trim(),
                    contact = contact.trim(),
                    service = service,
                    description = description.trim()
                )
                
                coroutineScope.launch {
                    val result = firebaseService.submitProjectInquiry(inquiry)
                    isSubmitting = false
                    if (result.isSuccess) {
                        submitSuccess = true
                        name = ""
                        contact = ""
                        description = ""
                    } else {
                        errorMessage = "Submission failed. Try again."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(30.dp),
            enabled = !isSubmitting && !submitSuccess
        ) {

            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.Send, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (submitSuccess) "Submitted" else "Send Inquiry")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}