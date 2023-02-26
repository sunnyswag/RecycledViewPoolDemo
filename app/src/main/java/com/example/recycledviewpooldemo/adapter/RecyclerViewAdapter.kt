package com.example.recycledviewpooldemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recycledviewpooldemo.R
import com.example.recycledviewpooldemo.databinding.RecyclerviewItemBinding

class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvOnCreate.apply {
            text = "onCreateViewHolder invoke, viewType: $viewType"
            visibility = View.VISIBLE
        }
        return if (viewType == VIEW_TYPE_BLUE) {
            BlueViewHolder(binding)
        } else {
            PurpleViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BlueViewHolder) {
            holder.binding.tvOnBind.text = "onBindViewHolder invoke, position: $position"
            holder.binding.tvOnBind.visibility = View.VISIBLE
        } else if (holder is PurpleViewHolder) {
            holder.binding.tvOnBind.text = "onBindViewHolder invoke, position: $position"
            holder.binding.tvOnBind.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return ITEM_COUNT
    }

    override fun getItemViewType(position: Int): Int {
        val middle = ITEM_COUNT / 2
        return if (position <= middle) {
            VIEW_TYPE_BLUE
        } else {
            VIEW_TYPE_PURPLE
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is BlueViewHolder) {
            holder.binding.tvOnCreate.visibility = View.INVISIBLE
            holder.binding.tvOnBind.visibility = View.INVISIBLE
        } else if (holder is PurpleViewHolder) {
            holder.binding.tvOnCreate.visibility = View.INVISIBLE
            holder.binding.tvOnBind.visibility = View.INVISIBLE
        }
    }

    class BlueViewHolder(val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setBackgroundResource(R.color.blue_test)
        }
    }
    class PurpleViewHolder(val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setBackgroundResource(R.color.purple_test)
        }
    }

    companion object {
        private const val TAG = "RecyclerViewAdapter"
        const val ITEM_COUNT = 40

        const val VIEW_TYPE_BLUE = 0
        const val VIEW_TYPE_PURPLE = 1
    }
}