package pl.jsyty.audiobookshelfnative

import pl.jsyty.audiobookshelfnative.models.*
import pl.jsyty.audiobookshelfnative.models.dtos.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AudiobookshelfService {
    @POST("login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): LoginResponseDto

    @GET("api/libraries")
    suspend fun getAllLibraries(): GetAllLibrariesResponseDto

    @GET("api/libraries/{id}/items?limit=0&minified=1")
    suspend fun getAllItems(@Path("id") id: String): GetAllLibraryItemsResponseDto

    @GET("api/me")
    suspend fun getUser(): UserDto

    @GET("api/me/items-in-progress")
    suspend fun getItemsInProgress(): GetItemsInProgressResponseDto

    @GET("api/me/progress/{libraryItemId}")
    suspend fun getItemProgress(@Path("libraryItemId") libraryItemId: String): MediaProgressDto

    @POST("api/items/{libraryItemId}/play")
    suspend fun playItem(
        @Path("libraryItemId") libraryItemId: String,
        @Body playItemRequestDto: PlayItemRequestDto
    ): PlaybackSessionExpandedDto
}
