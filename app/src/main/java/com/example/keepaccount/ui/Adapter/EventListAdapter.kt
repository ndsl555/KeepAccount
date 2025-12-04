package com.example.itembook.ui.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.databinding.EventListItemBinding

class EventListAdapter : RecyclerView.Adapter<EventListAdapter.ItemViewHolder>() {
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    var items: List<Event>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    var onItemClick: ((Event) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EventListItemBinding.inflate(inflater, parent, false)
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
        private val binding: EventListItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.apply {
                binding.itemName.text = event.eventName
                println(event.eventColorCode)
                binding.itemColorshow.setBackgroundColor(event.eventColorCode.toColorInt())
                root.setOnClickListener {
                    onItemClick?.invoke(event)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Event>() {
                override fun areItemsTheSame(
                    oldItem: Event,
                    newItem: Event,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Event,
                    newItem: Event,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
