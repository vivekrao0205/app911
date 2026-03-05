package com.nrikesari.app.ui.screens.projects

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val Ivory = Color(0xFFF2EDEA)
private val Maroon = Color(0xFF8B2C2C)

@Composable
fun ProjectEnquiryScreen() {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf("") }
    var complexity by remember { mutableStateOf(0.5f) }

    val services = listOf(
        "Video Editing",
        "3D / VFX",
        "Graphic Design",
        "UI/UX Design",
        "Web Development",
        "App Development",
        "Digital Marketing"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ivory)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        Text(
            "Start a Project",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Maroon
        )

        Spacer(modifier = Modifier.height(30.dp))

        // ---------------- CONTACT INFO ----------------

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text("Phone or Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Project Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(30.dp))

        // ---------------- SERVICE SELECTION ----------------

        Text(
            "Select Service",
            fontWeight = FontWeight.SemiBold,
            color = Maroon
        )

        Spacer(modifier = Modifier.height(12.dp))

        services.forEach { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { selectedService = service },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service,
                        modifier = Modifier.weight(1f)
                    )

                    RadioButton(
                        selected = selectedService == service,
                        onClick = { selectedService = service },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Maroon
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ---------------- COMPLEXITY ----------------

        Text(
            "Project Complexity",
            fontWeight = FontWeight.SemiBold,
            color = Maroon
        )

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = complexity,
            onValueChange = { complexity = it },
            colors = SliderDefaults.colors(
                thumbColor = Maroon,
                activeTrackColor = Maroon
            )
        )

        Text(
            when {
                complexity < 0.33f -> "Basic"
                complexity < 0.66f -> "Standard"
                else -> "Enterprise"
            },
            color = Maroon,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ---------------- SEND BUTTON ----------------

        Button(
            onClick = {

                val complexityText = when {
                    complexity < 0.33f -> "Basic"
                    complexity < 0.66f -> "Standard"
                    else -> "Enterprise"
                }

                val message = """
🔥 New Project Inquiry - Nrikesari

👤 Name: $name
📞 Contact: $contact
🎯 Service: $selectedService
📊 Complexity: $complexityText

📝 Description:
$description
                """.trimIndent()

                val url = "https://wa.me/916305313360?text=${Uri.encode(message)}"

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Maroon)
        ) {
            Text("Send via WhatsApp", color = Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}