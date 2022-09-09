package com.example.exoplayerplayground.recyclerView

import android.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.exoplayerplayground.databinding.ItemLayoutVideoListBinding


class VideoPlayerRecyclerAdapter :
    ListAdapter<MediaObject, RecyclerView.ViewHolder>(BaseDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemLayoutVideoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VideoPlayerViewHolder).onBind(getItem(position))
    }

    class VideoPlayerViewHolder(private val binding: ItemLayoutVideoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //        private var media_container: FrameLayout
//        var title: TextView
//        var thumbnail: ImageView
//        var volumeControl: ImageView
//        var progressBar: ProgressBar
        private var parent: View = itemView

        fun onBind(item: MediaObject) {
            parent.tag = this
            binding.apply {

                parent.tag = this
                title.text = item.title
                Glide.with(root).load(item.thumbnail).into(thumbnail)

            }


        }

    }

}

class BaseDiffUtil<T>() : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}