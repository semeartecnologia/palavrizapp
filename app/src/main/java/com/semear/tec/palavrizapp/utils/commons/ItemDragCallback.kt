package com.semear.tec.palavrizapp.utils.commons

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class ItemDragCallback(val adapter: ItemTouchHelperContract) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, 0)
    }


    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        adapter.onRowMoved(p1.adapterPosition, p2.adapterPosition)
        return true
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
    }

    interface ItemTouchHelperContract {

        fun onRowMoved(fromPosition: Int, toPosition: Int)

    }
}