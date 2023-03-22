package pl.jsyty.audiobookshelfnative.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val username: String, val password: String)

@Serializable
data class LoginResponseDto(
    val user: UserDto,
    val userDefaultLibraryId: String,
)
