package com.nrikesari.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/* FONT FAMILIES */

val ElegantSerif = FontFamily.Serif
val CleanSans = FontFamily.SansSerif

/* TYPOGRAPHY SYSTEM */

val Typography = Typography(

    /* -------- HERO / SPLASH -------- */

    displayLarge = TextStyle(
        fontFamily = ElegantSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.6).sp
    ),

    displayMedium = TextStyle(
        fontFamily = ElegantSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.4).sp
    ),

    /* -------- MAIN HEADLINES -------- */

    headlineLarge = TextStyle(
        fontFamily = ElegantSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = ElegantSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),

    headlineSmall = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),

    /* -------- SECTION TITLES -------- */

    titleLarge = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.2.sp
    ),

    titleMedium = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),

    titleSmall = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),

    /* -------- BODY TEXT -------- */

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

    bodySmall = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),

    /* -------- BUTTONS -------- */

    labelLarge = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.sp  // strong premium tracking
    ),

    labelMedium = TextStyle(
        fontFamily = CleanSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.8.sp
    )
)