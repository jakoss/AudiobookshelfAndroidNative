package pl.jsyty.audiobookshelfnative.player

import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_SPEECH
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.*
import okhttp3.OkHttpClient
import org.koin.core.component.*

class PlaybackService : MediaSessionService(), KoinComponent {
    private var mediaSession: MediaSession? = null

    private val playerBitmapLoader by inject<PlayerBitmapLoader>()

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(this)
                    .setDataSourceFactory(
                        DefaultDataSource.Factory(
                            this,
                            OkHttpDataSource.Factory(get<OkHttpClient>())
                        )
                    )
            )
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AUDIO_CONTENT_TYPE_SPEECH)
                    .setUsage(USAGE_MEDIA)
                    .build(),
                true
            )
            .build()
        player.addAnalyticsListener(EventLogger())
        mediaSession = MediaSession
            .Builder(this, player)
            .setBitmapLoader(CacheBitmapLoader(playerBitmapLoader))
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
