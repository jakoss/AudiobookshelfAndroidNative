package pl.jsyty.audiobookshelfnative.features.player

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState
import pl.jsyty.audiobookshelfnative.R
import pl.jsyty.audiobookshelfnative.core.images.BlurImageTransformation
import pl.jsyty.audiobookshelfnative.core.voyager.getScreenModel
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenAsyncHandler
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

class PlayerScreen(private val libraryItemId: String) : Screen {
    @Composable
    override fun Content() {
        val playerScreenModel = getScreenModel<PlayerScreenModel> { parametersOf(libraryItemId) }
        val state by playerScreenModel.collectAsState()

        FullscreenAsyncHandler(
            state = state.model,
            onRetryAction = playerScreenModel::loadLibraryItem
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
    val playerState = rememberPlayerState(
        initialTitle = model.title,
        initialAuthor = model.author,
        initialDuration = model.duration,
        initialCurrentTimeInSeconds = model.currentTimeInSeconds
    )

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

        var seekToValue by remember {
            mutableStateOf<Float?>(null)
        }
        Slider(
            modifier = Modifier.fillMaxWidth(),
            enabled = playerState.isPlayerReady,
            value = seekToValue ?: playerState.currentTimeInSeconds.toFloat(),
            valueRange = 0f..playerState.durationInSeconds.toFloat(),
            onValueChange = {
                seekToValue = it

            },
            onValueChangeFinished = {
                seekToValue?.let {
                    playerState.seekTo(it.toLong())
                }
                seekToValue = null
            })

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val currentLabel = DateUtils.formatElapsedTime(
                seekToValue?.toLong() ?: playerState.currentTimeInSeconds
            )
            val durationLabel = DateUtils.formatElapsedTime(playerState.durationInSeconds)
            Text(text = currentLabel, style = MaterialTheme.typography.bodySmall)
            Text(text = durationLabel, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.FastRewind,
                contentDescription = "Fast rewind",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(enabled = playerState.isPlayerReady, onClick = playerState::seekBack)
            )
            Icon(
                imageVector = if (playerState.isPlaying) {
                    Icons.Filled.PauseCircleFilled
                } else {
                    Icons.Filled.PlayCircleFilled
                },
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable(enabled = playerState.isPlayerReady) {
                        if (!playerState.isPlaying) {
                            playMedia(playerState, model)
                        } else {
                            playerState.pause()
                        }
                    }
            )
            Icon(
                imageVector = Icons.Filled.FastForward,
                contentDescription = "Fast forward",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        enabled = playerState.isPlayerReady,
                        onClick = playerState::seekForward
                    )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun playMedia(
    playerState: PlayerState,
    model: PlayerScreenUiModel
) {
    playerState.play(
        id = model.libraryItemId,
        title = model.title,
        author = model.author,
        mediaUri = model.audioFilePath,
        coverUri = "${model.serverAddress}api/items/${model.libraryItemId}/cover",
        startTimeInSeconds = model.currentTimeInSeconds.toLong()
    )
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
                duration = 4123413f,
                currentTimeInSeconds = 123123f,
                audioFilePath = "audioFilePath",
            ),
        )
    }
}
