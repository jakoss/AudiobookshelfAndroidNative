package pl.jsyty.audiobookshelfnative.features.library

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import pl.jsyty.audiobookshelfnative.R
import pl.jsyty.audiobookshelfnative.core.images.BlurImageTransformation
import pl.jsyty.audiobookshelfnative.models.*
import pl.jsyty.audiobookshelfnative.ui.components.FullscreenAsyncHandler
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

class LibraryScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        Library()
    }
}

@Composable
private fun Library(libraryViewModel: LibraryViewModel = koinViewModel()) {
    val state by libraryViewModel.collectAsState()

    LaunchedEffect(Unit) {
        libraryViewModel.loadData()
    }

    FullscreenAsyncHandler(
        state = state.items,
        onRetryAction = libraryViewModel::loadData
    ) { items ->
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(items.size, key = { items[it].id }) {
                LibraryItemCell(libraryItemDto = items[it])
            }
        }
    }
}

@Composable
private fun LibraryItemCell(libraryItemDto: LibraryItemDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://audiobookshelf.jsyty.pl/api/items/${libraryItemDto.id}/cover")
                .transformations(BlurImageTransformation())
                .build(),
            contentDescription = libraryItemDto.media.metadata.title,
            placeholder = debugPlaceholder(debugPreview = R.drawable.img_audiobook),
            fallback = painterResource(id = R.drawable.img_audiobook),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = libraryItemDto.media.metadata.title ?: "No title")
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = libraryItemDto.media.metadata.authors.firstOrNull()?.name ?: "No author")
    }
}

@Preview
@Composable
private fun LibraryItemCellPreview() {
    AudiobookshelfNativeTheme {
        LibraryItemCell(
            libraryItemDto = LibraryItemDto(
                id = "test",
                mediaType = "book",
                media = BookDto(
                    libraryItemId = "testid",
                    metadata = BookMetadataDto(
                        title = "Book title",
                        subtitle = null,
                        authors = listOf(AuthorDto(id = "artistid", name = "Book Author")),
                        narrators = emptyList(),
                        genres = emptyList(),
                        publisher = null,
                        description = null,
                        isbn = null,
                        asin = null,
                        language = "en",
                        explicit = false
                    ),
                    coverPath = null,
                    tags = emptyList()
                )
            )
        )
    }
}

@Composable
fun debugPlaceholder(@DrawableRes debugPreview: Int) =
    if (LocalInspectionMode.current) {
        painterResource(id = debugPreview)
    } else {
        null
    }
