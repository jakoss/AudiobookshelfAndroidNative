package pl.jsyty.audiobookshelfnative.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import pl.jsyty.audiobookshelfnative.core.voyager.getScreenModel
import pl.jsyty.audiobookshelfnative.features.tabs.TabsScreen
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenLoadingAsyncHandler
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val loginScreenModel = getScreenModel<LoginScreenModel>()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        loginScreenModel.collectSideEffect {
            when (it) {
                LoginScreenModel.SideEffect.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Something went wrong")
                    }
                }

                LoginScreenModel.SideEffect.LoggedIn -> navigator.replaceAll(TabsScreen)
            }
        }

        val state by loginScreenModel.collectAsState()
        LoginScreenImpl(
            snackbarHostState = snackbarHostState,
            state = state,
            loginAction = loginScreenModel::login
        )
    }
}

@Composable
private fun LoginScreenImpl(
    snackbarHostState: SnackbarHostState,
    state: LoginScreenModel.State,
    loginAction: (String, String, String) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPaddings ->
        FullscreenLoadingAsyncHandler(
            state = state.loginAction,
            modifier = Modifier.padding(scaffoldPaddings)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    var loginText by rememberSaveable { mutableStateOf("") }
                    var passwordText by rememberSaveable { mutableStateOf("") }
                    var serverAddressText by rememberSaveable { mutableStateOf("") }

                    Text(
                        text = "Login to your account",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = serverAddressText,
                        onValueChange = { serverAddressText = it },
                        label = { Text(text = "Server address") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Uri
                        ),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = loginText,
                        onValueChange = { loginText = it },
                        label = { Text(text = "Username") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        label = { Text(text = "Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            loginAction(serverAddressText, loginText, passwordText)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Login")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenImplPreview() {
    AudiobookshelfNativeTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        LoginScreenImpl(
            snackbarHostState = snackbarHostState,
            state = LoginScreenModel.State(),
            loginAction = { _, _, _ -> }
        )
    }
}
