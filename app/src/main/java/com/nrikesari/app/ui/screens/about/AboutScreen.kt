package com.nrikesari.app.ui.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.navigation.Screen

@Composable
fun AboutScreen(navController: NavController) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    fun openMaps() {
        val uri = Uri.parse("https://www.google.com/maps/@17.6364829,78.4860126,17.89z")
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        Text(
            text = "About Nrikesari",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Nrikesari is a creative media and technology agency helping brands grow through powerful digital solutions, modern design, and innovative storytelling.",
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            HighlightCard("Projects", "10+")
            HighlightCard("Clients", "20+")
            HighlightCard("Experience", ">1 yr")
        }

        SectionCard(
            icon = Icons.Default.Flag,
            title = "Our Mission",
            content = "Empowering brands through creative technology, strategic design, and performance-driven digital solutions."
        )

        SectionCard(
            icon = Icons.Default.Visibility,
            title = "Our Vision",
            content = "To become a globally recognized digital innovation agency delivering impactful creative solutions."
        )

        SectionCard(
            icon = Icons.Default.Build,
            title = "What We Do",
            content =
            "• App Development\n" +
                    "• Website Development\n" +
                    "• VFX & CGI\n" +
                    "• Video Editing\n" +
                    "• Digital Marketing\n" +
                    "• Branding & Creative Design"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openMaps() },
            elevation = CardDefaults.cardElevation(0.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hyderabad, India",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Open in Google Maps",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "Explore More",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = { navController.navigate(Screen.Skills.route) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Code, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Skills")
            }

            Button(
                onClick = { navController.navigate(Screen.Settings.route) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Settings, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Settings")
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Contact.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {

            Icon(Icons.Default.Email, null)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Contact Us",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

/* -------- HIGHLIGHT CARD -------- */

@Composable
fun HighlightCard(title: String, value: String) {

    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/* -------- SECTION CARD -------- */

@Composable
fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}