package com.nrikesari.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/* -------------------- */
/* THEME COLOR SWITCH */
/* -------------------- */

fun getPrimaryColor(theme: String): Color {
    return when (theme) {
        "Slate" -> ThemeSlate
        "Indigo" -> ThemeIndigo
        "Emerald" -> ThemeEmerald
        "Amber" -> ThemeAmber
        "Rose" -> ThemeRose
        else -> DeepMaroon
    }
}

/* -------------------- */
/* DARK COLOR SCHEME */
/* -------------------- */

fun darkScheme(primaryColor: Color) = darkColorScheme(
    primary = primaryColor,
    onPrimary = PureWhite,

    secondary = SoftBeige,
    onSecondary = DarkBackground,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkOnSurface,

    surfaceTint = primaryColor
)

/* -------------------- */
/* LIGHT COLOR SCHEME */
/* -------------------- */

fun lightScheme(primaryColor: Color) = lightColorScheme(
    primary = primaryColor,
    onPrimary = PureWhite,

    secondary = SoftBeige,
    onSecondary = DarkCharcoal,

    background = WarmIvory,
    onBackground = DarkCharcoal,

    surface = PureWhite,
    onSurface = DarkCharcoal,

    surfaceVariant = SoftBeige.copy(alpha = 0.2f),
    onSurfaceVariant = DarkCharcoal,

    surfaceTint = primaryColor
)

/* -------------------- */
/* MAIN THEME */
/* -------------------- */

@Composable
fun NrikesariTheme(
    themeColor: String = "Default",
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,   // 🔴 FIXED HERE
    content: @Composable () -> Unit
) {

    val context = LocalContext.current
    val view = LocalView.current

    val primaryColor = getPrimaryColor(themeColor)

    val colorScheme = when {

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme(primaryColor)

        else -> lightScheme(primaryColor)
    }

    if (!view.isInEditMode) {
        SideEffect {

            val window = (view.context as Activity).window

            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
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