package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = GlassDarkBackground,
    primaryContainer = SecondaryNavy,
    onPrimaryContainer = Color.White,
    secondary = VibrantPurple,
    onSecondary = Color.White,
    secondaryContainer = DeepIndigo,
    onSecondaryContainer = Color.White,
    tertiary = EmeraldTeal,
    onTertiary = Color.White,
    background = GlassDarkBackground,
    onBackground = TextPrimary,
    surface = GlassSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderStroke,
    outlineVariant = GlassBorderSubtle
)

private val LightColorScheme = darkColorScheme( // Defaulting to high-contrast dark glass for authentic Liquid Glass aesthetic
    primary = ElectricBlue,
    onPrimary = GlassDarkBackground,
    primaryContainer = SecondaryNavy,
    onPrimaryContainer = Color.White,
    secondary = VibrantPurple,
    onSecondary = Color.White,
    surface = GlassSurfaceDark,
    background = GlassDarkBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun GovIntelTheme(
    darkTheme: Boolean = true, // Default to futuristic Dark Liquid Glass aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    GovIntelTheme(darkTheme = true, content = content)
}

