package com.nrikesari.app.ui.screens.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.ui.components.CustomTextField
import com.nrikesari.app.ui.components.PrimaryButton

@Composable
fun ContactScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            Text(
                text = "Contact Nrikesari",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 🔮 Glass Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(0.5.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Direct Connect",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text("WhatsApp: +91 6305509386")
                    Text("Call: +91 6305509386")
                    Text("Email: contact@nrikesari.in")
                    Text("Instagram: @nrikesari")
                    Text("Website: nrikesari.in")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Send us a message",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                CustomTextField(value = name, onValueChange = { name = it }, label = "Name")
                CustomTextField(value = company, onValueChange = { company = it }, label = "Company")
                CustomTextField(value = email, onValueChange = { email = it }, label = "Email")
                CustomTextField(value = phone, onValueChange = { phone = it }, label = "Phone")
                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Project Description",
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Submit Inquiry",
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Thank You",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Thank you for contacting Nrikesari. Our team will reach out shortly.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}