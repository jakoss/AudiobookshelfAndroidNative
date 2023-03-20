package pl.jsyty.audiobookshelfnative.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val username: String, val password: String)

@Serializable
data class LoginResponseDto(
    val user: UserDto,
    val userDefaultLibraryId: String,
)

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val type: String,
    val token: String,
    val isActive: Boolean,
    val isLocked: Boolean,
    val librariesAccessible: List<String>,
)