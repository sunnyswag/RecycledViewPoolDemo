package com.example.recycledviewpooldemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recycledviewpooldemo.databinding.RecyclerviewItemBinding

class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvOnCreate.apply {
            text = "onCreateViewHolder invoke, viewType: $viewType"
            visibility = View.VISIBLE
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvOnBind.apply {
            text = "onBindViewHolder invoke, position: $position"
            visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return ITEM_COUNT
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.tvOnBind.visibility = View.INVISIBLE
        holder.binding.tvOnCreate.visibility = View.INVISIBLE
    }

    class ViewHolder(val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val TAG = "RecyclerViewAdapter"
        const val ITEM_COUNT = 20
    }
}