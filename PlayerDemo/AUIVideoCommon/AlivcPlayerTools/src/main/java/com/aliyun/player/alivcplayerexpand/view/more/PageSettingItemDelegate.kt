package com.aliyun.player.alivcplayerexpand.view.more

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.player.alivcplayerexpand.R
import com.aliyun.player.alivcplayerexpand.databinding.LayoutVideoMorePageSettingItemBinding
import com.aliyun.video.common.ui.adapter.ItemClickDelegate
import com.aliyun.video.common.ui.adapter.MultiSelectedAdapter
import com.aliyun.video.common.ui.getItemView

class PageSettingItemDelegate(clickListener: ItemClickListener<VideoSettingItemInfo>) :
    ItemClickDelegate<VideoSettingItemInfo, PageSettingItemDelegate.ViewHolder>(clickListener) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mItemBinding = LayoutVideoMorePageSettingItemBinding.bind(itemView)
        fun bind(
            position: Int,
            item: VideoSettingItemInfo,
            selected: Boolean
        ) {
            mItemBinding.apply {
                itemName.isSelected = selected
                itemName.text = item.content
            }
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        item: VideoSettingItemInfo
    ) {
        super.onBindViewHolder(holder, item)
        val selected =
            (adapter as? MultiSelectedAdapter)?.getSelectedPosition() == holder.layoutPosition
        holder.bind(holder.layoutPosition, item, selected)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) = ViewHolder(
        parent.getItemView(
            R.layout.layout_video_more_page_setting_item
        )
    )
}