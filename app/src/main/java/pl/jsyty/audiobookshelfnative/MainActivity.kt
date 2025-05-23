package pl.jsyty.audiobookshelfnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.compose.KoinContext
import pl.jsyty.audiobookshelfnative.features.login.LoginScreen
import pl.jsyty.audiobookshelfnative.features.tabs.TabsScreen
import pl.jsyty.audiobookshelfnative.settings.Settings
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

class MainActivity : ComponentActivity() {
    private val settings by inject<Settings>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val token = settings.token.get()

            val initialScreen = if (token.isNullOrBlank()) {
                LoginScreen()
            } else {
                TabsScreen
            }

            ensureActive()

            setContent {
                KoinContext {
                    AudiobookshelfNativeTheme {
                        var systemBarStyle by remember {
                            val defaultSystemBarColor = android.graphics.Color.TRANSPARENT
                            mutableStateOf(
                                SystemBarStyle.auto(
                                    lightScrim = defaultSystemBarColor,
                                    darkScrim = defaultSystemBarColor
                                )
                            )
                        }
                        LaunchedEffect(systemBarStyle) {
                            enableEdgeToEdge(
                                statusBarStyle = systemBarStyle,
                                navigationBarStyle = systemBarStyle
                            )
                        }

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Navigator(initialScreen) { navigator ->
                                SlideTransition(navigator)
                            }
                        }
                    }
                }
            }
        }
    }
}
