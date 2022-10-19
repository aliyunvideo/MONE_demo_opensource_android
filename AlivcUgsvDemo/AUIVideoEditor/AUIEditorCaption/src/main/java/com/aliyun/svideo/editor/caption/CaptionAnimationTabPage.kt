package com.aliyun.svideo.editor.caption

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliyun.svideo.editor.caption.adapter.CaptionResourceTabWithNameAdapter
import com.aliyun.svideo.editor.caption.databinding.CaptionEditPanelAnimationBinding
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.model.CaptionResourceType
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditAnimationViewModel
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel
import com.aliyun.svideo.editor.common.widget.LinearItemDecoration
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter
import com.aliyun.svideosdk.common.struct.project.Source

class CaptionAnimationTabPage(context: Context) : CaptionTabPage<CaptionEditPanelAnimationBinding>(context, R.string.ugsv_caption_tab_animation) {
    private lateinit var captionAnimationAdapter : CaptionResourceTabWithNameAdapter
    private var currentSelectPosition : Int = 0
    private var animationViewModel = CaptionEditAnimationViewModel()
    override val layoutId: Int
        get() = R.layout.caption_edit_panel_animation

    override fun onTabInit() {
        super.onTabInit()
        captionAnimationAdapter = CaptionResourceTabWithNameAdapter(context)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mDataBinding.captionEditPanelAnimationPage.layoutManager = layoutManager
        val spacing = context.resources.getDimensionPixelSize(R.dimen.ugsv_panel_caption_tab_page_space)
        mDataBinding.captionEditPanelAnimationPage.addItemDecoration(
            LinearItemDecoration(spacing, spacing, spacing)
        )
        mDataBinding.captionEditPanelAnimationPage.adapter = captionAnimationAdapter
        mDataBinding.lifecycleOwner = context as LifecycleOwner
        mDataBinding.viewModel = animationViewModel
        captionAnimationAdapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener<CaptionResource> {
            override fun onItemClick(t: CaptionResource, position: Int) {
                captionEditViewModel.onAnimationResourceSelect(context, t)
                captionAnimationAdapter.dataList!![currentSelectPosition].isChecked = false
                captionAnimationAdapter.dataList!![position].isChecked = true
                captionAnimationAdapter.notifyItemChanged(currentSelectPosition)
                captionAnimationAdapter.notifyItemChanged(position)
                currentSelectPosition = position
            }
        })

    }

    override fun onBind(captionEditVM: CaptionEditViewModel) {
        super.onBind(captionEditVM)
        animationViewModel.bind(20)
        val flowerResource = mutableListOf<CaptionResource>()
        val animationArray = context.resources.getStringArray(R.array.ugsv_caption_animation_list);
        val currentEffectResId = captionEditViewModel.currentCaption.value?.frameAnimations?.firstOrNull()?.resId ?: CaptionConfig.EFFECT_NONE
        CaptionConfig.POSITION_FONT_ANIM_ARRAY.forEachIndexed{ index, it ->
            val captionResource = CaptionResource(CaptionResourceType.Animation, it, Source(""), "", animationArray[index], CaptionConfig.NAME_FONT_ANIM_ARRAY[index])
            captionResource.isChecked = captionResource.id == currentEffectResId
            if(captionResource.isChecked) {
                currentSelectPosition = index
            }
            flowerResource.add(captionResource)
        }

        captionAnimationAdapter.refreshDataList(flowerResource)
        captionAnimationAdapter.notifyDataSetChanged()
    }
}