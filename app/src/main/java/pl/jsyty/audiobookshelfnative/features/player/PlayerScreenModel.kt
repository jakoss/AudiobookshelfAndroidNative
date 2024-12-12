package pl.jsyty.audiobookshelfnative.features.player

import android.os.Build
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.BuildConfig
import pl.jsyty.audiobookshelfnative.core.*
import pl.jsyty.audiobookshelfnative.core.orbit.OrbitScreenModel
import pl.jsyty.audiobookshelfnative.models.dtos.*
import pl.jsyty.audiobookshelfnative.settings.Settings

@Factory
class PlayerScreenModel(
    @InjectedParam private val libraryItemId: String,
    private val audiobookshelfService: AudiobookshelfService,
    private val settings: Settings,
) : OrbitScreenModel<PlayerScreenModel.State, Unit>(State()) {
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

            PlayerScreenUiModel(
                libraryItemId = libraryItemId,
                serverAddress = serverAddress,
                title = playbackSession.displayTitle,
                subtitle = playbackSession.libraryItem.media.metadata.subtitle,
                author = playbackSession.displayAuthor,
                currentTimeInSeconds = playbackSession.currentTime,
                duration = playbackSession.duration,
                audioFilePath = "${serverAddress.let { if (it.last() == '/') it.dropLast(1) else it }}${playbackSession.audioTracks.first().contentUrl}"
            )
        }.execute { state.copy(model = it) }
    }
}
