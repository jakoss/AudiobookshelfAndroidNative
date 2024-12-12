package pl.jsyty.audiobookshelfnative.features.login

import org.koin.core.annotation.Factory

import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.core.*
import pl.jsyty.audiobookshelfnative.core.orbit.OrbitScreenModel
import pl.jsyty.audiobookshelfnative.models.dtos.LoginRequestDto
import pl.jsyty.audiobookshelfnative.settings.Settings

@Factory
class LoginScreenModel(
    private val settings: Settings,
    private val audiobookshelfService: AudiobookshelfService
) : OrbitScreenModel<LoginScreenModel.State, LoginScreenModel.SideEffect>(State()) {
    data class State(
        val loginAction: Async<Unit> = Uninitialized
    )

    sealed class SideEffect {
        data object LoggedIn : SideEffect()
        data object Error : SideEffect()
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
