package com.aliyun.video.play.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.player.alivcplayerexpand.util.ImageLoader
import com.aliyun.video.R
import com.aliyun.video.common.ui.adapter.ItemClickDelegate
import com.aliyun.video.common.ui.getItemView
import com.aliyun.video.databinding.LayoutRecommendItemBinding


class RecommendVideoItemDelegate(clickListener: ItemClickListener<VideoInfo>) :
    ItemClickDelegate<VideoInfo, RecommendVideoItemDelegate.ViewHolder>(
        clickListener
    ) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mItemViewBinding = LayoutRecommendItemBinding.bind(itemView)
        fun bind(position: Int, item: VideoInfo) {
            mItemViewBinding.apply {
                ImageLoader.loadImg(item.coverUrl, mRecommendCover, R.drawable.item_default_image)
                mRecommendVideoTitle.text = item.title
                mVideoDuration.text = DateUtils.formatElapsedTime(item.duration.toLong())
            }
        }

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        item: VideoInfo
    ) {
        super.onBindViewHolder(holder, item)
        holder.bind(holder.layoutPosition, item)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder = ViewHolder(
        parent.getItemView(
            R.layout.layout_recommend_item
        )
    )
}