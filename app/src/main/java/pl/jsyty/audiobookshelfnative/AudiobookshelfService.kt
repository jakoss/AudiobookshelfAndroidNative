package pl.jsyty.audiobookshelfnative

import pl.jsyty.audiobookshelfnative.models.LoginRequestDto
import pl.jsyty.audiobookshelfnative.models.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AudiobookshelfService {
    @POST("login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): LoginResponseDto
}