package pl.jsyty.audiobookshelfnative

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ApiClient {
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = runBlocking { Settings.getToken() }
            if (token.isNotBlank()) {
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(chain.request())
            }
        }
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("https://audiobookshelf.jsyty.pl/")
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    val client: AudiobookshelfService = retrofit.create(AudiobookshelfService::class.java)
}