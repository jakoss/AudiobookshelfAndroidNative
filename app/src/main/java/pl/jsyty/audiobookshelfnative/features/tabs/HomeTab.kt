package pl.jsyty.audiobookshelfnative.features.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import pl.jsyty.audiobookshelfnative.features.home.HomeScreen

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
        Navigator(HomeScreen())
        // val url = remember {
        //     "https://audiobookshelf.jsyty.pl//hls/play_jnvn7pi75uibf9wa2u/output.m3u8"
        // }
        //
        // val context = LocalContext.current
        // val httpClient = get<OkHttpClient>()
        // val exoPlayer = remember(context) {
        //
        //     ExoPlayer.Builder(context)
        //         .setMediaSourceFactory(
        //             DefaultMediaSourceFactory(context)
        //                 .setDataSourceFactory(
        //                     DefaultDataSource.Factory(
        //                         context,
        //                         OkHttpDataSource.Factory(httpClient)
        //                     )
        //                 )
        //         )
        //         .build()
        //         .apply {
        //             setMediaItem(MediaItem.fromUri(url))
        //             prepare()
        //         }
        // }
        //
        // AndroidView(factory = { context ->
        //     PlayerView(context).apply {
        //         player = exoPlayer
        //     }
        // })
    }
}
