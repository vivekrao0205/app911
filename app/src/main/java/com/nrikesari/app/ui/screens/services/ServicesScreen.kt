package com.nrikesari.app.ui.screens.services

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.ui.components.ServiceCard
import com.nrikesari.app.viewmodel.MainViewModel

@Composable
fun ServicesScreen(
    navController: NavController,
    viewModel: MainViewModel
) {

    val services by viewModel.services.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {

        item { Spacer(Modifier.height(24.dp)) }

        /* ---------- HERO HEADER ---------- */

        item {

            Text(
                text = "Our Services",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Creative, technical and digital solutions designed to help brands grow.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
        }

        /* ---------- SERVICES LIST ---------- */

        items(
            items = services,
            key = { it.id }
        ) { service ->

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { 40 }
            ) {

                ServiceCard(
                    service = service,
                    onClick = {
                        navController.navigate(
                            Screen.ServiceDetail.createRoute(service.id)
                        )
                    }
                )
            }
        }

        /* ---------- PRICING SECTION ---------- */

        item {
            Spacer(Modifier.height(20.dp))
            PricingSection()
        }
    }
}

/* ---------- PRICING ---------- */

@Composable
fun PricingSection() {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Pricing Estimates",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Approximate starting prices for base packages.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        val prices = listOf(

            Triple("Video Editing", "From ₹2,000", Icons.Default.Movie),

            Triple("3D / VFX", "From ₹20,000", Icons.Default.AutoFixHigh),

            Triple("Graphic Design", "From ₹1,000", Icons.Default.Brush),

            Triple("UI/UX Design", "From ₹5,000", Icons.Default.DesignServices),

            Triple("Web Development", "From ₹10,000", Icons.Default.Language),

            Triple("App Development", "From ₹40,000", Icons.Default.PhoneAndroid),

            Triple("Digital Marketing", "From ₹8,000", Icons.Default.Campaign),

            Triple("Content Creation", "From ₹3,000", Icons.Default.EditNote)
        )

        prices.forEach { (service, price, icon) ->

            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                ),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = service,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = price,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Pricing may vary depending on project complexity, timeline and additional features.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}