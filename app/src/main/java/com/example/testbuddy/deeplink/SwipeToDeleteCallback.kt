package com.example.testbuddy.deeplink

import android.drm.DrmRights
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToDeleteCallback : ItemTouchHelper.Callback() {

    //region Variables
    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val background = ColorDrawable()

    //endregion


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)

    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height

        val isCancelled = dX == 0f && !isCurrentlyActive
//        if (isCancelled) {
//        } else {
//            background.apply {
//                color = Color.parseColor("#b80f0a")
//                setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
//                draw(c)
//            }
//
//
//
//
//        }
        clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    //region Private Helpers
    private fun clearCanvas(
        c: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }
    //endregion
}