package com.nrikesari.app.ui.screens.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.model.Testimonial
import com.nrikesari.app.viewmodel.AuthViewModel
import com.nrikesari.app.viewmodel.SubmissionState
import com.nrikesari.app.viewmodel.UserViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {

    val currentUserProfile by authViewModel.currentUserProfile.collectAsState()
    val submissionState by userViewModel.submissionState.collectAsState()

    var feedback by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    LaunchedEffect(submissionState) {
        if (submissionState is SubmissionState.Success) {
            isSubmitted = true
        } else if (submissionState is SubmissionState.Error) {
            errorMessage = (submissionState as SubmissionState.Error).message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Write a Review", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            if (isSubmitted) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Thank you! Your review has been submitted.",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Go Back")
                }

                return@Column
            }

            Text(
                "Share Your Experience",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Your feedback helps us improve and helps others.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ⭐ Rating
            Text("Rating", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                for (i in 1..5) {

                    Icon(
                        imageVector =
                        if (i <= rating)
                            Icons.Filled.Star
                        else
                            Icons.Outlined.StarBorder,

                        contentDescription = "Star",

                        tint =
                        if (i <= rating)
                            Color(0xFFFFC107)
                        else
                            Color.Gray,

                        modifier = Modifier
                            .size(40.dp)
                            .clickable { rating = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Service Type
            OutlinedTextField(
                value = serviceType,
                onValueChange = { serviceType = it },
                label = { Text("Service Received") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Feedback
            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                label = { Text("Your Review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            val isSubmitting = submissionState is SubmissionState.Submitting

            Button(
                onClick = {

                    if (currentUserProfile == null) {
                        errorMessage = "Please login first."
                        return@Button
                    }

                    if (feedback.isBlank() || serviceType.isBlank()) {
                        errorMessage = "Please fill all fields."
                        return@Button
                    }

                    val testimonial = Testimonial(
                        id = UUID.randomUUID().toString(),
                        clientName = currentUserProfile?.name ?: "Anonymous",
                        serviceType = serviceType.trim(),
                        feedback = feedback.trim(),
                        rating = rating.toFloat(),
                        avatarUrl = ""
                    )

                    userViewModel.submitTestimonial(testimonial)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                shape = RoundedCornerShape(10.dp),

                enabled = !isSubmitting
            ) {

                if (isSubmitting) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White
                    )

                } else {

                    Text(
                        "Submit Review",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}