package pl.jsyty.audiobookshelfnative

import pl.jsyty.audiobookshelfnative.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AudiobookshelfService {
    @POST("login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): LoginResponseDto

    @GET("api/libraries")
    suspend fun getAllLibraries(): GetAllLibrariesResponseDto

    @GET("api/libraries/{id}/items?limit=0&minified=0")
    suspend fun getAllItems(@Path("id") id: String): GetAllLibraryItemsResponseDto
}
