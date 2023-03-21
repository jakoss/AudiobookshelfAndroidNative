package pl.jsyty.audiobookshelfnative.features.login

import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.Settings
import pl.jsyty.audiobookshelfnative.core.Async
import pl.jsyty.audiobookshelfnative.core.BaseViewModel
import pl.jsyty.audiobookshelfnative.core.Uninitialized
import pl.jsyty.audiobookshelfnative.core.async
import pl.jsyty.audiobookshelfnative.models.LoginRequestDto

@KoinViewModel
class LoginViewModel(
    private val settings: Settings,
    private val audiobookshelfService: AudiobookshelfService
) : BaseViewModel<LoginViewModel.State, LoginViewModel.LoggedIn>(State()) {
    data class State(
        val loginAction: Async<Unit> = Uninitialized
    )

    object LoggedIn

    fun login(username: String, password: String) = intent {
        async {
            val response = audiobookshelfService.login(LoginRequestDto(username, password))
            val token = response.user.token
            settings.saveToken(token)
            postSideEffect(LoggedIn)
        }.execute {
            state.copy(loginAction = it)
        }
    }
}
