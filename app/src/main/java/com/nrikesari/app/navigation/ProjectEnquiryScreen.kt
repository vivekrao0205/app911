package com.nrikesari.app.ui.screens.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.viewmodel.AuthViewModel
import com.nrikesari.app.viewmodel.UserViewModel

@Composable
fun ProjectEnquiryScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {

    val currentUser = FirebaseAuth.getInstance().currentUser

    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("Video Editing") }
    var complexity by remember { mutableStateOf("Medium") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf(false) }

    val services = listOf(
        "Video Editing",
        "3D / VFX",
        "Graphic Design",
        "UI/UX",
        "Web Development",
        "App Development",
        "Digital Marketing",
        "Content Creation"
    )

    val complexityLevels = listOf("Simple", "Medium", "Advanced")

    val priceEstimate = when (service) {
        "Video Editing" -> "₹2k – ₹30k"
        "3D / VFX" -> "₹20k – ₹70k"
        "Graphic Design" -> "₹1k – ₹20k"
        "UI/UX" -> "₹5k – ₹50k"
        "Web Development" -> "₹5k – ₹80k"
        "App Development" -> "₹40k – ₹3L"
        "Digital Marketing" -> "₹8k – ₹1L"
        "Content Creation" -> "₹3k – ₹40k"
        else -> "Custom Quote"
    }

    val scrollState = rememberScrollState()

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
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Start a Project",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Nrikesari builds modern digital products.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        /* SUCCESS MESSAGE */

        if (successMessage) {

            Surface(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "✅ Inquiry submitted successfully! We will contact you soon.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(Modifier.height(20.dp))
        }

        errorMessage?.let {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(12.dp))
        }

        Text("Select Service", fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(10.dp))

        Column {

            services.chunked(2).forEach { row ->

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    row.forEach { item ->

                        FilterChip(
                            selected = service == item,
                            onClick = { service = item },
                            label = { Text(item) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ) {

            Text(
                text = "Estimated Price: $priceEstimate",
                modifier = Modifier.padding(14.dp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(18.dp))

        Text("Project Complexity", fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            complexityLevels.forEach {

                FilterChip(
                    selected = complexity == it,
                    onClick = { complexity = it },
                    label = { Text(it) }
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text("Phone or Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Project Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {

                val userId = currentUser?.uid

                if (userId == null) {
                    errorMessage = "Please login first from Settings."
                    return@Button
                }

                if (name.isBlank() || contact.isBlank() || description.isBlank()) {
                    errorMessage = "Please fill all required fields."
                    return@Button
                }

                errorMessage = null

                val inquiry = ProjectInquiry(
                    userId = userId,
                    name = name.trim(),
                    contact = contact.trim(),
                    service = service,
                    description = description.trim()
                )

                userViewModel.submitProjectEnquiry(inquiry)

                successMessage = true

                name = ""
                contact = ""
                description = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {

            Icon(Icons.Default.Send, contentDescription = null)

            Spacer(Modifier.width(8.dp))

            Text("Send Inquiry")
        }

        Spacer(Modifier.height(50.dp))
    }
}