package com.aliyun.svideo.editor.caption.adapter

import android.content.Context
import android.graphics.Typeface
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.caption.R
import com.aliyun.svideo.editor.caption.databinding.CaptionEditPanelStyleFontItemBinding
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.viewmodel.CaptionResourceItemViewModel
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter

class CaptionStyleTabFontAdapter(private val context : Context) :
    RecyclerAdapter<CaptionResource, CaptionEditPanelStyleFontItemBinding>(context) ,
    RecyclerAdapter.OnItemClickListener<CaptionResource> {

    private var onItemClickListener : OnItemClickListener<CaptionResource>? = null

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.caption_edit_panel_style_font_item
    }

    fun setOnItemClickListener(listener: OnItemClickListener<CaptionResource>) {
        this.onItemClickListener = listener
    }

    override fun onViewCreated(binding: CaptionEditPanelStyleFontItemBinding, viewType: Int) {

        val viewModel = CaptionResourceItemViewModel()
        viewModel.setOnItemClickListener(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = context as LifecycleOwner
    }

    override fun onBindItem(binding: CaptionEditPanelStyleFontItemBinding?, data: CaptionResource, position: Int) {
        binding?.apply {
            if(!data.source.path.isNullOrEmpty()) {
                val typeface = Typeface.createFromFile(data.source.path)
                typeface?.let {
                    textView.typeface = it
                }
            } else {
                textView.typeface = Typeface.DEFAULT
            }

            viewModel?.bind(data, position)
            executePendingBindings()
        }
    }

    override fun onItemClick(t: CaptionResource, position: Int) {
        onItemClickListener?.onItemClick(t, position);
    }


}