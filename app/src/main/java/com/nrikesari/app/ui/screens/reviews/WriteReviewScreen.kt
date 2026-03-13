package com.nrikesari.app.ui.screens.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.firebase.auth.FirebaseAuth
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

    val currentUser = FirebaseAuth.getInstance().currentUser

    val submissionState by userViewModel.submissionState.collectAsState()
    val reviews by userViewModel.reviews.collectAsState()

    var feedback by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.loadTestimonials()
    }

    LaunchedEffect(submissionState) {
        if (submissionState is SubmissionState.Error) {
            errorMessage = (submissionState as SubmissionState.Error).message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {

                Text(
                    "Share Your Experience",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Your feedback helps us improve.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Text("Rating", fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(8.dp))

                Row {

                    for (i in 1..5) {

                        Icon(
                            imageVector =
                            if (i <= rating)
                                Icons.Filled.Star
                            else
                                Icons.Outlined.StarBorder,

                            contentDescription = null,

                            tint =
                            if (i <= rating)
                                Color(0xFFFFC107)
                            else
                                Color.Gray,

                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = i }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("Service Received") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Your Review") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val isSubmitting = submissionState is SubmissionState.Submitting

                Button(
                    onClick = {

                        if (currentUser == null) {
                            errorMessage = "Please login first"
                            return@Button
                        }

                        if (feedback.isBlank()) {
                            errorMessage = "Write your review"
                            return@Button
                        }

                        val testimonial = Testimonial(
                            id = UUID.randomUUID().toString(),
                            clientName = currentUser.displayName ?: "Anonymous",
                            serviceType = serviceType,
                            feedback = feedback,
                            rating = rating.toFloat(),
                            avatarUrl = "",
                            timestamp = System.currentTimeMillis()
                        )

                        userViewModel.submitTestimonial(testimonial)

                        feedback = ""
                        serviceType = ""
                        rating = 5
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting
                ) {

                    if (isSubmitting) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )

                    } else {

                        Text("Submit Review")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Client Reviews",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(reviews) { review ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            review.clientName,
                            fontWeight = FontWeight.Bold
                        )

                        Row {

                            repeat(review.rating.toInt()) {

                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(review.feedback)

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            review.serviceType,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}