package pl.jsyty.audiobookshelfnative

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.ksp.generated.*
import pl.jsyty.audiobookshelfnative.di.AppModule
import pl.jsyty.audiobookshelfnative.di.networkModule
import timber.log.Timber

class MyApplication : Application(), SingletonImageLoader.Factory, KoinComponent {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                AppModule().module,
                networkModule,
            )
        }
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = {
                    get<OkHttpClient>()
                }))
            }
            .build()
    }
}
