package pl.jsyty.audiobookshelfnative.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.settings.Settings
import retrofit2.Retrofit
import java.io.IOException

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
    single {
        val settings = get<Settings>()
        OkHttpClient.Builder()
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
    }

    single { Json { ignoreUnknownKeys = true } }

    single {
        val httpClient = get<OkHttpClient>()
        val json = get<Json>()
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://replace.me/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    single<AudiobookshelfService> {
        val retrofit = get<Retrofit>()
        retrofit.create(AudiobookshelfService::class.java)
    }
}
