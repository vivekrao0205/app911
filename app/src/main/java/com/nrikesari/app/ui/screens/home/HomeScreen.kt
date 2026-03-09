package com.nrikesari.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.*
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.R
import com.nrikesari.app.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController) {

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        PremiumBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            AnimatedLogo()

            Spacer(modifier = Modifier.height(30.dp))

            AnimatedHeroText()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nrikesari is a creative digital agency building modern brands, applications and digital experiences.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            ActionButtons(navController)

            Spacer(modifier = Modifier.height(40.dp))

            StatsSection()

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

/* ---------------- PREMIUM BACKGROUND ---------------- */

@Composable
fun PremiumBackground() {

    val transition = rememberInfiniteTransition(label = "bg")

    val glowScale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            tween(4000),
            RepeatMode.Reverse
        ),
        label = "glow"
    )

    val alpha by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.16f,
        animationSpec = infiniteRepeatable(
            tween(4000),
            RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(glowScale)
            .alpha(alpha)
            .blur(120.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
    )
}

/* ---------------- LOGO ---------------- */

@Composable
fun AnimatedLogo() {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f)
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ) {}

            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/* ---------------- HERO TEXT ---------------- */

@Composable
fun AnimatedHeroText() {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(350)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { 60 }
    ) {

        Text(
            buildAnnotatedString {

                append("We Build ")

                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) { append("Brands") }

                append("\nWe Create ")

                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) { append("Digital Experiences") }
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
    }
}

/* ---------------- BUTTONS ---------------- */

@Composable
fun ActionButtons(navController: NavController) {

    Button(
        onClick = { navController.navigate(Screen.Contact.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("Start a Project", fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
        onClick = { navController.navigate(Screen.Services.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("View Services")
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
        onClick = { navController.navigate(Screen.Portfolio.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("View Portfolio")
    }
}

/* ---------------- STATS ---------------- */

@Composable
fun StatsSection() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        StatItem(Icons.Default.Work,"10+","Projects")
        StatItem(Icons.Default.Groups,"20+","Clients")
        StatItem(Icons.Default.Timeline,">1","Years")
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}