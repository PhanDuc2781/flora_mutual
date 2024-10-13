package com.cmd.flora.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val isTop = position == 0
        if (isTop) {
            outRect.top = space
        }
    }
}