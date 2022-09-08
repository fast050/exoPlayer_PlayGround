package com.example.exoplayerplayground

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.navigation.fragment.findNavController
import com.example.exoplayerplayground.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var player: ExoPlayer? = null

    private val playbackStateListener: Player.Listener = playbackStateListener()

    private val TAG = "PlayerActivity"



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonFirst.setOnClickListener {
            //player?.play()
        }
    }


    /**
     *
    Android API level 24 and higher supports multiple windows.
    As your app can be visible, but not active in split window mode,
    you need to initialize the player in onStart. Android API level 23
    and lower requires you to wait as long as possible until you grab resources,
    so you wait until onResume before initializing the player.
     */


    override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23)
            initializePlayer(requireContext())

    }

    override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23 || player == null)
            initializePlayer(requireContext())
    }


    private fun initializePlayer(context: Context) {

        /*
        * First, create a DefaultTrackSelector, which is responsible for choosing tracks in the media item. Then,
        * tell your trackSelector to only pick tracks of standard definition or lower—a good way of saving your user's
        *  data at the expense of quality. Lastly,pass your trackSelector to your builder so that it is used when building
        * the ExoPlayer instance
        * */
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.exoPlayerPv.player = exoPlayer
            }

        addListenerToPlayer()
        addItemToPlay()
        setYouPlayerFromWhereItPause()
    }
    private fun addListenerToPlayer(){
        player?.addListener(playbackStateListener)
    }

    private fun removeListenerFromPlayer(){
        player?.removeListener(playbackStateListener)
    }

    private fun addItemToPlay() {
/*        val item = MediaItem.fromUri(MP4Item)
        val secondMediaItem = MediaItem.fromUri(MP3Item)

        player?.setMediaItem(item)

        player?.addMediaItem(secondMediaItem)*/

        val mediaItem = MediaItem.Builder()
            .setUri(getString(R.string.media_url_dash))
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()

        player?.setMediaItem(mediaItem)
    }


    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, binding.exoPlayerPv).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    /**
     * With API Level 23 and lower, there is no guarantee of onStop
    being called, so you have to release the player as early as possible
    in onPause. With API Level 24 and higher (which brought multi- and split-window mode),
    onStop is guaranteed to be called. In the paused state, your activity is still visible,
    so you wait to release the player until onStop.
     */

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    /**
     *
    -Play/pause state using playWhenReady.
    -Current playback position using currentPosition.
    -Current media item index using currentMediaItemIndex.
    This allows you to resume playback from where the user left off.
    All you need to do is supply this state information when you initialize your player
     */

    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        removeListenerFromPlayer()
        player = null
    }

    private fun setYouPlayerFromWhereItPause() {
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentItem, playbackPosition)
        player?.prepare()

    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")



        }
    }


    private fun createBackStateListener() = object : AnalyticsListener {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val MP3Item = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"
        const val MP4Item ="https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    }
}


