package com.kihare.todo.app.recyclerView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView

class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView?) {
        drawVertical(c as Canvas, parent as RecyclerView)
    }

    fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView?) {
        outRect.set(0, 0, 0, mDivider.intrinsicHeight)
    }

    companion object {

        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}