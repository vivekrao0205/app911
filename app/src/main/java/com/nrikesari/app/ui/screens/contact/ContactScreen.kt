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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
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

    val scroll = rememberScrollState()

    val isFormValid =
        name.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                description.isNotBlank()

    fun sendWhatsApp() {

        val message = """
📩 *New Inquiry from Nrikesari App*

👤 Name: $name
🏢 Company: $company
📧 Email: $email
📱 Phone: $phone

📝 Project Details:
$description
        """.trimIndent()

        val url = "https://wa.me/916305313360?text=${Uri.encode(message)}"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

        name = ""
        company = ""
        email = ""
        phone = ""
        description = ""
    }

    fun callNow() {
        context.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:+916305313360"))
        )
    }

    fun sendEmail() {
        context.startActivity(
            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:contact@nrikesari.in"))
        )
    }

    fun openWebsite() {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://nrikesari.in"))
        )
    }

    fun openMaps() {
        val uri = Uri.parse("https://www.google.com/maps/@17.6364829,78.4860126,17.89z")
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun openInstagram() {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://instagram.com/nrikesari")
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scroll)
            .padding(20.dp)
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Contact Us",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "We'd love to hear about your project",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        /* -------- CONTACT OPTIONS -------- */

        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {

            Column {

                ContactRow(Icons.Default.Chat,"WhatsApp","+91 63053 13360"){ sendWhatsApp() }

                HorizontalDivider()

                ContactRow(Icons.Default.Call,"Call","Talk directly with us"){ callNow() }

                HorizontalDivider()

                ContactRow(Icons.Default.Email,"Email","contact@nrikesari.in"){ sendEmail() }

                HorizontalDivider()

                ContactRow(Icons.Default.Language,"Website","nrikesari.in"){ openWebsite() }

                HorizontalDivider()

                ContactRow(Icons.Default.LocationOn,"Location","Open in Google Maps"){ openMaps() }

                HorizontalDivider()

                ContactRow(Icons.Default.CameraAlt,"Instagram","@nrikesari"){ openInstagram() }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        /* -------- MESSAGE FORM -------- */

        Text(
            text = "Send a Message",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            CustomTextField(name,{name=it},"Name")

            CustomTextField(company,{company=it},"Company (Optional)")

            CustomTextField(email,{email=it},"Email")

            CustomTextField(phone,{phone=it},"Phone")

            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Project Description",
                singleLine = false
            )

            PrimaryButton(
                text = "Submit Inquiry",
                onClick = { sendWhatsApp() },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

/* -------- CONTACT ROW -------- */

@Composable
fun ContactRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
){

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ){

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ){
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)){

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}