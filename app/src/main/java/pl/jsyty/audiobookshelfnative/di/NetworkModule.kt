package pl.jsyty.audiobookshelfnative.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.settings.Settings
import retrofit2.Retrofit
import java.io.IOException
import kotlin.text.isNullOrBlank

@Module
class NetworkModule {
    @Single
    fun okHttpClient(settings: Settings): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val (token, serverAddress) = runBlocking {
                Pair(settings.token.get(), settings.serverAddress.get())
            }
            val newHost = serverAddress?.toHttpUrlOrNull()
                ?: throw IOException("Server address cannot be null to call services")

            val newUrl = chain.request().url.newBuilder()
                .scheme(newHost.scheme)
                .host(newHost.host)
                .build()
            val request = chain.request().newBuilder()
                .url(newUrl)
                .apply {
                    if (!token.isNullOrBlank()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }
                .build()
            chain.proceed(request)
        }
        .build()

    @Single
    fun json(): Json = Json { ignoreUnknownKeys = true }

    @Single
    fun retrofit(httpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("https://replace.me/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Single
    fun audiobookshelfService(retrofit: Retrofit): AudiobookshelfService = retrofit.create(AudiobookshelfService::class.java)
}
