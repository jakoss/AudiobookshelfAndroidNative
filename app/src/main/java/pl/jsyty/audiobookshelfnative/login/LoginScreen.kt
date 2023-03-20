package pl.jsyty.audiobookshelfnative.login

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.orbitmvi.orbit.compose.collectSideEffect
import pl.jsyty.audiobookshelfnative.tabs.TabsScreen

class LoginScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val loginViewModel = viewModel<LoginViewModel>()

        loginViewModel.collectSideEffect {
            navigator.replaceAll(TabsScreen)
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                var loginText by remember { mutableStateOf("") }
                var passwordText by remember { mutableStateOf("") }

                Text(text = "Login")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = loginText, onValueChange = { loginText = it })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = passwordText, onValueChange = { passwordText = it })
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { loginViewModel.login(loginText, passwordText) }) {
                    Text(text = "Login")
                }
            }
        }
    }
}