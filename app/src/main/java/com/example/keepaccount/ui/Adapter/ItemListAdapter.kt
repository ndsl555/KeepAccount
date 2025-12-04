package com.example.itembook.ui.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.keepaccount.ViewModels.ShowItem
import com.example.keepaccount.databinding.ItemListItemBinding

class ItemListAdapter : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    var items: List<ShowItem>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(
        private val context: Context,
        private val binding: ItemListItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShowItem) {
            binding.apply {
                binding.itemName.text = item.name
                binding.itemCost.text = item.cost.toString()
                binding.itemCost.setTextColor(item.color.toColorInt())
                println(item.name)
                root.setOnClickListener {
                    onItemClick?.invoke(item.name)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ShowItem>() {
                override fun areItemsTheSame(
                    oldItem: ShowItem,
                    newItem: ShowItem,
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                override fun areContentsTheSame(
                    oldItem: ShowItem,
                    newItem: ShowItem,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
