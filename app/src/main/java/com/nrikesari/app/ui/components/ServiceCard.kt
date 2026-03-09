package com.nrikesari.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nrikesari.app.model.Service

@Composable
fun ServiceCard(
    service: Service,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colorScheme = MaterialTheme.colorScheme
    val icon = getServiceIcon(service.id)

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colorScheme.primary.copy(alpha = 0.08f)
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = service.title,
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Open service",
                tint = colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/* -------- ICON MAPPER -------- */

fun getServiceIcon(id: String): ImageVector {

    val normalized = id.trim().lowercase()

    return when (normalized) {

        "video_editing", "video editing" ->
            Icons.Default.VideoLibrary

        "vfx" ->
            Icons.Default.AutoFixHigh

        "graphic_design", "graphic design" ->
            Icons.Default.Brush

        "uiux", "ui/ux" ->
            Icons.Default.DesignServices

        "web_dev", "web development" ->
            Icons.Default.Language

        "app_dev", "app development" ->
            Icons.Default.PhoneAndroid

        "digital_marketing", "digital marketing" ->
            Icons.Default.Campaign

        "branding" ->
            Icons.Default.Business

        "motion_graphics", "motion graphics" ->
            Icons.Default.Movie

        "content_creation", "content creation" ->
            Icons.Default.EditNote

        else ->
            Icons.Default.Build
    }
}