package pl.jsyty.audiobookshelfnative.features.library

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.core.*
import pl.jsyty.audiobookshelfnative.models.LibraryItemDto

@KoinViewModel
class LibraryViewModel(
    private val audiobookshelfService: AudiobookshelfService,
) : BaseViewModel<LibraryViewModel.State, Unit>(State()) {
    data class State(
        val items: Async<ImmutableList<LibraryItemDto>> = Uninitialized
    )

    fun loadData() = intent {
        async {
            val libraries = audiobookshelfService.getAllLibraries().libraries

            // TODO : allow user to choose library
            val firstLibrary = libraries.firstOrNull() ?: error("No libraries")
            audiobookshelfService.getAllItems(firstLibrary.id).results.toImmutableList()
        }.execute { state.copy(items = it) }
    }
}
