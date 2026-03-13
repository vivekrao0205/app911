package com.nrikesari.app.ui.screens.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.nrikesari.app.ui.components.CustomTextField
import com.nrikesari.app.ui.components.PrimaryButton
import com.nrikesari.app.viewmodel.AuthViewModel

@Composable
fun ContactScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    val currentUser = FirebaseAuth.getInstance().currentUser

    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var isSending by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid =
        name.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                description.isNotBlank()

    val scroll = rememberScrollState()

    fun openLink(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun callNow() {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+916305313360")))
    }

    fun sendEmail() {
        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:contact@nrikesari.in")))
    }

    fun submitInquiry() {

        isSending = true

        val data = hashMapOf(
            "userId" to currentUser?.uid,
            "name" to name,
            "company" to company,
            "email" to email,
            "phone" to phone,
            "description" to description,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("contact_inquiries")
            .add(data)
            .addOnSuccessListener {

                isSending = false
                successMessage = "success"

                name = ""
                company = ""
                email = ""
                phone = ""
                description = ""
            }
            .addOnFailureListener {

                isSending = false
                successMessage = "error"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scroll)
            .padding(20.dp)
    ) {

        Spacer(Modifier.height(20.dp))

        Text(
            "NRIKESARI",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "We build modern websites, apps and digital products.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            DashboardItem(Icons.Default.Call, "Call") { callNow() }
            DashboardItem(Icons.Default.Email, "Email") { sendEmail() }
            DashboardItem(Icons.Default.Language, "Website") { openLink("https://nrikesari.in") }
            DashboardItem(Icons.Default.CameraAlt, "Instagram") { openLink("https://instagram.com/nrikesari") }
        }

        Spacer(Modifier.height(30.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {

            ContactRow(
                Icons.Default.LocationOn,
                "Our Location",
                "Open in Google Maps"
            ) {
                openLink("https://www.google.com/maps/@17.6364829,78.4860126,17z")
            }
        }

        Spacer(Modifier.height(30.dp))

        Text(
            "Dashboard",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {

            Column(modifier = Modifier.padding(18.dp)) {

                if (currentUser != null) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(Icons.Default.AccountCircle, null)

                        Spacer(Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {

                            Text(
                                currentUser.displayName ?: "User",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                currentUser.email ?: "",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            "Logout",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {

                                authViewModel.logout()

                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            }
                        )
                    }

                } else {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Button(
                            onClick = { navController.navigate("login") },
                            modifier = Modifier.weight(1f)
                        ) { Text("Login") }

                        OutlinedButton(
                            onClick = { navController.navigate("signup") },
                            modifier = Modifier.weight(1f)
                        ) { Text("Sign Up") }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    DashboardItem(Icons.Default.Event, "Book") {
                        navController.navigate("book_call")
                    }

                    DashboardItem(Icons.Default.Star, "Review") {
                        navController.navigate("write_review")
                    }

                    DashboardItem(Icons.Default.Work, "My Projects") {
                        navController.navigate("my_projects")
                    }

                    DashboardItem(Icons.Default.Settings, "Settings") {
                        navController.navigate("settings")
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        Text(
            "Send a Message",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

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
                text = if (isSending) "Sending..." else "Submit Inquiry",
                onClick = { if (isFormValid) submitInquiry() },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isSending
            )
        }

        Spacer(Modifier.height(30.dp))

        successMessage?.let {

            if (it == "success") {

                Text(
                    "Inquiry sent successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

            } else {

                Text(
                    "Failed to send inquiry",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun DashboardItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {

        Surface(
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {

            Icon(
                icon,
                null,
                modifier = Modifier.padding(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun ContactRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {

        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(title, fontWeight = FontWeight.SemiBold)

            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(Icons.Default.ChevronRight, null)
    }
}