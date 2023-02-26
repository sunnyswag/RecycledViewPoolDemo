package com.example.recycledviewpooldemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recycledviewpooldemo.adapter.RecyclerViewAdapter
import com.example.recycledviewpooldemo.databinding.ViewpagerItemBinding

class ViewPagerItemFragment: Fragment() {

    lateinit var binding: ViewpagerItemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewpagerItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRoot.apply {
            adapter = RecyclerViewAdapter()
            layoutManager = LinearLayoutManager(context)
            setItemViewCacheSize(0)
        }
    }

    companion object {
        private const val TAG = "ViewPagerItemFragment"

        fun newInstance(): ViewPagerItemFragment {
            return ViewPagerItemFragment()
        }
    }
}