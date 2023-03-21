package pl.jsyty.audiobookshelfnative.features.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import pl.jsyty.audiobookshelfnative.features.tabs.TabsScreen
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenAsyncHandler

class LoginScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val loginViewModel = koinViewModel<LoginViewModel>()

        loginViewModel.collectSideEffect {
            navigator.replaceAll(TabsScreen)
        }

        val state by loginViewModel.collectAsState()

        var loginText by rememberSaveable { mutableStateOf("") }
        var passwordText by rememberSaveable { mutableStateOf("") }

        val loginLambda = {
            loginViewModel.login(loginText, passwordText)
        }

        FullscreenAsyncHandler(state = state.loginAction, onRetryAction = loginLambda) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    OutlinedTextField(
                        value = loginText,
                        onValueChange = { loginText = it },
                        label = { Text(text = "Username") },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        label = { Text(text = "Password") },
                        visualTransformation = PasswordVisualTransformation(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = loginLambda,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Login")
                    }
                }
            }
        }
    }
}
