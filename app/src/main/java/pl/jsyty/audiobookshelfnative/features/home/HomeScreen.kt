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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.orbitmvi.orbit.compose.collectAsState
import pl.jsyty.audiobookshelfnative.R
import pl.jsyty.audiobookshelfnative.core.images.BlurImageTransformation
import pl.jsyty.audiobookshelfnative.core.voyager.getScreenModel
import pl.jsyty.audiobookshelfnative.features.player.PlayerScreen
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenAsyncHandler
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        Home(homeScreenModel = getScreenModel())
    }
}

@Composable
private fun Home(homeScreenModel: HomeScreenModel) {
    val state by homeScreenModel.collectAsState()

    LaunchedEffect(Unit) {
        homeScreenModel.loadData()
    }

    // get root navigator
    val navigator = LocalNavigator.current?.parent?.parent

    val navigateToPlayerScreen: (String) -> Unit = remember {
        { id: String ->
            navigator?.push(PlayerScreen(id))
        }
    }

    FullscreenAsyncHandler(
        state = state.screenModel,
        onRetryAction = homeScreenModel::loadData
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
                        libraryItem = it,
                        navigateToPlayerScreen = navigateToPlayerScreen,
                    )
                }
            }
            items(items.size, key = { items[it].id }, contentType = { "LibraryItemCell" }) {
                LibraryItemCell(
                    serverAddress = screenModel.serverAddress,
                    libraryItem = items[it],
                    navigateToPlayerScreen = navigateToPlayerScreen,
                )
            }
        }
    }
}

@Composable
private fun CurrentlyReadingCard(
    serverAddress: String,
    libraryItem: HomeScreenUiModel.LibraryItem,
    navigateToPlayerScreen: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                navigateToPlayerScreen(libraryItem.id)
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
            ),
            navigateToPlayerScreen = {}
        )
    }
}

@Composable
private fun LibraryItemCell(
    serverAddress: String,
    libraryItem: HomeScreenUiModel.LibraryItem,
    navigateToPlayerScreen: (String) -> Unit,
) {
    Card(
        modifier = Modifier.clickable {
            navigateToPlayerScreen(libraryItem.id)
        }
    ) {
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
            ),
            navigateToPlayerScreen = {}
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
            ),
            navigateToPlayerScreen = {}
        )
    }
}
