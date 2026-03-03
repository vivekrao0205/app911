package com.nrikesari.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.ui.components.PrimaryButton

@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Icon(
            imageVector = Icons.Default.LocalFlorist,
            contentDescription = "Logo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "NRIKESARI",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Media & Technology",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "We Build Brands.\nWe Create Digital Experiences.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Nrikesari is a creative digital agency specializing in digital experiences, branding, technology, and performance marketing.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        PrimaryButton(
            text = "Start a Project",
            onClick = { navController.navigate(Screen.Contact.route) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        PrimaryButton(
            text = "View Services",
            onClick = { navController.navigate(Screen.Services.route) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        PrimaryButton(
            text = "View Portfolio",
            onClick = { navController.navigate(Screen.Portfolio.route) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        PrimaryButton(
            text = "Contact Us",
            onClick = { navController.navigate(Screen.Contact.route) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
