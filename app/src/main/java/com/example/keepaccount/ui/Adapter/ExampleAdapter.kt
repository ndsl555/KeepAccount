package com.example.keepaccount

import ExampleItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExampleAdapter(private val exampleList: List<ExampleItem>) :
    RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ExampleViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(
                R.layout.today_item,
                parent,
                false,
            )

        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ExampleViewHolder,
        position: Int,
    ) {
        val currentItem = exampleList[position]

        holder.textView1.text = currentItem.itemname
        holder.textView2.text = currentItem.itemcost.toString()
    }

    override fun getItemCount() = exampleList.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.text_view_1)
        val textView2: TextView = itemView.findViewById(R.id.text_view_2)
    }
}
