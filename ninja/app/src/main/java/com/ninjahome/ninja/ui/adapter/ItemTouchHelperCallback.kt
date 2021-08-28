package com.ninjahome.ninja.ui.adapter

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import com.ninjahome.ninja.utils.itemtouchhelper.ItemTouchHelper
import kotlinx.android.synthetic.main.item_message.view.*

/**
 *Author:Mr'x
 *Time:2021/8/27
 *Description:
 */
class ItemTouchHelperCallback : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        //设置滑动方向
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (dY != 0.0f && dX == 0.0f) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        val contentCl = viewHolder.itemView.contentCl
        val actionLl = viewHolder.itemView.actionLl
        val width = actionLl.width
        var translationX = dX
        if (translationX < -width) {
            translationX = (-width).toFloat()
        }
        contentCl.translationX = translationX
        println("dX:${dX}   translationX:${translationX}")


    }

}