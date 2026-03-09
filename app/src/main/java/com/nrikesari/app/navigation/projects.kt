package com.nrikesari.app.ui.screens.projects

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay

data class Project(
    val title: String,
    val description: String,
    val tech: String,
    val icon: ImageVector
)

@Composable
fun ProjectsScreen() {

    val projects = listOf(
        Project(
            "Campus Event Hub",
            "Android app for discovering and managing campus events with realtime updates.",
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
            "Procedural city generator built using Blender Python scripting.",
            "Python • Blender API",
            Icons.Default.Code
        ),
        Project(
            "Developer Portfolio",
            "Modern portfolio app showcasing projects and services.",
            "Kotlin • Compose • Material3",
            Icons.Default.Web
        )
    )

    val transition = rememberInfiniteTransition()

    val offset by transition.animateFloat(
        initialValue = -200f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing)
        ),
        label = "gradient"
    )

    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ),
        start = Offset(0f, offset),
        end = Offset(offset, 0f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        FloatingBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {

                Spacer(modifier = Modifier.height(40.dp))

                AnimatedHeader()

                Spacer(modifier = Modifier.height(30.dp))
            }

            itemsIndexed(projects) { index, project ->
                AnimatedProjectCard(project, index)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun AnimatedHeader() {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { -100 }
    ) {

        Column {

            Text(
                text = "Our Projects",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Some of our best development work and products.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AnimatedProjectCard(project: Project, index: Int) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200L * index)
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "scale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { 200 }
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .shadow(12.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Row(
                modifier = Modifier.padding(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {

                    Icon(
                        imageVector = project.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column {

                    Text(
                        text = project.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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

@Composable
fun FloatingBackground() {

    val transition = rememberInfiniteTransition()

    val x by transition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            tween(14000),
            RepeatMode.Reverse
        ),
        label = "x"
    )

    val y by transition.animateFloat(
        initialValue = -200f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            tween(16000),
            RepeatMode.Reverse
        ),
        label = "y"
    )

    Box(
        modifier = Modifier
            .offset(x.dp, y.dp)
            .size(280.dp)
            .blur(120.dp)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                CircleShape
            )
    )

    Box(
        modifier = Modifier
            .offset((-x).dp, (y / 2).dp)
            .size(250.dp)
            .blur(120.dp)
            .background(
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                CircleShape
            )
    )
}