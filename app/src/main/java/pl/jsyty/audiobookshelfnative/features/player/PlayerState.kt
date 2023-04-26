package pl.jsyty.audiobookshelfnative.features.player

import android.content.ComponentName
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.media3.common.*
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import pl.jsyty.audiobookshelfnative.player.PlaybackService

@Stable
class PlayerState(
    initialTitle: String,
    initialAuthor: String,
    initialDuration: Float,
    initialCurrentTimeInSeconds: Float,
    private var mediaController: MediaController?,
) {
    var title by mutableStateOf(initialTitle)
    var author by mutableStateOf(initialAuthor)
    var durationInSeconds by mutableStateOf(initialDuration.toLong())
    var currentTimeInSeconds by mutableStateOf(initialCurrentTimeInSeconds.toLong())
    var isPlaying by mutableStateOf(false)
    // TODO : add isPlayerReady flag

    fun play(
        id: String,
        title: String,
        author: String,
        mediaUri: String,
        coverUri: String,
        startTimeInSeconds: Long,
    ) {
        mediaController?.let {
            it.setMediaItem(
                MediaItem
                    .Builder()
                    .setRequestMetadata(
                        MediaItem.RequestMetadata
                            .Builder()
                            .setMediaUri(mediaUri.toUri())
                            .build()
                    )
                    .setMediaId(id)
                    .setMediaMetadata(
                        MediaMetadata
                            .Builder()
                            .setTitle(title)
                            .setArtist(author)
                            .setWriter(author)
                            .setArtworkUri(coverUri.toUri())
                            .build()
                    )
                    .build(),
                (startTimeInSeconds * 1000)
            )
            it.prepare()
            it.play()
        }
    }

    fun pause() {
        mediaController?.pause()
    }

    fun seekTo(positionInSeconds: Long) {
        mediaController?.seekTo(positionInSeconds * 1000)
    }

    internal fun setMediaController(mediaController: MediaController?) {
        this.mediaController = mediaController
    }
}

@Composable
fun rememberPlayerState(
    initialTitle: String,
    initialAuthor: String,
    initialDuration: Float,
    initialCurrentTimeInSeconds: Float,
): PlayerState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val playerState = remember {
        PlayerState(
            initialTitle,
            initialAuthor,
            initialDuration,
            initialCurrentTimeInSeconds,
            null
        )
    }

    var mediaController: MediaController? by remember {
        mutableStateOf(null)
    }
    DisposableEffect(Unit) {
        val listener =
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    val duration = player.duration
                    val currentPosition = player.currentPosition
                    if (duration > 0) {
                        playerState.durationInSeconds = duration.coerceAtLeast(0L) / 1000
                        playerState.currentTimeInSeconds =
                            currentPosition.coerceAtLeast(0L) / 1000
                    }
                    player.currentMediaItem?.mediaMetadata?.let {
                        playerState.title = it.title.toString()
                        playerState.author = it.artist.toString()
                    }
                    playerState.isPlaying = player.isPlaying
                }
            }

        scope.launch {
            val sessionToken =
                SessionToken(context, ComponentName(context, PlaybackService::class.java))
            mediaController =
                MediaController.Builder(context, sessionToken).buildAsync().await().also {
                    it.addListener(listener)
                    playerState.setMediaController(it)
                }
        }
        onDispose {
            mediaController?.let {
                it.removeListener(listener)
                it.release()
                mediaController = null
            }
        }
    }

    LaunchedEffect(mediaController, playerState.isPlaying) {
        val localMediaController = mediaController
        if (localMediaController != null && playerState.isPlaying) {
            while (true) {
                playerState.currentTimeInSeconds = localMediaController.currentPosition / 1000
                delay(500)
            }
        }
    }

    return playerState
}
