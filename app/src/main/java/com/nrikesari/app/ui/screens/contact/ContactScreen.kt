package com.nrikesari.app.ui.screens.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.ui.components.CustomTextField
import com.nrikesari.app.ui.components.PrimaryButton

@Composable
fun ContactScreen(navController: NavController) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val isFormValid =
        name.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                description.isNotBlank()

    fun sendToWhatsApp() {
        val message = """
            📩 New Inquiry from Nrikesari App
            
            👤 Name: $name
            🏢 Company: $company
            📧 Email: $email
            📱 Phone: $phone
            
            📝 Project Details:
            $description
        """.trimIndent()

        val url = "https://wa.me/916305313360?text=${Uri.encode(message)}"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun makeCall() {
        context.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:+916305313360"))
        )
    }

    fun sendEmail() {
        context.startActivity(
            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:contact@nrikesari.in"))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Contact Nrikesari",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Let’s build something impactful together.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // CONTACT CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {

                    ContactRow(
                        icon = Icons.Default.Chat,
                        text = "WhatsApp: +91 63053 13360",
                        onClick = { sendToWhatsApp() }
                    )

                    Divider()

                    ContactRow(
                        icon = Icons.Default.Call,
                        text = "Call: +91 63053 13360",
                        onClick = { makeCall() }
                    )

                    Divider()

                    ContactRow(
                        icon = Icons.Default.Email,
                        text = "Email: contact@nrikesari.in",
                        onClick = { sendEmail() }
                    )

                    Divider()

                    ContactRow(
                        icon = Icons.Default.Language,
                        text = "Website: nrikesari.in",
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://nrikesari.in")
                                )
                            )
                        }
                    )
                }
            }

            Text(
                text = "Send a Message",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                CustomTextField(name, { name = it }, "Name")
                CustomTextField(company, { company = it }, "Company (Optional)")
                CustomTextField(email, { email = it }, "Email")
                CustomTextField(phone, { phone = it }, "Phone")

                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Project Description",
                    singleLine = false
                )

                PrimaryButton(
                    text = "Submit Inquiry",
                    onClick = { sendToWhatsApp() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ContactRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}