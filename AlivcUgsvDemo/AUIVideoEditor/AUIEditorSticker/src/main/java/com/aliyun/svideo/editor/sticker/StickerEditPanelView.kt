package com.aliyun.svideo.editor.sticker

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.aliyun.svideo.editor.common.panel.BasePanelView
import com.aliyun.svideo.editor.common.widget.GridItemDecoration
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter
import com.aliyun.svideo.editor.sticker.adapter.StickerResourceTabAdapter
import com.aliyun.svideo.editor.sticker.databinding.StickerEditPanelBinding
import com.aliyun.svideo.editor.sticker.model.StickerResource
import com.aliyun.svideo.editor.sticker.util.StickerResourceUtil
import com.aliyun.svideo.editor.sticker.viewmodel.StickerEditViewModel


open class StickerEditPanelView(context: Context) : BasePanelView<StickerEditPanelBinding>(context) {
    private lateinit var stickerEditViewModel : StickerEditViewModel
    private lateinit var stickerResourceAdapter : StickerResourceTabAdapter
    override fun initView(context: Context) {
        super.initView(context)

        stickerResourceAdapter = StickerResourceTabAdapter(context)
        val gridLayoutManager = GridLayoutManager(context, 5)
        mDataBinding.stickerEditPanelPage.layoutManager = gridLayoutManager
        val spacing = context.resources.getDimensionPixelSize(R.dimen.ugsv_panel_sticker_tab_page_space)
        mDataBinding.stickerEditPanelPage.addItemDecoration(
            GridItemDecoration(spacing, spacing, spacing)
        )
        mDataBinding.stickerEditPanelPage.adapter = stickerResourceAdapter
        stickerResourceAdapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener<StickerResource> {
            override fun onItemClick(t: StickerResource, position: Int) {
                stickerEditViewModel.onSelectSticker(t)
            }
        } )

    }

    override val layoutId: Int
        get() = R.layout.sticker_edit_panel

    fun setViewModel(captionEditVM: StickerEditViewModel) {
        this.stickerEditViewModel = captionEditVM
        mDataBinding.viewModel = captionEditVM
        val lifeOwner = context as LifecycleOwner
        mDataBinding.lifecycleOwner = lifeOwner
        val stickerResourceList = StickerResourceUtil.getStickerFileList()
        stickerResourceAdapter.refreshDataList(stickerResourceList)
    }
}