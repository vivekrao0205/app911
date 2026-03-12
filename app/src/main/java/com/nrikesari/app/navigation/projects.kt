package com.nrikesari.app.ui.screens.projects

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.nrikesari.app.navigation.Screen
import kotlinx.coroutines.delay

data class Project(
    val title: String,
    val description: String,
    val tech: String,
    val icon: ImageVector
)

@Composable
fun ProjectsScreen(navController: NavController) {


    val projects = listOf(
        Project(
            "Event Hub",
            "Android app for discovering and managing campus events.",
            "Kotlin • Compose • Firebase",
            Icons.Default.Devices
        ),
        Project(
            "Farmer Marketplace",
            "Marketplace connecting farmers and buyers directly.",
            "Android • API • Room DB",
            Icons.Default.ShoppingCart
        ),
        Project(
            "City Generator",
            "Procedural city generator using Blender Python.",
            "Python • Blender API",
            Icons.Default.Code
        ),
        Project(
            "Developer Portfolio",
            "Modern portfolio showcasing services and projects.",
            "Kotlin • Compose • Material3",
            Icons.Default.Web
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Our Projects",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Some of our development work and digital products.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(20.dp))
        }

        itemsIndexed(projects) { index, project ->
            AnimatedProjectCard(project, index)
        }

        /* -------- PORTFOLIO SECTION -------- */

        item {

            Spacer(Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                ),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Default.Workspaces,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Explore Our Full Portfolio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "View all completed work and case studies.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            navController.navigate(Screen.Portfolio.route)
                        }
                    ) {
                        Text("View Portfolio")
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }


}

@Composable
fun AnimatedProjectCard(project: Project, index: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(120L * index)
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = tween(300),
        label = "scale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { 80 }
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            ),
            color = MaterialTheme.colorScheme.surface
        ) {

            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {

                    Icon(
                        imageVector = project.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(26.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {

                    Text(
                        text = project.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = project.tech,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }


}
