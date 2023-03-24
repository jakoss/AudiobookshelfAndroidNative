package pl.jsyty.audiobookshelfnative.models.dtos

import kotlinx.serialization.Serializable
import pl.jsyty.audiobookshelfnative.core.serialization.EnumAsIntSerializer

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
    val duration: Float,
    val progress: Float,
    val currentTime: Float,
    val isFinished: Boolean,
    val hideFromContinueListening: Boolean,
    val lastUpdate: Long,
    val startedAt: Long,
    val finishedAt: Long?,
)

@Serializable
data class PlayItemRequestDto(
    val deviceInfo: DeviceInfoDto, // Information about the device
    val mediaPlayer: String, // The media player the client is using
    val forceDirectPlay: Boolean = false, // Whether to force direct play of the library item
    val forceTranscode: Boolean = false, // Whether to force the server to transcode the audio
    val supportedMimeTypes: List<String> = emptyList() // The MIME types that are supported by the client
)

@Serializable
data class PlaybackSessionExpandedDto(
    val id: String,
    val userId: String,
    val libraryId: String,
    val libraryItemId: String,
    val mediaType: String,
    val mediaMetadata: BookMetadataDto,
    val chapters: List<ChapterDto>,
    val displayTitle: String,
    val displayAuthor: String,
    val coverPath: String?,
    val duration: Float,
    val playMethod: PlayMethod,
    val mediaPlayer: String,
    val deviceInfo: DeviceInfoDto,
    val startTime: Float,
    val currentTime: Float,
    val libraryItem: LibraryItemDto,
    val audioTracks: List<AudioTrackDto>,
)

@Serializable
data class BookMetadataDto(
    val title: String?,
    val subtitle: String?,
    val authors: List<AuthorMinifiedDto>,
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
data class AuthorMinifiedDto(
    val id: String,
    val name: String,
)

@Serializable
data class ChapterDto(
    val id: Int, // The ID of the book chapter
    val start: Float, // When in the book (in seconds) the chapter starts
    val end: Float, // When in the book (in seconds) the chapter ends
    val title: String // The title of the chapter
)

@Serializable
data class AudioTrackDto(
    val index: Int, // The index of the audio track
    val startOffset: Float, // When in the audio file (in seconds) the track starts
    val duration: Float, // The length (in seconds) of the audio track
    val title: String, // The filename of the audio file the audio track belongs to
    val contentUrl: String, // The URL path of the audio file
    val mimeType: String, // The MIME type of the audio file
    val metadata: FileMetadataDto? // The metadata of the audio file
)

@Serializable
data class FileMetadataDto(
    val filename: String, // The filename of the file
    val ext: String, // The file extension of the file
    val path: String, // The absolute path on the server of the file
    val relPath: String, // The path of the file, relative to the book's or podcast's folder
    val size: Long, // The size (in bytes) of the file
    val mtimeMs: Long, // The time (in ms since POSIX epoch) when the file was last modified on disk
    val ctimeMs: Long, // The time (in ms since POSIX epoch) when the file status was changed on disk
    val birthtimeMs: Long // The time (in ms since POSIX epoch) when the file was created on disk. Will be 0 if unknown
)

private class PlayMethodSerializer : EnumAsIntSerializer<PlayMethod>(
    "playMethod",
    { it.value },
    { v -> PlayMethod.values().first { it.value == v } }
)

@Serializable(with = PlayMethodSerializer::class)
enum class PlayMethod(val value: Int) {
    DirectPlay(0),
    DirectStream(1),
    Transcode(2),
    Local(3),
}
