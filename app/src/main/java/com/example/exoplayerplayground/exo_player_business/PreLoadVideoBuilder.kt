package com.example.exoplayerplayground.exo_player_business

import android.content.Context
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.example.exoplayerplayground.R
import java.io.File

/*
var cacheData : CacheDataSource.Factory? = null
var simpleCache:SimpleCache?=null

object PreLoadVideoBuilder {


    fun getPreLoad(context: Context): PreloadVideos {

        val cacheDataSourceFactory = getCacheDataSourceFactory(context)

        return createPreloadVideo(context, cacheDataSourceFactory)
    }

    fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val standaloneDatabase = createStandaloneDatabase(context)
        val leastRecentlyUsedCacheEvictor = createLeastRecentlyUsedEnvictor()
        val cache = createCacheDir(context)

        val simpleCache =
            createSimpleCache(cache, leastRecentlyUsedCacheEvictor, standaloneDatabase)
        val defaultHttpDataSource = createDefaultDataSourceFactory(context)

       return if (cacheData==null){
            cacheData = createCacheDataSourceFactory(simpleCache,defaultHttpDataSource)
            cacheData!!
        }else{
            cacheData!!
        }

    }


    private fun createPreloadVideo(
        context: Context,
        cacheDataSourceFactory: CacheDataSource.Factory
    ): PreloadVideos {
        return PreloadVideos(context, cacheDataSourceFactory)
    }


    private fun createCacheDataSourceFactory(
        simpleCache: SimpleCache,
        httpDataSource: DefaultHttpDataSource.Factory
    ): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSource)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }


    private fun createSimpleCache(
        cacheDire: File,
        evictor: LeastRecentlyUsedCacheEvictor,
        standaloneDatabaseProvider: StandaloneDatabaseProvider
    ): SimpleCache {
        return if (simpleCache==null){
            simpleCache = SimpleCache(cacheDire, evictor, standaloneDatabaseProvider)
            simpleCache!!
        }else{
            simpleCache!!
        }
    }

    private fun createDefaultDataSourceFactory(context: Context): DefaultHttpDataSource.Factory {
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

    private fun createCacheDir(context: Context): File {
        return context.cacheDir
    }


    private fun createLeastRecentlyUsedEnvictor(): LeastRecentlyUsedCacheEvictor {
        val exoPlayerCacheSize: Long = 250 * 1024 * 1024 //250mb
        return LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
    }


    private fun createStandaloneDatabase(context: Context): StandaloneDatabaseProvider {
        return StandaloneDatabaseProvider(context)
    }

}*/
