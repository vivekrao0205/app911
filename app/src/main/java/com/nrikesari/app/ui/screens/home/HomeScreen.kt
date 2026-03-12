package com.nrikesari.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.R
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.model.Testimonial
import com.nrikesari.app.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController, userViewModel: UserViewModel) {

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
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))

            ActionButtons(navController)

            Spacer(modifier = Modifier.height(40.dp))

            StatsSection()

            Spacer(modifier = Modifier.height(60.dp))

            TestimonialSection(navController, userViewModel)

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/* ---------------- PREMIUM BACKGROUND ---------------- */

@Composable
fun PremiumBackground() {

    val transition = rememberInfiniteTransition(label = "bg")

    val offsetX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val offsetY by transition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background
                    ),
                    start = Offset(offsetX, offsetY),
                    end = Offset(offsetX + 800f, offsetY + 1200f)
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
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape) // ensures whole container is circle
        ) {

            Surface(
                modifier = Modifier.matchParentSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ) {}

            Image(
                painter = painterResource(id = R.drawable.icon2_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape) // clip image to circle
                    .scale(1.6f),      // zoom image inside circle
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

        Icon(icon,null,tint = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(4.dp))

        Text(value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)

        Text(label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
    }
}

/* ---------------- TESTIMONIALS ---------------- */

@Composable
fun TestimonialSection(navController: NavController, userViewModel: UserViewModel) {

    val reviews by userViewModel.reviews.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadTestimonials()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Client Feedback",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate(Screen.WriteReview.route) },
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(Icons.Default.Star,null,modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Write a Review")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (reviews.isEmpty()) {

            Text(
                "No reviews yet.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

        } else {

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                items(reviews) { testimonial ->
                    TestimonialCard(testimonial)
                }
            }
        }
    }
}

/* ---------------- TESTIMONIAL CARD ---------------- */

@Composable
fun TestimonialCard(testimonial: Testimonial) {

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                        testimonial.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        testimonial.serviceType,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Row {

                    repeat(testimonial.rating.toInt()) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}