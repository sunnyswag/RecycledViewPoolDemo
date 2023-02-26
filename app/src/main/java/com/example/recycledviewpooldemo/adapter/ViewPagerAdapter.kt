package com.example.recycledviewpooldemo.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.recycledviewpooldemo.ViewPagerItemFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return ITEM_COUNT
    }

    override fun createFragment(position: Int): ViewPagerItemFragment {
        return ViewPagerItemFragment.newInstance()
    }

    companion object {
        private const val TAG = "ViewPagerAdapter"
        private const val ITEM_COUNT = 5
    }
}