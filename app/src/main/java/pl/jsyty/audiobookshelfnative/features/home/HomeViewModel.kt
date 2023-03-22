package pl.jsyty.audiobookshelfnative.features.home

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.core.*
import pl.jsyty.audiobookshelfnative.models.LibraryItemDto
import pl.jsyty.audiobookshelfnative.models.UserDto
import pl.jsyty.audiobookshelfnative.settings.Settings

@KoinViewModel
class HomeViewModel(
    private val settings: Settings,
    private val audiobookshelfService: AudiobookshelfService,
) : BaseViewModel<HomeViewModel.State, Unit>(State()) {
    data class State(
        val screenModel: Async<HomeScreenUiModel> = Uninitialized,
    )

    fun loadData() = intent {
        val address =
            settings.serverAddress.get() ?: error("Server address cannot be null at this point")

        async {
            val itemsJob = viewModelScope.async {
                val libraries = audiobookshelfService.getAllLibraries().libraries
                val firstLibrary = libraries.firstOrNull() ?: error("No libraries")
                audiobookshelfService.getAllItems(firstLibrary.id).results.toImmutableList()
            }
            val userJob = viewModelScope.async {
                audiobookshelfService.getUser()
            }

            val user = userJob.await()
            val items = itemsJob.await()

            val currentMediaProgress = user.mediaProgress
                .sortedByDescending { it.lastUpdate }
                .firstOrNull { it.progress > 0 && !it.isFinished && !it.hideFromContinueListening }
            val libraryItemInProgress = currentMediaProgress?.let { mediaProgress ->
                items.find { it.id == mediaProgress.libraryItemId }
            }

            HomeScreenUiModel(
                currentMediaInProgress = libraryItemInProgress?.let {
                    mapLibraryItemToUiModel(
                        it,
                        user
                    )
                },
                serverAddress = address,
                libraryItems = items.map {
                    mapLibraryItemToUiModel(it, user)
                }.toImmutableList()
            )
        }.execute { state.copy(screenModel = it) }
    }

    private fun mapLibraryItemToUiModel(
        libraryItemDto: LibraryItemDto,
        userDto: UserDto
    ): HomeScreenUiModel.LibraryItem {
        val mediaProgress = userDto.mediaProgress.find { it.libraryItemId == libraryItemDto.id }
        return HomeScreenUiModel.LibraryItem(
            id = libraryItemDto.id,
            title = libraryItemDto.media.metadata.title ?: "No title",
            author = libraryItemDto.media.metadata.authorName ?: "No author",
            progress = mediaProgress?.progress ?: 0.0,
            isFinished = mediaProgress?.isFinished ?: false,
        )
    }
}
