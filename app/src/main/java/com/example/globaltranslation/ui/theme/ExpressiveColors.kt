package com.example.globaltranslation.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Material 3 Expressive Color Palette - Lavender/Purple Theme
// Inspired by Google Translate's soft, modern aesthetic

// Primary Colors - Lavender/Purple
val LightLavender = Color(0xFFE8DEF8)
val MediumPurple = Color(0xFF6750A4)
val DarkPurple = Color(0xFF4A3D7A)
val VeryLightPurple = Color(0xFFF6EDFF)

// Accent & Secondary
val AccentPurple = Color(0xFF7F67BE)
val SoftPink = Color(0xFFFFD7F0)
val MediumPink = Color(0xFFE91E8C)

// Surface & Background
val SoftWhite = Color(0xFFFFFBFE)
val LightGray = Color(0xFFF5F5F5)
val MediumGray = Color(0xFFE0E0E0)

// Text Colors
val OnPrimary = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF1C1B1F)
val OnSurfaceVariant = Color(0xFF49454F)

// Error Colors
val ErrorRed = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)

// Material 3 Light Color Scheme
val ExpressiveLightColors = lightColorScheme(
    primary = MediumPurple,
    onPrimary = OnPrimary,
    primaryContainer = LightLavender,
    onPrimaryContainer = DarkPurple,
    
    secondary = AccentPurple,
    onSecondary = OnPrimary,
    secondaryContainer = SoftPink,
    onSecondaryContainer = DarkPurple,
    
    tertiary = MediumPink,
    onTertiary = OnPrimary,
    tertiaryContainer = SoftPink,
    onTertiaryContainer = DarkPurple,
    
    error = ErrorRed,
    onError = OnPrimary,
    errorContainer = ErrorContainer,
    onErrorContainer = DarkPurple,
    
    background = SoftWhite,
    onBackground = OnSurface,
    
    surface = SoftWhite,
    onSurface = OnSurface,
    surfaceVariant = LightGray,
    onSurfaceVariant = OnSurfaceVariant,
    
    outline = MediumGray,
    outlineVariant = LightGray,
    
    scrim = Color.Black,
    
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = LightLavender
)

// Material 3 Dark Color Scheme
val ExpressiveDarkColors = darkColorScheme(
    primary = LightLavender,
    onPrimary = DarkPurple,
    primaryContainer = DarkPurple,
    onPrimaryContainer = LightLavender,
    
    secondary = AccentPurple,
    onSecondary = DarkPurple,
    secondaryContainer = DarkPurple,
    onSecondaryContainer = SoftPink,
    
    tertiary = SoftPink,
    onTertiary = DarkPurple,
    tertiaryContainer = DarkPurple,
    onTertiaryContainer = SoftPink,
    
    error = ErrorContainer,
    onError = ErrorRed,
    errorContainer = ErrorRed,
    onErrorContainer = ErrorContainer,
    
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    
    scrim = Color.Black,
    
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF1C1B1F),
    inversePrimary = MediumPurple
)

