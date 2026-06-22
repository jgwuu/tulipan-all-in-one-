package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalThemeIsDark = staticCompositionLocalOf { false }

// 0: Amarillo Oro (Original)
private val Theme0Light = lightColorScheme(
    primary = TulipYellowPrimary,
    secondary = TulipYellowSecondary,
    tertiary = TulipYellowTertiary,
    background = TulipCreamBackground,
    surface = TulipCreamSurface,
    onPrimary = Color.White,
    onSecondary = SoftCharcoalBrown,
    onTertiary = Color.White,
    onBackground = SoftCharcoalBrown,
    onSurface = SoftCharcoalBrown,
    primaryContainer = Color(0xFFFEF9C3),
    secondaryContainer = Color(0xFFFEF08A)
)

private val Theme0Dark = darkColorScheme(
    primary = TulipDarkPrimary,
    secondary = TulipDarkSecondary,
    tertiary = TulipDarkTertiary,
    background = TulipDarkBackground,
    surface = TulipDarkSurface,
    onPrimary = Color(0xFF1C1A16),
    onSecondary = Color(0xFF1C1A16),
    onTertiary = Color(0xFFFFFEFC),
    onBackground = TulipDarkOnBackground,
    onSurface = TulipDarkOnBackground,
    primaryContainer = Color(0xFF4B3C14),
    secondaryContainer = Color(0xFF3E2D1A)
)

// 1: Gris Neutro (Austero)
private val Theme1Light = lightColorScheme(
    primary = NeutralLightPrimary,
    secondary = NeutralLightSecondary,
    tertiary = NeutralLightTertiary,
    background = NeutralLightBackground,
    surface = NeutralLightSurface,
    onPrimary = Color.White,
    onSecondary = NeutralTextColor,
    onTertiary = Color.White,
    onBackground = NeutralTextColor,
    onSurface = NeutralTextColor,
    primaryContainer = Color(0xFFF3F4F6),
    secondaryContainer = Color(0xFFE5E7EB)
)

private val Theme1Dark = darkColorScheme(
    primary = NeutralDarkPrimary,
    secondary = NeutralDarkSecondary,
    tertiary = NeutralDarkTertiary,
    background = NeutralDarkBackground,
    surface = NeutralDarkSurface,
    onPrimary = Color(0xFF111827),
    onSecondary = Color(0xFF111827),
    onTertiary = Color(0xFFF9FAFB),
    onBackground = NeutralDarkOnBackground,
    onSurface = NeutralDarkOnBackground,
    primaryContainer = Color(0xFF374151),
    secondaryContainer = Color(0xFF1F2937)
)

// 2: Azul Océano (Masculino/Neutral)
private val Theme2Light = lightColorScheme(
    primary = BlueLightPrimary,
    secondary = BlueLightSecondary,
    tertiary = BlueLightTertiary,
    background = BlueLightBackground,
    surface = BlueLightSurface,
    onPrimary = Color.White,
    onSecondary = BlueTextColor,
    onTertiary = Color.White,
    onBackground = BlueTextColor,
    onSurface = BlueTextColor,
    primaryContainer = Color(0xFFEFF6FF),
    secondaryContainer = Color(0xFFDBEAFE)
)

private val Theme2Dark = darkColorScheme(
    primary = BlueDarkPrimary,
    secondary = BlueDarkSecondary,
    tertiary = BlueDarkTertiary,
    background = BlueDarkBackground,
    surface = BlueDarkSurface,
    onPrimary = Color(0xFF0F172A),
    onSecondary = Color(0xFF0F172A),
    onTertiary = Color(0xFFF8FAFC),
    onBackground = BlueDarkOnBackground,
    onSurface = BlueDarkOnBackground,
    primaryContainer = Color(0xFF1E3A8A),
    secondaryContainer = Color(0xFF1E293B)
)

// 3: Verde Bosque (Fresco/Neutral)
private val Theme3Light = lightColorScheme(
    primary = GreenLightPrimary,
    secondary = GreenLightSecondary,
    tertiary = GreenLightTertiary,
    background = GreenLightBackground,
    surface = GreenLightSurface,
    onPrimary = Color.White,
    onSecondary = GreenTextColor,
    onTertiary = Color.White,
    onBackground = GreenTextColor,
    onSurface = GreenTextColor,
    primaryContainer = Color(0xFFECFDF5),
    secondaryContainer = Color(0xFFD1FAE5)
)

private val Theme3Dark = darkColorScheme(
    primary = GreenDarkPrimary,
    secondary = GreenDarkSecondary,
    tertiary = GreenDarkTertiary,
    background = GreenDarkBackground,
    surface = GreenDarkSurface,
    onPrimary = Color(0xFF064E3B),
    onSecondary = Color(0xFF064E3B),
    onTertiary = Color(0xFFECFDF5),
    onBackground = GreenDarkOnBackground,
    onSurface = GreenDarkOnBackground,
    primaryContainer = Color(0xFF065F46),
    secondaryContainer = Color(0xFF0A5C46)
)

// 4: Rosa Pastel (Femenino)
private val Theme4Light = lightColorScheme(
    primary = PinkLightPrimary,
    secondary = PinkLightSecondary,
    tertiary = PinkLightTertiary,
    background = PinkLightBackground,
    surface = PinkLightSurface,
    onPrimary = Color.White,
    onSecondary = PinkTextColor,
    onTertiary = Color.White,
    onBackground = PinkTextColor,
    onSurface = PinkTextColor,
    primaryContainer = Color(0xFFFFF1F2),
    secondaryContainer = Color(0xFFFFE4E6)
)

private val Theme4Dark = darkColorScheme(
    primary = PinkDarkPrimary,
    secondary = PinkDarkSecondary,
    tertiary = PinkDarkTertiary,
    background = PinkDarkBackground,
    surface = PinkDarkSurface,
    onPrimary = Color(0xFF200C12),
    onSecondary = Color(0xFF200C12),
    onTertiary = Color(0xFFFFF1F2),
    onBackground = PinkDarkOnBackground,
    onSurface = PinkDarkOnBackground,
    primaryContainer = Color(0xFF9D174D),
    secondaryContainer = Color(0xFF28141A)
)

// 5: Púrpura Imperial (Elegante/Moderno)
private val Theme5Light = lightColorScheme(
    primary = PurpleLightPrimary,
    secondary = PurpleLightSecondary,
    tertiary = PurpleLightTertiary,
    background = PurpleLightBackground,
    surface = PurpleLightSurface,
    onPrimary = Color.White,
    onSecondary = PurpleTextColor,
    onTertiary = Color.White,
    onBackground = PurpleTextColor,
    onSurface = PurpleTextColor,
    primaryContainer = Color(0xFFF5F3FF),
    secondaryContainer = Color(0xFFEDE9FE)
)

private val Theme5Dark = darkColorScheme(
    primary = PurpleDarkPrimary,
    secondary = PurpleDarkSecondary,
    tertiary = PurpleDarkTertiary,
    background = PurpleDarkBackground,
    surface = PurpleDarkSurface,
    onPrimary = Color(0xFF0F0B1E),
    onSecondary = Color(0xFF0F0B1E),
    onTertiary = Color(0xFFFAF5FF),
    onBackground = PurpleDarkOnBackground,
    onSurface = PurpleDarkOnBackground,
    primaryContainer = Color(0xFF4C1D95),
    secondaryContainer = Color(0xFF1D142E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeId: Int = 0,
    languageId: Int = 0,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeId) {
        1 -> if (darkTheme) Theme1Dark else Theme1Light
        2 -> if (darkTheme) Theme2Dark else Theme2Light
        3 -> if (darkTheme) Theme3Dark else Theme3Light
        4 -> if (darkTheme) Theme4Dark else Theme4Light
        5 -> if (darkTheme) Theme5Dark else Theme5Light
        else -> if (darkTheme) Theme0Dark else Theme0Light // 0: Clásico Tulipán
    }

    CompositionLocalProvider(
        LocalThemeIsDark provides darkTheme,
        LocalLanguage provides languageId
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
