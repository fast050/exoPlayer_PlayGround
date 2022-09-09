package com.example.exoplayerplayground.exo_player_business

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRvHorizontalScrollListener(lm: RecyclerView.LayoutManager) :
    RecyclerView.OnScrollListener() {
    private val layoutManager = lm as LinearLayoutManager
    var lastPosition: Int? = null

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        val position: Int = layoutManager.findFirstVisibleItemPosition()
        val rect = Rect()
        layoutManager.findViewByPosition(position)?.getGlobalVisibleRect(rect)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        val lastPosition = layoutManager.findLastVisibleItemPosition()

        val globalVisibleRect = Rect()
        recyclerView.getGlobalVisibleRect(globalVisibleRect)
        for (pos in firstPosition..lastPosition) {
            val view = layoutManager.findViewByPosition(pos)
            if (view != null) {
                val percentage = getVisibleWidtPercentage(view)
                if (percentage == 100.0) {
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> {

                            if (this.lastPosition != layoutManager.findFirstVisibleItemPosition()) {
                                idleState(layoutManager.findFirstVisibleItemPosition())
                                this.lastPosition =
                                    layoutManager.findLastVisibleItemPosition() // when  smoothScrollToPosition will call then we have to set lastPosition

                            }
                        }
                        RecyclerView.SCROLL_STATE_DRAGGING -> {

                            this.lastPosition = layoutManager.findLastVisibleItemPosition()

                        }
                    }
                    // idleState(layoutManager.findFirstVisibleItemPosition())
                }

            }
        }


    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        loadMore(layoutManager)
    }


    abstract fun loadMore(layoutManager: LinearLayoutManager)
    abstract fun idleState(position: Int)

    //Method to calculate how much of the view is visible
    private fun getVisibleWidtPercentage(view: View): Double {

        val itemRect = Rect()
        val isParentViewEmpty = view.getLocalVisibleRect(itemRect)

        // Find the height of the item.
        val visibleWidth = itemRect.width().toDouble()
        val width = view.width

        val viewVisibleWidthPercentage = visibleWidth / width * 100

        if (isParentViewEmpty) {
            return viewVisibleWidthPercentage
        } else {
            return 0.0
        }
    }
}
