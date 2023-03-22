package pl.jsyty.audiobookshelfnative.features.login

import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.settings.Settings
import pl.jsyty.audiobookshelfnative.core.Async
import pl.jsyty.audiobookshelfnative.core.BaseViewModel
import pl.jsyty.audiobookshelfnative.core.Uninitialized
import pl.jsyty.audiobookshelfnative.core.async
import pl.jsyty.audiobookshelfnative.models.LoginRequestDto

@KoinViewModel
class LoginViewModel(
    private val settings: Settings,
    private val audiobookshelfService: AudiobookshelfService
) : BaseViewModel<LoginViewModel.State, LoginViewModel.SideEffect>(State()) {
    data class State(
        val loginAction: Async<Unit> = Uninitialized
    )

    sealed class SideEffect {
        object LoggedIn : SideEffect()
        object Error : SideEffect()
    }

    fun login(serverAddress: String, username: String, password: String) = intent {
        async {
            settings.serverAddress.put(serverAddress)
            val response =
                audiobookshelfService.login(LoginRequestDto(username.trim(), password.trim()))
            val token = response.user.token
            settings.token.put(token)
            postSideEffect(SideEffect.LoggedIn)
        }
            .handleError {
                postSideEffect(SideEffect.Error)
            }
            .execute {
                state.copy(loginAction = it)
            }
    }
}
