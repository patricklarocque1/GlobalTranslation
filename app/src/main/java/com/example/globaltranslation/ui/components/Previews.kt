package com.example.globaltranslation.ui.components

import androidx.compose.ui.tooling.preview.Preview

// A multi-preview annotation you can apply to any @Composable preview function
// to render common device sizes and both light/dark themes (stacked in the Preview pane).
@Preview(name = "Phone - Light", group = "Devices", device = "spec:width=411dp,height=891dp,dpi=480", showBackground = true)
@Preview(name = "Phone - Dark", group = "Devices", device = "spec:width=411dp,height=891dp,dpi=480", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Tablet - Light", group = "Devices", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true)
@Preview(name = "Tablet - Dark", group = "Devices", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
annotation class MultiDevicePreview
