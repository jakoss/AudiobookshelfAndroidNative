package pl.jsyty.audiobookshelfnative.features.player

import androidx.compose.runtime.Immutable

@Immutable
data class PlayerScreenUiModel(
    val libraryItemId: String,
    val serverAddress: String,
    val title: String,
    val subtitle: String?,
    val author: String,
    val progress: Float,
    val audioFilePath: String,
)
