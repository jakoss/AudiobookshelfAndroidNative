package pl.jsyty.audiobookshelfnative

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.*
import pl.jsyty.audiobookshelfnative.di.AppModule
import pl.jsyty.audiobookshelfnative.di.networkModule
import timber.log.Timber

class MyApplication : Application() {

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
}
