package com.example.gloabtranslation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 Expressive Shapes with large corner radii.
 * This creates the soft, pill-shaped aesthetic seen in Google Translate.
 */
val ExpressiveShapes = Shapes(
    // Extra small - 12dp radius
    extraSmall = RoundedCornerShape(12.dp),
    
    // Small - 16dp radius (chips, small buttons)
    small = RoundedCornerShape(16.dp),
    
    // Medium - 20dp radius (cards, text fields)
    medium = RoundedCornerShape(20.dp),
    
    // Large - 28dp radius (large cards, dialogs)
    large = RoundedCornerShape(28.dp),
    
    // Extra large - 32dp radius (bottom sheets, large dialogs)
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Pill shape - fully rounded corners for buttons and chips.
 * Use this for accent buttons and interactive elements.
 */
val PillShape = RoundedCornerShape(50)

/**
 * Top rounded shape - for bottom sheets and modals.
 */
val TopRoundedShape = RoundedCornerShape(
    topStart = 28.dp,
    topEnd = 28.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

