package com.nrikesari.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.*
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.R
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.model.Testimonial
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

            StatsSection()

            Spacer(modifier = Modifier.height(60.dp))

            TestimonialSection()

            Spacer(modifier = Modifier.height(60.dp))

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

/* ---------------- TESTIMONIALS ---------------- */

@Composable
fun TestimonialSection() {
    val firebaseService = remember { FirebaseService() }
    var testimonials by remember { mutableStateOf<List<Testimonial>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val result = firebaseService.getTestimonials()
        if (result.isSuccess) {
            testimonials = result.getOrDefault(emptyList())
        }
        isLoading = false
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Client Feedback",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (testimonials.isEmpty()) {
             Text(
                "No reviews available yet.",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
             LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(testimonials) { testimonial ->
                    TestimonialCard(testimonial)
                }
            }
        }
    }
}

@Composable
fun TestimonialCard(testimonial: Testimonial) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.width(300.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(
                Icons.Default.FormatQuote, 
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = testimonial.feedback,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                minLines = 3,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = testimonial.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = testimonial.serviceType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Row {
                    val fullStars = testimonial.rating.toInt()
                    // Simplistic star rendering
                    repeat(fullStars) {
                        Icon(Icons.Default.Star, contentDescription = "Star", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
