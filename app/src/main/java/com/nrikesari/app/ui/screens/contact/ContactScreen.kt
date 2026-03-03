package com.nrikesari.app.ui.screens.contact

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Contact Us",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Direct Connect", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("WhatsApp: +91 6305509386", style = MaterialTheme.typography.bodyMedium)
                Text("Call: +91 6305509386", style = MaterialTheme.typography.bodyMedium)
                Text("Email: contact@nrikesari.in", style = MaterialTheme.typography.bodyMedium)
                Text("Instagram: @nrikesari", style = MaterialTheme.typography.bodyMedium)
                Text("Website: nrikesari.in", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Text("Send us a message", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(value = name, onValueChange = { name = it }, label = "Name")
        CustomTextField(value = company, onValueChange = { company = it }, label = "Company")
        CustomTextField(value = email, onValueChange = { email = it }, label = "Email")
        CustomTextField(value = phone, onValueChange = { phone = it }, label = "Phone")
        CustomTextField(value = description, onValueChange = { description = it }, label = "Project Description", singleLine = false)

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Submit Inquiry",
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Thank You") },
            text = { Text("Thank you for contacting Nrikesari. Our team will reach out shortly.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.popBackStack()
                }) {
                    Text("OK")
                }
            }
        )
    }
}
