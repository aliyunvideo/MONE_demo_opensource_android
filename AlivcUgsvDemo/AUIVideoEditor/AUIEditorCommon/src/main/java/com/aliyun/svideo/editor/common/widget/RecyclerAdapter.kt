package com.aliyun.svideo.editor.common.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * ****************************************************************************
 * Copyright (C) 2005-2022 alibaba-inc All rights reserved
 *
 * Description : 通用的RecycleViewAdapter，传入指定的数据类型和ViewDataBinding对象
 *
 * Creation : 1/6/22
 * ****************************************************************************
 */
abstract class RecyclerAdapter<T, B : ViewDataBinding>(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataList: MutableList<T>?= null

    fun refreshDataList(dataList: MutableList<T>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<B>(LayoutInflater.from(mContext), getLayoutId(viewType), parent, false)
        onViewCreated(binding, viewType)
        return RecycleHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<B>(holder.itemView)
        val data = dataList!![position]
        onBindItem(binding, data, position)
    }

    /**
     * item布局id
     * @param viewType
     * @return
     */
    @LayoutRes
    protected abstract fun getLayoutId(viewType: Int): Int

    /**
     * view被创建
     * @param binding
     * @param viewType
     */
    protected abstract fun onViewCreated(binding: B, viewType: Int)

    /**
     * 数据绑定
     * @param binding
     * @param data
     * @param position
     */
    protected abstract fun onBindItem(binding: B?, data: T, position: Int)

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class RecycleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemClickListener<T> {
        fun onItemClick(t: T, position: Int)
    }
}
