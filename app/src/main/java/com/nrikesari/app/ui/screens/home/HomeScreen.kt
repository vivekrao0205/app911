package com.nrikesari.app.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.R
import com.nrikesari.app.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    val transition = rememberInfiniteTransition()

    val offsetX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val offsetY by transition.animateFloat(
        initialValue = -200f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {

        //  Adaptive Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        )

        // Jelly Blob 1 (theme-based)
        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size(300.dp)
                .blur(150.dp)
                .background(
                    colorScheme.primary.copy(alpha = 0.15f),
                    shape = CircleShape
                )
        )

        // Jelly Blob 2
        Box(
            modifier = Modifier
                .offset(x = (-offsetX).dp, y = (offsetY / 2).dp)
                .size(250.dp)
                .blur(150.dp)
                .background(
                    colorScheme.secondary.copy(alpha = 0.18f),
                    shape = CircleShape
                )
        )

        //  Glass  Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(
                    colorScheme.surface.copy(alpha = 0.6f)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        colorScheme.surface.copy(alpha = 0.8f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "Nrikesari Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            //  Highlighted Hero Text
            Text(
                buildAnnotatedString {
                    append("We Build ")
                    withStyle(
                        style = SpanStyle(
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    ) { append("Brands") }

                    append(".\nWe Create ")

                    withStyle(
                        style = SpanStyle(
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    ) { append("Digital Experiences") }
                },
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nrikesari is a creative digital agency specializing in branding, digital experiences, technology, and performance marketing.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { navController.navigate(Screen.Contact.route) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                )
            ) {
                Text("Start a Project")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate(Screen.Services.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Services")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate(Screen.Portfolio.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Portfolio")
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}