package com.aliyun.svideo.editor.caption

import android.content.Context
import android.graphics.Rect
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliyun.svideo.editor.caption.adapter.CaptionStyleTabFontAdapter
import com.aliyun.svideo.editor.caption.adapter.CaptionStyleTabFontColorAdapter
import com.aliyun.svideo.editor.caption.adapter.CaptionStyleTabFontStyleTemplateAdapter
import com.aliyun.svideo.editor.caption.databinding.CaptionEditPanelStyleBinding
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.model.CaptionResourceType
import com.aliyun.svideo.editor.caption.util.CaptionResourceUtil
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel
import com.aliyun.svideo.editor.common.widget.LinearItemDecoration
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter
import com.aliyun.svideosdk.common.struct.project.Source

class CaptionStyleTabPage(context: Context) : CaptionTabPage<CaptionEditPanelStyleBinding>(context, R.string.ugsv_caption_tab_style) {
    companion object {
        const val DEFAULT_RES_ID = 1
    }

    override val layoutId: Int
        get() = R.layout.caption_edit_panel_style

    private lateinit var captionStyleFontAdapter : CaptionStyleTabFontAdapter
    private lateinit var captionStyleFontTemplateAdapter : CaptionStyleTabFontStyleTemplateAdapter
    private lateinit var captionStyleFontColorAdapter : CaptionStyleTabFontColorAdapter
    
    private var currentFontSelectPosition : Int = 0
//    private var currentFontTemplateSelectPosition : Int = 0
//    private var currentFontColorSelectPosition : Int = 0

    override fun onTabInit() {
        super.onTabInit()
        initFontRecycle()
        initFontTemplateRecycle()
        initFontColorRecycle()
        mDataBinding.lifecycleOwner = context as LifecycleOwner
    }
    
    private fun initFontRecycle() {
        captionStyleFontAdapter = CaptionStyleTabFontAdapter(context)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mDataBinding.captionEditPanelStyleFontPage.layoutManager = layoutManager
        val spacing = context.resources.getDimensionPixelSize(R.dimen.ugsv_panel_caption_tab_page_space)
        mDataBinding.captionEditPanelStyleFontPage.addItemDecoration(
            LinearItemDecoration(spacing, spacing, spacing, Rect(0, spacing, spacing,spacing))
        )
        mDataBinding.captionEditPanelStyleFontPage.adapter = captionStyleFontAdapter
        captionStyleFontAdapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener<CaptionResource> {
            override fun onItemClick(t: CaptionResource, position: Int) {
                captionEditViewModel.onFontTypefaceSelect(t)
                captionStyleFontAdapter.dataList!![currentFontSelectPosition].isChecked = false
                captionStyleFontAdapter.dataList!![position].isChecked = true
                captionStyleFontAdapter.notifyItemChanged(currentFontSelectPosition)
                captionStyleFontAdapter.notifyItemChanged(position)
                currentFontSelectPosition = position
            }
        })
    }
    
    private fun initFontTemplateRecycle() {
        captionStyleFontTemplateAdapter = CaptionStyleTabFontStyleTemplateAdapter(context)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mDataBinding.captionEditPanelStyleFontTemplatePage.layoutManager = layoutManager
        val spacing = context.resources.getDimensionPixelSize(R.dimen.ugsv_panel_caption_tab_page_space)
        mDataBinding.captionEditPanelStyleFontTemplatePage.addItemDecoration(
            LinearItemDecoration(spacing, spacing, spacing, Rect(0, spacing, spacing,spacing))
        )
        mDataBinding.captionEditPanelStyleFontTemplatePage.adapter = captionStyleFontTemplateAdapter
        captionStyleFontTemplateAdapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener<CaptionResource> {
            override fun onItemClick(t: CaptionResource, position: Int) {
                captionEditViewModel.onFontStyleTemplateSelect(context, t)
                //注释选中逻辑
//                captionStyleFontTemplateAdapter.dataList!![currentFontTemplateSelectPosition].isChecked = false
//                captionStyleFontTemplateAdapter.dataList!![position].isChecked = true
//                captionStyleFontTemplateAdapter.notifyItemChanged(currentFontTemplateSelectPosition)
//                captionStyleFontTemplateAdapter.notifyItemChanged(position)
//                currentFontTemplateSelectPosition = position
            }
        })
    }
    
    private fun initFontColorRecycle() {
        captionStyleFontColorAdapter = CaptionStyleTabFontColorAdapter(context)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mDataBinding.captionEditPanelStyleFontColorPage.layoutManager = layoutManager
        val spacing = context.resources.getDimensionPixelSize(R.dimen.ugsv_panel_caption_tab_page_space)
        mDataBinding.captionEditPanelStyleFontColorPage.addItemDecoration(
            LinearItemDecoration(spacing, spacing, spacing, Rect(0, spacing, spacing,spacing))
        )
        mDataBinding.captionEditPanelStyleFontColorPage.adapter = captionStyleFontColorAdapter
        captionStyleFontColorAdapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener<CaptionResource> {
            override fun onItemClick(t: CaptionResource, position: Int) {
                captionEditViewModel.onFontColorSelect(t)
                //注释选中逻辑
//                captionStyleFontColorAdapter.dataList!![currentFontColorSelectPosition].isChecked = false
//                captionStyleFontColorAdapter.dataList!![position].isChecked = true
//                captionStyleFontColorAdapter.notifyItemChanged(currentFontColorSelectPosition)
//                captionStyleFontColorAdapter.notifyItemChanged(position)
//                currentFontColorSelectPosition = position
            }
        })
    }

    override fun onBind(captionEditVM: CaptionEditViewModel) {
        super.onBind(captionEditVM)
        bindFontRecycle()
        bindFontTemplateRecycle()
        bindFontColorRecycle()
    }

    private fun bindFontRecycle() {
        //TODO 如果设置了文字气泡后，命中了字体，在这里找不到字体，要设置成默认

        val fontResourceList = mutableListOf<CaptionResource>()
        val currentEffectResId = captionEditViewModel.currentCaption.value?.fontPath?.id ?: DEFAULT_RES_ID

        val defaultResource = CaptionResource(CaptionResourceType.Font,
            DEFAULT_RES_ID, Source(), "", context.resources.getString(R.string.ugsv_caption_style_default_font))
        defaultResource.isChecked = currentEffectResId == DEFAULT_RES_ID

        var fonSelectPosition = -1
        if(defaultResource.isChecked) {
            fonSelectPosition = 0
        }
        fontResourceList.add(defaultResource)

        val configFontResourceList = CaptionResourceUtil.getCaptionFontList()

        configFontResourceList.forEachIndexed{ index, it ->
            it.isChecked = it.id == currentEffectResId
            if(it.isChecked) {
                fonSelectPosition = index
            }
            fontResourceList.add(it)
        }

        if(fonSelectPosition != -1) {
            currentFontSelectPosition = fonSelectPosition
        } else {
            currentFontSelectPosition = 0;
            defaultResource.isChecked = true
        }

        captionStyleFontAdapter.refreshDataList(fontResourceList)
        captionStyleFontAdapter.notifyDataSetChanged()
    }

    private fun bindFontTemplateRecycle() {
        val fontStyleResourceList = mutableListOf<CaptionResource>()
//        val currentEffectResId = captionEditViewModel.currentFontStyleTemplateResource?.id ?: DEFAULT_RES_ID

        val defaultResource = CaptionResource(CaptionResourceType.FontStyleTemplate,
            DEFAULT_RES_ID, Source(), "", "", R.drawable.ic_editor_reset)
//        defaultResource.isChecked = currentEffectResId == DEFAULT_RES_ID
//        if(defaultResource.isChecked) {
//            currentFontTemplateSelectPosition = 0
//        }
        fontStyleResourceList.add(defaultResource)

        val configFontResourceList = CaptionResourceUtil.getCaptionFontStyleTemplateList(context)

        configFontResourceList.forEachIndexed{ _, it ->
//            it.isChecked = it.id == currentEffectResId
//            if(it.isChecked) {
//                currentFontTemplateSelectPosition = index
//            }
            fontStyleResourceList.add(it)
        }

        captionStyleFontTemplateAdapter.refreshDataList(fontStyleResourceList)
        captionStyleFontTemplateAdapter.notifyDataSetChanged()
    }

    private fun bindFontColorRecycle() {
        val fontResourceList = mutableListOf<CaptionResource>()
        val configFontResourceList = CaptionResourceUtil.getCaptionColorList()
//        val currentEffectResId = captionEditViewModel.currentCaption.value?.color?.toArgb() ?: configFontResourceList[0].toArgb()
        configFontResourceList.forEachIndexed{ _, it ->
            val captionResource = CaptionResource(CaptionResourceType.Color, it.toArgb(), Source(), "")
//            captionResource.isChecked = it.toArgb() == currentEffectResId
//            if(captionResource.isChecked) {
//                currentFontColorSelectPosition = index
//            }
            fontResourceList.add(captionResource)
        }

        captionStyleFontColorAdapter.refreshDataList(fontResourceList)
        captionStyleFontColorAdapter.notifyDataSetChanged()
    }


}