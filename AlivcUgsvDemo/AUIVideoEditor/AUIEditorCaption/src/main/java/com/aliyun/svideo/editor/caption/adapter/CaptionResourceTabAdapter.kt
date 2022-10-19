package com.aliyun.svideo.editor.caption.adapter

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.caption.R
import com.aliyun.svideo.editor.caption.databinding.CaptionEditPanelResourceItemBinding
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.viewmodel.CaptionResourceItemViewModel
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter

class CaptionResourceTabAdapter(private val context : Context) :
    RecyclerAdapter<CaptionResource, CaptionEditPanelResourceItemBinding>(context) ,
    RecyclerAdapter.OnItemClickListener<CaptionResource> {

    private var onItemClickListener : OnItemClickListener<CaptionResource>? = null

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.caption_edit_panel_resource_item
    }

    fun setOnItemClickListener(listener: OnItemClickListener<CaptionResource>) {
        this.onItemClickListener = listener
    }

    override fun onViewCreated(binding: CaptionEditPanelResourceItemBinding, viewType: Int) {

        val viewModel = CaptionResourceItemViewModel()
        viewModel.setOnItemClickListener(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = context as LifecycleOwner
    }

    override fun onBindItem(binding: CaptionEditPanelResourceItemBinding?, data: CaptionResource, position: Int) {
        binding?.viewModel?.bind(data, position)
        binding?.executePendingBindings()
    }

    override fun onItemClick(t: CaptionResource, position: Int) {
        onItemClickListener?.onItemClick(t, position);
    }


}