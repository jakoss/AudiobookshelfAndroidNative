package pl.jsyty.audiobookshelfnative.features.player

import android.content.ComponentName
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState
import pl.jsyty.audiobookshelfnative.R
import pl.jsyty.audiobookshelfnative.core.images.BlurImageTransformation
import pl.jsyty.audiobookshelfnative.player.PlaybackService
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenAsyncHandler
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

class PlayerScreen(private val libraryItemId: String) : AndroidScreen() {
    @Composable
    override fun Content() {
        val playerViewModel = koinViewModel<PlayerViewModel> { parametersOf(libraryItemId) }
        val state by playerViewModel.collectAsState()

        FullscreenAsyncHandler(
            state = state.model,
            onRetryAction = playerViewModel::loadLibraryItem
        ) {
            PlayerScreenContent(it)
        }
    }
}

@Composable
private fun PlayerScreenContent(model: PlayerScreenUiModel) {
    val navigator = LocalNavigator.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navigator?.pop()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back")
            }
            Text(
                text = model.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f),
                style = MaterialTheme.typography.headlineMedium,
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("${model.serverAddress}api/items/${model.libraryItemId}/cover")
                .transformations(BlurImageTransformation())
                .build(),
            contentDescription = model.title,
            placeholder = painterResource(id = R.drawable.img_audiobook),
            fallback = painterResource(id = R.drawable.img_audiobook),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .padding(28.dp)
        )

        PlayerControls(model)
    }
}

@Composable
private fun PlayerControls(model: PlayerScreenUiModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mediaController: MediaController? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        scope.launch {
            val sessionToken =
                SessionToken(context, ComponentName(context, PlaybackService::class.java))
            mediaController = MediaController.Builder(context, sessionToken).buildAsync().await()
        }
        onDispose {
            mediaController?.let {
                it.release()
                mediaController = null
            }
        }
    }
    Column(modifier = Modifier.padding(horizontal = 48.dp)) {
        Text(
            text = model.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = model.author,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(32.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = model.progress)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(text = "0:00", style = MaterialTheme.typography.bodySmall)
            Text(text = "1:00", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.FastRewind,
                contentDescription = "Skip previous",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { }
            )
            Icon(
                imageVector = Icons.Filled.PlayCircleFilled,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable {
                        mediaController?.let {
                            it.setMediaItem(
                                MediaItem.fromUri(model.audioFilePath)
                            )
                            // it.seekTo(model.progress.toLong())
                            it.prepare()
                        }
                    }
            )
            Icon(
                imageVector = Icons.Filled.FastForward,
                contentDescription = "Skip next",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview
@Composable
private fun PlayerScreenContentPreview() {
    AudiobookshelfNativeTheme {
        PlayerScreenContent(
            PlayerScreenUiModel(
                libraryItemId = "id",
                serverAddress = "serverAddress",
                title = "Title",
                subtitle = "Subtitle",
                author = "Author",
                progress = 0.5f,
                audioFilePath = "audioFilePath",
            ),
        )
    }
}
