package com.aliyun.svideo.editor.caption

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.aliyun.ugsv.common.utils.ThreadUtils
import com.aliyun.svideo.editor.caption.adapter.CaptionResourceTabAdapter
import com.aliyun.svideo.editor.caption.databinding.CaptionEditPanelFlowerBinding
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.model.CaptionResourceType
import com.aliyun.svideo.editor.caption.util.CaptionResourceUtil
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel
import com.aliyun.svideo.editor.common.widget.GridItemDecoration
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter
import com.aliyun.svideosdk.common.struct.project.Source
import java.io.File

class CaptionFlowerTabPage(context: Context) : CaptionTabPage<CaptionEditPanelFlowerBinding>(context, R.string.ugsv_caption_tab_flower_text) {
    companion object {
        const val DEFAULT_FLOWER_RES_ID = 1
    }
    private var currentSelectPosition : Int = 0
    private lateinit var captionFlowAdapter :CaptionResourceTabAdapter
    override val layoutId: Int
        get() = R.layout.caption_edit_panel_flower

    override fun onTabInit() {
        super.onTabInit()
        captionFlowAdapter = CaptionResourceTabAdapter(context)
        val gridLayoutManager = GridLayoutManager(context, 5)
        mDataBinding.captionEditPanelFlowerPage.layoutManager = gridLayoutManager
        val spacing = context.resources.getDimensionPixelSize(R.dimen.ugsv_panel_caption_tab_page_space)
        mDataBinding.captionEditPanelFlowerPage.addItemDecoration(
            GridItemDecoration(spacing, spacing, spacing)
        )
        mDataBinding.captionEditPanelFlowerPage.adapter = captionFlowAdapter
        mDataBinding.lifecycleOwner = context as LifecycleOwner
        captionFlowAdapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener<CaptionResource> {
            override fun onItemClick(t: CaptionResource, position: Int) {
                captionEditViewModel.onFlowerResourceSelect(t)
                captionFlowAdapter.dataList!![currentSelectPosition].isChecked = false
                captionFlowAdapter.dataList!![position].isChecked = true
                captionFlowAdapter.notifyItemChanged(currentSelectPosition)
                captionFlowAdapter.notifyItemChanged(position)
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
            val selectResId = captionEditViewModel.currentCaption.value?.fontEffectTemplateSource?.id ?: DEFAULT_FLOWER_RES_ID
            val defaultResource = CaptionResource(CaptionResourceType.Flower, DEFAULT_FLOWER_RES_ID, Source(), "" ,"",R.drawable.ic_editor_reset)
            defaultResource.isChecked = selectResId == DEFAULT_FLOWER_RES_ID
            if(defaultResource.isChecked) {
                currentSelectPosition = 0
            }
            flowerResourceList.add(defaultResource)
            val flowerFileList = CaptionResourceUtil.getCaptionFlowerTemplateList()
            flowerFileList.forEachIndexed {index, it ->
                val resource = CaptionResource(CaptionResourceType.Flower, it.hashCode(), Source(it), "${it+File.separator}icon.png")
                resource.isChecked = selectResId == resource.id
                if(resource.isChecked) {
                    currentSelectPosition = index + 1
                }
                flowerResourceList.add(resource)
            }


            ThreadUtils.runOnUiThread {
                captionFlowAdapter.refreshDataList(flowerResourceList)
                captionFlowAdapter.notifyDataSetChanged()
            }
        }
    }
}