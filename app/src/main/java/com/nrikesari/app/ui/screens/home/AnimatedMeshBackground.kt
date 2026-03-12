import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedMeshBackground() {

    val transition = rememberInfiniteTransition(label = "")

    /* ---------- ANIMATION MOVEMENTS ---------- */

    val moveX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            tween(18000, easing = LinearEasing),
            RepeatMode.Reverse
        ), label = ""
    )

    val moveY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            tween(22000, easing = LinearEasing),
            RepeatMode.Reverse
        ), label = ""
    )

    val moveX2 by transition.animateFloat(
        initialValue = 600f,
        targetValue = -300f,
        animationSpec = infiniteRepeatable(
            tween(26000, easing = LinearEasing),
            RepeatMode.Reverse
        ), label = ""
    )

    /* ---------- BASE GRADIENT ---------- */

    val baseGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF3EFEA),
            Color(0xFFEDE6DF),
            Color(0xFFE1D8CF)
        ),
        start = Offset(moveX, moveY),
        end = Offset(0f, 1200f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(baseGradient)
    ) {

        /* ---------- LIGHT GLOW LAYER ---------- */

        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(160.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF8A65).copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = Offset(moveX, moveY),
                        radius = 900f
                    )
                )
        )

        /* ---------- SECOND GLOW ---------- */

        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(140.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF7043).copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(moveX2, 400f),
                        radius = 800f
                    )
                )
        )
    }
}