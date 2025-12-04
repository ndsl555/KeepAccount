package com.example.keepaccount.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.keepaccount.R
import com.example.keepaccount.ViewModels.Example2Item

class RankItemListAdapter :
    ListAdapter<Example2Item, RankItemListAdapter.ItemViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<Example2Item>() {
        //  用名稱唯一判斷是否同一個 item
        override fun areItemsTheSame(
            oldItem: Example2Item,
            newItem: Example2Item,
        ): Boolean = oldItem.itemname == newItem.itemname

        //  檢查內容有沒有變更
        override fun areContentsTheSame(
            oldItem: Example2Item,
            newItem: Example2Item,
        ): Boolean = oldItem == newItem
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtRank: TextView = itemView.findViewById(R.id.txtnum)
        val txtName: TextView = itemView.findViewById(R.id.txtname)
        val txtPrice: TextView = itemView.findViewById(R.id.txtprice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rank_item, parent, false)

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
    ) {
        val current = getItem(position)

        holder.txtName.text = current.itemname
        holder.txtPrice.text = current.itemprice.toString()
        holder.txtRank.text = "第${position + 1}名" // ⬅️ 保留你的邏輯
    }
}
