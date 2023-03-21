package pl.jsyty.audiobookshelfnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import pl.jsyty.audiobookshelfnative.features.login.LoginScreen
import pl.jsyty.audiobookshelfnative.features.tabs.TabsScreen
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

class MainActivity : ComponentActivity() {
    private val settings by inject<Settings>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = runBlocking { settings.getToken() }
        val initialScreen = if (token.isNotBlank()) {
            TabsScreen
        } else {
            LoginScreen()
        }
        setContent {
            AudiobookshelfNativeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigator(initialScreen)
                }
            }
        }
    }
}
