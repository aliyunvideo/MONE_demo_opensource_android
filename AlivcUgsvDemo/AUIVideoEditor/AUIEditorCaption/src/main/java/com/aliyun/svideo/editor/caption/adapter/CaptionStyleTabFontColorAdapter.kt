package com.aliyun.svideo.editor.caption.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.caption.R
import com.aliyun.svideo.editor.caption.databinding.CaptionEditPanelStyleFontColorItemBinding
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.viewmodel.CaptionResourceItemViewModel
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter

class CaptionStyleTabFontColorAdapter(private val context : Context) :
    RecyclerAdapter<CaptionResource, CaptionEditPanelStyleFontColorItemBinding>(context) ,
    RecyclerAdapter.OnItemClickListener<CaptionResource> {

    private var onItemClickListener : OnItemClickListener<CaptionResource>? = null

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.caption_edit_panel_style_font_color_item
    }

    fun setOnItemClickListener(listener: OnItemClickListener<CaptionResource>) {
        this.onItemClickListener = listener
    }

    override fun onViewCreated(binding: CaptionEditPanelStyleFontColorItemBinding, viewType: Int) {

        val viewModel = CaptionResourceItemViewModel()
        viewModel.setOnItemClickListener(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = context as LifecycleOwner
    }

    override fun onBindItem(binding: CaptionEditPanelStyleFontColorItemBinding?, data: CaptionResource, position: Int) {
        binding?.apply {
            colorItem.setImageDrawable(ColorDrawable(data.id))
            viewModel?.bind(data, position)
            executePendingBindings()
        }
    }

    override fun onItemClick(t: CaptionResource, position: Int) {
        onItemClickListener?.onItemClick(t, position);
    }


}