package com.example.exoplayerplayground.exo_player_business.di

import android.content.Context
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {


    @Provides
    @Singleton
    fun provideCacheDir(@ApplicationContext context: Context): File {
        return context.cacheDir
    }

    @Provides
    @Singleton
    fun provideLeastRecentlyUsedEnvictor(): LeastRecentlyUsedCacheEvictor {
        val exoPlayerCacheSize: Long = 250 * 1024 * 1024 //250mb
        return LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
    }

    @Provides
    @Singleton
    fun provideStandaloneDatabase(@ApplicationContext context: Context): StandaloneDatabaseProvider {
        return StandaloneDatabaseProvider(context)
    }

    @Provides
    @Singleton
    fun provideSimpleCache(
        cacheDire: File,
        evictor: LeastRecentlyUsedCacheEvictor,
        standaloneDatabaseProvider: StandaloneDatabaseProvider

    ): SimpleCache {
        return SimpleCache(cacheDire, evictor, standaloneDatabaseProvider)
    }


    @Provides
    fun provideDefaultDataSourceFactory(
        @ApplicationContext context: Context
    ): DefaultHttpDataSource.Factory {
        val httpDataSource = DefaultHttpDataSource.Factory()
        httpDataSource.setUserAgent(
            Util.getUserAgent(
                context,
                context.getString(R.string.app_name)
            )
        )
        httpDataSource.setAllowCrossProtocolRedirects(true)
        return httpDataSource
    }

    @Provides
    fun providesCacheDataSourceFactory(
        simpleCache: SimpleCache,
        httpDataSource: DefaultHttpDataSource.Factory
    ): CacheDataSource.Factory {

        return CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSource)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}