package com.nrikesari.app.ui.screens.home
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.R
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.ui.components.PrimaryButton

@Composable
fun HomeScreen(navController: NavController) {

    val scrollState = rememberScrollState()
    val circleColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)                 // 🔥 Hard circle mask
                .background(
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Nrikesari Logo",
                modifier = Modifier
                    .fillMaxSize()                 // 🔥 Fill entire circle
                    .clip(CircleShape),            // 🔥 Mask image itself
                contentScale = ContentScale.Crop   // 🔥 Crop square edges
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Hero Section
        Text(
            text = "We Build Brands.\nWe Create Digital Experiences.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Nrikesari is a creative digital agency specializing in branding, digital experiences, technology, and performance marketing.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = "Start a Project",
            onClick = { navController.navigate(Screen.Contact.route) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrimaryButton(
            text = "View Services",
            onClick = { navController.navigate(Screen.Services.route) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrimaryButton(
            text = "View Portfolio",
            onClick = { navController.navigate(Screen.Portfolio.route) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrimaryButton(
            text = "Contact Us",
            onClick = { navController.navigate(Screen.Contact.route) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}