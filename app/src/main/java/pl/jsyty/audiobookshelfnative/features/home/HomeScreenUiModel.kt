package pl.jsyty.audiobookshelfnative.features.home

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class HomeScreenUiModel(
    val currentMediaInProgress: LibraryItem?,
    val serverAddress: String,
    val libraryItems: ImmutableList<LibraryItem>,
) {
    data class LibraryItem(
        val id: String,
        val title: String,
        val author: String,
        val progress: Double,
        val isFinished: Boolean,
    )
}
