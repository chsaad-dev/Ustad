package com.ustad.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val UstadLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor,
    error = Error,
    onError = Color.White
)

val UstadWorkerDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = SecondaryDark,
    onBackground = Color.White,
    surface = SecondaryDark,
    onSurface = Color.White
)

@Composable
fun UstadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Per design.md Section 3: Do not use dynamicColorScheme.
    // Ustad uses fixed light scheme for app-wide UI.
    val colorScheme = UstadLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = UstadTypography,
        shapes = UstadShapes,
        content = content
    )
}
