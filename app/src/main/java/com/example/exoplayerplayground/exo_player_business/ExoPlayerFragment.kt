package com.example.exoplayerplayground.exo_player_business

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.exoplayerplayground.databinding.FragmentExoPlayerBinding


/** mock test screen to implement player view in service details**/
class ExoPlayerFragment : Fragment(),MediaAdapter.PlayerState {

   private var _binding: FragmentExoPlayerBinding? = null
   private val binding get() = _binding!!
   private lateinit var exoPlayer: ExoPlayer

   private val cacheDataSourceFactory: CacheDataSource.Factory by lazy {
       PreLoadVideoBuilder.getCacheDataSourceFactory(requireContext())
   }
   private lateinit var adapter: MediaAdapter

   private val preloadVideos: PreloadVideos by lazy {
       PreLoadVideoBuilder.getPreLoad(requireContext())
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentExoPlayerBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        listeners()
    }


     fun initViews() {
         Log.d("TAG", "initViews: ")

        preloadVideos.cacheVideos(MockGenerator.mediaList)
        exoPlayer = ExoPlayer.Builder(requireContext()).build()

        adapter = MediaAdapter(exoPlayer, cacheDataSourceFactory, this)
        val pagerSnapHelper = PagerSnapHelper()
        binding.mediaRv.let {
            it.adapter = adapter
            pagerSnapHelper.attachToRecyclerView(it)
        }

        adapter.submitList(MockGenerator.mediaList)
        exoPlayer.addListener(adapter)

        binding.pbIndicator.attachToRecyclerView(binding.mediaRv, pagerSnapHelper, exoPlayer)
        adapter.registerAdapterDataObserver(binding.pbIndicator.getAdapterDataObserver())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::exoPlayer.isInitialized)
            exoPlayer.release()
    }

    fun listeners() {


        binding.mediaRv.let {
            it.addOnScrollListener(object : EndlessRvHorizontalScrollListener(it.layoutManager!!) {
                override fun loadMore(layoutManager: LinearLayoutManager) {
                    //load more
                }

                override fun idleState(position: Int) {
                    when (adapter.currentList[position].type) {
                        1 -> {
                            val playerHolder = adapter.getPlayerHolder(position)
                            adapter.attachPlayer(playerHolder)

                        }
                        0 -> {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.stop()
                            }
                        }
                    }


                }
            })
        }
    }

    override fun onPlayerEnd(position: Int) {
        val index = if (position + 1 < adapter.itemCount) position + 1 else 0
        binding.mediaRv.smoothScrollToPosition(index)
    }

}