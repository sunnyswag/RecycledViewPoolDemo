package com.example.recycledviewpooldemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recycledviewpooldemo.adapter.RecyclerViewAdapter
import com.example.recycledviewpooldemo.databinding.ViewpagerItemBinding
import com.example.recycledviewpooldemo.viewmodel.RecyclerViewItemViewModel

class ViewPagerItemFragment: Fragment() {

    lateinit var binding: ViewpagerItemBinding
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(RecyclerViewItemViewModel::class.java)
    }

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
            setRecycledViewPool(viewModel.viewPool)
            adapter = RecyclerViewAdapter()
            layoutManager = LinearLayoutManager(context)
            setItemViewCacheSize(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearViewPool()
    }

    companion object {
        private const val TAG = "ViewPagerItemFragment"

        fun newInstance(): ViewPagerItemFragment {
            return ViewPagerItemFragment()
        }
    }
}