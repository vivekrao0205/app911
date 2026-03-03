package com.nrikesari.app.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip

@Composable
fun SplashScreen(navController: NavController) {

    val backgroundColor = Color(0xFFF2EDEA)
    val textDark = Color(0xFF3E3E3E)
    val subText = Color(0xFF6F6F6F)
    val circleColor = Color(0xFFCFC6BD).copy(alpha = 0.25f)

    var startAnimation by remember { mutableStateOf(false) }

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.9f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = ""
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000),
        label = ""
    )

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
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(circleColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "Lotus",
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "N R I K E S A R I",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 6.sp,
                fontFamily = FontFamily.Serif,
                color = textDark
            )

            Spacer(modifier = Modifier.height(10.dp))

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