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
    onPrimary = PureWhite,

    secondary = SoftBeige,
    onSecondary = DarkBackground,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkOnSurface,

    surfaceTint = DeepMaroon
)

/* -------------------- */
/* LIGHT COLOR SCHEME */
/* -------------------- */

private val LightColorScheme = lightColorScheme(

    primary = DeepMaroon,
    onPrimary = PureWhite,

    secondary = SoftBeige,
    onSecondary = DarkCharcoal,

    background = WarmIvory,
    onBackground = DarkCharcoal,

    surface = PureWhite,
    onSurface = DarkCharcoal,

    surfaceVariant = SoftBeige.copy(alpha = 0.2f),
    onSurfaceVariant = DarkCharcoal,

    surfaceTint = DeepMaroon
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
            if (darkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Status bar color
            window.statusBarColor = colorScheme.background.toArgb()

            // Navigation bar color
            window.navigationBarColor = colorScheme.background.toArgb()

            // Light / Dark icons
            WindowCompat.getInsetsController(window, view)
                .apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}