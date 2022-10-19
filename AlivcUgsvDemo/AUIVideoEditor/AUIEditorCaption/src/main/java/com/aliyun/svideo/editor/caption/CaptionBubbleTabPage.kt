package com.aliyun.svideo.editor.caption

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.aliyun.ugsv.common.utils.ThreadUtils
import com.aliyun.svideo.editor.caption.CaptionFlowerTabPage.Companion.DEFAULT_FLOWER_RES_ID
import com.aliyun.svideo.editor.caption.adapter.CaptionResourceTabAdapter
import com.aliyun.svideo.editor.caption.databinding.CaptionEditPanelBubbleBinding
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.model.CaptionResourceType
import com.aliyun.svideo.editor.caption.util.CaptionResourceUtil
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel
import com.aliyun.svideo.editor.common.widget.GridItemDecoration
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter
import com.aliyun.svideosdk.common.struct.project.Source
import java.io.File

class CaptionBubbleTabPage(context: Context) : CaptionTabPage<CaptionEditPanelBubbleBinding>(context, R.string.ugsv_caption_tab_bubble) {
    companion object {
        const val DEFAULT_RES_ID = 1
    }
    private lateinit var captionBubbleAdapter :CaptionResourceTabAdapter
    private var currentSelectPosition : Int = 0
    override val layoutId: Int
        get() = R.layout.caption_edit_panel_bubble

    override fun onTabInit() {
        super.onTabInit()
        captionBubbleAdapter = CaptionResourceTabAdapter(context)
        val gridLayoutManager = GridLayoutManager(context, 5)
        mDataBinding.captionEditPanelBubblePage.layoutManager = gridLayoutManager
        val spacing = context.resources.getDimensionPixelSize(R.dimen.ugsv_panel_caption_tab_page_space)
        mDataBinding.captionEditPanelBubblePage.addItemDecoration(
            GridItemDecoration(spacing, spacing, spacing)
        )
        mDataBinding.captionEditPanelBubblePage.adapter = captionBubbleAdapter
        mDataBinding.lifecycleOwner = context as LifecycleOwner
        captionBubbleAdapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener<CaptionResource> {
            override fun onItemClick(t: CaptionResource, position: Int) {
                captionEditViewModel.onBubbleResourceSelect(t)
                captionBubbleAdapter.dataList!![currentSelectPosition].isChecked = false
                captionBubbleAdapter.dataList!![position].isChecked = true
                captionBubbleAdapter.notifyItemChanged(currentSelectPosition)
                captionBubbleAdapter.notifyItemChanged(position)
                currentSelectPosition = position
            }
        })
    }

    override fun onBind(captionEditVM: CaptionEditViewModel) {
        super.onBind(captionEditVM)
        loadFiles()
    }

    private fun loadFiles() {
        ThreadUtils.runOnSubThread {
            val flowerResourceList = mutableListOf<CaptionResource>()

            val selectResId = captionEditViewModel.currentCaption.value?.fontEffectTemplateSource?.id ?: DEFAULT_RES_ID
            val defaultResource = CaptionResource(CaptionResourceType.Bubble, DEFAULT_FLOWER_RES_ID, Source(), "", "", R.drawable.ic_editor_reset)
            defaultResource.isChecked = selectResId == DEFAULT_RES_ID
            if(defaultResource.isChecked) {
                currentSelectPosition = 0
            }
            flowerResourceList.add(defaultResource)

            val flowerFileList = CaptionResourceUtil.getCaptionBubbleFileList()
            flowerFileList.forEachIndexed {index, it ->
                val resource = CaptionResource(CaptionResourceType.Bubble, it.hashCode(), Source(it), "${it+File.separator}icon.png")
                resource.isChecked = selectResId == resource.id
                if(resource.isChecked) {
                    currentSelectPosition = index + 1
                }
                flowerResourceList.add(resource)
            }

            ThreadUtils.runOnUiThread {
                captionBubbleAdapter.refreshDataList(flowerResourceList)
                captionBubbleAdapter.notifyDataSetChanged()
            }
        }
    }

}