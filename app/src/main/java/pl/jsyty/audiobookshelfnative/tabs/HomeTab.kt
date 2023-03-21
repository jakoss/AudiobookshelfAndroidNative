package pl.jsyty.audiobookshelfnative.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import okhttp3.OkHttpClient
import org.koin.androidx.compose.get

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val url = remember {
            "https://audiobookshelf.jsyty.pl//hls/play_jnvn7pi75uibf9wa2u/output.m3u8"
        }

        val context = LocalContext.current
        val httpClient = get<OkHttpClient>()
        val exoPlayer = remember(context) {

            ExoPlayer.Builder(context)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(context)
                        .setDataSourceFactory(
                            DefaultDataSource.Factory(
                                context,
                                OkHttpDataSource.Factory(httpClient)
                            )
                        )
                )
                .build()
                .apply {
                    setMediaItem(MediaItem.fromUri(url))
                    prepare()
                }
        }

        AndroidView(factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
            }
        })
    }
}
