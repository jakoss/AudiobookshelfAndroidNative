package pl.jsyty.audiobookshelfnative.features.player

import android.os.Build
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.BuildConfig
import pl.jsyty.audiobookshelfnative.core.*
import pl.jsyty.audiobookshelfnative.models.dtos.*
import pl.jsyty.audiobookshelfnative.settings.Settings

@KoinViewModel
class PlayerViewModel(
    @InjectedParam private val libraryItemId: String,
    private val audiobookshelfService: AudiobookshelfService,
    private val settings: Settings,
) : BaseViewModel<PlayerViewModel.State, Unit>(State()) {
    data class State(
        val model: Async<PlayerScreenUiModel> = Uninitialized,
        val playbackSession: PlaybackSessionExpandedDto? = null,
    )

    init {
        loadLibraryItem()
    }

    fun loadLibraryItem() = intent {
        async {
            val serverAddress = settings.serverAddress.get() ?: error("Server address is not set")

            val playbackSession = audiobookshelfService.playItem(
                libraryItemId, PlayItemRequestDto(
                    deviceInfo = DeviceInfoDto(
                        BuildConfig.VERSION_NAME,
                        Build.MANUFACTURER,
                        Build.MODEL,
                        Build.VERSION.SDK_INT,
                    ),
                    mediaPlayer = "ExoPlayer"
                )
            )
            reduce { state.copy(playbackSession = playbackSession) }

            val progress = playbackSession.currentTime / playbackSession.duration
            PlayerScreenUiModel(
                libraryItemId = libraryItemId,
                serverAddress = serverAddress,
                title = playbackSession.displayTitle,
                subtitle = playbackSession.libraryItem.media.metadata.subtitle,
                author = playbackSession.displayAuthor,
                progress = progress,
                currentTimeInSeconds = playbackSession.currentTime,
                audioFilePath = "${serverAddress.let { if (it.last() == '/') it.dropLast(1) else it }}${playbackSession.audioTracks.first().contentUrl}"
            )
        }.execute { state.copy(model = it) }
    }
}
