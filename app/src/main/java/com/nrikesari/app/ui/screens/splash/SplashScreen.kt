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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nrikesari.app.R
import com.nrikesari.app.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val textDark = Color(0xFF3E3E3E)
    val subText = Color(0xFF6F6F6F)
    val circleColor = Color(0xFFCFC6BD)

    var startAnimation by remember { mutableStateOf(false) }

    /* ---------- MAIN SCALE ---------- */

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

    /* ---------- FLOAT ANIMATION ---------- */

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    /* ---------- PULSE GLOW ---------- */

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    /* ---------- BACKGROUND GRADIENT ---------- */

    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF2EDEA),
            Color(0xFFF7F4F2)
        )
    )

    /* ---------- START ---------- */

    LaunchedEffect(Unit) {

        startAnimation = true

        delay(2200)

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

            /* ---------- LOGO ---------- */

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulse)
                    .background(circleColor.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = R.mipmap.icon),
                    contentDescription = "Nrikesari",
                    modifier = Modifier
                        .size(190.dp)
                        .offset(y = floatAnim.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            /* ---------- TITLE ---------- */

            Text(
                text = "N R I K E S A R I",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 6.sp,
                fontFamily = FontFamily.Serif,
                color = textDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            /* ---------- SUBTITLE ---------- */

            Text(
                text = "Media & Technology",
                fontSize = 14.sp,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.SansSerif,
                color = subText
            )
        }
    }
}