package com.nrikesari.app.ui.screens.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.Booking
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookCallScreen(navController: NavController) {

    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    val user = firebaseService.currentUser

    var name by remember {
        mutableStateOf(
            user?.displayName ?: user?.email?.substringBefore("@") ?: ""
        )
    }

    var email by remember { mutableStateOf(user?.email ?: "") }
    var dateString by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedTimeSlot by remember { mutableStateOf("10:00 AM") }

    val timeSlots = listOf(
        "10:00 AM",
        "11:30 AM",
        "01:00 PM",
        "03:00 PM",
        "05:00 PM"
    )

    var isSubmitting by remember { mutableStateOf(false) }
    var submitSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Call", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            /* HEADER */

            Card(shape = RoundedCornerShape(18.dp)) {

                Column(modifier = Modifier.padding(20.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            "Schedule a Consultation",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "Pick a time that works for you. We'll confirm your meeting via email.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (submitSuccess) {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {

                    Text(
                        "✅ Booking confirmed! We'll contact you shortly.",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            errorMessage?.let {

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(12.dp))
            }

            /* NAME */

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !submitSuccess
            )

            Spacer(Modifier.height(12.dp))

            /* EMAIL */

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !submitSuccess
            )

            Spacer(Modifier.height(12.dp))

            /* DATE */

            OutlinedTextField(
                value = dateString,
                onValueChange = { dateString = it },
                label = { Text("Preferred Date (eg: Oct 15, 2026)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !submitSuccess
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Select Time Slot",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))

            /* TIME SLOT */

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                timeSlots.forEach { slot ->

                    FilterChip(
                        selected = selectedTimeSlot == slot,
                        onClick = { selectedTimeSlot = slot },
                        label = { Text(slot) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            /* NOTES */

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Discussion Topic (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                enabled = !submitSuccess
            )

            Spacer(Modifier.height(30.dp))

            /* BOOK BUTTON */

            Button(
                onClick = {

                    val userId = firebaseService.currentUser?.uid

                    if (userId == null) {
                        errorMessage = "Please login first from Settings."
                        return@Button
                    }

                    if (name.isBlank() || email.isBlank() || dateString.isBlank()) {
                        errorMessage = "Please complete all required fields."
                        return@Button
                    }

                    isSubmitting = true
                    errorMessage = null

                    val booking = Booking(
                        userId = userId,
                        name = name.trim(),
                        email = email.trim(),
                        date = dateString.trim(),
                        timeSlot = selectedTimeSlot,
                        notes = notes.trim()
                    )

                    coroutineScope.launch {

                        val result = firebaseService.submitBooking(booking)

                        isSubmitting = false

                        if (result.isSuccess) {

                            submitSuccess = true

                            dateString = ""
                            notes = ""

                        } else {

                            errorMessage = "Booking failed. Please try again."
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

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                } else {

                    Icon(Icons.Default.Event, contentDescription = null)

                    Spacer(Modifier.width(8.dp))

                    Text(if (submitSuccess) "Booked" else "Confirm Booking")
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}