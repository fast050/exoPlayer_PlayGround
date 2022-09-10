package com.example.exoplayerplayground.exo_player_business

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.exoplayerplayground.R
import com.example.exoplayerplayground.databinding.ItemMediaImageBinding
import com.example.exoplayerplayground.databinding.ItemMediaPlayerBinding
import javax.inject.Inject

/**
 * use for images and player
 */
class MediaAdapter(
    private val exoPlayer: ExoPlayer,
    private val cacheDataSourceFactory: CacheDataSource.Factory,
    private val playerState: PlayerState
) : ListAdapter<MockMediaResponse, RecyclerView.ViewHolder>(BaseDiffUtilCallBack()),
    Player.Listener {

    private val playerVhArray: SparseArray<PlayerVH> = SparseArray()
    private var isFirstItem: Boolean = true
    private lateinit var playerVH: PlayerVH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            R.layout.item_media_image -> {
                val binding: ItemMediaImageBinding = ItemMediaImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                       parent,
                    false
                )

                return ImageVH(binding)
            }

            R.layout.item_media_player -> {
                val binding: ItemMediaPlayerBinding = ItemMediaPlayerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PlayerVH(binding)
            }
            else -> {
                throw IllegalAccessError("Can't able to catch view type of view holder")
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            0 -> {
                R.layout.item_media_image
            }
            1 -> {
                R.layout.item_media_player
            }
            else -> {
                R.layout.item_media_image
            }
        }

    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is PlayerVH -> {
                playerVH = holder
                if (isFirstItem) {
                    isFirstItem = false
                    attachPlayer(holder)
                }
            }
        }

    }

    fun attachPlayer(holder: PlayerVH) {
        playerVH = holder

        if (exoPlayer.isPlaying) {
            exoPlayer.stop()
        }

        val videoUrl = holder.item?.videoUrl

        val mediaSource: ProgressiveMediaSource =
            ProgressiveMediaSource.Factory(
                cacheDataSourceFactory
            ).createMediaSource(
                MediaItem.fromUri(Uri.parse(videoUrl))
            )

        exoPlayer.also {
            it.playWhenReady = true
            it.setMediaSource(mediaSource)
            it.prepare()
        }

        exoPlayer.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                super.onVideoSizeChanged(videoSize)
                val height = videoSize.height
                val width = videoSize.width
                if (height > width)
                    holder.binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                else
                    holder.binding.player.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
            }
        })

        playerVH.binding.player.player = null
        playerVH.binding.player.let {
            it.player = exoPlayer
            it.setKeepContentOnPlayerReset(true)
            it.setShutterBackgroundColor(Color.TRANSPARENT)
        }
    }


    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        playerVH.binding.player.invisible()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            Player.STATE_ENDED -> {
                playerVH.binding.player.invisible()
                exoPlayer.playWhenReady = false
                exoPlayer.seekTo(0)
                playerState.onPlayerEnd(playerVH.bindingAdapterPosition)
            }


            Player.STATE_READY -> {
                playerVH.binding.player.visible()
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if (isPlaying) {
            playerVH.binding.player.visible()
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is PlayerVH -> {
                holder.onBind(item)
                playerVhArray.put(position, holder)
            }
            is ImageVH -> {
                holder.onBind(item)
            }
        }

    }


    inner class PlayerVH(val binding: ItemMediaPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var item: MockMediaResponse? = null

        fun onBind(item: MockMediaResponse) {
            this.item = item

           /* binding.setVariable(BR.item, item)
            binding.executePendingBindings()*/
            Glide.with(binding.root.context).load(item.thumbnailUrl).into(binding.thumbnailIv)

        }

    }


    inner class ImageVH(val binding: ItemMediaImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: MockMediaResponse) {
          /*  binding.setVariable(BR.item, item)
            binding.executePendingBindings()*/

           Glide.with(binding.root.context).load(item.image).into(binding.photoIv)

        }
    }

    fun getPlayerHolder(position: Int): PlayerVH {
        return playerVhArray.get(position)
    }

    interface PlayerState {
        fun onPlayerEnd(position: Int)
    }
}

class BaseDiffUtilCallBack<T>() : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}
