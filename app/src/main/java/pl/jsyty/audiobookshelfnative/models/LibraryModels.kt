package pl.jsyty.audiobookshelfnative.models

import kotlinx.serialization.Serializable

@Serializable
data class GetAllLibrariesResponseDto(val libraries: List<LibraryDto>)

@Serializable
data class LibraryDto(
    val id: String,
    val name: String,
    val displayOrder: Int,
    val icon: String,
    val mediaType: String,
)

@Serializable
data class GetAllLibraryItemsResponseDto(
    val results: List<LibraryItemDto>,
)

@Serializable
data class GetItemsInProgressResponseDto(
    val results: List<LibraryItemDto>,
)

@Serializable
data class LibraryItemDto(
    val id: String,
    val mediaType: String,
    val media: BookMinifiedDto,
)

@Serializable
data class BookMinifiedDto(
    val metadata: BookMetadataMinifiedDto,
    val coverPath: String?,
)

@Serializable
data class BookMetadataMinifiedDto(
    val title: String?,
    val subtitle: String?,
    val authorName: String?,
    //val publishedYear: String?,
    //val publishedDate: String?,
    val publisher: String?,
    val description: String?,
    val isbn: String?,
    val asin: String?,
    val language: String?,
    val explicit: Boolean,
)

@Serializable
data class MediaProgressDto(
    val id: String,
    val libraryItemId: String,
    val duration: Double,
    val progress: Double,
    val currentTime: Double,
    val isFinished: Boolean,
    val hideFromContinueListening: Boolean,
    val lastUpdate: Long,
    val startedAt: Long,
    val finishedAt: Long?,
)
