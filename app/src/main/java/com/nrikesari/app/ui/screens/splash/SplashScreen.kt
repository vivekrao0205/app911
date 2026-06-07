package com.nrikesari.app.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.nrikesari.app.R
import com.nrikesari.app.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val textDark = Color(0xFF1C1C1C)
    val subText = Color(0xFF7A7A7A)
    val circleColor = Color(0xFFFFE0D6)

    var phase by remember { mutableStateOf(1) }

    val logoScale by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0.5f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0f,
        animationSpec = tween(800),
        label = "logoAlpha"
    )

    val nameAlpha by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = tween(800),
        label = "nameAlpha"
    )

    val nameOffset by animateDpAsState(
        targetValue = if (phase >= 2) 0.dp else 20.dp,
        animationSpec = tween(800, easing = EaseOutBack),
        label = "nameOffset"
    )

    val loadingAlpha by animateFloatAsState(
        targetValue = if (phase >= 3) 1f else 0f,
        animationSpec = tween(600),
        label = "loadingAlpha"
    )

    val infinite = rememberInfiniteTransition(label = "pulse")
    val floatAnim by infinite.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "logoFloat"
    )

    val background = Brush.verticalGradient(
        listOf(
            Color(0xFFFDFDFD),
            Color(0xFFF7F4F2)
        )
    )

    LaunchedEffect(Unit) {
        // Phase 1: Logo enters
        delay(600)
        
        // Phase 2: Name and tagline enter
        phase = 2
        delay(1000)
        
        // Phase 3: Premium loading spinner fades in
        phase = 3
        delay(1000)
        
        // Phase 4: Route navigation check
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            /* ---------- LOGO AREA (Phase 1) ---------- */
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .blur(30.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(circleColor, Color.Transparent)
                            ),
                            CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(circleColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_round),
                        contentDescription = "Nrikesari Logo",
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .scale(1.4f)
                            .offset(y = floatAnim.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            /* ---------- AGENCY NAME & TAGLINE (Phase 2) ---------- */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = nameOffset)
                    .alpha(nameAlpha)
            ) {
                Text(
                    text = "N R I K E S A R I",
                    fontSize = 28.sp,
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = textDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Transforming Ideas Into Products",
                    fontSize = 13.sp,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = subText
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            /* ---------- PREMIUM LOADING EXPERIENCE (Phase 3) ---------- */
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .alpha(loadingAlpha),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}