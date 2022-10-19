package com.aliyun.svideo.editor.clip.audioeffect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.svideo.editor.clip.R

class AudioEffectAdapter : RecyclerView.Adapter<AudioEffectAdapter.AudioEffectViewHolder>() {

    var mDataList: ArrayList<AudioEffectViewModel> = ArrayList()
    var mAudioEffectClickListener:AudioEffectClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioEffectViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.ugsv_editor_clip_item, parent, false)
        return AudioEffectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AudioEffectViewHolder, position: Int) {
        holder.bindViewModel(mDataList.get(position))
        holder.itemView.setOnClickListener(View.OnClickListener {
            var model = mDataList.get(position)
            model.mSelect = !model.mSelect
            mAudioEffectClickListener?.onEffectSelected(model.mEffectType)
        })
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    open fun setAudioSelectListener(listener: AudioEffectClickListener) {
        mAudioEffectClickListener = listener
    }

    fun setData(data: ArrayList<AudioEffectViewModel>) {
        mDataList.clear()
        mDataList.addAll(data)
        notifyDataSetChanged()
    }

    inner class AudioEffectViewHolder(contentView: View) : RecyclerView.ViewHolder(contentView) {

        var mImageView: ImageView = contentView.findViewById(R.id.ugsv_editor_clip_item_image)
        var mTextView: TextView = contentView.findViewById(R.id.ugsv_editor_clip_item_title)
        var mSelectedView: View = contentView.findViewById(R.id.ugsv_editor_clip_item_select)

        fun bindViewModel(viewModel: AudioEffectViewModel) {
            mImageView.setImageResource(viewModel.mImageResId)
            mTextView.setText(viewModel.mTextResId)
            if (viewModel.mSelect) {
                mSelectedView.visibility = View.VISIBLE
            } else {
                mSelectedView.visibility = View.INVISIBLE
            }
        }
    }
}