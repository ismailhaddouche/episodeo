package com.haddouche.episodeo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = Purple80,
    onPrimary        = OnPrimaryDark,
    secondary        = PurpleGrey80,
    onSecondary      = OnSecondaryDark,
    tertiary         = Pink80,
    onTertiary       = OnTertiaryDark,
    background       = BackgroundDark,
    onBackground     = OnBackgroundDark,
    surface          = SurfaceDark,
    onSurface        = OnSurfaceDark,
    surfaceVariant   = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error            = Color_ErrorDark,
    onError          = OnErrorDark,
)

private val LightColorScheme = lightColorScheme(
    primary          = Purple40,
    onPrimary        = OnPrimaryLight,
    secondary        = PurpleGrey40,
    onSecondary      = OnSecondaryLight,
    tertiary         = Pink40,
    onTertiary       = OnTertiaryLight,
    background       = BackgroundLight,
    onBackground     = OnBackgroundLight,
    surface          = SurfaceLight,
    onSurface        = OnSurfaceLight,
    surfaceVariant   = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error            = Color_ErrorLight,
    onError          = OnErrorLight,
)

@Composable
fun EpisodeoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
