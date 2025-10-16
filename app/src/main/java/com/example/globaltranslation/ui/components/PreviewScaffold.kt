package com.example.globaltranslation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.globaltranslation.ui.theme.GlobalTranslationTheme

/**
 * A small wrapper to standardize theming and background for Compose previews.
 * Usage:
 *
 * PreviewScaffold { MyComposablePreviewContent() }
 */
@Composable
fun PreviewScaffold(
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit
) {
	GlobalTranslationTheme {
		Surface {
			Box(modifier = modifier.fillMaxSize()) {
				content()
			}
		}
	}
}
