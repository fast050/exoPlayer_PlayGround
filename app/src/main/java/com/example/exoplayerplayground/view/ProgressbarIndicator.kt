package com.example.exoplayerplayground.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.exoplayerplayground.R
import com.example.exoplayerplayground.exo_player_business.MediaAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ProgressbarIndicator : LinearLayout {

    private var pbWidth = -1
    private var pbHeight = -1
    private var pbMargin = -1
    private var mLastPosition = -1
    private var mRecyclerView: RecyclerView? = null
    private var mSnapHelper: SnapHelper? = null
    private var exoPlayer: ExoPlayer? = null

    private var activeProgressBar: ProgressBar? = null

    private var holder: RecyclerView.ViewHolder? = null
    private var imageProgressCounter: Int = 0

    constructor(context: Context?) : super(context!!) {
        init(context, null)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context!!, attrs) {
        init(context, attrs)
    }


    constructor(
        context: Context?,
        attrs: AttributeSet?, defStyle: Int

    ) : super(context!!, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        if (attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressbarIndicator)
        pbWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressbarIndicator_pb_width, -1)
        pbHeight = typedArray.getDimensionPixelSize(R.styleable.ProgressbarIndicator_pb_height, -1)
        pbMargin = typedArray.getDimensionPixelSize(R.styleable.ProgressbarIndicator_pb_margin, 20)
        typedArray.recycle()

        if (isInEditMode) {
            createPbIndicators(3, 1)
        }

    }


    private fun createPbIndicators(count: Int, currentPosition: Int) {

        // Diff View
        val childViewCount = childCount
        if (count < childViewCount) {
            removeViews(count, childViewCount - count)
        } else if (count > childViewCount) {
            val addCount = count - childViewCount
            val orientation = orientation
            for (i in 0 until addCount) {
                addProgressbarIndicator(orientation)
            }
        }

        mLastPosition = currentPosition
    }


    private fun addProgressbarIndicator(orientation: Int) {
        val indicator = ProgressBar(
            context,
            null,
            0,
            androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal
        )
        val params = generateDefaultLayoutParams()
        params.weight = 1.0f
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = pbHeight

        if (orientation == HORIZONTAL) {
            params.marginStart = pbMargin
            params.marginEnd = pbMargin

        } else {
            params.topMargin = pbMargin
            params.bottomMargin = pbMargin
        }
        addView(indicator, params)
    }


    fun attachToRecyclerView(
        recyclerView: RecyclerView,
        snapHelper: SnapHelper,
        exoPlayer: ExoPlayer
    ) {
        mRecyclerView = recyclerView
        mSnapHelper = snapHelper
        mLastPosition = -1
        this.exoPlayer = exoPlayer
        createPbIndicator()
        recyclerView.addOnScrollListener(mOnScrollListener)
    }

    private fun createPbIndicator() {
        val count = mRecyclerView?.adapter?.itemCount ?: 0
        createPbIndicators(count, getSnapPosition(mRecyclerView?.layoutManager))
    }

    private fun getSnapPosition(layoutManager: RecyclerView.LayoutManager?): Int {
        if (layoutManager == null) {
            return RecyclerView.NO_POSITION
        }
        val snapView = mSnapHelper?.findSnapView(layoutManager)
            ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }


    private val mAdapterDataObserver: RecyclerView.AdapterDataObserver =
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (mRecyclerView == null) {
                    return
                }
                val adapter = mRecyclerView!!.adapter
                val newCount = adapter?.itemCount ?: 0
                val currentCount = childCount
                mLastPosition = if (newCount == currentCount) {
                    // No change

                    return
                } else if (mLastPosition < newCount) {
                    getSnapPosition(mRecyclerView!!.layoutManager)
                } else {
                    RecyclerView.NO_POSITION
                }
                createPbIndicator()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                onChanged()
            }

            override fun onItemRangeChanged(
                positionStart: Int, itemCount: Int,
                payload: Any?
            ) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                onChanged()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                onChanged()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                onChanged()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                onChanged()
            }
        }

    fun getAdapterDataObserver(): RecyclerView.AdapterDataObserver {
        return mAdapterDataObserver
    }


    private val mOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = getSnapPosition(recyclerView.layoutManager)
                if (position == RecyclerView.NO_POSITION) {
                    return
                }
                itemSelected(position)
            }
        }


    private fun itemSelected(position: Int) {
        if (mLastPosition == position) return

        val totalItem = mRecyclerView!!.adapter?.itemCount ?: 0

        //when user scroll left to right then all left progress bar must be set to 100
        if (mLastPosition >= 0 && getChildAt(mLastPosition) != null) {
            val lastPb: ProgressBar = getChildAt(mLastPosition) as ProgressBar
            lastPb.progress = 100
        }

        //when user scrolled right to left then all right progress bar position  must be reset to 0
        (position until totalItem step 1).forEach {
            val resetNextPb: ProgressBar = getChildAt(it) as ProgressBar
            resetNextPb.progress = 0
        }

        //current progress bar
        val pb: ProgressBar = getChildAt(position) as ProgressBar
        activeProgressBar = pb


        holder = mRecyclerView?.findViewHolderForLayoutPosition(position)
        imageProgressCounter = 0 //reset on each item selected
        mLastPosition = position

    }


    private val progressJob = CoroutineScope(Dispatchers.Main).launch {
        updateProgress().collectLatest {
           // printLog("ProgressBar Indicator ProgressBar:$it")
            activeProgressBar?.progress = it
        }
    }


    private fun updateProgress() = flow<Int> {
        while (true) {
            when (holder) {
                is MediaAdapter.PlayerVH -> {
                  //  printLog("Progress_bar player holder")
                    emit(
                        ((exoPlayer?.currentPosition?.toFloat()?.div(exoPlayer!!.duration.toFloat())
                            ?.times(100))?.toInt()!!)
                    )
                    delay(100)
                }
                is MediaAdapter.ImageVH -> {
                  //  printLog("Progress_bar image holder")
                    if ((holder as MediaAdapter.ImageVH).binding.photoIv.drawable != null) { //make sure drawable loaded
                        emit(imageProgressCounter)
                        imageProgressCounter += 10
                        delay(250)

                        if (imageProgressCounter == 100) {
                            imageProgressCounter = 0
                            moveToNextItem()
                        }
                    } else {
                        delay(100)
                    }
                }
                else -> {
                   // printLog("holder doesn't support type")
                    delay(100)
                }

            }


        }
    }.flowOn(Dispatchers.Main)


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressJob.cancel()
    }

    private fun moveToNextItem() {
        val adapter = mRecyclerView!!.adapter
        val index = if (mLastPosition + 1 < adapter!!.itemCount) mLastPosition + 1 else 0

        mRecyclerView!!.smoothScrollToPosition(index)
    }
}