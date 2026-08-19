package com.chartmann1590.verselight.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Ink = Color(0xFF173A38)
val Forest = Color(0xFF315A4E)
val Parchment = Color(0xFFF8F1E4)
val WarmWhite = Color(0xFFFFFBF4)
val Gold = Color(0xFFB88746)
val Burgundy = Color(0xFF733C46)

private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = WarmWhite,
    primaryContainer = Color(0xFFD2E8DE),
    onPrimaryContainer = Color(0xFF0B2928),
    secondary = Burgundy,
    secondaryContainer = Color(0xFFF2D8DC),
    tertiary = Gold,
    background = WarmWhite,
    surface = Parchment,
    surfaceContainer = Color(0xFFF2E8D7),
    onSurface = Color(0xFF28251F),
    outline = Color(0xFF817669),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA9D3C4),
    onPrimary = Color(0xFF063730),
    primaryContainer = Color(0xFF234E45),
    secondary = Color(0xFFE7BBC2),
    tertiary = Color(0xFFE4BD7E),
    background = Color(0xFF121A19),
    surface = Color(0xFF1C2624),
    surfaceContainer = Color(0xFF25312E),
    onSurface = Color(0xFFF1EBDF),
    outline = Color(0xFF9E9488),
)

@Composable
fun VerseLightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = MaterialTheme.typography.copy(
            displaySmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 34.sp, lineHeight = 42.sp),
            headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 28.sp),
            titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
            bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 24.sp),
        ),
        content = content,
    )
}

