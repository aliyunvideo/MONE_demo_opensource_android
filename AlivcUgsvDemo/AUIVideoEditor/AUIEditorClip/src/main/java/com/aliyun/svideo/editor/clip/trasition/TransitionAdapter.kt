package com.aliyun.svideo.editor.clip.trasition

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.svideo.editor.clip.R

class TransitionAdapter : RecyclerView.Adapter<TransitionAdapter.TransitionViewHolder>() {

    var mDataList: ArrayList<TransitionViewModel> = ArrayList()
    var mTransitionClickListener:TransitionClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransitionViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.ugsv_editor_clip_item, parent, false)
        itemView.layoutParams.width = (parent.context.resources.displayMetrics.density * 60).toInt()
        return TransitionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransitionViewHolder, position: Int) {
        holder.bindViewModel(mDataList.get(position))
        holder.itemView.setOnClickListener(View.OnClickListener {
            var model = mDataList.get(position)
            model.mSelect = !model.mSelect
            mTransitionClickListener?.onEffectSelected(model.mEffectType)
        })
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    open fun setTransitionSelectListener(listener: TransitionClickListener) {
        mTransitionClickListener = listener
    }

    fun setData(data: ArrayList<TransitionViewModel>) {
        mDataList.clear()
        mDataList.addAll(data)
        notifyDataSetChanged()
    }

    inner class TransitionViewHolder(contentView: View) : RecyclerView.ViewHolder(contentView) {
        var mImageView: ImageView = contentView.findViewById(R.id.ugsv_editor_clip_item_image)
        var mTextView: TextView = contentView.findViewById(R.id.ugsv_editor_clip_item_title)
        var mSelectedView: View = contentView.findViewById(R.id.ugsv_editor_clip_item_select)

        fun bindViewModel(viewModel: TransitionViewModel) {
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