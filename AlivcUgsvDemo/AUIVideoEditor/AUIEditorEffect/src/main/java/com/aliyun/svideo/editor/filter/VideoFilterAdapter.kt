package com.aliyun.svideo.editor.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.svideo.editor.effect.R
import com.aliyun.ugsv.common.utils.StringUtils

class VideoFilterAdapter(videoFilterManager: VideoFilterManager) : RecyclerView.Adapter<VideoFilterAdapter.VideoEffectViewHolder>() {

    val mVideoFilterManager = videoFilterManager
    var mDataList: ArrayList<VideoFilterItemViewModel> = ArrayList()
    var mVideoFilterClickListener:VideoFilterClickListener ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoEffectViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.ugsv_editor_effect_item, parent, false)
        return VideoEffectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoEffectViewHolder, position: Int) {
        var viewModel = mDataList.get(position)
        holder.bindViewModel(viewModel, viewModel == mVideoFilterManager.mVideoFilterItemViewModel)
        holder.itemView.setOnClickListener(View.OnClickListener {
            mVideoFilterClickListener?.onEffectSelected(viewModel)
            notifyDataSetChanged()
        })
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    open fun setVideoFilterSelectListener(listener: VideoFilterClickListener) {
        mVideoFilterClickListener = listener
    }

    fun setData(data: ArrayList<VideoFilterItemViewModel>) {
        mDataList.clear()
        mDataList.addAll(data)
        if (mVideoFilterManager.mVideoFilterItemViewModel == null) {
            mVideoFilterManager.setVideoFilterItem(mDataList.get(0))
        }
        notifyDataSetChanged()
    }

    inner class VideoEffectViewHolder(contentView: View) : RecyclerView.ViewHolder(contentView) {

        var mImageView = contentView.findViewById<ImageView>(R.id.ugsv_editor_effect_item_image)
        var mTextView = contentView.findViewById<TextView>(R.id.ugsv_editor_clip_item_title)
        var mSelectView = contentView.findViewById<ImageView>(R.id.ugsv_editor_select_view)
        fun bindViewModel(viewModel: VideoFilterItemViewModel, isSelected: Boolean) {
            if (StringUtils.isEmpty(viewModel.sourcePath)) {
                mTextView.setText(viewModel.title)
                mImageView.setImageResource(R.drawable.ugsv_video_filter_reset)
            } else {
                viewModel.loadIcon(mImageView, itemView.context)
                viewModel.title = viewModel.getFilterName(itemView.context)
                mTextView.setText(viewModel.title)
            }
            if (isSelected) {
                mSelectView.visibility = View.VISIBLE
                mImageView.alpha = 0.5f
                mTextView.setTextColor(itemView.resources.getColor(R.color.text_ultraweak))
            } else {
                mSelectView.visibility = View.GONE
                mImageView.alpha = 1.0f
                mTextView.setTextColor(itemView.resources.getColor(R.color.text_strong))
            }

        }
    }
}