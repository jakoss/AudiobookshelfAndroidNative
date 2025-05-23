package pl.jsyty.audiobookshelfnative.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [NetworkModule::class])
@ComponentScan("pl.jsyty.audiobookshelfnative**")
class AppModule
