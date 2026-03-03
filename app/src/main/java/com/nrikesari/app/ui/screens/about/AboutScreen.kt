package com.nrikesari.app.ui.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AboutScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header
        Text(
            text = "About Nrikesari",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Nrikesari is a results-driven creative agency delivering innovative digital solutions. We blend strategy, creativity, and technology to help brands grow, scale, and dominate in the digital world.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectionCard(
            title = "Our Mission",
            content = "To empower brands through creative technology and performance-driven strategies."
        )

        SectionCard(
            title = "Our Vision",
            content = "To become a leading digital innovation agency recognized for creativity, impact, and excellence."
        )

        SectionCard(
            title = "What We Do",
            content = "• App Development\n" +
                    "• Website Development\n" +
                    "• VFX & CGI\n" +
                    "• Digital Marketing\n" +
                    "• Branding & Creative Design"
        )

        SectionCard(
            title = "Location",
            content = "Hyderabad, India"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Let’s build something extraordinary together.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SectionCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}