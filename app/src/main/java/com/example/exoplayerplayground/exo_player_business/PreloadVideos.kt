package com.example.exoplayerplayground.exo_player_business

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * cache video from url with coroutine
 */
class PreloadVideos @Inject constructor(
    @ApplicationContext val context: Context,
    private val cacheDataSourceFactory: CacheDataSource.Factory
) {


    fun <T> cacheVideos(videos: List<T>) {
        if (videos.isEmpty()) return
        val videoUrls: MutableList<String> = ArrayList()
        val cacheDataSource = cacheDataSourceFactory.createDataSource()
        val progressListener =
            CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                val downloadPercentage: Double = (bytesCached * 100.0
                        / requestLength)

              //  printLog("pre-load download:$downloadPercentage")
            }



        when {
            videos.first() is MockMediaResponse -> {
                (videos as List<MockMediaResponse>).forEach {
                    videoUrls.add(it.videoUrl)
                }
            }
        }

        videoUrls.forEach {
            val url = it
            val videoUri = Uri.parse(url)
//            printLog("videoUrl for caching $videoUri")

            val dataSpec = DataSpec(videoUri)

            val uiScope = CoroutineScope(Dispatchers.IO)
            uiScope.launch {
                kotlin.runCatching {
                    CacheWriter(
                        cacheDataSource,
                        dataSpec,
                        null,
                        progressListener
                    ).cache()
                }.onFailure { e ->
//                    Log.d(TAG, "cacheVideos: ")(
//                        "pre-load cache exception: localize msg: " + e.localizedMessage + "  message: " + e.message,
//                        true
//                    )
                }

            }
        }
    }
}