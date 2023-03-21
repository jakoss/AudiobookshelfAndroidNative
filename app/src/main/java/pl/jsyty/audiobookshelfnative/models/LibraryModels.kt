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
data class LibraryItemDto(
    val id: String,
    val mediaType: String,
    val media: BookDto,
)

@Serializable
data class BookDto(
    val libraryItemId: String,
    val metadata: BookMetadataDto,
    val coverPath: String?,
    val tags: List<String>,
    // TODO : add files and chapters
)

@Serializable
data class BookMetadataDto(
    val title: String?,
    val subtitle: String?,
    val authors: List<AuthorDto>,
    val narrators: List<String>,
    val genres: List<String>,
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
data class AuthorDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val imagePath: String? = null,
)
