package com.example.globaltranslation.ui.components
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark

// A multi-preview annotation you can apply to any @Composable preview function
// to render common device sizes and both light/dark themes (stacked in the Preview pane).
@Preview(name = "Phone - Light", group = "Devices", device = "spec:width=411dp,height=891dp,dpi=480", showBackground = true)
@Preview(name = "Phone - Dark", group = "Devices", device = "spec:width=411dp,height=891dp,dpi=480", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Tablet - Light", group = "Devices", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true)
@Preview(name = "Tablet - Dark", group = "Devices", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
annotation class MultiDevicePreview

// A design variant annotation to multiply previews across font scales, light/dark, and dynamic colors.
@PreviewLightDark
@PreviewFontScale
@PreviewDynamicColors
annotation class DesignVariantPreview

// Font scaling preview set for accessibility testing (quick focused runs)
@Preview(name = "Font 1.0x", group = "Accessibility", fontScale = 1.0f, showBackground = true)
@Preview(name = "Font 1.5x", group = "Accessibility", fontScale = 1.5f, showBackground = true)
@Preview(name = "Font 2.0x", group = "Accessibility", fontScale = 2.0f, showBackground = true)
annotation class FontScalePreview

// Dynamic color preview set to quickly verify Material You palettes in light/dark
@Preview(name = "Dynamic - Light", group = "Dynamic Colors", showBackground = true)
@Preview(name = "Dynamic - Dark", group = "Dynamic Colors", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@PreviewDynamicColors
annotation class DynamicColorPreview

// UI check preview set: Extreme font scale and RTL in light/dark to spot layout/accessibility issues
@Preview(name = "UI Check - LTR Light 2.0x", group = "UI Checks", fontScale = 2.0f, showBackground = true)
@Preview(name = "UI Check - LTR Dark 2.0x", group = "UI Checks", fontScale = 2.0f, showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "UI Check - RTL Light", group = "UI Checks", locale = "ar", showBackground = true)
@Preview(name = "UI Check - RTL Dark", group = "UI Checks", locale = "ar", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
annotation class UiCheckPreview
