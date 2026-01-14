package com.gear.hub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1F6BFF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCE9FF),
    onPrimaryContainer = Color(0xFF0B1B4A),
    secondary = Color(0xFF3B82F6),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0ECFF),
    onSecondaryContainer = Color(0xFF13203D),
    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Color(0xFFE5E7EB),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7BA7FF),
    onPrimary = Color(0xFF0A1433),
    primaryContainer = Color(0xFF233B74),
    onPrimaryContainer = Color(0xFFE6EEFF),
    secondary = Color(0xFF93C5FD),
    onSecondary = Color(0xFF0B1B4A),
    secondaryContainer = Color(0xFF1F2B47),
    onSecondaryContainer = Color(0xFFDCE9FF),
    background = Color(0xFF0B0F1A),
    onBackground = Color(0xFFF9FAFB),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF30363D),
    error = Color(0xFFF87171),
    onError = Color(0xFF0B0F1A)
)

@Composable
fun GearHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
