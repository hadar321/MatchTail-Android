package com.example.matchtail.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddedItemDecoration : RecyclerView.ItemDecoration() {
    private val spaceSize = 25

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.top = spaceSize
    }
}