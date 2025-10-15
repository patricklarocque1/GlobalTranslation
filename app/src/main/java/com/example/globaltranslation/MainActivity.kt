package com.example.globaltranslation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.globaltranslation.util.DeviceCompatibility
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.globaltranslation.ui.camera.CameraScreen
import com.example.globaltranslation.ui.conversation.ConversationScreen
import com.example.globaltranslation.ui.languages.LanguageScreen
import com.example.globaltranslation.ui.textinput.TextInputScreen
import com.example.globaltranslation.ui.theme.GlobalTranslationTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Log device compatibility information for 16KB page size support
        DeviceCompatibility.logPageSizeInfo()
        
        setContent {
            GlobalTranslationTheme {
                GloabTranslationApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun GloabTranslationApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.CONVERSATION) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // Animated content transition between screens
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = {
                    // Slide in from right and fade in, while sliding out to left and fading out
                    slideInHorizontally(
                        initialOffsetX = { it / 3 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeIn(
                        animationSpec = tween(300)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it / 3 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeOut(
                        animationSpec = tween(300)
                    )
                },
                label = "screen_transition"
            ) { destination ->
                when (destination) {
                    AppDestinations.CONVERSATION -> {
                        ConversationScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    AppDestinations.TEXT_INPUT -> {
                        TextInputScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    AppDestinations.CAMERA -> {
                        CameraScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    AppDestinations.LANGUAGES -> {
                        LanguageScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    CONVERSATION("Conversation", Icons.Filled.Mic),
    TEXT_INPUT("Text Input", Icons.Filled.Translate),
    CAMERA("Camera", Icons.Filled.CameraAlt),
    LANGUAGES("Languages", Icons.Filled.Language),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GlobalTranslationTheme {
        Greeting("Android")
    }
}
