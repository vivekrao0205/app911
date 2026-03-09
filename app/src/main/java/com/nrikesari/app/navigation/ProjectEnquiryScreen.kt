package com.nrikesari.app.ui.screens.projects

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProjectEnquiryScreen() {

    val context = LocalContext.current

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

        services.forEach {

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = service == it,
                    onClick = { service = it }
                )

                Text(it)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        /* -------- SEND -------- */

        Button(
            onClick = {

                val message = """
New Project Inquiry - Nrikesari

Name: $name
Contact: $contact
Service: $service

Description:
$description
                """.trimIndent()

                val url = "https://wa.me/916305313360?text=${Uri.encode(message)}"

                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(30.dp)
        ) {

            Icon(Icons.Default.Send, null)

            Spacer(modifier = Modifier.width(8.dp))

            Text("Send Inquiry")
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}