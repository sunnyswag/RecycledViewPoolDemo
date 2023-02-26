package com.example.recycledviewpooldemo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.recycledviewpooldemo.RecycledViewPoolTest

class RecyclerViewItemViewModel: ViewModel() {

    var viewPool = RecycledViewPoolTest().apply {
        setMaxRecycledViews(0, 10)
    }
        private set

    fun clearViewPool() {
        viewPool.clear()
    }

    override fun onCleared() {
        super.onCleared()
        viewPool.clear()
    }
}