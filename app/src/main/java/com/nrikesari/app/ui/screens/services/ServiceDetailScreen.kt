package com.nrikesari.app.ui.screens.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.model.Service
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.ui.components.PrimaryButton
import com.nrikesari.app.ui.components.getServiceIcon

@Composable
fun ServiceDetailScreen(
    navController: NavController,
    service: Service
) {

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme
    val icon = getServiceIcon(service.id)

    val priceEstimate = when(service.title){
        "Video Editing" -> "₹2k – ₹30k"
        "3D / VFX" -> "₹20k – ₹70k"
        "Graphic Design" -> "₹1k – ₹20k"
        "UI/UX" -> "₹5k – ₹50k"
        "Web Development" -> "₹5k – ₹80k"
        "App Development" -> "₹40k – ₹3L"
        "Digital Marketing" -> "₹8k – ₹1L"
        "Content Creation" -> "₹3k – ₹40k"
        else -> "Custom Quote"
    }

    val timeline = when(service.title){
        "Video Editing" -> "2 – 7 days"
        "3D / VFX" -> "7 – 21 days"
        "Graphic Design" -> "1 – 5 days"
        "UI/UX" -> "5 – 14 days"
        "Web Development" -> "7 – 30 days"
        "App Development" -> "20 – 90 days"
        "Digital Marketing" -> "Monthly campaign"
        "Content Creation" -> "2 – 10 days"
        else -> "Project Based"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        /* -------- SERVICE ICON -------- */

        Surface(
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, colorScheme.outlineVariant),
            color = colorScheme.surface,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = service.title,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* -------- TITLE -------- */

        Text(
            text = service.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Professional service designed to help your business grow and scale.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        /* -------- PRICE -------- */

        SectionCard(
            icon = Icons.Default.Payments,
            title = "Estimated Pricing"
        ) {
            Text(
                text = priceEstimate,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* -------- TIMELINE -------- */

        SectionCard(
            icon = Icons.Default.Schedule,
            title = "Delivery Timeline"
        ) {
            Text(
                text = timeline,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* -------- WHAT WE OFFER -------- */

        SectionCard(
            icon = Icons.Default.DesignServices,
            title = "What we offer"
        ) {
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* -------- BENEFITS -------- */

        SectionCard(
            icon = Icons.Default.Star,
            title = "Benefits"
        ) {

            service.benefits.forEach { benefit ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* -------- WHY CHOOSE US -------- */

        SectionCard(
            icon = Icons.Default.WorkspacePremium,
            title = "Why choose Nrikesari"
        ) {
            Text(
                text = service.whyChooseUs,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        /* -------- CTA -------- */

        PrimaryButton(
            text = "Start This Service",
            onClick = { navController.navigate(Screen.Contact.route) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, colorScheme.outlineVariant),
        color = colorScheme.surface
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}