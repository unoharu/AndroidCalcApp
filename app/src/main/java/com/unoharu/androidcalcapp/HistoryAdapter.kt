package com.unoharu.androidcalcapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private val onItemClick: (HistoryItem) -> Unit,
) : ListAdapter<HistoryItem, HistoryAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    private object DiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.textView.text = "${item.expression} = ${item.result}"
        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}
