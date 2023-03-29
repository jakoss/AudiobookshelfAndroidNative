package pl.jsyty.audiobookshelfnative.player

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import coil.imageLoader
import coil.request.ImageRequest
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import timber.log.Timber
import java.io.ByteArrayOutputStream

class PlaybackService : MediaSessionService(), KoinComponent {
    private var mediaSession: MediaSession? = null

    private val ioScope = CoroutineScope(Dispatchers.IO)

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
            .build()
        player.addAnalyticsListener(EventLogger())
        mediaSession = MediaSession
            .Builder(this, player)
            .setCallback(object : MediaSession.Callback {
                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<MutableList<MediaItem>> {
                    return ioScope.future {
                        mediaItems.map {
                            val newBuilder = it.buildUpon()
                            try {
                                val imageRequest = ImageRequest.Builder(this@PlaybackService)
                                    .data(it.mediaMetadata.artworkUri)
                                    .build()
                                val result = this@PlaybackService.imageLoader.execute(imageRequest)
                                result.drawable?.toBitmap()?.let { bitmap ->
                                    val stream = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    val byteArray = stream.toByteArray()
                                    newBuilder.setMediaMetadata(
                                        it.mediaMetadata.buildUpon()
                                            .setArtworkData(
                                                byteArray,
                                                MediaMetadata.PICTURE_TYPE_FRONT_COVER
                                            )
                                            .build()
                                    )
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error while downloading artwork")
                            }
                            newBuilder
                                .setUri(it.requestMetadata.mediaUri)
                                .build()
                        }.toMutableList()
                    }
                }
            })
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
