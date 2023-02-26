package com.example.recycledviewpooldemo

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

class RecycledViewPoolTest: RecyclerView.RecycledViewPool() {

    override fun putRecycledView(scrap: RecyclerView.ViewHolder?) {
        super.putRecycledView(scrap)
        Log.d(TAG, "putRecycledView: scrap.hashCode(): ${scrap?.hashCode()}")
    }

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        return super.getRecycledView(viewType).also {
            Log.d(TAG, "getRecycledView: it.hashCode(): ${it?.hashCode()}")
        }
    }

    override fun clear() {
        Log.d(TAG, "clear: ")
        super.clear()
    }

    companion object {
        private const val TAG = "RecycledViewPoolTest"
    }
}