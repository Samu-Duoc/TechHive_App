package com.example.techhive_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TechHiveColors.YellowPrimary,
    secondary = TechHiveColors.BrownSecondary,
    background = TechHiveColors.BackgroundWhite,
    surface = TechHiveColors.CardGray,
    onPrimary = TechHiveColors.TextDark,
    onSecondary = TechHiveColors.TextDark,
    onBackground = TechHiveColors.TextDark,
    onSurface = TechHiveColors.TextDark
)

private val DarkColorScheme = darkColorScheme(
    primary = TechHiveColors.YellowPrimary,
    secondary = TechHiveColors.BrownSecondary,
    background = Color(0xFF2E3948),
    surface = Color(0xFF111013),
    onPrimary = TechHiveColors.BackgroundWhite,
    onSecondary = TechHiveColors.BackgroundWhite,
    onBackground = TechHiveColors.BackgroundWhite,
    onSurface = TechHiveColors.BackgroundWhite
)

@Composable
fun TechHive_AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
