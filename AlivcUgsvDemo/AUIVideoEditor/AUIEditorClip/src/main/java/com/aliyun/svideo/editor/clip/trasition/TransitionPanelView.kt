package com.aliyun.svideo.editor.clip.trasition

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.databinding.UgsvEditorClipTransitionPanelBinding
import com.aliyun.svideo.editor.common.panel.BasePanelView


class TransitionPanelView(context: Context) : BasePanelView<UgsvEditorClipTransitionPanelBinding>(context) {

    override val layoutId: Int
        get() = R.layout.ugsv_editor_clip_transition_panel

    lateinit var mRecyclerView:RecyclerView
    lateinit var mTransitionAdapter:TransitionAdapter

    override fun initView(context: Context) {
        super.initView(context)
        mRecyclerView = findViewById(R.id.editor_transition_recyclerview)
        var layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mRecyclerView.layoutManager = layoutManager
        mTransitionAdapter = TransitionAdapter()
        mRecyclerView.adapter = mTransitionAdapter
    }

    open fun setViewModel(viewModel: TransitionPanelViewModel) {
        mDataBinding.viewModel = viewModel
        mTransitionAdapter.setData(viewModel.listViewModel)
    }

    open fun setItemClickListener(listener: TransitionClickListener) {
        mTransitionAdapter.setTransitionSelectListener(listener)
    }

    open fun notifyDataChanged() {
        mTransitionAdapter.notifyDataSetChanged()
    }


}