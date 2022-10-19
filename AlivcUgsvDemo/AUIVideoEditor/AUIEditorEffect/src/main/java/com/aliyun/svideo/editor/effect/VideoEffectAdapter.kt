package com.aliyun.svideo.editor.effect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoEffectAdapter : RecyclerView.Adapter<VideoEffectAdapter.VideoEffectViewHolder>() {

    var mDataList: ArrayList<VideoEffectItemViewModel> = ArrayList()
    var mVideoEffectClickListener:VideoEffectClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoEffectViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.ugsv_editor_effect_item, parent, false)
        return VideoEffectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoEffectViewHolder, position: Int) {
        holder.bindViewModel(mDataList.get(position))
        holder.itemView.setOnClickListener(View.OnClickListener {
            var model = mDataList.get(position)
            mVideoEffectClickListener?.onEffectSelected(model.sourcePath, model.title!!)
        })
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    open fun setVideoEffectSelectListener(listener: VideoEffectClickListener) {
        mVideoEffectClickListener = listener
    }

    fun setData(data: ArrayList<VideoEffectItemViewModel>) {
        mDataList.clear()
        mDataList.addAll(data)
        notifyDataSetChanged()
    }

    inner class VideoEffectViewHolder(contentView: View) : RecyclerView.ViewHolder(contentView) {

        var mImageView = contentView.findViewById<ImageView>(R.id.ugsv_editor_effect_item_image)
        var mTextView = contentView.findViewById<TextView>(R.id.ugsv_editor_clip_item_title)
        fun bindViewModel(viewModel: VideoEffectItemViewModel) {
            viewModel.loadIcon(mImageView, itemView.context)
            viewModel.title = viewModel.getFilterName(itemView.context)
            mTextView.setText(viewModel.title)
        }
    }
}