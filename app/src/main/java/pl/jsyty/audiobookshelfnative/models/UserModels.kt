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

@Serializable
data class DeviceInfoDto(
    val clientVersion: String, // The version of the client
    val manufacturer: String, // The manufacturer of the client device
    val model: String, // The model of the client device
    val sdkVersion: Int // For an Android client, the Android SDK version of the client
)
