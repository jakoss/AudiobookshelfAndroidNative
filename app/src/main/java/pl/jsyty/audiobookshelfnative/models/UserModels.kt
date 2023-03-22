package pl.jsyty.audiobookshelfnative.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val type: String,
    val token: String,
    val mediaProgress: List<MediaProgressDto>,
    val seriesHideFromContinueListening: List<String>,
    val isActive: Boolean,
    val isLocked: Boolean,
)
