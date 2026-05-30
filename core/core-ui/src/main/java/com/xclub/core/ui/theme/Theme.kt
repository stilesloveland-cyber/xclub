package com.xclub.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = XclubPrimary, onPrimary = XclubOnPrimary,
    primaryContainer = XclubPrimaryContainer, onPrimaryContainer = XclubOnPrimaryContainer,
    secondary = XclubSecondary, onSecondary = XclubOnSecondary,
    secondaryContainer = XclubSecondaryContainer, onSecondaryContainer = XclubOnSecondaryContainer,
    background = XclubBackground, onBackground = XclubOnBackground,
    surface = XclubSurface, onSurface = XclubOnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = XclubDarkPrimary, onPrimary = XclubDarkOnPrimary,
    primaryContainer = XclubDarkPrimaryContainer, onPrimaryContainer = XclubDarkOnPrimaryContainer,
    background = XclubDarkBackground, onBackground = XclubDarkOnBackground,
    surface = XclubDarkSurface, onSurface = XclubDarkOnSurface
)

@Composable
fun XclubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = XclubTypography, content = content)
}
