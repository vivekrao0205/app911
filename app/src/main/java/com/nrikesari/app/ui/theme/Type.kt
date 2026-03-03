package com.nrikesari.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/* FONT FAMILIES */

val ElegantSerif = FontFamily.Serif
val CleanSans = FontFamily.SansSerif

/* PROFESSIONAL TYPOGRAPHY */

val Typography = Typography(

    /* --- Hero / Splash / Big Headers --- */
    displayLarge = TextStyle(
        fontFamily = ElegantSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 54.sp,
        lineHeight = 62.sp,
        letterSpacing = (-0.5).sp
    ),

    displayMedium = TextStyle(
        fontFamily = ElegantSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 42.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.25).sp
    ),

    /* --- Main Screen Headings --- */
    headlineLarge = TextStyle(
        fontFamily = ElegantSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = ElegantSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),

    /* --- Section Titles --- */
    titleLarge = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.1.sp
    ),

    titleMedium = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),

    /* --- Body Text --- */
    bodyLarge = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    /* --- Buttons & Labels --- */
    labelLarge = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.8.sp   // slight tracking = premium look
    )
)