/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import cu.ashlydev.birthdays.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Pink40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Pink80,
    onPrimaryContainer = PinkGrey40,
    secondary = Peach40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Peach80,
    tertiary = Yellow40,
    onTertiary = androidx.compose.ui.graphics.Color.Black,
    tertiaryContainer = Yellow80,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = PinkGrey80
)

private val DarkColors = darkColorScheme(
    primary = Pink80,
    onPrimary = PinkGrey40,
    primaryContainer = PinkGrey40,
    onPrimaryContainer = Pink80,
    secondary = Peach80,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = Peach40,
    tertiary = Yellow80,
    onTertiary = androidx.compose.ui.graphics.Color.Black,
    tertiaryContainer = Yellow40,
    background = DarkBackground,
    surface = DarkSurface
)

@Composable
fun CumpleanosTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}