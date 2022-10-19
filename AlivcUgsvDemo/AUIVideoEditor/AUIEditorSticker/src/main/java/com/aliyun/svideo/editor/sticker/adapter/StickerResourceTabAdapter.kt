package com.aliyun.svideo.editor.sticker.adapter

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter
import com.aliyun.svideo.editor.sticker.R
import com.aliyun.svideo.editor.sticker.databinding.StickerEditPanelResourceItemBinding
import com.aliyun.svideo.editor.sticker.model.StickerResource
import com.aliyun.svideo.editor.sticker.viewmodel.StickerResourceItemViewModel

class StickerResourceTabAdapter(private val context : Context) :
    RecyclerAdapter<StickerResource, StickerEditPanelResourceItemBinding>(context) ,
    RecyclerAdapter.OnItemClickListener<StickerResource> {

    private var onItemClickListener : OnItemClickListener<StickerResource>? = null

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.sticker_edit_panel_resource_item
    }

    fun setOnItemClickListener(listener: OnItemClickListener<StickerResource>) {
        this.onItemClickListener = listener
    }

    override fun onViewCreated(binding: StickerEditPanelResourceItemBinding, viewType: Int) {

        val viewModel = StickerResourceItemViewModel()
        viewModel.setOnItemClickListener(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = context as LifecycleOwner
    }

    override fun onBindItem(binding: StickerEditPanelResourceItemBinding?, data: StickerResource, position: Int) {
        binding?.viewModel?.bind(data, position)
        binding?.executePendingBindings()
    }

    override fun onItemClick(t: StickerResource, position: Int) {
        onItemClickListener?.onItemClick(t, position);
    }


}