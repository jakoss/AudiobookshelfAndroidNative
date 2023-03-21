package pl.jsyty.audiobookshelfnative.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import pl.jsyty.audiobookshelfnative.AudiobookshelfService
import pl.jsyty.audiobookshelfnative.Settings
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
    single {
        val settings = get<Settings>()
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { settings.getToken() }
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
    }

    single { Json { ignoreUnknownKeys = true } }

    single {
        val httpClient = get<OkHttpClient>()
        val json = get<Json>()
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://audiobookshelf.jsyty.pl/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    single<AudiobookshelfService> {
        val retrofit = get<Retrofit>()
        retrofit.create(AudiobookshelfService::class.java)
    }
}
