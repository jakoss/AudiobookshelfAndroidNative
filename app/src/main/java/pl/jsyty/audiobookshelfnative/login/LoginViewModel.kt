package pl.jsyty.audiobookshelfnative.login

import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import pl.jsyty.audiobookshelfnative.ApiClient
import pl.jsyty.audiobookshelfnative.Settings
import pl.jsyty.audiobookshelfnative.core.Async
import pl.jsyty.audiobookshelfnative.core.BaseViewModel
import pl.jsyty.audiobookshelfnative.core.Uninitialized
import pl.jsyty.audiobookshelfnative.core.async
import pl.jsyty.audiobookshelfnative.models.LoginRequestDto

class LoginViewModel : BaseViewModel<LoginViewModel.State, LoginViewModel.LoggedIn>(State()) {
    data class State(
        val loginAction: Async<Unit> = Uninitialized
    )

    object LoggedIn

    fun login(username: String, password: String) = intent {
        async {
            val response = ApiClient.client.login(LoginRequestDto(username, password))
            val token = response.user.token
            Settings.saveToken(token)
            postSideEffect(LoggedIn)
        }.execute {
            state.copy(loginAction = it)
        }
    }
}