package pl.jsyty.audiobookshelfnative.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import pl.jsyty.audiobookshelfnative.R
import pl.jsyty.audiobookshelfnative.core.images.BlurImageTransformation
import pl.jsyty.audiobookshelfnative.features.player.PlayerScreen
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenAsyncHandler
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

class HomeScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        Home()
    }
}

@Composable
private fun Home(homeViewModel: HomeViewModel = koinViewModel()) {
    val state by homeViewModel.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadData()
    }

    FullscreenAsyncHandler(
        state = state.screenModel,
        onRetryAction = homeViewModel::loadData
    ) { screenModel ->
        val items = screenModel.libraryItems
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            screenModel.currentMediaInProgress?.let {
                item(span = { GridItemSpan(2) }, contentType = "ReadingCard") {
                    CurrentlyReadingCard(
                        serverAddress = screenModel.serverAddress,
                        libraryItem = it
                    )
                }
            }
            items(items.size, key = { items[it].id }, contentType = { "LibraryItemCell" }) {
                LibraryItemCell(serverAddress = screenModel.serverAddress, libraryItem = items[it])
            }
        }
    }
}

@Composable
private fun CurrentlyReadingCard(
    serverAddress: String,
    libraryItem: HomeScreenUiModel.LibraryItem
) {
    // get root navigator
    val navigator = LocalNavigator.currentOrThrow.parent?.parent
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                navigator?.push(PlayerScreen(libraryItem.id))
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${serverAddress}api/items/${libraryItem.id}/cover")
                    .transformations(BlurImageTransformation())
                    .build(),
                contentDescription = libraryItem.title,
                placeholder = painterResource(id = R.drawable.img_audiobook),
                fallback = painterResource(id = R.drawable.img_audiobook),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.fillMaxHeight()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = libraryItem.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = libraryItem.author,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.weight(1f))
                LinearProgressIndicator(
                    progress = libraryItem.progress,
                    trackColor = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview
@Composable
private fun CurrentlyReadingCardPreview() {
    AudiobookshelfNativeTheme {
        CurrentlyReadingCard(
            serverAddress = "",
            libraryItem = HomeScreenUiModel.LibraryItem(
                id = "test",
                title = "Book title",
                author = "Book Author",
                progress = 0.3f,
                isFinished = false
            )
        )
    }
}

@Composable
private fun LibraryItemCell(serverAddress: String, libraryItem: HomeScreenUiModel.LibraryItem) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("${serverAddress}api/items/${libraryItem.id}/cover")
                        .transformations(BlurImageTransformation())
                        .build(),
                    contentDescription = libraryItem.title,
                    placeholder = painterResource(id = R.drawable.img_audiobook),
                    fallback = painterResource(id = R.drawable.img_audiobook),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                )

                if (libraryItem.isFinished) {
                    // display check icon for finished books
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.CheckCircle),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Finished book",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(24.dp)
                    )
                }

                if (libraryItem.progress > 0 && !libraryItem.isFinished) {
                    LinearProgressIndicator(
                        progress = libraryItem.progress,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = libraryItem.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = libraryItem.author,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
private fun LibraryItemInProgressCellPreview() {
    AudiobookshelfNativeTheme {
        LibraryItemCell(
            serverAddress = "",
            libraryItem = HomeScreenUiModel.LibraryItem(
                id = "test",
                title = "Book title",
                author = "Book Author",
                progress = 0.3f,
                isFinished = false
            )
        )
    }
}

@Preview
@Composable
private fun LibraryItemFinishedCellPreview() {
    AudiobookshelfNativeTheme {
        LibraryItemCell(
            serverAddress = "",
            libraryItem = HomeScreenUiModel.LibraryItem(
                id = "test",
                title = "Book title",
                author = "Book Author",
                progress = 0.9f,
                isFinished = true
            )
        )
    }
}
