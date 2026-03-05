import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedMeshBackground() {

    val transition = rememberInfiniteTransition()

    val animatedX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 20000,   // slower = luxury feel
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF2EEEB),   // Cream
            Color(0xFFE6DFD8),   // Soft Beige
            Color(0xFFD8CFC6)    // Warm Sand
        ),
        start = Offset(animatedX, 0f),
        end = Offset(0f, 1200f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    )
}