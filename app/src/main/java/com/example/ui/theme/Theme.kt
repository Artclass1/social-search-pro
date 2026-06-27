package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val MinimalBlackColorScheme = darkColorScheme(
    primary = AccentWhite,
    onPrimary = PureBlack,
    primaryContainer = MediumGray,
    onPrimaryContainer = AccentWhite,
    secondary = SilverGray,
    onSecondary = PureBlack,
    background = PureBlack,
    onBackground = AccentWhite,
    surface = DarkGraySurface,
    onSurface = AccentWhite,
    surfaceVariant = MediumGray,
    onSurfaceVariant = MutedText,
    outline = LightGrayBorder,
    outlineVariant = MediumGray,
    error = SentimentNegative,
    onError = PureBlack
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark/black theme by default
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve pure black aesthetics
    content: @Composable () -> Unit,
) {
    // Force the custom gorgeous minimal black color scheme as requested by the user
    val colorScheme = MinimalBlackColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
