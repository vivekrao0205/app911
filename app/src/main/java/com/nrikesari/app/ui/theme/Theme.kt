package com.nrikesari.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/* -------------------- */
/* DARK COLOR SCHEME */
/* -------------------- */

private val DarkColorScheme = darkColorScheme(

    primary = DeepMaroon,
    onPrimary = WarmIvory,

    secondary = SoftBeige,
    onSecondary = DarkBackground,

    tertiary = DeepMaroon,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkOnSurface
)

/* -------------------- */
/* LIGHT COLOR SCHEME */
/* -------------------- */

private val LightColorScheme = lightColorScheme(

    primary = DeepMaroon,
    onPrimary = White,

    secondary = SoftBeige,
    onSecondary = DarkCharcoal,

    tertiary = DeepMaroon,

    background = WarmIvory,
    onBackground = DarkCharcoal,

    surface = White,
    onSurface = DarkCharcoal,

    surfaceVariant = SoftBeige.copy(alpha = 0.15f)
)

/* -------------------- */
/* MAIN THEME */
/* -------------------- */

@Composable
fun NrikesariTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}