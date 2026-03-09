package com.nrikesari.app.ui.screens.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCallScreen(navController: NavController) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf(firebaseService.currentUser?.displayName ?: "") }
    var email by remember { mutableStateOf(firebaseService.currentUser?.email ?: "") }
    var dateString by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedTimeSlot by remember { mutableStateOf("10:00 AM") }

    val timeSlots = listOf("10:00 AM", "11:30 AM", "01:00 PM", "03:00 PM", "05:00 PM")

    var isSubmitting by remember { mutableStateOf(false) }
    var submitSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Call", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
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

            Text(
                "Schedule a Consultation",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Pick a date and time that works for you. We'll send a meeting link to your email.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (submitSuccess) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                ) {
                    Text(
                        "Call booked successfully! Check your email for confirmation.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 12.dp))
            }

            // Form
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = dateString,
                onValueChange = { dateString = it },
                label = { Text("Desired Date (e.g. Oct 15, 2026)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Time Slot", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            timeSlots.forEach { slot ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedTimeSlot == slot,
                        onClick = { selectedTimeSlot = slot }
                    )
                    Text(slot)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Topic / Notes (Optional)") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    val userId = firebaseService.currentUser?.uid
                    if (userId == null) {
                        errorMessage = "Please login from Settings to book a call."
                        return@Button
                    }
                    if (name.isBlank() || email.isBlank() || dateString.isBlank()) {
                        errorMessage = "Please fill out all required fields."
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
                            errorMessage = "Booking failed. Try again."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(30.dp),
                enabled = !isSubmitting && !submitSuccess
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Event, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (submitSuccess) "Booked" else "Confirm Booking")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
