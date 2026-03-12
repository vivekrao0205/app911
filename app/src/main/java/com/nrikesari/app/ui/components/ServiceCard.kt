package com.nrikesari.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        ),
        color = colorScheme.surface
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = colorScheme.primary.copy(alpha = 0.08f)
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = service.title,
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
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
