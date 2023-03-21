package pl.jsyty.audiobookshelfnative.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import pl.jsyty.audiobookshelfnative.features.tabs.TabsScreen
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenAsyncHandler
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenLoadingAsyncHandler

class LoginScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val loginViewModel = koinViewModel<LoginViewModel>()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        loginViewModel.collectSideEffect {
            when (it){
                LoginViewModel.SideEffect.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Something went wrong")
                    }
                }
                LoginViewModel.SideEffect.LoggedIn -> navigator.replaceAll(TabsScreen)
            }

        }

        val state by loginViewModel.collectAsState()

        var loginText by rememberSaveable { mutableStateOf("") }
        var passwordText by rememberSaveable { mutableStateOf("") }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) {
            FullscreenLoadingAsyncHandler(state = state.loginAction, modifier = Modifier.padding(it)) {
                Box(
                    modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                            onClick = {
                                loginViewModel.login(loginText, passwordText)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = "Login")
                        }
                    }
                }
            }
        }
    }
}
