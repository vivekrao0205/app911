package com.nrikesari.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.nrikesari.app.R
import com.nrikesari.app.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val textDark = Color(0xFF1C1C1C)   // deeper premium black
    val subText = Color(0xFF7A7A7A)    // softer grey
    val circleColor = Color(0xFFFFE0D6) // warm soft glow circle

    var startAnimation by remember { mutableStateOf(false) }

    /* ---------- ENTRY SCALE ---------- */

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = ""
    )

    /* ---------- FADE ---------- */

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200),
        label = ""
    )

    /* ---------- FLOAT + PULSE ---------- */

    val infinite = rememberInfiniteTransition(label = "")

    val floatAnim by infinite.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            tween(2600, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = ""
    )

    val pulse by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = ""
    )

    val glow by infinite.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            tween(2000),
            RepeatMode.Reverse
        ),
        label = ""
    )

    /* ---------- BACKGROUND ---------- */

    val background = Brush.verticalGradient(
        listOf(
            Color(0xFFFDFDFD),
            Color(0xFFF7F4F2)
        )
    )

    /* ---------- NAVIGATION ---------- */

    LaunchedEffect(Unit) {

        startAnimation = true

        delay(2000)

        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
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
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {

            /* ---------- LOGO AREA ---------- */

            Box(
                modifier = Modifier.size(170.dp),
                contentAlignment = Alignment.Center
            ) {

                /* Glow layer */

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .scale(1.25f)
                        .alpha(glow)
                        .blur(50.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    circleColor,
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                /* Circle container */

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulse)
                        .clip(CircleShape)
                        .background(circleColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(R.drawable.icon_round),
                        contentDescription = "Nrikesari",
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .scale(1.55f)
                            .offset(y = floatAnim.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            /* ---------- TITLE ---------- */

            Text(
                text = "N R I K E S A R I",
                fontSize = 30.sp,
                letterSpacing = 7.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Serif,
                color = textDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            /* ---------- SUBTITLE ---------- */

            Text(
                text = "Media & Technology",
                fontSize = 14.sp,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif,
                color = subText
            )
        }
    }
}