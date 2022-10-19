package com.aliyun.video.common.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate

open abstract class ItemClickDelegate<T, VH : RecyclerView.ViewHolder>(val clickListener: ItemClickListener<T>) :
    ItemViewDelegate<T, VH>() {
    override fun onBindViewHolder(holder: VH, item: T) {
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(holder.layoutPosition, item)
        }
    }

    interface ItemClickListener<T> {
        fun onItemClick(position: Int, entity: T)
    }
}